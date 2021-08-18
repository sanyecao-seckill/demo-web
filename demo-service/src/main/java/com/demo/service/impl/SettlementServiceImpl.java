package com.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.demo.exception.BizException;
import com.demo.model.SettlementInitDTO;
import com.demo.model.SettlementSubmitDTO;
import com.demo.service.SettlementService;
import com.demo.support.constant.ResultCodeConstant;
import com.demo.support.dto.ProductInfoDTO;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.dto.SettlementOrderDTO;
import com.demo.support.export.ActivityExportService;
import com.demo.support.export.ProductExportService;
import com.demo.support.export.SettlementExportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class SettlementServiceImpl implements SettlementService {

    Logger logger = LogManager.getLogger(SettlementServiceImpl.class);

    @Autowired
    ActivityExportService activityExportService;

    @Autowired
    ProductExportService productExportService;

    @Autowired
    SettlementExportService settlementExportService;


    @Override
    public SettlementInitDTO initData(String productId,String buyNum) throws BizException {
        SettlementInitDTO initDTO = new SettlementInitDTO();

        //1.校验活动相关的有效性
        Result<SeckillActivityDTO> activityResult = activityExportService.queryActivity(productId);
        logger.info("结算页初始化-查询活动出参：" + JSON.toJSONString(activityResult));
        if(activityResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(activityResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(activityResult.getMessage());
        }
        SeckillActivityDTO activityDTO = activityResult.getData();

        //1.1校验单次限购
        if(Integer.parseInt(buyNum)>activityDTO.getLimitNum()){
            throw new BizException("超过了单次限购数量");
        }
        //1.2校验库存
        if(Integer.parseInt(buyNum)>activityDTO.getStockNum()){
            throw new BizException("商品已售完");
        }
        //1.3校验活动有效期
        Date nowDate = new Date();
        if(nowDate.before(activityDTO.getActivityStart()) || nowDate.after(activityDTO.getActivityEnd())){
            throw new BizException("不在活动有效期");
        }

        //2.设置活动相关信息
        initDTO.setLimitNum(String.valueOf(activityDTO.getLimitNum()));
        initDTO.setActivityName(activityDTO.getActivityName());
        initDTO.setProductId(productId);

        //3.设置商品信息
        Result<ProductInfoDTO> productResult = productExportService.queryProduct(productId);
        logger.info("结算页初始化-查询商品出参：" + JSON.toJSONString(activityResult));

        if(productResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(productResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(productResult.getMessage());
        }
        ProductInfoDTO productInfo = productResult.getData();
        initDTO.setProductPrice(productInfo.getProductPrice().toPlainString());
        initDTO.setProductPictureUrl(productInfo.getPictureUrl());

        //4.计算价格
        BigDecimal totalPrice = productInfo.getProductPrice().multiply(new BigDecimal(buyNum));
        initDTO.setTotalPrice(totalPrice.toPlainString());
        //5.获取结算相关的，获取或计算价格，支付方式，配送方式，要调用相关的接口来组装对应的信息
        initDTO.setPayType("1");
        //6.用户维度数据，包括地址、发票等，如果支持虚拟资产，像优惠券、红包等，还要调用相关的接口来组装对应的信息
        initDTO.setAddress("");

        return initDTO;
    }

    @Override
    public Map<String, Object> dependency() {
        return null;
    }

    @Override
    public SettlementSubmitDTO submitOrder(SettlementOrderDTO requestDTO) throws BizException {
        //1.校验活动相关的有效性
        Result<SeckillActivityDTO> activityResult = activityExportService.queryActivity(requestDTO.getProductId());
        logger.info("结算页提单-查询活动出参：" + JSON.toJSONString(activityResult));
        if(activityResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(activityResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(activityResult.getMessage());
        }
        SeckillActivityDTO activityDTO = activityResult.getData();

        String buyNum = requestDTO.getBuyNum();
        //1.1校验单次限购
        if(Integer.parseInt(buyNum)>activityDTO.getLimitNum()){
            throw new BizException("超过了单次限购数量");
        }
        //1.2校验库存
        if(Integer.parseInt(buyNum)>activityDTO.getStockNum()){
            throw new BizException("商品已售完");
        }
        //1.3校验活动有效期
        Date nowDate = new Date();
        if(nowDate.before(activityDTO.getActivityStart()) || nowDate.after(activityDTO.getActivityEnd())){
            throw new BizException("不在活动有效期");
        }

        //2.风控

        //3.提交订单
        Result<String> orderResult = settlementExportService.submitOrder(requestDTO);
        if(orderResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(orderResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(orderResult.getMessage());
        }

        //4.根据订单号获取支付URL
        Result<String> payPageUrlResult = settlementExportService.getPayPageUrl(orderResult.getData());
        if(payPageUrlResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(payPageUrlResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(payPageUrlResult.getMessage());
        }

        //5.其他：包括一些数据统计等，可通过消息来解耦完成

        return new SettlementSubmitDTO("000000","",payPageUrlResult.getData());
    }
}
