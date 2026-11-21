<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>登录 - 人事员工信息管理系统</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body class="login-page">
  <main class="login-card">
    <h1>人事员工信息管理系统</h1>
    <form action="${pageContext.request.contextPath}/login" method="post">
      <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
      <label for="username">用户名</label>
      <input id="username" name="username" type="text" autocomplete="username" required>
      <label for="password">密码</label>
      <input id="password" name="password" type="password" autocomplete="current-password" required>
      <c:if test="${param.error != null}">
        <p class="form-error">用户名或密码错误，请重试。</p>
      </c:if>
      <c:if test="${param.logout != null}">
        <p class="form-info">您已安全退出。</p>
      </c:if>
      <button type="submit">登录</button>
    </form>
  </main>
</body>
</html>
