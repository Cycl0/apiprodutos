# Documentação Técnica da API de Produtos

## 1. Estrutura e Explicação das Camadas do Projeto

**Controller:** Esta camada atua como a interface primária para as requisições HTTP de entrada. É responsável por receber estas requisições, realizar a validação inicial dos dados de entrada e formular as respostas HTTP a serem retornadas ao cliente.
  - Exemplos no projeto: ProdutoController, CategoriaController.
  - Os controllers utilizam anotações como `@RestController` para identificação e para o mapeamento de métodos às operações HTTP (GET, POST, PUT, DELETE). Constituem também um ponto apropriado para o tratamento de erros e para o retorno de códigos de status HTTP correspondentes.

**Model:** As Models representam as entidades do domínio do sistema, como Produto ou Categoria. Elas definem a estrutura dos dados com os quais a API opera: Produto, Categoria.
  - São classes Java que realizam o mapeamento objeto-relacional com as tabelas do banco de dados. Anotações JPA, como `@Entity` para a classe e anotações de coluna para os atributos (implícitas ou explícitas), são utilizadas para essa finalidade. Nesta camada também são definidos os relacionamentos entre as entidades, como a associação de um Produto a uma Categoria.

**Service:** A camada de Service encapsula a lógica de negócio da aplicação. As regras de negócio, validações complexas e a orquestração de operações são implementadas: ProdutoService, CategoriaService.
  - As classes de serviço processam as chamadas originadas nos controllers. Suas responsabilidades incluem a execução de tarefas como validações de consistência, aplicação de regras específicas e a interação com a camada de Repository para a persistência ou recuperação de dados.

**Repository:** Esta camada é dedicada exclusivamente ao acesso e à manipulação de dados no banco de dados. Ela abstrai os detalhes da interação com a fonte de dado: ProdutoRepository, CategoriaRepository.
  - Consistem em interfaces que, comumente, estendem `JpaRepository` (Spring Data JPA). Isso provê um conjunto de métodos para operações CRUD (Create, Read, Update, Delete) sem a necessidade de implementação manual de consultas SQL. Métodos customizados podem ser declarados para consultas específicas, como a busca de um produto por nome ou a verificação de duplicidade de um identificador.

**DTO (Data Transfer Object):** Os DTOs são objetos utilizados para a transferência de dados entre as camadas, notadamente para a formatação das respostas da API. O uso de DTOs evita a exposição direta das entidades do Model e permite o envio de subconjuntos específicos de dados: DescontoResponse.
  - Como exemplo, em vez de retornar o objeto Produto completo, um DTO como DescontoResponse pode ser empregado para transportar apenas informações pertinentes a um desconto, sem expor outros atributos internos da entidade produto.

**Exception Handler:** Trata-se de um componente centralizado para o tratamento de exceções que podem ocorrer durante o processamento das requisições na API. Seu objetivo é customizar as respostas de erro, tornando-as mais informativas e consistentes.
  - O Exception Handler intercepta exceções lançadas em diferentes pontos da aplicação (frequentemente originadas nas camadas de Controller ou Service) e as converte em uma resposta HTTP padronizada. Essa resposta inclui uma mensagem de erro clara, o status HTTP apropriado e, opcionalmente, detalhes adicionais que possam auxiliar na identificação da causa do problema.

![camadas](images/camadas.png)
![arquivos](images/arquivos.png)
![flow](images/flow.png)

## 2. Diagrama de Classes

![diagrama classes](images/diagrama_classes.png)

- Um produto pertence a uma categoria.
- Uma categoria pode ter muitos produtos.

## 3. Prints do Swagger

![swagger](images/swagger.png)

## 4. Regras de Negócio Implementadas

- **Produto**
  - Não permite criar produto com nome duplicado.
  - Não permite criar produto com id já existente.
  - Não permite criar produto sem categoria existente (categoria obrigatória).
  - O nome do produto deve ter entre 2 e 150 caracteres.
  - O nome do produto é obrigatório.
  - O preço do produto deve ser menor ou igual a R$ 10.000,00.
  - Se o nome do produto contiver "Promoção", o preço deve ser menor que R$ 500,00.
  - Não permite atualizar produto para nome já existente em outro produto.
  - Não permite buscar, atualizar ou deletar produto inexistente.
  - Não permite desconto maior que 50% nem menor que 0%.

- **Categoria**
  - Não permite criar categoria com nome duplicado.
  - Não permite criar categoria com id já existente.
  - O nome da categoria é obrigatório.
  - Não permite buscar, atualizar ou deletar categoria inexistente.

## 5. Exemplos de Uso da API

### Criar Categoria
**Request:**
```
POST /categorias
Content-Type: application/json

{
  "nome": "Informática"
}
```
**Response:**
```
200 OK
{
  "id": 1,
  "nome": "Informática"
}
```

### Criar Produto
**Request:**
```
POST /produtos?categoriaId=1
Content-Type: application/json

{
  "nome": "Notebook",
  "preco": 3500.0
}
```
**Response:**
```
201 Created
{
  "id": 1,
  "nome": "Notebook",
  "preco": 3500.0,
  "categoria": {
    "id": 1,
    "nome": "Informática"
  }
}
```

### Buscar Produtos por Nome
**Request:**
```
GET /produtos/buscar?nome=note
```
**Response:**
```
200 OK
[
  {
    "id": 1,
    "nome": "Notebook",
    "preco": 3500.0,
    "categoria": {
      "id": 1,
      "nome": "Informática"
    }
  }
]
```

### Calcular Preço com Desconto
**Request:**
```
GET /produtos/1/desconto?percentual=10
```
**Response:**
```
200 OK
{
  "nome": "Notebook",
  "precoOriginal": 3500.0,
  "descontoAplicado": "10%",
  "precoFinal": 3150.0
}
```

### Listar Produtos de uma Categoria
**Request:**
```
GET /categorias/1/produtos
```
**Response:**
```
200 OK
[
  {
    "id": 1,
    "nome": "Notebook",
    "preco": 3500.0,
    "categoria": {
      "id": 1,
      "nome": "Informática"
    }
  }
]
```

### Exemplo de Erro (Produto Duplicado)
**Request:**
```
POST /produtos?categoriaId=1
Content-Type: application/json

{
  "nome": "Notebook",
  "preco": 3500.0
}
```
**Response:**
```
400 Bad Request
{
  "timestamp": "2024-05-01T12:00:00",
  "status": 400,
  "erro": "Violação de regra de negócio",
  "mensagens": [
    "Já existe um produto com esse nome."
  ],
  "caminho": "/produtos"
}
```

---
