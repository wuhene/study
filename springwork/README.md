# 简介

实现一个用户的管理功能，实现对用户增删改查

1. 后端搭建SpringBoot + Mybatis
2. 数据库使用mysql
3. 使用aop对关键方法记录业务信息、异常信息和执行时长，打印日志
4. 日志集成logback

# 依赖pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.5.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.wuhen</groupId>
    <artifactId>springwork</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springwork</name>
    <description>springwork</description>
    <properties>
        <java.version>8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.2</version>
        </dependency>
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.3.0</version>
        </dependency>
        <!--aop-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--logback-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.2.3</version>
        </dependency>
        <!--数据库-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.10</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.83</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>31.1-jre</version>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

# 配置文件

application.yaml

```yaml
server:
  port: 8001
mybatis:
  type-aliases-package: com.wuhen.springwork.model
  mapper-locations: classpath:mapper/*.xml
spring:
  application:
    name: springwork
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springwork?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useAffectedRows=true
    username: root
    password: root
```

# 分包

1. anno包 注解包，存放自定义注解类
2. aspect包 切面包，存放切面类
3. base 存放一些基本使用的类
4. controller 控制层
5. dao 持久化层包
6. enums 枚举包 枚举定义
7. model 数据表实体类包
8. service 业务代码包
9. utils 工具包

# 日志功能实现

记录关键的业务方法和执行时间

1. 自定义一个注解LogRecord ，方法上加上这个注解就记录该方法请求参数、返回值、执行时长日志

   - ```java
     @Target(ElementType.METHOD)
     @Retention(RetentionPolicy.RUNTIME)
     public @interface LogRecord {
         String tag();//该方法唯一标识，日志中好找
     }
     ```

2. 定义一个日志切面LogAspect，用来增强加上LogRecord 注解的方法，做环绕增强，打印请求参数、返回值、执行时长日志

   - ```java
     @Aspect
     @Component
     @Order(1)
     public class LogAspect {
         private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
         /**
          * 有这个注解的方法都被视为切点
          */
         @Pointcut("@annotation(com.wuhen.springwork.anno.LogRecord)")
         public void advice(){}
     
         @Around("advice()")//环绕增强，前后都会执行
         public Object around(ProceedingJoinPoint joinPoint) {
             long start = System.currentTimeMillis();
             MethodSignature signature = (MethodSignature) joinPoint.getSignature();
             Method method = signature.getMethod();
             Object[] args = joinPoint.getArgs();
             LogRecord logRecord = method.getDeclaredAnnotation(LogRecord.class);
             String tag = logRecord.tag();
             Object proceed = null;
             try {
                 proceed = joinPoint.proceed();
                 logger.info("方法tag为：[{}]，参数为：{} 返回值为：{}",tag,JSONUtils.toJson(args),JSONUtils.toJson(proceed));
                 logger.info("方法tag为：[{}],执行时间{}ms",tag,System.currentTimeMillis() - start);
             } catch (Throwable throwable) {
                 logger.error("方法tag为：[{}]，参数为：{} 出现异常,异常信息为：",tag,JSONUtils.toJson(args),throwable);
                 return Response.error(ErrorCodeEnum.EXCEPTION);
             }
             return proceed;
         }
     }
     ```

# 统一响应对象定义

将返回值统一为同一响应对象，这样能够知道本次请求成功与否、错误信息，在base包

Response对象主要包含：

1. code：错误码
2. msg：错误信息
3. data：具体响应数据

- ```java
  @Data
  public class Response<T> {
      private int code;
      private String msg;
      private T data;
  
  
      public Response(int code, String msg, T data) {
          this.code = code;
          this.msg = msg;
          this.data = data;
      }
  
      public static <R> Response<R> ok(R data){
          return new Response<>(0,"success",data);
      }
      public static Response ok(){
          return new Response<>(0,"success",null);
      }
  
      public static Response error(ErrorCodeEnum errorCode){
          return new Response(errorCode.getCode(),errorCode.getMsg(),null);
      }
  }
  ```

# 错误码枚举定义

程序发生错误时响应给前端的一些错误信息

对应Reponse的code和msg

- ```java
  public enum ErrorCodeEnum {
      PARAM_ERROR(10001,"请检查参数是否正确"),
      UPDATE_FAIL(10002,"修改数据失败"),
      USERNAME_AL_EXIST(10003,"该昵称已被使用"),
      EXCEPTION(500,"服务端出现异常");
      private int code;
      private String msg;
  
      ErrorCodeEnum(int code, String msg) {
          this.code = code;
          this.msg = msg;
      }
  
      public int getCode() {
          return code;
      }
  
      public String getMsg() {
          return msg;
      }
  }
  ```

# util工具

程序需要频繁编写的一些公用功能，将其抽出一个工具类使用

本次程序就只有一个json的工具类

```java
public class JSONUtils {
    public static String toJson(Object object){
        return JSON.toJSONString(object);
    }

    /**
     * 反序列化json
     * @param json json串
     * @param tClass 对象class类型
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T fromJson(String json,Class<T> tClass){
        return JSON.parseObject(json,tClass);
    }

    /**
     * 反序列化json为list
     * @param json json传
     * @param tClass 对象class类型
     * @param <T> 泛型
     * @return list集合
     */
    public static <T> List<T> fromJson2List(String json,Class<T> tClass){
        return JSON.parseArray(json,tClass);
    }
}
```

