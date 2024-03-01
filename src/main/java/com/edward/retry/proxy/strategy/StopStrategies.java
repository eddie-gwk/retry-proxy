package com.edward.retry.proxy.strategy;

import com.edward.retry.proxy.core.RetryTask;
import com.edward.retry.proxy.utils.AssertsUtil;
import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 14:21
 */
public class StopStrategies {

    public static StopStrategy stopAfterAttempt(int attemptNumber) {
        return new StopAfterAttemptStrategy(attemptNumber);
    }

    @Immutable
    private static final class StopAfterAttemptStrategy implements StopStrategy {

        private final int maxAttemptNumber;

        public StopAfterAttemptStrategy(int maxAttemptNumber) {
            AssertsUtil.checkArgument(maxAttemptNumber >= 1, "maxAttemptNumber must be >= 1 but is %d", new Object[]{maxAttemptNumber});
            this.maxAttemptNumber = maxAttemptNumber;
        }

        @Override
        public boolean shouldStop(RetryTask<?> retryTask) {
            return retryTask.getAttemptNumber() >= this.maxAttemptNumber;
        }
    }
}
