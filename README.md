# Nexora - Backend v1

Backend Java/Spring Boot para autenticacao, lojas e catalogo. O dominio ja tem base em banco para clientes, pedidos e agendamentos, mas essas APIs ainda nao foram expostas.

## Stack
- Java 21
- Spring Boot 3.2.5
- Spring Security + JWT (`jjwt`)
- Spring Data JPA
- Bean Validation
- Flyway
- PostgreSQL 16
- Lombok
- MapStruct configurado no `pom.xml`

## Como subir localmente

O repositorio nao inclui Maven Wrapper, entao use `mvn` direto.

```bash
# Apenas o banco
docker compose up postgres -d

# Rodar a aplicacao localmente
mvn spring-boot:run

# Ou subir banco + app no Docker
docker compose up --build
```

## Configuracao

`src/main/resources/application.yml` ativa o perfil `dev` por padrao.
Nesse perfil, os envios de OTP e email usam implementacoes mock.

| Variavel / propriedade | Padrao | Onde e usada |
|---|---:|---|
| `DB_USERNAME` | `nexora` | Usuario do banco |
| `DB_PASSWORD` | `nexora` | Senha do banco |
| `JWT_SECRET` | segredo de desenvolvimento | Assinatura do JWT |
| `JWT_EXPIRATION_MS` | `86400000` | Expiracao do token em ms |
| `nexora.otp.expiration-minutes` | `5` no perfil `dev` | Expiracao do OTP |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/nexora` no Docker | URL do banco no container |

Para ambiente fora do `dev`, sobrescreva `SPRING_PROFILES_ACTIVE` e forneca uma implementacao real para email/OTP. Hoje, `SmtpEmailSender` e `EvolutionApiOtpSender` ainda lancam `UnsupportedOperationException` no perfil `prod`.

## API atual

### Auth publico

`/api/v1/auth/**` nao exige JWT.

| Metodo | Rota | Descricao |
|---|---|---|
| POST | `/api/v1/auth/register` | Cria usuario base de loja e retorna JWT |
| POST | `/api/v1/auth/login` | Login com email e senha |
| POST | `/api/v1/auth/customer/register` | Cria cliente e envia OTP por telefone |
| POST | `/api/v1/auth/customer/login` | Login de cliente por email/senha ou telefone |
| POST | `/api/v1/auth/customer/verify-otp` | Valida OTP de telefone e retorna JWT |
| POST | `/api/v1/auth/forgot-password` | Envia OTP de redefinicao por email |
| POST | `/api/v1/auth/reset-password` | Valida OTP e troca a senha |

Observacoes:
- `customer/register` responde `202 Accepted`.
- `customer/login` com telefone dispara OTP e tambem responde `202 Accepted`.
- `forgot-password` responde `202 Accepted`.
- `reset-password` responde `204 No Content`.

### Stores

| Metodo | Rota | Descricao |
|---|---|---|
| POST | `/api/v1/stores` | Cria a loja e vincula o usuario autenticado como `SUPER_ADMIN` |
| GET | `/api/v1/stores/mine` | Lista as lojas do usuario autenticado |
| GET | `/api/v1/stores/{id}` | Retorna os detalhes de uma loja |

### Products

| Metodo | Rota | Descricao |
|---|---|---|
| POST | `/api/v1/stores/{storeId}/products` | Cria produto |
| GET | `/api/v1/stores/{storeId}/products` | Lista produtos da loja |
| GET | `/api/v1/stores/{storeId}/products/{productId}` | Busca produto por id |
| PUT | `/api/v1/stores/{storeId}/products/{productId}` | Atualiza produto |
| DELETE | `/api/v1/stores/{storeId}/products/{productId}` | Desativa produto com `active=false` |

Na listagem de produtos, o query param `active` e opcional. Se vier vazio, a API retorna todos os produtos da loja.

## Regras de acesso

- `auth/**` e publico.
- As demais rotas exigem JWT.
- `MEMBER` pode listar e consultar produtos da propria loja.
- `SUPER_ADMIN` pode criar, atualizar e desativar produtos.
- Criar uma loja adiciona automaticamente o usuario autenticado como `SUPER_ADMIN`.

## Estrutura

```text
src/main/java/com/nexora/
|-- config/          # SecurityConfig, JpaConfig
|-- controller/      # AuthController, StoreController, ProductController
|-- dto/
|   |-- request/     # StoreRegisterRequest, CustomerRegisterRequest, ProductCreateRequest, etc.
|   `-- response/    # AuthResponse, UserResponse, StoreResponse, ProductResponse
|-- exception/       # BusinessException, GlobalExceptionHandler
|-- integration/
|   |-- email/       # EmailSender + mocks/prod
|   `-- otp/         # OtpSender + mocks/prod
|-- model/
|   |-- entity/      # User, Store, StoreMember, Product, Customer, Order, OrderItem, Appointment, ScheduleConfig
|   `-- enums/       # UserOrigin, StoreRole, ProductType, OtpType, etc.
|-- repository/      # Interfaces JPA
|-- security/        # JwtService, JwtAuthFilter
`-- service/         # AuthService, OtpService, StoreService, ProductService
```

## Estado atual

- [x] Autenticacao de loja e cliente
- [x] JWT, validacao e filtro de autenticacao
- [x] Lojas e produtos
- [x] OTP por telefone e email com mocks no `dev`
- [ ] Integracao real de email/OTP em producao
- [ ] APIs de customer management
- [ ] APIs de pedidos
- [ ] APIs de agendamentos
- [ ] APIs de membros da loja
- [ ] Testes automatizados em `src/test`
