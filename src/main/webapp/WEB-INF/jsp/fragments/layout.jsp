<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="topbar">
  <span>人事员工信息管理系统</span>
  <span>${appUser.displayName}
    <form action="${pageContext.request.contextPath}/logout" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <button type="submit">退出</button>
    </form>
  </span>
</header>
<nav class="sidebar">
  <a href="${pageContext.request.contextPath}/dashboard">首页</a>
  <c:choose>
    <c:when test="${appUser.role.name() == 'ADMIN'}">
      <a href="${pageContext.request.contextPath}/departments">部门管理</a>
      <a href="${pageContext.request.contextPath}/positions">岗位管理</a>
      <a href="${pageContext.request.contextPath}/users">账号管理</a>
      <a href="${pageContext.request.contextPath}/employees">员工档案</a>
      <a href="${pageContext.request.contextPath}/attendance">考勤管理</a>
      <a href="${pageContext.request.contextPath}/leave">请假管理</a>
      <a href="${pageContext.request.contextPath}/payroll">薪资管理</a>
    </c:when>
    <c:when test="${appUser.role.name() == 'HR'}">
      <a href="${pageContext.request.contextPath}/employees">员工档案</a>
      <a href="${pageContext.request.contextPath}/attendance">考勤管理</a>
      <a href="${pageContext.request.contextPath}/leave">请假审批</a>
      <a href="${pageContext.request.contextPath}/payroll">薪资管理</a>
    </c:when>
    <c:otherwise>
      <a href="${pageContext.request.contextPath}/leave">我的请假</a>
    </c:otherwise>
  </c:choose>
  <a href="${pageContext.request.contextPath}/profile">个人中心</a>
</nav>
