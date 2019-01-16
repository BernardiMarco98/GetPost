<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>GetPost</title>
</head>
<body>

<body style="background-color:${colore};">

Questa è la pagina GetPost!

<h1>Bentornato ${username} , il tuo id utente è: ${id}</h1>

<h2>La data odierna è :${data} ed hai usato il metodo ${metodo}</h2>

<h1>Il risultato della tua servlet è : " ${risultato} "</h1>

<!--  <form action = "Servlet" method ="get">
Inserire parametro con il metodo GET  : &nbsp;
<input type="hidden" name = "param" value = "100">
<input type = submit>
</form>

<p><form action = "Servlet" method ="post">
Inserire parametro con il metodo POST :
<input type="hidden" name = "param" value = "200">
<input type = submit>
</form>
-->

<c:if test = "${empty arraylist }">
	La lista dei risultati è vuota
</c:if>
<br>
<c:if test = "${not empty arraylist }">
	La lista dei risultati precedenti:</p>
<c:forEach var = "Output" items = "${arraylist}">
	<c:out value = "${Output.add1}"></c:out>
	<c:out value = "${Output.add2}"></c:out>
	<c:out value = "${Output.risultato}"></c:out> <!-- usa il metodo getRisultato() per ritornare il valore  -->
	<c:out value = "${Output.data}"></c:out>
	<c:out value = "${Output.metodo}"></c:out>
	<br>
</c:forEach></c:if> 



<h2>Il parametro inserito vale: ${param }</h2>

Inserire i numeri da sommare con il metodo GET :

<p><form action = "Servlet" method ="get">
<input type="text" name="valore1"  /> <br>
<p><input type="text" name="valore2"  /> <br>
<p><input type = submit >
</form>

Inserire i numeri da sommare con il metodo POST :

<p><form action = "Servlet" method ="post">
<input type="text" name="valore1"  /> <br>
<p><input type="text" name="valore2"  /> <br>
<p><input type = submit >

</form>
</body>
</html>