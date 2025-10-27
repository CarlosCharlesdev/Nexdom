# Usa a imagem oficial do WildFly da Red Hat
FROM quay.io/wildfly/wildfly:latest

# Define o caminho do banco H2
# Adicionei ":file:" para clareza e "DB_CLOSE_ON_EXIT=FALSE" para robustez
ENV DB_URL=jdbc:h2:file:/opt/jboss/wildfly/h2data/autorizacoes;DB_CLOSE_ON_EXIT=FALSE;

# 1. PREPARAÇÃO DO VOLUME E PERMISSÕES

# Garante que o diretório para o H2 exista dentro da imagem
RUN mkdir -p /opt/jboss/wildfly/h2data

# Garante que o usuário 'jboss' (padrão do WildFly) tenha permissão de escrita/criação
# no diretório que será persistido.
# Isso resolve o problema de permissão (se esse era o erro H2).
RUN chown -R jboss:jboss /opt/jboss/wildfly/h2data
RUN chmod -R 775 /opt/jboss/wildfly/h2data

# Define o ponto de montagem para persistência de dados
VOLUME /opt/jboss/wildfly/h2data

# 2. DEPLOY DA APLICAÇÃO

# Muda para o usuário 'jboss' (padrão)
USER jboss

# Copia o WAR gerado pelo Maven para o diretório de deploy do WildFly
COPY target/*.war /opt/jboss/wildfly/standalone/deployments/

# 3. EXECUÇÃO

# Expõe a porta padrão
EXPOSE 8080

# Comando padrão do container
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]