package com.demo.runnable;

import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试spring对servlet3.0异步功能的封装
 */
public class CustomSpringRunnable implements Runnable {

    private DeferredResult deferredResult;

    private HttpServletRequest request;

    public CustomSpringRunnable(DeferredResult deferredResult, HttpServletRequest request) {
        this.deferredResult = deferredResult;
        this.request = request;
    }

    @Override
    public void run() {
        //模拟业务逻辑
        String productId = request.getParameter("productId");
        if(StringUtils.isBlank(productId)){
            productId = "0000000";
        }
        //返回
        deferredResult.setResult(productId);
    }
}
