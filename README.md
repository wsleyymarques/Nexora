# Nexora - Backend v1

Backend Java/Spring Boot para gestao de lojas, catalogo, clientes, pedidos e agendamentos.

## Stack
- Java 21 + Spring Boot 3.2
- PostgreSQL 16
- Flyway para migrations
- JWT com `jjwt`
- Lombok + MapStruct

## Como subir local

```bash
# Apenas o banco
docker compose up postgres -d

# Rodar a aplicacao
./mvnw spring-boot:run

# Ou subir tudo junto
docker compose up --build
```

## Variaveis de ambiente

| Variavel | Padrao | Descricao |
|----------|--------|-----------|
| `DB_USERNAME` | `nexora` | Usuario do banco |
| `DB_PASSWORD` | `nexora` | Senha do banco |
| `JWT_SECRET` | inseguro | Segredo do JWT, troque em producao |
| `JWT_EXPIRATION_MS` | `86400000` | Expiracao do token em ms |

## Endpoints v1

### Auth
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/api/v1/auth/register` | Criar conta |
| POST | `/api/v1/auth/login` | Login e retorno de JWT |

### Stores
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/api/v1/stores` | Criar loja e virar `SUPER_ADMIN` |
| GET | `/api/v1/stores/mine` | Listar minhas lojas |
| GET | `/api/v1/stores/{id}` | Detalhe de uma loja |

### Products
| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/api/v1/stores/{storeId}/products` | Criar item do catalogo |
| GET | `/api/v1/stores/{storeId}/products` | Listar produtos da loja |
| GET | `/api/v1/stores/{storeId}/products/{productId}` | Buscar produto por id |
| PUT | `/api/v1/stores/{storeId}/products/{productId}` | Atualizar produto |
| DELETE | `/api/v1/stores/{storeId}/products/{productId}` | Desativar produto (`active=false`) |

Regras de acesso:
- `MEMBER` pode listar e consultar produtos da loja.
- `SUPER_ADMIN` pode criar, atualizar e desativar produtos.
- O delete e soft delete porque `order_items.product_id` referencia `products`.

## Estrutura do projeto

```text
src/main/java/com/nexora/
|-- config/          # SecurityConfig, JpaConfig
|-- controller/      # AuthController, StoreController, ProductController
|-- dto/
|   |-- request/     # RegisterRequest, LoginRequest, CreateStoreRequest, CreateProductRequest, UpdateProductRequest
|   `-- response/    # AuthResponse, UserResponse, StoreResponse, ProductResponse
|-- exception/       # BusinessException, GlobalExceptionHandler
|-- model/
|   |-- entity/      # User, Store, StoreMember, Product, Customer, Order, OrderItem, Appointment, ScheduleConfig
|   `-- enums/       # UserOrigin, StoreRole, ProductType, etc.
|-- repository/      # Interfaces JPA
|-- security/        # JwtService, JwtAuthFilter
`-- service/         # AuthService, StoreService, ProductService
```

## Proximos passos

- [x] ProductService + ProductController (CRUD de catalogo)
- [ ] CustomerService + CustomerController
- [ ] OrderService + OrderController (criar pedido, mudar status)
- [ ] AppointmentService (verificar disponibilidade, criar agendamento)
- [ ] StoreMemberService (convidar membro, remover)
- [ ] Testes de integracao com `@SpringBootTest`
