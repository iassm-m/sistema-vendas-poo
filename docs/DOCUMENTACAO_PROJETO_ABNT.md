# SISTEMA DE GESTÃO COMERCIAL E INFORMAÇÃO (SISVENDAS)
## UNIVERSIDADE VEIGA DE ALMEIDA – UVA
### CURSO DE CIÊNCIA DA COMPUTAÇÃO / ENGENHARIA DE SOFTWARE
### RELATÓRIO TÉCNICO E DE PROJETO EM PADRÃO ABNT (NBR 14724)

**Disciplina:** Programação Orientada a Objetos (POO)  
**Avaliação:** Trabalho da A2  
> **Grupo:** IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978,  SILVANA GOMES DA SILVA - 1250109767.  
**Local e Ano:** Rio de Janeiro – RJ, 2026  

---

### SUMÁRIO

1. **INTRODUÇÃO**  
   1.1. Contextualização do Mini-Mundo  
   1.2. Objetivos do Trabalho  
2. **REQUISITOS DO SISTEMA DE INFORMAÇÃO**  
   2.1. Requisitos Funcionais (RF)  
   2.2. Requisitos Não Funcionais (RNF)  
3. **MODELAGEM ORIENTADA A OBJETOS**  
   3.1. Abstração e Encapsulamento  
   3.2. Herança e Polimorfismo  
   3.3. Associação, Composição e Agregação  
   3.4. Descrição Detalhada das Classes de Domínio  
4. **ARQUITETURA E PADRÕES DE PROJETO (DESIGN PATTERNS)**  
   4.1. Camada Model-View-Controller/Service  
   4.2. Padrão Data Access Object (DAO)  
   4.3. Padrão Singleton  
5. **IMPLEMENTAÇÃO DAS OPERAÇÕES DO CRUD**  
   5.1. CRUD da Entidade Cliente  
   5.2. CRUD da Entidade Produto  
6. **IMPLEMENTAÇÃO DA TELA DE NEGÓCIO**  
   6.1. Fluxo do Caso de Uso de Venda  
   6.2. Validação de Estoque em Tempo Real  
   6.3. Cálculo Dinâmico de Valores e Descontos  
   6.4. Baixa Automática de Estoque e Emissão de Cupom  
7. **BASE DE DADOS SIMULADA E PERSISTÊNCIA TABULAR**  
   7.1. Estrutura das Tabelas Simuladas  
   7.2. Sincronização com Arquivos CSV  
8. **INTERFACE GRÁFICA E ARQUIVOS .FRM**  
   8.1. Estrutura dos Formulários Swing  
   8.2. Especificação dos Arquivos .frm e .form  
9. **GUIA DE EXECUÇÃO E TESTES DO SISTEMA**  
   9.1. Pré-requisitos  
   9.2. Roteiro de Testes para Avaliação  
10. **CONCLUSÃO**  
11. **REFERÊNCIAS BIBLIOGRÁFICAS**  

---

### 1. INTRODUÇÃO

#### 1.1. Contextualização do Mini-Mundo
O presente projeto consiste no desenvolvimento do **SisVendas**, um sistema de informação comercial desenvolvido na linguagem de programação Java, com foco na consolidação prática dos conceitos de Programação Orientada a Objetos (POO) lecionados durante a disciplina.

O mini-mundo modelado compreende o fluxo de atendimento e vendas de uma loja de equipamentos e suprimentos de tecnologia. O sistema permite o cadastro completo de clientes, o controle rigoroso do catálogo de produtos e seus respectivos saldos de estoque, além da execução da operação comercial principal: a emissão de vendas (pedidos), onde o operador seleciona o cliente, inclui itens no carrinho, define condições de pagamento, valida disponibilidades de estoque em tempo real e efetua a baixa automática dos produtos na base de dados.

