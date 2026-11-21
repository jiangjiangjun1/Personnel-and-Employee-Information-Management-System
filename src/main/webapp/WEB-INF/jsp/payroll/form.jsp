<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>编辑工资单 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input { width: 300px; padding: 6px; }
    .form-error { color: #b91c1c; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>编辑工资单</h1>
  <p class="muted">员工：${record.employeeNo} ${record.employeeName}，月份：${record.payMonth}</p>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <form action="${ctx}/payroll/${record.id}" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <label for="baseSalary">基本工资</label>
    <input id="baseSalary" type="number" step="0.01" name="baseSalary"
           value="${empty rawBase ? record.baseSalary : rawBase}" required>
    <label for="bonus">奖金</label>
    <input id="bonus" type="number" step="0.01" name="bonus"
           value="${empty rawBonus ? record.bonus : rawBonus}" required>
    <label for="allowance">补贴</label>
    <input id="allowance" type="number" step="0.01" name="allowance"
           value="${empty rawAllowance ? record.allowance : rawAllowance}" required>
    <label for="deduction">扣款</label>
    <input id="deduction" type="number" step="0.01" name="deduction"
           value="${empty rawDeduction ? record.deduction : rawDeduction}" required>
    <label for="socialSecurity">社保</label>
    <input id="socialSecurity" type="number" step="0.01" name="socialSecurity"
           value="${empty rawSocial ? record.socialSecurity : rawSocial}" required>
    <p><button type="submit">保存</button> <a href="${ctx}/payroll?payMonth=${record.payMonth}">返回</a></p>
  </form>
</body>
</html>
