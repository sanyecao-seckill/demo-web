package com.demo.controller;

import com.demo.support.constant.ResultCodeConstant;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.service.SeckillActivityService;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping( "/activity" )
public class ActivityController {

    @Autowired
    SeckillActivityService seckillActivityService;

    Logger logger = LogManager.getLogger(ActivityController.class);

    @RequestMapping(value = {"/create"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String create(SeckillActivityDTO activityDTO) {
        try{
            Result<Integer> result = seckillActivityService.createActivity(activityDTO);
            if(StringUtils.isEquals(result.getCode(), ResultCodeConstant.SUCCESS)){
                return "create seckill activity success!";
            }
        }catch (Exception e){
            logger.error(e);
        }
        return "create seckill activity fail!";
    }

}
