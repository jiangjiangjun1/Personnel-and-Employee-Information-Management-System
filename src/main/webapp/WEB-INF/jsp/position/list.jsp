<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="岗位管理"/>
</jsp:include>
  <h1 class="page-title">岗位管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <div class="toolbar">
    <a class="button-link" href="${ctx}/positions/new">新增岗位</a>
  </div>
  <div class="table-wrap">
  <table>
    <thead>
      <tr><th>编码</th><th>名称</th><th>职级</th><th>描述</th><th>状态</th><th>操作</th></tr>
    </thead>
    <tbody>
      <c:forEach var="p" items="${positions}">
        <tr>
          <td><c:out value="${p.code}"/></td>
          <td><c:out value="${p.name}"/></td>
          <td><c:out value="${p.level}"/></td>
          <td><c:out value="${p.description}"/></td>
          <td><c:out value="${p.status.name() == 'ACTIVE' ? '启用' : '停用'}"/></td>
          <td class="actions">
            <a href="${ctx}/positions/${p.id}/edit">编辑</a>
            <form action="${ctx}/positions/${p.id}/status" method="post" style="display:inline">
              <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
              <input type="hidden" name="status" value="${p.status.name() == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}">
              <button type="submit">${p.status.name() == 'ACTIVE' ? '停用' : '启用'}</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>
<jsp:include page="../fragments/page-end.jsp"/>
