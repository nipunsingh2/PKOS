package com.pkos.backend.service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.pkos.backend.config.LlmProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LlmRetryExecutorImpl
        implements LlmRetryExecutor {

    private final LlmProperties llmProperties;

    @Override
    public <T> T execute(
            Supplier<T> supplier
    ) {

        int maxAttempts =
                llmProperties
                        .getRetry()
                        .getMaxAttempts();

        Duration delay =
                llmProperties
                        .getRetry()
                        .getDelay();

        RuntimeException lastException = null;

        for (int attempt = 1;
                attempt <= maxAttempts;
                attempt++) {

            try {

                return supplier.get();

            } catch (RuntimeException exception) {

                if (!isRetryable(exception)) {
                    throw exception;
                }

                lastException = exception;

                if (attempt == maxAttempts) {

                    log.error(
                            "LLM request failed after {} attempts.",
                            maxAttempts,
                            exception
                    );

                    break;
                }

                log.warn(
                        "Transient LLM failure. Retry attempt {}/{}.",
                        attempt,
                        maxAttempts,
                        exception
                );

                try {

                    Thread.sleep(
                            delay.toMillis()
                    );

                } catch (InterruptedException interruptedException) {

                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "Retry interrupted.",
                            interruptedException
                    );
                }
            }
        }

        throw lastException;
    }

    private boolean isRetryable(
            RuntimeException exception
    ) {

        return exception instanceof HttpServerErrorException
                || exception instanceof ResourceAccessException
                || exception instanceof HttpClientErrorException.TooManyRequests
                || exception.getCause() instanceof ConnectException
                || exception.getCause() instanceof SocketTimeoutException;
    }

}