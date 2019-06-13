package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.model.Question;
import com.nowcoder.toutiao.model.ViewObject;
import com.nowcoder.toutiao.service.QuestionService;
import com.nowcoder.toutiao.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
public class HomeController {
    private static final Logger logger= LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;


    private List<ViewObject>getQuestions(int userId,int offset,int limit) {
        List<Question>questionList=questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject>vos=new ArrayList<ViewObject>();
        for (Question question:questionList){
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    //显示前10个
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @RequestParam(value = "pop",defaultValue = "0")int pop) {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId")int userId) {
        model.addAttribute("vos",getQuestions(userId,0,10));
//        User user=userService.getUser(userId);
//        ViewObject vo=new ViewObject();
//        vo.set("user",user);

        return "index";
    }




}
