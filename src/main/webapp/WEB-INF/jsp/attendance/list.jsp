<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>考勤管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    .form-error { color: #b91c1c; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>考勤管理</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <form method="get" action="${ctx}/attendance">
    <label>员工
      <select name="employeeId">
        <option value="">全部</option>
        <c:forEach var="e" items="${employees}">
          <option value="${e.id}" <c:if test="${employeeId == e.id}">selected</c:if>>${e.employeeNo} ${e.name}</option>
        </c:forEach>
      </select>
    </label>
    <label>开始日期 <input type="date" name="from" value="${from}"></label>
    <label>结束日期 <input type="date" name="to" value="${to}"></label>
    <button type="submit">查询</button>
    <a href="${ctx}/attendance/new">登记考勤</a>
  </form>

  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>日期</th><th>签到</th><th>签退</th><th>状态</th><th>备注</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="a" items="${result.items}">
        <tr>
          <td>${a.employeeNo}</td>
          <td>${a.employeeName}</td>
          <td>${a.workDate}</td>
          <td>${a.checkIn}</td>
          <td>${a.checkOut}</td>
          <td>
            ${a.status.name() == 'NORMAL' ? '正常'
              : a.status.name() == 'LATE' ? '迟到'
              : a.status.name() == 'EARLY_LEAVE' ? '早退'
              : a.status.name() == 'ABSENT' ? '缺勤' : a.status.name()}
          </td>
          <td>${a.remark}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <p class="muted">
    共 ${result.total} 条，第 ${result.page}/${result.totalPages} 页
    <c:if test="${result.page > 1}">
      <a href="${ctx}/attendance?employeeId=${employeeId}&from=${from}&to=${to}&page=${result.page - 1}">上一页</a>
    </c:if>
    <c:if test="${result.page < result.totalPages}">
      <a href="${ctx}/attendance?employeeId=${employeeId}&from=${from}&to=${to}&page=${result.page + 1}">下一页</a>
    </c:if>
  </p>
</body>
</html>
