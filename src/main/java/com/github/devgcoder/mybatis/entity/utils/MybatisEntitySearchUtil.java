package com.github.devgcoder.mybatis.entity.utils;

import java.util.HashMap;
import java.util.Map;

public class MybatisEntitySearchUtil {

  // 解析用
  public static final String prefixString = "@{";
  public static final String suffixString = "}";
  public static final String ifPrefixString = "$[";
  public static final String ifSuffixString = "]$";
  public static final String targetPrefixString = "#{";
  public static final String lastSqlValue = "lastSqlValue";

  public static String getSqlByParam(String sql, String key, String value) {
    if (null == value || value.trim().equals("")) {
      return sql;
    }
    String word = prefixString + key + suffixString;
    return sql.replace(word, fillQuotation(value));
  }

  public static String getTargetSqlByParam(String sql, String key, String value) {
    String word = targetPrefixString + key + suffixString;
    if (null == value) {
      return sql.replace(word, "null");
    }
    if (value.trim().equals("")) {
      return sql.replace(word, "''");
    }
    return sql.replace(word, fillQuotation(value));
  }

  public static String fillQuotation(String value) {
    return value;
  }

  public static String removeEmptyValue(String sql) {
    if (null == sql || sql.trim().equals("")) {
      return sql;
    }
    String tempSql = sql;
    int index = 0;
    while (index >= 0) {
      tempSql = tempSql.substring(index + 1);
      index = tempSql.indexOf(prefixString);
      if (index >= 0) {
        String startSql = tempSql.substring(0, index);
        int startPosition = startSql.lastIndexOf(ifPrefixString);
        int endPosition = tempSql.indexOf(ifSuffixString, startPosition + 1);
        String removeString = tempSql.substring(startPosition, endPosition + 1);
        sql = sql.replace(removeString, "");
      }
    }
    sql = sql.replace(ifPrefixString, "").replace(ifSuffixString, "").replace("$", "");
    return sql;
  }

  public static String mapToString(Map map, String string) {
    return map == null ? "" : map.get(string) == null ? "" : String.valueOf(map.get(string));
  }

  public static String mapToStringNull(Map map, String string) {
    return map == null ? null : map.get(string) == null ? null : String.valueOf(map.get(string));
  }

  public static Map<String, String> getTargetKeys(Map<String, Object> queryMap, String targetId) {
    Map<String, String> resultMap = new HashMap<>();
    if (null == queryMap || queryMap.isEmpty()) {
      return resultMap;
    }
    if (null == targetId || targetId.trim().equals("")) {
      return resultMap;
    }
    String[] targetIds = targetId.split(",");
    if (null == targetIds || targetIds.length <= 0) {
      return resultMap;
    }
    for (String key : targetIds) {
      String value = queryMap.get(key) == null ? null : queryMap.get(key).toString();
      resultMap.put(key, value);
    }
    return resultMap;
  }

  public static String getTargetKey(Map<String, Object> queryMap, String targetId, String defaultValue) {
    return queryMap.get(targetId) == null ? defaultValue : queryMap.get(targetId).toString();
  }

}
