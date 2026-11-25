# SisVendas - Sistema de Informação e Gestão Comercial em Java (POO)

> **Trabalho da Disciplina de Programação Orientada a Objetos**  > **Avaliação A2** <br>
> **Entrega:** Postagem individual no Microsoft Teams até o dia da A2 (28/11) <br>
> **Grupo:** IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978,  SILVANA GOMES DA SILVA - 1250109767.


---

## 📌 1. Visão Geral do Projeto

O **SisVendas** é um sistema de informação comercial desktop desenvolvido em **Java**, aplicando rigorosamente todos os conceitos fundamentais de **Programação Orientada a Objetos (POO)**:
- **Abstração e Encapsulamento**: Atributos privados, getters/setters criteriosos, validações de integridade e métodos de negócio.
- **Herança**: Classe base abstrata `EntidadeBase` compartilhada por todas as entidades com controle de identificadores e auditoria temporal.
- **Polimorfismo e Interfaces**: Interface genérica `DAO<T>` para persistência desacoplada e polimórfica.
- **Associação, Agregação e Composição**: Relacionamento estruturado entre `Cliente`, `Produto`, `ItemPedido` e `Pedido`.

---

## 🎯 2. Requisitos Atendidos

| Requisito do Enunciado | Implementação no SisVendas | Status |
| :--- | :--- | :---: |
| **Mini-mundo simples com 2 ou 3 classes de domínio** | 3 Classes centrais: `Cliente`, `Produto` e `Pedido` (com `ItemPedido` em composição) | ✅ Atendido |
| **CRUD para todas as classes de domínio** | Inclusão, Consulta/Filtro, Alteração e Exclusão completas para `Cliente` (`TelaCliente`) e `Produto` (`TelaProduto`) | ✅ Atendido |
| **Desenvolver uma tela de negócio** | `TelaVenda`: Seleção de cliente, carrinho dinâmico, validação de estoque em tempo real, cálculo de descontos por forma de pagamento, baixa automática de estoque e emissão de cupom fiscal | ✅ Atendido |
| **Simulação de base de dados para persistir informações em tabelas** | `BancoDadosMemoria`: Padrão Singleton simulando tabelas relacionais em memória com sincronização em arquivos tabulares CSV (`clientes.csv`, `produtos.csv`, `vendas.csv`) e exibição em `JTable` | ✅ Atendido |
| **Conter arquivo `.frm` (interface gráfica)** | Arquivos `.frm` e `.form` para todos os formulários (`TelaPrincipal.frm`, `TelaCliente.frm`, `TelaProduto.frm`, `TelaVenda.frm`) | ✅ Atendido |
| **Entregável 1: Arquivos do projeto** | Código-fonte completo, estrutura Maven/Ant/scripts batch, pronto para execução | ✅ Atendido |
| **Entregável 2: PDF detalhado em padrão ABNT** | Documento `SisVendas_Documentacao_ABNT.pdf` formatado segundo as normas ABNT NBR 14724 | ✅ Atendido |

---

## 🏗️ 3. Estrutura do Projeto

```
sistema-vendas-poo/
├── docs/
│   ├── SisVendas_Documentacao_ABNT.pdf   <-- PDF ABNT 
│   ├── DOCUMENTACAO_PROJETO_ABNT.md      <-- Documentação completa em Markdown
│   └── diagrams/                         <-- Diagramas UML (PlantUML)
├── src/
│   └── main/
│       ├── java/br/com/sisvendas/
│       │   ├── Main.java                 <-- Classe executável principal
│       │   ├── model/                    <-- Classes de Domínio (POO)
│       │   │   ├── EntidadeBase.java     (Herança)
│       │   │   ├── Cliente.java          (Classe de Domínio 1)
│       │   │   ├── Produto.java          (Classe de Domínio 2)
│       │   │   ├── ItemPedido.java       (Composição)
│       │   │   ├── Pedido.java           (Classe de Domínio 3)
│       │   │   ├── FormaPagamento.java   (Enum)
│       │   │   └── StatusPedido.java     (Enum)
│       │   ├── dao/                      <-- Camada de Persistência Simulada
│       │   │   ├── DAO.java              (Interface Polimórfica)
│       │   │   ├── BancoDadosMemoria.java(Simulador de BD Singleton + CSV)
│       │   │   ├── ClienteDAO.java
│       │   │   ├── ProdutoDAO.java
│       │   │   └── PedidoDAO.java
│       │   ├── service/                  <-- Regras de Negócio
│       │   │   ├── ClienteService.java
│       │   │   ├── ProdutoService.java
│       │   │   └── VendaService.java     (Lógica da Tela de Negócio)
│       │   └── view/                     <-- Interface Gráfica (Swing + .frm)
│       │       ├── TelaPrincipal.java / .frm / .form
│       │       ├── TelaCliente.java   / .frm / .form (CRUD)
│       │       ├── TelaProduto.java   / .frm / .form (CRUD)
│       │       ├── TelaVenda.java     / .frm / .form (TELA DE NEGÓCIO)
│       │       └── TelaConsultaVendas.java
│       └── resources/dados/              <-- Tabelas de persistência simulada
├── build.bat                             <-- Script de compilação rápida no Windows
├── run.bat                               <-- Script de execução rápida no Windows
├── pom.xml                               <-- Configuração Maven
└── README.md
```

---

## 💻 4. Como Executar o Projeto

### Opção A: Execução Rápida via Terminal / Prompt de Comando (Windows)
1. Dê um duplo clique no arquivo `run.bat` (ou execute `run.bat` no terminal).
2. O script compilará automaticamente os fontes na pasta `bin/` e abrirá a interface gráfica.

### Opção B: No Apache NetBeans IDE
1. Abra o NetBeans.
2. Vá em **File -> Open Project...** e selecione a pasta `sistema-vendas-poo`.
3. Clique com o botão direito no projeto e selecione **Run** (ou pressione **F6**).
4. Os formulários gráficos podem ser inspecionados visualmente através dos arquivos `.form` e `.frm`.

### Opção C: No Visual Studio Code ou IntelliJ IDEA
1. Abra a pasta do projeto no VS Code ou IntelliJ IDEA.
2. Certifique-se de que a extensão "Extension Pack for Java" está instalada.
3. Abra `src/main/java/br/com/sisvendas/Main.java` e clique no botão **Run** acima do método `main`.

---

