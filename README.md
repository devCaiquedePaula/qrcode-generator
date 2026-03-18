# QR Code Generator API

## 📋 Descrição do Projeto

API REST que gera códigos QR em tempo real e armazena as imagens geradas em um bucket Amazon S3. O projeto implementa uma solução escalável e profissional utilizando arquitetura hexagonal (ports and adapters) com Spring Boot.

### ✨ Principais Funcionalidades

- **Geração de Códigos QR**: Converte textos em códigos QR (formato PNG)
- **Armazenamento em Nuvem**: Upload automático para Amazon S3
- **Geração de URLs**: Retorna URL pública para acesso direto ao QR Code
- **Tratamento de Erros**: Tratamento robusto de exceções

---

## 🏗️ Arquitetura

O projeto segue a **Arquitetura Hexagonal (Ports & Adapters)**, garantindo:

- **Separação de responsabilidades**: Cada camada tem sua função específica
- **Desacoplamento**: Fácil substituição de implementações
- **Testabilidade**: Código altamente testável
- **Escalabilidade**: Estrutura preparada para crescimento

### Estrutura de Camadas

```
src/main/java/com/devKiq/qrcode_generator/
├── controller/              # Camada de Apresentação (REST API)
│   └── QrCodeController.java
├── service/                 # Camada de Lógica de Negócio
│   └── QrCodeGeneratorService.java
├── ports/                   # Interfaces (Contratos)
│   └── StoragePort.java
├── infrastructure/          # Implementações (Adapters)
│   └── S3StorageAdapter.java
├── dto/                     # Objetos de Transferência de Dados
│   ├── QrCodeGenerateRequestDTO.java
│   └── QrCodeGenerateResponseDTO.java
└── QrcodeGeneratorApplication.java  # Classe Principal
```

---

## 🛠️ Tecnologias Utilizadas

### Backend Framework
- **Spring Boot 4.0.3**: Framework web moderno baseado em Spring
  - Spring Web MVC: Criação de APIs REST
  - Spring DevTools: Reload automático durante desenvolvimento

### Processamento de QR Code
- **Google Zxing 3.5.2**: Biblioteca padrão da indústria para códigos de barras/QR
  - `core`: Lógica de geração
  - `javase`: Suporte específico para Java/PNG

### Integração com AWS
- **AWS SDK v2.24.12**: SDK oficial da Amazon para Java
  - Modulo S3: Upload e gerenciamento de arquivos em buckets S3

### Linguagem & Ferramentas
- **Java 21**: Última versão LTS com features modernas
- **Maven**: Gerenciamento de dependências e build
- **Lombok**: Redução de código boilerplate (getters, setters, construtores)

### Testes
- **JUnit 5 (via Spring Boot Starter Test)**: Framework de testes
- **MockMvc**: Testes de API REST

### Containerização
- **Docker**: Arquivo Dockerfile incluído para deployment em containers

---

## 📡 API REST

### Endpoint: Gerar QR Code

