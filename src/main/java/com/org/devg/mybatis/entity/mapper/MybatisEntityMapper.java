package com.org.devg.mybatis.entity.mapper;

import com.org.devg.mybatis.entity.proxy.MybatisEntityDelete;
import com.org.devg.mybatis.entity.proxy.MybatisEntityDeleteByMap;
import com.org.devg.mybatis.entity.proxy.MybatisEntityInsert;
import com.org.devg.mybatis.entity.proxy.MybatisEntityInsertList;
import com.org.devg.mybatis.entity.proxy.MybatisEntitySelectList;
import com.org.devg.mybatis.entity.proxy.MybatisEntityUpdate;
import com.org.devg.mybatis.entity.proxy.MybatisEntityUpdateByMap;
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

	int deleteEntityByMap(@Param(MybatisEntityDeleteByMap.DELETEMAP) Map<String, Object> deleteMap);

	List<Map<String, Object>> selectMapList(@Param(MybatisEntitySelectList.SELECTWHERE) Map<String, Object> whereMap,
			@Param(MybatisEntitySelectList.SELECTCLASS) Class clazz);

}
