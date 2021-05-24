package com.github.devgcoder.mybatis.entity.service;

import java.util.List;
import java.util.Map;

/**
 * @author duheng
 * @Date 2021/1/29 17:15
 */
public interface MybatisEntityService {

	<T> int insertEntity(T entity);

	<E> int insertEntityList(List<E> mybatisEntityList);

	<T> int updateEntity(T entity);

	<T> int updateEntityByMap(T entity, Map<String, Object> whereMap);

	<T> int deleteEntity(T entity);

	int deleteEntityByMap(Map<String, Object> deleteMap, Class clazz);

	List<Map<String, Object>> selectMapList(Map<String, Object> whereMap, Class clazz);

	Map<String, Object> selectOneMap(Object id, Class clazz);

	<E> List<E> selectEntityList(Map<String, Object> whereMap, Class clazz);

	<T> T selectOneEntity(Object id, Class clazz);

	List<Map<String, Object>> selectCacheMapList(Map<String, Object> whereMap, Class clazz, String methodName);

	Map<String, Object> selectCacheMap(Map<String, Object> whereMap, Class clazz, String methodName);

	<E> List<E> selectCacheList(Map<String, Object> whereMap, Class clazz, String methodName,Class<E> resultClazz);

	<T> T selectCacheOneEntity(Map<String, Object> whereMap, Class clazz, String methodName,Class<T> resultClazz);

	int insertCacheMap(Map<String, Object> whereMap, Class clazz, String methodName);

	int updateCacheMap(Map<String, Object> whereMap, Class clazz, String methodName);

	int deleteCacheMap(Map<String, Object> whereMap, Class clazz, String methodName);
}
