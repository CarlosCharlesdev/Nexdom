package br.com.autoracoes.servlet;

import br.com.autoracoes.dao.SolicitacaoDAO;
import br.com.autoracoes.model.Solicitacao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/consulta" )
public class ConsultaServlet extends HttpServlet {

    private SolicitacaoDAO solicitacaoDAO = new SolicitacaoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Solicitacao> solicitacoes = solicitacaoDAO.buscarTodas();

        request.setAttribute("solicitacoes", solicitacoes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/consulta.jsp");
        dispatcher.forward(request, response);
    }
}
