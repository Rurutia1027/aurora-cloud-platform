package com.aurora.customer.controller;

import com.aurora.amqp.RabbitMQMessageProducer;
import com.aurora.clients.fraud.FraudCheckResponse;
import com.aurora.clients.fraud.FraudClient;
import com.aurora.customer.dto.CustomerRegistrationRequest;
import com.aurora.customer.repo.CustomerRepository;
import com.aurora.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        doNothing().when(customerService).registerCustomer(any());
    }

    @Test
    void registerCustomer_ValidRequest_CallsService() throws Exception {
        CustomerRegistrationRequest request = CustomerRegistrationRequest.builder()
                .firstName("Emma")
                .lastName("Sam")
                .email("rr@333.com")
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(customerService, times(1)).registerCustomer(Mockito.refEq(request));
    }
}