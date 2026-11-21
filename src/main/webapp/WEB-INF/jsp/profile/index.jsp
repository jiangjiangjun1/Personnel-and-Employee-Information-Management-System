<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>个人中心 - 人事员工信息管理系统</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body class="app-page">
  <jsp:include page="../fragments/layout.jsp"/>
  <main class="app-main">
    <h1 class="page-title">个人中心</h1>
    <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>
    <c:if test="${not empty message}"><p class="form-info">${message}</p></c:if>

    <c:choose>
      <c:when test="${employee == null}">
        <section class="card">
          <p>${appUser.displayName}（${appUser.role.name()}）</p>
          <p class="muted">当前账号未关联员工档案。</p>
        </section>
      </c:when>
      <c:otherwise>
        <section class="card">
          <h2>${employee.name}（${employee.employeeNo}）</h2>
          <p>部门：${employee.departmentName} · 岗位：${employee.positionName}</p>
          <p>入职日期：${employee.hireDate} · 状态：${employee.status.name()}</p>
          <form action="${pageContext.request.contextPath}/profile" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <label>电话</label>
            <input name="phone" value="${employee.phone}" type="text">
            <label>邮箱</label>
            <input name="email" value="${employee.email}" type="email">
            <label>地址</label>
            <input name="address" value="${employee.address}" type="text">
            <button type="submit">保存联系方式</button>
          </form>
        </section>

        <section class="card">
          <h2>我的请假</h2>
          <div class="table-wrap">
            <table>
              <thead><tr><th>类型</th><th>开始日期</th><th>结束日期</th><th>状态</th></tr></thead>
              <tbody>
                <c:forEach var="l" items="${leaveRequests}">
                  <tr>
                    <td>${l.leaveType}</td>
                    <td>${l.startDate}</td>
                    <td>${l.endDate}</td>
                    <td>${l.status.name()}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>

        <section class="card">
          <h2>本月工资（${currentMonth}）</h2>
          <div class="table-wrap">
            <table>
              <thead><tr><th>基本工资</th><th>奖金</th><th>补贴</th><th>扣款</th><th>实发</th><th>状态</th></tr></thead>
              <tbody>
                <c:forEach var="p" items="${payrollRecords}">
                  <tr>
                    <td>${p.baseSalary}</td>
                    <td>${p.bonus}</td>
                    <td>${p.allowance}</td>
                    <td>${p.deduction}</td>
                    <td>${p.netSalary}</td>
                    <td>${p.status.name() == 'PAID' ? '已发放' : '草稿'}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>

        <section class="card">
          <h2>最近考勤</h2>
          <div class="table-wrap">
            <table>
              <thead><tr><th>日期</th><th>签到</th><th>签退</th><th>状态</th></tr></thead>
              <tbody>
                <c:forEach var="a" items="${recentAttendance}">
                  <tr>
                    <td>${a.workDate}</td>
                    <td>${a.checkIn}</td>
                    <td>${a.checkOut}</td>
                    <td>${a.status.name()}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>
      </c:otherwise>
    </c:choose>
  </main>
</body>
</html>
