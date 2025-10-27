package br.com.autoracoes.dao;

import br.com.autoracoes.model.RegraAutorizacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegraAutorizacaoDAO {

    public List<RegraAutorizacao> buscarRegrasAplicaveis(String procedimentoCodigo, int idade, String sexo) {
        List<RegraAutorizacao> regras = new ArrayList<>();

        String sql = "SELECT * FROM regras_autorizacao WHERE procedimento_codigo = ? " +
                "AND sexo_necessario = ? " +
                "AND idade_min <= ? " +
                "AND (idade_max IS NULL OR idade_max >= ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, procedimentoCodigo);
            stmt.setString(2, sexo.toUpperCase());
            stmt.setInt(3, idade);
            stmt.setInt(4, idade);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RegraAutorizacao regra = new RegraAutorizacao();
                    regra.setId(rs.getLong("id"));
                    regra.setProcedimentoCodigo(rs.getString("procedimento_codigo"));
                    regra.setSexoNecessario(rs.getString("sexo_necessario"));
                    regra.setIdadeMin(rs.getInt("idade_min"));

                    int idadeMax = rs.getInt("idade_max");
                    if (!rs.wasNull()) {
                        regra.setIdadeMax(idadeMax);
                    }

                    regra.setResultado(rs.getBoolean("resultado"));
                    regras.add(regra);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar regras aplicáveis: " + e.getMessage());
        }
        return regras;
    }
}
