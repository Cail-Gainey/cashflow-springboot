# Web记账应用 - 后端

## [前端项目](https://github.com/Cail-Gainey/cashflow-vue)

## 项目简介
后端采用 **Spring Boot 3** 框架，提供 **RESTful API** 接口，处理前端请求。Spring Boot 的简洁配置和强大的生态系统使得开发和部署变得更加简单。为了提高应用的性能和可扩展性，后端还使用了 **Spring Data JPA** 进行数据持久化操作，提供高效和直观的数据库操作。

## 技术栈
- **Spring Boot 3**：构建后端服务。
- **Spring Data JPA**：数据持久化操作。
- **JWT**：用户认证和授权。
- **Spring Boot Starter Web**：简化Web应用开发，提供RESTful API接口。
- **MyBatis Plus**：简化MyBatis开发，提高数据库操作效率。
- **Spring Boot Starter AOP**：实现面向切面编程，用于日志记录和性能监控。
- **Spring Boot Starter Mail**：用于发送电子邮件，支持用户注册和找回密码功能。
- **Spring Boot Starter Data Redis**：用于缓存数据，提高响应速度。
- **Easy Captcha**：生成验证码，确保用户注册和登录的安全性。

## 后端架构

### 多模块设计
项目后端采用了 **多模块设计**，主要包括以下两个核心模块：

1. **通用代码模块**
   - 包含项目中通用的功能和服务，如工具类、公共配置、日志管理、异常处理等。
   - 通过模块化设计，减少代码耦合，提高代码复用性和维护性。
   - 使用 **Maven** 多模块管理功能，便于团队协作和版本控制。

2. **Web模块**
   - 负责处理前端的所有HTTP请求，包括用户认证、数据查询、增删改查等操作。
   - 提供 **RESTful API** 接口，与前端进行数据交互。
   - 使用 **Spring Boot Starter Web** 组件库，简化Web应用的开发流程。

## 安全防护措施

### 1. 防止SQL注入的Filter
为了防止 **SQL注入** 攻击，项目实现了一个专门的过滤器（Filter）。该过滤器会对所有传入的请求参数进行检查，识别并拦截潜在的SQL注入攻击。具体措施如下：
- 使用预定义的规则和模式匹配技术，能够有效识别常见的SQL注入攻击payload。
- 拦截到可疑请求时，系统会拒绝该请求，并在日志中记录详细的攻击信息，包括请求来源、时间、参数内容等。

### 2. 基于Token的用户封禁机制
项目采用了 **JWT**（JSON Web Token） 作为用户认证的令牌，增强用户认证的安全性：
- 用户登录后，系统会生成包含用户信息的Token，并将其返回给客户端。
- 每次请求时，客户端需要将Token包含在请求头中，后端服务接收到Token后，会对其进行解析和验证。
- 系统会检测异常行为（如多次失败登录尝试、频繁请求敏感接口等），并自动触发封禁逻辑。
- 被封禁的用户ID或账号信息会记录在黑名单表中，后续所有来自该用户的请求都会被拒绝，并提示“账户已被封禁，请联系管理员”。

此机制有效提升了系统的安全性，避免了恶意用户对系统的持续攻击。

## 运行与部署

### 安装依赖
首先，确保已安装 **Maven** 和 **Java 17+**，然后可以通过以下命令安装项目依赖：
``` bash
mvn clean install
```

启动项目
你可以使用以下命令启动后端项目：
```bash
mvn spring-boot:run
```

生产环境构建
构建生产环境时，使用以下命令生成可执行的 JAR 包：
``` bash
mvn clean package
```

## 配置说明
- 数据库：使用 MySQL 8 作为数据存储，确保在 application.yml 或 application.properties 中配置正确的数据库连接信息。
- Redis：用于缓存数据，确保在配置文件中设置 Redis 连接信息。
- 邮件服务：使用 Spring Boot Starter Mail 发送邮件，配置邮件服务器信息以支持注册和密码找回。
