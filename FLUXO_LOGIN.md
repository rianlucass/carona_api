# 🔐 Fluxo de Login e Autenticação - Carona API

## 📋 Visão Geral

Este documento descreve o fluxo completo de autenticação, desde o registro até o login com validações de segurança.

---

## 🔄 Fluxo Completo de Autenticação

### 📱 Login Tradicional (Email/Senha)

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
                       │  • authProvider = LOCAL
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

### 🔐 Login com Google (OAuth 2.0)

```
┌─────────────────────────────────────────────────────────────┐
│              1. GOOGLE SIGN-IN (React Native)                │
│                   GoogleSignin.signIn()                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Usuário faz login com conta Google
                       ├─ Google retorna idToken
                       │
                       └─ App obtém: idToken, userInfo
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│           2. ENVIAR TOKEN PARA API                           │
│              POST /auth/google                               │
│         Body: { "idToken": "eyJ..." }                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│        3. VALIDAR TOKEN COM GOOGLE                           │
│         GoogleIdTokenVerifier.verify()                       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Valida assinatura do token
                       ├─ Verifica audience (Client ID)
                       ├─ Confirma autenticidade com Google
                       │
                       └─ Se válido, extrai:
                          • email
                          • name
                          • picture (foto de perfil)
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│         4. BUSCAR/CRIAR USUÁRIO                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Busca usuário por email
                       │
                       ├─ Se EXISTE:
                       │  ├─ Gera JWT
                       │  └─ Verifica se perfil está completo:
                       │     • phone, cpf, birthDate preenchidos?
                       │     • profileComplete = true/false
                       │
                       └─ Se NÃO EXISTE (primeiro login):
                          • Cria novo usuário com:
                            - email (do Google)
                            - name (do Google)
                            - username (parte antes do @)
                            - photoUrl (do Google)
                            - emailVerified = true ✅
                            - accountStatus = ACTIVE
                            - role = USER
                            - authProvider = GOOGLE
                          • Salva no banco
                          • Gera JWT
                          • profileComplete = false (dados faltando)
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│            5. RETORNAR RESPOSTA                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       └─ ✅ Retorna:
                          • token (JWT da aplicação)
                          • email
                          • name
                          • pictureUrl
                          • profileComplete (true/false)
                       
                       ↓
                       
┌─────────────────────────────────────────────────────────────┐
│     6. REACT NATIVE VERIFICA profileComplete                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ├─ Se profileComplete = false:
                       │  └─ Redireciona para /auth/registerComplete
                       │     (completar phone, cpf, birthDate)
                       │
                       └─ Se profileComplete = true:
                          └─ Acesso total liberado
                             (usa JWT em requisições)

⚠️  NOTA: Usuários Google com profileComplete=false podem fazer login,
    mas devem ser direcionados para completar o cadastro no app
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

### 5️⃣ **POST /auth/google**
Autenticação via Google Sign-In (OAuth 2.0).

**Como Funciona:**
1. O app React Native usa o Google Sign-In para obter um `idToken`
2. O `idToken` é enviado para a API Spring Boot
3. A API valida o token diretamente com os servidores do Google
4. Se válido, extrai informações do usuário (email, nome, foto)
5. Busca ou cria o usuário no banco de dados
6. Retorna um JWT próprio da aplicação

**Request Body:**
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjdhNGE..."
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Login com Google realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "usuario@gmail.com",
    "name": "Nome do Usuário",
    "pictureUrl": "https://lh3.googleusercontent.com/a/...",
    "profileComplete": false
  }
}
```

**Campos da Resposta:**
- `token`: JWT da aplicação para usar em requisições futuras
- `email`: Email do usuário do Google
- `name`: Nome completo do usuário
- `pictureUrl`: URL da foto de perfil do Google
- `profileComplete`: **true** se o usuário já completou o cadastro (phone, cpf, birthDate), **false** se ainda precisa completar

**Possíveis Erros:**
- `AUTH_002` - Token do Google inválido (HTTP 401)
- `INTERNAL_ERROR` - Erro ao processar login (HTTP 500)

