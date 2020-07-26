package com.zsw.simpletomcat.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zsw
 */
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String newSession = req.getParameter("newSession");
        System.out.println("get");
        PrintWriter writer = resp.getWriter();
        writer.write("<h1>get hello</h1> \n");
        HttpSession session = req.getSession();
        String hello = (String) session.getAttribute("hello");
        writer.write("pre session.getAttribute(\"hello\") is " + hello + "\n");
        session.setAttribute("hello", newSession);
        writer.write("now session.getAttribute(\"hello\") is " + session.getAttribute("hello") + "\n");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("post");
        resp.getWriter().write("<h1>post hello</h1>");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("hello init");
    }
}
