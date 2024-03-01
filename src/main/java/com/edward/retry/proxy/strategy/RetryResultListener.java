package com.edward.retry.proxy.strategy;

import com.edward.retry.proxy.core.RetryTask;
import com.edward.retry.proxy.core.RetryTaskResult;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:22
 */
public interface RetryResultListener<V> {

    void onRetry(RetryTask<V> retryTask, RetryTaskResult<V> retryTaskResult);
}
