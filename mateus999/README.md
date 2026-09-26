# mateus999 - transferencias assincronas com SQS

Versao revisada do projeto com foco em quatro pontos: encerramento logico de conta, DLQ/reconciliacao, seguranca JWT e idempotencia/concorrencia.

## O que mudou

- Conta nao e apagada fisicamente. Usa `ATIVA`, `BLOQUEADA` e `ENCERRADA`.
- Conta so pode ser encerrada com saldo zero e nao pode ser reativada depois de encerrada.
- Saldo inicial nao vem mais do cliente: toda conta nasce com saldo `0.00`.
- Credito manual existe apenas em rota `/admin/**` para ambiente de teste.
- Login e registro usam Spring Security + BCrypt + JWT.
- Todas as rotas, exceto `/auth/**` e `/actuator/health`, exigem autenticacao.
- Transferencias exigem header `Idempotency-Key`.
- Existe constraint unica `(usuario_solicitante_id, idempotency_key)` no banco.
- O worker trava primeiro a transferencia (`PESSIMISTIC_WRITE`) e depois as duas contas em ordem deterministica.
- Duas mensagens SQS iguais nao debitam duas vezes: a segunda espera o lock e encontra a transferencia como `CONCLUIDA`.
- Falhas ficam como `ERRO_REPROCESSAVEL` com contador e mensagem do ultimo erro.
- Quando a mensagem chega na DLQ, a transferencia passa para `ERRO_DLQ`.
- Existe rota de reprocessamento manual para ADMIN.
- Existe job de reconciliacao para transferencias antigas/stale.
- Spring Boot DevTools foi removido porque o Spring Cloud AWS documenta incompatibilidade com `@SqsListener`.

## Subir infraestrutura

```bash
docker compose up -d
```

Isso sobe:

- PostgreSQL em `localhost:5432`, banco `mateus999`, usuario/senha `admin/admin`.
- LocalStack em `localhost:4566`.
- Fila `transferencias` com redrive para `transferencias-dlq` apos 5 recebimentos.

## Criar um ADMIN local

No IntelliJ, adicione as variaveis de ambiente:

```text
ADMIN_EMAIL=admin@local.dev
ADMIN_PASSWORD=uma-senha-forte
JWT_SECRET=um-segredo-com-pelo-menos-32-bytes-e-bem-grande
```

O ADMIN e criado somente se `ADMIN_EMAIL` e `ADMIN_PASSWORD` estiverem preenchidos.

## Fluxo para testar

### 1. Registrar usuario

`POST /auth/register`

```json
{
  "nome": "Mateus",
  "email": "mateus@email.com",
  "senha": "12345678"
}
```

### 2. Login

`POST /auth/login`

```json
{
  "email": "mateus@email.com",
  "senha": "12345678"
}
```

Use o `accessToken` em:

```text
Authorization: Bearer SEU_TOKEN
```

### 3. Criar conta

`POST /contas`

Sem body. A conta nasce com saldo zero.

### 4. Creditar conta para teste

Entre com o usuario ADMIN e chame:

`POST /admin/contas/{contaId}/credito`

```json
{
  "valor": 1000.00
}
```

### 5. Criar transferencia

`POST /transferencias`

Headers:

```text
Authorization: Bearer SEU_TOKEN
Idempotency-Key: pagamento-001
Content-Type: application/json
```

Body:

```json
{
  "contaOrigemId": "UUID-DA-CONTA-ORIGEM",
  "contaDestinoId": "UUID-DA-CONTA-DESTINO",
  "valor": 50.00
}
```

A API devolve `202 Accepted` e status inicial `PENDENTE`. O processamento e feito pelo listener do SQS.

Se voce repetir a requisicao com a mesma `Idempotency-Key`, a mesma transferencia e retornada em vez de criar outra.

### 6. Consultar transferencias

`GET /transferencias`

`GET /transferencias/{id}`

### 7. Encerrar conta sem apagar historico

`PATCH /contas/{id}/encerrar`

A conta precisa ter saldo zero. Nenhum `DELETE` fisico e executado.

### 8. Reprocessar transferencia que caiu na DLQ

Como ADMIN:

`POST /admin/transferencias/{id}/reprocessar`

So funciona se o status for `ERRO_DLQ`.

## Status de transferencia

- `PENDENTE`: criada e aguardando fila.
- `PROCESSANDO`: worker esta processando.
- `CONCLUIDA`: debito e credito confirmados na mesma transacao do banco.
- `ERRO_REPROCESSAVEL`: houve falha e o SQS ainda pode tentar novamente.
- `ERRO_DLQ`: atingiu a DLQ ou foi marcada assim pela reconciliacao.

## Observacao importante

Para ambiente real, substitua `ddl-auto: update` por migrations (Flyway/Liquibase), armazene o segredo JWT em Secrets Manager/KMS e use TLS/HTTPS. O `JWT_SECRET` padrao do `application.yml` existe apenas para desenvolvimento local.
