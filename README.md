# retry-proxy
The easiest way to retry java method.

---

# what is retry-porxy

retry-proxy is a non-blocking model application for retrying methods, which is simple to use and highly efficient in performance.

# how to use it
First, you need to pull the source code into your project.(I'm preparing to upload it to the Maven Central Repository.)
then create a proxy instance：
```java
RetryProxyBuilder.<String>newBuilder(service)
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
```

Create a task that executes until the stop condition is met, retrying if the retry condition is satisfied:

```java
RetryTask<String> retryTaskAttemptOnce = new RetryTask<>(() -> foo("Hello!"));
retryProxy.retryCall(retryTaskAttemptOnce);

while (!retryTaskAttemptOnce.isDone()) {
        Thread.sleep(200);
}
```
Note: The method requiring retry must return a <strong>RetryTaskResult</strong> object.You can set <strong>returnData</strong> to return data.