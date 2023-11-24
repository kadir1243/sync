package dev.kir.sync.util.reflect;

import dev.kir.sync.util.function.FunctionUtil;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @deprecated No Replacement this directly uses reflection and I will remove it to be way faster
 */
@Deprecated(forRemoval = true)
public final class ClassUtil {
    public static Optional<Method> getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        return FunctionUtil.tryInvoke(() -> type.getMethod(name, parameterTypes));
    }
}