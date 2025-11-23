#!/bin/bash

echo "Verificando o status do registro do provedor 'Microsoft.DBforPostgreSQL'..."

az provider register --namespace 'Microsoft.DBforPostgreSQL'

if [ $? -eq 0 ]; then
    echo "Registro do provedor 'Microsoft.DBforPostgreSQL' enviado. Isso pode levar alguns minutos."
else
    echo "ERRO: Falha ao enviar a solicitação de registro do provedor."
    exit 1
fi

echo "Aguardando 30 segundos e verificando o status do registro..."
sleep 30

STATUS=$(az provider show --namespace 'Microsoft.DBforPostgreSQL' --query "registrationState" --output tsv)

while [ "$STATUS" != "Registered" ]; do
    echo "Status atual: $STATUS. Aguardando mais 30 segundos..."
    sleep 30
    STATUS=$(az provider show --namespace 'Microsoft.DBforPostgreSQL' --query "registrationState" --output tsv)
done