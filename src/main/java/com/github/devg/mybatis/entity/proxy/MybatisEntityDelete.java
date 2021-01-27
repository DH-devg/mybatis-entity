package com.github.devg.mybatis.entity.proxy;

import com.github.devg.mybatis.entity.annos.TableField;
import com.github.devg.mybatis.entity.annos.TableName;
import java.lang.reflect.Field;
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
 * @Date 2021/1/23 11:11
 */
public class MybatisEntityDelete implements MybatisEntityInvoke {

	public static final String DELETEMYBATISENTITY = "deleteMybatisEntity";

	private Invocation invocation;

	public MybatisEntityDelete(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Object parameterObject = paramsMap.get(DELETEMYBATISENTITY);
		Class clazz = parameterObject.getClass();
		TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("DELETE FROM ").append(tableName.value()).append(" WHERE ");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			TableField fieldName = field.getAnnotation(TableField.class);
			boolean isId = fieldName.isId();
			if (!isId) {
				continue;
			}
			if (fieldName != null) {
				String cloumnName = fieldName.value();
				Object result = MybatisEntityProxy.getFieldValue(clazz, field, parameterObject);
				sqlBuffer.append(cloumnName).append("=?");
				params.put(field.getName(), result);
				parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), field.getName(), field.getType()).build());
				break;
			}
		}
		String sql = sqlBuffer.toString();
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, params);
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		return executor.update(builder.build(), params);
	}
}
