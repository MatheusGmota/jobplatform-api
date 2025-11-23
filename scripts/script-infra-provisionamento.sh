#!/bin/bash

echo "Iniciando o provisionamento de recursos do Azure para AZURE SQL..."

################################################################################
#                   CONFIGURAÇÃO DE VARIÁVEIS                                  #
################################################################################
RESOURCE_GROUP=$RESOURCE_GROUP_NAME
LOCATION=$LOCATION
PROJECT_NAME="jobplatform"
APP_NAME="${PROJECT_NAME}-api"
PLAN_NAME="${PROJECT_NAME}-plan"
DB_SERVER_NAME="${PROJECT_NAME}-db"
DB_ADMIN_USER=$DB_USER

JWT_SECRET_TOKEN=$JWT_SECRET_TOKEN
MAIL_HOST=$MAIL_HOST
MAIL_PORT=$MAIL_PORT
MAIL_USER=$MAIL_USER
MAIL_PWD=$MAIL_PWD
AMQP_ADDRESS=$AMQP_ADDRESS
AMQP_QUEUE=$AMQP_QUEUE
DEEPSEEK_API_KEY=$DEEPSEEK_API_KEY
DEEPSEEK_URL="https://api.deepseek.com/v1"

SPRING_BOOT_PORT="8080"
DB_NAME="jobplatformdb"
DB_PASSWORD=$DB_PWD

################################################################################
#                       FUNÇÕES DE AJUDA E VALIDAÇÃO                           #
################################################################################

# Verificação de variáveis críticas
if [ -z "$RESOURCE_GROUP" ] || [ -z "$LOCATION" ] || [ -z "$DB_PASSWORD" ]; then
    echo "ERRO CRÍTICO: Variáveis RESOURCE_GROUP_NAME, LOCATION ou DB_PWD estão vazias."
    echo "Verifique se foram setadas corretamente no Azure DevOps."
    exit 1
fi

# Verificação inicial do login (Geralmente feito na Task 'AzureCLI@2')
if ! az account show > /dev/null 2>&1; then
    echo "ERRO: O agente não está logado no Azure. Certifique-se de usar a task 'AzureCLI@2' e uma Service Connection."
    exit 1
fi

################################################################################
#                           1. CRIAÇÃO DO RESOURCE GROUP                       #
################################################################################

echo "1. Criando Resource Group: $RESOURCE_GROUP em $LOCATION..."
az group create \
    --name $RESOURCE_GROUP \
    --location $LOCATION

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Resource Group."
    exit 1
fi

################################################################################
#                       2. CRIAÇÃO DO BANCO DE DADOS AZURE SQL                 #
################################################################################

echo "2. Criando Azure SQL Server..."
az sql server create \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --name $DB_SERVER_NAME \
    --admin-user $DB_ADMIN_USER \
    --admin-password $DB_PASSWORD \
    --enable-public-network 'true'

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Azure SQL Server."
    exit 1
fi

# CORREÇÃO: Aguarda um tempo MAIOR para que o servidor se estabeleça. (30 segundos)
echo "Aguardando 30 segundos para o servidor SQL se estabilizar..."
sleep 30

echo "Criando Azure SQL Database: $DB_NAME (Tier Basic, o mais econômico)..."
# Criação do banco de dados (dentro do servidor lógico)
# CORREÇÃO: Adicionado --max-size 1GB para garantir que o tier Basic seja aceito
az sql db create \
    --resource-group $RESOURCE_GROUP \
    --server $DB_SERVER_NAME \
    --name $DB_NAME \
    --edition Basic \
    --service-objective Basic \
    --max-size 1GB \
    --yes

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Azure SQL Database. Verifique se o provedor 'Microsoft.Sql' está registrado e se há limites de assinatura."
    exit 1
fi

# Obtém o host do DB para as variáveis da aplicação
DB_HOST=$(az sql server show \
    --resource-group $RESOURCE_GROUP \
    --name $DB_SERVER_NAME \
    --query "fullyQualifiedDomainName" \
    --output tsv)

echo "SQL Server Host: $DB_HOST"

# Configuração da Regra de Firewall para o Serviço Azure (Obrigatório para Web App)
echo "Configurando regra de firewall para permitir acesso do Azure Web App (Regra 0.0.0.0)..."
# Permite que todos os serviços Azure acessem o SQL Server
az sql server firewall-rule create \
    --resource-group $RESOURCE_GROUP \
    --server $DB_SERVER_NAME \
    --name AllowAzureServices \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0

################################################################################
#                       3. CRIAÇÃO DO APP SERVICE PLAN (PaaS)                  #
################################################################################

echo "3. Criando App Service Plan: $PLAN_NAME (Tier Gratuito F1)..."
az appservice plan create \
    --name $PLAN_NAME \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --is-linux \
    --sku F1 # SKU F1 (Gratuito) para maximizar recursos gratuitos

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o App Service Plan."
    exit 1
fi

################################################################################
#                       4. CRIAÇÃO DO WEB APP (API)                            #
################################################################################

echo "4. Criando Web App: $APP_NAME (Runtime Java 21, Tomcat)..."
az webapp create \
    --name $APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --plan $PLAN_NAME \
    --runtime "JAVA|21-java21"

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Web App."
    exit 1
fi

################################################################################
#                       5. CONFIGURAÇÃO DAS VARIÁVEIS DE AMBIENTE              #
################################################################################

echo "5. Configurando variáveis de ambiente (App Settings)..."

# Constrói a URL de conexão do DB para o Spring Boot (PROTOCOLO SQL Server)
# O driver MS SQL Server usa o nome do DB diretamente na URL
DB_URL="jdbc:sqlserver://${DB_HOST}:1433;databaseName=${DB_NAME};"
# Para Azure SQL, o nome de usuário é o admin user
DB_USER_AZURE=$DB_ADMIN_USER

# Cria um array de variáveis de ambiente para a API
APP_SETTINGS=(
    # Variáveis do DB (Requisito 16: Dados sensíveis)
    "DB_URL=$DB_URL"
    "DB_USER=$DB_USER_AZURE"
    "DB_PWD=$DB_PASSWORD"

    # Variáveis de E-mail (Externas)
    "MAIL_HOST=$MAIL_HOST"
    "MAIL_PORT=$MAIL_PORT"
    "MAIL_USER=$MAIL_USER"
    "MAIL_PWD=$MAIL_PWD"

    # Variáveis do RabbitMQ (Externas)
    "AMQP_ADDRESS=$AMQP_ADDRESS"
    "AMQP_QUEUE=$AMQP_QUEUE"

    # Variáveis de Segurança/Secrets
    "JWT_SECRET=$JWT_SECRET_TOKEN"
    "DEEPSEEK_API_KEY=$DEEPSEEK_API_KEY"
    "DEEPSEEK_URL=$DEEPSEEK_URL"

    # Configuração da porta para aplicações Spring Boot em PaaS
    "WEBSITES_PORT=$SPRING_BOOT_PORT"
    "PORT=$SPRING_BOOT_PORT"
)

# Executa a configuração
az webapp config appsettings set \
    --resource-group $RESOURCE_GROUP \
    --name $APP_NAME \
    --settings "${APP_SETTINGS[@]}"

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao configurar as variáveis de ambiente."
    exit 1
fi

echo " "
echo "✅ Provisionamento Concluído com Sucesso!"
echo "------------------------------------------------------------------"
echo "Web App URL: https://${APP_NAME}.azurewebsites.net"
echo "Host do DB: $DB_HOST"
echo " "