# 🚀 JobPlatform API - DevOps & Cloud Computing

Este projeto é uma API de gerenciamento de vagas e candidatos, desenvolvida em Java (Spring Boot) e provisionada na nuvem Azure utilizando práticas de DevOps (Azure DevOps, Azure CLI) e Continuous Integration/Continuous Deployment (CI/CD).

## 💡 Arquitetura da Solução

O projeto segue uma arquitetura moderna baseada em microsserviços e serviços PaaS (Platform as a Service) do Azure, garantindo escalabilidade e alta disponibilidade.

### Componentes Chave

| **Componente**        | **Tecnologia / Serviço Azure**    | **Função**                                                                                                           |
|-----------------------|-----------------------------------|----------------------------------------------------------------------------------------------------------------------|
| **API Backend**       | Java 21 (Spring Boot), Maven      | Lógica de negócio, autenticação JWT, integração com o DB e serviços externos.                                        |
| **PaaS (Deployment)** | Azure App Service (Linux)         | Hospedagem da API. Configurado com runtime Java 21.                                                                  |
| **Banco de Dados**    | Azure SQL Database                | Armazenamento persistente de dados de vagas, candidatos e usuários.                                                  |
| **Infraestrutura**    | Azure CLI (scripts Bash)          | Provisionamento automatizado do Resource Group, Servidor SQL, Database e App Service Plan/Web App.                   |
| **CI/CD**             | Azure Pipelines (Build e Release) | Automação completa do fluxo de trabalho: compilação, testes, empacotamento (artefato JAR), provisionamento e deploy. |

## 🛠️ Pré-requisitos e Configuração

Para executar e provisionar o projeto, são necessárias as seguintes ferramentas e contas configuradas:

1. **Conta Azure** com assinatura ativa.
2. **Azure DevOps** com um Projeto e Service Connection configurados.
3. **Código-fonte da API** (Java/Spring Boot).
4. **Variáveis de Pipeline** (Secrets e Configurações):
    - `RESOURCE_GROUP_NAME`: Nome do grupo de recursos (Ex: `rg-jobplatform`).
    - `LOCATION`: Região do Azure (Ex: `brazilsouth`).
    - `DB_USER`: Usuário administrador do SQL Server.
    - `DB_PWD`: **Secret** - Senha do SQL Server.
    - `DB_SERVER_NAME`: Nome do servidor SQL.
    - `JWT_SECRET_TOKEN`: **Secret** - Chave secreta para JWT.
    - `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PWD`: **Secrets** - Configurações de serviço de e-mail (se aplicável).
    - `AMQP_ADDRESS`: **Secret** - Endereço do serviço de mensageria (RabbitMQ, se aplicável).
    - `DEEPSEEK_API_KEY`: **Secret** - Chave da API DeepSeek (se aplicável).

## ⚙️ Fluxo de CI/CD (Azure Pipelines)

### Pipeline de Build (CI)

1. **Checkout**: Baixa o código-fonte do repositório (Git).
2. **Maven Task**:
    - Compila o código-fonte.
    - Executa os **Testes Unitários e de Integração**.
    - Gera a cobertura de código.
3. **Copy Files**: Copia o artefato JAR gerado e os logs de teste/cobertura.
4. **Publish Test Results**: Publica os resultados dos testes no Azure DevOps.
5. **Publish Artifact**: Publica o arquivo `.jar` como artefato (`drop`).

### Pipeline de Release (CD)

1. **Artifact Consumption**: Baixa o artefato (`.jar`) da Pipeline de Build.
2. **Provisionamento de Infraestrutura (Azure CLI)**:
    - Executa o script `scripts/script-infra-provisionamento.sh`.
    - Cria/atualiza o Resource Group, Azure SQL Server/Database, App Service Plan e Web App.
    - Configura as variáveis de ambiente (incluindo as credenciais do DB).
3. **Deploy to Azure Web App**:
    - Faz o deploy do artefato JAR para o App Service.
    - Reinicia o serviço para que a aplicação Spring Boot inicie com as novas configurações.