package com.example.demo.aspect;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggingAspectTest {

    private LoggingAspect aspect;
    private ProceedingJoinPoint joinPoint;

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    private static final String URL = "http://localhost/api/test";

    @BeforeEach
    void setUp() {
        aspect = new LoggingAspect();
        joinPoint = mock(ProceedingJoinPoint.class);

        // Configure mock HTTP request
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/api/test");

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        // Capture Logback events
        logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);

        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldLogOperationDuration() throws Throwable {
        when(joinPoint.proceed()).thenReturn(null);

        aspect.logOperationDuration(joinPoint);

        assertEquals(1, appender.list.size());

        ILoggingEvent event = appender.list.get(0);

        assertEquals("INFO", event.getLevel().toString());
        assertTrue(event.getFormattedMessage().contains(URL));
        assertTrue(event.getFormattedMessage().matches(
                ".* took \\d+ ms"
        ));

        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void shouldLogExceptionAndRethrowIt() throws Throwable {
        RuntimeException exception =
                new RuntimeException("Database connection failed");

        when(joinPoint.proceed()).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> aspect.logOperationDuration(joinPoint)
        );

        // Verify the exact same exception was rethrown
        assertSame(exception, thrown);

        assertEquals(1, appender.list.size());

        ILoggingEvent event = appender.list.get(0);

        assertEquals("ERROR", event.getLevel().toString());
        assertTrue(event.getFormattedMessage().contains(URL));
        assertEquals(
                exception.getMessage(),
                event.getThrowableProxy().getMessage()
        );

        verify(joinPoint, times(1)).proceed();
    }
}

