package com.le.consumer;

import com.le.api.IRpcCalculate;
import com.le.consumer.proxy.RpcProxy;

/**
 * @Auther: xll
 * @Desc: 消费端、发起RPC远程调用
 */
public class RpcConsumer {

    public static void main(String[] args) {
        /** RPC方法调用 */
        IRpcCalculate cal = RpcProxy.create(IRpcCalculate.class);

        int a = 8, b = 2;
        System.out.println(cal.add(a, b));
        System.out.println(cal.sub(a, b));
        System.out.println(cal.mul(a, b));
        System.out.println(cal.div(a, b));

    }
}
