package com.le.provider;

import com.le.api.IRpcCalculate;

/**
 * @Auther: xll
 * @Desc:
 */
public class RpcCalculate implements IRpcCalculate {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mul(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
