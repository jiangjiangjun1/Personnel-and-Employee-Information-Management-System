<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>工资管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    .form-error { color: #b91c1c; }
    .form-success { color: #15803d; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>工资管理</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>
  <c:if test="${not empty message}"><p class="form-success">${message}</p></c:if>

  <form action="${ctx}/payroll" method="get" style="display:inline">
    <label for="payMonth">工资月份</label>
    <input id="payMonth" type="month" name="payMonth" value="${payMonth}">
    <button type="submit">查询</button>
  </form>
  <form action="${ctx}/payroll/generate" method="post" style="display:inline; margin-left:12px">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <input type="hidden" name="payMonth" value="${payMonth}">
    <button type="submit">生成当月工资单</button>
  </form>

  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>月份</th><th>基本工资</th><th>奖金</th>
        <th>补贴</th><th>扣款</th><th>社保</th><th>实发工资</th>
        <th>状态</th><th>发放时间</th><th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="record" items="${records}">
        <tr>
          <td>${record.employeeNo}</td>
          <td>${record.employeeName}</td>
          <td>${record.payMonth}</td>
          <td>${record.baseSalary}</td>
          <td>${record.bonus}</td>
          <td>${record.allowance}</td>
          <td>${record.deduction}</td>
          <td>${record.socialSecurity}</td>
          <td>${record.netSalary}</td>
          <td>${record.status.name() == 'DRAFT' ? '草稿' : '已发放'}</td>
          <td>${empty record.paidAt ? '' : record.paidAt}</td>
          <td>
            <c:if test="${record.status.name() == 'DRAFT'}">
              <a href="${ctx}/payroll/${record.id}/edit">编辑</a>
              <form action="${ctx}/payroll/${record.id}/paid" method="post" style="display:inline">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button type="submit">确认发放</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  <c:if test="${empty records}"><p class="muted">暂无工资记录</p></c:if>
</body>
</html>
