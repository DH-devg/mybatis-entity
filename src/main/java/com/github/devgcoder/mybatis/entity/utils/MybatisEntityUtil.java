package com.github.devgcoder.mybatis.entity.utils;

import com.github.devgcoder.mybatis.entity.annos.TableField;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author duheng
 * @Date 2021/1/28 10:53
 */
public class MybatisEntityUtil {

	public static <E> List<E> parseObjctList(List<Map<String, Object>> list, Class<E> sourceType) {
		List<E> resultList = new ArrayList<>();
		if (null == list || list.size() <= 0) {
			return resultList;
		}
		for (Map<String, Object> map : list) {
			resultList.add(parseObject(map, sourceType));
		}
		return resultList;
	}

	public static <E> E parseObject(Map<String, Object> map, Class<E> sourceType) {
		try {
			E result = sourceType.newInstance();
			Field[] fields = sourceType.getDeclaredFields();
			for (Field field : fields) {
				Object value = map.get(field.getName());
				Field entityField = result.getClass().getDeclaredField(field.getName());
				entityField.setAccessible(true);
				entityField.set(result, value);
			}
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String getIdCloumnName(Class clazz) {
		String idCloumnName = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			TableField fieldName = field.getAnnotation(TableField.class);
			if (fieldName == null) {
				continue;
			}
			boolean isId = fieldName.isId();
			if (isId) {
				idCloumnName = field.getName();
				break;
			}
		}
		return idCloumnName;
	}

}
