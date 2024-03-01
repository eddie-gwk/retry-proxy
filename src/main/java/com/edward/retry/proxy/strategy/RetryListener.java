package com.edward.retry.proxy.strategy;

import com.edward.retry.proxy.core.RetryTask;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:22
 */
public interface RetryListener<V> {
    void onRetry(RetryTask<V> retryTask);
}
