package com.demo.service.impl;

import com.demo.service.SettlementService;
import com.demo.support.export.ActivityExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettlementServiceImpl implements SettlementService {
    @Autowired
    ActivityExportService activityExportService;

    @Override
    public Map<String, Object> initData() {
        return null;
    }

    @Override
    public Map<String, Object> dependency() {
        return null;
    }

    @Override
    public Map<String, Object> submit() {
        return null;
    }
}
