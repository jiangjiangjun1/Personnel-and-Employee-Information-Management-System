<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="请假管理"/>
</jsp:include>
  <h1 class="page-title">请假管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <c:if test="${canSubmit}">
    <div class="toolbar"><a class="button-link" href="${ctx}/leave/new">提交请假</a></div>
  </c:if>

  <div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>类型</th><th>开始</th><th>结束</th>
        <th>原因</th><th>状态</th><th>审批人</th><th>审批意见</th><th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="leave" items="${requests}">
        <tr>
          <td><c:out value="${leave.employeeNo}"/></td>
          <td><c:out value="${leave.employeeName}"/></td>
          <td>
            <c:out value="${leave.leaveType == 'ANNUAL' ? '年假'
              : leave.leaveType == 'SICK' ? '病假'
              : leave.leaveType == 'PERSONAL' ? '事假'
              : leave.leaveType == 'OTHER' ? '其他' : leave.leaveType}"/>
          </td>
          <td><c:out value="${leave.startDate}"/></td>
          <td><c:out value="${leave.endDate}"/></td>
          <td><c:out value="${leave.reason}"/></td>
          <td>
            <c:out value="${leave.status.name() == 'PENDING' ? '待审批'
              : leave.status.name() == 'APPROVED' ? '已通过'
              : leave.status.name() == 'REJECTED' ? '已驳回' : leave.status.name()}"/>
          </td>
          <td><c:out value="${leave.approverName}"/></td>
          <td><c:out value="${leave.approverComment}"/></td>
          <td>
            <c:if test="${approver and leave.status.name() == 'PENDING'}">
              <form action="${ctx}/leave/${leave.id}/approval" method="post" style="display:inline">
                <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
                <input type="hidden" name="approved" value="true">
                <input type="hidden" name="comment" value="同意">
                <button type="submit">通过</button>
              </form>
              <form action="${ctx}/leave/${leave.id}/approval" method="post" style="display:inline">
                <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
                <input type="hidden" name="approved" value="false">
                <input type="text" name="comment" placeholder="驳回意见">
                <button type="submit">驳回</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>
  <c:if test="${empty requests}"><p class="muted">暂无请假记录</p></c:if>
<jsp:include page="../fragments/page-end.jsp"/>
