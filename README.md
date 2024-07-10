# README

## eCommerce API

### Estrutura do Projeto

O projeto está organizado em pacotes que seguem as melhores práticas de desenvolvimento com Spring Boot:

- `com.ecommerce.ecomm.config`: Contém configurações da aplicação, como configuração de cache.
- `com.ecommerce.ecomm.controller`: Contém os controladores que lidam com as requisições HTTP.
- `com.ecommerce.ecomm.dto`: Contém os Data Transfer Objects (DTOs) utilizados para transferência de dados entre camadas.
- `com.ecommerce.ecomm.entities`: Contém as entidades JPA que representam os objetos de domínio.
- `com.ecommerce.ecomm.exception`: Contém classes relacionadas ao tratamento de exceções.
- `com.ecommerce.ecomm.model`: Contém modelos de dados utilizados na aplicação.
- `com.ecommerce.ecomm.repository`: Contém os repositórios JPA para acesso a dados.
- `com.ecommerce.ecomm.service`: Contém os serviços que implementam a lógica de negócios.

### Principais Funcionalidades

#### Produtos

- **Criar Produto**: Endpoint para criar um novo produto.
- **Listar Produtos**: Endpoint para listar todos os produtos disponíveis.
- **Atualizar Produto**: Endpoint para atualizar as informações de um produto existente.
- **Deletar Produto**: Endpoint para deletar um produto. Se o produto tiver vendas associadas, ele será inativado em vez de deletado.

#### Vendas

- **Criar Venda**: Endpoint para registrar uma nova venda.
- **Listar Vendas**: Endpoint para listar todas as vendas.
- **Atualizar Venda**: Endpoint para atualizar as informações de uma venda existente.
- **Deletar Venda**: Endpoint para deletar uma venda.
- **Filtrar Vendas por Data**: Endpoint para filtrar vendas por um intervalo de datas.
- **Gerar Relatório Mensal**: Endpoint para gerar um relatório de vendas para um mês específico.
- **Gerar Relatório Semanal**: Endpoint para gerar um relatório de vendas para uma semana específica.

### Configuração e Execução

#### Pré-requisitos

- Java 17
- Maven
- PostgreSQL (ou outro banco de dados suportado)

#### Configuração do Banco de Dados

Edite o arquivo `application.properties` para configurar a conexão com o banco de dados:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seu-banco-de-dados
spring.datasource.username=seuusuario
spring.datasource.password=suasenha
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jackson.date-format=yyyy-MM-dd'T'HH:mm:ss'Z'
spring.jackson.serialization.write-dates-as-timestamps=false
management.endpoints.web.exposure.include=*
```

#### Executando a Aplicação

1. Clone o repositório:

   ```sh
   git clone https://github.com/seu-usuario/ecommerce-api.git
   ```

2. Navegue até o diretório do projeto:

   ```sh
   cd ecommerce-api
   ```

3. Execute a aplicação usando Maven:

   ```sh
   mvn spring-boot:run
   ```

A aplicação estará disponível em `http://localhost:8080`.

Contribuições são bem-vindas! Por favor, abra uma issue ou envie um pull request para contribuir com o projeto.

### Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
