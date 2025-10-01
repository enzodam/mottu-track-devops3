# Mottu Track – Spring Boot + PostgreSQL + Azure App Service

Aplicação web (Thymeleaf) para gestão de locações de motos: **Filial**, **Moto**, **Usuário** e **Locação**.  
CRUD completo em **Moto** com integração a **PostgreSQL** na nuvem (**Azure Database for PostgreSQL – Flexible Server**) e **deploy PaaS** no **Azure App Service** via **Azure CLI**.

## 📌 Benefício para o negócio
- Centraliza cadastro e disponibilidade de motos por filial.
- Acelera locação/devolução, reduz erros e retrabalho.
- Base única para relatórios e tomada de decisão.

---

## 🏗️ Arquitetura

```mermaid
flowchart LR
    User[Usuário Web] -->|HTTPS| App[Azure App Service (Java 17)]
    App -->|JDBC + Flyway| PG[(Azure Database for PostgreSQL)]
    subgraph Azure
      App
      PG
    end
App Service (Linux, Java 17) executa o JAR empacotado com Spring Boot Loader.

Flyway aplica/valida o schema no PostgreSQL na inicialização.

✅ Pré-requisitos
Java 17 + Maven 3.9+

Azure CLI autenticado: az login

Subscription selecionada: az account set --subscription "<id ou nome>"
