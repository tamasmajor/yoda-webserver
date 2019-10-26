package com.major.yodaserver.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Method {
    GET("GET");

    private static final Map<String, Method> methodsByName;
    private final String methodName;

    static {
        methodsByName = Arrays.stream(values()).collect(Collectors.toMap(v -> v.methodName, v -> v));
    }

    Method(String methodName) {
        this.methodName = methodName;
    }

    public static Method methodByName(String methodName) {
        return methodsByName.get(methodName);
    }
}
