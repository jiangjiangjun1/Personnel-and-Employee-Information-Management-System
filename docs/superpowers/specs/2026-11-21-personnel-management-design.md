# 人事员工信息管理系统设计

## 1. 背景与目标

本项目是《JavaEE企业级开发》课程设计，使用 JSP 和 MVC 架构实现一个可运行的人事员工信息管理系统。系统需要覆盖基础员工档案和实际人事业务，支持不同角色按权限操作，并能够为课程设计报告提供需求分析、数据库设计、业务流程和运行成果。

## 2. 范围

### 2.1 角色

- `ADMIN`：维护账号、部门和岗位，查看全部业务及统计数据。
- `HR`：管理员工档案，登记考勤，审批请假，生成并发放工资。
- `EMPLOYEE`：查看个人档案、考勤和工资，提交请假，维护个人联系方式。

### 2.2 功能

- 登录、退出和基于角色的页面访问控制。
- 首页统计：员工数、在职人数、部门数、待审批请假数和近期业务数据。
- 部门与岗位的查询、新增、编辑、启停。
- 员工档案分页查询及新增、编辑、启停，支持按工号、姓名、部门筛选。
- 每日考勤登记，状态包括正常、迟到、早退和缺勤。
- 员工提交请假，HR/管理员审批通过或驳回。
- 按月为在职员工生成工资单，编辑奖金、补贴、扣款并确认发放。
- 员工个人中心、个人考勤、个人请假和个人工资条。

不包含前端单页应用、复杂组织树、考勤设备接入、财务系统对接、邮件通知和云部署。课程设计报告不属于本系统运行时功能，但项目结构和数据需要支撑报告编写。

## 3. 技术方案

- Java 17、Maven 3.9。
- Spring Boot 3.x，内置 Tomcat。
- Spring MVC、JSP、JSTL，服务端渲染。
- Spring Security 会话登录，BCrypt 密码哈希。
- Spring JDBC 实现数据访问，明确区分 Controller、Service 和 Repository。
- MySQL 8，Flyway 管理建表、索引和演示数据。
- Docker Compose 提供本地 MySQL。
- JUnit 5、Spring Boot Test、MockMvc 和 Testcontainers。
- GitHub Actions 执行持续集成。

## 4. 架构

```text
JSP 视图
  -> Controller：请求绑定、基础校验、页面跳转
  -> Service：事务、权限外的业务规则和流程编排
  -> Repository：SQL 与数据映射
  -> MySQL
```

主要包结构：

```text
src/main/java/com/fzgs/personnel/
├── config/
├── controller/
├── service/
├── repository/
├── model/
├── dto/
└── common/
src/main/resources/db/migration/
src/main/webapp/WEB-INF/jsp/
src/test/java/
```

Controller 不直接执行 SQL；业务规则位于 Service；Repository 只负责持久化。JSP 公共片段统一导航、权限菜单、消息提示和分页。

## 5. 数据模型

| 表 | 主要字段 | 约束与关系 |
|---|---|---|
| `departments` | `id`、`code`、`name`、`manager`、`phone`、`status`、时间字段 | 编码唯一；一对多员工 |
| `positions` | `id`、`code`、`name`、`level`、`description`、`status`、时间字段 | 编码唯一；一对多员工 |
| `employees` | `id`、`employee_no`、姓名、性别、生日、电话、邮箱、地址、入职日期、基本工资、`status`、部门和岗位外键 | 工号唯一；状态为 `ACTIVE`、`INACTIVE` 或 `RESIGNED` |
| `app_users` | `id`、`username`、`password_hash`、显示名、角色、启用状态、员工外键 | 用户名唯一；员工账号唯一 |
| `attendance_records` | `id`、员工、日期、上下班时间、状态、备注 | 员工和日期联合唯一 |
| `leave_requests` | `id`、员工、类型、开始日期、结束日期、原因、状态、审批人、审批时间、审批意见 | 状态为 `PENDING`、`APPROVED` 或 `REJECTED` |
| `payroll_records` | `id`、员工、工资月份、基本工资、奖金、补贴、扣款、社保、实发工资、状态、发放时间、创建人 | 员工和月份联合唯一；状态为 `DRAFT` 或 `PAID` |

所有主键使用 `BIGINT`，金额使用 `DECIMAL(12,2)`。Flyway 创建外键、唯一索引和常用筛选字段索引，并加载演示账号和业务数据。

## 6. 业务规则

- 工号、用户名、部门编码和岗位编码不可重复。
- 部门或岗位停用后不能分配新员工，既有员工仍可查看。
- `RESIGNED` 员工不能登记考勤、提交请假或生成新工资单。
- 请假开始日期不得晚于结束日期；只有 `PENDING` 请假可以审批，审批后不可重复处理。
- 同一员工同一天只有一条考勤记录；重复登记时更新该记录。
- 同一员工同一月份只有一张工资单；只能在 `DRAFT` 状态修改，`PAID` 后不可编辑。
- 存在业务记录的员工不物理删除，通过状态改为离岗。
- 普通员工只能读取或修改与自身账号关联的数据。

## 7. 页面与错误处理

后台页面使用统一侧边导航和顶部用户区。列表页提供筛选、分页和操作按钮；表单页保留输入并进行服务端校验；审批和状态变更使用 POST。

参数错误时返回原页面并显示字段提示；重复数据和非法状态转换显示可读业务错误；权限不足返回 403；未知异常返回统一 500 页面。输入在 Controller/DTO 层校验，涉及跨记录和状态的规则在 Service 层再次校验。

## 8. 测试与 CI

- Service 单元测试覆盖请假、工资、离职限制、重复数据等规则。
- Repository 集成测试使用真实 MySQL 容器验证 SQL、外键、唯一约束和分页。
- MockMvc 流程测试覆盖登录、角色权限、员工维护、请假审批和工资生成。
- 本地命令：`docker compose up -d mysql`、`mvn spring-boot:run`、`mvn test`、`mvn clean package`。
- `.github/workflows/ci.yml` 在推送 `main` 和 Pull Request 时使用 JDK 17、Maven 缓存和 MySQL 8 服务，执行 `mvn -B verify`。
- 数据库凭据通过环境变量传入，CI 和仓库不包含生产密码。

## 9. 验收标准

- `docker compose up -d mysql` 后可启动 MySQL 8。
- `mvn spring-boot:run` 后可登录并完成三个角色的主要流程。
- `mvn test` 和 `mvn -B verify` 全部通过。
- 数据库迁移、演示数据、README 启动说明和演示账号齐全。
- GitHub Actions 在 `main` 推送和 Pull Request 上成功运行。
