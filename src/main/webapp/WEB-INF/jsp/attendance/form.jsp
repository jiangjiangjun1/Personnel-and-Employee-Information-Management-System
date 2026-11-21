<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>登记考勤 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input, select { width: 280px; padding: 6px; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>登记考勤</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <form action="${ctx}/attendance" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <label for="employeeId">员工</label>
    <select id="employeeId" name="employeeId" required>
      <option value="">请选择</option>
      <c:forEach var="e" items="${employees}">
        <option value="${e.id}" <c:if test="${form.employeeId == e.id}">selected</c:if>>${e.employeeNo} ${e.name}</option>
      </c:forEach>
    </select>
    <label for="workDate">考勤日期</label>
    <input id="workDate" type="date" name="workDate"
           value="${empty workDate ? form.workDate : workDate}" required>
    <label for="checkIn">签到时间</label>
    <input id="checkIn" type="time" name="checkIn"
           value="${empty checkIn ? form.checkIn : checkIn}">
    <label for="checkOut">签退时间</label>
    <input id="checkOut" type="time" name="checkOut"
           value="${empty checkOut ? form.checkOut : checkOut}">
    <label for="status">考勤状态</label>
    <select id="status" name="status">
      <option value="NORMAL" <c:if test="${form.status.name() == 'NORMAL'}">selected</c:if>>正常</option>
      <option value="LATE" <c:if test="${form.status.name() == 'LATE'}">selected</c:if>>迟到</option>
      <option value="EARLY_LEAVE" <c:if test="${form.status.name() == 'EARLY_LEAVE'}">selected</c:if>>早退</option>
      <option value="ABSENT" <c:if test="${form.status.name() == 'ABSENT'}">selected</c:if>>缺勤</option>
    </select>
    <label for="remark">备注</label>
    <input id="remark" name="remark" value="${form.remark}">
    <p><button type="submit">保存</button> <a href="${ctx}/attendance">返回</a></p>
  </form>
</body>
</html>
