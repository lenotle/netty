package com.le.servlets;


import com.alibaba.fastjson.JSON;
import com.le.http.GPRequest;
import com.le.http.GPResponse;
import com.le.http.GPServlet;

public class SecondServlet extends GPServlet {

	@Override
	public void doGet(GPRequest request, GPResponse response) {
		doPost(request, response);
	}
	
	@Override
	public void doPost(GPRequest request, GPResponse response) {
	    String str = JSON.toJSONString(request.getParameters(),true);
	    response.write(str,200);
	}
	
}
