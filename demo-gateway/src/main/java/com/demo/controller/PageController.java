package com.demo.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping( "/" )
public class PageController {

    Logger logger = LogManager.getLogger(PageController.class);

    @RequestMapping(value = {"/sayhello"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String sayhello() {
        return "seckill web is success!";
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String index() {
        return "/product";
    }

    @RequestMapping(value = {"/payPage"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String payPage() {
        return "/payment";
    }

    @RequestMapping(value = {"/settlement"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String settlement() {
        return "/settlement";
    }

}
