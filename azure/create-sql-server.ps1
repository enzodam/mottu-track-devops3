param(
  [string]$SubscriptionId = "",
  [string]$ResourceGroup = "rg-challenge3-devops-558438",
  [string]$ResourceGroupLocation = "eastus",
  [string]$SqlLocation = "brazilsouth",  # se bloquear, troque p/ southcentralus, northcentralus, mexicocentral ou canadacentral
  [string]$SqlServerName = "sql-challenge3-devops-558438",
  [string]$SqlDbName = "db_challenge3-devops",
  [string]$SqlAdminUser = "sqladmin558438",
  [string]$SqlAdminPassword = "SenhaForte!558438",
  [string]$DdlPath = "./script_bd.sql"
)
if ($SubscriptionId -ne "") { az account set --subscription $SubscriptionId }

# RG já existe em eastus - só garante
az group show -n $ResourceGroup 1>$null 2>$null
if ($LASTEXITCODE -ne 0) { az group create --name $ResourceGroup --location $ResourceGroupLocation }

# SQL no SqlLocation
az sql server create `
  --name $SqlServerName `
  --resource-group $ResourceGroup `
  --location $SqlLocation `
  --admin-user $SqlAdminUser `
  --admin-password $SqlAdminPassword

# Firewall: seu IP + Azure services
$myIp = (Invoke-WebRequest -Uri "http://ifconfig.me/ip").Content
az sql server firewall-rule create -g $ResourceGroup -s $SqlServerName -n AllowMyIP --start-ip-address $myIp --end-ip-address $myIp
az sql server firewall-rule create -g $ResourceGroup -s $SqlServerName -n AllowAzure --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

# DB
az sql db create -g $ResourceGroup -s $SqlServerName -n $SqlDbName --service-objective S0

# DDL
sqlcmd -S "$($SqlServerName).database.windows.net" -d $SqlDbName -U $SqlAdminUser -P $SqlAdminPassword -i $DdlPath

# Secrets p/ GitHub
Write-Host "SPRING_DATASOURCE_URL=jdbc:sqlserver://$($SqlServerName).database.windows.net:1433;database=$SqlDbName;encrypt=true;trustServerCertificate=false;loginTimeout=30;"
Write-Host "SPRING_DATASOURCE_USERNAME=$SqlAdminUser@$SqlServerName"
Write-Host "SPRING_DATASOURCE_PASSWORD=$SqlAdminPassword"

Write-Host "==== Secrets para configurar no GitHub ===="
Write-Host "SPRING_DATASOURCE_URL=jdbc:sqlserver://$($SqlServerName).database.windows.net:1433;database=$SqlDbName;encrypt=true;trustServerCertificate=false;loginTimeout=30;"
Write-Host "SPRING_DATASOURCE_USERNAME=$SqlAdminUser@$SqlServerName"
Write-Host "SPRING_DATASOURCE_PASSWORD=$SqlAdminPassword"
