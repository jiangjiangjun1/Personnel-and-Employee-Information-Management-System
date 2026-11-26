<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="编辑工资单"/>
</jsp:include>
  <h1 class="page-title">编辑工资单</h1>
  <p class="muted">员工：<c:out value="${record.employeeNo}"/> <c:out value="${record.employeeName}"/>，月份：<c:out value="${record.payMonth}"/></p>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>

  <form class="card page-form" action="${ctx}/payroll/${record.id}" method="post">
    <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
    <label for="baseSalary">基本工资</label>
    <input id="baseSalary" type="number" step="0.01" name="baseSalary"
           value="${fn:escapeXml(empty rawBase ? record.baseSalary : rawBase)}" required>
    <label for="bonus">奖金</label>
    <input id="bonus" type="number" step="0.01" name="bonus"
           value="${fn:escapeXml(empty rawBonus ? record.bonus : rawBonus)}" required>
    <label for="allowance">补贴</label>
    <input id="allowance" type="number" step="0.01" name="allowance"
           value="${fn:escapeXml(empty rawAllowance ? record.allowance : rawAllowance)}" required>
    <label for="deduction">扣款</label>
    <input id="deduction" type="number" step="0.01" name="deduction"
           value="${fn:escapeXml(empty rawDeduction ? record.deduction : rawDeduction)}" required>
    <label for="socialSecurity">社保</label>
    <input id="socialSecurity" type="number" step="0.01" name="socialSecurity"
           value="${fn:escapeXml(empty rawSocial ? record.socialSecurity : rawSocial)}" required>
    <c:url var="backUrl" value="/payroll">
      <c:param name="payMonth" value="${record.payMonth}"/>
    </c:url>
    <p class="form-actions"><button type="submit">保存</button> <a href="${fn:escapeXml(backUrl)}">返回</a></p>
  </form>
<jsp:include page="../fragments/page-end.jsp"/>
