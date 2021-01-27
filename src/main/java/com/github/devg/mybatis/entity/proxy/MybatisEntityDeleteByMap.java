package com.github.devg.mybatis.entity.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;

/**
 * @author duheng
 * @Date 2021/1/25 17:03
 */
public class MybatisEntityDeleteByMap implements MybatisEntityInvoke {

	public static final String DELETEMAP = "mybatisEntityDeleteMap";
	public static final String tableName = "tableName";

	private Invocation invocation;

	public MybatisEntityDeleteByMap(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramDeleteMap = (Map<String, Object>) paramsMap.get(DELETEMAP);
		if (null == paramDeleteMap || !paramDeleteMap.containsKey(tableName)) {
			throw new Exception("the tableName can not be null");
		}
		int j = 0;
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		String deleteTableName = (String) paramDeleteMap.get(tableName);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("DELETE FROM ").append(deleteTableName).append(" WHERE ");
		for (String key : paramDeleteMap.keySet()) {
			if (null == key || key.length() <= 0 || key.equals(tableName)) {
				continue;
			}
			if (j > 0) {
				sqlBuffer.append(" AND ");
			}
			Object result = paramDeleteMap.get(key);
			sqlBuffer.append(key).append("=?");
			params.put(key, result);
			parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), key, result.getClass()).build());
			j++;
		}
		String sql = sqlBuffer.toString();
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, params);
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		return executor.update(builder.build(), params);
	}
}
