<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login page</title>
</head>
<body>
${logout_message}

<h1>Benvenuto nella pagina di login!</h1>

<h2>Inserisci i tuoi dati per accedere</h2>
		<form method="get" action="Servlet" action="Servlet">
        	Username:<br><input type="text" name="username" /><br/>
        	<p>Password:<br><input type="password" name="password" /><br/>
        <p><input type="submit" value="login" />
        </form>
</body>
</html>