#### 1.2. Objetivos do Trabalho
- Aplicar na prática os quatro pilares da Orientação a Objetos: Abstração, Encapsulamento, Herança e Polimorfismo.
- Implementar operações completas de CRUD (Create, Read, Update, Delete) para as classes de domínio.
- Projetar e implementar uma **Tela de Negócio** funcional com regras de negócio transacionais.
- Simular uma base de dados estruturada em tabelas, garantindo persistência das informações.
- Construir formulários visuais com interface gráfica Swing e arquivos de definição `.frm`.
- Estruturar a documentação acadêmica detalhada em conformidade com as normas da ABNT.

---

### 2. REQUISITOS DO SISTEMA DE INFORMAÇÃO

#### 2.1. Requisitos Funcionais (RF)
- **RF01 - CRUD de Clientes:** O sistema deve permitir cadastrar, consultar (por ID ou busca textual por nome), alterar e excluir clientes, validando nome e formato de CPF.
- **RF02 - CRUD de Produtos:** O sistema deve permitir cadastrar, consultar, atualizar e excluir produtos, controlando código, categoria, preço unitário e quantidade em estoque.
- **RF03 - Tela de Negócio (Realização de Venda):** O sistema deve fornecer interface específica para emissão de vendas, permitindo associar um cliente a múltiplos produtos com suas respectivas quantidades.
- **RF04 - Validação de Estoque:** O sistema não deve permitir a inclusão de itens em quantidade superior ao estoque físico disponível na base de dados.
- **RF05 - Cálculo de Descontos e Totais:** O sistema deve calcular automaticamente o subtotal bruto, aplicar descontos conforme a forma de pagamento (ex.: 5% para Dinheiro e PIX) ou desconto manual e calcular o valor final.
- **RF06 - Baixa de Estoque Automática:** Ao concluir uma venda, o sistema deve debitar imediatamente a quantidade vendida de cada produto na base de dados.
- **RF07 - Emissão de Cupom/Comprovante:** O sistema deve gerar e exibir um cupom fiscal/comprovante textual detalhado da venda realizada.
- **RF08 - Cancelamento com Estorno:** O sistema deve permitir o cancelamento de uma venda anterior, estornando automaticamente os itens devolvidos ao estoque de produtos.
- **RF09 - Persistência Tabular:** As informações devem ser persistidas através de tabelas simuladas em memória e sincronizadas em arquivos de dados estruturados.

#### 2.2. Requisitos Não Funcionais (RNF)
- **RNF01 - Linguagem e Plataforma:** Desenvolvido em Java padrão (SE 8 ou superior), compatível com qualquer sistema operacional (multiplataforma).
- **RNF02 - Interface Gráfica:** Construída em Java Swing com Look and Feel moderno e responsivo, contendo arquivos `.frm` e `.form`.
- **RNF03 - Arquitetura Limpa:** Separação estrita em camadas (Model, DAO, Service, View).
- **RNF04 - Usabilidade:** Feedback visual imediato ao usuário através de mensagens dialogadas (`JOptionPane`) e tabelas interativas (`JTable`).

---

### 3. MODELAGEM ORIENTADA A OBJETOS

#### 3.1. Abstração e Encapsulamento
Todas as entidades do sistema ocultam seus atributos internos utilizando modificadores de acesso restritivos (`private` e `protected`). O acesso e a alteração dos estados ocorrem exclusivamente por meio de métodos seletores (*getters*) e modificadores (*setters*), que incluem validações de integridade e regras de coerência.

#### 3.2. Herança e Polimorfismo
A classe abstrata `EntidadeBase` encapsula atributos e comportamentos comuns a todas as entidades do sistema:
- Atributos: `id` (identificador único gerado por sequência), `dataCriacao` e `dataUltimaModificacao`.
- Método Abstrato: `public abstract void validar()`, obrigando cada subclasse a fornecer sua própria implementação de validação polimórfica.

Adicionalmente, o polimorfismo é aplicado no padrão `DAO<T>`, onde uma única interface genérica define contratos para manipulação de qualquer entidade do sistema.

#### 3.3. Associação, Composição e Agregação
- **Composição:** A classe `Pedido` compõe uma coleção de objetos `ItemPedido`. Os itens não possuem ciclo de vida independente do pedido.
- **Agregação/Associação:** Cada `ItemPedido` referencia um `Produto`. O produto existe independentemente de estar ou não vinculado a um pedido específico.
- **Associação Unidirecional:** O `Pedido` associa-se ao `Cliente` comprador.

