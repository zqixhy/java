package com.qiao.interceptor;

import com.alibaba.fastjson.JSON;
import com.qiao.common.R;
import com.qiao.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Component
//@ComponentScan({"com.qiao.controller"})
@Slf4j
public class ProjectInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("preHandle...");
       HttpSession session = request.getSession();
        System.out.println(request.getContextPath());

/*        if(null == session.getAttribute("employee")){
            response.sendRedirect("/employee/login");
            return false;
        }*/
        //response.sendRedirect("/employee/login");
/*
        System.out.println(session.getAttribute("employee"));
*/
        if(null == session.getAttribute("employee")){
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }
        return true;
    }
}
