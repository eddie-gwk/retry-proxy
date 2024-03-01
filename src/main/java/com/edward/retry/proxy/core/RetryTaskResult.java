package com.edward.retry.proxy.core;

import java.io.Serializable;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:20
 */
public class RetryTaskResult<T> implements Serializable {

    private boolean success;
    private boolean exception;
    private Throwable throwable;
    private T returnData;

    public RetryTaskResult() {
        this.success = false;
        this.exception = false;
        this.throwable = null;
        this.returnData = null;
    }

    public RetryTaskResult(boolean success, T returnData) {
        this.success = success;
        this.returnData = returnData;
    }

    @Override
    public String toString() {
        return String.format("success=%s,exception=%s,throwable=%s,returnData=%s", success, exception, throwable.getMessage(), returnData);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public T getReturnData() {
        return returnData;
    }

    public void setReturnData(T returnData) {
        this.returnData = returnData;
    }
}
