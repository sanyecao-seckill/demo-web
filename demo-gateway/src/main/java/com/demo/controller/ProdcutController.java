package com.demo.controller;

import com.alibaba.fastjson.JSON;
import com.demo.exception.BizException;
import com.demo.model.ProductDetailDTO;
import com.demo.model.SettlementInitDTO;
import com.demo.model.SettlementSubmitDTO;
import com.demo.service.SeckillActivityService;
import com.demo.service.SettlementService;
import com.demo.support.dto.ProductInfoDTO;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.dto.SettlementOrderDTO;
import com.demo.support.export.ActivityExportService;
import com.demo.support.export.ProductExportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping( "/product" )
public class ProdcutController {

    @Autowired
    ProductExportService productExportService;

    @Autowired
    ActivityExportService activityExportService;

    Logger logger = LogManager.getLogger(ProdcutController.class);

    /**
     * 商品详情页
     * @return
     */
    @RequestMapping(value = {"/detail"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ProductDetailDTO detail(String productId) {

        Result<ProductInfoDTO> productInfoDTOResult = productExportService.queryProduct(productId);

        if(productInfoDTOResult == null || productInfoDTOResult.getData() == null){
            return null;
        }

        ProductDetailDTO detailDTO = new ProductDetailDTO();

        ProductInfoDTO productInfo = productInfoDTOResult.getData();
        //标识  1：正常商品，2：秒杀商品 3：预约商品
        if(productInfo.getTag() == 1){
            detailDTO.setProductPrice(productInfo.getProductPrice().toPlainString());
            detailDTO.setProductPictureUrl(productInfo.getPictureUrl());
            detailDTO.setIsAvailable(0);//不可购买
            detailDTO.setProductName(productInfo.getProductName());
            return detailDTO;
        }

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
