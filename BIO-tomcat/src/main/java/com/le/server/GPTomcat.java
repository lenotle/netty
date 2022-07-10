package com.le.server;


import com.le.http.GPRequest;
import com.le.http.GPResponse;
import com.le.http.GPServlet;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Auther: xll
 * @Desc: tomcat 服务端
 */
public class GPTomcat {
    private static final Logger log = Logger.getLogger(GPTomcat.class);

    private int port = 8080;
    private ServerSocket server;

    private Map<String, GPServlet> servletMap = new HashMap<>();


    // 1. 绑定端口
    // 2. 根据配置文件信息，做关系映射,servlet 继承HttpServlet
    void init() {
        log.info("init properties");
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("web.properties"));

            for (Object k : properties.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = properties.getProperty(key);
                    String className = properties.getProperty(servletName + ".className");
                    GPServlet obj = (GPServlet) Class.forName(className).newInstance();

                    servletMap.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        init();

        try {
            server = new ServerSocket(port);
            log.info("server has running, listen on " + this.port);

            while (true) {

                Socket client = server.accept();
                process(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) {
        try {
            // 3. http请求
            InputStream ins = client.getInputStream();
            OutputStream ous = client.getOutputStream();

            GPRequest request = new GPRequest(ins);
            GPResponse response = new GPResponse(ous);
            // 4. 从http协议中拿取信息
            String uri = request.getUri();

            log.info("recv: " + uri);
            if (servletMap.containsKey(uri)) {
                // 5. 调用实例化对象的的doGet/ doPost方法
                servletMap.get(uri).service(request, response);
            } else {
                response.write("404 - Not Found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GPTomcat().start();
    }


}
