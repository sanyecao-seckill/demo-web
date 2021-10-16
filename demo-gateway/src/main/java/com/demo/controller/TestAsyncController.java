package com.demo.controller;

import com.demo.runnable.CustomSpringRunnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping( "/test" )
public class TestAsyncController {
    Logger logger = LogManager.getLogger(TestAsyncController.class);

    /**
     * 模拟业务线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        threadPoolExecutor = new ThreadPoolExecutor(10, 50,
                3000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 测试spring mvc对Servlet3.0版本开始的异步功能支持
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/testSpringAsync")
    public DeferredResult<ResponseEntity<String>> testSpringAsync(HttpServletRequest request) {
        final DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();
        //交由业务线程池来处理
        threadPoolExecutor.execute(new CustomSpringRunnable(deferredResult,request));
        //异步返回结果
        return deferredResult;
    }

}
