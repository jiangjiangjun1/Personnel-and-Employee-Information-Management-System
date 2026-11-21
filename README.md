# 人事员工信息管理系统

[![CI](https://github.com/jiangjiangjun1/Personnel-and-Employee-Information-Management-System/actions/workflows/ci.yml/badge.svg)](https://github.com/jiangjiangjun1/Personnel-and-Employee-Information-Management-System/actions/workflows/ci.yml)

基于 JSP 和 MVC 架构的 JavaEE 课程设计项目，用于管理员工档案以及部门、岗位、考勤、请假和薪资业务。

## 功能模块

- 三种角色：管理员 `ADMIN`、人事专员 `HR`、普通员工 `EMPLOYEE`。
- 管理员：账号维护、部门管理、岗位管理、全局统计。
- 人事专员：员工档案、考勤登记、请假审批、月度薪资。
- 普通员工：个人资料、个人考勤、请假申请、个人工资条。
- Spring Security 登录鉴权、CSRF 防护和按角色访问控制。
- 首页统计、分页查询、业务校验、友好错误页和响应式后台界面。

## 技术栈

Java 17、Spring Boot 3.3、Spring MVC、JSP/JSTL、Spring Security、Spring JDBC、Flyway、MySQL 8、Maven、JUnit 5、MockMvc、Testcontainers、Docker Compose、GitHub Actions。

## 项目结构

```text
src/main/java/com/fzgs/personnel/
├── config/       Spring Security 配置
├── controller/   MVC 控制器
├── service/      事务与业务规则
├── repository/   JDBC 数据访问
├── model/        实体与枚举
├── dto/          表单对象
└── common/       分页、异常处理
src/main/resources/db/migration/  Flyway 建表与演示数据
src/main/webapp/WEB-INF/jsp/      JSP 页面
src/test/java/                    单元测试与 MySQL 集成测试
docs/superpowers/                 设计文档与实施计划
```

## 快速开始

需要 Java 17+、Maven 3.9+ 和 Docker。

```bash
docker compose up -d mysql
mvn spring-boot:run
```

访问 [http://localhost:8080](http://localhost:8080)。Compose 会创建独立的 `personnel-management-mysql` 容器，宿主机端口为 `3307`，不会复用或修改其他 MySQL 容器。

## 演示账号

| 用户名 | 密码 | 角色 |
|---|---|---|
| `admin` | `123456` | 管理员 |
| `hr` | `123456` | 人事专员 |
| `employee` | `123456` | 普通员工 |

这些账号仅用于本地课程演示。部署到共享或生产环境前，必须修改密码和数据库凭据。

## 测试与打包

```bash
mvn test
mvn -B verify
mvn clean package
```

本地集成测试通过 Testcontainers 启动 MySQL 8。打包产物为 `target/personnel-management.war`，可执行：

```bash
java -jar target/personnel-management.war
```

## 数据库配置

默认连接参数可通过环境变量覆盖：

| 变量 | 默认值 |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3307/personnel_management?...` |
| `DB_USERNAME` | `personnel` |
| `DB_PASSWORD` | `personnel_dev` |
| `MYSQL_PORT` | `3307` |
| `MYSQL_IMAGE` | `mysql:8.0` |

Flyway 在应用启动时自动创建 7 张业务表并导入演示数据。

## 持续集成

`.github/workflows/ci.yml` 在推送到 `main` 和创建 Pull Request 时运行。GitHub Actions 使用 Temurin JDK 17、Maven 缓存和 MySQL 8 服务，执行 `mvn -B verify`。

## 常见问题

- `3307` 端口被占用：修改 Compose 的 `MYSQL_PORT` 和应用的 `DB_URL`。
- Docker 无法连接：确认 Docker Desktop 已启动，并确保当前用户有访问 Docker socket 的权限。
- 本地集成测试无法启动容器：检查 Docker 和已缓存的 `mysql:8.0` 镜像。
- 页面未加载样式：确认 WAR 中包含 `src/main/resources/static/css/app.css`，并检查 `/css/app.css` 是否可访问。

## 设计文档

- [系统设计](docs/superpowers/specs/2026-11-21-personnel-management-design.md)
- [实施计划](docs/superpowers/plans/2026-11-21-personnel-management-implementation.md)
