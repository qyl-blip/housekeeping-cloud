# Housekeeping API 服务模块

家政服务管理系统的核心 API 服务模块，提供 RESTful 接口。

## 功能模块

- 用户管理与权限控制
- 家政服务 CRUD
- 文件上传管理
- 订单与预约管理
- 评论与收藏
- 日志记录
- 数据统计

## 技术栈

- Spring Boot 2.5.5
- MyBatis-Plus 3.5.2
- MySQL 8.0
- Druid 连接池
- Nacos 服务注册
- Redis 缓存

## 配置说明

### application.yml 配置项

```yaml
server:
  port: 8009                    # 服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/DB_NAME  # 数据库名称
  servlet:
    multipart:
      max-file-size: 10MB       # 文件上传大小限制

file:
  upload:
    path: BASE_LOCATION         # 文件上传路径
```

### 日志配置

修改 `logback-spring.xml` 中的 `LOG_HOME` 值设置日志存储路径。

## 部署流程

1. 配置 `application.yml` 中的端口、数据库、文件路径
2. 修改 `logback-spring.xml` 日志路径
3. 打包：`mvn clean package`
4. 上传 jar 包和 `upload` 文件夹到服务器
5. 导入数据库
6. 启动服务：
```bash
java -jar -Xms64m -Xmx128m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=64m study-0.0.1-SNAPSHOT.jar
```

## 数据库操作

### 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS housekeeping DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 备份数据库
```bash
mysqldump -u root -p --databases housekeeping > backup.sql
```

### 恢复数据库
```sql
source /path/to/backup.sql
```

## 监控

Druid 监控面板：http://localhost:8009/druid/index.html

## 开发注意事项

- 实体字段建议使用 String 类型，MyBatis-Plus 更新时 null 值不会被更新
- 修改 yml 配置后需执行 `mvn clean` 使配置生效
- 跨域配置见 `CorsConfig.java`
- 静态资源路径配置见 `WebMvcConfig.java`





