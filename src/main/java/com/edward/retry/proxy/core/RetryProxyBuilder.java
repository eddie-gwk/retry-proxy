package com.edward.retry.proxy.core;

import com.edward.retry.proxy.strategy.ExceptionPredicate;
import com.edward.retry.proxy.strategy.RetryListener;
import com.edward.retry.proxy.strategy.RetryResultListener;
import com.edward.retry.proxy.strategy.StopStrategy;
import com.edward.retry.proxy.utils.AssertsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:20
 */
public class RetryProxyBuilder<V> {
    private Long sleepTime;
    private TimeUnit timeUnit;
    private StopStrategy stopStrategy;
    private ScheduledExecutorService scheduledExecutorService;

    private Predicate<RetryTaskResult<V>> rejectionPredicate;
    private ExceptionPredicate<V> exceptionPredicate;
    private List<RetryListener<V>> globalRetryListeners;
    private List<RetryResultListener<V>> globalRetryResultListeners;


    private RetryProxyBuilder(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.globalRetryListeners = new ArrayList<>();
        this.globalRetryResultListeners = new ArrayList<>();
    }


    public static <V> RetryProxyBuilder<V> newBuilder(ScheduledExecutorService scheduledExecutorService) {
        return new RetryProxyBuilder<>(scheduledExecutorService);
    }


    public RetryProxyBuilder<V> retryIfResult(Predicate<RetryTaskResult<V>> resultPredicate) {
        AssertsUtil.checkNotNull(resultPredicate, "resultPredicate may not be null");
        if(this.rejectionPredicate != null) {
            this.rejectionPredicate = this.rejectionPredicate.or(resultPredicate);
        }else{
            this.rejectionPredicate = resultPredicate;
        }
        return this;
    }

    public RetryProxyBuilder<V> withGlobalRetryListener(RetryListener<V> listener) {
        AssertsUtil.checkNotNull(listener, "listener may not be null");
        this.globalRetryListeners.add(listener);
        return this;
    }

    public RetryProxyBuilder<V> withGlobalRetryResultListener(RetryResultListener<V> listener) {
        AssertsUtil.checkNotNull(listener, "listener may not be null");
        this.globalRetryResultListeners.add(listener);
        return this;
    }

    public RetryProxyBuilder<V> withStopStrategy(StopStrategy stopStrategy)  throws IllegalStateException{
        AssertsUtil.checkNotNull(stopStrategy, "stopStrategy may not be null");
        AssertsUtil.checkState(this.stopStrategy == null, "a stop strategy has already been set %s", new Object[]{this.stopStrategy});
        this.stopStrategy = stopStrategy;
        return this;
    }

    public RetryProxyBuilder<V> withWaitComponent(Long sleepTime, TimeUnit timeUnit) {
        AssertsUtil.checkNotNull(sleepTime, "sleepTime may not be null");
        AssertsUtil.checkNotNull(timeUnit, "timeUnit may not be null");
        this.sleepTime = sleepTime;
        this.timeUnit = timeUnit;
        return this;
    }

    public RetryProxyBuilder<V> retryIfException() {
        this.exceptionPredicate = new ExceptionPredicate(Throwable.class);
        return this;
    }

    public RetryProxy<V> build() {
        return new RetryProxy<V>(
                this.sleepTime,
                this.timeUnit,
                this.stopStrategy,
                this.scheduledExecutorService,
                this.rejectionPredicate,
                this.exceptionPredicate,
                this.globalRetryListeners,
                this.globalRetryResultListeners);
    }

}
