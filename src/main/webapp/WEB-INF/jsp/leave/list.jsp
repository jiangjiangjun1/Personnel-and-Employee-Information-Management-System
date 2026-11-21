<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>请假管理 - 人事员工信息管理系统</title>
  <style>
    body { font-family: system-ui, -apple-system, sans-serif; margin: 24px; color: #1f2937; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #e5e7eb; padding: 8px 10px; text-align: left; }
    th { background: #f9fafb; }
    .form-error { color: #b91c1c; }
    .muted { color: #6b7280; }
  </style>
</head>
<body>
  <h1>请假管理</h1>
  <c:if test="${not empty error}"><p class="form-error">${error}</p></c:if>
  <c:if test="${canSubmit}"><p><a href="${ctx}/leave/new">提交请假</a></p></c:if>

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
          <td>${leave.employeeNo}</td>
          <td>${leave.employeeName}</td>
          <td>
            ${leave.leaveType == 'ANNUAL' ? '年假'
              : leave.leaveType == 'SICK' ? '病假'
              : leave.leaveType == 'PERSONAL' ? '事假'
              : leave.leaveType == 'OTHER' ? '其他' : leave.leaveType}
          </td>
          <td>${leave.startDate}</td>
          <td>${leave.endDate}</td>
          <td>${leave.reason}</td>
          <td>
            ${leave.status.name() == 'PENDING' ? '待审批'
              : leave.status.name() == 'APPROVED' ? '已通过'
              : leave.status.name() == 'REJECTED' ? '已驳回' : leave.status.name()}
          </td>
          <td>${empty leave.approverName ? '' : leave.approverName}</td>
          <td>${empty leave.approverComment ? '' : leave.approverComment}</td>
          <td>
            <c:if test="${approver and leave.status.name() == 'PENDING'}">
              <form action="${ctx}/leave/${leave.id}/approval" method="post" style="display:inline">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <input type="hidden" name="approved" value="true">
                <input type="hidden" name="comment" value="同意">
                <button type="submit">通过</button>
              </form>
              <form action="${ctx}/leave/${leave.id}/approval" method="post" style="display:inline">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
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
  <c:if test="${empty requests}"><p class="muted">暂无请假记录</p></c:if>
</body>
</html>
