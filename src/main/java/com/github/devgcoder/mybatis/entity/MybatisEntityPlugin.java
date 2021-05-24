package com.github.devgcoder.mybatis.entity;

import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheDelete;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheInsert;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheUpdate;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityDeleteByMap;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityInsertList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheSelectMapList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntitySelectMapList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityUpdateByMap;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityDelete;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityInsert;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityProxy;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityUpdate;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author duheng
 * @Date 2021/1/22 20:50
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
				CacheKey.class, BoundSql.class})
})
public class MybatisEntityPlugin implements Interceptor {

	private Properties properties;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object parameterObject = null;
		if (invocation.getArgs().length > 1) {
			parameterObject = invocation.getArgs()[1];
		}
		if (parameterObject instanceof Map) {
			Map<String, Object> paramsMap = (Map<String, Object>) parameterObject;
			if (paramsMap.containsKey(MybatisEntityUpdateByMap.WHEREMAP) && paramsMap.containsKey(MybatisEntityUpdateByMap.ENTITY)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityUpdate.UPDATEMYBATISENTITY)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityInsert.INSERTMYBATISENTITY)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityInsertList.MYBATISENTITYLIST)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityDeleteByMap.DELETEMAP) && paramsMap.containsKey(MybatisEntityDeleteByMap.DELETECLASS)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityDelete.DELETEMYBATISENTITY)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntitySelectMapList.SELECTMAPWHERE) && paramsMap.containsKey(MybatisEntitySelectMapList.SELECTMAPCLASS)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityCacheSelectMapList.CACHESELECTMAPWHERE) && paramsMap
					.containsKey(MybatisEntityCacheSelectMapList.CACHESELECTMAPCLASS) && paramsMap
					.containsKey(MybatisEntityCacheSelectMapList.CACHESELECTMAPMETHODNAME)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityCacheInsert.CACHEINSERTMAPWHERE) && paramsMap
					.containsKey(MybatisEntityCacheInsert.CACHEINSERTMAPCLASS) && paramsMap
					.containsKey(MybatisEntityCacheInsert.CACHEINSERTMAPMETHODNAME)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityCacheUpdate.CACHEUPDATEMAPWHERE) && paramsMap
					.containsKey(MybatisEntityCacheUpdate.CACHEUPDATEMAPCLASS) && paramsMap
					.containsKey(MybatisEntityCacheUpdate.CACHEUPDATEMAPMETHODNAME)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else if (paramsMap.containsKey(MybatisEntityCacheDelete.CACHEDELETEMAPWHERE) && paramsMap
					.containsKey(MybatisEntityCacheDelete.CACHEDELETEMAPCLASS) && paramsMap
					.containsKey(MybatisEntityCacheDelete.CACHEDELETEMAPMETHODNAME)) {
				MybatisEntityProxy mybatisEntityInvoke = new MybatisEntityProxy(invocation);
				return mybatisEntityInvoke.invoke();
			} else {
				return invocation.proceed();
			}
		} else {
			return invocation.proceed();
		}
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
