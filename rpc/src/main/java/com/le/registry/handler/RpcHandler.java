package com.le.registry.handler;

import com.le.core.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: xll
 * @Desc:
 */
public class RpcHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOG = Logger.getLogger(RpcHandler.class);

    /** 保存扫描的类名 */
    private static List<String> classCache = new ArrayList<>();
    /** 对扫描的类名进行注册 */
    private static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();

    public RpcHandler() {
        scanClasses("com.le.provider");
        doRegister();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();

        RpcMessage req = (RpcMessage) msg;
        if (registryMap.containsKey(req.getClassName())) {
            Object clazz = registryMap.get(req.getClassName());
            Method method = clazz.getClass().getMethod(req.getMethodName(), req.getParameters());
            result = method.invoke(clazz, req.getValues());
        }

        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 扫描packageName路径下的类
     * @param packageName
     */
    private void scanClasses(String packageName) {
        assert packageName != null;
        LOG.info("scan " + packageName);

        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanClasses(packageName + "." + file.getName());
            } else {
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    /**
     * 对classCache中的类进行实例化
     */
    private void doRegister() {
        LOG.info("register classCache");
        for (String className : classCache) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> anInterface = clazz.getInterfaces()[0];
                // register
                registryMap.put(anInterface.getName(), clazz.newInstance());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