**Características Especiais:**
- ✅ Email é **automaticamente verificado** (Google já valida)
- ✅ Perfil do usuário é **criado com dados do Google**
- ✅ **Não requer** fluxo de verificação de email
- ✅ Username gerado automaticamente (parte antes do @)
- ✅ Campo **`profileComplete`** indica se precisa completar cadastro
- ⚠️ Usuários Google ainda precisam **completar dados adicionais** (phone, cpf, birthDate) para acesso completo

**Configuração Necessária:**
No arquivo `application.properties`:
```properties
google.client.id=SEU_GOOGLE_CLIENT_ID.apps.googleusercontent.com
```

**Implementação no React Native:**
```javascript
import { GoogleSignin } from '@react-native-google-signin/google-signin';

// Configurar Google Sign-In
GoogleSignin.configure({
  webClientId: 'SEU_GOOGLE_CLIENT_ID.apps.googleusercontent.com'
});

// Fazer login
const googleLogin = async () => {
  try {
    await GoogleSignin.hasPlayServices();
    const userInfo = await GoogleSignin.signIn();
    
    // Enviar idToken para a API
    const response = await fetch('http://SEU_IP:8080/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken: userInfo.idToken })
    });
    
    const data = await response.json();
    
    if (data.success) {
      // Salvar JWT retornado pela API
      await AsyncStorage.setItem('token', data.data.token);
      
      // VERIFICAR SE PRECISA COMPLETAR CADASTRO
      if (!data.data.profileComplete) {
        // Redirecionar para tela de completar cadastro
        navigation.navigate('CompleteProfile', { email: data.data.email });
      } else {
        // Perfil completo, ir para tela principal
        navigation.navigate('Home');
      }
    }
  } catch (error) {
    console.error('Erro no login Google:', error);
  }
};
```

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

### Login Tradicional (LOCAL)

| Estado | emailVerified | Dados Completos | Pode fazer Login? | Ação Necessária |
|--------|---------------|-----------------|-------------------|-----------------|
| **Registrado** | ❌ | ❌ | ❌ | Verificar email |
| **Email Verificado** | ✅ | ❌ | ❌ | Completar cadastro |
| **Cadastro Completo** | ✅ | ✅ | ✅ | Pode acessar |

### Login com Google (GOOGLE)

| Estado | emailVerified | Dados Completos | Pode fazer Login? | Ação Necessária |
|--------|---------------|-----------------|-------------------|-----------------|
| **Primeiro Login** | ✅ (automático) | ❌ | ⚠️ Parcial | Completar cadastro |
| **Cadastro Completo** | ✅ | ✅ | ✅ | Acesso total |

**Nota:** Usuários Google têm email automaticamente verificado, mas ainda precisam completar dados adicionais (phone, cpf, birthDate) para acesso completo ao sistema.

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

### Cenário 1: Fluxo Completo com Sucesso (Login Tradicional)
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

### Cenário 4: Login com Google - Primeira Vez
```bash
# 1. Usuário faz Google Sign-In no app React Native
# 2. App recebe idToken do Google

# 3. Enviar para API
POST /auth/google
{ "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6..." }

# 4. API valida com Google, cria usuário e retorna
→ ✅ HTTP 200
{
  "success": true,
  "message": "Login com Google realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
    "email": "usuario@gmail.com",
    "name": "Nome do Usuário",
    "pictureUrl": "https://lh3.googleusercontent.com/...",
    "profileComplete": false  ← Indica que precisa completar
  }
}

# 5. App verifica profileComplete e redireciona para completar cadastro
POST /auth/registerComplete/usuario@gmail.com
{ phone: "11999999999", cpf: "12345678901", birthDate: "1995-05-15" }
→ ✅ Perfil completo, acesso total liberado
```

### Cenário 5: Login com Google - Usuário com Perfil Completo
```bash
# Usuário já fez login com Google antes e completou o cadastro

POST /auth/google
{ "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6..." }

→ ✅ HTTP 200
{
  "success": true,
  "message": "Login com Google realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
    "email": "usuario@gmail.com",
    "name": "Nome do Usuário",
    "pictureUrl": "https://lh3.googleusercontent.com/...",
    "profileComplete": true  ← Perfil já está completo
  }
}
# App redireciona direto para tela principal

### Cenário 6: Login com Google - Usuário Incompleto Retornando
```bash
# Usuário fez login Google antes, mas NÃO completou o cadastro

