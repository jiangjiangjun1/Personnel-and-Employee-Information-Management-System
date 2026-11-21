# 人事员工信息管理系统实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个使用 JSP、Spring MVC、Spring Security、Spring JDBC 和 MySQL 的可运行人事员工信息管理系统。

**Architecture:** JSP 负责视图，Controller 负责请求和页面跳转，Service 承载事务与业务规则，Repository 集中管理 SQL。Flyway 创建 7 张业务表并导入演示数据，Docker Compose 提供 MySQL 8，内置 Tomcat 运行应用。

**Tech Stack:** Java 17、Maven 3.9、Spring Boot 3.3、JSP/JSTL、Spring Security、Spring JDBC、Flyway、MySQL 8、JUnit 5、MockMvc、Testcontainers、GitHub Actions。

**Spec:** `docs/superpowers/specs/2026-11-21-personnel-management-design.md`

## Global Constraints

- Java 编译目标和运行目标均为 17；Maven 最低版本为 3.9。
- 所有页面使用 JSP/JSTL 服务端渲染，不使用前端单页应用。
- 所有新增、编辑、审批、生成和状态变更请求使用 POST。
- Controller 不编写 SQL；事务和跨记录规则位于 Service；SQL 位于 Repository。
- 密码只保存 BCrypt 哈希，任何日志和页面都不能输出密码或密码哈希。
- 数据库测试使用真实 MySQL 8；不使用 H2 代替 MySQL 方言测试。
- 金额字段使用 `BigDecimal`，数据库使用 `DECIMAL(12,2)`。
- 所有用户可见文本使用中文；代码标识符使用英文。
- 每个任务完成时运行对应测试并提交一个 Conventional Commit。
- 最终必须通过 `mvn -B verify`，并保证 `mvn clean package` 产出可运行 WAR。

## 文件结构

```text
.github/workflows/ci.yml
compose.yaml
pom.xml
README.md
src/main/java/com/fzgs/personnel/
├── PersonnelApplication.java
├── common/
│   ├── BusinessException.java
│   ├── PageResult.java
│   └── GlobalExceptionHandler.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── UserController.java
│   ├── DepartmentController.java
│   ├── PositionController.java
│   ├── EmployeeController.java
│   ├── AttendanceController.java
│   ├── LeaveController.java
│   ├── PayrollController.java
│   └── ProfileController.java
├── dto/
│   ├── DepartmentForm.java
│   ├── PositionForm.java
│   ├── UserForm.java
│   ├── EmployeeForm.java
│   ├── AttendanceForm.java
│   ├── LeaveForm.java
│   └── PayrollForm.java
├── model/
│   ├── Role.java
│   ├── RecordStatus.java
│   ├── EmployeeStatus.java
│   ├── AttendanceStatus.java
│   ├── LeaveStatus.java
│   ├── PayrollStatus.java
│   ├── AppUser.java
│   ├── Department.java
│   ├── Position.java
│   ├── Employee.java
│   ├── AttendanceRecord.java
│   ├── LeaveRequest.java
│   └── PayrollRecord.java
├── repository/
│   ├── UserRepository.java
│   ├── DepartmentRepository.java
│   ├── PositionRepository.java
│   ├── EmployeeRepository.java
│   ├── AttendanceRepository.java
│   ├── LeaveRepository.java
│   ├── PayrollRepository.java
│   └── DashboardRepository.java
└── service/
    ├── DepartmentService.java
    ├── PositionService.java
    ├── UserService.java
    ├── EmployeeService.java
    ├── AttendanceService.java
    ├── LeaveService.java
    ├── PayrollService.java
    └── DashboardService.java
src/main/resources/
├── application.yml
├── db/migration/V1__schema.sql
├── db/migration/V2__demo_data.sql
└── static/css/app.css
src/main/webapp/WEB-INF/jsp/
├── fragments/layout.jsp
├── auth/login.jsp
├── dashboard/index.jsp
├── user/list.jsp
├── user/form.jsp
├── department/list.jsp
├── department/form.jsp
├── position/list.jsp
├── position/form.jsp
├── employee/list.jsp
├── employee/form.jsp
├── attendance/list.jsp
├── attendance/form.jsp
├── leave/list.jsp
├── leave/form.jsp
├── payroll/list.jsp
├── payroll/form.jsp
├── profile/index.jsp
└── error/{403,404,500}.jsp
src/test/java/com/fzgs/personnel/
├── integration/AbstractIntegrationTest.java
├── integration/DatabaseMigrationTest.java
├── integration/SecurityFlowTest.java
├── integration/EmployeeFlowTest.java
├── integration/LeaveFlowTest.java
├── integration/PayrollFlowTest.java
└── service/{DepartmentServiceTest,PositionServiceTest,UserServiceTest,AttendanceServiceTest,LeaveServiceTest,PayrollServiceTest,DashboardServiceTest}.java
```

---

### Task 1: Maven、Spring Boot 与本地 MySQL 基线

**Files:**
- Create: `pom.xml`
- Create: `compose.yaml`
- Create: `src/main/java/com/fzgs/personnel/PersonnelApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `src/test/java/com/fzgs/personnel/integration/AbstractIntegrationTest.java`
- Create: `src/test/java/com/fzgs/personnel/integration/ApplicationSmokeTest.java`

**Interfaces:**
- Consumes: Java 17 和 Docker。
- Produces: 可启动的 Spring Boot 应用；测试基类 `AbstractIntegrationTest` 提供共享 MySQL 容器并设置 `spring.datasource.*` 动态属性。

- [ ] **Step 1: 编写上下文启动测试**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationSmokeTest extends AbstractIntegrationTest {
    @Test
    void applicationStarts() {
        assertThat(true).isTrue();
    }
}
```

