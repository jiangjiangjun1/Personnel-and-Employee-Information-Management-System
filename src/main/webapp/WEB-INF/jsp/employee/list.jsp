<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>员工档案 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    form.inline { display: inline; }
    .form-error { color: #b91c1c; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>员工档案</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>

  <form method="get" action="${ctx}/employees">
    <label>关键字 <input type="text" name="keyword" value="${keyword}"></label>
    <label>部门
      <select name="departmentId">
        <option value="">全部</option>
        <c:forEach var="d" items="${departments}">
          <option value="${d.id}" <c:if test="${departmentId == d.id}">selected</c:if>>${d.name}</option>
        </c:forEach>
      </select>
    </label>
    <label>状态
      <select name="status">
        <option value="">全部</option>
        <c:forEach var="s" items="${statuses}">
          <option value="${s.name()}" <c:if test="${status != null and status.name() == s.name()}">selected</c:if>>${s.name()}</option>
        </c:forEach>
      </select>
    </label>
    <button type="submit">查询</button>
    <a href="${ctx}/employees/new">新增员工</a>
  </form>

  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>性别</th><th>部门</th><th>岗位</th>
        <th>入职日期</th><th>基本工资</th><th>状态</th><th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="e" items="${result.items}">
        <tr>
          <td>${e.employeeNo}</td>
          <td>${e.name}</td>
          <td>${e.gender}</td>
          <td>${e.departmentName}</td>
          <td>${e.positionName}</td>
          <td>${e.hireDate}</td>
          <td>${e.baseSalary}</td>
          <td>${e.status.name()}</td>
          <td class="actions">
            <a href="${ctx}/employees/${e.id}/edit">编辑</a>
            <form class="inline" action="${ctx}/employees/${e.id}/status" method="post">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
              <select name="status">
                <c:forEach var="s" items="${statuses}">
                  <option value="${s.name()}" <c:if test="${e.status == s}">selected</c:if>>${s.name()}</option>
                </c:forEach>
              </select>
              <button type="submit">更新状态</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <p class="muted">
    共 ${result.total} 条，第 ${result.page}/${result.totalPages} 页
    <c:if test="${result.page > 1}">
      <a href="${ctx}/employees?keyword=${keyword}&departmentId=${departmentId}&status=${status}&page=${result.page - 1}">上一页</a>
    </c:if>
    <c:if test="${result.page < result.totalPages}">
      <a href="${ctx}/employees?keyword=${keyword}&departmentId=${departmentId}&status=${status}&page=${result.page + 1}">下一页</a>
    </c:if>
  </p>
</body>
</html>
