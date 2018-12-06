<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Post</title>
</head>
<body>
Questa è la pagina Post!

<h1>Il risultato della tua servlet è ${risultato}</h1>

<form action = "Servlet" method ="get">
Inserire parametro con il metodo GET : &nbsp;
<input type="hidden" name = "param" value = "100">
<input type = submit>
</form>



<p><form action = "Servlet" method ="post">
Inserire parametro con il metodo POST :
<input type="hidden" name = "param" value = "200">
<input type = submit>
</form>

<h2>Il parametro inserito vale: ${param }</h2>


<body style="background-color:blue">
</body>
</html>