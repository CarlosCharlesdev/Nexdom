<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<title>Cadastro de Solicitação</title>
</head>
<body>
<h2>Nova Solicitação</h2>
<%-- O action chama o AutorizaServlet mapeado para "/autorizar" --%>
<form action="autorizar" method="post">
Nome: <input type="text" name="nome"/>

Sexo: <input type="text" name="sexo"/>

Idade: <input type="number" name="idade"/>

Procedimento (código): <input type="text" name="procedimentoCodigo"/>

<button type="submit">Enviar</button>
</form>
</body>
</html>
