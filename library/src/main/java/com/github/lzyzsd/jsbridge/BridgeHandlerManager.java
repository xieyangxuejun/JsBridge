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

    /**
     * 一次注册所有的方法,如果obj不同需要多次注册
     *
     * @param obj
     * @param wv
     */
    public void registerHandler(Object obj, BridgeWebView wv) {
        try {
            Class<?> aClass = obj.getClass();
            List<Method> methods = findMethods(aClass);

            for (final Method method : methods) {
                JavascriptInterface a = method.getAnnotation(JavascriptInterface.class);
                if (a != null) {
                    if (a.handlerMode().equals(HandlerMode.SEND)) {
                        wv.setDefaultHandler(new MethodDefaultHandler(obj, method));
                    } else if (a.handlerMode().equals(HandlerMode.REGISTER)) {
                        wv.registerHandler(method.getName(), new MethodDefaultHandler(obj, method));
                    } else {
                        //call handler和其他处理模式不反应
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
                JavascriptInterface a = method.getAnnotation(JavascriptInterface.class);
                if (a != null) {
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
