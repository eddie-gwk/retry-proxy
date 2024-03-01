package com.edward.retry.proxy.core;

import com.edward.retry.proxy.exception.RetryException;
import com.edward.retry.proxy.strategy.ExceptionPredicate;
import com.edward.retry.proxy.strategy.RetryListener;
import com.edward.retry.proxy.strategy.RetryResultListener;
import com.edward.retry.proxy.strategy.StopStrategy;
import com.edward.retry.proxy.utils.AssertsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:20
 */
public class RetryProxy<T> {

    private final Long sleepTime;
    private final TimeUnit timeUnit;
    private final StopStrategy stopStrategy;
    private final ScheduledExecutorService scheduledExecutorService;

    private final Predicate<RetryTaskResult<T>> rejectionPredicate;
    private final ExceptionPredicate<T> exceptionPredicate;
    private final List<RetryListener<T>> globalRetryListeners;
    private final List<RetryResultListener<T>> globalRetryResultListeners;

    private final Map<String, List<Future<RetryTaskResult<T>>>> retryTaskCache = new ConcurrentHashMap<>();

    public RetryProxy(Long sleepTime, TimeUnit timeUnit, StopStrategy stopStrategy, ScheduledExecutorService scheduledExecutorService, Predicate<RetryTaskResult<T>> rejectionPredicate, ExceptionPredicate<T> exceptionPredicate, List<RetryListener<T>> globalRetryListeners, List<RetryResultListener<T>> globalRetryResultListeners) {
        this.sleepTime = sleepTime;
        this.timeUnit = timeUnit;
        this.stopStrategy = stopStrategy;
        this.scheduledExecutorService = scheduledExecutorService;
        this.rejectionPredicate = rejectionPredicate;
        this.exceptionPredicate = exceptionPredicate;
        this.globalRetryListeners = globalRetryListeners;
        this.globalRetryResultListeners = globalRetryResultListeners;
    }

    public void retryCall(RetryTask<T> task) throws Throwable{
        AssertsUtil.checkNotNull(task, "task may not be null");

        task.setProxy(this);
        if(this.stopStrategy.shouldStop(task)) {
            RetryException retryException = new RetryException(task.getAttemptNumber());
            retryException.printStackTrace();
            task.cancel();
            throw retryException;
        }
        if(task.getAttemptNumber() == 0){
            addRetryTask(task.getId(), scheduledExecutorService.submit(task));
        }else {
            addRetryTask(task.getId(), scheduledExecutorService.schedule(task, sleepTime, timeUnit));
        }
    }

    private void addRetryTask(String id, Future<RetryTaskResult<T>> future) {
        if(retryTaskCache.containsKey(id)) {
            retryTaskCache.get(id).add(future);
        } else {
            List<Future<RetryTaskResult<T>>> list = Collections.synchronizedList(new ArrayList<>());
            list.add(future);
            retryTaskCache.put(id, list);
        }
    }


    public void throughGlobalRetryListener(RetryTask<T> retryTask) {
        if(globalRetryListeners.size() > 0) {
            globalRetryListeners.forEach(listener -> listener.onRetry(retryTask));
        }
    }

    public void throughGlobalRetryResultListener(RetryTask<T> retryTask, RetryTaskResult<T> retryTaskResult) {
        if(globalRetryResultListeners.size() > 0) {
            globalRetryResultListeners.forEach(listener -> listener.onRetry(retryTask, retryTaskResult));
        }
    }

    public boolean isRejection(RetryTaskResult<T> retryTaskResult) {
        return rejectionPredicate.test(retryTaskResult);
    }

    public boolean isTargetException(RetryTaskResult<T> retryTaskResult) {
        return exceptionPredicate != null ? exceptionPredicate.apply(retryTaskResult) : false;
    }

    public void removeTaskCache(String taskId) {
        retryTaskCache.remove(taskId);
    }
}
