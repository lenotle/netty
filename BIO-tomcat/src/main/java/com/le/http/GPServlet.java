package com.le.http;

import java.io.IOException;

/**
 * @Auther: xll
 * @Desc:
 */
public abstract class GPServlet {

    public void service(GPRequest request, GPResponse response) throws IOException{
        if ("Get".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public void doGet(GPRequest request, GPResponse response) throws IOException {}
    public void doPost(GPRequest request, GPResponse response) throws IOException {}
}
