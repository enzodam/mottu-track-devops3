#!/usr/bin/env bash
set -euo pipefail

# ===============================
# Script de deploy App Service
# Projeto: challenge3-devops
# RM: 558438
# ===============================

SUBSCRIPTION_ID=""
LOCATION="eastus"
RG_NAME="rg-challenge3-devops-558438"
PLAN_NAME="plan-challenge3-devops-558438"
WEBAPP_NAME="app-challenge3-devops-558438"
JAVA_VERSION="JAVA:17"

if [[ -n "$SUBSCRIPTION_ID" ]]; then
  az account set --subscription "$SUBSCRIPTION_ID"
fi

echo ">> Registrando providers (idempotente)"
az provider register --namespace Microsoft.Web || true
az provider register --namespace Microsoft.Insights || true
az provider register --namespace Microsoft.OperationalInsights || true
az provider register --namespace Microsoft.ServiceLinker || true
az provider register --namespace Microsoft.Sql || true

echo ">> Criando Resource Group: $RG_NAME"
az group create -n "$RG_NAME" -l "$LOCATION"

echo ">> Criando App Service Plan (Linux)"
az appservice plan create -g "$RG_NAME" -n "$PLAN_NAME" --sku B1 --is-linux

echo ">> Criando Web App Java 17 (Linux): $WEBAPP_NAME"
az webapp create -g "$RG_NAME" -p "$PLAN_NAME" -n "$WEBAPP_NAME" --runtime "$JAVA_VERSION"

echo ">> Web App criado. URL:"
echo "https://$WEBAPP_NAME.azurewebsites.net"
echo "==== Configure a repository variable WEBAPP_NAME=$WEBAPP_NAME (GitHub → Settings → Secrets and variables → Actions → Variables) ===="
