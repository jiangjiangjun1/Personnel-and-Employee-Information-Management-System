<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
    <c:if test="${not empty error}"><p class="form-error"><c:out value="${error}"/></p></c:if>
    <c:if test="${not empty message}"><p class="form-info"><c:out value="${message}"/></p></c:if>

    <c:choose>
      <c:when test="${employee == null}">
        <section class="card">
          <p><c:out value="${appUser.displayName}"/>（<c:out value="${appUser.role.name()}"/>）</p>
          <p class="muted">当前账号未关联员工档案。</p>
        </section>
      </c:when>
      <c:otherwise>
        <section class="card">
          <h2><c:out value="${employee.name}"/>（<c:out value="${employee.employeeNo}"/>）</h2>
          <p>部门：<c:out value="${employee.departmentName}"/> · 岗位：<c:out value="${employee.positionName}"/></p>
          <p>入职日期：<c:out value="${employee.hireDate}"/> · 状态：<c:out value="${employee.status.name()}"/></p>
          <form action="${pageContext.request.contextPath}/profile" method="post">
            <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
            <label>电话</label>
            <input name="phone" value="${fn:escapeXml(employee.phone)}" type="text">
            <label>邮箱</label>
            <input name="email" value="${fn:escapeXml(employee.email)}" type="email">
            <label>地址</label>
            <input name="address" value="${fn:escapeXml(employee.address)}" type="text">
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
                    <td><c:out value="${l.leaveType}"/></td>
                    <td><c:out value="${l.startDate}"/></td>
                    <td><c:out value="${l.endDate}"/></td>
                    <td><c:out value="${l.status.name()}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>

        <section class="card">
          <h2>本月工资（<c:out value="${currentMonth}"/>）</h2>
          <div class="table-wrap">
            <table>
              <thead><tr><th>基本工资</th><th>奖金</th><th>补贴</th><th>扣款</th><th>实发</th><th>状态</th></tr></thead>
              <tbody>
                <c:forEach var="p" items="${payrollRecords}">
                  <tr>
                    <td><c:out value="${p.baseSalary}"/></td>
                    <td><c:out value="${p.bonus}"/></td>
                    <td><c:out value="${p.allowance}"/></td>
                    <td><c:out value="${p.deduction}"/></td>
                    <td><c:out value="${p.netSalary}"/></td>
                    <td><c:out value="${p.status.name() == 'PAID' ? '已发放' : '草稿'}"/></td>
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
      </c:otherwise>
    </c:choose>
  </main>
</body>
</html>
