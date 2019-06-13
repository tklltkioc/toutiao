package com.nowcoder.toutiao.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;


@Aspect
@Component
public class LogAspect {
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class);
    @Before("execution(* com.nowcoder.toutiao.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuffer sb=new StringBuffer();
        for (Object arg:joinPoint.getArgs()){
            if (arg!=null){
                sb.append("arg:" + arg.toString()+" ");
            }
        }
        logger.info("before method"+sb.toString());
    }
    @After("execution(* com.nowcoder.toutiao.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after method"+new Date());
    }
}