在 `AbstractIntegrationTest` 中使用静态 `MySQLContainer<?>`；CI 设置 `CI_DB=true` 时改用工作流中的 MySQL 服务：

```java
@SpringBootTest
public abstract class AbstractIntegrationTest {
    private static final boolean CI_DB =
        Boolean.parseBoolean(System.getenv().getOrDefault("CI_DB", "false"));
    private static final MySQLContainer<?> MYSQL = CI_DB ? null :
        new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("personnel_management")
            .withUsername("personnel")
            .withPassword("personnel_test");

    static {
        if (MYSQL != null) {
            MYSQL.start();
        }
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        if (MYSQL != null) {
            registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
            registry.add("spring.datasource.username", MYSQL::getUsername);
            registry.add("spring.datasource.password", MYSQL::getPassword);
        }
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=ApplicationSmokeTest test`
Expected: FAIL，因为 Maven 项目和 Spring Boot 入口尚不存在。

- [ ] **Step 3: 创建 Maven 项目**

`pom.xml` 使用 `spring-boot-starter-parent` 3.3.5、`<packaging>war</packaging>`，依赖：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
<dependency>
  <groupId>jakarta.servlet.jsp.jstl</groupId>
  <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
  <groupId>org.glassfish.web</groupId>
  <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-mysql</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>mysql</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>
</dependency>
```

入口类：

```java
@SpringBootApplication
public class PersonnelApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonnelApplication.class, args);
    }
}
```

- [ ] **Step 4: 配置 MySQL 和 JSP**

```yaml
server:
  port: ${SERVER_PORT:8080}
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/personnel_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai}
    username: ${DB_USERNAME:personnel}
    password: ${DB_PASSWORD:personnel_dev}
  flyway:
    enabled: true
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

`compose.yaml` 使用 `mysql:8.4`、数据库 `personnel_management`、用户 `personnel`、密码 `personnel_dev`、端口 `3306`，并配置 `mysqladmin ping` healthcheck。

- [ ] **Step 5: 运行测试和启动检查**

Run: `docker compose up -d mysql`
Run: `mvn -Dtest=ApplicationSmokeTest test`
Expected: PASS。
Run: `mvn spring-boot:run`
Expected: 应用在 8080 端口启动，数据库健康检查通过后退出。

- [ ] **Step 6: 提交**

```bash
git add pom.xml compose.yaml src/main/java/com/fzgs/personnel/PersonnelApplication.java src/main/resources/application.yml src/test/java/com/fzgs/personnel/integration
git commit -m "build: bootstrap Spring Boot JSP application"
```

---

### Task 2: 数据库表、约束和演示数据

**Files:**
- Create: `src/main/resources/db/migration/V1__schema.sql`
- Create: `src/main/resources/db/migration/V2__demo_data.sql`
- Create: `src/test/java/com/fzgs/personnel/integration/DatabaseMigrationTest.java`

**Interfaces:**
- Consumes: Flyway 和 `AbstractIntegrationTest`。
- Produces: 7 张表及演示账号：`admin/123456`、`hr/123456`、`employee/123456`。

- [ ] **Step 1: 编写迁移测试**

```java
class DatabaseMigrationTest extends AbstractIntegrationTest {
    @Autowired JdbcTemplate jdbc;

    @Test
    void createsSevenCoreTablesAndSeedAccount() {
        List<String> tables = jdbc.queryForList(
            "select table_name from information_schema.tables where table_schema = database()",
            String.class);
        assertThat(tables).contains(
            "departments", "positions", "employees", "app_users",
            "attendance_records", "leave_requests", "payroll_records");
        assertThat(jdbc.queryForObject(
            "select count(*) from app_users where username = 'admin'", Long.class)).isEqualTo(1);
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=DatabaseMigrationTest test`
Expected: FAIL，表不存在。

- [ ] **Step 3: 创建表结构**

按设计文档创建 7 张表。关键 SQL 必须包含：

```sql
create table departments (
  id bigint primary key auto_increment,
  code varchar(20) not null unique,
  name varchar(80) not null,
  manager varchar(60),
  phone varchar(30),
  status varchar(20) not null default 'ACTIVE',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp
);
create table positions (
  id bigint primary key auto_increment,
  code varchar(20) not null unique,
  name varchar(80) not null,
  level varchar(30),
  description varchar(255),
  status varchar(20) not null default 'ACTIVE',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp
);
create table employees (
  id bigint primary key auto_increment,
  employee_no varchar(24) not null unique,
  name varchar(60) not null,
  gender varchar(10) not null,
  birth_date date,
  phone varchar(30),
  email varchar(120),
  address varchar(255),
  hire_date date not null,
  base_salary decimal(12,2) not null default 0,
  status varchar(20) not null default 'ACTIVE',
  department_id bigint not null,
  position_id bigint not null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  constraint fk_employee_department foreign key (department_id) references departments(id),
  constraint fk_employee_position foreign key (position_id) references positions(id)
);
create table app_users (
  id bigint primary key auto_increment,
  username varchar(60) not null unique,
  password_hash varchar(100) not null,
  display_name varchar(60) not null,
  role varchar(20) not null,
  enabled boolean not null default true,
  employee_id bigint unique,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  constraint fk_user_employee foreign key (employee_id) references employees(id)
);
create table attendance_records (
  id bigint primary key auto_increment,
  employee_id bigint not null,
  work_date date not null,
  check_in time,
  check_out time,
  status varchar(20) not null,
  remark varchar(255),
  unique key uk_attendance_employee_date (employee_id, work_date),
  constraint fk_attendance_employee foreign key (employee_id) references employees(id)
);
create table leave_requests (
  id bigint primary key auto_increment,
  employee_id bigint not null,
  leave_type varchar(20) not null,
  start_date date not null,
  end_date date not null,
  reason varchar(500) not null,
  status varchar(20) not null default 'PENDING',
  approver_id bigint,
  approved_at datetime,
  approver_comment varchar(500),
  created_at datetime not null default current_timestamp,
  constraint fk_leave_employee foreign key (employee_id) references employees(id),
  constraint fk_leave_approver foreign key (approver_id) references app_users(id)
);
create table payroll_records (
  id bigint primary key auto_increment,
  employee_id bigint not null,
  pay_month char(7) not null,
  base_salary decimal(12,2) not null,
  bonus decimal(12,2) not null default 0,
  allowance decimal(12,2) not null default 0,
  deduction decimal(12,2) not null default 0,
  social_security decimal(12,2) not null default 0,
  net_salary decimal(12,2) not null,
  status varchar(20) not null default 'DRAFT',
  paid_at datetime,
  created_by bigint not null,
  unique key uk_payroll_employee_month (employee_id, pay_month),
  constraint fk_payroll_employee foreign key (employee_id) references employees(id),
  constraint fk_payroll_creator foreign key (created_by) references app_users(id)
);
```

