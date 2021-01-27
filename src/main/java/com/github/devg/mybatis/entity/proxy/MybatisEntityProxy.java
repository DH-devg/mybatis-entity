package com.github.devg.mybatis.entity.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author duheng
 * @Date 2021/1/23 10:41
 */
public class MybatisEntityProxy {

	private static final Logger logger = LoggerFactory.getLogger(MybatisEntityProxy.class);

	private Invocation invocation;

	public MybatisEntityProxy(Invocation invocation) {
		this.invocation = invocation;
	}

	public Object invoke() {
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		SqlCommandType sqlCommandType = ms.getSqlCommandType();
		if (SqlCommandType.INSERT.equals(sqlCommandType)) {
			Object parameterObject = invocation.getArgs()[1];
			if (parameterObject instanceof Map) {
				Map<String, Object> paramsMap = (Map<String, Object>) parameterObject;
				if (paramsMap.containsKey(MybatisEntityInsertList.MYBATISENTITYLIST)) {
					try {
						MybatisEntityInsertList mybatisEntityInsertList = new MybatisEntityInsertList(invocation);
						return mybatisEntityInsertList.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity insertList.  Cause: ", ex);
					}
				} else if (paramsMap.containsKey(MybatisEntityInsert.INSERTMYBATISENTITY)) {
					try {
						MybatisEntityInsert mybatisEntityInsert = new MybatisEntityInsert(invocation);
						return mybatisEntityInsert.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity insert.  Cause: ", ex);
					}
				}
			}
		} else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
			Object parameterObject = invocation.getArgs()[1];
			if (parameterObject instanceof Map) {
				Map<String, Object> paramsMap = (Map<String, Object>) parameterObject;
				if (paramsMap.containsKey(MybatisEntityUpdateByMap.WHEREMAP) && paramsMap.containsKey(MybatisEntityUpdateByMap.ENTITY)) {
					try {
						MybatisEntityUpdateByMap mybatisEntityUpdateByMap = new MybatisEntityUpdateByMap(invocation);
						return mybatisEntityUpdateByMap.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity updateByMap.  Cause: ", ex);
					}
				} else if (paramsMap.containsKey(MybatisEntityUpdate.UPDATEMYBATISENTITY)) {
					try {
						MybatisEntityUpdate mybatisEntityUpdate = new MybatisEntityUpdate(invocation);
						return mybatisEntityUpdate.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity update.  Cause: ", ex);
					}
				}
			}
		} else if (SqlCommandType.DELETE.equals(sqlCommandType)) {
			Object parameterObject = invocation.getArgs()[1];
			if (parameterObject instanceof Map) {
				Map<String, Object> paramsMap = (Map<String, Object>) parameterObject;
				if (paramsMap.containsKey(MybatisEntityDeleteByMap.DELETEMAP)) {
					try {
						MybatisEntityDeleteByMap mybatisEntityDeleteByMap = new MybatisEntityDeleteByMap(invocation);
						return mybatisEntityDeleteByMap.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity deleteByMap.  Cause: ", ex);
					}
				}
				if (paramsMap.containsKey(MybatisEntityDelete.DELETEMYBATISENTITY)) {
					try {
						MybatisEntityDelete mybatisEntityDelete = new MybatisEntityDelete(invocation);
						return mybatisEntityDelete.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity delete.  Cause: ", ex);
					}
				}
			}

		} else if (SqlCommandType.SELECT.equals(sqlCommandType)) {
			Object parameterObject = invocation.getArgs()[1];
			if (parameterObject instanceof Map) {
				Map<String, Object> paramsMap = (Map<String, Object>) parameterObject;
				if (paramsMap.containsKey(MybatisEntitySelectList.SELECTWHERE) && paramsMap.containsKey(MybatisEntitySelectList.SELECTCLASS)) {
					try {
						MybatisEntitySelectList mybatisEntitySelectList = new MybatisEntitySelectList(invocation);
						return mybatisEntitySelectList.invoke();
					} catch (Exception ex) {
						throw ExceptionFactory.wrapException("Error entity deleteByMap.  Cause: ", ex);
					}
				}
			}
		}
		return null;
	}


	public static String getMethodName(Field field) {
		String fieldNameString = field.getName();
		char[] ch = fieldNameString.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		String theMethodName = "get" + new String(ch);
		return theMethodName;
	}

	public static Object getFieldValue(Class clazz, Field field, Object parameterObject) throws Exception {
		Object result = null;
		try {
			Method method = clazz.getDeclaredMethod(MybatisEntityProxy.getMethodName(field), new Class[0]);
			result = method.invoke(parameterObject, new Object[]{});
		} catch (Exception e) {
			logger.error("get field value error", e);
			e.printStackTrace();
			throw new Exception(e);
		}
		return result;
	}
}
