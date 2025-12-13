package com.aurora.customer.service;

import com.aurora.amqp.RabbitMQMessageProducer;
import com.aurora.clients.fraud.FraudCheckResponse;
import com.aurora.clients.fraud.FraudClient;
import com.aurora.customer.dto.CustomerRegistrationRequest;
import com.aurora.customer.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {
    private CustomerRepository customerRepository;
    private FraudClient fraudClient;
    private RabbitMQMessageProducer rabbitMQMessageProducer;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        fraudClient = mock(FraudClient.class);
        rabbitMQMessageProducer = mock(RabbitMQMessageProducer.class);

        customerService = new CustomerService(customerRepository, fraudClient,
                rabbitMQMessageProducer);
    }

    @Test
    void registerCustomer_HappyPath() {
        CustomerRegistrationRequest registrationRequest =
                CustomerRegistrationRequest.builder()
                        .firstName("Emma")
                        .lastName("John")
                        .email("emma@333.com")
                        .build();

        when(fraudClient.isFraudster(anyInt()))
                .thenReturn(FraudCheckResponse.builder()
                        .isFraudster(false).build());

        customerService.registerCustomer(registrationRequest);

        // verify save called
        verify(customerRepository).saveAndFlush(any());

        // verify message published
        verify(rabbitMQMessageProducer).publish(any(),
                eq("internal.exchange"),
                eq("internal.notification.routing-key"));
    }

    @Test
    void registerCustomer_Fraudster_ThrowException() {
        CustomerRegistrationRequest registrationRequest =
                CustomerRegistrationRequest.builder()
                        .firstName("Emma")
                        .lastName("John")
                        .email("emma@333.com")
                        .build();

        when(fraudClient.isFraudster(anyInt()))
                .thenReturn(FraudCheckResponse.builder()
                        .isFraudster(true).build());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> customerService.registerCustomer(registrationRequest)
        );

        assertEquals("fraudster", exception.getMessage());

        // verify message NOT published
        verify(rabbitMQMessageProducer, never()).publish(any(), any(), any());
    }
}