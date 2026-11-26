<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="../fragments/page-start.jsp">
  <jsp:param name="pageTitle" value="工资管理"/>
</jsp:include>
  <h1 class="page-title">工资管理</h1>
  <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
  <c:if test="${not empty message}"><p class="form-success"><c:out value="${message}"/></p></c:if>

  <form class="toolbar" action="${ctx}/payroll" method="get">
    <label for="payMonth">工资月份</label>
    <input id="payMonth" type="month" name="payMonth" value="${fn:escapeXml(payMonth)}">
    <button type="submit">查询</button>
  </form>
  <form class="toolbar" action="${ctx}/payroll/generate" method="post">
    <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
    <input type="hidden" name="payMonth" value="${fn:escapeXml(payMonth)}">
    <button type="submit">生成当月工资单</button>
  </form>

  <div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>工号</th><th>姓名</th><th>月份</th><th>基本工资</th><th>奖金</th>
        <th>补贴</th><th>扣款</th><th>社保</th><th>实发工资</th>
        <th>状态</th><th>发放时间</th><th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="record" items="${records}">
        <tr>
          <td><c:out value="${record.employeeNo}"/></td>
          <td><c:out value="${record.employeeName}"/></td>
          <td><c:out value="${record.payMonth}"/></td>
          <td><c:out value="${record.baseSalary}"/></td>
          <td><c:out value="${record.bonus}"/></td>
          <td><c:out value="${record.allowance}"/></td>
          <td><c:out value="${record.deduction}"/></td>
          <td><c:out value="${record.socialSecurity}"/></td>
          <td><c:out value="${record.netSalary}"/></td>
          <td><c:out value="${record.status.name() == 'DRAFT' ? '草稿' : '已发放'}"/></td>
          <td><c:out value="${record.paidAt}"/></td>
          <td>
            <c:if test="${record.status.name() == 'DRAFT'}">
              <a href="${ctx}/payroll/${record.id}/edit">编辑</a>
              <form action="${ctx}/payroll/${record.id}/paid" method="post" style="display:inline">
                <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
                <button type="submit">确认发放</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </div>
  <c:if test="${empty records}"><p class="muted">暂无工资记录</p></c:if>
<jsp:include page="../fragments/page-end.jsp"/>
