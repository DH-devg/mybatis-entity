package com.org.devg.mybatis.entity.proxy;

import com.org.devg.mybatis.entity.annos.TableField;
import com.org.devg.mybatis.entity.annos.TableName;
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
 * @Date 2021/1/23 10:54
 */
public class MybatisEntityUpdate implements MybatisEntityInvoke {

	public static final String UPDATEMYBATISENTITY = "updateMybatisEntity";

	private Invocation invocation;

	public MybatisEntityUpdate(Invocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Object invoke() throws Exception {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Executor executor = (Executor) invocation.getTarget();
		Object parameterArgs = invocation.getArgs()[1];
		Map<String, Object> paramsMap = (Map<String, Object>) parameterArgs;
		Object parameterObject = paramsMap.get(UPDATEMYBATISENTITY);
		Class clazz = parameterObject.getClass();
		TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
		String idName = null;
		String idCloumnName = null;
		Object idValue = null;
		Class idClassType = null;
		int i = 0;
		List<ParameterMapping> parameterMappings = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("UPDATE ").append(tableName.value()).append(" SET ");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			TableField fieldName = field.getAnnotation(TableField.class);
			if (fieldName != null) {
				String cloumnName = fieldName.value();
				Object result = MybatisEntityProxy.getFieldValue(clazz, field, parameterObject);
				if (null == result) {
					// 为空的字段不更新
					continue;
				}
				boolean isId = fieldName.isId();
				if (isId) {
					idName = field.getName();
					idCloumnName = cloumnName;
					idValue = result;
					idClassType = field.getType();
				} else {
					params.put(field.getName(), result);
					if (i > 0) {
						sqlBuffer.append(",");
					}
					sqlBuffer.append(cloumnName).append("=").append("?");
					parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), field.getName(), field.getType()).build());
					i++;
				}
			}
		}
		params.put(idName, idValue);
		parameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), idName, idClassType).build());
		String sql = sqlBuffer.toString();
		sql += (" WHERE " + idCloumnName + "= ?");
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, params);
		MappedStatement.Builder builder = getBuilder(ms, new MyBoundSql(newBoundSql));
		return executor.update(builder.build(), params);
	}
}
