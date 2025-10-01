# Mottu Track – Spring Boot + PostgreSQL + Azure App Service

Aplicação web (Thymeleaf) para gestão de locações de motos.  
CRUD completo em **Moto** com **PostgreSQL** na Azure e deploy **PaaS** no **Azure App Service** via **Azure CLI**.

## 1) Descrição da solução
- Cadastro e manutenção de **Motos** (inclui listar, criar, atualizar e excluir).
- Integração com **PostgreSQL Flexible Server** (Azure).
- **Flyway** aplica/valida o schema na inicialização.
- Empacotamento com **Spring Boot Loader** (JAR executável).

## 2) Benefícios para o negócio
- Centraliza o cadastro e status de motos por filial.
- Acelera locação/devolução, reduz erros e retrabalho.
- Base única para relatórios e tomada de decisão.

## 3) Arquitetura

```mermaid
flowchart LR
  User[Usuário Web] -->|HTTPS| App[Azure App Service (Java 17)]
  App -->|JDBC + Flyway| PG[(Azure PostgreSQL Flexible Server)]
  subgraph Azure
    App
    PG
  end
  ```
4) Pré-requisitos
Java 17 + Maven 3.9+

Azure CLI autenticado: az login

Uma subscription selecionada: az account set --subscription "<id-ou-nome>"

5) Configuração local e build
bash
Copiar código
# compila e gera jar com Boot Loader
mvn -DskipTests clean package spring-boot:repackage

# (opcional) rodar local
java -jar target/mottu-track-api-0.0.1-SNAPSHOT.jar
Verifique o MANIFEST:

bash
Copiar código
unzip -p target/mottu-track-api-0.0.1-SNAPSHOT.jar META-INF/MANIFEST.MF | grep -E "Main-Class|Start-Class"
# Deve ter:
# Main-Class: org.springframework.boot.loader.launch.JarLauncher
# Start-Class: br.com.fiap.mottuapp.MottuAppApplication
6) Provisionamento na Azure (CLI)
Opção App Service (requisito da disciplina).

Defina variáveis (ajuste se quiser):

bash
Copiar código
RG="rg-challenge3-devops-558438"
LOC="eastus"
PLAN="plan-challenge3-devops-558438"
APP="app-challenge3-devops-558438"

PG="pg-challenge3-devops-558438"
PG_ADMIN="pgadmin558438"
PG_PASS="OutraSenha!F0rte_992"
PG_DB="db_challenge3_devops"
Crie recursos (pule este bloco se você já criou com os mesmos nomes):

bash
Copiar código
az group create -n $RG -l $LOC
az appservice plan create -g $RG -n $PLAN --sku B1 --is-linux
az webapp create -g $RG -p $PLAN -n $APP --runtime "JAVA|17-java17"
az webapp config set -g $RG -n $APP --always-on true

az postgres flexible-server create \
  -g $RG -n $PG -l $LOC \
  --version 16 \
  --tier Burstable --sku-name B1ms --storage-size 32 \
  --administrator-user $PG_ADMIN --administrator-password $PG_PASS \
  --yes

PG_HOST=$(az postgres flexible-server show -g $RG -n $PG --query fullyQualifiedDomainName -o tsv)
az postgres flexible-server db create -g $RG -s $PG -d $PG_DB
App Settings (necessário mesmo que os recursos já existam):

bash
Copiar código
PG_HOST=$(az postgres flexible-server show -g $RG -n $PG --query fullyQualifiedDomainName -o tsv)
SPRING_URL="jdbc:postgresql://$PG_HOST:5432/$PG_DB?sslmode=require"

az webapp config appsettings set -g $RG -n $APP --settings \
  SPRING_DATASOURCE_URL="$SPRING_URL" \
  SPRING_DATASOURCE_USERNAME="$PG_ADMIN" \
  SPRING_DATASOURCE_PASSWORD="$PG_PASS" \
  JAVA_TOOL_OPTIONS="-Dserver.port=8080" \
  WEBSITES_PORT=8080
7) Deploy do JAR
bash
Copiar código
# garante jar correto
mvn -DskipTests clean package spring-boot:repackage

JAR=$(ls -1 target/*.jar | grep -vE 'sources|javadoc' | head -n1)

# envia com OneDeploy
az webapp deploy -g $RG -n $APP --type jar --src-path "$JAR"

# reinicia e acompanha logs
az webapp restart -g $RG -n $APP
az webapp log config -g $RG -n $APP --application-logging filesystem --level information
az webapp log tail   -g $RG -n $APP
Acesse: https://$APP.azurewebsites.net/

8) Testes (CRUD Moto)
Exemplos (ajuste rota se necessário):

bash
Copiar código
BASE="https://$APP.azurewebsites.net"

# Criar 1
curl -s -X POST "$BASE/motos" -H "Content-Type: application/json" -d '{
  "placa":"ABC1D23","modelo":"Honda CG 160","ano":2022,"status":"DISPONIVEL","filialId":1
}'

# Criar 2
curl -s -X POST "$BASE/motos" -H "Content-Type: application/json" -d '{
  "placa":"XYZ4E56","modelo":"Yamaha Fazer 250","ano":2023,"status":"DISPONIVEL","filialId":1
}'

# Listar
curl -s "$BASE/motos"

# Atualizar (id=1)
curl -s -X PUT "$BASE/motos/1" -H "Content-Type: application/json" -d '{
  "placa":"ABC1D23","modelo":"Honda CG 160 Start","ano":2022,"status":"LOCADA","filialId":1
}'

# Buscar por id
curl -s "$BASE/motos/1"

# Deletar (id=2)
curl -s -X DELETE "$BASE/motos/2"
9) DDL exigido (script_bd.sql)
Crie na raiz do repositório um arquivo script_bd.sql com a DDL abaixo (usada pelo Flyway):

sql
Copiar código
CREATE TABLE IF NOT EXISTS filial (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cidade VARCHAR(60) NOT NULL,
  uf CHAR(2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE filial IS 'Filiais da Mottu';
COMMENT ON COLUMN filial.uf IS 'UF da filial';

CREATE TABLE IF NOT EXISTS moto (
  id BIGSERIAL PRIMARY KEY,
  placa VARCHAR(10) NOT NULL UNIQUE,
  modelo VARCHAR(100) NOT NULL,
  ano INT NOT NULL CHECK (ano BETWEEN 1990 AND EXTRACT(YEAR FROM NOW())::INT + 1),
  status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
  filial_id BIGINT REFERENCES filial(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE moto IS 'Cadastro de motos';
COMMENT ON COLUMN moto.status IS 'DISPONIVEL | LOCADA | MANUTENCAO';

CREATE TABLE IF NOT EXISTS usuario (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  perfil VARCHAR(30) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS locacao (
  id BIGSERIAL PRIMARY KEY,
  usuario_id BIGINT NOT NULL REFERENCES usuario(id),
  moto_id BIGINT NOT NULL REFERENCES moto(id),
  retirada_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  devolucao_prevista TIMESTAMPTZ,
  devolucao_real TIMESTAMPTZ,
  valor_total NUMERIC(10,2)
);
10) Troubleshooting rápido
no main manifest attribute: refaça o build com mvn ... spring-boot:repackage e redeploy.

App travado em “Starting the site…”: confirme:

runtime JAVA|17-java17: az webapp config show -g $RG -n $APP --query linuxFxVersion

App Settings: WEBSITES_PORT=8080 e JAVA_TOOL_OPTIONS=-Dserver.port=8080

SPRING_DATASOURCE_* corretos (sem null).
