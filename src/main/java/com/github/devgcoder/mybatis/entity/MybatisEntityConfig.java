package com.github.devgcoder.mybatis.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author duheng
 * @Date 2021/5/21 17:19
 */
@ConfigurationProperties(prefix = "devg.mybatisentity")
public class MybatisEntityConfig {

	private String basePackage;

	private String cacheDir;

	private Integer cacheSecond;

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public Integer getCacheSecond() {
		return cacheSecond;
	}

	public void setCacheSecond(Integer cacheSecond) {
		this.cacheSecond = cacheSecond;
	}
}
