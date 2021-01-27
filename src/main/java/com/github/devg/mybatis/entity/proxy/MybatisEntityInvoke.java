package com.github.devg.mybatis.entity.proxy;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author duheng
 * @Date 2021/1/23 10:39
 */
public interface MybatisEntityInvoke {

	Object invoke() throws Exception;

	default MappedStatement.Builder getBuilder(MappedStatement ms, SqlSource sqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
			builder.keyProperty(ms.getKeyProperties()[0]);
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		return builder;
	}

	static class MyBoundSql implements SqlSource {

		private BoundSql boundSql;

		public MyBoundSql(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		@Override
		public BoundSql getBoundSql(Object o) {
			return boundSql;
		}
	}

}
