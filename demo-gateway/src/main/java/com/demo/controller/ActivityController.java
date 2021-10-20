package com.demo.controller;

import com.demo.cache.ILocalCache;
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

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping( "/activity" )
public class ActivityController {

    @Autowired
    ActivityExportService activityExportService;

    @Resource(name = "activityLocalCache")
    ILocalCache iLocalCache;

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
    @RequestMapping(value = {"/subQuery"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ActivityDetailDTO subQuery(String productId) {

        ActivityDetailDTO detailDTO = new ActivityDetailDTO();

        SeckillActivityDTO activityDTO = (SeckillActivityDTO)iLocalCache.get(productId);
        if(activityDTO == null){
            return null;
        }

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
