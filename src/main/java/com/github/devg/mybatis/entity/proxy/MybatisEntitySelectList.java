package com.github.devg.mybatis.entity.proxy;

import com.github.devg.mybatis.entity.annos.TableField;
import com.github.devg.mybatis.entity.annos.TableName;
import java.lang.reflect.Field;
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
 * @Date 2021/1/25 17:42
 */
public class MybatisEntitySelectList implements MybatisEntityInvoke {

	public static final String SELECTWHERE = "mybatisEntitySelectWhere";

	public static final String SELECTCLASS = "mybatisEntitySelectClass";

	private Invocation invocation;

	public MybatisEntitySelectList(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Map<String, Object> paramWhereMap = (Map<String, Object>) paramsMap.get(SELECTWHERE);
		Class clazz = (Class) paramsMap.get(SELECTCLASS);
		TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
		int i = 0;
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT ");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			TableField fieldName = field.getAnnotation(TableField.class);
			if (fieldName != null) {
				String cloumnName = fieldName.value();
				if (i > 0) {
					sqlBuffer.append(",");
				}
				sqlBuffer.append(cloumnName).append(" AS ").append(field.getName());
				i++;
			}
		}
		sqlBuffer.append(" FROM ").append(tableName.value());
		if (null != paramWhereMap && !paramWhereMap.isEmpty()) {
			sqlBuffer.append(" WHERE ");
			int j = 0;
			for (String key : paramWhereMap.keySet()) {
				Object value = paramWhereMap.get(key);
				if (j > 0) {
					sqlBuffer.append(" AND ");
				}
				sqlBuffer.append(key).append("=?");
				parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), key, value.getClass()).build());
				params.put(key, value);
				j++;
			}
		}
		String sql = sqlBuffer.toString();
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, params);
		CacheKey key = executor.createCacheKey(ms, params, RowBounds.DEFAULT, newBoundSql);
		List<Map<String, Object>> list = executor.query(ms, params, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, key, newBoundSql);
		return list;
	}
}
