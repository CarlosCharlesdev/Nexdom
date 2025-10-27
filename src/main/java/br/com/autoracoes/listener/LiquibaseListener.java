package br.com.autoracoes.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import java.sql.Connection;
import java.sql.DriverManager;

@WebListener
public class LiquibaseListener implements ServletContextListener {

    // Configurações do H2 (DEVEM SER AS MESMAS DO liquibase.properties)
    private static final String URL = "jdbc:h2:file:./db/autorizacoes;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String CHANGELOG_FILE = "db/changelog/db/db.changelog-master.yaml";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Iniciando Liquibase...");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // O Liquibase precisa de uma conexão ativa.
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            // O ClassLoaderResourceAccessor busca o changelog em src/main/resources
            Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);

            // Executa o update (cria tabelas e insere regras)
            liquibase.update("main");

            System.out.println("Liquibase executado com sucesso! Tabelas criadas.");

        } catch (Exception e) {
            System.err.println("ERRO FATAL AO EXECUTAR LIQUIBASE: " + e.getMessage());
            e.printStackTrace();
            // Em caso de falha, é melhor parar a aplicação
            throw new RuntimeException("Falha ao inicializar o banco de dados com Liquibase.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Opcional: Limpar recursos, mas o H2 em memória já se fecha
        System.out.println("Contexto destruído.");
    }
}
