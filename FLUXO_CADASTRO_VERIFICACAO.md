# 📱 Fluxo de Cadastro e Verificação de Email - Via Carona API

## 🎯 Visão Geral

Este documento descreve o fluxo completo de cadastro e verificação de email para integração com front-end (React, Angular, React Native, etc.).

---

## 🔄 Fluxo Completo

### **1️⃣ CADASTRO (Tela de Registro)**

**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "email": "usuario@email.com",
  "password": "senha12345",
  "name": "João Silva",
  "username": "joaosilva",
  "phone": "11999999999",
  "birthDate": "1990-01-15",
  "gender": "M",
  "isDriver": true
}
```

**Response (201 Created):**
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

**Response (400 Bad Request) - Email já cadastrado:**
```json
{
  "success": false,
  "message": "Email esta ja em uso.",
  "data": null
}
```

**Ação do Front-end:**
- ✅ Mostrar mensagem de sucesso
- ✅ Informar que código foi enviado para o email
- ✅ **Redirecionar para tela de verificação**
- ✅ Passar o email para a próxima tela

---

### **2️⃣ VERIFICAÇÃO DE EMAIL (Tela de Código)**

**Endpoint:** `POST /api/email-verification/verify`

**Request Body:**
```json
{
  "email": "usuario@email.com",
  "code": "123456"
}
```

**Response (200 OK) - Código válido:**
```json
{
  "success": true,
  "message": "Email verificado",
  "data": {
    "success": true,
    "message": "Email verificado com sucesso! Você já pode fazer login.",
    "email": "usuario@email.com"
  }
}
```

**Response (400 Bad Request) - Código inválido:**
```json
{
  "success": false,
  "message": "Código inválido ou expirado",
  "data": null
}
```

**Ação do Front-end:**
- ✅ Mostrar campo para inserir código de 6 dígitos
- ✅ Exibir timer (10 minutos)
- ✅ Se sucesso: **Redirecionar para tela de login**
- ✅ Se erro: Mostrar mensagem e opção de reenviar

---

### **3️⃣ REENVIAR CÓDIGO (Opcional)**

**Endpoint:** `POST /api/email-verification/resend-code`

**Request Body:**
```json
{
  "email": "usuario@email.com"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Novo código enviado para o email",
  "data": null
}
```

**Ação do Front-end:**
- ✅ Botão "Reenviar código"
- ✅ Resetar timer
- ✅ Mostrar feedback visual

---

### **4️⃣ LOGIN (Tela de Login)**

**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "email": "usuario@email.com",
  "password": "senha12345"
}
```

**Response (200 OK) - Email verificado:**
```json
{
  "success": true,
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Response (403 Forbidden) - Email não verificado:**
```json
{
  "success": false,
  "message": "Email não verificado. Verifique seu email antes de fazer login.",
  "data": null
}
```

**Response (401 Unauthorized) - Credenciais inválidas:**
```json
{
  "success": false,
  "message": "Email ou senha inválidos",
  "data": null
}
```

**Ação do Front-end:**
- ✅ Se 200: Salvar token (localStorage/AsyncStorage)
- ✅ Se 403: Redirecionar para tela de verificação
- ✅ Se 401: Mostrar erro de credenciais

---

## 🎨 Exemplo de Implementação Front-end (React)

### **Tela de Cadastro:**
```javascript
async function handleRegister(formData) {
  try {
    const response = await fetch('http://localhost:8080/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Redireciona para tela de verificação
      navigate('/verify-email', { 
        state: { email: formData.email } 
      });
      
      toast.success('Código enviado para seu email!');
    } else {
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Erro ao cadastrar');
  }
}
```

### **Tela de Verificação:**
```javascript
async function handleVerify(code) {
  const { email } = location.state; // Email da tela anterior
  
  try {
    const response = await fetch('http://localhost:8080/api/email-verification/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, code })
    });
    
    const result = await response.json();
    
    if (result.success) {
      toast.success('Email verificado!');
      navigate('/login');
    } else {
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Erro na verificação');
  }
}

async function handleResendCode() {
  const { email } = location.state;
  
  try {
    const response = await fetch('http://localhost:8080/api/email-verification/resend-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });
    
    const result = await response.json();
    
    if (result.success) {
      toast.success('Novo código enviado!');
      resetTimer();
    }
  } catch (error) {
    toast.error('Erro ao reenviar');
  }
}
```

### **Tela de Login:**
```javascript
async function handleLogin(email, password) {
  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const result = await response.json();
    
    if (result.success) {
      // Salva token
      localStorage.setItem('token', result.data.token);
      
      toast.success('Login realizado!');
      navigate('/dashboard');
    } else if (response.status === 403) {
      // Email não verificado
      toast.warning('Verifique seu email primeiro');
      navigate('/verify-email', { state: { email } });
    } else {
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Erro ao fazer login');
  }
}
```

---

## 📊 Diagrama de Fluxo

```
[REGISTRO]
    ↓
Envia dados → API valida → Cria usuário → Envia código
    ↓
[TELA DE VERIFICAÇÃO]
    ↓
Insere código → API valida código → Marca email como verificado
    ↓
[TELA DE LOGIN]
    ↓
Credenciais → API verifica email? → SIM → Retorna token → [DASHBOARD]
                                  → NÃO → Volta para verificação
```

---

## ✅ Checklist de Implementação Front-end

### Telas necessárias:
- [ ] Tela de Cadastro
- [ ] Tela de Verificação de Email (com campo de 6 dígitos)
- [ ] Tela de Login
- [ ] Tela de Dashboard/Home (autenticada)

### Funcionalidades:
- [ ] Validação de formulários
- [ ] Feedback visual (loading, toasts)
- [ ] Timer de 10 minutos na verificação
- [ ] Botão de reenviar código
- [ ] Salvamento de token
- [ ] Proteção de rotas autenticadas
- [ ] Tratamento de erros HTTP

### Headers para requisições autenticadas:
```javascript
headers: {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${token}`
}
```

---

## 🔐 Segurança

- ✅ Código expira em 10 minutos
- ✅ Login bloqueado sem verificação
- ✅ Senha criptografada (BCrypt)
- ✅ Token JWT com expiração
- ✅ CORS configurado
- ✅ Validações no DTO

---

## 🎯 Endpoints Públicos (não requerem autenticação)

- `POST /auth/register`
- `POST /auth/login`
- `POST /api/email-verification/request-code`
- `POST /api/email-verification/verify`
- `POST /api/email-verification/resend-code`

---

## 📝 Notas Importantes

1. **Código é enviado automaticamente** no registro
2. **Login bloqueado** até email ser verificado
3. **Código expira** em 10 minutos
4. **Pode reenviar** código quantas vezes necessário
5. **Respostas padronizadas** com estrutura `ApiResponse`

---

## 🚀 Próximos Passos

- [ ] Implementar recuperação de senha
- [ ] Adicionar rate limiting
- [ ] Logs de auditoria
- [ ] Notificações push
- [ ] Login social (Google, Facebook)
