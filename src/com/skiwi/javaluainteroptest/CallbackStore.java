
package com.skiwi.javaluainteroptest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.*;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Frank van Heeswijk
 */
//public class CallbackStore extends TwoArgFunction {
//    private static final Map<String, List<LuaValue>> CALLBACK_MAP = new HashMap<>();
//    
//    @Override
//    public LuaValue call(final LuaValue modname, final LuaValue env) {
//        LuaValue library = tableOf();
//        library.set("addCallback", new addCallback());
//        env.set("CallbackStore", library);
//        return library;
//    }
//    
//    public static List<String> performCallback(final String key, final String value) {
//        Objects.requireNonNull(key, "key");
//        Objects.requireNonNull(value, "value");
//        return CALLBACK_MAP.getOrDefault(key, Collections.emptyList()).stream()
//            .map(function -> function.invoke(LuaValue.valueOf(value)).tojstring())
//            .collect(Collectors.toList());
//    }
//    
//    public static void reset() {
//        CALLBACK_MAP.clear();
//    }
//    
//    public static class addCallback extends TwoArgFunction {
//        @Override
//        public LuaValue call(final LuaValue key, final LuaValue function) {
//            key.checkstring();
//            function.checkfunction();
//            CALLBACK_MAP.putIfAbsent(key.tojstring(), new ArrayList<>());
//            CALLBACK_MAP.get(key.tojstring()).add(function);
//            return LuaValue.NIL;
//        }
//    }
//}
public class CallbackStore {
    private final Map<String, List<LuaValue>> callbackMapping = new HashMap<>();
    
    public void addCallback(final String key, final LuaValue function) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(function, "function");
        callbackMapping.putIfAbsent(key, new ArrayList<>());
        callbackMapping.get(key).add(function);
    }
    
    public List<String> performCallback(final String key, final String value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        return callbackMapping.getOrDefault(key, Collections.emptyList()).stream()
            .map(function -> function.invoke(LuaValue.valueOf(value)).tojstring())
            .collect(Collectors.toList());
    }
}