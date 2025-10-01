# DevOps Tools & Cloud Computing — Entrega (Opção: Azure App Service + Azure SQL)

**Projeto:** challenge3-devops  
**Aluno:** Enzo Dias Alfaia Mendes  
**RM:** 558438  

> **Stack**: Java 17 (Spring Boot) + Azure App Service (PaaS) + Azure SQL Database (PaaS) + GitHub Actions (CI/CD)

Este repositório contém **scripts, DDL, pipeline e passo a passo** para publicar uma API Java (Spring Boot)
no **Azure App Service** com **banco Azure SQL**. Tudo criado via **Azure CLI** e **PowerShell**, conforme os requisitos da disciplina.

---

## 1) Descrição da Solução
API REST (Spring Boot) com CRUD completo sobre a tabela `motos`.  
A API expõe endpoints REST para **criar**, **listar**, **atualizar** e **excluir** motos.  
O backend persiste em **Azure SQL** (`db_challenge3-devops`).

## 2) Benefícios para o Negócio
- **Disponibilidade em nuvem (PaaS)** com escala automática do App Service.
- **Pipeline CI/CD**: a cada commit no `main`, o GitHub Actions **builda** e **faz deploy** automaticamente.
- **Custo reduzido**: sem gerenciar VMs/patching; backup/alta disponibilidade gerenciados pela Azure.
- **Confiabilidade**: Banco em Azure SQL, redundância geográfica e segurança de dados.

## 3) Arquitetura da Solução

```
Dev → GitHub (repo challenge3-devops-558438) ──(commit/push)──► GitHub Actions (build + package)
                                                    │
                                                    └─► Deploy ► Azure App Service (Java 17, Linux)
                                                                  │
                                                                  └─► Azure SQL Database (db_challenge3-devops)
```

Recursos Azure:
- Resource Group: `rg-challenge3-devops-558438`
- App Service Plan (Linux): `plan-challenge3-devops-558438`
- Web App (Java 17): `app-challenge3-devops-558438`
- Azure SQL Server: `sql-challenge3-devops-558438`
- Azure SQL Database: `db_challenge3-devops`

---

## 4) Pré-requisitos

- Conta Azure ativa e Azure CLI logado (`az login`).
- Cloud Shell (PowerShell + Bash) **ou** terminal local com Azure CLI + PowerShell.
- Repositório GitHub com sua API Spring Boot e permissão para adicionar **Secrets**.

### Registrar provedores (uma vez por conta)
```bash
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.Insights
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ServiceLinker
az provider register --namespace Microsoft.Sql
```

---

## 5) Criação da Infra (CLI)

### 5.1 Banco (PowerShell no Cloud Shell)
Envie os arquivos `azure/create-sql-server.ps1` e `script_bd.sql` e execute:

```powershell
.zure\create-sql-server.ps1
```

Esse script cria RG, SQL Server, Database, libera firewall do seu IP e executa o `script_bd.sql` (DDL + inserts).  
No final ele imprime as 3 variáveis para você configurar como **Secrets** no GitHub.

### 5.2 App Service (Bash no Cloud Shell)
Envie o script `azure/deploy-app.sh`, torne executável e execute:

```bash
chmod +x azure/deploy-app.sh
./azure/deploy-app.sh
```

Esse script cria o **App Service Plan Linux** e o **Web App Java 17**.  
A URL ficará como: `https://app-challenge3-devops-558438.azurewebsites.net`.

---

## 6) Configurar Secrets no GitHub
Vá em *Settings → Secrets and variables → Actions* e crie:
- `AZURE_WEBAPP_PUBLISH_PROFILE` → (conteúdo do Publish Profile do Web App)
- `SPRING_DATASOURCE_URL` → `jdbc:sqlserver://sql-challenge3-devops-558438.database.windows.net:1433;database=db_challenge3-devops;encrypt=true;trustServerCertificate=false;loginTimeout=30;`
- `SPRING_DATASOURCE_USERNAME` → `sqladmin558438@sql-challenge3-devops-558438`
- `SPRING_DATASOURCE_PASSWORD` → `SenhaForte!558438`

No seu `application.properties` da API, garanta:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

---

## 7) Pipeline CI/CD (GitHub Actions)
Arquivo: `.github/workflows/azure-webapp-java.yml`  
- Build com **Maven (Java 17)**
- Deploy via `azure/webapps-deploy` usando o **Publish Profile**

Para definir o nome do Web App no ambiente do repositório, crie uma **Repository Variable** chamada `WEBAPP_NAME` com valor: `app-challenge3-devops-558438` (Settings → Secrets and variables → Actions → *Variables*).

---

## 8) Endpoints do CRUD (exemplo)
- `GET    /motos` → lista todas
- `GET    /motos/{id}` → busca por id
- `POST   /motos` → cria
- `PUT    /motos/{id}` → atualiza
- `DELETE /motos/{id}` → exclui

### Exemplos (curl)
```bash
API_BASE="https://app-challenge3-devops-558438.azurewebsites.net"

# criar (2 registros)
curl -X POST "$API_BASE/motos" -H "Content-Type: application/json" -d '{"placa":"ABC1D23","modelo":"Honda CG 160","ano":2022,"disponivel":true}'
curl -X POST "$API_BASE/motos" -H "Content-Type: application/json" -d '{"placa":"XYZ9Z99","modelo":"Yamaha Fazer 250","ano":2024,"disponivel":false}'

# listar
curl "$API_BASE/motos"

# atualizar
curl -X PUT "$API_BASE/motos/1" -H "Content-Type: application/json" -d '{"placa":"ABC1D23","modelo":"Honda CG 160","ano":2023,"disponivel":false}'

# excluir
curl -X DELETE "$API_BASE/motos/2"
```

> Mostre também um `SELECT` no banco após cada operação no vídeo.

---

## 9) Limpeza
```bash
az group delete -n rg-challenge3-devops-558438 --yes --no-wait
```

---

## 10) Estrutura
```
.
├── README.md
├── script_bd.sql
├── azure/
│   ├── create-sql-server.ps1
│   └── deploy-app.sh
├── .github/workflows/azure-webapp-java.yml
└── docs/
    └── ENTREGA_DEVOPS.pdf
```

**Data:** 2025-10-01