POST /auth/google
{ "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6..." }

→ ✅ HTTP 200
{
  "success": true,
  "data": {
    "profileComplete": false  ← Ainda precisa completar
  }
}
# App detecta e redireciona novamente para completar cadastro
# Sem criar novo usuário
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
✅ **OAuth 2.0 com Google** - validação server-to-server  
✅ **Google Client ID** configurado no backend  
✅ **Dois provedores de autenticação** (LOCAL e GOOGLE)  

---

## 🔧 Implementação Técnica - Google Login

### Backend (Spring Boot)

**1. Dependência no pom.xml:**
```xml
<dependency>
    <groupId>com.google.api-client</groupId>
    <artifactId>google-api-client</artifactId>
    <version>2.0.0</version>
</dependency>
```

**2. Configuração (application.properties):**
```properties
google.client.id=SEU_GOOGLE_CLIENT_ID.apps.googleusercontent.com
```

**3. Service - GoogleTokenVerifierService.java:**
```java
@Service
public class GoogleTokenVerifierService {
    
    @Value("${google.client.id}")
    private String googleClientId;
    
    public GoogleIdToken.Payload verifyToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), 
            new GsonFactory()
        )
        .setAudience(Collections.singletonList(googleClientId))
        .build();
        
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload(); // email, name, picture
        } else {
            throw new IllegalArgumentException("Token ID inválido");
        }
    }
    
    public AuthResponse buildAuthResponse(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            // Criar novo usuário
            user = new User();
            user.setEmail(email);
            user.setName(payload.get("name"));
            user.setUsername(email.split("@")[0]);
            user.setPhotoUrl(payload.get("picture"));
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setEmailVerified(true);
            user.setRole(UserRole.USER);
            userRepository.save(user);
        }
        
        String token = tokenService.generateToken(user);
        return new AuthResponse(token, email, name, pictureUrl);
    }
}
```

**4. Controller - AuthenticationController.java:**
```java
@PostMapping("/google")
public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
    try {
        GoogleIdToken.Payload payload = googleService.verifyToken(request.idToken());
        AuthResponse response = googleService.buildAuthResponse(payload);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login com Google realizado com sucesso", response));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>(false, "Token do Google inválido", null));
    }
}
```

**5. SecurityConfiguration.java:**
```java
.requestMatchers(HttpMethod.POST, "/auth/google").permitAll()
```

### Frontend (React Native)

**1. Instalar pacote:**
```bash
npm install @react-native-google-signin/google-signin
```

**2. Configurar e usar:**
```javascript
import { GoogleSignin } from '@react-native-google-signin/google-signin';

// Configurar (no início do app)
GoogleSignin.configure({
  webClientId: 'SEU_GOOGLE_CLIENT_ID.apps.googleusercontent.com'
});

// Função de login
const handleGoogleLogin = async () => {
  try {
    await GoogleSignin.hasPlayServices();
    const userInfo = await GoogleSignin.signIn();
    
    // Enviar idToken para backend
    const response = await fetch('http://192.168.x.x:8080/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken: userInfo.idToken })
    });
    
    const data = await response.json();
    
    if (data.success) {
      await AsyncStorage.setItem('token', data.data.token);
      navigation.navigate('Home');
    }
  } catch (error) {
    console.error('Erro:', error);
  }
};
```

---

## 🔑 Diferenças entre Provedores de Autenticação

| Aspecto | LOCAL (Email/Senha) | GOOGLE (OAuth 2.0) |
|---------|---------------------|-------------------|
| **Verificação Email** | Manual (código 6 dígitos) | Automática ✅ |
| **Senha** | Obrigatória (BCrypt) | Não possui |
| **Username** | Escolhido pelo usuário | Auto-gerado (parte do email) |
| **Foto de Perfil** | Upload manual | Do Google |
| **Primeiro Acesso** | Registro → Verificar → Completar | Google Sign-In → Completar |
| **AuthProvider** | LOCAL | GOOGLE |
| **Fluxo de Verificação** | 3 etapas | 2 etapas |  

