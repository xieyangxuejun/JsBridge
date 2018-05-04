package com.github.lzyzsd.jsbridge;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * Created by silen on 04/05/2018.
 */

public class MethodDefaultHandler implements BridgeHandler {
    private Object mObj;
    private Method mMethod;

    public MethodDefaultHandler(Object obj, Method method) {
        this.mMethod = method;
        this.mObj = obj;
    }

    @Override
    public void handler(String data, CallBackFunction function) {
        function.onCallBack(data);
        try {
            mMethod.setAccessible(true);
            JavascriptInterface a = mMethod.getAnnotation(JavascriptInterface.class);
            if (a.handlerMode().equals(HandlerMode.SEND)) {
                mMethod.invoke(mObj);
            } else {
                String[] value = a.value();
                if (value.length == 0) {
                    mMethod.invoke(mObj);
                } else {
                    TypeVariable<Method>[] typeParameters = mMethod.getTypeParameters();
                    Object[] args = new Object[typeParameters.length];
                    for (int i = 0; i < typeParameters.length; i++) {
                        args[i] = typeParameters[i].getName();
                    }
                    mMethod.invoke(mObj, args);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
