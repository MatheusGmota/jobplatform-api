#!/bin/bash

echo "Iniciando o provisionamento de recursos do Azure..."
################################################################################
#                   CONFIGURAÇÃO DE VARIÁVEIS                                  #
################################################################################
RESOURCE_GROUP=${RESOURCE_GROUP_NAME}
LOCATION=${LOCATION}
PROJECT_NAME="jobplatform"
APP_NAME="${PROJECT_NAME}-api"
PLAN_NAME="${PROJECT_NAME}-plan"
DB_SERVER_NAME="${PROJECT_NAME}-db"
DB_ADMIN_USER=${DB_USER}

JWT_SECRET_TOKEN=${JWT_SECRET_TOKEN}
MAIL_HOST=${MAIL_HOST}
MAIL_PORT=${MAIL_PORT}
MAIL_USER=${MAIL_USER}
MAIL_PWD=${MAIL_PWD}
AMQP_ADDRESS=${AMQP_ADDRESS}
AMQP_QUEUE=${AMQP_QUEUE}
DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}
DEEPSEEK_URL="https://api.deepseek.com/v1"

SPRING_BOOT_PORT="8080"
DB_NAME="jobplatformdb"
DB_PASSWORD=${DB_PWD}

################################################################################
#                           1. CRIAÇÃO DO RESOURCE GROUP                       #
################################################################################

echo "1. Criando Resource Group: $RESOURCE_GROUP em $LOCATION..."
az group create \
    --name $RESOURCE_GROUP \
    --location $LOCATION \

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Resource Group."
    exit 1
fi

################################################################################
#                       2. CRIAÇÃO DO BANCO DE DADOS POSTGRESQL                #
################################################################################

echo "2. Criando Azure Database for PostgreSQL Flexible Server: $DB_SERVER_NAME..."
az postgres flexible-server create \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --name $DB_SERVER_NAME \
    --admin-user $DB_ADMIN_USER \
    --admin-password $DB_PASSWORD \
    --sku-name Standard_B1ms \
    --tier Burstable \
    --version 16 \
    --storage-size 32 \
    --public-access 0.0.0.0

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o PostgreSQL Flexible Server. Verifique as restrições de SKUs e a senha."
    exit 1
fi

# Obtém o host do DB para as variáveis da aplicação
DB_HOST=$(az postgres flexible-server show \
    --resource-group $RESOURCE_GROUP \
    --name $DB_SERVER_NAME \
    --query "fullyQualifiedDomainName" \
    --output tsv)

echo "PostgreSQL Host: $DB_HOST"

# Criação da Base de Dados inicial
echo "Criando o banco de dados '$DB_NAME' no servidor..."
az postgres flexible-server execute \
    --name $DB_SERVER_NAME \
    --admin-user $DB_ADMIN_USER \
    --admin-password $DB_PASSWORD \
    --querytext "CREATE DATABASE $DB_NAME;" \
    --database-name postgres

if [ $? -ne 0 ]; then
    echo "AVISO: Falha ao criar a base de dados '$DB_NAME'. Você precisará criá-la manualmente ou via script SQL."
fi

# Configuração da Regra de Firewall para o Serviço Azure (Obrigatório para Web App)
echo "Configurando regra de firewall para permitir acesso do Azure Web App..."
az postgres flexible-server firewall-rule create \
    --resource-group $RESOURCE_GROUP \
    --name $DB_SERVER_NAME \
    --rule-name AllowAzureServices \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0

################################################################################
#                       3. CRIAÇÃO DO APP SERVICE PLAN (PaaS)                  #
################################################################################

echo "3. Criando App Service Plan: $PLAN_NAME..."
az appservice plan create \
    --name $PLAN_NAME \
    --resource-group $RESOURCE_GROUP \
    --location $LOCATION \
    --is-linux \
    --sku F1

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
    --runtime "JAVA|21-java21" \
    --deployment-container-image-enabled false

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao criar o Web App."
    exit 1
fi

################################################################################
#                       5. CONFIGURAÇÃO DAS VARIÁVEIS DE AMBIENTE              #
################################################################################

echo "5. Configurando variáveis de ambiente (App Settings)..."

# Constrói a URL de conexão do DB para o Spring Boot
DB_URL="jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}"
DB_USER="${DB_ADMIN_USER}@${DB_SERVER_NAME}"

# Cria um array de variáveis de ambiente para a API
APP_SETTINGS=(
    # Variáveis do DB
    "DB_URL=$DB_URL"
    "DB_USER=$DB_USER"
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
echo "Recursos criados no Resource Group: $RESOURCE_GROUP"
echo "Web App URL: https://${APP_NAME}.azurewebsites.net"
echo " "