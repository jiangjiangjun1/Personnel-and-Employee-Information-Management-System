<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>系统异常 - 人事员工信息管理系统</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body class="app-page">
  <main class="app-main" style="margin-left:0">
    <h1>500 系统异常</h1>
    <p>${empty message ? '系统繁忙，请稍后重试。' : message}</p>
    <a href="${pageContext.request.contextPath}/dashboard">返回首页</a>
  </main>
</body>
</html>
