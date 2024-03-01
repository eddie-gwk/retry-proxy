package com.edward.retry.proxy.utils;

import com.sun.istack.internal.Nullable;

/**
 * @author ：edward
 * @description：
 * @date ：Created in 2024/2/23 18:07
 */
public final class AssertsUtil {

    public static <T> T checkNotNull(T reference, @Nullable Object message) {
        if(reference == null) {
            throw new NullPointerException(String.valueOf(message));
        }else{
            return reference;
        }
    }


    public static <T> T checkNotNull(T reference, @Nullable String errorMsgTemplate, @Nullable Object... args) {
        if(reference == null) {
            throw new NullPointerException(String.format(errorMsgTemplate, args));
        }else{
            return reference;
        }
    }

    public static void checkState(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkArgument(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }
}
