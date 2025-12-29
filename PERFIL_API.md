# 👤 API de Perfil - Carona API

## 📋 Visão Geral

Endpoints para gerenciamento do perfil do usuário autenticado.

---

## 🔐 Autenticação Necessária

Todos os endpoints requerem token JWT no header:
```
Authorization: Bearer {seu_token_jwt}
```

---

## 📍 Endpoints

### 1️⃣ **BUSCAR PERFIL**

**GET** `/api/profile`

Retorna os dados completos do perfil do usuário autenticado.

#### Request
```bash
GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer {seu_token_jwt}"
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Perfil recuperado com sucesso",
  "data": {
    "username": "rianlucass",
    "name": "Rian Lucas",
    "email": "rian@email.com",
    "phone": "11999999999",
    "photoUrl": "users/uuid-123/photo.jpg",
    "isDriver": false,
    "city": "São Paulo",
    "state": "SP",
    "cpf": "12345678900",
    "gender": "M",
    "birthDate": "1990-01-15"
  }
}
```

---

### 2 **ATUALIZAR FOTO DE PERFIL**

**PUT** `/api/profile/photo`

Atualiza apenas a foto do perfil do usuário autenticado.

#### Request
```bash
PUT http://localhost:8080/api/profile/photo \
  -H "Authorization: Bearer {seu_token_jwt}" \
  -F "photo=@/caminho/para/sua/foto.jpg"
```

- O campo `photo` deve ser enviado como arquivo (multipart/form-data).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Foto de perfil atualizada com sucesso: url_da_foto"
}
```

---

### 3 **TROCAR NOME DE USUARIO (USERNAME)**

Trocar o nome de usuario mas possibilitando outra troca após 60 dias

### Resquest
```bash
PUT http://localhost:8080/api/profile/username \
  -H "Authorization: Bearer {seu_token_jwt}" \
  {
    "newUsername":"rianlucass"
  }
```

- o campo `username` deve ser enviado em JSON

### Response (200 OK)

```json
{
    "data": {
        "username": "rianlucas"
    },
    "message": "Username atualizado com sucesso",
    "success": true
}
```

### Response (403 Forbidden)

```json
{
    "errorCode": "USER_003",
    "message": "O username informado já está em uso. Escolha outro.",
    "path": "/api/profile/username",
    "success": false,
    "timestamp": "2025-12-27T16:36:34.3692335"
}
```

ou

```json
{
    "errorCode": "USER_003",
    "message": "O username só pode ser alterado uma vez a cada 60 dias, sua próxima alteração estará disponível em " + diasRestantes + " dias.",
    "path": "/api/profile/username",
    "success": false,
    "timestamp": "2025-12-27T16:36:34.3692335"
}
```