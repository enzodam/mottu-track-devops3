# ===============================
# Script de criação do SQL Server
# Projeto: challenge3-devops
# RM: 558438
# ===============================
param(
    [string]$SubscriptionId = "",
    [string]$Location = "eastus",
    [string]$ResourceGroup = "rg-challenge3-devops-558438",
    [string]$SqlServerName = "sql-challenge3-devops-558438",
    [string]$SqlDbName = "db_challenge3-devops",
    [string]$SqlAdminUser = "sqladmin558438",
    [string]$SqlAdminPassword = "SenhaForte!558438",
    [string]$DdlPath = "./script_bd.sql"
)

if ($SubscriptionId -ne "") { az account set --subscription $SubscriptionId }

az group create --name $ResourceGroup --location $Location

az sql server create `
  --name $SqlServerName `
  --resource-group $ResourceGroup `
  --location $Location `
  --admin-user $SqlAdminUser `
  --admin-password $SqlAdminPassword

$myIp = (Invoke-WebRequest -uri "http://ifconfig.me/ip").Content
az sql server firewall-rule create -g $ResourceGroup -s $SqlServerName -n AllowMyIP --start-ip-address $myIp --end-ip-address $myIp

az sql db create -g $ResourceGroup -s $SqlServerName -n $SqlDbName --service-objective S0

sqlcmd -S "$($SqlServerName).database.windows.net" -d $SqlDbName -U $SqlAdminUser -P $SqlAdminPassword -i $DdlPath

Write-Host "==== Secrets para configurar no GitHub ===="
Write-Host "SPRING_DATASOURCE_URL=jdbc:sqlserver://$($SqlServerName).database.windows.net:1433;database=$SqlDbName;encrypt=true;trustServerCertificate=false;loginTimeout=30;"
Write-Host "SPRING_DATASOURCE_USERNAME=$SqlAdminUser@$SqlServerName"
Write-Host "SPRING_DATASOURCE_PASSWORD=$SqlAdminPassword"
