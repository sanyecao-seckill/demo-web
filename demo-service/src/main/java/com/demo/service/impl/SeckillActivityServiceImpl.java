package com.demo.service.impl;

import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.export.ActivityExportService;
import com.demo.service.SeckillActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {
    @Autowired
    ActivityExportService activityExportService;

    @Override
    public Result<Integer> createActivity(SeckillActivityDTO activityDTO) {
        return activityExportService.createActivity(activityDTO);
    }
}
