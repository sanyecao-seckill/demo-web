package com.demo.service;

import com.demo.exception.BizException;
import com.demo.model.SettlementInitDTO;
import com.demo.model.SettlementSubmitDTO;
import com.demo.support.dto.SettlementOrderDTO;

import java.util.Map;

public interface SettlementService {

    SettlementInitDTO initData(String productId,String buyNum) throws BizException;

    Map<String,Object> dependency();

    SettlementSubmitDTO submitOrder(SettlementOrderDTO requestDTO) throws BizException;

}
