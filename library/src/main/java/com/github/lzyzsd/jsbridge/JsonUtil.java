package com.github.lzyzsd.jsbridge;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class JsonUtil {

    /**
     * JSONObject对象转JavaBean
     *
     * @param data
     * @param cls
     * @return 转换结果（异常情况下返回null）
     */
    public static Object fromJson(String data, Class cls) {
        Object obj = null;

        try {
            JSONObject jb = new JSONObject(data);
            obj = cls.newInstance();

            // 取出Bean里面的所有方法  
            Method[] methods = cls.getDeclaredMethods();
            //non methods
            if (methods.length == 0) return null;
            //has methods
            for (Method method : methods) {
                // 取出方法名
                String methodName = method.getName();
                // 取出方法的类型
                Class[] clzArr = method.getParameterTypes();
                if (clzArr.length != 1 || !methodName.contains("set")) {
                    continue;
                }

                // 类型
                String type = clzArr[0].getSimpleName();

                String key = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

                // 如果map里有该key
                if (jb.has(key) && jb.get(key) != null) {
                    setValue(type, jb.get(key), method, obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 根据key从JSONObject对象中取得对应值
     *
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static String toString(JSONObject json, String key) throws JSONException {
        String retVal = null;
        if (json.isNull(key)) {
            retVal = "";
        } else {
            retVal = json.getString(key);
        }
        return retVal;
    }

    /**
     * 给JavaBean的每个属性设值
     *
     * @param type
     * @param value
     * @param method
     * @param bean
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressLint("SimpleDateFormat")
    private static boolean setValue(String type, Object value, Method method, Object bean) {

        try {
            if (value != null && !"".equals(value)) {
                if ("String".equals(type)) {
                    method.invoke(bean, value);
                } else if ("int".equals(type) || "Integer".equals(type)) {
                    method.invoke(bean, Integer.valueOf("" + value));
                } else if ("double".equals(type) || "Double".equals(type)) {
                    method.invoke(bean, Double.valueOf("" + value));
                } else if ("float".equals(type) || "Float".equals(type)) {
                    method.invoke(bean, Float.valueOf("" + value));
                } else if ("long".equals(type) || "Long".equals(type)) {
                    method.invoke(bean, Long.valueOf("" + value));
                } else if ("int".equals(type) || "Integer".equals(type)) {
                    method.invoke(bean, Integer.valueOf("" + value));
                } else if ("boolean".equals(type) || "Boolean".equals(type)) {
                    method.invoke(bean, Boolean.valueOf("" + value));
                } else if ("BigDecimal".equals(type)) {
                    method.invoke(bean, new BigDecimal("" + value));
                } else if ("Date".equals(type)) {
                    Class dateType = method.getParameterTypes()[0];
                    if ("java.util.Date".equals(dateType.getName())) {
                        java.util.Date date;
                        if ("String".equals(value.getClass().getSimpleName())) {
                            String time = String.valueOf(value);
                            String format;
                            if (time.indexOf(":") > 0) {
                                if (time.indexOf(":") == time.lastIndexOf(":")) {
                                    format = "yyyy-MM-dd H:mm";
                                } else {
                                    format = "yyyy-MM-dd H:mm:ss";
                                }
                            } else {
                                format = "yyyy-MM-dd";
                            }
                            SimpleDateFormat sf = new SimpleDateFormat();
                            sf.applyPattern(format);
                            date = sf.parse(time);
                        } else {
                            date = (java.util.Date) value;
                        }

                        if (date != null) {
                            method.invoke(bean, new Object[]{date});
                        }
                    } else if ("java.sql.Date".equals(dateType.getName())) {
                        Date date = null;
                        if ("String".equals(value.getClass().getSimpleName())) {
                            String time = String.valueOf(value);
                            String format = null;
                            if (time.indexOf(":") > 0) {
                                if (time.indexOf(":") == time.lastIndexOf(":")) {
                                    format = "yyyy-MM-dd H:mm";
                                } else {
                                    format = "yyyy-MM-dd H:mm:ss";
                                }
                            } else {
                                format = "yyyy-MM-dd";
                            }
                            SimpleDateFormat sf = new SimpleDateFormat();
                            sf.applyPattern(format);
                            date = new Date(sf.parse(time).getTime());
                        } else {
                            date = (Date) value;
                        }

                        if (date != null) {
                            method.invoke(bean, new Object[]{date});
                        }
                    }
                } else if ("Timestamp".equals(type)) {
                    Timestamp timestamp;
                    if ("String".equals(value.getClass().getSimpleName())) {
                        String time = String.valueOf(value);
                        String format;
                        if (time.indexOf(":") > 0) {
                            if (time.indexOf(":") == time.lastIndexOf(":")) {
                                format = "yyyy-MM-dd H:mm";
                            } else {
                                format = "yyyy-MM-dd H:mm:ss";
                            }
                        } else {
                            format = "yyyy-MM-dd";
                        }
                        SimpleDateFormat sf = new SimpleDateFormat();
                        sf.applyPattern(format);
                        timestamp = new Timestamp(sf.parse(time).getTime());
                    } else {
                        timestamp = (Timestamp) value;
                    }

                    method.invoke(bean, timestamp);
                } else if ("byte[]".equals(type)) {
                    method.invoke(bean, new Object[]{("" + value).getBytes()});
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将Model转换成JSONObject
     */
    public static JSONObject toJSONObject(Object o) throws Exception {
        JSONObject json = new JSONObject();
        Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            json.put(f.getName(), invokeMethod(clazz, f.getName(), o));
        }
        return json;
    }


    /**
     * 将list转换成JSONArray
     */
    public static JSONArray toJSONArray(List list) throws Exception {
        JSONArray array = null;
        if (list.isEmpty()) {
            return array;
        }
        array = new JSONArray();
        for (Object o : list) {
            array.put(toJSONObject(o));
        }
        return array;
    }

    private static Object invokeMethod(Class c, String fieldName, Object o) {
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method;
        try {
            method = c.getMethod("get" + methodName);
            return method.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

} 