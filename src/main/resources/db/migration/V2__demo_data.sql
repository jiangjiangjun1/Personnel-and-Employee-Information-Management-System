insert into departments (id, code, name, manager, phone, status) values
  (1, 'HR', '人力资源部', '王芳', '13800000001', 'ACTIVE'),
  (2, 'TECH', '技术部', '李强', '13800000002', 'ACTIVE'),
  (3, 'FIN', '财务部', '赵敏', '13800000003', 'ACTIVE');

insert into positions (id, code, name, level, description, status) values
  (1, 'DEV', '开发工程师', 'P5', '负责业务系统开发', 'ACTIVE'),
  (2, 'QA', '测试工程师', 'P4', '负责质量保障', 'ACTIVE'),
  (3, 'HRBP', '人事专员', 'P3', '负责员工人事事务', 'ACTIVE'),
  (4, 'FINANALYST', '财务专员', 'P4', '负责薪资核算', 'ACTIVE');

insert into employees (id, employee_no, name, gender, birth_date, phone, email, address, hire_date, base_salary, status, department_id, position_id) values
  (1, 'E1001', '王芳', '女', '1988-03-12', '13800000001', 'wangfang@example.com', '福州市鼓楼区', '2018-04-01', 9000.00, 'ACTIVE', 1, 3),
  (2, 'E1002', '李强', '男', '1986-07-22', '13800000002', 'liqiang@example.com', '福州市台江区', '2019-06-15', 18000.00, 'ACTIVE', 2, 1),
  (3, 'E1003', '陈晨', '女', '1995-11-08', '13800000005', 'chenchen@example.com', '福州市仓山区', '2022-03-01', 8000.00, 'ACTIVE', 2, 1),
  (4, 'E1004', '刘洋', '男', '1993-01-30', '13800000006', 'liuyang@example.com', '福州市晋安区', '2021-09-10', 7000.00, 'ACTIVE', 2, 2),
  (5, 'E1005', '孙悦', '女', '1997-05-18', '13800000007', 'sunyue@example.com', '福州市马尾区', '2023-07-01', 6500.00, 'ACTIVE', 3, 4),
  (6, 'E1006', '周凯', '男', '1990-12-02', '13800000008', 'zhoukai@example.com', '福州市长乐区', '2020-02-14', 11000.00, 'INACTIVE', 2, 1);

insert into app_users (id, username, password_hash, display_name, role, enabled, employee_id) values
  (1, 'admin', '$2y$10$c4.Jr2RIKrWeHNlCm1DQqOkZPWESRw7/uWrlgxH0Ts4irHbeJrUZa', '系统管理员', 'ADMIN', true, null),
  (2, 'hr', '$2y$10$c4.Jr2RIKrWeHNlCm1DQqOkZPWESRw7/uWrlgxH0Ts4irHbeJrUZa', '人事专员', 'HR', true, null),
  (3, 'employee', '$2y$10$c4.Jr2RIKrWeHNlCm1DQqOkZPWESRw7/uWrlgxH0Ts4irHbeJrUZa', '陈晨', 'EMPLOYEE', true, 3);

insert into attendance_records (id, employee_id, work_date, check_in, check_out, status, remark) values
  (1, 3, '2026-11-20', '09:05:00', '18:10:00', 'LATE', '早高峰迟到'),
  (2, 4, '2026-11-20', '08:50:00', '18:00:00', 'NORMAL', null),
  (3, 5, '2026-11-20', '08:55:00', '17:45:00', 'NORMAL', null);

insert into leave_requests (id, employee_id, leave_type, start_date, end_date, reason, status, approver_id, approved_at, approver_comment) values
  (1, 4, 'SICK', '2026-11-18', '2026-11-18', '身体不适就医', 'APPROVED', 2, '2026-11-17 10:00:00', '同意'),
  (2, 5, 'ANNUAL', '2026-11-25', '2026-11-26', '年假回乡', 'PENDING', null, null, null);

insert into payroll_records (id, employee_id, pay_month, base_salary, bonus, allowance, deduction, social_security, net_salary, status, paid_at, created_by) values
  (1, 3, '2026-11', 8000.00, 500.00, 300.00, 100.00, 800.00, 7900.00, 'PAID', '2026-11-10 14:00:00', 2),
  (2, 4, '2026-11', 7000.00, 0.00, 200.00, 0.00, 700.00, 6500.00, 'DRAFT', null, 2);
