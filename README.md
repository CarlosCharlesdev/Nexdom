# NexDOM - Sistema de Controle de Autorizações de Procedimentos Médicos

## Objetivo

Implementar um Webapp JSP/Servlet para controle de autorizações de procedimentos médicos, utilizando Servlets, JSP, H2 (para persistência das regras), Liquibase e Maven, conforme os requisitos obrigatórios da atividade. O foco principal é a lógica de validação de regras (idade, sexo, procedimento).

## Requisitos Obrigatórios Atendidos

| Requisito | Status | Detalhes |
| :--- | :--- | :--- |
| **Servlets e JSP** | ✅ | Utilizados para controle de fluxo (`AutorizaServlet`, `CadastroServlet`) e interface (`cadastro.jsp`). |
| **Estrutura DB (H2)** | ✅ | H2 Database em modo arquivo (`jdbc:h2:file:./db/autorizacoes;DB_CLOSE_DELAY=-1`) para persistência estável das regras. |
| **Liquibase** | ✅ | Utilizado para versionamento e inicialização da tabela `regras_autorizacao`. |
| **Maven** | ✅ | Utilizado para gerenciamento de dependências e build do projeto (`war`). |
| **Documentação (README)** | ✅ | Este arquivo. |
| **Lógica de Validação** | ✅ | Implementada com prioridade de negação e justificativas detalhadas. |

## Requisitos Desejáveis Atendidos

| Requisito                             | Status | Detalhes                                                                   |
|:--------------------------------------| :--- |:---------------------------------------------------------------------------|
| **Estilizar Componentes (CSS)**       | ✅ | Estilização básica aplicada via `style.css` para melhor UX.                |
| **Configuração Externa**              | ✅ | Credenciais de DB lidas via Propriedades do Sistema (prática recomendada). |
| **Testes Unitarios**                  | ✅ | Construção de teste unitarios de cada categoria de demanda pedida .        |
| **Utilizar docker na solução.**       | ✅ | Feita a digitalização em container do mesmo produto necessario.            |

---

## Estrutura do Projeto

O projeto segue a estrutura padrão Maven para um Webapp (`war`):

*   `src/main/java/`: Código-fonte Java (Servlets, DAO, Model).
*   `src/main/resources/`: Arquivos de configuração (Liquibase).
*   `src/main/webapp/`: Arquivos Web (JSP, CSS, `web.xml`).

## Configuração e Execução

### 1. Pré-requisitos

*   JDK 17+
*   Apache Maven
*   Servidor de Aplicação Wildfly (30.0.1.Final ou superior, compatível com Jakarta EE 10)

### 2. Configuração de Credenciais (Prática Recomendada)

As credenciais do banco de dados são lidas via **Propriedades do Sistema** (`-D` no Java), garantindo que não estejam *hardcoded*. O `pom.xml` já as configura por padrão para o H2 embutido, mas podem ser sobrescritas:

*   `db.url`
*   `db.user`
*   `db.password`

### 3. Configuração do Banco de Dados (Liquibase)

O projeto utiliza H2 em modo arquivo. O Liquibase criará o arquivo `autorizacoes.mv.db` na raiz do projeto.

Execute o comando para criar a tabela `regras_autorizacao` e inserir os dados iniciais:

```bash
mvn liquibase:update
```

### 4. Compilação e Execução

**Passo 4.1: Configurar o Wildfly**

O `wildfly-maven-plugin` no `pom.xml` deve estar configurado para apontar para a sua instalação local do Wildfly:

```xml
<configuration>
    <jbossHome>C:\caminho\para\sua\instalacao\wildfly-VERSAO</jbossHome>
    <!-- Configuração das propriedades do DB -->
    <jvmArgs>
        <jvmArg>-Ddb.url=jdbc:h2:file:./db/autorizacoes;DB_CLOSE_DELAY=-1</jvmArg>
        <jvmArg>-Ddb.user=sa</jvmArg>
        <jvmArg>-Ddb.password=</jvmArg>
    </jvmArgs>
</configuration>
```

**Passo 4.2: Iniciar o Projeto**

Execute o comando para compilar, empacotar e fazer o *deploy* no Wildfly:

```bash
mvn clean wildfly:run
```

### 5. Acesso à Aplicação

Após a inicialização do Wildfly, acesse a aplicação:

*   **Página Inicial:** `http://localhost:8080/NexDOM/`
*   **Formulário de Cadastro:** `http://localhost:8080/NexDOM/cadastro`

## Lógica de Validação

A validação ocorre no `AutorizaServlet` e segue a seguinte prioridade:

1.  **Busca Regras Potenciais:** O DAO busca todas as regras que batem com o **Procedimento** e o **Sexo** (sem filtro de idade no SQL).
2.  **Filtro de Idade:** O Servlet filtra essas regras pela **Idade** do paciente usando *Java Streams*.
3.  **Prioridade de Negação:** Se alguma regra de negação (`resultado=FALSE`) se aplica, a solicitação é **NEGADA** imediatamente.
4.  **Prioridade de Autorização:** Se não houver negação, a primeira regra de autorização (`resultado=TRUE`) que se aplicar resulta em **AUTORIZADO**.
5.  **Justificativa:** O resultado é exibido na mesma tela (`cadastro.jsp`) com uma justificativa detalhada.

