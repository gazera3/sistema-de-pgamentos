# Sistema de Gerenciamento de Contas e Pagamentos

Sistema desenvolvido em Java para gerenciamento de contas e pagamentos, com funcionalidades de bloqueio automático e notificações.

## 📋 Funcionalidades

### Gerenciamento de Contas
- Criação de novas contas com valor esperado e data de vencimento
- Visualização de todas as contas cadastradas
- Acompanhamento de saldo e status

### Sistema de Pagamentos
- Registro de pagamentos parciais ou totais
- Desbloqueio automático após pagamento completo
- Cálculo automático de valores pendentes

### Bloqueio e Desbloqueio
- Bloqueio automático de contas vencidas
- Sistema de avisos antes do bloqueio
- Desbloqueio automático após regularização

### Interface Gráfica
- Tabela de visualização de contas
- Painel de notificações
- Formulários para cadastro e pagamentos

## 🚀 Tecnologias Utilizadas

- Java 17
- Swing para interface gráfica
- Maven para gerenciamento de dependências
- Lombok para redução de boilerplate
- Logback para logging

## 📦 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/Gazera3/sistema-pagamentos.git
```

2. Entre no diretório do projeto:
```bash
cd sistema-pagamentos
```

3. Compile o projeto:
```bash
mvn clean install
```

4. Execute a aplicação:
```bash
java -jar target/account-blocking-system-1.0-SNAPSHOT.jar
```

## 💻 Como Usar

### Criar Nova Conta
1. Clique no botão "Nova Conta"
2. Preencha os dados do titular
3. Defina o valor esperado
4. Estabeleça o prazo de pagamento

### Registrar Pagamento
1. Selecione a conta na tabela
2. Clique em "Registrar Pagamento"
3. Informe o valor recebido
4. O sistema processará automaticamente o pagamento

### Verificar Status
- O sistema verifica automaticamente contas vencidas
- Notificações são exibidas no painel lateral
- Status da conta é atualizado em tempo real

## 🔧 Configuração

O sistema pode ser configurado através do arquivo `logback.xml` para ajustar os níveis de log e destino das mensagens.

## 📊 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── accountmanager/
│   │           ├── model/
│   │           ├── service/
│   │           └── ui/
│   └── resources/
│       └── logback.xml
```

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a Branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## ✨ Funcionalidades Futuras

- [ ] Integração com banco de dados
- [ ] Sistema de autenticação
- [ ] Exportação de relatórios
- [ ] Envio de notificações por email
- [ ] Dashboard com gráficos e estatísticas

## 👥 Autores

* **Seu Nome** - *Desenvolvimento Inicial* - [seu-usuario](https://github.com/seu-usuario)

## 📄 Notas de Versão

* 1.0.0
    * Primeira versão com funcionalidades básicas
    * Interface gráfica implementada
    * Sistema de bloqueio automático
