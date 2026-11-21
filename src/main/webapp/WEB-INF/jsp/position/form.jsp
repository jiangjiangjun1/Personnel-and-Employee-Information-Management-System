<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>岗位表单 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input, select, textarea { width: 300px; padding: 6px; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>${empty id ? '新增岗位' : '编辑岗位'}</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <c:choose>
    <c:when test="${empty id}">
      <c:set var="formAction" value="${ctx}/positions"/>
    </c:when>
    <c:otherwise>
      <c:set var="formAction" value="${ctx}/positions/${id}"/>
    </c:otherwise>
  </c:choose>
  <form action="${fn:escapeXml(formAction)}" method="post">
    <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
    <label for="code">编码</label>
    <input id="code" name="code" value="${fn:escapeXml(form.code)}" required>
    <label for="name">名称</label>
    <input id="name" name="name" value="${fn:escapeXml(form.name)}" required>
    <label for="level">职级</label>
    <input id="level" name="level" value="${fn:escapeXml(form.level)}">
    <label for="description">描述</label>
    <textarea id="description" name="description"><c:out value="${form.description}"/></textarea>
    <label for="status">状态</label>
    <select id="status" name="status">
      <option value="ACTIVE" <c:if test="${form.status.name() == 'ACTIVE'}">selected</c:if>>启用</option>
      <option value="INACTIVE" <c:if test="${form.status.name() == 'INACTIVE'}">selected</c:if>>停用</option>
    </select>
    <p><button type="submit">保存</button> <a href="${ctx}/positions">返回</a></p>
  </form>
</body>
</html>
