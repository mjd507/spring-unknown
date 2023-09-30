package com.mjd507.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mock.MockIntegration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringJUnitConfig
@SpringIntegrationTest
@ContextConfiguration(classes = IntegrationFlowConfig.class)
public class IntegrationFlowTest {

    @Autowired
    private MockIntegrationContext mockIntegrationContext;

    @Test
    void testFlow() throws InterruptedException {
        MessageSource<Integer> messageSource = MockIntegration.mockMessageSource(1, 2, 3);
        mockIntegrationContext.substituteMessageSourceFor("producerFlowEndPoint", messageSource);
    }

}
