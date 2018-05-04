package com.github.lzyzsd.jsbridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * helper
 * Created by silen on 04/05/2018.
 */

public class BridgeHandlerManager {
    private static BridgeHandlerManager mInstance;
    private Map<Class, List<Method>> METHOD_CACHE;


    private BridgeHandlerManager() {
        METHOD_CACHE = new HashMap<>();
    }

    public static BridgeHandlerManager getInstance() {
        if (mInstance == null) {
            synchronized (BridgeHandlerManager.class) {
                if (mInstance == null) {
                    mInstance = new BridgeHandlerManager();
                }
            }
        }
        return mInstance;
    }

    public void registerHandler(final Object obj, BridgeWebView wv) {
        try {
            Class<?> aClass = obj.getClass();
            List<Method> methods = findMethods(aClass);

            for (final Method method : methods) {
                method.setAccessible(true);
                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation != null) {
                    if (annotation.handlerMode().equals(HandlerMode.SEND)) {
                        wv.setDefaultHandler(new MethodDefaultHandler(obj, method));
                    } else {
                        wv.registerHandler(method.getName(), new MethodDefaultHandler(obj, method));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Method> findMethods(Class aClass) {
        List<Method> methods = METHOD_CACHE.get(aClass);
        if (methods == null) {
            methods = new ArrayList<>();
            for (Method method : aClass.getDeclaredMethods()) {
                method.setAccessible(true);
                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation != null) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    private void onDestory() {
        METHOD_CACHE.clear();
        mInstance = null;
        System.gc();
    }
}
