<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>岗位管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    .actions a, .actions button { margin-right: 8px; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>岗位管理</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>
  <p><a href="${ctx}/positions/new">新增岗位</a></p>
  <table>
    <thead>
      <tr><th>编码</th><th>名称</th><th>职级</th><th>描述</th><th>状态</th><th>操作</th></tr>
    </thead>
    <tbody>
      <c:forEach var="p" items="${positions}">
        <tr>
          <td>${p.code}</td>
          <td>${p.name}</td>
          <td>${p.level}</td>
          <td>${p.description}</td>
          <td>${p.status.name() == 'ACTIVE' ? '启用' : '停用'}</td>
          <td class="actions">
            <a href="${ctx}/positions/${p.id}/edit">编辑</a>
            <form action="${ctx}/positions/${p.id}/status" method="post" style="display:inline">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
              <input type="hidden" name="status" value="${p.status.name() == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
              <button type="submit">${p.status.name() == 'ACTIVE' ? '停用' : '启用'}</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
