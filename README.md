# Nexora — Backend v1

## Stack
- Java 21 + Spring Boot 3.2
- PostgreSQL 16
- Flyway (migrations)
- JWT (jjwt 0.12)
- Lombok + MapStruct

## Subir local

```bash
# Só o banco
docker compose up postgres -d

# Rodar a aplicação
./mvnw spring-boot:run

# Ou subir tudo junto
docker compose up --build
```

## Endpoints disponíveis na v1

### Auth
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/auth/register` | Criar conta |
| POST | `/api/v1/auth/login` | Login, retorna JWT |

### Stores
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/stores` | Criar loja (vira SUPER_ADMIN) |
| GET | `/api/v1/stores/mine` | Minhas lojas |
| GET | `/api/v1/stores/{id}` | Detalhes de uma loja |

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_USERNAME` | nexora | Usuário do banco |
| `DB_PASSWORD` | nexora | Senha do banco |
| `JWT_SECRET` | (inseguro) | Segredo do JWT — trocar em produção |
| `JWT_EXPIRATION_MS` | 86400000 | Expiração do token (24h) |

## Estrutura do projeto

```
src/main/java/com/nexora/
├── config/          # SecurityConfig, JpaConfig
├── controller/      # AuthController, StoreController
├── dto/
│   ├── request/     # RegisterRequest, LoginRequest, CreateStoreRequest
│   └── response/    # AuthResponse, UserResponse, StoreResponse
├── exception/       # BusinessException, GlobalExceptionHandler
├── model/
│   ├── entity/      # User, Store, StoreMember, Product, Customer, Order, OrderItem, Appointment, ScheduleConfig
│   └── enums/       # UserOrigin, StoreRole, ProductType, etc.
├── repository/      # Interfaces JPA para cada entidade
├── security/        # JwtService, JwtAuthFilter
└── service/         # AuthService, StoreService
```

## Próximos passos (backlog v1)

- [ ] ProductService + ProductController (CRUD de catálogo)
- [ ] CustomerService + CustomerController
- [ ] OrderService + OrderController (criar pedido, mudar status)
- [ ] AppointmentService (verificar disponibilidade, criar agendamento)
- [ ] StoreMemberService (convidar membro, remover)
- [ ] Testes de integração com `@SpringBootTest`
