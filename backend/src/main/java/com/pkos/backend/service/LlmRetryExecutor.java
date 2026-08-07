package com.pkos.backend.service;

import java.util.function.Supplier;

public interface LlmRetryExecutor {

    <T> T execute(
            Supplier<T> supplier
    );

    boolean isRetryable(
            RuntimeException exception
    );

}