package com.le.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther: xll
 * @Desc:
 */
public class GPRequest {

    private String uri;
    private String method;

    public GPRequest(InputStream ins) {
        String content = "";
        byte[] buff = new byte[1024];
        int len = 0;

        try {
            if ((len = ins.read(buff)) > 0) {
                content = new String(buff, 0, len);
                String line = content.split("\\n")[0];
                String[] array = line.split("\\s");
                this.method = array[0];
                this.uri = array[1].split("\\?")[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
