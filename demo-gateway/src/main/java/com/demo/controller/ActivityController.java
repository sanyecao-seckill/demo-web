package com.demo.controller;

import com.demo.model.ActivityDetailDTO;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.export.ActivityExportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping( "/activity" )
public class ActivityController {

    @Autowired
    ActivityExportService activityExportService;

    Logger logger = LogManager.getLogger(ActivityController.class);

    /**
     * 查询活动库存
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = {"/queryStore"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Integer queryStore(String productId) {
        try{
            Result<Integer> result = activityExportService.queryStore(productId);
            return result.getData();
        }catch (Exception e){
            logger.error("query activity store exception:",e);
            return null;
        }
    }


    /**
     * 查询活动信息
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = {"/query"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ActivityDetailDTO query(String productId) {

        ActivityDetailDTO detailDTO = new ActivityDetailDTO();

        //标识  1：正常商品，2：秒杀商品 3：预约商品
        Result<SeckillActivityDTO> activityDTOResult = activityExportService.queryActivity(productId);
        if(activityDTOResult == null || activityDTOResult.getData() == null){
            return null;
        }
        SeckillActivityDTO activityDTO = activityDTOResult.getData();
        detailDTO.setProductPrice(activityDTO.getActivityPrice().toPlainString());
        detailDTO.setProductPictureUrl(activityDTO.getActivityPictureUrl());
        detailDTO.setProductName(activityDTO.getActivityName());

        Integer isAvailable = 1;
        if(activityDTO.getStockNum()<=0){
            isAvailable = 0;
        }
        Date now = new Date();
        if(now.before(activityDTO.getActivityStart()) || now.after(activityDTO.getActivityEnd())){
            isAvailable = 0;
        }
        detailDTO.setIsAvailable(isAvailable);

        return detailDTO;

    }

}
