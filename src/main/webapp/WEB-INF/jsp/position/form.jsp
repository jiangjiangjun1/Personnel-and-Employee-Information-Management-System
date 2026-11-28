<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="岗位表单"/>
</jsp:include>
  <h1 class="page-title">${empty id ? '新增岗位' : '编辑岗位'}</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <c:choose>
    <c:when test="${empty id}">
      <c:set var="formAction" value="${ctx}/positions"/>
    </c:when>
    <c:otherwise>
      <c:set var="formAction" value="${ctx}/positions/${id}"/>
    </c:otherwise>
  </c:choose>
  <form class="card page-form" action="${fn:escapeXml(formAction)}" method="post">
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
    <p class="form-actions"><button type="submit">保存</button> <a href="${ctx}/positions">返回</a></p>
  </form>
<jsp:include page="../fragments/page-end.jsp"/>
