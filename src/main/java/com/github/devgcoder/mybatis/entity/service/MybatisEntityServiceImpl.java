package com.github.devgcoder.mybatis.entity.service;

import com.github.devgcoder.mybatis.entity.mapper.MybatisEntityMapper;
import com.github.devgcoder.mybatis.entity.utils.MybatisEntityUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author duheng
 * @Date 2021/1/29 17:15
 */
public class MybatisEntityServiceImpl implements MybatisEntityService {

	@Autowired
	private MybatisEntityMapper mybatisEntityMapper;

	@Override
	public <T> int insertEntity(T entity) {
		return mybatisEntityMapper.insertEntity(entity);
	}

	@Override
	public <E> int insertEntityList(List<E> mybatisEntityList) {
		return mybatisEntityMapper.insertEntityList(mybatisEntityList);
	}

	@Override
	public <T> int updateEntity(T entity) {
		return mybatisEntityMapper.updateEntity(entity);
	}

	@Override
	public <T> int updateEntityByMap(T entity, Map<String, Object> whereMap) {
		return mybatisEntityMapper.updateEntityByMap(entity, whereMap);
	}

	@Override
	public <T> int deleteEntity(T entity) {
		return mybatisEntityMapper.deleteEntity(entity);
	}

	@Override
	public int deleteEntityByMap(Map<String, Object> deleteMap, Class clazz) {
		return mybatisEntityMapper.deleteEntityByMap(deleteMap, clazz);
	}

	@Override
	public List<Map<String, Object>> selectMapList(Map<String, Object> whereMap, Class clazz) {
		return mybatisEntityMapper.selectMapList(whereMap, clazz);
	}

	@Override
	public Map<String, Object> selectOneMap(Object id, Class clazz) {
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put(MybatisEntityUtil.getIdCloumnName(clazz), id);
		List<Map<String, Object>> list = mybatisEntityMapper.selectMapList(whereMap, clazz);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <E> List<E> selectEntityList(Map<String, Object> whereMap, Class clazz) {
		List<Map<String, Object>> list = mybatisEntityMapper.selectMapList(whereMap, clazz);
		return MybatisEntityUtil.parseObjctList(list, clazz);
	}

	@Override
	public <T> T selectOneEntity(Object id, Class clazz) {
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put(MybatisEntityUtil.getIdCloumnName(clazz), id);
		List<Map<String, Object>> list = mybatisEntityMapper.selectMapList(whereMap, clazz);
		if (null != list && list.size() > 0) {
			List<T> entityList = MybatisEntityUtil.parseObjctList(list, clazz);
			return entityList.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> selectCacheMapList(Map<String, Object> whereMap, Class clazz, String methodName) {
		return mybatisEntityMapper.selectCacheMapList(whereMap, clazz, methodName);
	}

	@Override
	public Map<String, Object> selectCacheMap(Map<String, Object> whereMap, Class clazz, String methodName) {
		List<Map<String, Object>> list = mybatisEntityMapper.selectCacheMapList(whereMap, clazz, methodName);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <E> List<E> selectCacheList(Map<String, Object> whereMap, Class clazz, String methodName, Class<E> resultClazz) {
		List<Map<String, Object>> list = mybatisEntityMapper.selectCacheMapList(whereMap, clazz, methodName);
		return MybatisEntityUtil.parseObjctList(list, resultClazz);
	}

	@Override
	public <T> T selectCacheOneEntity(Map<String, Object> whereMap, Class clazz, String methodName, Class<T> resultClazz) {
		List<Map<String, Object>> list = mybatisEntityMapper.selectCacheMapList(whereMap, clazz, methodName);
		if (null != list && list.size() > 0) {
			Map<String, Object> resultMap = list.get(0);
			T result = MybatisEntityUtil.parseObject(resultMap, resultClazz);
			return result;
		}
		return null;
	}

	@Override
	public int insertCacheMap(Map<String, Object> whereMap, Class clazz, String methodName) {
		return mybatisEntityMapper.insertCacheMap(whereMap, clazz, methodName);
	}

	@Override
	public int updateCacheMap(Map<String, Object> whereMap, Class clazz, String methodName) {
		return mybatisEntityMapper.updateCacheMap(whereMap, clazz, methodName);
	}

	@Override
	public int deleteCacheMap(Map<String, Object> whereMap, Class clazz, String methodName) {
		return mybatisEntityMapper.deleteCacheMap(whereMap, clazz, methodName);
	}
}
