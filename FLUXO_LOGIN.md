# 🔐 Fluxo de Login e Autenticação - Carona API

## 📋 Visão Geral

Este documento descreve o fluxo completo de autenticação, desde o registro até o login com validações de segurança.

---

## 🔄 Fluxo Completo de Autenticação

```
┌─────────────────────────────────────────────────────────────┐
│                    1. REGISTRO INICIAL                       │
│                   POST /auth/register                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Valida email único
                       ├─ Valida username único
                       ├─ Cria usuário com dados básicos:
                       │  • email
                       │  • password (criptografado)
                       │  • name
                       │  • username
                       │  • emailVerified = false
                       │  • accountStatus = ACTIVE
                       │  • role = USER
                       │
                       ├─ Envia código de verificação por email
                       │
                       └─ Retorna: token JWT (acesso limitado)
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│              2. VERIFICAÇÃO DE EMAIL                         │
│         POST /api/email-verification/verify                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Valida código de verificação
                       ├─ Verifica expiração (1 minuto)
                       │
                       └─ Se válido:
                          • emailVerified = true
                          • Retorna: sucesso
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│           3. COMPLETAR CADASTRO                              │
│       POST /auth/registerComplete/{email}                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ REQUER: emailVerified = true
                       │
                       ├─ Valida dados adicionais:
                       │  • phone (único)
                       │  • cpf (único)
                       │  • birthDate (formato válido)
                       │
                       ├─ Dados opcionais:
                       │  • photo (upload de arquivo)
                       │  • gender
                       │  • city
                       │  • state
                       │
                       └─ Retorna: novo token JWT (acesso completo)
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│                    4. LOGIN                                  │
│                 POST /auth/login                             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ 1. Valida credenciais (email + senha)
                       │    ❌ Se inválido: HTTP 401 (AUTH_002)
                       │
                       ├─ 2. Verifica email verificado
                       │    ❌ Se não: HTTP 403 (AUTH_001)
                       │       └─ EmailNotVerifiedException
                       │
                       ├─ 3. Verifica perfil completo
                       │    • phone não pode ser null ou vazio
                       │    • cpf não pode ser null ou vazio
                       │    • birthDate não pode ser null
                       │    
                       │    ❌ Se incompleto: HTTP 403 (USER_005)
                       │       └─ ProfileIncompleteException
                       │
                       └─ ✅ Se tudo OK:
                          • Retorna: token JWT válido
                          • Usuário tem acesso completo ao sistema
```

---

## 🎯 Endpoints de Autenticação

### 1️⃣ **POST /auth/register**
Registro inicial do usuário.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123",
  "name": "Nome Completo",
  "username": "usuario"
}
```

**Response Success (201):**
```json
{
  "success": true,
  "message": "Email cadastrado com sucesso: {token}",
  "data": {
    "message": "Usuário registrado com sucesso. Código de verificação enviado para o email.",
    "email": "usuario@example.com",
    "emailVerificationRequired": true
  }
}
```

**Possíveis Erros:**
- `USER_001` - Email já existe (HTTP 409)
- `USER_002` - Username já existe (HTTP 409)

---

### 2️⃣ **POST /api/email-verification/verify**
Verifica o código enviado por email.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "code": "123456"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Email verificado com sucesso!",
  "verified": true
}
```

**Possíveis Erros:**
- `VERIFY_001` - Código inválido (HTTP 400)
- `VERIFY_002` - Código expirado (HTTP 400)

---

### 3️⃣ **POST /auth/registerComplete/{email}**
Completa o cadastro com dados adicionais (multipart/form-data).

