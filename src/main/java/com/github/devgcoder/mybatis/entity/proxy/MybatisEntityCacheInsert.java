package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.MybatisEntityCache;
import com.github.devgcoder.mybatis.entity.annos.CacheInsert;
import com.github.devgcoder.mybatis.entity.annos.CacheMapper;
import com.github.devgcoder.mybatis.entity.parser.HandleParser;
import com.github.devgcoder.mybatis.entity.parser.impl.SqlDefaultParser;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;

/**
 * @author duheng
 * @Date 2021/5/24 15:10
 */
public class MybatisEntityCacheInsert implements MybatisEntityInvoke {

	public static final String CACHEINSERTMAPWHERE = "mybatisEntityCacheInsertMapWhere";

	public static final String CACHEINSERTMAPCLASS = "mybatisEntityCacheInsertMapClass";

	public static final String CACHEINSERTMAPMETHODNAME = "mybatisEntityCacheInsertMapMethodName";


	private Invocation invocation;

	public MybatisEntityCacheInsert(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(CACHEINSERTMAPWHERE);
		String methodName = (String) paramsMap.get(CACHEINSERTMAPMETHODNAME);
		Class clazz = (Class) paramsMap.get(CACHEINSERTMAPCLASS);
		CacheMapper cacheMapper = (CacheMapper) clazz.getAnnotation(CacheMapper.class);
		if (null == cacheMapper) {
			throw new Exception("the clazz " + clazz.getName() + " CacheMapper annotation can not be null");
		}
		Method method = clazz.getMethod(methodName, null);
		CacheInsert cacheInsert = method.getAnnotation(CacheInsert.class);
		if (null == cacheInsert) {
			throw new Exception("the method " + method.getName() + " CacheInsert annotation can not be null");
		}
		String sql = cacheInsert.sql();
		String clazzName = clazz.getName();
		String cacheSql = MybatisEntityCache.sqlCacheMap.get(clazzName + MybatisEntityCache.underline + methodName);
		if (null != cacheSql && !cacheSql.equals("")) {
			sql = cacheSql;
		}
		Map<String, Object> params = new HashMap<>();
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		HandleParser handleParser = new SqlDefaultParser(sql, paramWhereMap);
		String executeSql = handleParser.getText();
		Configuration configuration = ms.getConfiguration();
		configuration.setUseGeneratedKeys(false);
		BoundSql newBoundSql = new BoundSql(configuration, executeSql, parameterMappings, params);
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		int result = executor.update(builder.build(), params);
		return result;
	}
}
