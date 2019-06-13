package com.nowcoder.toutiao.interceptor;


import com.nowcoder.toutiao.dao.LoginTicketDAO;
import com.nowcoder.toutiao.dao.UserDAO;
import com.nowcoder.toutiao.model.HostHolder;
import com.nowcoder.toutiao.model.LoginTicket;
import com.nowcoder.toutiao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket=null;
        if (httpServletRequest.getCookies ()!=null){
            for (Cookie cookie:httpServletRequest.getCookies ()){
                if (cookie.getName ().equals ("ticket")){
                    ticket=cookie.getValue ();
                    break;
                }
            }
        }
        //判断是否t票存在
        if (ticket!=null){
            LoginTicket loginTicket=loginTicketDAO.selectByTicket (ticket);
            if (loginTicket==null||loginTicket.getExpired ().before (new Date ())||loginTicket.getStatus ()!=0){
                return true;
            }

            //存放用户信息，存放在线程池
            User user=userDAO.selectById (loginTicket.getUserId ());
            hostHolder.setUser (user);

        }
        return true;
    }

    //modelAndView controller中的model；view代表可以传入页面上下文使用，渲染使用
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView!=null&&hostHolder.getUser ()!=null){
            modelAndView.addObject ("user",hostHolder.getUser ());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear ();

    }
}
