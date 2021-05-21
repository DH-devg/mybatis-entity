package com.github.devgcoder.mybatis.entity;

import com.github.devgcoder.mybatis.entity.annos.CacheSelect;
import com.github.devgcoder.mybatis.entity.utils.MybatisEntityUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author duheng
 * @Date 2021/5/21 17:23
 */
public class MybatisEntityCache implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(MybatisEntityCache.class);

	private static final String defaultBasePackage = "com.github.devgcoder";

	private static final String dotSql = ".sql";

	private static String theadCacheDir = "";

	public static final Map<String, String> sqlCacheMap = new ConcurrentHashMap();

	private MybatisEntityConfig mybatisEntityConfig;

	public MybatisEntityCache(MybatisEntityConfig mybatisEntityConfig) {
		this.mybatisEntityConfig = mybatisEntityConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		String basePackage = mybatisEntityConfig.getBasePackage();
		if (null == basePackage || basePackage.equals("")) {
			basePackage = defaultBasePackage;
		}
		String cacheDir = mybatisEntityConfig.getCacheDir();
		if (null == cacheDir || cacheDir.equals("")) {
			Properties props = System.getProperties();
			cacheDir = props.getProperty("user.dir");
		}
		if (!cacheDir.endsWith(File.separator)) {
			cacheDir += File.separator;
		}
		theadCacheDir = cacheDir;
		File fileDir = new File(cacheDir);
		if (!fileDir.exists() && !fileDir.isDirectory()) {
			fileDir.mkdir();
		}
		// 不使用默认的TypeFilter
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(CacheSelect.class));
		Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(basePackage);
		if (null != beanDefinitionSet && beanDefinitionSet.size() > 0) {
			for (BeanDefinition beanDefinition : beanDefinitionSet) {
				String beanClassName = beanDefinition.getBeanClassName();
				Class<?> clazz = Class.forName(beanClassName);
				String sql = clazz.getAnnotation(CacheSelect.class).sql();
				String sqlPath = (cacheDir + beanClassName + dotSql);
				File sqlFile = new File(sqlPath);
				if (sqlFile.exists()) {
					sqlFile.delete();
				}
				boolean createSqlFile = sqlFile.createNewFile();
				if (createSqlFile) {
					Writer out = new FileWriter(sqlFile);
					out.write(sql);
					out.close();
					sqlCacheMap.put(beanClassName, sql);
				}
			}
		}
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				File file = new File(theadCacheDir);
				File[] tempFileList = file.listFiles();
				if (null != tempFileList && tempFileList.length > 0) {
					for (File tempFile : tempFileList) {
						if (tempFile.isFile()) {
							String name = tempFile.getName();
							name = name.substring(0, name.lastIndexOf("."));
							String executeSql = MybatisEntityUtil.readFileContent(tempFile);
							sqlCacheMap.put(name, executeSql);
							logger.info("load name:{},sql:{}", name, executeSql);
						}
					}
				}
			}
		}, 0, 120, TimeUnit.SECONDS);
	}
}
