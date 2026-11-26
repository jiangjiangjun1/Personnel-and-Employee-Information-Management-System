<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="部门管理"/>
</jsp:include>
  <h1 class="page-title">部门管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <div class="toolbar">
    <a class="button-link" href="${ctx}/departments/new">新增部门</a>
  </div>
  <div class="table-wrap">
  <table>
    <thead>
      <tr><th>编码</th><th>名称</th><th>负责人</th><th>联系电话</th><th>状态</th><th>操作</th></tr>
    </thead>
    <tbody>
      <c:forEach var="d" items="${departments}">
        <tr>
          <td><c:out value="${d.code}"/></td>
          <td><c:out value="${d.name}"/></td>
          <td><c:out value="${d.manager}"/></td>
          <td><c:out value="${d.phone}"/></td>
          <td><c:out value="${d.status.name() == 'ACTIVE' ? '启用' : '停用'}"/></td>
          <td class="actions">
            <a href="${ctx}/departments/${d.id}/edit">编辑</a>
            <form action="${ctx}/departments/${d.id}/status" method="post" style="display:inline">
              <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
              <input type="hidden" name="status" value="${d.status.name() == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
              <button type="submit">${d.status.name() == 'ACTIVE' ? '停用' : '启用'}</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>
<jsp:include page="../fragments/page-end.jsp"/>
