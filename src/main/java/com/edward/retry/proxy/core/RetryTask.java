package com.edward.retry.proxy.core;

import com.edward.retry.proxy.strategy.RetryListener;
import com.edward.retry.proxy.strategy.RetryResultListener;
import com.edward.retry.proxy.utils.StringUtil;
import com.edward.retry.proxy.utils.TryCatchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:20
 */
public class RetryTask<T> implements Callable<RetryTaskResult<T>> {

    private final String id;
    private RetryProxy<T> proxy;
    private Callable<RetryTaskResult<T>> actualTask;
    private final AtomicInteger attemptNumber = new AtomicInteger();
    private final List<RetryListener<T>> retryListeners;
    private final List<RetryResultListener<T>> retryResultListeners;

    private boolean done;

    public RetryTask(Callable<RetryTaskResult<T>> actualTask) {
        this.done = false;
        this.id = StringUtil.uuid();
        this.actualTask = actualTask;
        this.retryListeners = Collections.synchronizedList(new ArrayList<>());
        this.retryResultListeners = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public RetryTaskResult<T> call() throws Exception {
        RetryTaskResult<T> result = new RetryTaskResult<>();

        //execute pre-hook
        TryCatchUtil.safetyPost(() -> {
            proxy.throughGlobalRetryListener(this);
            this.throughRetryListener();
        });

        try{
            result = actualTask.call();
        }catch (Throwable throwable) {
            result.setException(true);
            result.setThrowable(throwable);
            throwable.printStackTrace();
        }finally {
            //execute post-hook
            RetryTaskResult<T> finalResult = result;
            TryCatchUtil.safetyPost(() -> {
                this.throughRetryResultListener(finalResult);
                proxy.throughGlobalRetryResultListener(this, finalResult);
            });
            // +1 try times
            attemptNumber.incrementAndGet();
            //if the conditions met, retry
            if(proxy.isRejection(finalResult) || proxy.isTargetException(finalResult)) {
                try {
                    proxy.retryCall(this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }else{
                //end the task
                done = true;
            }

            //clear cache
            if(done) {
                retryListeners.clear();
                retryResultListeners.clear();
                proxy.removeTaskCache(this.id);
            }
        }
        return result;
    }

    public void setProxy(RetryProxy<T> proxy) {
        this.proxy = proxy;
    }

    public void addRetryListeners(RetryListener<T> retryListener) {
        this.retryListeners.add(retryListener);
    }

    protected void throughRetryListener() {
        if(retryListeners.size() > 0) {
            retryListeners.forEach(listener -> listener.onRetry(this));
        }
    }

    public void addRetryResultListener(RetryResultListener<T> retryResultListener) {
        this.retryResultListeners.add(retryResultListener);
    }

    protected void throughRetryResultListener(RetryTaskResult<T> retryTaskResult) {
        if(retryResultListeners.size() > 0) {
            retryResultListeners.forEach(listener -> listener.onRetry(this, retryTaskResult));
        }
    }

    //change callable
    public void change(Callable<RetryTaskResult<T>> callable) {
        this.actualTask = callable;
    }

    public String getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public void cancel() {
        this.done = true;
    }

    public int getAttemptNumber() {
        return attemptNumber.get();
    }
}
