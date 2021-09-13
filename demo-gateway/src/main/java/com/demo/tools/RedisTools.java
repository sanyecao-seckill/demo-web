//package com.demo.tools;
//
//import com.alibaba.fastjson.JSON;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//import javax.annotation.PostConstruct;
//
//@Component
//public class RedisTools {
//
//    @Autowired
//    JedisPool jedisPool;
//
//    Logger logger = LogManager.getLogger(RedisTools.class);
//
//    /**
//     * lua逻辑：首先判断活动库存是否存在，以及库存余量是否够本次购买数量，如果不够，则返回0，如果够则完成扣减并返回1
//     * 两个入参，KEYS[1] : 活动库存的key
//     *         KEYS[2] : 活动库存的扣减数量
//     */
//    private String STORE_DEDUCTION_SCRIPT_LUA =
//            "local c_s = redis.call('get', KEYS[1])\n" +
//            "if not c_s or tonumber(c_s) < tonumber(KEYS[2]) then\n" +
//            "return 0\n" +
//            "end\n" +
//            "redis.call('decrby',KEYS[1], KEYS[2])\n" +
//            "return 1";
//
//    /**
//     * 在系统启动时，将脚本预加载到Redis中，并返回一个加密的字符串，下次只要传该加密窜，即可执行对应脚本，减少了Redis的预编译
//     */
//    private String STORE_DEDUCTION_SCRIPT_SHA1 = "";
//
//    @PostConstruct
//    public void init(){
//        try (Jedis jedis = jedisPool.getResource()) {
//            String sha1 = jedis.scriptLoad(STORE_DEDUCTION_SCRIPT_LUA);
//            logger.error("生成的sha1：" + sha1);
//            STORE_DEDUCTION_SCRIPT_SHA1 = sha1;
//        }
//    }
//
//    /**
//     * String-设置缓存
//     * @param key
//     * @param value
//     */
//    public void set(String key,String value){
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.set(key, value);
//        }
//    }
//
//    /**
//     * String-设置缓存
//     * 带失效时间
//     * @param key
//     * @param value
//     */
//    public void set(String key,String value,int expireTime){
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.set(key, value);
//            jedis.expire(key,expireTime);
//        }
//    }
//
//    /**
//     * String-查询
//     * @param key
//     */
//    public String get(String key){
//        try (Jedis jedis = jedisPool.getResource()) {
//            return jedis.get(key);
//        }
//    }
//
//    /**
//     * String-eval
//     * @param key
//     */
//    public String eval(String key,String buyNum){
//        logger.error("入参key:"+key+";buyNum:"+buyNum);
//        try (Jedis jedis = jedisPool.getResource()) {
//            Object obj = jedis.evalsha(STORE_DEDUCTION_SCRIPT_SHA1,2,key,buyNum);
//            return JSON.toJSONString(obj);
//        }
//    }
//}
