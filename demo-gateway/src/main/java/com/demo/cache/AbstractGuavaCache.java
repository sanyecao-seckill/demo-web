package com.demo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用guava实现本地缓存功能
 * @param <K>
 * @param <V>
 */
public abstract class AbstractGuavaCache<K,V> {

	/**
	 * guava cache
	 */
	private LoadingCache<K, V> cache;

	/**
	 * 缓存的key容量
	 */
	private int maximumSize;

	/**
	 * 缓存的失效时间
	 */
	private int expireAfterDuration;

	/**
	 * 缓存的失效时间单位
	 */
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	/**
	 * 构建本地缓存
	 */
	protected void init() {
		if (cache == null) {
			cache = CacheBuilder.newBuilder().maximumSize(maximumSize)
					.expireAfterWrite(expireAfterDuration, timeUnit)
					.build(new CacheLoader<K, V>() {
						@Override
						public V load(K key) {
							return fetchData(key);
						}
					});
		}
	}

	protected V getValue(K key) throws ExecutionException {
		return cache.get(key);
	}

	protected abstract V fetchData(K key);

	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public int getExpireAfterDuration() {
		return expireAfterDuration;
	}

	public void setExpireAfterDuration(int expireAfterDuration) {
		this.expireAfterDuration = expireAfterDuration;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
}
