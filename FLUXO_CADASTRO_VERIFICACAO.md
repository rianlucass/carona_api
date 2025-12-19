# 📱 Fluxo de Cadastro - Via Carona API

## 🎯 Visão Geral

Cadastro em 3 etapas: **Registro inicial** → **Verificação de email** → **Completar perfil**

---

## 🔄 Fluxo de Cadastro

### **1️⃣ REGISTRO INICIAL**

`POST /auth/register`

**Request:**
```json
{
  "email": "usuario@email.com",
  "password": "senha12345",
  "name": "Rian Lucas",
  "username": "rianlucas"
}
```

**Success (201):**
```json
{
  "success": true,
  "message": "Cadastro realizado com sucesso",
  "data": {
    "message": "Usuário registrado com sucesso. Código de verificação enviado para o email.",
    "email": "usuario@email.com",
    "emailVerificationRequired": true
  }
}
```

**Errors:**
- `409` - Email/Username/Phone já em uso (`USER_001`, `USER_002`, `USER_003`)
- `400` - Validação de campos (`VALIDATION_ERROR`)

**Ação:** Redirecionar para `/verify-email` passando o email

---

### **2️⃣ VERIFICAÇÃO DE EMAIL**

`POST /api/email-verification/verify`

**Request:**
```json
{
  "email": "usuario@email.com",
  "code": "12345678"
}
```

**Success (200):**
```json
{
  "success": true,
  "message": "Email verificado",
  "data": {
    "success": true,
    "message": "Email verificado com sucesso!",
    "email": "usuario@email.com"
  }
}
```

**Errors:**
- `400` - Código inválido ou expirado (`VERIFICATION_001`, `VERIFICATION_002`)

**Ação:** Redirecionar para `/complete-profile` passando o email

#### **Reenviar Código (Opcional)**

`POST /api/email-verification/resend-code`

```json
{
  "email": "usuario@email.com"
}
```

---

### **3️⃣ COMPLETAR PERFIL**

`POST /auth/registerComplete/{email}`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `photo` (file, opcional) - Foto de perfil (JPG/PNG, máx 5MB)
- `phone` (text, obrigatório) - Telefone com 10 ou 11 dígitos (ex: "11987654321")
- `birthDate` (text, obrigatório) - Data de nascimento no formato YYYY-MM-DD (ex: "2000-01-15")
- `gender` (text, obrigatório) - Gênero: M (Masculino), F (Feminino) ou O (Outro)
- `cpf` (text, obrigatório) - CPF com 11 dígitos (ex: "12345678901")
- `state` (text, obrigatório) - UF com 2 letras maiúsculas (ex: "SP")
- `city` (text, obrigatório) - Nome da cidade (2-100 caracteres)

**Success (200):** 
```json
{
  "success": true,
  "message": "Cadastro completo com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Errors:**
- `403` - Email não verificado (`AUTH_001`)
- `409` - CPF/Telefone já em uso (`USER_004`, `USER_003`)
- `400` - Validação de campos (`VALIDATION_ERROR`, `VALIDATION_001`)

**Ação:** Salvar token e redirecionar para `/dashboard`

---

### **4️⃣ LOGIN (Alternativo)**

`POST /auth/login`

**Request:**
```json
{
  "email": "usuario@email.com",
  "password": "senha12345"
}
```

**Success (200):**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Errors:**
- `403` - Email não verificado (`AUTH_001`)
- `401` - Credenciais inválidas (`AUTH_002`)

---

## 📊 Diagrama Simplificado

```
[1. REGISTRO]
    ↓ (Envia código por email)
[2. VERIFICAÇÃO EMAIL]
    ↓ (Marca email como verificado)
[3. COMPLETAR PERFIL]
    ↓ (Retorna token)
[DASHBOARD]
```

---

## 🔍 Códigos de Erro

| Código | Descrição |
|--------|-----------|
| **AUTH_001** | Email não verificado |
| **AUTH_002** | Credenciais inválidas |
| **AUTH_003** | Erro ao gerar token |
| **AUTH_004** | Token inválido/expirado |
| **USER_001** | Email já em uso |
| **USER_002** | Username já em uso |
| **USER_003** | Telefone já em uso |
| **USER_004** | CPF já em uso |
| **VALIDATION_001** | Formato de data inválido |
| **VALIDATION_ERROR** | Erro de validação nos campos |
| **VERIFICATION_001** | Código inválido |
| **VERIFICATION_002** | Código expirado |
| **VERIFICATION_003** | Erro ao enviar email |

---

## 🔐 Recursos de Segurança

- ✅ Código de verificação expira em 1 minutos
- ✅ Email deve ser verificado antes de completar perfil
- ✅ Senha criptografada (BCrypt)
- ✅ Token JWT com expiração (2 horas)
- ✅ Validação de CPF único
- ✅ Upload de imagem com validação de tipo e tamanho (5MB)
- ✅ Exception Handler global
