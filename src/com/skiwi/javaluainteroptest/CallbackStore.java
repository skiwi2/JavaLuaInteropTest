
package com.skiwi.javaluainteroptest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

/**
 *
 * @author Frank van Heeswijk
 */
public class CallbackStore {
    private final Map<String, List<LuaValue>> callbackMapping = new HashMap<>();
    
    public void addCallback(final String key, final LuaValue function) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(function, "function");
        callbackMapping.putIfAbsent(key, new ArrayList<>());
        callbackMapping.get(key).add(function);
    }
    
    public <T> List<String> performCallback(final String key, final T value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        return callbackMapping.getOrDefault(key, Collections.emptyList()).stream()
            .map(function -> function.invoke(CoerceJavaToLua.coerce(value)).tojstring())
            .collect(Collectors.toList());
    }
    
    public <T, U> List<String> performCallback(final String key, final T value, final Class<U> clazz, final Function<U, String> stringifyFunction) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(stringifyFunction, "stringifyFunction");
        return callbackMapping.getOrDefault(key, Collections.emptyList()).stream()
            .map(function -> CoerceLuaToJava.coerce(function.invoke(CoerceJavaToLua.coerce(value)).arg1(), clazz))
            .map(obj -> {
                //safe because CoerceLuaToJava.coerce already performs a cast
                @SuppressWarnings("unchecked")
                U u = (U)obj;
                return u;
            })
            .map(stringifyFunction)
            .collect(Collectors.toList());
    }
}