<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
<title>Cadastro de Solicitação</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
<h2>Nova Solicitação</h2>

<%-- Lógica para exibir a caixa de resultado --%>
<c:if test="${not empty sessionScope.autorizacaoStatus}">
    <div class="result-box ${sessionScope.autorizacaoStatus eq 'AUTORIZADO' ? 'success' : 'error'}">
        <h3>Status: ${sessionScope.autorizacaoStatus}</h3>
        <p>${sessionScope.autorizacaoJustificativa}</p>
    </div>
    <%-- Limpa a sessão para que a mensagem não apareça novamente --%>
    <c:remove var="autorizacaoStatus" scope="session"/>
    <c:remove var="autorizacaoJustificativa" scope="session"/>
</c:if>

<form action="autorizar" method="post">
Nome: <input type="text" name="nome"/>

Sexo: <input type="text" name="sexo"/>

Idade: <input type="number" name="idade"/>

Procedimento (código): <input type="text" name="procedimentoCodigo"/>

<button type="submit">Enviar</button>
</form>
</body>
</html>
