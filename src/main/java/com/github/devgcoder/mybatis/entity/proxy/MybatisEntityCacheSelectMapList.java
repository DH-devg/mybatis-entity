package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.MybatisEntityCache;
import com.github.devgcoder.mybatis.entity.annos.CacheMapper;
import com.github.devgcoder.mybatis.entity.annos.CacheSelect;
import com.github.devgcoder.mybatis.entity.parser.HandleParser;
import com.github.devgcoder.mybatis.entity.parser.impl.SqlDefaultParser;
import java.lang.reflect.Method;
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
public class MybatisEntityCacheSelectMapList implements MybatisEntityInvoke {

	public static final String CACHESELECTMAPWHERE = "mybatisEntityCacheSelectMapWhere";

	public static final String CACHESELECTMAPCLASS = "mybatisEntityCacheSelectMapClass";

	public static final String CACHESELECTMAPMETHODNAME = "mybatisEntityCacheSelectMapMethodName";

	private Invocation invocation;

	public MybatisEntityCacheSelectMapList(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(CACHESELECTMAPWHERE);
		String methodName = (String) paramsMap.get(CACHESELECTMAPMETHODNAME);
		Class clazz = (Class) paramsMap.get(CACHESELECTMAPCLASS);
		CacheMapper cacheMapper = (CacheMapper) clazz.getAnnotation(CacheMapper.class);
		if (null == cacheMapper) {
			throw new Exception("the clazz " + clazz.getName() + " CacheMapper annotation can not be null");
		}
		Method method = clazz.getMethod(methodName, null);
		CacheSelect cacheSelect = method.getAnnotation(CacheSelect.class);
		if (null == cacheSelect) {
			throw new Exception("the method " + method.getName() + " cacheSelect annotation can not be null");
		}
		String sql = cacheSelect.sql();
		String clazzName = clazz.getName();
		String cacheSql = MybatisEntityCache.sqlCacheMap.get(clazzName + MybatisEntityCache.underline + methodName);
		if (null != cacheSql && !cacheSql.equals("")) {
			sql = cacheSql;
		}
		Map<String, Object> params = new HashMap<>();
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		HandleParser handleParser = new SqlDefaultParser(sql, paramWhereMap);
		String executeSql = handleParser.getText();
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), executeSql, parameterMappings, params);
		CacheKey key = executor.createCacheKey(ms, params, RowBounds.DEFAULT, newBoundSql);
		List<Map<String, Object>> list = executor.query(ms, params, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, key, newBoundSql);
		return list;
	}
}
