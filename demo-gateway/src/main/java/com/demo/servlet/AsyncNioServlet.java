//package com.demo.servlet;
//
//import javax.servlet.AsyncContext;
//import javax.servlet.ReadListener;
//import javax.servlet.ServletException;
//import javax.servlet.ServletInputStream;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * servlet3.1开始，支持IO非阻塞
// */
//@WebServlet(urlPatterns = "/test/asyncNioServlet",asyncSupported = true)
//public class AsyncNioServlet extends HttpServlet {
//
//    /**
//     * @param req
//     * @param resp
//     * @throws ServletException
//     * @throws IOException
//     */
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        //开启异步
//        AsyncContext asyncCtx = req.startAsync();
//        //设置超时时间
//        asyncCtx.setTimeout(2000);
//
//        //开始读取数据流
//        ServletInputStream inputStream =req.getInputStream();
//
//        System.out.println("执行Servlet的线程名称："+Thread.currentThread().getName());
//
//        //增加读监听
//        inputStream.setReadListener(new ReadListener() {
//            @Override
//            public void onDataAvailable() throws IOException {
//            }
//
//            //读取完毕后，执行回调
//            @Override
//            public void onAllDataRead() throws IOException {
//                System.out.println("执行IO读取完毕后的线程名称："+Thread.currentThread().getName());
//
//                try{
//                    StringBuilder sb = new StringBuilder();
//                    byte[] b = new byte[1024];
//                    int l = -1;
//                    while (inputStream.isReady() && (l = inputStream.read(b)) > 0) {
//                        sb.append(new String(b, 0, l));
//                    }
//
//                    //转成字符串
//                    String res = sb.toString();
//
//                    PrintWriter out = asyncCtx.getResponse().getWriter();
//
//                    //返回前端
//                    out.write(res);
//                    out.flush();
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//            }
//        });
//
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//    }
//}
