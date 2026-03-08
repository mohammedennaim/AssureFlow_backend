package com.pfe.workflow.infrastructure.messaging;

import com.pfe.workflow.domain.model.SAGAStep;
import com.pfe.workflow.domain.model.SAGATransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SAGAStepExecutorPublisher {

    private static final String SAGA_COMMANDS_TOPIC = "saga-commands";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void executeNextPendingStep(SAGATransaction saga) {
        saga.getSteps().stream()
                .filter(s -> s.getStatus() == com.pfe.workflow.domain.model.StepStatus.PENDING)
                .findFirst()
                .ifPresent(step -> executeStep(saga, step));
    }

    public void executeCompensation(SAGATransaction saga) {
        var steps = saga.getSteps();
        for (int i = steps.size() - 1; i >= 0; i--) {
            SAGAStep step = steps.get(i);
            if (step.getCompensationAction() != null && !step.getCompensationAction().isBlank()
                    && step.getStatus() == com.pfe.workflow.domain.model.StepStatus.COMPENSATING) {
                publishCompensationCommand(saga, step);
            }
        }
    }

    private void executeStep(SAGATransaction saga, SAGAStep step) {
        Map<String, Object> command = new HashMap<>();
        command.put("sagaId", saga.getId().toString());
        command.put("stepId", step.getId().toString());
        command.put("sagaType", saga.getSagaType());
        command.put("action", step.getAction());
        command.put("serviceName", step.getServiceName());
        command.put("compensationAction", step.getCompensationAction());

        String eventType = step.getServiceName() + "." + step.getAction();
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(SAGA_COMMANDS_TOPIC, eventType, command);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[SAGA] Failed to publish step command — saga={} step={} action={}: {}",
                        saga.getId(), step.getId(), step.getAction(), ex.getMessage());
            } else {
                log.info("[SAGA] Step command published — saga={} step={} action={} → topic={} partition={} offset={}",
                        saga.getId(), step.getId(), step.getAction(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    private void publishCompensationCommand(SAGATransaction saga, SAGAStep step) {
        Map<String, Object> command = new HashMap<>();
        command.put("sagaId", saga.getId().toString());
        command.put("stepId", step.getId().toString());
        command.put("sagaType", saga.getSagaType());
        command.put("action", step.getCompensationAction());
        command.put("serviceName", step.getServiceName());

        String eventType = step.getServiceName() + ".compensate." + step.getAction();
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(SAGA_COMMANDS_TOPIC, eventType, command);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[SAGA] Failed to publish compensation command — saga={} step={}: {}",
                        saga.getId(), step.getId(), ex.getMessage());
            } else {
                log.info("[SAGA] Compensation command published — saga={} step={} compensationAction={}",
                        saga.getId(), step.getId(), step.getCompensationAction());
            }
        });
    }
}
