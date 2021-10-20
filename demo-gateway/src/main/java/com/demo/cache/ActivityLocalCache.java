package com.demo.cache;

import com.alibaba.fastjson.JSONObject;
import com.demo.controller.ActivityController;
import com.demo.support.dto.Result;
import com.demo.support.dto.SeckillActivityDTO;
import com.demo.support.export.ActivityExportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 用于存放活动的本地缓存
 */
@Service(value = "activityLocalCache")
public class ActivityLocalCache extends AbstractGuavaCache<String, SeckillActivityDTO> implements ILocalCache<String, SeckillActivityDTO> {

	Logger logger = LogManager.getLogger(ActivityLocalCache.class);

	@Value("${localcache.maximumSize:100}")
	private int maximumSize;

	@Value("${localcache.expire:10}")
	private int expireAfterDuration;

	@Autowired
	ActivityExportService activityExportService;

	@PostConstruct
	public void init() {
		setMaximumSize(maximumSize);
		setExpireAfterDuration(expireAfterDuration);
		super.init();
	}
	
	@Override
	public SeckillActivityDTO get(String key) {
		try {
			return getValue(key);
		} catch (Exception e) {
			logger.error("activityLocalCache exception:" + key, e);
			return null;
		}
	}

	/**
	 * 本地缓存不存在时，用于加载数据到本地缓存
	 * 注：在加载数据时，只有一个请求去加载，其他的在等待，这样可以避免并发时请求都到达目标接口
	 * @param key
	 * @return
	 */
	@Override
	protected SeckillActivityDTO fetchData(String key) {
		//通过RPC接口查询活动信息，并放入本地缓存中1秒钟（通过expireAfterDuration设置），这样1秒内对该活动查询接口的请求量就是部署的服务数
		Result<SeckillActivityDTO> activityDTOResult = activityExportService.queryActivity(key);
		logger.info("通过接口加载数据到本地缓存！！"+ JSONObject.toJSONString(activityDTOResult));
		//如果为了防止一直穿透，可以在本地缓存中设置一个标识(这里放一个空对象)
		if(activityDTOResult == null || activityDTOResult.getData() == null){
			return new SeckillActivityDTO();
		}
		return activityDTOResult.getData();
	}

}
