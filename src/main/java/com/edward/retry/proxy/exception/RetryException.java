package com.edward.retry.proxy.exception;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 15:38
 */
public class RetryException extends Throwable {

    private final int numberOfFailedAttempts;

    public RetryException(int numberOfFailedAttempts) {
        super("Retrying failed to complete successfully after " + numberOfFailedAttempts + " attempts.");
        this.numberOfFailedAttempts = numberOfFailedAttempts;
    }

}
