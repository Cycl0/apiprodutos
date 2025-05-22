# API de Produtos

Documentação técnica completa: [`DOCUMENTACAO.md`](./DOCUMENTACAO.md).

## Pré-requisitos
- Java 17 ou superior
- Maven
- PostgreSQL
- (Opcional) Docker e Docker Compose

## Como rodar o projeto

### 1. Clonar o repositório
```
git clone <url-do-repositorio>
cd apiprodutos
```

### 2. Configurar o banco de dados
- Alterar se necessário usuário e senha padrão (`src/main/resources/application.properties` e `docker-compose.yml`):
  - usuário: `apiprodutos`
  - senha: `apiprodutos`
- Iniciar o banco
```
docker compose up
```

### 3. Instalar as dependências
```
mvn clean install
```

### 4. Rodar a aplicação
```
mvn spring-boot:run
```
A API vai subir na porta 8080.

## Documentação da API
A documentação interativa (Swagger) fica disponível em:
```
http://localhost:8080/swagger-ui.html
```