# model实体类编写

对应数据库表中的各个字段

@JsonFormat查询数据库的datetime类型数据时，格式化为指定格式的Date字段

```java
@Data
public class User {
    private Long uid;
    private String name;
    private String username;
    private int sex;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date updateTime;
}
```

# dao层编写

直接操作数据库的层

定义mapper接口，mybatis会用动态代理创建出接口的代理对象，执行具体的sql语句

- ```java
  @Mapper
  @Repository
  public interface UserMapper {
      /**
       * 保存用户信息到数据库
       * @param user 用户对象
       * @return 主键id
       */
      void saveUser(User user);
  
      /**
       * 删除某个用户
       * @param uid 用户id
       */
      void deleteUser(long uid);
  
  
      /**
       * 修改某个用户信息
       * @param user 用户对象
       * @return 修改成功条数
       */
      int updateUser(User user);
  
      /**
       * 查询用户列表,只查uid和name和username
       * @return 用户对象列表
       */
      List<User> queryUserList();
  
      /**
       *查询用户详细信息
       * @param uid 用户id
       * @return 用户详细信息
       */
      User queryUserDetailByUid(long uid);
  
      /**
       *查询用户详细信息
       * @param username 用户昵称
       * @return 用户详细信息
       */
      User queryUserDetailByUsername(String username);
  
  
  }
  ```

sql语句编写，编写在mapper.xml文件中，与我们程序解耦

resultMap主要是为了让数据库的datetime类型字段跟我们实体类对象的Date类型字段相转换

- ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.wuhen.springwork.dao.UserMapper">
  
      <insert id="saveUser" parameterType="user" useGeneratedKeys="true" keyProperty="uid">
          insert into users(name,username,sex,description) values (#{name},#{username},#{sex},#{description})
      </insert>
  
      <delete id="deleteUser" parameterType="long">
          delete from users where uid = #{uid}
      </delete>
  
      <update id="updateUser" parameterType="user">
          update users set
          <if test="name != null">
              name = #{name},
          </if>
          <if test="username != null">
              username = #{username},
          </if>
          <if test="sex != 0">
              sex = #{sex},
          </if>
          description = #{description}
          where uid = #{uid}
      </update>
  
      <select id="queryUserList" resultType="user">
          select uid,name,username from users
      </select>
  
      <resultMap id="userDetailMap" type="user">
          <result column="create_time" property="createTime" jdbcType="TIMESTAMP"/>
          <result column="update_time" property="updateTime" jdbcType="TIMESTAMP"/>
      </resultMap>
  
      <select id="queryUserDetailByUid" parameterType="long" resultMap="userDetailMap" >
          select uid,name,username,sex,description,create_time,update_time from users where uid = #{uid}
      </select>
  
  
      <select id="queryUserDetailByUsername" parameterType="string" resultMap="userDetailMap">
          select uid,name,username,sex,description,create_time,update_time from users where username = #{username}
      </select>
  
  </mapper>
  ```

# service层接口定义

业务代码层

定义出具体要实现的业务，实现类我就不贴了，进代码里面看

- ```java
  public interface UserService {
      /**
       * 新增用户
       * @param user 用户对象
       * @return 此条记录主键
       */
      Response<Long> addUser(User user);
  
      /**
       * 删除用户
       * @param uid 要删除的用户id
       */
      Response delUser(Long uid);
  
      /**
       * 修改用户信息
       * @param user 要修改的用户对象
       * @return 修改成功条数
       */
      Response updateUser(User user);
  
      /**
       * 获取用户列表, 只有uid，name，username 根据页数和每页个数
       * @param pageNum 当前页
       * @param pageSize 每页个数
       * @return
       */
      Response<List<User>> getUserList(int pageNum, int pageSize);
  
      /**
       * 获取用户详细信息
       * @param uid 用户id
       * @return
       */
      Response<User> getUserDetail(long uid);
  
  }
  ```

# controller层编写

直接面向前端的一层，请求首先进这，返回值设置为全json，前后端分离结构

```java
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 新增用户
     * @param user 用户对象
     * @return 数据库主键
     */
    @PostMapping("/addUser")
    public Response<Long> addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    /**
     * 删除用户
     * @param user 用户对象，里面只需要uid字段
     * @return 结果
     */
    @PostMapping("/delUser")
    public Response delUser(@RequestBody User user) {
        return userService.delUser(user.getUid());
    }

    /**
     * 修改用户信息
     * @param user 用户对象
     * @return 结果条数
     */
    @PostMapping("/updateUser")
    public Response updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 获取用户列表,只有uid，name，username 根据页数和每页个数
     * @param pageNum 当前页
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/getUserList/{pageNum}/{pageSize}")
    public Response<List<User>> getUserList(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return userService.getUserList(pageNum, pageSize);
    }

    /**
     * 获取用户详细信息
     * @param uid 用户id
     * @return
     */
    @GetMapping("/getUserDetail/{uid}")
    public Response<User> getUserDetail(@PathVariable("uid") long uid){
        return userService.getUserDetail(uid);
    }
}
```

# 启动类

用来启动程序，虽然要求说在tomcat上运行，但是这个只是打成jar包和war包的区别，我就直接用jar比较方便了

```java
@SpringBootApplication
public class SpringWorkApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringWorkApp.class, args);
    }

}
```

