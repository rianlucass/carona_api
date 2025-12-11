##Exceptions Necessárias para o Sistema:

1. Autenticação e Autorização:
EmailNotVerifiedException - Email não verificado
InvalidCredentialsException - Credenciais inválidas
TokenGenerationException - Erro ao gerar token
TokenValidationException - Token inválido/expirado
2. Cadastro de Usuário:
EmailAlreadyExistsException - Email já cadastrado
UsernameAlreadyExistsException - Username já em uso
PhoneAlreadyExistsException - Telefone já cadastrado
InvalidDateFormatException - Data em formato inválido
3. Verificação de Email:
VerificationCodeExpiredException - Código expirado
InvalidVerificationCodeException - Código inválido
EmailSendingException - Erro ao enviar email
4. Recursos não encontrados:
UserNotFoundException - Usuário não encontrado
ResourceNotFoundException - Recurso genérico não encontrado
5. Validação:
ValidationException - Erro de validação genérico
