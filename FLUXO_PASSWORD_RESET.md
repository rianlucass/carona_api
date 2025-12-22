# Fluxo de Password Reset (Redefinição de Senha)

## Visão Geral
Sistema completo de redefinição de senha via email com token temporário.

## Arquitetura

### 1. DTOs (domain/password/)
- **PasswordResetRequestDTO**: Requisição para solicitar reset
  - `email`: Email do usuário

- **PasswordResetDTO**: Requisição para realizar o reset
  - `token`: Token recebido por email
  - `newPassword`: Nova senha (mínimo 8 caracteres, deve conter maiúscula, minúscula, número e caractere especial)
  - `confirmPassword`: Confirmação da senha

- **PasswordResetResponseDTO**: Resposta padrão
  - `message`: Mensagem de sucesso
  - `email`: Email do usuário

### 2. Entidade (domain/password/)
- **PasswordResetToken**
  - `id`: UUID gerado automaticamente
  - `user`: Relação ManyToOne com User (domain/user/)
  - `tokenHash`: Hash SHA-256 do token (para segurança)
  - `expiresAt`: Data/hora de expiração (15 minutos após criação)
  - `usedAt`: Data/hora de uso (null enquanto não usado)

### 3. Repository
- **PasswordResetTokenRepository**
  - `findByTokenHashAndUsedAtIsNull()`: Busca token válido (não usado)

### 4. Exceções (infra/exceptions/auth/)
- **InvalidTokenException**: Token inválido ou já utilizado
  - ErrorCode: `INVALID_TOKEN`
  - Status: 400 BAD_REQUEST

- **TokenExpiredException**: Token expirado
  - ErrorCode: `TOKEN_EXPIRED`
  - Status: 400 BAD_REQUEST

### 5. Service (PasswordResetService)

#### Métodos:

**requestPasswordReset(String email)**
- Valida se usuário existe
- Gera token UUID único
- Cria hash SHA-256 do token
- Salva no banco com expiração de 15 minutos
- Envia email com link contendo o token
- Retorna: email do usuário

**validateToken(String token)**
- Cria hash do token recebido
- Busca token no banco (não usado)
- Valida se não expirou
- Retorna: PasswordResetToken
- Exceções: InvalidTokenException, TokenExpiredException

**resetPassword(String token, String newPassword, String confirmPassword)**
- Valida se senhas coincidem
- Valida o token
- Atualiza senha do usuário (com BCrypt)
- Marca token como usado (usedAt = now)
- Retorna: email do usuário

### 6. Controller (PasswordResetController)

**Base URL**: `/auth/password`

#### Endpoints:

**POST /auth/password/request-reset**
```json
Request:
{
  "email": "usuario@example.com"
}

Response (200 OK):
{
  "success": true,
  "message": "Email de redefinição enviado com sucesso",
  "data": {
    "message": "Um email com instruções para redefinir sua senha foi enviado",
    "email": "usuario@example.com"
  }
}
```

**POST /auth/password/reset**
```json
Request:
{
  "token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "newPassword": "NovaSenha@123",
  "confirmPassword": "NovaSenha@123"
}

Response (200 OK):
{
  "success": true,
  "message": "Senha alterada com sucesso",
  "data": {
    "message": "Senha redefinida com sucesso",
    "email": "usuario@example.com"
  }
}
```

## Fluxo Completo

### 1. Solicitação de Reset
```
Cliente → POST /auth/password/request-reset
        ↓
PasswordResetController.requestPasswordReset()
        ↓
PasswordResetService.requestPasswordReset()
        ↓
- Valida usuário existe (UserNotFoundException)
- Gera token UUID
- Cria hash SHA-256
- Salva PasswordResetToken (expiresAt = now + 15min)
- EmailService.sendPasswordResetEmail()
        ↓
Response com email de confirmação
```

### 2. Email Enviado
```
Assunto: Solicitação de Redefinição de Senha
Link: http://localhost:8080/reset-password?token={token}
Validade: 15 minutos
```

### 3. Reset da Senha
```
Cliente → POST /auth/password/reset (com token)
        ↓
PasswordResetController.resetPassword()
        ↓
PasswordResetService.resetPassword()
        ↓
- Valida senhas coincidem (InvalidPasswordException)
- Valida token existe e não foi usado (InvalidTokenException)
- Valida token não expirou (TokenExpiredException)
- Atualiza senha (BCrypt)
- Marca token como usado (usedAt = now)
        ↓
Response com sucesso
```

## Segurança

### 1. Token Hash
- Token nunca armazenado em texto puro
- Usa SHA-256 para hash
- Token enviado apenas por email
- Token único (UUID)

### 2. Expiração
- 15 minutos de validade
- Token só pode ser usado uma vez
- Campo `usedAt` previne reuso

### 3. Validação de Senha
- Mínimo 8 caracteres
- Requer: maiúscula, minúscula, número, caractere especial
- BCrypt para armazenamento

### 4. Tratamento de Erros
- Mensagens específicas para cada erro
- Códigos de erro padronizados
- Status HTTP apropriados
- Sem exposição de detalhes sensíveis

## Possíveis Exceções

| Exceção | Código | Status | Quando Ocorre |
|---------|--------|--------|---------------|
| UserNotFoundException | USER_NOT_FOUND | 404 | Email não existe |
| InvalidTokenException | INVALID_TOKEN | 400 | Token inválido/usado |
| TokenExpiredException | TOKEN_EXPIRED | 400 | Token expirado |
| InvalidPasswordException | INVALID_PASSWORD | 400 | Senhas não coincidem |
| EmailSendingException | EMAIL_SENDING_ERROR | 500 | Falha ao enviar email |
| MethodArgumentNotValidException | VALIDATION_ERROR | 400 | Validação de campos |

## Banco de Dados

### Tabela: password_reset_tokens
```sql
CREATE TABLE password_reset_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Índice para busca eficiente
CREATE INDEX idx_token_hash ON password_reset_tokens(token_hash);
CREATE INDEX idx_expires_at ON password_reset_tokens(expires_at);
```

## Melhorias Futuras

1. **Limpeza de Tokens**
   - Job agendado para deletar tokens expirados
   - Implementar com @Scheduled

2. **Rate Limiting**
   - Limitar tentativas por IP
   - Limitar solicitações por email

3. **Logs de Auditoria**
   - Registrar todas tentativas
   - Alertar sobre atividades suspeitas

4. **Notificações**
   - Email confirmando alteração de senha
   - Alerta se não foi o usuário

5. **Frontend**
   - Página de reset com validação
   - Timer mostrando expiração
   - Indicador de força de senha

## Testando a API

### 1. Solicitar Reset
```bash
curl -X POST http://localhost:8080/auth/password/request-reset \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@example.com"}'
```

### 2. Verificar Email
- Abrir email recebido
- Copiar token do link

### 3. Resetar Senha
```bash
curl -X POST http://localhost:8080/auth/password/reset \
  -H "Content-Type: application/json" \
  -d '{
    "token": "seu-token-aqui",
    "newPassword": "NovaSenha@123",
    "confirmPassword": "NovaSenha@123"
  }'
```
