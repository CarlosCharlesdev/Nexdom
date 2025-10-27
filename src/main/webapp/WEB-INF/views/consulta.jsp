<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Consulta de Autorizações</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2 { color: #333; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .autorizado { color: green; font-weight: bold; }
        .negado { color: red; font-weight: bold; }
    </style>
</head>
<body>

    <h2>Consulta de Solicitações de Autorização</h2>

    <p><a href="index.jsp">Voltar para o Início</a></p>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Idade</th>
                <th>Sexo</th>
                <th>Procedimento</th>
                <th>Data/Hora</th>
                <th>Status</th>
                <th>Justificativa</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="solicitacao" items="${solicitacoes}">
                <tr>
                    <td>${solicitacao.id}</td>
                    <td>${solicitacao.pacienteNome}</td>
                    <td>${solicitacao.pacienteIdade}</td>
                    <td>${solicitacao.pacienteSexo}</td>
                    <td>${solicitacao.procedimentoCodigo}</td>
                    <td>${solicitacao.dataSolicitacao}</td>
                    <td class="${solicitacao.autorizado ? 'autorizado' : 'negado'}">
                        ${solicitacao.autorizado ? 'AUTORIZADO' : 'NEGADO'}
                    </td>
                    <td>${solicitacao.justificativa}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty solicitacoes}">
                <tr>
                    <td colspan="8">Nenhuma solicitação encontrada.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

</body>
</html>
