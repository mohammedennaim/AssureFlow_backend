package com.pfe.gateway.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class OpenApiAggregatorController {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // List of active microservices
    private final List<String> services = List.of(
            "http://iam-service:8080/v3/api-docs",
            "http://client-service:8080/v3/api-docs",
            "http://policy-service:8080/v3/api-docs",
            "http://claims-service:8080/v3/api-docs");

    public OpenApiAggregatorController(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/v3/api-docs/all")
    public Mono<JsonNode> getAggregatedDocs() {
        return Flux.fromIterable(services)
                .flatMap(url -> webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .onErrorResume(e -> Mono.empty()))
                .collectList()
                .map(this::mergeJsonNodes);
    }

    private JsonNode mergeJsonNodes(List<JsonNode> nodes) {
        ObjectNode result = objectMapper.createObjectNode();
        if (nodes.isEmpty())
            return result;

        result.put("openapi", "3.0.1");

        ObjectNode info = result.putObject("info");
        info.put("title", "AssureFlow Global API");
        info.put("description", "Unified API Documentation for all Microservices");
        info.put("version", "1.0.0");

        ArrayNode servers = result.putArray("servers");
        servers.addObject().put("url", "http://localhost:8080").put("description", "API Gateway");

        ObjectNode mergedPaths = result.putObject("paths");
        ObjectNode mergedComponents = result.putObject("components");
        ObjectNode mergedSchemas = mergedComponents.putObject("schemas");
        ArrayNode mergedTags = result.putArray("tags");

        for (JsonNode node : nodes) {
            if (node.hasNonNull("paths")) {
                ObjectNode paths = (ObjectNode) node.get("paths");
                Iterator<Map.Entry<String, JsonNode>> fields = paths.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    mergedPaths.set(field.getKey(), field.getValue());
                }
            }

            if (node.hasNonNull("components") && node.get("components").hasNonNull("schemas")) {
                ObjectNode schemas = (ObjectNode) node.get("components").get("schemas");
                Iterator<Map.Entry<String, JsonNode>> fields = schemas.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    mergedSchemas.set(field.getKey(), field.getValue());
                }
            }

            if (node.hasNonNull("tags")) {
                ArrayNode tags = (ArrayNode) node.get("tags");
                for (JsonNode tag : tags) {
                    mergedTags.add(tag);
                }
            }
        }

        return result;
    }
}
