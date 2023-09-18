package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        log.info("拦截到请求：{} ,1 {}",request,servletRequest);
        //1、获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
//        filterChain.doFilter(request,response);
//        return;
//
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls,requestURI);

        //3、如果不需要处理，则直接放行
        if(check){
            log.info("不需要拦截！");
            // 放行
            filterChain.doFilter(request,response);
            return;
        }
        log.info("拦截成功！");

        //4、判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果
        //给前端返回信息，在前端拦截器中进行跳转
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return ;

    }
    public boolean check(String [] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;

    }

}
