package com.demo.model;

import java.io.Serializable;

public class SettlementSubmitDTO implements Serializable {

    private String code;
    private String message;
    private String payPageUrl;

    public SettlementSubmitDTO(String code, String message, String payPageUrl) {
        this.code = code;
        this.message = message;
        this.payPageUrl = payPageUrl;
    }

    public SettlementSubmitDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayPageUrl() {
        return payPageUrl;
    }

    public void setPayPageUrl(String payPageUrl) {
        this.payPageUrl = payPageUrl;
    }
}
