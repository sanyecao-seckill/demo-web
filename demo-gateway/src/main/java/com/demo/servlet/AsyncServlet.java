package com.demo.servlet;

import com.demo.runnable.CustomServletRunnable;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * servlet3.0开始，支持异步返回结果
 */
@WebServlet(urlPatterns = "/test/asyncServlet",asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void init() throws ServletException {
        threadPoolExecutor = new ThreadPoolExecutor(10, 50,
                3000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }


    /**
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //开启异步
        AsyncContext asyncCtx = req.startAsync();
        //设置超时时间
        asyncCtx.setTimeout(2000);
        //业务线程池执行业务逻辑
        threadPoolExecutor.execute(new CustomServletRunnable(asyncCtx));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
