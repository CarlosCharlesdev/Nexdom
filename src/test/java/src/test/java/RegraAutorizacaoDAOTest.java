package src.test.java;

import br.com.autoracoes.dao.ConnectionFactoryTeste;
import br.com.autoracoes.dao.RegraAutorizacaoDAO;
import br.com.autoracoes.model.RegraAutorizacao;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegraAutorizacaoDAOTest {

    private RegraAutorizacaoDAO dao;
    private static Connection connection;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        System.out.println("=== Configurando banco de dados para TESTES ===");

        connection = ConnectionFactoryTeste.getConnectionTeste();

        String dropTable = "DROP TABLE IF EXISTS regras_autorizacao";
        try (PreparedStatement stmt = connection.prepareStatement(dropTable)) {
            stmt.execute();
            System.out.println("✓ Tabela antiga dropada (se existia)");
        }

        String createTable = "CREATE TABLE regras_autorizacao (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "procedimento_codigo VARCHAR(50), " +
                "sexo_necessario VARCHAR(10), " +
                "idade INTEGER, " +
                "resultado BOOLEAN NOT NULL)";

        try (PreparedStatement stmt = connection.prepareStatement(createTable)) {
            stmt.execute();
            System.out.println("✓ Tabela de teste criada com sucesso");
        }

        System.out.println("=== Banco de dados de TESTE pronto! ===\n");
    }

    @BeforeEach
    void setUp() throws SQLException {
        dao = new RegraAutorizacaoDAO();
        limparDados();
        inserirDadosTeste();
    }

    @AfterEach
    void tearDown() throws SQLException {
        limparDados();
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            String dropTable = "DROP TABLE IF EXISTS regras_autorizacao";
            try (PreparedStatement stmt = connection.prepareStatement(dropTable)) {
                stmt.execute();
            } catch (SQLException e) {
                System.err.println("Erro ao dropar tabela: " + e.getMessage());
            }
            connection.close();
        }
    }

    private void limparDados() throws SQLException {
        String delete = "DELETE FROM regras_autorizacao";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.execute();
        }
    }

    private void inserirDadosTeste() throws SQLException {
        String insert = "INSERT INTO regras_autorizacao " +
                "(procedimento_codigo, sexo_necessario, idade, resultado) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            // Regra 1: 1234, M, 10 anos, NÃO
            stmt.setString(1, "1234");
            stmt.setString(2, "M");
            stmt.setInt(3, 10);
            stmt.setBoolean(4, false);
            stmt.addBatch();

            // Regra 2: 4567, M, 20 anos, SIM
            stmt.setString(1, "4567");
            stmt.setString(2, "M");
            stmt.setInt(3, 20);
            stmt.setBoolean(4, true);
            stmt.addBatch();

            // Regra 3: 6789, F, 10 anos, NÃO
            stmt.setString(1, "6789");
            stmt.setString(2, "F");
            stmt.setInt(3, 10);
            stmt.setBoolean(4, false);
            stmt.addBatch();

            // Regra 4: 6789, M, 10 anos, SIM
            stmt.setString(1, "6789");
            stmt.setString(2, "M");
            stmt.setInt(3, 10);
            stmt.setBoolean(4, true);
            stmt.addBatch();

            // Regra 5: 1234, M, 20 anos, SIM
            stmt.setString(1, "1234");
            stmt.setString(2, "M");
            stmt.setInt(3, 20);
            stmt.setBoolean(4, true);
            stmt.addBatch();

            // Regra 6: 4567, F, 30 anos, SIM
            stmt.setString(1, "4567");
            stmt.setString(2, "F");
            stmt.setInt(3, 30);
            stmt.setBoolean(4, true);
            stmt.addBatch();

            stmt.executeBatch();
        }
    }

    @Test
    @DisplayName("1234 | M | 15 anos → SEM REGRA (lista vazia)")
    void test_1234_M_15anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 15, "M");

        assertTrue(regras.isEmpty(), "⚠️ 1234-M-15anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("1234 | M | 20 anos → DEVE AUTORIZAR (regra 20+ anos)")
    void test_1234_M_20anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 20, "M");

        assertEquals(1, regras.size());
        assertTrue(regras.get(0).getResultado(), "✅ 1234-M-20anos = AUTORIZADO");
    }

    @Test
    @DisplayName("1234 | F | qualquer idade → SEM REGRA (lista vazia)")
    void test_1234_F_qualquerIdade() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 20, "F");

        assertTrue(regras.isEmpty(), "⚠️ 1234-F NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("4567 | M | 19 anos → SEM REGRA (lista vazia)")
    void test_4567_M_19anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 19, "M");

        assertTrue(regras.isEmpty(), "⚠️ 4567-M-19anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("4567 | M | 20 anos → DEVE AUTORIZAR")
    void test_4567_M_20anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 20, "M");

        assertEquals(1, regras.size());
        assertTrue(regras.get(0).getResultado(), "✅ 4567-M-20anos = AUTORIZADO");
    }

    @Test
    @DisplayName("4567 | M | 21 anos → SEM REGRA (lista vazia)")
    void test_4567_M_21anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 21, "M");

        assertTrue(regras.isEmpty(), "⚠️ 4567-M-21anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("4567 | M | 25 anos → SEM REGRA (lista vazia)")
    void test_4567_M_25anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 25, "M");

        assertTrue(regras.isEmpty(), "⚠️ 4567-M-25anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("4567 | F | 29 anos → SEM REGRA (lista vazia)")
    void test_4567_F_29anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 29, "F");

        assertTrue(regras.isEmpty(), "⚠️ 4567-F-29anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("4567 | F | 30 anos → DEVE AUTORIZAR")
    void test_4567_F_30anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 30, "F");

        assertEquals(1, regras.size());
        assertTrue(regras.get(0).getResultado(), "✅ 4567-F-30anos = AUTORIZADO");
    }

    @Test
    @DisplayName("6789 | M | 5 anos → SEM REGRA (lista vazia)")
    void test_6789_M_5anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 5, "M");

        assertTrue(regras.isEmpty(), "⚠️ 6789-M-5anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("6789 | M | 10 anos → DEVE AUTORIZAR")
    void test_6789_M_10anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 10, "M");

        assertEquals(1, regras.size());
        assertTrue(regras.get(0).getResultado(), "✅ 6789-M-10anos = AUTORIZADO");
    }

    @Test
    @DisplayName("6789 | F | 10 anos → DEVE NEGAR (regra 0-10 anos)")
    void test_6789_F_10anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 10, "F");

        assertEquals(1, regras.size());
        assertFalse(regras.get(0).getResultado(), "❌ 6789-F-10anos = NEGADO");
    }

    @Test
    @DisplayName("6789 | F | 11 anos → SEM REGRA (lista vazia)")
    void test_6789_F_11anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 11, "F");

        assertTrue(regras.isEmpty(), "⚠️ 6789-F-11anos NÃO ESTÁ NA TABELA = VAZIO");
    }

    @Test
    @DisplayName("Procedimento inexistente → lista vazia")
    void test_procedimentoInexistente() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("9999", 25, "M");

        assertTrue(regras.isEmpty(), "Procedimento 9999 não existe");
    }

    @Test
    @DisplayName("Código null → lista vazia")
    void test_codigoNull() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis(null, 25, "M");

        assertNotNull(regras);
        assertTrue(regras.isEmpty());
    }

    @Test
    @DisplayName("Sexo null → não deve lançar exceção")
    void test_sexoNull() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 25, null);

        assertNotNull(regras);
    }

    @Test
    @DisplayName("Campos devem estar populados corretamente")
    void test_camposPreenchidos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 10, "M");

        assertFalse(regras.isEmpty());

        RegraAutorizacao regra = regras.get(0);
        assertNotNull(regra.getId());
        assertNotNull(regra.getProcedimentoCodigo());
        assertNotNull(regra.getSexoNecessario());
        assertNotNull(regra.getIdade());
    }
}