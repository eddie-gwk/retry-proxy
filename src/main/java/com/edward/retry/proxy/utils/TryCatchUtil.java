package com.edward.retry.proxy.utils;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 15:00
 */
public class TryCatchUtil {

    public static void safetyPost(Runnable runnable) {
        try{
            runnable.run();
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
