package com.demo.controller;

import com.demo.model.ProductDetailDTO;
import com.demo.support.constant.ResultCodeConstant;
import com.demo.support.dto.ProductInfoDTO;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.export.ActivityExportService;
import com.demo.support.export.ProductExportService;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping( "/mock" )
public class MockController {

    @Autowired
    ProductExportService productExportService;

    @Autowired
    ActivityExportService activityExportService;

    Logger logger = LogManager.getLogger(MockController.class);

    @RequestMapping(value = {"/createActivity"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createActivity(SeckillActivityDTO activityDTO) {
        try{
            if(activityDTO.getLimitNum()==null){
                activityDTO.setLimitNum(2);
            }
            if(activityDTO.getActivityName()==null){
                activityDTO.setActivityName("荣耀手机特价998，性价比高，最优的选择，不再犹豫，买到即赚到");
            }
            if(activityDTO.getStockNum()==null){
                activityDTO.setStockNum(4);
            }
            if(activityDTO.getActivityPrice()==null){
                activityDTO.setActivityPrice(new BigDecimal("998"));
            }
            if(activityDTO.getActivityPictureUrl()==null){
                activityDTO.setActivityPictureUrl("/images/product_seckill.jpg");
            }

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH,2);
            activityDTO.setActivityStart(now);
            activityDTO.setActivityStart(calendar.getTime());

            Result<Integer> result = activityExportService.createActivity(activityDTO);
            if(StringUtils.isEquals(result.getCode(), ResultCodeConstant.SUCCESS)){
                return "创建活动成功!";
            }
        }catch (Exception e){
            logger.error(e);
        }
        return "create seckill activity fail!";
    }


    @RequestMapping(value = {"/activityDesc"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String activityDesc(String productId) {
        try{
            Result<SeckillActivityDTO> result = activityExportService.queryActivityByCondition(productId,null);
            if(result == null || result.getData() == null){
                return "查询活动信息失败!";
            }
            SeckillActivityDTO activityDTO = result.getData();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("活动名称：").append(activityDTO.getActivityName()).append("\r\n");
            stringBuilder.append("商品编号：").append(activityDTO.getProductId()).append("\r\n");
            stringBuilder.append("活动价格：").append(activityDTO.getProductId()).append("\r\n");
            stringBuilder.append("活动库存：").append(activityDTO.getStockNum()).append("\r\n");
            stringBuilder.append("单次限购：").append(activityDTO.getLimitNum()).append("\r\n");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            stringBuilder.append("开始时间：").append(sf.format(activityDTO.getActivityStart())).append("\r\n");
            stringBuilder.append("结束时间：").append(sf.format(activityDTO.getActivityEnd())).append("\r\n");

            stringBuilder.toString();
        }catch (Exception e){
            logger.error(e);
        }
        return "查询活动信息失败!";
    }

    @RequestMapping(value = {"/productDesc"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String productDesc(String productId) {
        try{
            Result<ProductInfoDTO> productInfoDTOResult = productExportService.queryProduct(productId);

            if(productInfoDTOResult == null || productInfoDTOResult.getData() == null){
                return "查询活动信息失败";
            }

            ProductInfoDTO productInfo = productInfoDTOResult.getData();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("商品名称：").append(productInfo.getProductName()).append("\r\n");
            stringBuilder.append("商品编号：").append(productId).append("\r\n");
            stringBuilder.append("商品价格：").append(productInfo.getProductPrice().toString()).append("\r\n");
            stringBuilder.append("商品标识：").append(productInfo.getTag()==1?"普通商品":"秒杀商品").append("\r\n");

            stringBuilder.toString();
        }catch (Exception e){
            logger.error(e);
        }
        return "查询活动信息失败!";
    }

    @RequestMapping(value = {"/startActivity"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String startActivity(String productId) {
        try{
            Result<Integer> countResult = activityExportService.startActivity(productId);
            if(countResult==null ||countResult.getData()==null){
                return "开始秒杀活动失败！";
            }
            if(!org.springframework.util.StringUtils.endsWithIgnoreCase(countResult.getCode(), ResultCodeConstant.SUCCESS)){
                return countResult.getMessage();
            }
            if(countResult.getData() == 0){
                return "开始秒杀活动失败！";
            }
            return "开始秒杀活动成功！";
        }catch (Exception e){
            logger.error(e);
            return "开始秒杀活动失败！";
        }
    }

    @RequestMapping(value = {"/endActivity"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String endActivity(String productId) {
        try{
            Result<Integer> countResult = activityExportService.endActivity(productId);
            if(countResult==null ||countResult.getData()==null){
                return "结束秒杀活动失败！";
            }
            if(!org.springframework.util.StringUtils.endsWithIgnoreCase(countResult.getCode(), ResultCodeConstant.SUCCESS)){
                return countResult.getMessage();
            }
            if(countResult.getData() == 0){
                return "结束秒杀活动失败！";
            }
            return "结束秒杀活动成功！";
        }catch (Exception e){
            logger.error(e);
            return "结束秒杀活动失败！";
        }
    }

    /**
     * 商品详情页
     * @return
     */
    @RequestMapping(value = {"/product"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ProductDetailDTO product(String productId) {

        Result<ProductInfoDTO> productInfoDTOResult = productExportService.queryProduct(productId);

        if(productInfoDTOResult == null || productInfoDTOResult.getData() == null){
            return null;
        }

        ProductDetailDTO detailDTO = new ProductDetailDTO();

        ProductInfoDTO productInfo = productInfoDTOResult.getData();
        //标识  1：正常商品，2：秒杀商品 3：预约商品
        detailDTO.setProductPrice(productInfo.getProductPrice().toPlainString());
        detailDTO.setProductPictureUrl(productInfo.getPictureUrl());
        detailDTO.setIsAvailable(0);//不可购买
        detailDTO.setProductName(productInfo.getProductName());
        detailDTO.setTag(productInfo.getTag());
        return detailDTO;

    }


    @RequestMapping(value = {"/index"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String index() {
        return "/product";
    }

    @RequestMapping(value = {"/payPage"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String payPage() {
        return "/payment";
    }

}
