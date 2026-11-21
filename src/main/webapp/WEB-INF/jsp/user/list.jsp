<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>账号管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    form.inline { display: inline; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>账号管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <p><a href="${ctx}/users/new">新增账号</a></p>
  <table>
    <thead>
      <tr>
        <th>用户名</th><th>显示名</th><th>角色</th><th>关联员工</th><th>状态</th><th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="u" items="${users}">
        <tr>
          <td><c:out value="${u.username}"/></td>
          <td><c:out value="${u.displayName}"/></td>
          <td><c:out value="${u.role.name()}"/></td>
          <td>
            <c:choose>
              <c:when test="${u.employeeId != null}"><c:out value="${employeeNames[u.employeeId]}"/></c:when>
              <c:otherwise>未关联</c:otherwise>
            </c:choose>
          </td>
          <td><c:out value="${u.enabled ? '启用' : '停用'}"/></td>
          <td>
            <a href="${ctx}/users/${u.id}/edit">编辑</a>
            <form class="inline" action="${ctx}/users/${u.id}/status" method="post">
              <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
              <input type="hidden" name="enabled" value="${u.enabled ? 'false' : 'true'}">
              <button type="submit">${u.enabled ? '停用' : '启用'}</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
