<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>账号表单 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input, select { width: 280px; padding: 6px; }
    .form-error { color: #b91c1c; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>${empty id ? '新增账号' : '编辑账号'}</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <c:choose>
    <c:when test="${empty id}">
      <c:set var="formAction" value="${ctx}/users"/>
    </c:when>
    <c:otherwise>
      <c:set var="formAction" value="${ctx}/users/${id}"/>
    </c:otherwise>
  </c:choose>

  <form action="${formAction}" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <label for="username">用户名</label>
    <input id="username" name="username" value="${form.username}" required>
    <label for="displayName">显示名</label>
    <input id="displayName" name="displayName" value="${form.displayName}" required>
    <label for="role">角色</label>
    <select id="role" name="role">
      <c:forEach var="r" items="${roles}">
        <option value="${r.name()}" <c:if test="${form.role == r}">selected</c:if>>${r.name()}</option>
      </c:forEach>
    </select>
    <label for="employeeId">关联员工</label>
    <select id="employeeId" name="employeeId">
      <option value="">不绑定员工</option>
      <c:forEach var="e" items="${employees}">
        <option value="${e.id}" <c:if test="${form.employeeId == e.id}">selected</c:if>>
          ${e.employeeNo} - ${e.name}
        </option>
      </c:forEach>
    </select>
    <label for="enabled">状态</label>
    <select id="enabled" name="enabled">
      <option value="true" <c:if test="${form.enabled}">selected</c:if>>启用</option>
      <option value="false" <c:if test="${not form.enabled}">selected</c:if>>停用</option>
    </select>
    <label for="password">密码</label>
    <input id="password" type="password" name="password" autocomplete="new-password"
           <c:if test="${empty id}">required</c:if>>
    <c:if test="${not empty id}"><p class="muted">留空表示不修改密码</p></c:if>
    <p><button type="submit">保存</button> <a href="${ctx}/users">返回</a></p>
  </form>
</body>
</html>
