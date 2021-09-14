package com.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.demo.exception.BizException;
import com.demo.model.SettlementInitDTO;
import com.demo.model.SettlementSubmitDTO;
import com.demo.service.SettlementService;
import com.demo.support.constant.ResultCodeConstant;
import com.demo.support.dto.*;
import com.demo.support.export.ActivityExportService;
import com.demo.support.export.SettlementExportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class SettlementServiceImpl implements SettlementService {

    Logger logger = LogManager.getLogger(SettlementServiceImpl.class);

    @Autowired
    ActivityExportService activityExportService;

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

        //2.设置活动商品相关信息
        initDTO.setLimitNum(String.valueOf(activityDTO.getLimitNum()));
        initDTO.setActivityName(activityDTO.getActivityName());
        initDTO.setProductPrice(activityDTO.getActivityPrice().toPlainString());
        initDTO.setProductPictureUrl(activityDTO.getActivityPictureUrl());
        initDTO.setProductId(productId);

        //3.调用初始化接口
        SettlementDataRequestDTO requestDTO = new SettlementDataRequestDTO();
        requestDTO.setUserId(UUID.randomUUID().toString());
        requestDTO.setBuyNum(Integer.parseInt(buyNum));
        requestDTO.setProductId(productId);

        Result<SettlementDataDTO> dataDTOResult = settlementExportService.settlementData(requestDTO);
        logger.info("结算页初始化-初始数据出参：" + JSON.toJSONString(activityResult));
        if(dataDTOResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(dataDTOResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(dataDTOResult.getMessage());
        }
        SettlementDataDTO settlementDataDTO = dataDTOResult.getData();

        //4.设置结算元素
        initDTO.setTotalPrice(settlementDataDTO.getTotalPrice().toPlainString());
        //5.获取结算相关的，获取或计算价格，支付方式，配送方式，要调用相关的接口来组装对应的信息
        initDTO.setPayType(String.valueOf(settlementDataDTO.getPayType()));
        //6.用户维度数据，包括地址、发票等，如果支持虚拟资产，像优惠券、红包等，还要调用相关的接口来组装对应的信息
        initDTO.setAddress(settlementDataDTO.getAddress());

        return initDTO;
    }

    @Override
    public Map<String, Object> dependency() {
        return null;
    }

    @Override
    public SettlementSubmitDTO submitOrder(SettlementOrderDTO requestDTO) throws BizException {
//        //1.校验活动相关的有效性
//        Result<SeckillActivityDTO> activityResult = activityExportService.queryActivity(requestDTO.getProductId());
//        logger.info("结算页提单-查询活动出参：" + JSON.toJSONString(activityResult));
//        if(activityResult == null){
//            throw new BizException("系统异常");
//        }
//        if(!StringUtils.endsWithIgnoreCase(activityResult.getCode(), ResultCodeConstant.SUCCESS)){
//            throw new BizException(activityResult.getMessage());
//        }
//        SeckillActivityDTO activityDTO = activityResult.getData();
//
//        Integer buyNum = requestDTO.getBuyNum();
//        //1.1校验单次限购
//        if(buyNum>activityDTO.getLimitNum()){
//            throw new BizException("超过了单次限购数量");
//        }
//        //1.2校验库存
//        if(buyNum>activityDTO.getStockNum()){
//            throw new BizException("商品已售完");
//        }
//        //1.3校验活动有效期
//        Date nowDate = new Date();
//        if(nowDate.before(activityDTO.getActivityStart()) || nowDate.after(activityDTO.getActivityEnd())){
//            throw new BizException("不在活动有效期");
//        }

        //2.风控

        //3.提交订单
        requestDTO.setUserId(UUID.randomUUID().toString());
        Result<String> orderResult = settlementExportService.submitOrder(requestDTO);
        if(orderResult == null){
            throw new BizException("系统异常");
        }
        if(!StringUtils.endsWithIgnoreCase(orderResult.getCode(), ResultCodeConstant.SUCCESS)){
            throw new BizException(orderResult.getMessage());
        }
        if(StringUtils.isEmpty(orderResult.getData())){
            throw new BizException("抢购失败");
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