为 `employees.department_id`、`employees.position_id`、考勤日期、请假状态和工资月份建立额外索引。

- [ ] **Step 4: 添加演示数据**

插入 3 个部门、4 个岗位、6 名员工、3 个账号及少量考勤、请假和工资记录。三个账号使用同一 BCrypt 哈希：

```sql
'$2y$10$c4.Jr2RIKrWeHNlCm1DQqOkZPWESRw7/uWrlgxH0Ts4irHbeJrUZa'
```

该哈希对应演示密码 `123456`。`employee` 账号关联一名员工，`admin` 和 `hr` 的 `employee_id` 为 `NULL`。

- [ ] **Step 5: 验证迁移和约束**

Run: `mvn -Dtest=DatabaseMigrationTest test`
Expected: PASS。
Run: `docker compose down -v`
Run: `docker compose up -d mysql`
Run: `mvn -Dtest=DatabaseMigrationTest test`
Expected: Flyway 从空数据库完成迁移，测试仍 PASS。

- [ ] **Step 6: 提交**

```bash
git add src/main/resources/db/migration src/test/java/com/fzgs/personnel/integration/DatabaseMigrationTest.java
git commit -m "feat: add personnel database schema"
```

---

### Task 3: 登录、会话与角色鉴权

**Files:**
- Create: `model/Role.java`
- Create: `model/AppUser.java`
- Create: `repository/UserRepository.java`
- Create: `config/SecurityConfig.java`
- Create: `src/main/webapp/WEB-INF/jsp/auth/login.jsp`
- Create: `src/test/java/com/fzgs/personnel/integration/SecurityFlowTest.java`

**Interfaces:**
- Consumes: `app_users` 表。
- Produces: `UserRepository.findByUsername(String): Optional<AppUser>`；`AppUser` 暴露 `id`、`username`、`passwordHash`、`displayName`、`role`、`enabled`、`employeeId`；Spring Security 角色名为 `ADMIN`、`HR`、`EMPLOYEE`。

- [ ] **Step 1: 编写登录和权限测试**

```java
@AutoConfigureMockMvc
class SecurityFlowTest extends AbstractIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void employeeCannotOpenDepartmentManagement() throws Exception {
        mvc.perform(formLogin().user("employee").password("123456"))
            .andExpect(authenticated().withUsername("employee"));
        mvc.perform(get("/departments").with(user("employee").roles("EMPLOYEE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void seededAdminCanLogIn() throws Exception {
        mvc.perform(formLogin().user("admin").password("123456"))
            .andExpect(authenticated().withUsername("admin"));
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=SecurityFlowTest test`
Expected: FAIL，登录页、用户仓库和安全配置不存在。

- [ ] **Step 3: 实现用户加载和授权**

核心配置：

```java
@Bean
SecurityFilterChain security(HttpSecurity http, UserRepository users) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/error/**").permitAll()
            .requestMatchers("/departments/**", "/positions/**", "/users/**").hasRole("ADMIN")
            .requestMatchers("/employees/**", "/attendance/**", "/payroll/**").hasAnyRole("ADMIN", "HR")
            .requestMatchers("/leave/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
            .permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
    return http.build();
}
```

自定义 `UserDetailsService` 查询 `app_users`，检查 `enabled`，并构建 `ROLE_` 前缀权限。`PasswordEncoder` 使用 `BCryptPasswordEncoder`。

- [ ] **Step 4: 创建 JSP 登录页并验证**

`login.jsp` 使用 POST `/login`，包含用户名、密码、CSRF 隐藏字段和登录失败提示，不显示演示密码。

