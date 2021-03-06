package com.github.devgcoder.mybatis.entity.mapper;

import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheDelete;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheInsert;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheUpdate;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityDeleteByMap;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityInsertList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityCacheSelectMapList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntitySelectMapList;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityUpdateByMap;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityDelete;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityInsert;
import com.github.devgcoder.mybatis.entity.proxy.MybatisEntityUpdate;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author duheng
 * @Date 2021/1/22 20:40
 */
@Mapper
public interface MybatisEntityMapper {

	<T> int insertEntity(@Param(MybatisEntityInsert.INSERTMYBATISENTITY) T entity);

	<E> int insertEntityList(@Param(MybatisEntityInsertList.MYBATISENTITYLIST) List<E> mybatisEntityList);

	<T> int updateEntity(@Param(MybatisEntityUpdate.UPDATEMYBATISENTITY) T entity);

	<T> int updateEntityByMap(@Param(MybatisEntityUpdateByMap.ENTITY) T entity, @Param(MybatisEntityUpdateByMap.WHEREMAP) Map<String, Object> whereMap);

	<T> int deleteEntity(@Param(MybatisEntityDelete.DELETEMYBATISENTITY) T entity);

	int deleteEntityByMap(@Param(MybatisEntityDeleteByMap.DELETEMAP) Map<String, Object> deleteMap,
			@Param(MybatisEntityDeleteByMap.DELETECLASS) Class clazz);

	List<Map<String, Object>> selectMapList(@Param(MybatisEntitySelectMapList.SELECTMAPWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntitySelectMapList.SELECTMAPCLASS) Class clazz);

	List<Map<String, Object>> selectCacheMapList(@Param(MybatisEntityCacheSelectMapList.CACHESELECTMAPWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntityCacheSelectMapList.CACHESELECTMAPCLASS) Class clazz,
			@Param(MybatisEntityCacheSelectMapList.CACHESELECTMAPMETHODNAME) String methodName);


	int insertCacheMap(@Param(MybatisEntityCacheInsert.CACHEINSERTMAPWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntityCacheInsert.CACHEINSERTMAPCLASS) Class clazz, @Param(MybatisEntityCacheInsert.CACHEINSERTMAPMETHODNAME) String methodName);

	int updateCacheMap(@Param(MybatisEntityCacheUpdate.CACHEUPDATEMAPWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntityCacheUpdate.CACHEUPDATEMAPCLASS) Class clazz, @Param(MybatisEntityCacheUpdate.CACHEUPDATEMAPMETHODNAME) String methodName);

	int deleteCacheMap(@Param(MybatisEntityCacheDelete.CACHEDELETEMAPWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntityCacheDelete.CACHEDELETEMAPCLASS) Class clazz, @Param(MybatisEntityCacheDelete.CACHEDELETEMAPMETHODNAME) String methodName);
}
