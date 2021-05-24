package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.MybatisEntityCache;
import com.github.devgcoder.mybatis.entity.annos.CacheDelete;
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

/**
 * @author duheng
 * @Date 2021/5/24 14:57
 */
public class MybatisEntityCacheDelete implements MybatisEntityInvoke {

	public static final String CACHEDELETEMAPWHERE = "mybatisEntityCacheDeleteMapWhere";

	public static final String CACHEDELETEMAPCLASS = "mybatisEntityCacheDeleteMapClass";

	public static final String CACHEDELETEMAPMETHODNAME = "mybatisEntityCacheDeleteMapMethodName";


	private Invocation invocation;

	public MybatisEntityCacheDelete(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(CACHEDELETEMAPWHERE);
		String methodName = (String) paramsMap.get(CACHEDELETEMAPMETHODNAME);
		Class clazz = (Class) paramsMap.get(CACHEDELETEMAPCLASS);
		CacheMapper cacheMapper = (CacheMapper) clazz.getAnnotation(CacheMapper.class);
		if (null == cacheMapper) {
			throw new Exception("the clazz " + clazz.getName() + " CacheMapper annotation can not be null");
		}
		Method method = clazz.getMethod(methodName, null);
		CacheDelete cacheDelete = method.getAnnotation(CacheDelete.class);
		if (null == cacheDelete) {
			throw new Exception("the method " + method.getName() + " CacheDelete annotation can not be null");
		}
		String sql = cacheDelete.sql();
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
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		return executor.update(builder.build(), params);
	}
}