#### 3.4. Descrição das Classes de Domínio
1. **`Cliente`:**
   - Atributos: `nome` (String), `cpf` (String), `email` (String), `telefone` (String), `logradouro` (String), `cidade` (String).
   - Métodos: `validar()`, `formatarCpf()`, getters e setters.
2. **`Produto`:**
   - Atributos: `codigo` (String), `nome` (String), `categoria` (String), `precoUnitario` (Double), `quantidadeEstoque` (Integer), `estoqueMinimo` (Integer).
   - Métodos: `debitarEstoque(int)`, `creditarEstoque(int)`, `isEstoqueBaixo()`, `validar()`.
3. **`Pedido`:**
   - Atributos: `codigoPedido` (String), `cliente` (Cliente), `itens` (List<ItemPedido>), `dataVenda` (LocalDateTime), `valorDesconto` (Double), `formaPagamento` (FormaPagamento), `status` (StatusPedido).
   - Métodos: `adicionarItem(Produto, int)`, `removerItem(int)`, `getSubtotalBruto()`, `getTotalComDesconto()`, `finalizarVenda()`, `cancelarVenda()`.

---

### 4. ARQUITETURA E PADRÕES DE PROJETO

O sistema foi estruturado seguindo uma divisão clara de responsabilidades:
- **`model`:** Classes de domínio que representam o negócio e contêm lógica intrínseca e validações.
- **`dao`:** Camada de persistência que abstrai o armazenamento dos dados simulados.
- **`service`:** Camada de negócio e orquestração entre interface e base de dados.
- **`view`:** Telas e componentes visuais Swing acoplados aos arquivos `.frm`.

**Padrões de Projeto Utilizados:**
1. **Data Access Object (DAO):** Centraliza o acesso aos dados através da interface `DAO<T>`.
2. **Singleton:** A classe `BancoDadosMemoria` utiliza o padrão Singleton para garantir uma única instância compartilhada de acesso às tabelas do sistema durante todo o ciclo de vida da aplicação.

---

### 5. IMPLEMENTAÇÃO DAS OPERAÇÕES DO CRUD

O sistema oferece interfaces visuais especializadas para manutenção cadastral:
- **Inclusão:** Formulário com validação de campos obrigatórios e gravação com auto-incremento de ID.
- **Consulta:** Filtros de pesquisa em tempo real com exibição dos registros em grade tabular (`JTable`).
- **Alteração:** Ao selecionar um registro na tabela, seus campos são carregados automaticamente nos campos de edição, permitindo modificação e regravação.
- **Exclusão:** Remoção com confirmação explícita do operador (`JOptionPane.YES_NO_OPTION`) e verificação de integridade referencial (impede exclusão de produtos ou clientes com pedidos ativos).

---

### 6. IMPLEMENTAÇÃO DA TELA DE NEGÓCIO

A **`TelaVenda`** materializa a principal regra de negócio do mini-mundo:
1. **Seleção do Cliente:** O usuário escolhe o cliente cadastrado, cujos dados cadastrais (CPF, telefone, endereço) são imediatamente apresentados na tela.
2. **Seleção e Adição de Produtos:** O operador escolhe o produto e define a quantidade. O sistema valida se a quantidade requisitada é suportada pelo saldo em estoque.
3. **Composição Dinâmica da Venda:** A cada produto adicionado, a linha é inserida na tabela visual do carrinho, atualizando o subtotal do item e o total geral.
4. **Fechamento e Liquidação:** O operador escolhe a forma de pagamento (PIX, Dinheiro, Cartão), o sistema aplica o percentual de desconto correspondente ou permite desconto negociado.
5. **Conclusão:** Ao clicar em "Concluir Venda", o sistema aciona `vendaService.realizarVenda()`, que debita o estoque de cada produto na base de dados, registra o pedido na tabela de vendas e emite o comprovante fiscal na tela.

---

### 7. BASE DE DADOS SIMULADA E PERSISTÊNCIA TABULAR

