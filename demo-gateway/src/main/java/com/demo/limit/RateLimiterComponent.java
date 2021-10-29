package com.demo.limit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 方法限流公用组件
 */
@Component
public class RateLimiterComponent {

	Logger logger = LogManager.getLogger(RateLimiterComponent.class);

	private static final Cache<String, RateLimiter> RATE_LIMITER_MAP = CacheBuilder.newBuilder()
																					.maximumSize(50)
																					.build();
	/**
	 * 这里的限流key以及限流阈值，可以写死，也可以从外部动态传入，然后从配置文件或配置中心动态获取对应值
	 */
	private static final String INIT_LIMIT = "1";//限流key-进结算页

	private static final String SUBMIT_LIMIT = "2";//限流key-提单

	@Value("${limiter.init.permitsPerSecond:2}")
	private double initPermitsPerSecond; //限流阈值-进结算页,每秒允许通过的请求数

	@Value("${limiter.submit.permitsPerSecond:1}")
	private double submitPermitsPerSecond; //限流阈值-提单,每秒允许通过的请求数

	/**
	 * 进结算页限流
	 * @return true 被限流, false 未被限流
	 */
	public boolean isLimitedByInit(){
		return isLimited(INIT_LIMIT,initPermitsPerSecond);
	}

	/**
	 * 提单限流
	 * @return true 被限流, false 未被限流
	 */
	public boolean isLimitedBySubmit(){
		return isLimited(SUBMIT_LIMIT,submitPermitsPerSecond);
	}

	/**
	 * 公共限流方法
	 * @param limiterKey
	 * @param permitsPerSecond
	 * @return
	 */
	private boolean isLimited(String limiterKey,double permitsPerSecond) {
		/**
		 * 从配置文件或者配置中心中拉取进结算页的限流阈值
		 *   大于0的整数 ：正常开启限流
		 *   等于0 ：则整个接口降级
		 *   小于0 ：不开启限流
		 */
		//等于0 ：则整个接口降级
		if(permitsPerSecond == 0){
			return true;
		}
		//小于0 ：不开启限流
		if(permitsPerSecond < 0){
			return false;
		}
		try {
			/**
			 * 从cache中获取对应的限流器，如果不存在则新建一个
			 *
			 * 底层根据令牌漏桶算法思想实现：比如1秒允许通过2个请求，那么也就是每500秒生成一个令牌
			 */

			RateLimiter rateLimiter = RATE_LIMITER_MAP.get(limiterKey, () -> RateLimiter.create(permitsPerSecond));
			//判断是否达到限流阈值,并根据最新的阈值更新限流器配置
			if (isLimitedAndUpdateRate(rateLimiter, permitsPerSecond)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(limiterKey+"限流异常：",e);
		}

		return false;
	}

	private boolean isLimitedAndUpdateRate(RateLimiter rateLimiter, double permitsPerSecond) {
		//如果限流器设置阈值有变更，则更新
		if (rateLimiter.getRate() != permitsPerSecond) {
			rateLimiter.setRate(permitsPerSecond);
		}
        //获取令牌，成功获取则不被限流，未获取到，则意味着被限流
		return !rateLimiter.tryAcquire();
	}

}
