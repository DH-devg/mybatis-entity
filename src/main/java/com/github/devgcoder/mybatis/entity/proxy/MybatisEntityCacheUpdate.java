package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.MybatisEntityCache;
import com.github.devgcoder.mybatis.entity.annos.CacheMapper;
import com.github.devgcoder.mybatis.entity.annos.CacheUpdate;
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
 * @Date 2021/5/24 15:16
 */
public class MybatisEntityCacheUpdate implements MybatisEntityInvoke {

	public static final String CACHEUPDATEMAPWHERE = "mybatisEntityCacheUpdateMapWhere";

	public static final String CACHEUPDATEMAPCLASS = "mybatisEntityCacheUpdateMapClass";

	public static final String CACHEUPDATEMAPMETHODNAME = "mybatisEntityCacheUpdateMapMethodName";


	private Invocation invocation;

	public MybatisEntityCacheUpdate(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {

		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(CACHEUPDATEMAPWHERE);
		String methodName = (String) paramsMap.get(CACHEUPDATEMAPMETHODNAME);
		Class clazz = (Class) paramsMap.get(CACHEUPDATEMAPCLASS);
		CacheMapper cacheMapper = (CacheMapper) clazz.getAnnotation(CacheMapper.class);
		if (null == cacheMapper) {
			throw new Exception("the clazz " + clazz.getName() + " CacheMapper annotation can not be null");
		}
		Method method = clazz.getMethod(methodName, null);
		CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
		if (null == cacheUpdate) {
			throw new Exception("the method " + method.getName() + " CacheUpdate annotation can not be null");
		}
		String sql = cacheUpdate.sql();
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
