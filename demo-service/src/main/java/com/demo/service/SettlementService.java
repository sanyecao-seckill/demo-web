package com.demo.service;

import java.util.Map;

public interface SettlementService {

    Map<String,Object> initData();

    Map<String,Object> dependency();

    Map<String,Object> submit();

}