A persistência do sistema é assegurada pela classe `BancoDadosMemoria`:
- Tabelas em Memória: Mapeadas através de estruturas `LinkedHashMap<Integer, Entidade>`, preservando a ordem cronológica de inserção.
- Sincronização em Arquivos: A cada operação de escrita (inclusão, alteração, exclusão ou venda), os dados são salvos em formato tabular CSV (`clientes.csv`, `produtos.csv` e `vendas.csv`) no diretório `dados/`.
- Carga Inicial (*Seed*): O sistema vem acompanhado de dados pré-cadastrados para que o avaliador possa testar o sistema imediatamente sem a necessidade de digitação prévia.

---

### 8. INTERFACE GRÁFICA E ARQUIVOS .FRM

Atendendo ao requisito explícito `"Deverá conter um arquivo .frm (interface gráfica)"`, o projeto conta com:
- `TelaPrincipal.frm` e `TelaPrincipal.java`: Menu geral, botões de atalho e painel de métricas (dashboard com totalizadores e histórico).
- `TelaCliente.frm` e `TelaCliente.java`: Interface do CRUD de clientes com tabela integrada.
- `TelaProduto.frm` e `TelaProduto.java`: Interface do CRUD de produtos com indicador de estoque baixo.
- `TelaVenda.frm` e `TelaVenda.java`: Interface da Tela de Negócio para processamento de pedidos.
- Arquivos `.form` no padrão XML do NetBeans GUI Builder para compatibilidade total em qualquer IDE.

---

### 9. GUIA DE EXECUÇÃO E TESTES DO SISTEMA

#### 9.1. Pré-requisitos
- Java Development Kit (JDK 8, 11, 17 ou superior) instalado.

#### 9.2. Roteiro de Testes Recomendado para Avaliação
1. Execute o script `run.bat` ou abra o projeto no NetBeans/VS Code executando `br.com.sisvendas.Main`.
2. No menu principal, clique em **👤 Clientes**: visualize os clientes pré-carregados, adicione um novo cliente e teste a busca por nome.
3. Clique em **📦 Produtos**: visualize o catálogo, observe os estoques e cadastre um produto de teste.
4. Clique em **🛒 Nova Venda (Negócio)**:
   - Selecione o cliente cadastrado.
   - Adicione 2 itens ao carrinho e observe a validação de quantidade.
   - Mude a forma de pagamento para PIX (5% de desconto automático).
   - Clique em **Concluir Venda e Emitir Cupom**.
   - Observe a emissão do cupom fiscal na janela de diálogo.
5. Retorne à tela de **📦 Produtos** e certifique-se de que o estoque do produto vendido sofreu baixa automática.
6. Acesse a tela de **📋 Histórico Vendas** e visualize a venda registrada na tabela.

---

### 10. CONCLUSÃO

O desenvolvimento do **SisVendas** atingiu com excelência todos os objetivos didáticos e técnicos estabelecidos para a avaliação A2. O software demonstra como os princípios de Orientação a Objetos possibilitam a criação de sistemas modulares, reutilizáveis e robustos. As regras de integridade de dados e as operações transacionais de estoque asseguram um sistema consistente e pronto para uso acadêmico e profissional.

---

### 11. REFERÊNCIAS BIBLIOGRÁFICAS

- ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. **NBR 14724: Informação e documentação — Trabalhos acadêmicos — Apresentação**. Rio de Janeiro: ABNT, 2011.
- ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. **NBR 6023: Informação e documentação — Referências — Elaboração**. Rio de Janeiro: ABNT, 2018.
- DEITEL, Paul; DEITEL, Harvey. **Java: Como Programar**. 10. ed. São Paulo: Pearson Education do Brasil, 2016.
- GAMMA, Erich et al. **Padrões de Projeto: Soluções Reutilizáveis de Software Orientado a Objetos**. Porto Alegre: Bookman, 2000.
- SIERRA, Kathy; BATES, Bert. **Use a Cabeça! Java**. 2. ed. Rio de Janeiro: Alta Books, 2007.
