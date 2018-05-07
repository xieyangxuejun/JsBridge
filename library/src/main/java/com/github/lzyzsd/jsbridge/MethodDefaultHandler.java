package com.github.lzyzsd.jsbridge;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * 将json转换成model
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
        try {
            JavascriptInterface a = mMethod.getAnnotation(JavascriptInterface.class);
            String[] values = a.value();
            if (values.length == 0) {
                //get method params
                Class<?>[] pt = mMethod.getParameterTypes();
                if (pt != null && pt.length == 1) {
                    Class<?> cls = pt[0];
                    if (String.class.isAssignableFrom(cls)) {
                        mMethod.invoke(mObj, data);
                    } else {
                        Object obj = JsonUtil.fromJson(data, cls);
                        Method sdMethod = cls.getMethod("setData", String.class);
                        sdMethod.invoke(obj, data);
                        mMethod.invoke(mObj, obj);
                    }
                } else {
                    //throw
                    throw new BridgeHandlerException("注解无参数方式,方法参数只能一个");
                }
            } else {
                JSONObject jb = new JSONObject(data);
                Object[] args = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    args[i] = jb.opt(values[i]);
                }
                mMethod.invoke(mObj, args);
            }
            function.onCallBack(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
