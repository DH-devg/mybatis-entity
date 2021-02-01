# MyBatis Entity
# 简介
    MyBatis Entity 是一个基于MyBatis开发的实体对象操作插件，简化CURD的开发，不影响原有MyBatis的使用，提升开发效率。

# 快速开始    
    开始前需先熟悉 springboot，mybatis。   
## 初始化工程
    创建一个空的 Spring Boot 工程 
    可参考完整版本demo：  `https://github.com/devgcoder/mybatis-entity-springboot-demo`
##      添加依赖(pom.xml):         
         
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.1</version>
      </dependency>
      <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.19</version>
      </dependency>
      <dependency>
        <groupId>com.github.devgcoder</groupId>
        <artifactId>mybatis-entity</artifactId>
        <version>1.0.5</version>
      </dependency>
    </dependencies>

## 配置
### application.yml 配置
         
    spring:
      datasource:
        username: root
        password: root
        url: jdbc:mysql://localhost:3306/mybatis-entity?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.cj.jdbc.Driver

### springboot 启动类配置
         
    增加 @MapperScan(basePackages = {"com.github.devgcoder.mybatis.entity.mapper"})

    @MapperScan(basePackages = {"com.github.devgcoder.mybatis.entity.mapper"})
    @SpringBootApplication
    public class Applicatioin {
    public static void main(String[] args) {
      SpringApplication.run(Applicatioin.class, args);
     }
    }

## 代码编写
### 注解介绍
   
     // 类注解
     @Inherited
     @Documented
     @Target({ElementType.TYPE})
     @Retention(RetentionPolicy.RUNTIME)
     public @interface TableName {
       // 配置为表名
       String value() default "";
     }
     
     // 字段注解
     @Inherited
     @Documented
     @Target({ElementType.FIELD})
     @Retention(RetentionPolicy.RUNTIME)
     public @interface TableField {
       // 对应表字段
       String value() default "";  
       // 是否为主键
       boolean isId() default false; 
       //设置为true，自增主键添加数据时返回主键值
       boolean useGeneratedKeys() default false; 
       // 字段为空时，不会插入表
       boolean noneNotInsert() default false;
     }


### 实体类编写
     
     @TableName("user")
     public class User {
     
     // 主键建议用Long
     @TableField(value = "id", isId = true, noneNotInsert = true, useGeneratedKeys = true)
     private Long id;
     @TableField(value = "name")
     private String name;
     @TableField(value = "age")
     private Integer age;
     
     public Long getId() {
        return id;
     }
     public void setId(Long id) {
        this.id = id;
     }
     public String getName() {
        return name;
     }
     public void setName(String name) {
        this.name = name;
     }
     public Integer getAge() {
        return age;
     }
     public void setAge(Integer age) {
        this.age = age;
     }
     }

### 测试类编写
#### 用MybatisEntityMapper的地方用MybatisEntityService 同样可以
#### 添加实体数据（insertEntity）
     
     @RunWith(SpringRunner.class)
     @SpringBootTest
     public class ApplicatioinTest {
     
     @Autowired
      private MybatisEntityMapper mybatisEntityMapper;
     @Test
      public void testEntity() {
       User user = new User();
       user.setName("张三");
       user.setAge(18);
       Integer num = mybatisEntityMapper.insertEntity(user);
       Long id = user.getId();
       System.out.println("num=" + num + ",id=" + id);
      }
     }
     
     结果如下：
     15:01:59.173 [main] DEBUG c.g.d.m.e.m.M.insertEntity - ==>  Preparing: INSERT INTO user(name,age) Values (?,?) 
     15:01:59.208 [main] DEBUG c.g.d.m.e.m.M.insertEntity - ==> Parameters: 张三(String), 18(Integer)
     15:01:59.215 [main] DEBUG c.g.d.m.e.m.M.insertEntity - <==    Updates: 1
     num=1,id=1

#### 批量添加实体 (insertEntityList)
     
     @Test
     public void testEntity() {
      List<User> userList = new ArrayList<>();
     for (int i = 0; i < 5; i++) {
       User user = new User();
       user.setName("王五" + i);
       user.setAge(10 + i);
       userList.add(user);
      }
      Integer num = mybatisEntityMapper.insertEntityList(userList);
      userList.forEach(user -> {
       System.out.println("id=" + user.getId());
      });
      System.out.println("num=" + num);
     }
     
      结果如下：
     15:36:22.522 [main] DEBUG c.g.d.m.e.m.M.insertEntityList - ==>  Preparing: INSERT INTO user(name,age) Values (?,?),(?,?),(?,?),(?,?),(?,?) 
     15:36:22.561 [main] DEBUG c.g.d.m.e.m.M.insertEntityList - ==> Parameters: 王五0(String), 10(Integer), 王五1(String), 11(Integer), 王五2(String), 12(Integer), 王五3(String), 13(Integer), 王五4(String), 14(Integer)
     15:36:22.564 [main] DEBUG c.g.d.m.e.m.M.insertEntityList - <==    Updates: 5
     id=32
     id=33
     id=34
     id=35
     id=36
     num=5

