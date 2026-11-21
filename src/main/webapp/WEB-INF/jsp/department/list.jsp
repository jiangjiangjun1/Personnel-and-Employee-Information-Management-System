<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>部门管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    .actions a, .actions button { margin-right: 8px; }
  </style>
</head>
<body>
  <h1>部门管理</h1>
  <p><a href="${ctx}/departments/new">新增部门</a></p>
  <table>
    <thead>
      <tr><th>编码</th><th>名称</th><th>负责人</th><th>联系电话</th><th>状态</th><th>操作</th></tr>
    </thead>
    <tbody>
      <c:forEach var="d" items="${departments}">
        <tr>
          <td>${d.code}</td>
          <td>${d.name}</td>
          <td>${d.manager}</td>
          <td>${d.phone}</td>
          <td>${d.status.name() == 'ACTIVE' ? '启用' : '停用'}</td>
          <td class="actions">
            <a href="${ctx}/departments/${d.id}/edit">编辑</a>
            <form action="${ctx}/departments/${d.id}/status" method="post" style="display:inline">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
              <input type="hidden" name="status" value="${d.status.name() == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
              <button type="submit">${d.status.name() == 'ACTIVE' ? '停用' : '启用'}</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
