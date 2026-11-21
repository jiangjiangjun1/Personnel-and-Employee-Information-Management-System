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

create index idx_employee_department on employees(department_id);
create index idx_employee_position on employees(position_id);
create index idx_attendance_date on attendance_records(work_date);
create index idx_leave_status on leave_requests(status);
create index idx_payroll_month on payroll_records(pay_month);
