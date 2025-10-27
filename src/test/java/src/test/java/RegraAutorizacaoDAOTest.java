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

        // Cria tabela de teste
        String createTable = "CREATE TABLE regras_autorizacao (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "procedimento_codigo VARCHAR(50), " +
                "sexo_necessario VARCHAR(10), " +
                "idade_min INTEGER, " +
                "idade_max INTEGER, " +
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
            // Drop table antes de fechar
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
                "(procedimento_codigo, sexo_necessario, idade_min, idade_max, resultado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, "1234");
            stmt.setString(2, "M");
            stmt.setInt(3, 0);
            stmt.setInt(4, 10);
            stmt.setBoolean(5, false);
            stmt.addBatch();

            // Regra 2: Procedimento 4567, Masculino, 20+ anos, Autorizado
            stmt.setString(1, "4567");
            stmt.setString(2, "M");
            stmt.setInt(3, 20);
            stmt.setObject(4, null);
            stmt.setBoolean(5, true);
            stmt.addBatch();

            // Regra 3: Procedimento 6789, Feminino, 0-10 anos, Negado
            stmt.setString(1, "6789");
            stmt.setString(2, "F");
            stmt.setInt(3, 0);
            stmt.setInt(4, 10);
            stmt.setBoolean(5, false);
            stmt.addBatch();

            // Regra 4: Procedimento 6789, Masculino, 10+ anos, Autorizado
            stmt.setString(1, "6789");
            stmt.setString(2, "M");
            stmt.setInt(3, 10);
            stmt.setObject(4, null);
            stmt.setBoolean(5, true);
            stmt.addBatch();

            // Regra 5: Procedimento 1234, Masculino, 20+ anos, Autorizado
            stmt.setString(1, "1234");
            stmt.setString(2, "M");
            stmt.setInt(3, 20);
            stmt.setObject(4, null);
            stmt.setBoolean(5, true);
            stmt.addBatch();

            // Regra 6: Procedimento 4567, Feminino, 30+ anos, Autorizado
            stmt.setString(1, "4567");
            stmt.setString(2, "F");
            stmt.setInt(3, 30);
            stmt.setObject(4, null);
            stmt.setBoolean(5, true);
            stmt.addBatch();

            stmt.executeBatch();
        }
    }

    @Test
    @DisplayName("Deve buscar regras aplicáveis para procedimento 1234, homem de 5 anos")
    void testBuscarRegrasAplicaveis_Procedimento1234_Homem5Anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 5, "M");

        assertNotNull(regras);
        assertEquals(2, regras.size(), "Deve retornar 2 regras para procedimento 1234 e sexo M");

        assertTrue(regras.stream().allMatch(r -> "1234".equals(r.getProcedimentoCodigo())));
        assertTrue(regras.stream().allMatch(r -> "M".equals(r.getSexoNecessario())));
    }

    @Test
    @DisplayName("Deve buscar regras aplicáveis para procedimento 4567, mulher de 35 anos")
    void testBuscarRegrasAplicaveis_Procedimento4567_Mulher35Anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 35, "F");

        assertNotNull(regras);
        assertEquals(1, regras.size(), "Deve retornar 1 regra para procedimento 4567 e sexo F");

        RegraAutorizacao regra = regras.get(0);
        assertEquals("4567", regra.getProcedimentoCodigo());
        assertEquals("F", regra.getSexoNecessario());
        assertEquals(30, regra.getIdadeMin());
        assertTrue(regra.getResultado());
    }

    @Test
    @DisplayName("Deve buscar regras aplicáveis para procedimento 6789, homem de 15 anos")
    void testBuscarRegrasAplicaveis_Procedimento6789_Homem15Anos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 15, "M");

        assertNotNull(regras);
        assertEquals(1, regras.size());

        RegraAutorizacao regra = regras.get(0);
        assertEquals("6789", regra.getProcedimentoCodigo());
        assertEquals("M", regra.getSexoNecessario());
        assertEquals(10, regra.getIdadeMin());
        assertTrue(regra.getResultado());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para procedimento inexistente")
    void testBuscarRegrasAplicaveis_ProcedimentoInexistente() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("9999", 25, "M");

        assertNotNull(regras);
        assertTrue(regras.isEmpty(), "Deve retornar lista vazia para procedimento inexistente");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para sexo incompatível")
    void testBuscarRegrasAplicaveis_SexoIncompativel() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("6789", 5, "M");

        assertNotNull(regras);
        assertEquals(1, regras.size(), "Deve retornar apenas regras compatíveis com o sexo");
    }

    @Test
    @DisplayName("Deve tratar corretamente idade_max NULL")
    void testBuscarRegrasAplicaveis_IdadeMaxNull() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("4567", 25, "M");

        assertNotNull(regras);
        assertFalse(regras.isEmpty());

        RegraAutorizacao regra = regras.get(0);
        assertEquals(20, regra.getIdadeMin());
        assertNull(regra.getIdadeMax(), "Idade máxima deve ser null quando não definida");
    }

    @Test
    @DisplayName("Deve popular todos os campos da RegraAutorizacao corretamente")
    void testBuscarRegrasAplicaveis_CamposPreenchidos() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 5, "M");

        assertFalse(regras.isEmpty());

        RegraAutorizacao regra = regras.get(0);
        assertNotNull(regra.getId());
        assertNotNull(regra.getProcedimentoCodigo());
        assertNotNull(regra.getSexoNecessario());
        assertTrue(regra.getIdadeMin() >= 0);
    }

    @Test
    @DisplayName("Deve retornar múltiplas regras quando existem várias aplicáveis")
    void testBuscarRegrasAplicaveis_MultiplasRegras() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 25, "M");

        assertNotNull(regras);
        assertEquals(2, regras.size(), "Deve retornar todas as regras aplicáveis");

        assertTrue(regras.stream().allMatch(r ->
                "1234".equals(r.getProcedimentoCodigo()) && "M".equals(r.getSexoNecessario())
        ));
    }

    @Test
    @DisplayName("Deve respeitar filtro de sexo na query SQL")
    void testBuscarRegrasAplicaveis_FiltroSexo() {
        List<RegraAutorizacao> regrasMasculino = dao.buscarRegrasAplicaveis("6789", 5, "M");
        List<RegraAutorizacao> regrasFeminino = dao.buscarRegrasAplicaveis("6789", 5, "F");

        assertNotNull(regrasMasculino);
        assertNotNull(regrasFeminino);
        assertEquals(1, regrasMasculino.size());
        assertEquals(1, regrasFeminino.size());

        assertEquals("M", regrasMasculino.get(0).getSexoNecessario());
        assertEquals("F", regrasFeminino.get(0).getSexoNecessario());

        assertNotEquals(regrasMasculino.get(0).getId(), regrasFeminino.get(0).getId());
    }

    @Test
    @DisplayName("Deve lidar com código de procedimento null sem lançar exceção")
    void testBuscarRegrasAplicaveis_CodigoNull() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis(null, 25, "M");

        assertNotNull(regras);
        assertTrue(regras.isEmpty());
    }

    @Test
    @DisplayName("Deve lidar com sexo null sem lançar exceção")
    void testBuscarRegrasAplicaveis_SexoNull() {
        List<RegraAutorizacao> regras = dao.buscarRegrasAplicaveis("1234", 25, null);

        assertNotNull(regras);
    }
}