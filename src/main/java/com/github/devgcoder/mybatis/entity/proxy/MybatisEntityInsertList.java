package com.github.devgcoder.mybatis.entity.proxy;

import com.github.devgcoder.mybatis.entity.annos.TableField;
import com.github.devgcoder.mybatis.entity.annos.TableName;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;

/**
 * @author duheng
 * @Date 2021/1/23 18:23
 */
public class MybatisEntityInsertList implements MybatisEntityInvoke {

	public static final String MYBATISENTITYLIST = "mybatisEntityList";

	private Invocation invocation;

	public MybatisEntityInsertList(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		List<Object> mybatisEntityList = (List<Object>) paramsMap.get(MYBATISENTITYLIST);
		if (null == mybatisEntityList || mybatisEntityList.size() <= 0) {
			throw new Exception("mybatisEntityList can not be empty");
		}
		int i = 0;
		int j = 0;
		boolean useGeneratedKeys = false;
		String keyProperty = null;
		String idName = null;
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> additionalParametersMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		List<Map<String, Object>> collection = new ArrayList<>();
		Object mybatisEntity = mybatisEntityList.get(0);
		Class clazz = mybatisEntity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("INSERT INTO ").append(tableName.value()).append("(");
		for (Field field : fields) {
			TableField fieldName = field.getAnnotation(TableField.class);
			if (fieldName == null) {
				continue;
			}
			boolean isId = fieldName.isId();
			if (isId) {
				if (fieldName.useGeneratedKeys()) {
					useGeneratedKeys = true;
					keyProperty = field.getName();
				}
				idName = field.getName();
			}
			if (isId) {
				// id can not be insert
				continue;
			}
			if (i > 0) {
				sqlBuffer.append(",");
			}
			sqlBuffer.append(fieldName.value());
			i++;
		}
		sqlBuffer.append(") Values ");
		for (Object object : mybatisEntityList) {
			Map<String, Object> paramData = new HashMap<>();
			Class objClazz = object.getClass();
			Field[] objFields = objClazz.getDeclaredFields();
			if (j > 0) {
				sqlBuffer.append(",");
			}
			sqlBuffer.append("(");
			int k = 0;
			for (Field field : objFields) {
				TableField fieldName = field.getAnnotation(TableField.class);
				if (fieldName == null) {
					continue;
				}
				if (fieldName.isId()) {
					// id can not be insert
					continue;
				}
				Object result = MybatisEntityProxy.getFieldValue(objClazz, field, object);
				paramData.put(field.getName(), result);
				if (k > 0) {
					sqlBuffer.append(",");
				}
				sqlBuffer.append("?");
				k++;
				String property = ForEachSqlNode.ITEM_PREFIX + "_item_" + j + "." + field.getName();
				parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), property, field.getType()).build());
			}
			sqlBuffer.append(")");
			collection.add(paramData);
			additionalParametersMap.put(ForEachSqlNode.ITEM_PREFIX + "_item_" + j, paramData);
			j++;
		}
		params.put("collection", collection);
		params.put("list", collection);
		Configuration configuration = ms.getConfiguration();
		if (useGeneratedKeys && null != keyProperty) {
			configuration.setUseGeneratedKeys(true);
		}
		String sql = sqlBuffer.toString();
		BoundSql newBoundSql = new BoundSql(configuration, sql, parameterMappings, params);
		newBoundSql.setAdditionalParameter(DynamicContext.PARAMETER_OBJECT_KEY, params);
		if (null != additionalParametersMap && !additionalParametersMap.isEmpty()) {
			for (String additionalParameterKey : additionalParametersMap.keySet()) {
				newBoundSql.setAdditionalParameter(additionalParameterKey, additionalParametersMap.get(additionalParameterKey));
			}
		}
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		if (null != keyProperty) {
			builder.keyProperty(keyProperty);
			KeyGenerator keyGenerator = new Jdbc3KeyGenerator();
			builder.keyGenerator(keyGenerator);
		}
		int result = executor.update(builder.build(), params);
		if (null == idName || !useGeneratedKeys) {
			return result;
		}
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) params.get("list");
		int idNum = 0;
		for (Object object : mybatisEntityList) {
			Map<String, Object> resultMap = resultList.get(idNum);
			Class objClazz = object.getClass();
			Field idField = objClazz.getDeclaredField(idName);
			idField.setAccessible(true);
			idField.set(object, resultMap.get(idName));
			idNum++;
		}
		return result;
	}
}
