package com.edward.retry.proxy.strategy;

import com.edward.retry.proxy.core.RetryTaskResult;

import java.util.function.Function;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:28
 */
public class ExceptionPredicate<V> implements Function<RetryTaskResult<V>, Boolean> {

    private Class<? extends Throwable> exceptionClass;

    public ExceptionPredicate(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    @Override
    public Boolean apply(RetryTaskResult<V> retryTaskResult) {
        return retryTaskResult.isException() && this.exceptionClass.isAssignableFrom(retryTaskResult.getThrowable().getClass());
    }
}
