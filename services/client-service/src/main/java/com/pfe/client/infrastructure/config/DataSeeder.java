package com.pfe.client.infrastructure.config;

import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Beneficiary;
import com.pfe.client.domain.model.Client;
import com.pfe.client.domain.model.ClientStatus;
import com.pfe.client.domain.model.ClientType;
import com.pfe.client.domain.repository.AddressRepository;
import com.pfe.client.domain.repository.BeneficiaryRepository;
import com.pfe.client.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!clientRepository.findAll().isEmpty()) {
            log.info("Clients already exist, skipping seeding");
            return;
        }

        for (int i = 1; i <= 3; i++) {
            Client c = Client.builder()
                    .clientNumber(String.format("CLT-%05d", i))
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .email("client" + i + "@example.com")
                    .phone("+21650100" + i)
                    .cin("CIN00" + i)
                    .dateOfBirth(LocalDate.of(1990, 1, i))
                    .status(ClientStatus.ACTIVE)
                    .type(ClientType.INDIVIDUAL)
                    .userId(UUID.randomUUID())
                    .build();

            var saved = clientRepository.save(c);

            // Create primary address
            Address address = Address.builder()
                    .clientId(saved.getId())
                    .street("Street " + i)
                    .city("City")
                    .postalCode("1000" + i)
                    .country("TN")
                    .primary(true)
                    .build();
            addressRepository.save(address);

            Beneficiary b = Beneficiary.builder()
                    .clientId(saved.getId())
                    .firstName("Ben" + i)
                    .lastName("Doe")
                    .relationship("SPOUSE")
                    .percentage(50)
                    .email("ben" + i + "@example.com")
                    .phone("+21650000" + i)
                    .build();

            beneficiaryRepository.save(b);
        }

        log.info("Seeded sample clients, addresses and beneficiaries");
    }
}
