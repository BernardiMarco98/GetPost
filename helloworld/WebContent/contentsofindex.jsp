<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ciao Mondo!</title>
</head>
<body>
<% java.util.Date d = new java.util.Date(); %>
<h1>
Oggi è il <%= d.toString()%> e questa pagina jsp funziona!
</h1>
<h2>
Ciaoooo mondoooo!
</h2>
</body>
</html>