#!/usr/bin/env bash
set -euo pipefail

RG_NAME="rg-challenge3-devops-558438"
RG_LOCATION="eastus"          # RG já existe aqui; não mudar
PLAN_LOCATION="canadacentral" # App Service FREE (F1) no Canada Central
PLAN_NAME="plan-challenge3-devops-558438"
WEBAPP_NAME="app-challenge3-devops-558438"
JAVA_VERSION="JAVA:17"

# Garante RG (não recria em outra região)
az group show -n "$RG_NAME" >/dev/null 2>&1 || az group create -n "$RG_NAME" -l "$RG_LOCATION"

# Plano FREE Linux no Canadá
az appservice plan create -g "$RG_NAME" -n "$PLAN_NAME" --sku F1 --is-linux -l "$PLAN_LOCATION"

# Web App herda a região do plano
az webapp create -g "$RG_NAME" -p "$PLAN_NAME" -n "$WEBAPP_NAME" --runtime "$JAVA_VERSION"

echo ">> Web App URL:"
echo "https://$WEBAPP_NAME.azurewebsites.net"
