import com.edward.retry.proxy.core.RetryProxy;
import com.edward.retry.proxy.core.RetryProxyBuilder;
import com.edward.retry.proxy.core.RetryTask;
import com.edward.retry.proxy.core.RetryTaskResult;
import com.edward.retry.proxy.strategy.RetryListener;
import com.edward.retry.proxy.strategy.StopStrategies;
import com.edward.retry.proxy.utils.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ：eddie
 * @description：
 * @date ：Created in 2024/3/1 14:48
 */
public class RetryTest {

    public static final ScheduledExecutorService service = Executors.newScheduledThreadPool(4, new NamedThreadFactory("RETRY_SERVICE"));
    private final RetryProxy<String> retryProxy = RetryProxyBuilder.<String>newBuilder(service)
            .retryIfException()
            .retryIfResult(result -> !result.isSuccess())
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .withWaitComponent(2L, TimeUnit.SECONDS)
            .withGlobalRetryListener(retryTask -> {
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println("global  pre-hook:" + retryTask.getId() + " attempt " + (retryTask.getAttemptNumber() + 1) + " times.");
            })
            .withGlobalRetryResultListener(((retryTask, retryTaskResult) -> {
                System.out.println("global post-hook:" + retryTask.getId() + " after attempt " + (retryTask.getAttemptNumber() + 1) + " times.");
            }))
            .build();

    @Test
    public void testRetrySuccess() throws Throwable {
        RetryTask<String> retryTaskAttemptOnce = new RetryTask<>(() -> foo("Hello!"));

        retryTaskAttemptOnce.addRetryListeners(retryTask -> {
            System.out.println("pre-hook task listener : " + retryTask.getId());
        });
        retryTaskAttemptOnce.addRetryResultListener((retryTask, retryTaskResult) -> {
            System.out.println("post-hook task Result listener : " + retryTask.getId());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        });

        retryProxy.retryCall(retryTaskAttemptOnce);

        while (!retryTaskAttemptOnce.isDone()) {
            Thread.sleep(200);
        }
    }

    @Test
    public void testRetryFail() throws Throwable {
        RetryTask<String> retryTaskAttemptAllTimes = new RetryTask<>(() -> foo(null));
        retryTaskAttemptAllTimes.addRetryListeners(retryTask -> {
            System.out.println("pre-hook task listener : " + retryTask.getId());
        });
        retryTaskAttemptAllTimes.addRetryResultListener((retryTask, retryTaskResult) -> {
            System.out.println("post-hook task Result listener : " + retryTask.getId());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
        });
        retryProxy.retryCall(retryTaskAttemptAllTimes);

        while (!retryTaskAttemptAllTimes.isDone()) {
            Thread.sleep(200);
        }
    }

    public RetryTaskResult<String> foo(String str) {
        System.out.println("foo is executing...input:" + str);
        return StringUtil.isEmpty(str) ? new RetryTaskResult<>(false, "filled") : new RetryTaskResult<>(true, str);
    }
}
