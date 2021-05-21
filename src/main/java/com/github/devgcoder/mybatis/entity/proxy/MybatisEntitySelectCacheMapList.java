package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.annos.CacheSelect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.RowBounds;

/**
 * @author duheng
 * @Date 2021/5/21 11:02
 */
public class MybatisEntitySelectCacheMapList implements MybatisEntityInvoke {

	public static final String SELECTCACHEMAPWHERE = "mybatisEntitySelectCacheMapWhere";

	public static final String SELECTCACHEMAPMYBATISECLASS = "mybatisEntitySelectCacheMapClass";

	private Invocation invocation;

	public MybatisEntitySelectCacheMapList(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(SELECTCACHEMAPWHERE);
		Class clazz = (Class) paramsMap.get(SELECTCACHEMAPMYBATISECLASS);
		CacheSelect cacheSelect = (CacheSelect) clazz.getAnnotation(CacheSelect.class);
		String sql = cacheSelect.sql();
		StringBuffer whereSql = new StringBuffer();
		Map<String, Object> params = new HashMap<>();
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		if (null != paramWhereMap && !paramWhereMap.isEmpty()) {
			whereSql.append(" WHERE ");
			int j = 0;
			for (String key : paramWhereMap.keySet()) {
				Object value = paramWhereMap.get(key);
				if (j > 0) {
					whereSql.append(" AND ");
				}
				whereSql.append(key).append("=?");
				parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), key, value.getClass()).build());
				params.put(key, value);
				j++;
			}
		}
		String executeSql = sql + whereSql.toString();
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), executeSql, parameterMappings, params);
		CacheKey key = executor.createCacheKey(ms, params, RowBounds.DEFAULT, newBoundSql);
		List<Map<String, Object>> list = executor.query(ms, params, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, key, newBoundSql);
		return list;
	}
}
