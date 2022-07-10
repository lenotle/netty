package com.le.core;

import java.io.Serializable;
import java.lang.reflect.Parameter;

/**
 * @Auther: xll
 * @Desc: 定义RPC请求参数
 */
public class RpcMessage implements Serializable {
    /** 类名 */
    private String className;
    /** 请求方法 */
    private String methodName;
    /** 请求参数列表 */
    private Class<?>[] parameters;
    /** 参数值列表 */
    private Object[] values;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public void setParameters(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
