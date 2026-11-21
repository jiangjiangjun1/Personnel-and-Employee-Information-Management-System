<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>员工表单 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    label { display: block; margin-top: 12px; }
    input, select { width: 280px; padding: 6px; }
    .form-error { color: #b91c1c; }
  </style>
</head>
<body>
  <h1>${empty id ? '新增员工' : '编辑员工'}</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>

  <c:choose>
    <c:when test="${empty id}">
      <c:set var="formAction" value="${ctx}/employees"/>
    </c:when>
    <c:otherwise>
      <c:set var="formAction" value="${ctx}/employees/${id}"/>
    </c:otherwise>
  </c:choose>

  <form action="${fn:escapeXml(formAction)}" method="post">
    <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
    <label for="employeeNo">工号</label>
    <input id="employeeNo" name="employeeNo" value="${fn:escapeXml(form.employeeNo)}" required>
    <label for="name">姓名</label>
    <input id="name" name="name" value="${fn:escapeXml(form.name)}" required>
    <label for="gender">性别</label>
    <select id="gender" name="gender" required>
      <option value="男" <c:if test="${form.gender == '男'}">selected</c:if>>男</option>
      <option value="女" <c:if test="${form.gender == '女'}">selected</c:if>>女</option>
    </select>
    <label for="birthDate">出生日期</label>
    <input id="birthDate" type="date" name="birthDate" value="${fn:escapeXml(form.birthDate)}">
    <label for="phone">联系电话</label>
    <input id="phone" name="phone" value="${fn:escapeXml(form.phone)}">
    <label for="email">邮箱</label>
    <input id="email" type="email" name="email" value="${fn:escapeXml(form.email)}">
    <label for="address">联系地址</label>
    <input id="address" name="address" value="${fn:escapeXml(form.address)}">
    <label for="hireDate">入职日期</label>
    <input id="hireDate" type="date" name="hireDate" value="${fn:escapeXml(form.hireDate)}" required>
    <label for="baseSalary">基本工资</label>
    <input id="baseSalary" type="number" step="0.01" min="0" name="baseSalary" value="${fn:escapeXml(form.baseSalary)}" required>
    <label for="departmentId">部门</label>
    <select id="departmentId" name="departmentId" required>
      <option value="">请选择</option>
      <c:forEach var="d" items="${departments}">
        <option value="${d.id}" <c:if test="${form.departmentId == d.id}">selected</c:if>><c:out value="${d.name}"/></option>
      </c:forEach>
    </select>
    <label for="positionId">岗位</label>
    <select id="positionId" name="positionId" required>
      <option value="">请选择</option>
      <c:forEach var="p" items="${positions}">
        <option value="${p.id}" <c:if test="${form.positionId == p.id}">selected</c:if>><c:out value="${p.name}"/></option>
      </c:forEach>
    </select>
    <label for="status">状态</label>
    <select id="status" name="status">
      <option value="ACTIVE" <c:if test="${form.status.name() == 'ACTIVE'}">selected</c:if>>在职</option>
      <option value="INACTIVE" <c:if test="${form.status.name() == 'INACTIVE'}">selected</c:if>>停用</option>
      <option value="RESIGNED" <c:if test="${form.status.name() == 'RESIGNED'}">selected</c:if>>离职</option>
    </select>
    <p><button type="submit">保存</button> <a href="${ctx}/employees">返回</a></p>
  </form>
</body>
</html>
