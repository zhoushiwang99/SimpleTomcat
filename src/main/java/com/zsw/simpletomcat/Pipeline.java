package com.zsw.simpletomcat;


import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Pipeline类似过滤器链，Valve类似于过滤器
 */
public interface Pipeline {

    /**
     * 获取基础Valve，基础Valve总是最后一个执行
     * @return 基础阀
     */
    public Valve getBasic();

    public void setBasic(Valve valve);

    public void addValve(Valve valve);

    public Valve[] getValves();

    public void invoke(HttpRequest request, HttpResponse response)
        throws IOException, ServletException;

    public void removeValve(Valve valve);


}
