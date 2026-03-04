# 家政服务管理系统 - 后端

基于 Spring Cloud Alibaba 微服务架构的家政服务管理系统后端项目。

## 技术栈

- Java 8
- Spring Boot 2.5.5
- Spring Cloud 2020.0.4
- Spring Cloud Alibaba 2021.1
- MyBatis-Plus 3.5.2
- MySQL 8.0
- Redis
- Nacos (服务注册与发现)
- OpenFeign (微服务调用)
- Druid (数据库连接池)

## 项目结构

```
housekeeping-cloud/
├── housekeeping-api/              # API 服务模块
│   ├── src/                       # 源代码
│   ├── upload/                    # 文件上传目录
│   └── pom.xml
├── housekeeping-local-service/    # 本地服务模块
│   ├── src/                       # 源代码
│   └── pom.xml
└── pom.xml                        # 父工程配置
```

## 主要功能

- 用户管理与权限控制
- 家政服务分类管理
- 服务预约与订单管理
- 地址管理
- 评论与收藏
- 公告管理
- 日志记录
- 文件上传

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis
- Nacos Server

### 配置说明

1. 修改 `housekeeping-api/src/main/resources/application.yml`：
   - 配置服务端口
   - 配置数据库连接信息 (DB_NAME)
   - 配置文件上传路径 (BASE_LOCATION)
   - 配置 Nacos 地址

2. 修改 `logback-spring.xml` 中的日志路径 (LOG_HOME)

3. 导入数据库：
```bash
mysql -u root -p
source database.sql
```

### 本地运行

```bash
# 1. 启动 Nacos Server
# 2. 启动 Redis
# 3. 编译打包
mvn clean package

# 4. 运行 API 服务
cd housekeeping-api/target
java -jar study-0.0.1-SNAPSHOT.jar

# 5. 运行本地服务
cd housekeeping-local-service/target
java -jar housekeeping-local-service-1.0.0.jar
```

## 部署说明

### 打包

```bash
mvn clean package
```

### 服务器部署

1. 将 jar 包上传到服务器
2. 将 `upload` 文件夹上传到服务器
3. 确保 MySQL 和 Redis 已启动
4. 运行启动命令：

```bash
java -jar -Xms64m -Xmx128m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=64m study-0.0.1-SNAPSHOT.jar
```

## 监控

- Druid 监控地址：http://localhost:8009/druid/index.html

## 开发说明

- 实体字段建议使用 String 类型，MyBatis-Plus 更新时 null 值不会被更新
- 修改配置文件后需要执行 `mvn clean` 使配置生效
- 跨域配置见 `CorsConfig.java`
- 文件上传大小限制在 `application.yml` 的 `multipart` 配置中

## License

MIT
