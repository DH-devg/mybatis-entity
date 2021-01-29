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
}
