package br.com.autoracoes.dao;

import br.com.autoracoes.model.RegraAutorizacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegraAutorizacaoDAO {

    private static final String SELECT_APPLICABLE_SQL =
            "SELECT * FROM regras_autorizacao WHERE procedimento_codigo = ? " +
                    "AND (sexo_necessario = ? OR sexo_necessario = 'AMBOS')";

    public List<RegraAutorizacao> buscarRegrasAplicaveis(String codigo, int idade, String sexo) {
        List<RegraAutorizacao> regras = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_APPLICABLE_SQL)) {

            stmt.setString(1, codigo);
            stmt.setString(2, sexo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RegraAutorizacao regra = new RegraAutorizacao();
                    regra.setId(rs.getLong("ID"));
                    regra.setProcedimentoCodigo(rs.getString("PROCEDIMENTO_CODIGO"));
                    regra.setSexoNecessario(rs.getString("SEXO_NECESSARIO"));
                    regra.setIdadeMin(rs.getInt("IDADE_MIN"));

                    int idadeMax = rs.getInt("IDADE_MAX");
                    if (!rs.wasNull()) {
                        regra.setIdadeMax(idadeMax);
                    }

                    regra.setResultado(rs.getBoolean("RESULTADO"));
                    regras.add(regra);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar regras aplicáveis: " + e.getMessage());
        }
        return regras;
    }
}
