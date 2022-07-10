package com.le.servlet;

import com.le.http.GPRequest;
import com.le.http.GPResponse;
import com.le.http.GPServlet;

import java.io.IOException;

/**
 * @Auther: xll
 * @Desc:
 */
public class SecondServlet extends GPServlet {
    @Override
    public void doGet(GPRequest request, GPResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(GPRequest request, GPResponse response) throws IOException{
        response.write("this is second msg");
    }
}
