# Organização de Uploads de Arquivos

## Estrutura de Diretórios

A organização dos uploads segue a seguinte estrutura hierárquica:

```
uploads/
├── users/
│   ├── {userId}/
│   │   ├── {uuid}_user_profile_{timestamp}.jpg
│   │   └── {uuid}_user_profile_{timestamp}.png
│   └── {userId}/
│       └── ...
└── vehicles/
    ├── {vehicleId}/
    │   ├── {uuid}_vehicle_photo_{timestamp}.jpg
    │   └── {uuid}_vehicle_photo_{timestamp}.jpg
    └── {vehicleId}/
        └── ...
```

## Exemplo Prático

**Upload de foto de perfil do usuário ID 123:**
```
uploads/users/123/31c731a5-5cf4-49a8-a7d4-c1613de3994f_user_profile_1766602369218.jpg
```

**Upload de foto de veículo ID 456:**
```
uploads/vehicles/456/a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6_vehicle_photo_1766602400000.jpg
```

## Como Usar

### 1. Tipos de Arquivo Disponíveis

Enum `FileType` localizado em `domain/file/FileType.java`:

- `USER_PROFILE` → diretório `users/`
- `VEHICLE_PHOTO` → diretório `vehicles/`

### 2. Upload de Arquivo

```java
@Autowired
private FileStorageService fileStorageService;

// Upload de foto de perfil
String photoPath = fileStorageService.uploadPhoto(
    multipartFile,           // Arquivo
    FileType.USER_PROFILE,   // Tipo
    userId.toString()        // ID do proprietário
);

// Retorna: "users/123/uuid_user_profile_timestamp.jpg"
```

### 3. Acesso às Imagens

As imagens podem ser acessadas via URL:

```
http://localhost:8080/uploads/users/123/arquivo.jpg
http://localhost:8080/uploads/vehicles/456/foto.jpg
```

### 4. Deletar Arquivo

```java
// Usar o caminho retornado pelo upload
boolean deleted = fileStorageService.deleteFile(photoPath);
```

O método também tenta limpar diretórios vazios automaticamente.

## Vantagens desta Abordagem

1. **Organização**: Fácil localizar arquivos por tipo e proprietário
2. **Escalabilidade**: Evita diretórios com milhares de arquivos misturados
3. **Segurança**: Pode implementar controle de acesso por proprietário
4. **Manutenção**: Facilita backup e limpeza de dados órfãos
5. **Performance**: Sistemas de arquivos têm melhor performance com diretórios menores

## Boas Práticas Implementadas

✅ **Separação por tipo** (users, vehicles)  
✅ **Isolamento por ID** (cada entidade tem sua pasta)  
✅ **Nomes únicos** (UUID + tipo + timestamp)  
✅ **Validação de tamanho** (máximo 5MB)  
✅ **Validação de tipo** (apenas imagens permitidas)  
✅ **Limpeza automática** (remove diretórios vazios)  
✅ **Compatibilidade** (método legado mantido com @Deprecated)

## Extensões Permitidas

- jpg, jpeg
- png
- gif
- bmp
- webp

## Configuração

No `application.properties`:

```properties
# Tamanho máximo do arquivo
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

# Diretório de uploads
app.upload.dir=./uploads
```

## Próximos Passos

Para adicionar novos tipos de upload:

1. Adicione novo valor no enum `FileType`
2. Use o novo tipo ao chamar `uploadPhoto()`
3. Pronto! A estrutura de diretórios é criada automaticamente
