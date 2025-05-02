## Task.er

Task.er é um gerenciador de tarefas colaborativas! 🗓️

### Rodando a aplicação

1. Clone o repositório:

```bash
git clone https://github.com/anaanderle/task.er.git
```

2. Acesse o diretório do projeto:

```bash
cd task.er
```

3. Suba o banco de dados e o servidor:

```bash
docker compose up --build
```

4. Rode os inserts iniciais (opcional):

```bash
docker cp ./dev/initial-inserts.sql tasker-database:/initial-inserts.sql
docker exec -i tasker-database psql -U root -d tasker -f initial-inserts.sql
```

Pronto! A aplicação estará disponível na porta 8080 e você poderá acessá-la através do endereço
`http://localhost:8080/api`.

Se preferir, você pode utilizar o Swagger `http://localhost:8080/api/swagger-ui/index.html`.

> Caso prefira, você pode rodar a aplicação sem o Docker. Para isso, basta seguir os passos abaixo:

Repita os passos 1 e 2 do item anterior.

3. Crie um banco local PostgreSQL e configure as variáveis de ambiente no arquivo `.env` usando o `.env.example` como
   base.


4. Instale o Java 21.


5. Instale as dependências do projeto:

```
bash ./mvnw clean install
```

6. Rode a aplicação:

```bash 
./mvnw spring-boot:run
```

### Tecnologias 💻

- Java 21
- Spring Boot
- PostgreSQL
- Docker
- Swagger
- Flyway

### Padrões 📝

- Layered Architecture
- Git Flow