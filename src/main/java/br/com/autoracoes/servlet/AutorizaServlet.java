package br.com.autoracoes.servlet;

import br.com.autoracoes.dao.RegraAutorizacaoDAO;
import br.com.autoracoes.model.RegraAutorizacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AutorizaServlet", urlPatterns = {"/autorizar"} )
public class AutorizaServlet extends HttpServlet {

    // Mantemos o DAO de Regras, pois a validação é o foco
    private RegraAutorizacaoDAO regraDAO = new RegraAutorizacaoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Receber e preparar os parâmetros
        String codigo = req.getParameter("procedimentoCodigo");
        String sexo = req.getParameter("sexo").toUpperCase();
        String idadeStr = req.getParameter("idade");
        int idade = 0;
        try {
            idade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException e) {
            enviarResposta(resp, false, "Erro: Idade inválida.");
            return;
        }

        boolean autorizado = false;
        String justificativa = "Procedimento não cadastrado ou sem regras definidas.";

        // 2. Buscar regras aplicáveis
        List<RegraAutorizacao> regrasAplicaveis = regraDAO.buscarRegrasAplicaveis(codigo, idade, sexo);

        // 3. Aplicar a lógica de validação
        if (regrasAplicaveis.isEmpty()) {
            autorizado = false;
            justificativa = "NEGADO: Procedimento " + codigo + " não possui regras de autorização cadastradas.";
        } else {
            // Prioridade 1: Regras de Negação (resultado = FALSE)
            for (RegraAutorizacao regra : regrasAplicaveis) {
                if (!regra.getResultado()) {
                    autorizado = false;
                    justificativa = "NEGADO: Regra de negação por idade/sexo aplicada (ID:" + regra.getId() + ").";
                    break;
                }
            }

            // Se não foi negado, verifica as regras de Autorização
            if (!autorizado) {
                // Prioridade 2: Regras de Autorização (resultado = TRUE)
                for (RegraAutorizacao regra : regrasAplicaveis) {
                    if (regra.getResultado()) {
                        autorizado = true;
                        justificativa = "AUTORIZADO: Regra de autorização por idade/sexo aplicada (ID:" + regra.getId() + ").";
                        break;
                    }
                }
            }
        }

        // Se ainda não foi autorizado, mas encontrou regras, significa que nenhuma regra de autorização se aplicou
        if (!autorizado && !justificativa.startsWith("NEGADO:")) {
            justificativa = "NEGADO: Não foram encontradas regras de autorização que se apliquem à solicitação.";
        }

        // 4. Enviar a resposta (sem salvar nada)
        enviarResposta(resp, autorizado, justificativa);
    }

    private void enviarResposta(HttpServletResponse resp, boolean autorizado, String justificativa) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        String status = autorizado ? "AUTORIZADO" : "NEGADO";
        resp.getWriter().write("Status: " + status + "\nJustificativa: " + justificativa);
    }
}
