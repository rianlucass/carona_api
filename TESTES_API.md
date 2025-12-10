# 🧪 Testes de API - Via Carona

## Teste Completo do Fluxo

### 1️⃣ REGISTRO
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha12345",
    "name": "Rian Lucas",
    "username": "rianlucass",
    "phone": "11999999999",
    "birthDate": "1990-01-15",
    "gender": "M",
    "isDriver": true
  }'
```

**Resultado esperado:**
- Status: 201 Created
- Email recebe código de 6 dígitos
- Response com `emailVerificationRequired: true`

---

### 2️⃣ VERIFICAR EMAIL
```bash
curl -X POST http://localhost:8080/api/email-verification/verify \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "code": "123456"
  }'
```

**Resultado esperado:**
- Status: 200 OK
- Message: "Email verificado com sucesso!"

---

### 3️⃣ TENTAR LOGIN SEM VERIFICAR (deve falhar)
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha12345"
  }'
```

**Resultado esperado (SE NÃO VERIFICOU EMAIL):**
- Status: 403 Forbidden
- Message: "Email não verificado"

---

### 4️⃣ LOGIN APÓS VERIFICAÇÃO
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "senha12345"
  }'
```

**Resultado esperado:**
- Status: 200 OK
- Response com token JWT

---

### 5️⃣ REENVIAR CÓDIGO
```bash
curl -X POST http://localhost:8080/api/email-verification/resend-code \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com"
  }'
```

**Resultado esperado:**
- Status: 200 OK
- Novo código enviado para email

---

## 🔍 Testes de Validação

### Tentar cadastrar com email duplicado:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "password": "outrasenha",
    "name": "Outro Usuario",
    "username": "outrousername",
    "phone": "11988888888",
    "birthDate": "1995-05-20",
    "gender": "F",
    "isDriver": false
  }'
```

**Resultado esperado:**
- Status: 400 Bad Request
- Message: "Email esta ja em uso."

---

### Tentar verificar com código inválido:
```bash
curl -X POST http://localhost:8080/api/email-verification/verify \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "code": "999999"
  }'
```

**Resultado esperado:**
- Status: 400 Bad Request
- Message: "Código inválido ou expirado"

---

## 📱 Usando Postman/Insomnia

Importe esta collection:

```json
{
  "info": {
    "name": "Via Carona API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Register User",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/auth/register",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"teste@email.com\",\n  \"password\": \"senha12345\",\n  \"name\": \"João da Silva\",\n  \"username\": \"joaosilva\",\n  \"phone\": \"11999999999\",\n  \"birthDate\": \"1990-01-15\",\n  \"gender\": \"M\",\n  \"isDriver\": true\n}"
        }
      }
    },
    {
      "name": "2. Verify Email",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/email-verification/verify",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"teste@email.com\",\n  \"code\": \"123456\"\n}"
        }
      }
    },
    {
      "name": "3. Login",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/auth/login",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"teste@email.com\",\n  \"password\": \"senha12345\"\n}"
        }
      }
    },
    {
      "name": "4. Resend Code",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/email-verification/resend-code",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"teste@email.com\"\n}"
        }
      }
    }
  ]
}
```
