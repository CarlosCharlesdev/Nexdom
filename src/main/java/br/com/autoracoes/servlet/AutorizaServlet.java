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
import java.util.stream.Collectors;

@WebServlet(name = "AutorizaServlet", urlPatterns = {"/autorizar"}  )
public class AutorizaServlet extends HttpServlet {

    private RegraAutorizacaoDAO regraDAO = new RegraAutorizacaoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final String codigo = req.getParameter("procedimentoCodigo");
        final String sexo = req.getParameter("sexo").toUpperCase();
        String idadeStr = req.getParameter("idade");

        int tempIdade = 0;
        try {
            tempIdade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("autorizacaoStatus", "NEGADO");
            req.getSession().setAttribute("autorizacaoJustificativa", "Erro: Idade fornecida é inválida.");
            resp.sendRedirect("cadastro");
            return;
        }

        final int idade = tempIdade;

        boolean autorizado = false;
        String justificativa = "Procedimento não cadastrado ou sem regras definidas.";

        List<RegraAutorizacao> regrasPotenciais = regraDAO.buscarRegrasAplicaveis(codigo, idade, sexo);

        List<RegraAutorizacao> regrasAplicaveis = regrasPotenciais.stream()
                .filter(regra -> regra.getSexoNecessario().equals("AMBOS") || regra.getSexoNecessario().equals(sexo))
                .filter(regra -> idade >= regra.getIdadeMin())
                .filter(regra -> regra.getIdadeMax() == null || idade <= regra.getIdadeMax())
                .collect(Collectors.toList());

        if (regrasAplicaveis.isEmpty()) {
            autorizado = false;
            if (!regrasPotenciais.isEmpty()) {
                justificativa = "NEGADO: Nenhuma regra de autorização se aplica aos critérios (Idade/Sexo) fornecidos.";
            } else {
                justificativa = "NEGADO: Procedimento " + codigo + " não possui regras de autorização cadastradas.";
            }
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

        if (!autorizado && !justificativa.startsWith("NEGADO: Regra de negação")) {
            if (!regrasPotenciais.isEmpty() && regrasAplicaveis.isEmpty()) {
                justificativa = "NEGADO: Critérios não atendidos. Idade/Sexo não se qualificam para as regras existentes.";
            }
        }

        req.getSession().setAttribute("autorizacaoStatus", autorizado ? "AUTORIZADO" : "NEGADO");
        req.getSession().setAttribute("autorizacaoJustificativa", justificativa);

        resp.sendRedirect("cadastro");
    }
}
