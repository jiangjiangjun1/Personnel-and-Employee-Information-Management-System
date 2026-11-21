<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>工作台 - 人事员工信息管理系统</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body class="app-page">
  <jsp:include page="../fragments/layout.jsp"/>
  <main class="app-main">
    <h1 class="page-title">工作台</h1>

    <c:if test="${!employeeView}">
      <section class="stat-grid">
        <div class="stat">
          <span class="stat-value"><c:out value="${summary.employeeCount}"/></span>
          <span class="stat-label">员工总数</span>
        </div>
        <div class="stat">
          <span class="stat-value"><c:out value="${summary.activeEmployeeCount}"/></span>
          <span class="stat-label">在职员工</span>
        </div>
        <div class="stat">
          <span class="stat-value"><c:out value="${summary.departmentCount}"/></span>
          <span class="stat-label">部门数量</span>
        </div>
        <div class="stat">
          <span class="stat-value"><c:out value="${summary.pendingLeaveCount}"/></span>
          <span class="stat-label">待审批请假</span>
        </div>
      </section>
    </c:if>

    <section class="card">
      <h2>最近考勤</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>工号</th><th>姓名</th><th>日期</th><th>签到</th><th>签退</th><th>状态</th></tr>
          </thead>
          <tbody>
            <c:forEach var="a" items="${recentAttendance}">
              <tr>
                <td><c:out value="${a.employeeNo}"/></td>
                <td><c:out value="${a.employeeName}"/></td>
                <td><c:out value="${a.workDate}"/></td>
                <td><c:out value="${a.checkIn}"/></td>
                <td><c:out value="${a.checkOut}"/></td>
                <td><c:out value="${a.status.name()}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </section>

    <section class="card">
      <h2>本月工资汇总（<c:out value="${currentMonth}"/>）</h2>
      <p>实发合计：<strong><c:out value="${payrollTotal}"/></strong></p>
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>工号</th><th>姓名</th><th>实发工资</th><th>状态</th></tr>
          </thead>
          <tbody>
            <c:forEach var="p" items="${payrollRecords}">
              <tr>
                <td><c:out value="${p.employeeNo}"/></td>
                <td><c:out value="${p.employeeName}"/></td>
                <td><c:out value="${p.netSalary}"/></td>
                <td><c:out value="${p.status.name() == 'PAID' ? '已发放' : '草稿'}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</body>
</html>
