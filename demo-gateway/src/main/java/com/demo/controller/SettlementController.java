package com.demo.controller;

import com.demo.service.SeckillActivityService;
import com.demo.support.constant.ResultCodeConstant;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping( "/settlement" )
public class SettlementController {

    @Autowired
    SeckillActivityService seckillActivityService;

    Logger logger = LogManager.getLogger(SettlementController.class);

    /**
     * 结算页初始化
     * @return
     */
    @RequestMapping(value = {"/initData"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String initData() {

        return "create seckill activity fail!";
    }

    /**
     * 其他依赖数据的接口
     * @return
     */
    @RequestMapping(value = {"/dependency"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String dependency() {

        return "create seckill activity fail!";
    }

    /**
     * 提交订单
     * @return
     */
    @RequestMapping(value = {"/submit"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String submit() {
        return "create seckill activity fail!";
    }

}
