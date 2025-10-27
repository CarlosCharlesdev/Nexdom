package br.com.autoracoes.dao;

import br.com.autoracoes.model.Solicitacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;

public class SolicitacaoDAO {

    private static final String INSERT_SQL = "INSERT INTO solicitacoes (paciente_nome, paciente_sexo, paciente_idade, procedimento_codigo, data_solicitacao, status, justificativa) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM solicitacoes ORDER BY data_solicitacao DESC";

    public void salvar(Solicitacao solicitacao) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, solicitacao.getPacienteNome());
            stmt.setString(2, solicitacao.getPacienteSexo());
            stmt.setInt(3, solicitacao.getPacienteIdade());
            stmt.setString(4, solicitacao.getProcedimentoCodigo());
            stmt.setTimestamp(5, Timestamp.valueOf(solicitacao.getDataSolicitacao()));

            String status = solicitacao.getAutorizado() ? "AUTORIZADO" : "NEGADO";
            stmt.setString(6, status);

            stmt.setString(7, solicitacao.getJustificativa());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao salvar solicitação: " + e.getMessage());
        }
    }

    public List<Solicitacao> buscarTodas() {
        List<Solicitacao> solicitacoes = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Solicitacao sol = new Solicitacao();
                sol.setId(rs.getLong("id"));
                sol.setPacienteNome(rs.getString("paciente_nome"));
                sol.setPacienteSexo(rs.getString("paciente_sexo"));
                sol.setPacienteIdade(rs.getInt("paciente_idade"));
                sol.setProcedimentoCodigo(rs.getString("procedimento_codigo"));

                // Conversão de Timestamp para LocalDateTime
                sol.setDataSolicitacao(rs.getTimestamp("data_solicitacao").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                sol.setAutorizado("AUTORIZADO".equalsIgnoreCase(rs.getString("status")));
                sol.setJustificativa(rs.getString("justificativa"));

                solicitacoes.add(sol);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar solicitações: " + e.getMessage());
        }
        return solicitacoes;
    }
}
