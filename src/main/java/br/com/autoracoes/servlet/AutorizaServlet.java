package br.com.autoracoes.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "AutorizaServlet", urlPatterns = {"/autorizar"})
public class AutorizaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String codigo = req.getParameter("procedimentoCodigo");
        String sexo = req.getParameter("sexo");
        String idadeStr = req.getParameter("idade");
        int idade = Integer.parseInt(idadeStr == null ? "0" : idadeStr);


// Aqui você fará a consulta no banco (JDBC) para recuperar regra de autorização
// Por enquanto devolvemos um comportamento simples de exemplo.


        boolean autorizado = true;
        String justificativa = "Procedimento não cadastrado";


// Exemplo estático (troque pela lógica com DB)
        if ("PROC-EXEMPLO".equals(codigo)) {
            autorizado = (idade >= 18 && "F".equalsIgnoreCase(sexo));
            justificativa = autorizado ? "Autorizado" : "Negado: requisito de idade/sexo não atendido";
        }


        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(justificativa);
    }
}