**Request (Form Data):**
```
phone: "11999999999"
cpf: "12345678901"
birthDate: "1990-01-01"
gender: "M"
city: "São Paulo"
state: "SP"
photo: [arquivo de imagem]
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Cadastro completo com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Possíveis Erros:**
- `AUTH_001` - Email não verificado (HTTP 403)
- `USER_003` - Telefone já existe (HTTP 409)
- `USER_006` - CPF já existe (HTTP 409)
- `VALID_001` - Formato de data inválido (HTTP 400)

---

### 4️⃣ **POST /auth/login**
Autentica o usuário e retorna token JWT.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Possíveis Erros:**
- `AUTH_002` - Credenciais inválidas (HTTP 401)
- `AUTH_001` - Email não verificado (HTTP 403)
- `USER_005` - Perfil incompleto (HTTP 403)

---

## 🛡️ Validações de Segurança

### Nível 1: Credenciais
```java
✓ Email e senha corretos
✓ Usuário existe no banco
✓ Senha criptografada (BCrypt)
```

### Nível 2: Verificação de Email
```java
✓ Email deve estar verificado (emailVerified = true)
✓ Código de verificação enviado automaticamente no registro
✓ Código expira em 1 minuto
```

### Nível 3: Completude do Perfil
```java
✓ phone != null && !phone.isEmpty()
✓ cpf != null && !cpf.isEmpty()
✓ birthDate != null
```

---

## 📊 Estados do Usuário

| Estado | emailVerified | Dados Completos | Pode fazer Login? | Ação Necessária |
|--------|---------------|-----------------|-------------------|-----------------|
| **Registrado** | ❌ | ❌ | ❌ | Verificar email |
| **Email Verificado** | ✅ | ❌ | ❌ | Completar cadastro |
| **Cadastro Completo** | ✅ | ✅ | ✅ | Pode acessar |

---

## 🔑 JWT Token

### Geração
O token JWT é gerado após:
1. ✅ Registro inicial (acesso limitado)
2. ✅ Completar cadastro (acesso completo)
3. ✅ Login bem-sucedido (acesso completo)

### Estrutura
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Uso
```javascript
// No frontend (Expo/React)
const response = await fetch('http://IP:8080/api/protected-route', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

---

## ❌ Códigos de Erro

| Código | Descrição | HTTP Status |
|--------|-----------|-------------|
| `AUTH_001` | Email não verificado | 403 |
| `AUTH_002` | Credenciais inválidas | 401 |
| `USER_001` | Email já existe | 409 |
| `USER_002` | Username já existe | 409 |
| `USER_003` | Telefone já existe | 409 |
| `USER_004` | Usuário não encontrado | 404 |
| `USER_005` | Perfil incompleto | 403 |
| `USER_006` | CPF já existe | 409 |
| `VERIFY_001` | Código de verificação inválido | 400 |
| `VERIFY_002` | Código expirado | 400 |
| `VALID_001` | Formato de data inválido | 400 |

---

## 🧪 Exemplos de Teste

### Cenário 1: Fluxo Completo com Sucesso
```bash
# 1. Registrar
POST /auth/register
{ "email": "test@test.com", "password": "123456", "name": "Test", "username": "test" }
→ Retorna token1

# 2. Verificar email
POST /api/email-verification/verify
{ "email": "test@test.com", "code": "123456" }
→ Email verificado

# 3. Completar cadastro
POST /auth/registerComplete/test@test.com
{ phone: "11999999999", cpf: "12345678901", birthDate: "1990-01-01" }
→ Retorna token2

# 4. Login
POST /auth/login
{ "email": "test@test.com", "password": "123456" }
→ ✅ Retorna token (acesso completo)
```

### Cenário 2: Tentativa de Login sem Verificação
```bash
# 1. Registrar
POST /auth/register → OK

# 2. Tentar login SEM verificar email
POST /auth/login
→ ❌ HTTP 403: EmailNotVerifiedException (AUTH_001)
```

### Cenário 3: Tentativa de Login sem Completar Cadastro
```bash
# 1. Registrar → OK
# 2. Verificar email → OK
# 3. Tentar login SEM completar cadastro
POST /auth/login
→ ❌ HTTP 403: ProfileIncompleteException (USER_005)
```

---

## 🔒 Segurança Implementada

✅ **Senhas criptografadas** com BCrypt  
✅ **JWT stateless** para autenticação  
✅ **CORS configurado** para Expo Go  
✅ **Validação em múltiplas camadas**  
✅ **Exceções personalizadas** com códigos únicos  
✅ **Email verification** obrigatória  
✅ **Perfil completo** obrigatório para acesso  

