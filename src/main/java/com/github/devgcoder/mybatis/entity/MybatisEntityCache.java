package com.github.devgcoder.mybatis.entity;

import com.github.devgcoder.mybatis.entity.annos.CacheMapper;
import com.github.devgcoder.mybatis.entity.annos.CacheSelect;
import com.github.devgcoder.mybatis.entity.utils.MybatisEntityUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 * @author duheng
 * @Date 2021/5/21 17:23
 */
public class MybatisEntityCache implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(MybatisEntityCache.class);

	private static final String defaultBasePackage = "com.github.devgcoder";

	private final String RESOURCE_PATTERN = "/**/*.class";

	private static final String dotSql = ".sql";

	public static final String underline = "_";

	private static String theadCacheDir = "";

	public static final Map<String, String> sqlCacheMap = new ConcurrentHashMap();

	public static final Map<String, Long> sqlModifiedTimeMap = new ConcurrentHashMap();

	private static final AtomicBoolean lock = new AtomicBoolean(false);

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
		File[] files = fileDir.listFiles();
		if (null != files && files.length > 0) {
			for (File theFile : files) {
				if (theFile.isFile()) {
					theFile.delete();
				}
			}
		}
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
				ClassUtils.convertClassNameToResourcePath(basePackage) + RESOURCE_PATTERN;
		Resource[] resources = resourcePatternResolver.getResources(pattern);
		//MetadataReader 的工厂类
		MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		for (Resource resource : resources) {
			//用于读取类信息
			MetadataReader reader = readerfactory.getMetadataReader(resource);
			//扫描到的class
			String classname = reader.getClassMetadata().getClassName();
			Class<?> clazz;
			try {
				clazz = Class.forName(classname);
			} catch (ClassNotFoundException ex) {
				logger.error("classname {} not found", classname);
				ex.printStackTrace();
				continue;
			}
			//判断是否有指定主解
			CacheMapper anno = clazz.getAnnotation(CacheMapper.class);
			if (anno == null) {
				logger.warn("classname {} has not anno CacheMapper", classname);
				continue;
			}
			String clazzName = clazz.getName();
			Method[] methods = clazz.getMethods();
			if (null == methods || methods.length < 0) {
				logger.warn("classname {} has not method", classname);
				continue;
			}
			for (Method method : methods) {
				CacheSelect cacheSelect = method.getAnnotation(CacheSelect.class);
				if (null == cacheSelect) {
					continue;
				}
				String methodName = method.getName();
				String sqlName = clazzName + MybatisEntityCache.underline + methodName;
				String sqlPath = (cacheDir + sqlName + dotSql);
				String sql = cacheSelect.sql();
				dealCreateFileAndWriteSql(sqlName, sqlPath, sql);
			}
		}
		/*// 不使用默认的TypeFilter
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(CacheSelect.class));
		Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(basePackage);
		if (null != beanDefinitionSet && beanDefinitionSet.size() > 0) {
			for (BeanDefinition beanDefinition : beanDefinitionSet) {
				String beanClassName = beanDefinition.getBeanClassName();
				Class<?> clazz = Class.forName(beanClassName);
				String sql = clazz.getAnnotation(CacheSelect.class).sql();
				String sqlPath = (cacheDir + beanClassName + dotSql);

			}
		}*/
		dealSchedule();
	}

	private boolean dealCreateFileAndWriteSql(String sqlName, String sqlPath, String sql) {
		try {
			File sqlFile = new File(sqlPath);
			if (sqlFile.exists()) {
				sqlFile.delete();
			}
			boolean createSqlFile = sqlFile.createNewFile();
			if (createSqlFile) {
				Writer out = new FileWriter(sqlFile);
				out.write(sql);
				out.close();
				sqlCacheMap.put(sqlName, sql);
			}
			return true;
		} catch (Exception ex) {
			logger.error("dealCreateFileAndWriteSql error", ex);
			ex.printStackTrace();
		}
		return false;

	}

	private void dealSchedule() {
		Integer cacheSecond = mybatisEntityConfig.getCacheSecond();
		if (null == cacheSecond || cacheSecond <= 0) {
			cacheSecond = 120;
		}
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					if (lock.compareAndSet(false, true)) {
						File file = new File(theadCacheDir);
						File[] tempFileList = file.listFiles();
						if (null != tempFileList && tempFileList.length > 0) {
							for (File tempFile : tempFileList) {
								if (tempFile.isFile()) {
									String name = tempFile.getName();
									name = name.substring(0, name.lastIndexOf("."));
									long modifiedTime = tempFile.lastModified();
									Long lastModifiedTime = sqlModifiedTimeMap.get(name);
									if (null != lastModifiedTime && lastModifiedTime >= modifiedTime) {
										continue;
									}
									String executeSql = MybatisEntityUtil.readFileContent(tempFile);
									sqlCacheMap.put(name, executeSql);
									sqlModifiedTimeMap.put(name, modifiedTime);
									logger.info("load name:{},sql:{}", name, executeSql);
								}
							}
						}
						lock.set(false);
					} else {
						logger.warn("dealSchedule is running");
					}
				} catch (Exception ex) {
					logger.error("dealSchedule error", ex);
					ex.printStackTrace();
					lock.set(false);
				}
			}
		}, 0, cacheSecond, TimeUnit.SECONDS);
	}
}