Run: `mvn -Dtest=SecurityFlowTest test`
Expected: PASS。
Run: `mvn spring-boot:run`
Expected: 浏览器访问 `/dashboard` 跳转 `/login`，三个演示账号均能登录。

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/fzgs/personnel/model src/main/java/com/fzgs/personnel/repository/UserRepository.java src/main/java/com/fzgs/personnel/config src/main/webapp/WEB-INF/jsp/auth src/test/java/com/fzgs/personnel/integration/SecurityFlowTest.java
git commit -m "feat: add role-based authentication"
```

---

### Task 4: 部门与岗位管理

**Files:**
- Create: `model/RecordStatus.java`
- Create: `model/Department.java`
- Create: `model/Position.java`
- Create: `dto/DepartmentForm.java`
- Create: `dto/PositionForm.java`
- Create: `repository/DepartmentRepository.java`
- Create: `repository/PositionRepository.java`
- Create: `service/DepartmentService.java`
- Create: `service/PositionService.java`
- Create: `controller/DepartmentController.java`
- Create: `controller/PositionController.java`
- Create: JSP department/position 列表与表单页
- Create: `src/test/java/com/fzgs/personnel/service/DepartmentServiceTest.java`
- Create: `src/test/java/com/fzgs/personnel/service/PositionServiceTest.java`

**Interfaces:**
- Consumes: Flyway 表、Spring MVC、Spring Security。
- Produces: `DepartmentService.create(DepartmentForm)`、`update(long, DepartmentForm)`、`findAll()`；`PositionService` 提供相同签名；`RecordStatus` 值为 `ACTIVE`、`INACTIVE`。

- [ ] **Step 1: 编写重复编码测试**

```java
class DepartmentServiceTest {
    DepartmentRepository repository = mock(DepartmentRepository.class);
    DepartmentService service = new DepartmentService(repository);