**URL:** `POST /qrcode/generate`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "text": "https://www.exemplo.com"
}
```

**Response (Sucesso - 200 OK):**
```json
{
  "url": "https://seu-bucket.s3.sua-regiao.amazonaws.com/550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (Erro - 500 Internal Server Error):**
```
Sem corpo (body vazio)
```

### Fluxo da Requisição

```
1. Cliente envia POST com texto
        ↓
2. QrCodeController recebe a requisição
        ↓
3. QrCodeGeneratorService gera a imagem PNG
        ↓
4. S3StorageAdapter faz upload para S3
        ↓
5. URL pública é retornada ao cliente
```

---

## 🚀 Como Executar

### Pré-requisitos

- **Java 21+** instalado
- **Maven** instalado
- **Credenciais AWS** configuradas (Access Key ID e Secret Access Key)
- **Bucket S3** criado na AWS

### Variáveis de Ambiente

Configure as variáveis de ambiente antes de executar:

```bash
# Windows (PowerShell)
$env:AWS_REGION = "us-east-1"
$env:AWS_BUCKET_NAME = "seu-bucket-name"

# Linux/Mac
export AWS_REGION="us-east-1"
export AWS_BUCKET_NAME="seu-bucket-name"
```

### Build do Projeto

```bash
# Compilar e executar testes
mvn clean package

# Apenas compilar (sem testes)
mvn clean compile
```

### Executar Aplicação

**Opção 1: Maven**
```bash
mvn spring-boot:run
```

**Opção 2: JAR executável**
```bash
java -jar target/qrcode-generator-0.0.1-SNAPSHOT.jar
```

**Opção 3: Docker**
```bash
docker build -t qrcode-generator .
docker run -e AWS_REGION=us-east-1 -e AWS_BUCKET_NAME=seu-bucket -p 8080:8080 qrcode-generator
```

### Testar a API

```bash
# Usando curl
curl -X POST http://localhost:8080/qrcode/generate \
  -H "Content-Type: application/json" \
  -d '{"text":"https://www.github.com"}'

# Resposta esperada:
# {"url":"https://seu-bucket.s3.us-east-1.amazonaws.com/uuid-aleatorio"}
```

---

## 📊 Detalhes Técnicos

### Geração do QR Code

A classe `QrCodeGeneratorService` utiliza:

1. **QRCodeWriter** do ZXing: Codifica o texto em uma matriz binária
2. **BitMatrix**: Representação matricial do código QR (200x200 pixels)
3. **MatrixToImageWriter**: Converte matriz em imagem PNG

### Upload para S3

A classe `S3StorageAdapter` implementa o padrão Port:

1. Recebe array de bytes (imagem PNG)
2. Gera um UUID único como nome do arquivo
3. Envia para S3 com tipo MIME `image/png`
4. Retorna URL pública formatada no padrão AWS

**URL Retornada:** `https://{bucket}.s3.{region}.amazonaws.com/{fileName}`

### Data Transfer Objects (DTOs)

O projeto utiliza **Java Records** (Java 16+) para DTOs:

- **Imutáveis**: Não podem ser modificados após criação
- **Concisas**: Menos boilerplate
- **Type-safe**: Compilação garante tipagem

```java
// Entrada
public record QrCodeGenerateRequestDTO(String text) { }

// Saída
public record QrCodeGenerateResponseDTO(String url) { }
```

---

## 🔐 Considerações de Segurança

### Implementado

✅ **Injeção de Dependências**: Todas as dependências gerenciadas pelo Spring  
✅ **DTOs**: Transferência de dados segura  
✅ **Variáveis de Ambiente**: Credenciais não hardcoded  
✅ **Interface Port**: Abstração para fácil substituição

### Recomendações Futuras

- 🔒 **Validação de Input**: Adicionar `@Valid` e `@NotBlank` nas DTOs
- 📝 **Logging**: Implementar logger apropriado (SLF4J + Logback)
- 🛡️ **CORS**: Configurar se necessário
- 📊 **Monitoramento**: Adicionar Spring Actuator
- 🔑 **Autenticação**: JWT ou OAuth2 para endpoints privados

---

## 📈 Próximas Melhorias

1. **Validação de Requisições**
   ```java
   public record QrCodeGenerateRequestDTO(
       @NotBlank(message = "Texto não pode ser vazio")
       String text
   ) { }
   ```

2. **Melhor Tratamento de Erros**
   - Global Exception Handler
   - Erros específicos com status HTTP apropriados

3. **Cache**
   - Evitar regeneração de QR codes idênticos
   - Usar Spring Cache

4. **Testes Unitários & Integração**
   - Cobertura de testes mínima de 80%
   - Mock do S3 para testes

---

## 📝 Padrões de Design Utilizados

| Padrão | Localização | Propósito |
|--------|-------------|----------|
| **Hexagonal Architecture** | Estrutura geral | Desacoplamento e testabilidade |
| **Port & Adapter** | ports/ e infrastructure/ | Abstração de implementações |
| **Dependency Injection** | Spring Framework | Gerenciamento de dependências |
| **DTO** | dto/ | Transferência de dados segura |
| **Service Layer** | service/ | Lógica de negócio centralizada |
| **Repository Pattern** | S3StorageAdapter | Abstração de acesso a dados |

---

## 🧪 Testes

O projeto inclui testes básicos em:

```
src/test/java/com/devKiq/qrcode_generator/QrcodeGeneratorApplicationTests.java
```

Para executar testes:

```bash
mvn test
```

---

## 📦 Dependências do POM

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| spring-boot-starter-web | 4.0.3 | Framework web |
| spring-boot-starter-test | 4.0.3 | Testes |
| google-zxing | 3.5.2 | Geração de QR codes |
| aws-sdk-s3 | 2.24.12 | Upload para S3 |
| lombok | Latest | Redução de boilerplate |

---

## 📞 Informações de Contato

- **Desenvolvedor**: devKiq
- **Repositório**: Disponível em arquivo local ou Git
- **Licença**: Não especificada (adicionar conforme necessário)

---

## ✅ Checklist de Deployment

- [ ] Credenciais AWS configuradas
- [ ] Bucket S3 criado e acessível
- [ ] Java 21+ instalado
- [ ] Variáveis de ambiente definidas
- [ ] Build executado com sucesso (`mvn clean package`)
- [ ] Testes passando (`mvn test`)
- [ ] Aplicação iniciada sem erros
- [ ] Endpoint testado e funcionando

---

**Última Atualização:** Março de 2026  
**Versão da Aplicação:** 0.0.1-SNAPSHOT