#### 编辑实体数据（updateEntity）
     
     @Test
     public void testEntity() {
      User user = new User();
      user.setId(1L);
      user.setName("李四");
      user.setAge(19);
      Integer num = mybatisEntityMapper.updateEntity(user);
      Long id = user.getId();
      System.out.println("num=" + num + ",id=" + id);
     }
     
     结果如下
     15:04:51.897 [main] DEBUG c.g.d.m.e.m.M.updateEntity - ==>  Preparing: UPDATE user SET name=?,age=? WHERE id= ? 
     15:04:51.926 [main] DEBUG c.g.d.m.e.m.M.updateEntity - ==> Parameters: 李四(String), 19(Integer), 1(Long)
     15:04:51.935 [main] DEBUG c.g.d.m.e.m.M.updateEntity - <==    Updates: 1
     num=1,id=1

#### 编辑实体数据 (updateEntityByMap)
    
    @Test
    public void testEntity() {
     User user = new User();
     user.setAge(99);
     Map<String, Object> whereMap = new HashMap<>();
     // name为表字段
     whereMap.put("name", "王五1");
     Integer num = mybatisEntityMapper.updateEntityByMap(user, whereMap);
     System.out.println("num=" + num);
    }
    
    结果如下
    15:46:20.037 [main] DEBUG c.g.d.m.e.m.M.updateEntityByMap - ==>  Preparing: UPDATE user SET age=? WHERE name=? 
    15:46:20.067 [main] DEBUG c.g.d.m.e.m.M.updateEntityByMap - ==> Parameters: 99(Integer), 王五1(String)
    15:46:20.072 [main] DEBUG c.g.d.m.e.m.M.updateEntityByMap - <==    Updates: 1
    num=1

#### 删除实体数据(deleteEntity)
    
    @Test
    public void testEntity() {
     User user = new User();
     user.setId(1L);
     Integer num = mybatisEntityMapper.deleteEntity(user);
     System.out.println("num=" + num);
    }
    
    结果如下
    15:52:03.598 [main] DEBUG c.g.d.m.e.m.M.deleteEntity - ==>  Preparing: DELETE FROM user WHERE id=? 
    15:52:03.627 [main] DEBUG c.g.d.m.e.m.M.deleteEntity - ==> Parameters: 1(Long)
    15:52:03.633 [main] DEBUG c.g.d.m.e.m.M.deleteEntity - <==    Updates: 1
    num=1


#### 删除实体数据(deleteEntityByMap)
    
    @Test
    public void testEntity() {
     Map<String, Object> deleteMap = new HashMap<>();
      // name为表字段
     deleteMap.put("name", "王五1");
     Integer num = mybatisEntityMapper.deleteEntityByMap(deleteMap, User.class);
     System.out.println("num=" + num);
    }
    
    结果如下
    16:05:55.522 [main] DEBUG c.g.d.m.e.m.M.deleteEntityByMap - ==>  Preparing: DELETE FROM user WHERE name=? 
    16:05:55.553 [main] DEBUG c.g.d.m.e.m.M.deleteEntityByMap - ==> Parameters: 王五1(String)
    16:05:55.559 [main] DEBUG c.g.d.m.e.m.M.deleteEntityByMap - <==    Updates: 1
    num=1

#### 查询实体数据(selectMapList)
    
    @Test
    public void testEntity() {
     Map<String, Object> whereMap = new HashMap<>();
    // name为表字段
     whereMap.put("name", "王五2");
     List<Map<String, Object>> list = mybatisEntityMapper.selectMapList(whereMap, User.class);
     list.forEach(map -> {
      System.out.println(map);
     });
    }
    结果如下:
    16:11:04.723 [main] DEBUG c.g.d.m.e.m.M.selectMapList - ==>  Preparing: SELECT id AS id,name AS name,age AS age FROM user WHERE name=? 
    16:11:04.752 [main] DEBUG c.g.d.m.e.m.M.selectMapList - ==> Parameters: 王五2(String)
    16:11:04.790 [main] DEBUG c.g.d.m.e.m.M.selectMapList - <==      Total: 1
    {name=王五2, id=3, age=12}