    @Test
    void rejectsDuplicateDepartmentCode() {
        when(repository.existsByCode("HR", 0L)).thenReturn(true);
        BusinessException error = assertThrows(
            BusinessException.class,
            () -> service.create(new DepartmentForm("HR", "人力资源部", "王经理",
                "13800000000", RecordStatus.ACTIVE)));
        assertThat(error.getMessage()).isEqualTo("部门编码已存在");
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=DepartmentServiceTest test`
Expected: FAIL，类和方法不存在。

- [ ] **Step 3: 实现 Repository 和 Service**

`DepartmentRepository` 提供：

```java
List<Department> findAll();
Optional<Department> findById(long id);
boolean existsByCode(String code, long excludedId);
long create(DepartmentForm form);
void update(long id, DepartmentForm form);
void updateStatus(long id, RecordStatus status);
```

Service 去除首尾空格、将编码转大写、执行重复校验，并在状态更新前确认记录存在。岗位服务使用相同模式，编码示例为 `DEV`、`QA`。

- [ ] **Step 4: 实现页面与控制器**

- `GET /departments` 和 `GET /positions` 展示列表。
- `GET /departments/new`、`GET /positions/new` 展示新增表单。
- `POST /departments`、`POST /positions` 创建。
- `GET /departments/{id}/edit`、`GET /positions/{id}/edit` 展示编辑表单。
- `POST /departments/{id}`、`POST /positions/{id}` 更新。
- `POST /departments/{id}/status`、`POST /positions/{id}/status` 切换状态。

JSP 表格显示编码、名称、负责人/职级、状态和操作；重复编码错误返回表单并保留输入。

- [ ] **Step 5: 运行测试和手工流程**

Run: `mvn -Dtest='DepartmentServiceTest,PositionServiceTest' test`
Expected: PASS。
Run: `mvn spring-boot:run`
Expected: `admin` 可新增、编辑和停用部门/岗位；重复编码显示“编码已存在”。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel src/main/webapp/WEB-INF/jsp/department src/main/webapp/WEB-INF/jsp/position src/test/java/com/fzgs/personnel/service
git commit -m "feat: add department and position management"
```

---

### Task 5: 员工档案管理

**Files:**
- Create: `model/Employee.java`
- Create: `model/EmployeeStatus.java`
- Create: `dto/EmployeeForm.java`
- Create: `repository/EmployeeRepository.java`
- Create: `service/EmployeeService.java`
- Create: `controller/EmployeeController.java`
- Create: JSP employee 列表与表单页
- Create: `src/test/java/com/fzgs/personnel/integration/EmployeeFlowTest.java`

**Interfaces:**
- Consumes: Department、Position、Security 当前用户。
- Produces: `EmployeeRepository.findPage(String keyword, Long departmentId, EmployeeStatus status, int page, int size): PageResult<Employee>`；`EmployeeRepository.findByUserId(long): Optional<Employee>`；状态为 `ACTIVE`、`INACTIVE`、`RESIGNED`。

- [ ] **Step 1: 编写新增和离职限制测试**

```java
@AutoConfigureMockMvc
class EmployeeFlowTest extends AbstractIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @Test
    void hrCanCreateEmployee() throws Exception {
        mvc.perform(post("/employees")
                .with(user("hr").roles("HR"))
                .with(csrf())
                .param("employeeNo", "E2026001")
                .param("name", "陈晨")
                .param("gender", "女")
                .param("hireDate", "2026-11-21")
                .param("baseSalary", "8000")
                .param("departmentId", "1")
                .param("positionId", "1")
                .param("status", "ACTIVE"))
            .andExpect(status().is3xxRedirection());
        assertThat(jdbc.queryForObject(
            "select count(*) from employees where employee_no = 'E2026001'", Long.class)).isEqualTo(1);
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=EmployeeFlowTest test`
Expected: FAIL，员工路由不存在。

- [ ] **Step 3: 实现查询与表单校验**

EmployeeForm 使用 Jakarta Validation：

```java
public record EmployeeForm(
    @NotBlank @Size(max = 24) String employeeNo,
    @NotBlank @Size(max = 60) String name,
    @NotBlank String gender,
    LocalDate birthDate,
    String phone,
    @Email String email,
    String address,
    @NotNull LocalDate hireDate,
    @NotNull @DecimalMin("0") BigDecimal baseSalary,
    @NotNull Long departmentId,
    @NotNull Long positionId,
    @NotNull EmployeeStatus status
) {}
```

Repository SQL 使用 `limit ? offset ?` 分页，并联合查询部门名和岗位名。关键字匹配工号或姓名。

- [ ] **Step 4: 实现控制器和 JSP**

- `GET /employees` 支持 `keyword`、`departmentId`、`status`、`page` 参数。
- `GET /employees/new`、`POST /employees` 新增。
- `GET /employees/{id}/edit`、`POST /employees/{id}` 编辑。
- `POST /employees/{id}/status` 更新状态。
- 员工档案列表显示工号、姓名、部门、岗位、入职日期、状态和编辑操作。
- 存在业务记录的员工不能物理删除，状态改为 `RESIGNED`；页面明确显示该限制。

- [ ] **Step 5: 验证搜索、重复工号和权限**

Run: `mvn -Dtest=EmployeeFlowTest test`
Expected: PASS。
人工验证：HR 创建员工并搜索到；重复工号显示错误；EMPLOYEE 访问 `/employees` 返回 403；离职员工无法新增考勤或工资。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel/model/Employee.java src/main/java/com/fzgs/personnel/model/EmployeeStatus.java src/main/java/com/fzgs/personnel/dto/EmployeeForm.java src/main/java/com/fzgs/personnel/repository/EmployeeRepository.java src/main/java/com/fzgs/personnel/service/EmployeeService.java src/main/java/com/fzgs/personnel/controller/EmployeeController.java src/main/webapp/WEB-INF/jsp/employee src/test/java/com/fzgs/personnel/integration/EmployeeFlowTest.java
git commit -m "feat: add employee records management"
```

---

### Task 6: 员工账号维护

**Files:**
- Modify: `src/main/java/com/fzgs/personnel/repository/UserRepository.java`
- Create: `src/main/java/com/fzgs/personnel/dto/UserForm.java`
- Create: `src/main/java/com/fzgs/personnel/service/UserService.java`
- Create: `src/main/java/com/fzgs/personnel/controller/UserController.java`
- Create: `src/main/webapp/WEB-INF/jsp/user/list.jsp`
- Create: `src/main/webapp/WEB-INF/jsp/user/form.jsp`
- Create: `src/test/java/com/fzgs/personnel/service/UserServiceTest.java`

**Interfaces:**
- Consumes: `UserRepository`、`PasswordEncoder`、`EmployeeRepository`。
- Produces: `UserService.create(UserForm)`、`update(long, UserForm)`、`setEnabled(long, boolean)`；`UserForm` 暴露 `username`、`displayName`、`role`、`enabled`、`employeeId`、`password`。

- [ ] **Step 1: 编写账号唯一性与密码哈希测试**

```java
class UserServiceTest {
    UserRepository users = mock(UserRepository.class);
    EmployeeRepository employees = mock(EmployeeRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UserService service = new UserService(users, employees, encoder);

    @Test
    void rejectsDuplicateUsername() {
        when(users.existsByUsername("hr2", 0L)).thenReturn(true);
        BusinessException error = assertThrows(BusinessException.class,
            () -> service.create(new UserForm("hr2", "人事二号", Role.HR,
                true, null, "12345678")));
        assertThat(error.getMessage()).isEqualTo("用户名已存在");
    }

    @Test
    void storesBcryptHashInsteadOfPlainPassword() {
        Employee employee = mock(Employee.class);
        when(employee.getStatus()).thenReturn(EmployeeStatus.ACTIVE);
        when(employees.findById(3L)).thenReturn(Optional.of(employee));
        service.create(new UserForm("employee2", "员工二号", Role.EMPLOYEE,
            true, 3L, "12345678"));
        ArgumentCaptor<String> hash = ArgumentCaptor.forClass(String.class);
        verify(users).create(any(UserForm.class), hash.capture());
        assertThat(hash.getValue()).isNotEqualTo("12345678");
        assertThat(encoder.matches("12345678", hash.getValue())).isTrue();
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=UserServiceTest test`
Expected: FAIL，账号服务不存在。

- [ ] **Step 3: 实现账号规则**

`UserRepository` 增加：

```java
List<AppUser> findAll();
boolean existsByUsername(String username, long excludedId);
long create(UserForm form, String passwordHash);
void update(long id, UserForm form, String passwordHash);
void setEnabled(long id, boolean enabled);
```

创建时密码必填且至少 8 位；编辑时密码为空表示不修改。`EMPLOYEE` 角色必须绑定未被其他账号占用的员工，`ADMIN` 和 `HR` 的 `employeeId` 必须为空。服务端禁止管理员停用当前登录账号。

- [ ] **Step 4: 实现账号页面**

- `GET /users` 显示用户名、显示名、角色、关联员工和状态。
- `GET /users/new`、`POST /users` 创建账号。
- `GET /users/{id}/edit`、`POST /users/{id}` 编辑资料或重置密码。
- `POST /users/{id}/status` 启用或停用账号。
- 表单使用下拉框选择角色和可绑定员工，密码输入框不回显已有密码。

- [ ] **Step 5: 验证**

Run: `mvn -Dtest=UserServiceTest test`
Expected: PASS。
人工验证：管理员创建 HR 和员工账号；重复用户名失败；停用后的账号不能登录；非管理员访问 `/users` 返回 403。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel/dto/UserForm.java src/main/java/com/fzgs/personnel/service/UserService.java src/main/java/com/fzgs/personnel/controller/UserController.java src/main/java/com/fzgs/personnel/repository/UserRepository.java src/main/webapp/WEB-INF/jsp/user src/test/java/com/fzgs/personnel/service/UserServiceTest.java
git commit -m "feat: add administrator account management"
```

---

### Task 7: 每日考勤

**Files:**
- Create: `model/AttendanceStatus.java`
- Create: `model/AttendanceRecord.java`
- Create: `dto/AttendanceForm.java`
- Create: `repository/AttendanceRepository.java`
- Create: `service/AttendanceService.java`
- Create: `controller/AttendanceController.java`
- Create: JSP attendance 列表与表单页
- Create: `src/test/java/com/fzgs/personnel/service/AttendanceServiceTest.java`

**Interfaces:**
- Consumes: `EmployeeRepository.findById` 和员工状态。
- Produces: `AttendanceService.save(AttendanceForm)` 在同员工同日期记录存在时更新，否则新增；`findPage(Long employeeId, LocalDate from, LocalDate to, int page, int size)`。

- [ ] **Step 1: 编写重复日期更新测试**

```java
class AttendanceServiceTest {
    AttendanceRepository attendance = mock(AttendanceRepository.class);
    EmployeeRepository employees = mock(EmployeeRepository.class);
    AttendanceService service = new AttendanceService(attendance, employees);

    @Test
    void updatesExistingRecordForSameEmployeeAndDate() {
        Employee employee = mock(Employee.class);
        when(employee.getStatus()).thenReturn(EmployeeStatus.ACTIVE);
        when(employees.findById(3L)).thenReturn(Optional.of(employee));
        when(attendance.findByEmployeeAndDate(3L, LocalDate.of(2026, 11, 21)))
            .thenReturn(Optional.of(new AttendanceRecord(9L, 3L, LocalDate.of(2026, 11, 21),
                LocalTime.of(9, 0), null, AttendanceStatus.LATE, null)));

        service.save(new AttendanceForm(3L, LocalDate.of(2026, 11, 21),
            LocalTime.of(8, 30), LocalTime.of(18, 0), AttendanceStatus.NORMAL, "补录"));

        verify(attendance).update(eq(9L), any(AttendanceForm.class));
        verify(attendance, never()).create(any());
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=AttendanceServiceTest test`
Expected: FAIL，考勤服务不存在。

- [ ] **Step 3: 实现业务限制**

保存前确认员工存在且状态不是 `RESIGNED`；下班时间不得早于上班时间；缺勤记录允许上下班时间为空。状态枚举为 `NORMAL`、`LATE`、`EARLY_LEAVE`、`ABSENT`。

- [ ] **Step 4: 实现页面和权限**

- `GET /attendance` 按员工、起止日期筛选并分页。
- `GET /attendance/new`、`POST /attendance` 登记或更新考勤。
- HR 和管理员可维护全部考勤；普通员工只能访问 `/profile` 查看自己的考勤。
- JSP 表格显示员工、日期、上下班时间、状态和备注。

- [ ] **Step 5: 验证**

Run: `mvn -Dtest=AttendanceServiceTest test`
Expected: PASS。
人工验证：同员工同日期重复提交只有一条数据库记录；下班早于上班显示错误。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel/model/Attendance* src/main/java/com/fzgs/personnel/dto/AttendanceForm.java src/main/java/com/fzgs/personnel/repository/AttendanceRepository.java src/main/java/com/fzgs/personnel/service/AttendanceService.java src/main/java/com/fzgs/personnel/controller/AttendanceController.java src/main/webapp/WEB-INF/jsp/attendance src/test/java/com/fzgs/personnel/service/AttendanceServiceTest.java
git commit -m "feat: add attendance management"
```

---

### Task 8: 请假申请与审批

**Files:**
- Create: `model/LeaveStatus.java`
- Create: `model/LeaveRequest.java`
- Create: `dto/LeaveForm.java`
- Create: `repository/LeaveRepository.java`
- Create: `service/LeaveService.java`
- Create: `controller/LeaveController.java`
- Create: JSP leave 列表与表单页
- Create: `src/test/java/com/fzgs/personnel/service/LeaveServiceTest.java`
- Create: `src/test/java/com/fzgs/personnel/integration/LeaveFlowTest.java`

**Interfaces:**
- Consumes: `AppUser.employeeId`、`EmployeeStatus`。
- Produces: `LeaveService.submit(long employeeId, LeaveForm)`、`approve(long leaveId, long approverUserId, boolean approved, String comment)`、`findForEmployee(long employeeId)`、`findPending()`。

- [ ] **Step 1: 编写日期与重复审批测试**

```java
class LeaveServiceTest {
LeaveRepository repository = mock(LeaveRepository.class);
EmployeeRepository employees = mock(EmployeeRepository.class);
LeaveService service = new LeaveService(repository, employees);

@Test
void rejectsEndDateBeforeStartDate() {
    BusinessException error = assertThrows(BusinessException.class,
        () -> service.submit(3L, new LeaveForm("ANNUAL",
            LocalDate.of(2026, 11, 22), LocalDate.of(2026, 11, 21), "家中有事")));
    assertThat(error.getMessage()).isEqualTo("结束日期不能早于开始日期");
}

@Test
void rejectsApprovalOfProcessedRequest() {
    LeaveRequest request = mock(LeaveRequest.class);
    when(request.getStatus()).thenReturn(LeaveStatus.APPROVED);
    when(repository.findById(11L)).thenReturn(Optional.of(request));
    BusinessException error = assertThrows(BusinessException.class,
        () -> service.approve(11L, 2L, true, "同意"));
    assertThat(error.getMessage()).isEqualTo("该请假已处理");
}
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=LeaveServiceTest test`
Expected: FAIL，请假服务不存在。

- [ ] **Step 3: 实现状态流转**

提交时员工必须为 `ACTIVE`；类型支持 `ANNUAL`、`SICK`、`PERSONAL`、`OTHER`；新记录为 `PENDING`。审批仅接受 `PENDING`，更新为 `APPROVED` 或 `REJECTED`，同时保存审批人、时间和意见。

- [ ] **Step 4: 实现页面**

- `GET /leave` 根据角色显示个人申请或全部申请。
- `GET /leave/new`、`POST /leave` 供员工提交。
- `POST /leave/{id}/approval` 供 HR/管理员审批，`approved` 为布尔值。
- 待审批列表提供通过和驳回按钮；已处理记录只读显示审批意见。

- [ ] **Step 5: 验证角色和审批流程**

Run: `mvn -Dtest='LeaveServiceTest,LeaveFlowTest' test`
Expected: PASS。
人工验证：`employee` 提交后状态为待审批；`hr` 审批后普通员工不能再次处理；非法日期显示错误。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel/model/Leave* src/main/java/com/fzgs/personnel/dto/LeaveForm.java src/main/java/com/fzgs/personnel/repository/LeaveRepository.java src/main/java/com/fzgs/personnel/service/LeaveService.java src/main/java/com/fzgs/personnel/controller/LeaveController.java src/main/webapp/WEB-INF/jsp/leave src/test/java/com/fzgs/personnel
git commit -m "feat: add leave request approval"
```

---

### Task 9: 月度薪资

**Files:**
- Create: `model/PayrollStatus.java`
- Create: `model/PayrollRecord.java`
- Create: `dto/PayrollForm.java`
- Create: `repository/PayrollRepository.java`
- Create: `service/PayrollService.java`
- Create: `controller/PayrollController.java`
- Create: JSP payroll 列表与表单页
- Create: `src/test/java/com/fzgs/personnel/service/PayrollServiceTest.java`
- Create: `src/test/java/com/fzgs/personnel/integration/PayrollFlowTest.java`

**Interfaces:**
- Consumes: 在职员工、当前 HR 用户 ID。
- Produces: `PayrollService.generate(String payMonth, long createdBy): int`；`updateDraft(long id, PayrollForm)`；`markPaid(long id)`；`findByMonth(String payMonth)`。

- [ ] **Step 1: 编写工资计算和支付测试**

```java
class PayrollServiceTest {
PayrollRepository repository = mock(PayrollRepository.class);
EmployeeRepository employees = mock(EmployeeRepository.class);
PayrollService service = new PayrollService(repository, employees);

@Test
void calculatesNetSalary() {
    PayrollForm form = new PayrollForm(
        new BigDecimal("8000"), new BigDecimal("1000"), new BigDecimal("500"),
        new BigDecimal("200"), new BigDecimal("800"));
    assertThat(service.calculateNet(form)).isEqualByComparingTo("8500.00");
}

@Test
void rejectsEditingPaidPayroll() {
    PayrollRecord record = mock(PayrollRecord.class);
    when(record.getStatus()).thenReturn(PayrollStatus.PAID);
    when(repository.findById(7L)).thenReturn(Optional.of(record));
    BusinessException error = assertThrows(BusinessException.class,
        () -> service.updateDraft(7L, new PayrollForm(
            new BigDecimal("8000"), BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO)));
    assertThat(error.getMessage()).isEqualTo("已发放工资不可修改");
}
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=PayrollServiceTest test`
Expected: FAIL，薪资服务不存在。

- [ ] **Step 3: 实现生成与状态规则**

计算规则固定为：

```java
BigDecimal net = base.add(bonus).add(allowance)
    .subtract(deduction).subtract(socialSecurity);
```

批量生成只处理 `ACTIVE` 员工，已有月份工资单跳过并计数；为零条新增时返回“该月份工资单已生成”。`DRAFT` 可改，`PAID` 设置 `paid_at` 后只读。

- [ ] **Step 4: 实现页面**

- `GET /payroll` 按 `YYYY-MM` 筛选工资单。
- `POST /payroll/generate` 生成月度草稿。
- `GET /payroll/{id}/edit`、`POST /payroll/{id}` 编辑草稿。
- `POST /payroll/{id}/paid` 确认发放。
- 页面显示应发合计、扣款合计和实发工资，已发放记录不显示编辑按钮。

- [ ] **Step 5: 验证**

Run: `mvn -Dtest='PayrollServiceTest,PayrollFlowTest' test`
Expected: PASS。
人工验证：HR 生成当月工资、调整草稿、确认发放；再次生成不会产生重复记录。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel/model/Payroll* src/main/java/com/fzgs/personnel/dto/PayrollForm.java src/main/java/com/fzgs/personnel/repository/PayrollRepository.java src/main/java/com/fzgs/personnel/service/PayrollService.java src/main/java/com/fzgs/personnel/controller/PayrollController.java src/main/webapp/WEB-INF/jsp/payroll src/test/java/com/fzgs/personnel
git commit -m "feat: add monthly payroll management"
```

---

### Task 10: 首页、个人中心、错误页与统一界面

**Files:**
- Create: `repository/DashboardRepository.java`
- Create: `service/DashboardService.java`
- Create: `controller/DashboardController.java`
- Create: `controller/ProfileController.java`
- Create: `common/GlobalExceptionHandler.java`
- Create: `src/main/resources/static/css/app.css`
- Create: JSP dashboard、profile、layout 和 error 页面
- Create: `src/test/java/com/fzgs/personnel/service/DashboardServiceTest.java`

**Interfaces:**
- Consumes: 所有查询 Repository、Spring Security 当前用户。
- Produces: `DashboardService.summary(): DashboardSummary`，包含 `employeeCount`、`activeEmployeeCount`、`departmentCount`、`pendingLeaveCount`；`GET /dashboard` 和 `GET /profile` 页面。

- [ ] **Step 1: 编写统计测试**

```java
class DashboardServiceTest {
    @Test
    void returnsAllSummaryCounters() {
        DashboardRepository repository = mock(DashboardRepository.class);
        when(repository.summary()).thenReturn(new DashboardSummary(6, 5, 3, 2));
        assertThat(new DashboardService(repository).summary())
            .isEqualTo(new DashboardSummary(6, 5, 3, 2));
    }
}
```

- [ ] **Step 2: 运行测试并确认失败**

Run: `mvn -Dtest=DashboardServiceTest test`
Expected: FAIL，统计类和控制器不存在。

- [ ] **Step 3: 实现首页和个人中心**

- 首页查询员工总数、在职人数、部门数、待审批请假数。
- 展示最近 5 条考勤和当前月份工资汇总。
- 个人中心根据当前账号关联员工读取档案、最近考勤、请假和工资；资料编辑只允许修改电话、邮箱和地址。
- 管理员和 HR 没有关联员工时显示简化个人信息页。

- [ ] **Step 4: 完成统一样式和错误处理**

`layout.jsp` 使用 `c:choose` 按角色显示部门/岗位、员工、考勤、请假、工资菜单；包含 CSRF 隐藏字段的公共删除和审批表单由各页面显式提供。CSS 使用中性蓝灰配色、响应式侧栏和可横向滚动表格，不依赖外部 CDN。

`GlobalExceptionHandler` 处理：`BusinessException` 返回消息并保留表单；`MethodArgumentNotValidException` 返回字段错误；`AccessDeniedException` 显示 403；未捕获异常记录堆栈但页面只显示通用 500 信息。

- [ ] **Step 5: 运行全量测试和页面检查**

Run: `mvn test`
Expected: PASS。
Run: `mvn spring-boot:run`
Expected: 三个角色登录后菜单正确；首页数字与数据库一致；移动宽度下表格可横向浏览。

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/fzgs/personnel src/main/resources/static src/main/webapp/WEB-INF/jsp src/test/java/com/fzgs/personnel
git commit -m "feat: add dashboard and employee self-service"
```

---

### Task 11: GitHub Actions、README 与交付验收

**Files:**
- Create: `.github/workflows/ci.yml`
- Create: `README.md`
- Modify: `AGENTS.md`

**Interfaces:**
- Consumes: 已通过的 Maven 测试和应用启动命令。
- Produces: GitHub CI 流程、完整运行说明、演示账号和项目结构文档。

- [ ] **Step 1: 创建 CI 工作流**

```yaml
name: CI
on:
  push:
    branches: [main]
  pull_request:
jobs:
  verify:
    runs-on: ubuntu-latest
    services:
      mysql:
        image: mysql:8.4
        env:
          MYSQL_DATABASE: personnel_management
          MYSQL_USER: personnel
          MYSQL_PASSWORD: personnel_test
          MYSQL_ROOT_PASSWORD: root_test
        ports:
          - 3306:3306
        options: >-
          --health-cmd="mysqladmin ping -h localhost -u root -proot_test"
          --health-interval=5s
          --health-timeout=5s
          --health-retries=20
    env:
      CI_DB: 'true'
      DB_URL: 'jdbc:mysql://127.0.0.1:3306/personnel_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai'
      DB_USERNAME: personnel
      DB_PASSWORD: personnel_test
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven
      - name: Verify
        run: mvn -B verify
```

本地测试使用 Testcontainers 启动 MySQL 8；CI 中 `CI_DB=true` 让测试基类直接连接上面的 MySQL service。

- [ ] **Step 2: 编写 README**

README 必须包含：

```text
项目简介
功能模块
技术栈
目录结构
Docker MySQL 启动
应用启动
测试与打包
演示账号
数据库配置
常见问题
```

演示账号：

```text
admin / 123456       管理员
hr / 123456          人事专员
employee / 123456    普通员工
```

明确说明这些仅用于本地课程演示，部署时必须修改密码和数据库凭据。

- [ ] **Step 3: 更新 AGENTS.md**

将“尚无工具链”的说明改为实际命令：`docker compose up -d mysql`、`mvn spring-boot:run`、`mvn test`、`mvn clean package`；记录源码、JSP、迁移和测试目录。

- [ ] **Step 4: 执行最终验证**

Run: `docker compose down -v`
Run: `docker compose up -d mysql`
Run: `mvn -B verify`
Expected: 所有测试 PASS。
Run: `mvn clean package`
Expected: `target/personnel-management.war` 生成。
Run: `java -jar target/personnel-management.war`
Expected: 应用启动并可登录。

- [ ] **Step 5: 检查敏感信息和变更**

Run: `rg -n 'password=.*[^$]|BEGIN (RSA|OPENSSH|PRIVATE)' . --glob '!target/**'`
Expected: 仅 README 演示账号和本地开发默认凭据，无真实密钥。
Run: `git diff --check`
Expected: 无空白错误。

- [ ] **Step 6: 提交**

```bash
git add .github/workflows/ci.yml README.md AGENTS.md
git commit -m "ci: add verification workflow and project docs"
```

---

## 最终验收清单

- [ ] 7 张业务表、外键、唯一约束和演示数据由 Flyway 自动创建。
- [ ] 三个角色均可登录，未授权页面返回 403。
- [ ] 部门和岗位支持新增、编辑和启停。
- [ ] 员工支持筛选、分页、新增、编辑和离职处理。
- [ ] 考勤同员工同日期保持唯一，并可按日期查询。
- [ ] 请假可提交、审批、驳回，已处理记录不可重复审批。
- [ ] 工资可按月生成、编辑草稿并确认发放。
- [ ] 首页统计和个人中心正常显示。
- [ ] `mvn -B verify` 和 `mvn clean package` 成功。
- [ ] GitHub Actions 在 `main` 推送和 Pull Request 上运行。
