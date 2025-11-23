# 💼 JobPlatform API

Uma API RESTful para gerenciamento de vagas de emprego, candidatos e processos de aplicação, desenvolvida em Java com Spring Boot.

## 👥 Equipe

- Felipe Seiki Hashiguti - RM98985
- Lucas Corradini Silveira - RM555118
- Matheus Gregorio Mota - RM557254

## 💻 Tecnologias Utilizadas

Este projeto foi construído sobre uma arquitetura robusta, utilizando as seguintes tecnologias principais:

| Categoria           | Tecnologia                  | Versão/Detalhe                                                               |
|---------------------|-----------------------------|------------------------------------------------------------------------------|
| **Linguagem**       | Java                        | 21                                                                           |
| **Framework**       | Spring Boot                 | Utilizado para criar a aplicação e configurar a injeção de dependência.      |
| **Persistência**    | Spring Data JPA / Hibernate | Mapeamento Objeto-Relacional.                                                |
| **Banco de Dados**  | Azure SQL Database (MSSQL)  | Driver `com.microsoft.sqlserver.jdbc`.                                       |
| **Segurança**       | Spring Security             | Autenticação e Autorização baseada em JWT.                                   |
| **Mensageria**      | RabbitMQ (AMQP)             | Para processamento assíncrono (configurações `AMQP_ADDRESS` e `AMQP_QUEUE`). |
| **Provisionamento** | Azure CLI                   | Script `script-infra-provisionamento.sh` para IaC no Azure.                  |
| **CI/CD**           | Azure Pipelines             | Integração e Deploy Contínuo.                                                |
| **Desenvolvimento** | Maven                       | Gerenciamento de dependências e build.                                       |

## 🚀 Como Rodar o Projeto Localmente

### Pré-requisitos

1. Java JDK 21 instalado.
2. Maven instalado.
3. Um banco de dados SQL Server (ou Docker/serviço local) acessível.

### Passos

1. **Clone o Repositório:**
   ```
   git clone [https://www.youtube.com/watch?v=BEsAXYPulBo](https://www.youtube.com/watch?v=BEsAXYPulBo)
   cd [nome-do-repositorio]

   ```
2. **Configurar o** `application.yml`**:**
    - Crie um arquivo de propriedades ou utilize as variáveis de ambiente para configurar a conexão com o banco de dados e os secrets.
    - Exemplo de variáveis de ambiente para o Spring Boot:
      ```
      export SPRING_DATASOURCE_URL="jdbc:sqlserver://localhost:1433;databaseName=jobdb"
      export SPRING_DATASOURCE_USERNAME="sa"
      export SPRING_DATASOURCE_PASSWORD="SuaSenhaForte"
      export JWT_SECRET="sua-chave-secreta-forte-aqui"
 
      ```
3. **Compilar e Rodar:**
   ```
   # Limpa, compila e empacota o projeto
   mvn clean package

   # Executa o arquivo JAR gerado
   java -jar target/jobplatform-0.0.1-SNAPSHOT.jar

   ```

A API estará acessível em `http://localhost:8080`. A documentação (Swagger/OpenAPI) estará disponível em `http://localhost:8080/swagger-ui.html` (assumindo que você usa a dependência `springdoc`).

## 🧪 Testes de API (Endpoints)

A API é estruturada em torno de quatro controladores principais: `auth`, `user`, `job` e `application`.

### 1. Auth Controller (Autenticação)

| HTTP   | Endpoint             | Descrição                                                  |
|--------|----------------------|------------------------------------------------------------|
| `POST` | `/api/auth/register` | Cadastra um novo `User` (Candidato ou Recrutador).         |
| `POST` | `/api/auth/login`    | Realiza o login e retorna um token JWT para acesso seguro. |

**Exemplo de Teste:**

- **POST** `/api/auth/register`:
```json
{
  "name": "string",
  "email": "user@example.com",
  "password": "string",
  "type": "COMPANY",
  "skills": [
    "string"
  ],
  "description": "string"
}
```
- **POST** `/api/auth/login`:
```json
   {
   "email": "user@example.com",
   "password": "string"
   }
```
### 2. User Controller (Usuários) - **Necessita JWT**

