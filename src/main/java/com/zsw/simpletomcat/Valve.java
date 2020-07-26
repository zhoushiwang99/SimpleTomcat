package com.zsw.simpletomcat;


import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;


public interface Valve {

    /**
     * 返回阀的实现信息
     * @return 阀的实现信息
     */
    public String getInfo();


    public void invoke(HttpRequest request, HttpResponse response,
                       ValveContext context)
        throws IOException, ServletException;


}
