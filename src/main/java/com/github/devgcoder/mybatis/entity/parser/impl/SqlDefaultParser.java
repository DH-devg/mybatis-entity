package com.github.devgcoder.mybatis.entity.parser.impl;


import com.github.devgcoder.mybatis.entity.parser.HandleParser;
import com.github.devgcoder.mybatis.entity.utils.MybatisEntitySearchUtil;
import java.util.HashMap;
import java.util.Map;

public class SqlDefaultParser implements HandleParser {

	private String sql;

	private Map<String, ?> paramMap;

	public SqlDefaultParser(String sql, Map<String, ?> paramMap) {
		this.sql = sql;
		this.paramMap = paramMap;
	}

	@Override
	public String getText() {
		if (null == paramMap || paramMap.isEmpty()) {
			paramMap = new HashMap<>();
		}
		for (String key : paramMap.keySet()) {
			String value = MybatisEntitySearchUtil.mapToString(paramMap, key);
			sql = MybatisEntitySearchUtil.getSqlByParam(sql, key, value);
		}
		return MybatisEntitySearchUtil.removeEmptyValue(sql);
	}


}
