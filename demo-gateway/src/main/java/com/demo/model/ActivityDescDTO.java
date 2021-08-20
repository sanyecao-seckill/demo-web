package com.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ActivityDescDTO implements Serializable {
    private String activityName;
    private String productId;
    private String activityStartStr;
    private String activityEndStr;
    private Integer limitNum;
    private Integer stockNum;
    private BigDecimal activityPrice;
    private String statusStr;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getActivityStartStr() {
        return activityStartStr;
    }

    public void setActivityStartStr(String activityStartStr) {
        this.activityStartStr = activityStartStr;
    }

    public String getActivityEndStr() {
        return activityEndStr;
    }

    public void setActivityEndStr(String activityEndStr) {
        this.activityEndStr = activityEndStr;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    public BigDecimal getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(BigDecimal activityPrice) {
        this.activityPrice = activityPrice;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
