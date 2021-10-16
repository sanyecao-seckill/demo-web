package com.demo.controller;

import com.demo.model.ActivityDescDTO;
import com.demo.model.ProductDescDTO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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

//    @Autowired
//    RedisTools redisTools;

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
            activityDTO.setActivityEnd(calendar.getTime());

            Result<Integer> result = activityExportService.createActivity(activityDTO);
            if(StringUtils.isEquals(result.getCode(), ResultCodeConstant.SUCCESS)){
                return "创建活动成功!";
            }else{
                return result.getMessage();
            }
        }catch (Exception e){
            logger.error(e);
        }
        return "创建活动失败!";
    }


    @RequestMapping(value = {"/activityDescData"}, method = {RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public ActivityDescDTO activityDescData(String productId) {
        try{
            Result<SeckillActivityDTO> result = activityExportService.queryActivityByCondition(productId,null);
            if(result == null || result.getData() == null){
                return null;
            }
            ActivityDescDTO descDTO = new ActivityDescDTO();
            SeckillActivityDTO activityDTO = result.getData();
            BeanUtils.copyProperties(activityDTO,descDTO);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            descDTO.setActivityStartStr(sf.format(activityDTO.getActivityStart()));
            descDTO.setActivityEndStr(sf.format(activityDTO.getActivityEnd()));

            Integer status = activityDTO.getStatus();
            String statusDesc="";
            if(status==0){
                statusDesc="未开始";
            }else if(status==1){
                statusDesc="进行中";
            }else {
                statusDesc="已结束";
            }
            descDTO.setStatusStr(statusDesc);

            return descDTO;
        }catch (Exception e){
            logger.error("查询活动信息异常",e);
        }

        return null;
    }

    @RequestMapping(value = {"/productDescData"}, method = {RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public ProductDescDTO productDescData(String productId) {
        try{
            Result<ProductInfoDTO> productInfoDTOResult = productExportService.queryProduct(productId);

            if(productInfoDTOResult == null || productInfoDTOResult.getData() == null){
                return null;
            }

            ProductDescDTO descDTO = new ProductDescDTO();
            ProductInfoDTO productInfo = productInfoDTOResult.getData();
            BeanUtils.copyProperties(productInfo,descDTO);
            descDTO.setTagStr(productInfo.getTag()==1?"普通商品":"秒杀商品");

            return descDTO;
        }catch (Exception e){
            logger.error(e);
        }
        return null;
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
    public ProductDetailDTO product(String productId, HttpServletRequest request) {

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


    @RequestMapping(value = {"/activityDesc"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String activityDesc() {
        return "/activity_desc";
    }

    @RequestMapping(value = {"/productDesc"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String productDesc() {
        return "/product_desc";
    }

    @RequestMapping(value = {"/index"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String index() {
        return "/product";
    }

    @RequestMapping(value = {"/payPage"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
    public String payPage() {
        return "/payment";
    }


//    @RequestMapping(value = {"/redis/LoadStore"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
//    @ResponseBody
//    public String LoadStore(String productId,String storeNum) {
//        redisTools.set(productId,storeNum);
//        return "成功";
//    }
//
//    @RequestMapping(value = {"/redis/queryStore"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
//    @ResponseBody
//    public String queryStore(String productId) {
//        return redisTools.get(productId);
//    }
//
//    @RequestMapping(value = {"/redis/eval"}, method = {RequestMethod.POST,RequestMethod.GET} , produces = "text/html;charset=UTF-8")
//    @ResponseBody
//    public String redisEval(String productId,String buyNum) {
//        return redisTools.eval(productId,buyNum);
//    }

}
