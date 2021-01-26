package com.org.devg.mybatis.entity.proxy;

import com.org.devg.mybatis.entity.annos.TableField;
import com.org.devg.mybatis.entity.annos.TableName;
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
import org.apache.ibatis.session.Configuration;

/**
 * @author duheng
 * @Date 2021/1/23 10:43
 */
public class MybatisEntityInsert implements MybatisEntityInvoke {

	public static final String INSERTMYBATISENTITY = "insertMybatisEntity";

	private Invocation invocation;

	public MybatisEntityInsert(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Object parameterObject = paramsMap.get(INSERTMYBATISENTITY);
		Class clazz = parameterObject.getClass();
		TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
		int i = 0;
		boolean useGeneratedKeys = false;
		String keyProperty = null;
		String idName = null;
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		StringBuffer paramBuffer = new StringBuffer(" Values (");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("INSERT INTO ").append(tableName.value()).append("(");
		Field[] fields = clazz.getDeclaredFields();
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
			String cloumnName = fieldName.value();
			Object result = MybatisEntityProxy.getFieldValue(clazz, field, parameterObject);
			boolean noneNotInsert = fieldName.noneNotInsert();
			if (null == result && noneNotInsert) {
				continue;
			}
			if (i > 0) {
				sqlBuffer.append(",");
				paramBuffer.append(",");
			}
			sqlBuffer.append(cloumnName);
			paramBuffer.append("?");
			params.put(field.getName(), result);
			parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), field.getName(), field.getType()).build());
			i++;
		}
		sqlBuffer.append(")");
		paramBuffer.append(")");
		String sql = sqlBuffer.append(paramBuffer).toString();
		Configuration configuration = ms.getConfiguration();
		if (useGeneratedKeys && null != keyProperty) {
			configuration.setUseGeneratedKeys(true);
		}
		BoundSql newBoundSql = new BoundSql(configuration, sql, parameterMappings, params);
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
		Long idValue = params.get(idName) == null ? null : Long.valueOf(params.get(idName).toString());
		Field idField = clazz.getDeclaredField(idName);
		idField.setAccessible(true);
		idField.set(parameterObject, idValue);
		return result;
	}
}
