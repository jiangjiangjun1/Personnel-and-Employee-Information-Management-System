<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="员工档案"/>
</jsp:include>
  <h1 class="page-title">员工档案</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>

  <form class="toolbar" method="get" action="${ctx}/employees">
    <label>关键字 <input type="text" name="keyword" value="${fn:escapeXml(keyword)}"></label>
    <label>部门
      <select name="departmentId">
        <option value="">全部</option>
        <c:forEach var="d" items="${departments}">
          <option value="${d.id}" <c:if test="${departmentId == d.id}">selected</c:if>><c:out value="${d.name}"/></option>
        </c:forEach>
      </select>
    </label>
    <label>状态
      <select name="status">
        <option value="">全部</option>
        <c:forEach var="s" items="${statuses}">
          <option value="${s.name()}" <c:if test="${status != null and status.name() == s.name()}">selected</c:if>><c:out value="${s.name()}"/></option>
        </c:forEach>
      </select>
    </label>
    <button type="submit">查询</button>
    <a class="button-link" href="${ctx}/employees/new">新增员工</a>
  </form>

  <div class="table-wrap">
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
          <td><c:out value="${e.employeeNo}"/></td>
          <td><c:out value="${e.name}"/></td>
          <td><c:out value="${e.gender}"/></td>
          <td><c:out value="${e.departmentName}"/></td>
          <td><c:out value="${e.positionName}"/></td>
          <td><c:out value="${e.hireDate}"/></td>
          <td><c:out value="${e.baseSalary}"/></td>
          <td><c:out value="${e.status.name()}"/></td>
          <td class="actions">
            <a href="${ctx}/employees/${e.id}/edit">编辑</a>
            <form class="inline action-form" action="${ctx}/employees/${e.id}/status" method="post">
              <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
              <select name="status">
                <c:forEach var="s" items="${statuses}">
                  <option value="${s.name()}" <c:if test="${e.status == s}">selected</c:if>><c:out value="${s.name()}"/></option>
                </c:forEach>
              </select>
              <button type="submit">更新状态</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>

  <p class="muted">
    共 <c:out value="${result.total}"/> 条，第 <c:out value="${result.page}"/>/<c:out value="${result.totalPages}"/> 页
    <c:if test="${result.page > 1}">
      <c:url var="previousUrl" value="/employees">
        <c:param name="keyword" value="${keyword}"/>
        <c:param name="departmentId" value="${departmentId}"/>
        <c:param name="status" value="${status}"/>
        <c:param name="page" value="${result.page - 1}"/>
      </c:url>
      <a href="${fn:escapeXml(previousUrl)}">上一页</a>
    </c:if>
    <c:if test="${result.page < result.totalPages}">
      <c:url var="nextUrl" value="/employees">
        <c:param name="keyword" value="${keyword}"/>
        <c:param name="departmentId" value="${departmentId}"/>
        <c:param name="status" value="${status}"/>
        <c:param name="page" value="${result.page + 1}"/>
      </c:url>
      <a href="${fn:escapeXml(nextUrl)}">下一页</a>
    </c:if>
  </p>
<jsp:include page="../fragments/page-end.jsp"/>
