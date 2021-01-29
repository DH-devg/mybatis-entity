package com.github.devgcoder.mybatis.entity;

import com.github.devgcoder.mybatis.entity.service.MybatisEntityService;
import com.github.devgcoder.mybatis.entity.service.MybatisEntityServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author duheng
 * @Date 2021/1/22 20:27
 */
@Configuration
@ConditionalOnWebApplication //web应用才生效
public class MybatisEntityAutoConfiguration {

	@Bean
	public MybatisEntityPlugin pageInterceptor() {
		return new MybatisEntityPlugin();
	}

	@Bean
	public MybatisEntityService mybatisEntityService() {
		return new MybatisEntityServiceImpl();
	}

}
