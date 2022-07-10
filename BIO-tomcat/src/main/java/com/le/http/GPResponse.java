package com.le.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Auther: xll
 * @Desc:
 */
public class GPResponse {
    private OutputStream out;

    public GPResponse(OutputStream out) {
        this.out = out;
    }

    public void write(String msg) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb
                .append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/application\n")
                .append("\r\n")
                .append(msg);
        this.out.write(sb.toString().getBytes());
    }
}
