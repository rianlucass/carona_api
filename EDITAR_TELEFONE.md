# Editar Telefone - API

## Endpoint
```
PUT /api/profile/phone
```

## Headers
```
Authorization: Bearer {token}
Content-Type: application/json
```

## Request Body
```json
{
  "phone": "(11) 99999-9999"
}
```

## Formatos aceitos
- `(11) 99999-9999`
- `11999999999`
- `(11) 9 9999-9999`

## Respostas

### Sucesso (200)
```json
{
  "success": true,
  "message": "Telefone atualizado com sucesso",
  "data": {
    "phone": "(11) 99999-9999"
  }
}
```

### Erros

**Telefone vazio (400)**
```json
{
  "message": "O telefone não pode estar vazio",
  "errorCode": "VALIDATION_001"
}
```

**Telefone já existe (409)**
```json
{
  "message": "O telefone {phone} já está cadastrado",
  "errorCode": "USER_003"
}
```

**Formato inválido (400)**
```json
{
  "errors": {
    "phone": "Formato de telefone inválido. Use: (XX) 9XXXX-XXXX ou variações"
  }
}
```
