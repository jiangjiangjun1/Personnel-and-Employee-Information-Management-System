<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="currentPath" value="${requestScope.currentPath}"/>
<header class="topbar">
  <a class="brand" href="${ctx}/dashboard">人事员工信息管理系统</a>
  <span class="user-area">
    <c:out value="${appUser.displayName}"/>
    <form action="${pageContext.request.contextPath}/logout" method="post">
      <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
      <button type="submit">退出</button>
    </form>
  </span>
</header>
<nav class="sidebar" aria-label="系统导航">
  <a class="${currentPath == ctx.concat('/dashboard') ? 'active' : ''}"
     href="${ctx}/dashboard">首页</a>
  <c:choose>
    <c:when test="${appUser.role.name() == 'ADMIN'}">
      <a class="${fn:startsWith(currentPath, ctx.concat('/departments')) ? 'active' : ''}"
         href="${ctx}/departments">部门管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/positions')) ? 'active' : ''}"
         href="${ctx}/positions">岗位管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/users')) ? 'active' : ''}"
         href="${ctx}/users">账号管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/employees')) ? 'active' : ''}"
         href="${ctx}/employees">员工档案</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/attendance')) ? 'active' : ''}"
         href="${ctx}/attendance">考勤管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/leave')) ? 'active' : ''}"
         href="${ctx}/leave">请假管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/payroll')) ? 'active' : ''}"
         href="${ctx}/payroll">薪资管理</a>
    </c:when>
    <c:when test="${appUser.role.name() == 'HR'}">
      <a class="${fn:startsWith(currentPath, ctx.concat('/employees')) ? 'active' : ''}"
         href="${ctx}/employees">员工档案</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/attendance')) ? 'active' : ''}"
         href="${ctx}/attendance">考勤管理</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/leave')) ? 'active' : ''}"
         href="${ctx}/leave">请假审批</a>
      <a class="${fn:startsWith(currentPath, ctx.concat('/payroll')) ? 'active' : ''}"
         href="${ctx}/payroll">薪资管理</a>
    </c:when>
    <c:otherwise>
      <a class="${fn:startsWith(currentPath, ctx.concat('/leave')) ? 'active' : ''}"
         href="${ctx}/leave">我的请假</a>
    </c:otherwise>
  </c:choose>
  <a class="${fn:startsWith(currentPath, ctx.concat('/profile')) ? 'active' : ''}"
     href="${ctx}/profile">个人中心</a>
</nav>
