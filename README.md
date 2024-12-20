# BookLore

## Índice

- [Visão Geral](#visão-geral)
- [Recursos](#recursos)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
    - [Backend (book-social-network)](#backend-book-social-network)
    - [Frontend (book-social-network-ui)](#frontend-book-social-network-ui)
- [Objetivos de Aprendizado](#objetivos-de-aprendizado)
- [Licença](#licença)
- [Iniciando](#iniciando)
- [Colaboradores](#colaboradores)
- [Agradecimentos](#agradecimentos)

## Visão Geral

BookLore é uma aplicação full-stack que permite aos usuários gerenciar suas coleções de livros e interagir com uma comunidade de entusiastas de livros. A aplicação oferece recursos como registro de usuários, validação de e-mail segura, gerenciamento de livros (incluindo criação, atualização, compartilhamento e arquivamento), empréstimo de livros com verificações de disponibilidade, funcionalidade de devolução de livros e aprovação de devoluções. A segurança é garantida por meio de tokens JWT, e as melhores práticas de design de APIs REST são seguidas. O backend foi construído com Spring Boot 3 e Spring Security 6, enquanto o frontend foi desenvolvido utilizando Angular com Tailwind/Daisy-UI para estilização.

## Recursos

- **Registro de Usuários**: Permite que novos usuários criem contas.
- **Validação de E-mail**: Ativação de contas com códigos de validação de e-mail seguros.
- **Autenticação de Usuários**: Usuários existentes podem fazer login com segurança.
- **Gerenciamento de Livros**: Criação, atualização, compartilhamento e arquivamento de livros pelos usuários.
- **Empréstimo de Livros**: Verificações para determinar a disponibilidade dos livros para empréstimo.
- **Devolução de Livros**: Funcionalidade para devolução de livros emprestados.
- **Aprovação de Devoluções**: Funcionalidade para aprovar devoluções de livros.

#### Diagrama de classes
![Diagrama de classes](screenshots/class-diagram.png)

#### Diagrama de segurança do Spring
![Diagrama de segurança](screenshots/security.png)

#### Pipeline do backend
![Pipeline do backend](screenshots/be-pipeline.png)

#### Pipeline do frontend
![Pipeline do frontend](screenshots/fe-pipeline.png)

## Tecnologias Utilizadas

### Backend (booklore)

- Spring Boot 3
- Spring Security 6
- Autenticação com Token JWT
- Spring Data JPA
- Validação JSR-303 e do Spring
- Documentação OpenAPI e Swagger UI
- Docker
- GitHub Actions
- Keycloak

### Frontend (borelore-ui)

- Angular  
- Arquitetura Baseada em Componentes  
- Lazy Loading
- Guarda de Autenticação (Authentication Guard)  
- Gerador OpenAPI para Angular  
- Tailwind
- Daisy-Ui

## Aprendizagem  

Durante o desenvolvimento do projeto, aprendi como:  

- Projetar um diagrama de classes a partir de requisitos de negócio
- Organizar o projeto com Arquitetura Limpa
- Implementar uma abordagem de mono-repositório  
- Proteger uma aplicação usando tokens JWT com Spring Security  
- Registrar usuários e validar contas por meio de e-mail  
- Utilizar herança com Spring Data JPA  
- Implementar a camada de serviço e lidar com exceções da aplicação  
- Validar objetos utilizando JSR-303 e Spring Validation  
- Tratar exceções personalizadas  
- Implementar paginação e seguir as melhores práticas de APIs REST  
- Usar Perfis do Spring para configurações específicas de ambiente  
- Documentar APIs utilizando OpenAPI e Swagger UI  
- Implementar requisitos de negócio e gerenciar exceções de negócio  
- Dockerizar a infraestrutura  
- Configurar e utilizar pipelines de CI/CD e realizar deploys  

## Iniciando  

Para começar a trabalhar no projeto BookLore, siga as instruções de configuração disponíveis nos diretórios correspondentes:  

- [Instruções de Configuração do Backend](/book-network/README.md)  
- [Instruções de Configuração do Frontend](book-network-ui/README.md)  
