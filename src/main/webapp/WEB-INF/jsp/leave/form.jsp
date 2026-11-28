<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="提交请假"/>
</jsp:include>
  <h1 class="page-title">提交请假</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>

  <form class="card page-form" action="${ctx}/leave" method="post">
    <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
    <label for="leaveType">请假类型</label>
    <select id="leaveType" name="leaveType">
      <option value="ANNUAL" <c:if test="${form.leaveType == 'ANNUAL'}">selected</c:if>>年假</option>
      <option value="SICK" <c:if test="${form.leaveType == 'SICK'}">selected</c:if>>病假</option>
      <option value="PERSONAL" <c:if test="${form.leaveType == 'PERSONAL'}">selected</c:if>>事假</option>
      <option value="OTHER" <c:if test="${form.leaveType == 'OTHER'}">selected</c:if>>其他</option>
    </select>
    <label for="startDate">开始日期</label>
    <input id="startDate" type="date" name="startDate"
           value="${fn:escapeXml(empty startDate ? form.startDate : startDate)}" required>
    <label for="endDate">结束日期</label>
    <input id="endDate" type="date" name="endDate"
           value="${fn:escapeXml(empty endDate ? form.endDate : endDate)}" required>
    <label for="reason">请假原因</label>
    <textarea id="reason" name="reason" required><c:out value="${form.reason}"/></textarea>
    <p class="form-actions"><button type="submit">提交</button> <a href="${ctx}/leave">返回</a></p>
  </form>
<jsp:include page="../fragments/page-end.jsp"/>
