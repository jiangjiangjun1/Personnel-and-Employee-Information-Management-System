<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="账号管理"/>
</jsp:include>
  <h1 class="page-title">账号管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <div class="toolbar">
    <a class="button-link" href="${ctx}/users/new">新增账号</a>
  </div>
  <div class="table-wrap">
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
  </div>
<jsp:include page="../fragments/page-end.jsp"/>
