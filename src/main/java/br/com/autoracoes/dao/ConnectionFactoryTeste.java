package br.com.autoracoes.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactoryTeste {

    private static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String TEST_DB_USER = "sa";
    private static final String TEST_DB_PASSWORD = "";

    static {
        try {
            // Carrega o driver H2
            Class.forName("org.h2.Driver");
            System.out.println("✓ Driver H2 carregado para TESTES!");
        } catch (ClassNotFoundException e) {
            System.err.println("ERRO: Driver H2 não encontrado!");
            e.printStackTrace();
            throw new RuntimeException("Falha ao carregar driver H2", e);
        }
    }

    public static Connection getConnectionTeste() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
            System.out.println("✓ Conexão de TESTE estabelecida: " + TEST_DB_URL);
            return conn;
        } catch (SQLException e) {
            System.err.println("ERRO ao conectar ao banco de TESTE: " + e.getMessage());
            throw e;
        }
    }
}