Gerencia dados de usuários. A operação é focada em manipular o próprio perfil do usuário logado ou, para um administrador, gerenciar outros usuários pelo ID.  

| HTTP     | Endpoint             | Descrição                                  |
|----------|----------------------|--------------------------------------------|
| `GET`    | `/api/usuarios/{id}` | Recupera os detalhes de um usuário.        |
| `PUT`    | `/api/usuarios/{id}` | Atualiza o perfil de um usuário existente. |
| `DELETE` | `/api/usuarios/{id}` | Remove um usuário (exclui o perfil).       |

**Exemplo de Teste (CRUD Usuário):**

- Obtenha o `JWT_TOKEN`.
- **GET** `/api/usuarios/1`: Recupere o usuário com ID 1, utilizando o token no cabeçalho `Authorization: Bearer [JWT_TOKEN]`.  
```json
{
  "id": 1,
  "name": "string",
  "email": "user@example.com",
  "password": "string",
  "type": "COMPANY",
  "skills": [
    "string"
  ],
  "description": "string"
}
```
- **PUT** `/api/usuarios/1`: Atualize o campo `skills` do usuário.
```json
{
  "id": 1,
  "name": "string",
  "email": "user@example.com",
  "password": "string",
  "type": "COMPANY",
  "skills": [
    "Java"
  ],
  "description": "string"
}
```

### 3. Job Controller (Vagas)

| Método HTTP | Endpoint         | Descrição                                                                 |
|-------------|------------------|---------------------------------------------------------------------------|
| `GET`       | `/api/jobs`      | Lista todas as vagas ativas disponíveis.                                 |
| `POST`      | `/api/jobs`      | Cria uma nova vaga (restrito a usuários com `UserRole` de Recrutador/Empresa). |
| `GET`       | `/api/jobs/{id}` | Recupera os detalhes de uma vaga específica.                             |
| `PUT`       | `/api/jobs/{id}` | Atualiza uma vaga existente (restrito ao criador/Recrutador).            |
| `DELETE`    | `/api/jobs/{id}` | Desativa/Remove uma vaga.                                                |


**Exemplo de Teste (CRUD Vagas):**

1. Obtenha o `JWT_TOKEN` (de um Recrutador).
2. **POST** `/api/jobs`: Crie uma nova vaga com `title`, `company`, `description`, etc.  
    ```json
    {
      "title": "string",
      "location": "string",
      "category": "string",
      "type": "CLT",
      "salary": "string",
      "description": "stringstringstringstringstringstringstringstringst",
      "requirements": [
        "string"
      ]  
    }
    ```
3. **GET** `/api/jobs`: Verifique se a nova vaga aparece na lista.
4. **PUT** `/api/jobs/{id}`: Altere o campo `salary` da vaga recém-criada.

### 4. Application Controller (Candidaturas) - **Necessita JWT**

Gerencia o processo de candidatura de um usuário a uma vaga.

| Método HTTP | Endpoint                     | Descrição                                                     |
|-------------|------------------------------|-----------------------------------------------------------------|
| `POST`      | `/api/application/apply`     | Permite que um `User` (Candidato) aplique para uma `Job`.      |
| `GET`       | `/api/application/obter/{id}` | Recupera os detalhes de uma candidatura específica.            |


**Exemplo de Teste (Processo de Aplicação):**

1. Obtenha o `JWT_TOKEN` do Candidato e o ID de uma vaga ativa (`jobId`).
2. **POST** `/api/application/apply`: Envie um JSON contendo o ID da vaga e a `coverLetter` (o ID do candidato é obtido do token).
   ```json
   {
     "jobId": 5,
     "candidateId": 1,
     "coverLetter": "Tenho grande interesse nesta vaga..."
   }

   ```
3. **GET** `/api/application/obter/{id}`: Após a aplicação, use o ID retornado para buscar o status da candidatura (`PENDING` por padrão).