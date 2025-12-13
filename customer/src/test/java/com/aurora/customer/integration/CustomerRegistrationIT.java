package com.aurora.customer.integration;

import com.aurora.amqp.RabbitMQMessageProducer;
import com.aurora.clients.fraud.FraudCheckResponse;
import com.aurora.clients.fraud.FraudClient;
import com.aurora.customer.dto.Customer;
import com.aurora.customer.dto.CustomerRegistrationRequest;
import com.aurora.customer.repo.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CustomerRegistrationIT extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Stub external dependencies
     */
    @MockBean
    private FraudClient fraudClient;

    @MockBean
    private RabbitMQMessageProducer rabbitMQMessageProducer;

    @Test
    void registerCustomer_ShouldPersistCustomer() {
        // Arrange
        when(fraudClient.isFraudster(any()))
                .thenReturn(FraudCheckResponse.builder().isFraudster(false).build());

        String firstName = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();

        CustomerRegistrationRequest registrationRequest = CustomerRegistrationRequest.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity("/api/v1/customers",
                registrationRequest, Void.class);

        // Assert: HTTP
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert: DB
        Customer savedCustomer =
                customerRepository.findAll().stream().findFirst().orElse(null);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo(email);
        assertThat(savedCustomer.getFirstName()).isEqualTo(firstName);
        assertThat(savedCustomer.getLastName()).isEqualTo(lastName);
        assertThat(savedCustomer.getId()).isNotNull();
    }
}
