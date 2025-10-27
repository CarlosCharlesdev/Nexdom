package br.com.autoracoes.servlet;

import br.com.autoracoes.dao.RegraAutorizacaoDAO;
import br.com.autoracoes.dao.SolicitacaoDAO;
import br.com.autoracoes.model.RegraAutorizacao;
import br.com.autoracoes.model.Solicitacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AutorizaServlet", urlPatterns = {"/autorizar"} )
public class AutorizaServlet extends HttpServlet {

    private RegraAutorizacaoDAO regraDAO = new RegraAutorizacaoDAO();
    private SolicitacaoDAO solicitacaoDAO = new SolicitacaoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nome = req.getParameter("nome");
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

        List<RegraAutorizacao> regrasAplicaveis = regraDAO.buscarRegrasAplicaveis(codigo, idade, sexo);

        if (regrasAplicaveis.isEmpty()) {
            autorizado = false;
            justificativa = "NEGADO: Procedimento " + codigo + " não possui regras de autorização cadastradas.";
        } else {
            for (RegraAutorizacao regra : regrasAplicaveis) {
                if (!regra.getResultado()) {
                    autorizado = false;
                    justificativa = "NEGADO: Regra de negação por idade/sexo aplicada (ID:" + regra.getId() + ").";
                    break;
                }
            }

            if (!autorizado) {
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

        // 4. Salvar a solicitação no banco
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setPacienteNome(nome);
        solicitacao.setProcedimentoCodigo(codigo);
        solicitacao.setPacienteIdade(idade);
        solicitacao.setPacienteSexo(sexo);
        solicitacao.setAutorizado(autorizado);
        solicitacao.setJustificativa(justificativa);
        solicitacaoDAO.salvar(solicitacao);

        // 5. Enviar a resposta
        enviarResposta(resp, autorizado, justificativa);
    }

    private void enviarResposta(HttpServletResponse resp, boolean autorizado, String justificativa) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        String status = autorizado ? "AUTORIZADO" : "NEGADO";
        resp.getWriter().write("Status: " + status + "\nJustificativa: " + justificativa);
    }
}
