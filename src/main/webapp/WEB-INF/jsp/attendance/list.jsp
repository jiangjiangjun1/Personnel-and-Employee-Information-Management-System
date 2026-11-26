<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="考勤管理"/>
</jsp:include>
  <h1 class="page-title">考勤管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>

  <form class="toolbar" method="get" action="${ctx}/attendance">
    <label>员工
      <select name="employeeId">
        <option value="">全部</option>
        <c:forEach var="e" items="${employees}">
          <option value="${e.id}" <c:if test="${employeeId == e.id}">selected</c:if>><c:out value="${e.employeeNo}"/> <c:out value="${e.name}"/></option>
        </c:forEach>
      </select>
    </label>
    <label>开始日期 <input type="date" name="from" value="${fn:escapeXml(from)}"></label>
    <label>结束日期 <input type="date" name="to" value="${fn:escapeXml(to)}"></label>
    <button type="submit">查询</button>
    <a class="button-link" href="${ctx}/attendance/new">登记考勤</a>
  </form>

  <div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>日期</th><th>签到</th><th>签退</th><th>状态</th><th>备注</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="a" items="${result.items}">
        <tr>
          <td><c:out value="${a.employeeNo}"/></td>
          <td><c:out value="${a.employeeName}"/></td>
          <td><c:out value="${a.workDate}"/></td>
          <td><c:out value="${a.checkIn}"/></td>
          <td><c:out value="${a.checkOut}"/></td>
          <td>
            <c:out value="${a.status.name() == 'NORMAL' ? '正常'
              : a.status.name() == 'LATE' ? '迟到'
              : a.status.name() == 'EARLY_LEAVE' ? '早退'
              : a.status.name() == 'ABSENT' ? '缺勤' : a.status.name()}"/>
          </td>
          <td><c:out value="${a.remark}"/></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>

  <p class="muted">
    共 <c:out value="${result.total}"/> 条，第 <c:out value="${result.page}"/>/<c:out value="${result.totalPages}"/> 页
    <c:if test="${result.page > 1}">
      <c:url var="previousUrl" value="/attendance">
        <c:param name="employeeId" value="${employeeId}"/>
        <c:param name="from" value="${from}"/>
        <c:param name="to" value="${to}"/>
        <c:param name="page" value="${result.page - 1}"/>
      </c:url>
      <a href="${fn:escapeXml(previousUrl)}">上一页</a>
    </c:if>
    <c:if test="${result.page < result.totalPages}">
      <c:url var="nextUrl" value="/attendance">
        <c:param name="employeeId" value="${employeeId}"/>
        <c:param name="from" value="${from}"/>
        <c:param name="to" value="${to}"/>
        <c:param name="page" value="${result.page + 1}"/>
      </c:url>
      <a href="${fn:escapeXml(nextUrl)}">下一页</a>
    </c:if>
  </p>
<jsp:include page="../fragments/page-end.jsp"/>
