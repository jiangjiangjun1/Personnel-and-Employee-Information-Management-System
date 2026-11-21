<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>提交请假 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input, select, textarea { width: 300px; padding: 6px; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>提交请假</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <form action="${ctx}/leave" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <label for="leaveType">请假类型</label>
    <select id="leaveType" name="leaveType">
      <option value="ANNUAL" <c:if test="${form.leaveType == 'ANNUAL'}">selected</c:if>>年假</option>
      <option value="SICK" <c:if test="${form.leaveType == 'SICK'}">selected</c:if>>病假</option>
      <option value="PERSONAL" <c:if test="${form.leaveType == 'PERSONAL'}">selected</c:if>>事假</option>
      <option value="OTHER" <c:if test="${form.leaveType == 'OTHER'}">selected</c:if>>其他</option>
    </select>
    <label for="startDate">开始日期</label>
    <input id="startDate" type="date" name="startDate"
           value="${empty startDate ? form.startDate : startDate}" required>
    <label for="endDate">结束日期</label>
    <input id="endDate" type="date" name="endDate"
           value="${empty endDate ? form.endDate : endDate}" required>
    <label for="reason">请假原因</label>
    <textarea id="reason" name="reason" required>${form.reason}</textarea>
    <p><button type="submit">提交</button> <a href="${ctx}/leave">返回</a></p>
  </form>
</body>
</html>
