package br.com.sisvendas.dao;

import br.com.sisvendas.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulação de Base de Dados Relacional em Memória e Arquivos Tabulares (CSV).
 * Implementa o Padrão de Projeto Singleton.
 * 
 * Simula 3 tabelas de banco de dados:
 * - TB_CLIENTES
 * - TB_PRODUTOS
 * - TB_PEDIDOS (com itens)
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class BancoDadosMemoria {
    private static BancoDadosMemoria instancia;

    // Tabelas simuladas (Chave Primária -> Registro)
    private final Map<Integer, Cliente> tabelaClientes = new LinkedHashMap<>();
    private final Map<Integer, Produto> tabelaProdutos = new LinkedHashMap<>();
    private final Map<Integer, Pedido> tabelaPedidos = new LinkedHashMap<>();

    // Geradores de Sequência / Auto-incremento
    private final AtomicInteger seqCliente = new AtomicInteger(100);
    private final AtomicInteger seqProduto = new AtomicInteger(200);
    private final AtomicInteger seqPedido = new AtomicInteger(500);

    // Diretório de persistência
    private final File pastaDados;

    private BancoDadosMemoria() {
        pastaDados = new File("dados");
        if (!pastaDados.exists()) {
            pastaDados.mkdirs();
        }
        carregarOuInicializarDados();
    }

    public static synchronized BancoDadosMemoria getInstancia() {
        if (instancia == null) {
            instancia = new BancoDadosMemoria();
        }
        return instancia;
    }

    // ==========================================
    // SEED / DADOS INICIAIS
    // ==========================================
    private void carregarOuInicializarDados() {
        // Tenta carregar dos arquivos CSV, se não existirem cria massa de teste
        File fClientes = new File(pastaDados, "clientes.csv");
        File fProdutos = new File(pastaDados, "produtos.csv");

        if (fClientes.exists() && fProdutos.exists()) {
            carregarClientesCSV(fClientes);
            carregarProdutosCSV(fProdutos);
        } else {
            inicializarMassaDeDados();
            salvarTodosCSVs();
        }
    }

    private void inicializarMassaDeDados() {
        // Clientes de exemplo
        inserirCliente(new Cliente(null, "Ana Clara Souza", "123.456.789-01", "ana.souza@email.com", "(11) 98765-4321", "Av. Paulista, 1000", "São Paulo"));
        inserirCliente(new Cliente(null, "Carlos Eduardo Silva", "234.567.890-12", "carlos.silva@email.com", "(11) 97654-3210", "Rua das Flores, 250", "Campinas"));
        inserirCliente(new Cliente(null, "Mariana Oliveira", "345.678.901-23", "mariana.oliveira@email.com", "(21) 99887-7665", "Rua Copacabana, 50", "Rio de Janeiro"));
        inserirCliente(new Cliente(null, "Roberto Mendes", "456.789.012-34", "roberto.mendes@email.com", "(31) 98877-6655", "Av. Afonso Pena, 120", "Belo Horizonte"));
        inserirCliente(new Cliente(null, "Fernanda Lima", "567.890.123-45", "fernanda.lima@email.com", "(41) 99123-4567", "Rua XV de Novembro, 300", "Curitiba"));

        // Produtos de exemplo
        inserirProduto(new Produto(null, "PRD-001", "Notebook Dell Inspiron i5", "Informática", 3899.90, 15));
        inserirProduto(new Produto(null, "PRD-002", "Mouse Sem Fio Logitech", "Periféricos", 129.90, 40));
        inserirProduto(new Produto(null, "PRD-003", "Teclado Mecânico RGB", "Periféricos", 349.50, 25));
        inserirProduto(new Produto(null, "PRD-004", "Monitor 27 Polegadas IPS", "Monitores", 1199.00, 12));
        inserirProduto(new Produto(null, "PRD-005", "Headset Gamer 7.1", "Áudio", 279.90, 18));
        inserirProduto(new Produto(null, "PRD-006", "Cadeira Ergonômica Pro", "Mobiliário", 899.00, 8));
        inserirProduto(new Produto(null, "PRD-007", "SSD NVMe 1TB Kingston", "Hardware", 449.00, 30));
        inserirProduto(new Produto(null, "PRD-008", "Webcam Full HD 1080p", "Acessórios", 199.90, 20));

        // Pedido de exemplo inicial
        Cliente c1 = buscarClientePorId(101);
        if (c1 != null) {
            Pedido p1 = new Pedido(null, "PED-1001", c1, FormaPagamento.PIX);
            p1.adicionarItem(buscarProdutoPorId(202), 2); // 2 mouse
            p1.adicionarItem(buscarProdutoPorId(203), 1); // 1 teclado
            p1.setStatus(StatusPedido.FINALIZADO);
            inserirPedido(p1);
        }
    }

    // ==========================================
    // OPERAÇÕES NA TABELA TB_CLIENTES
    // ==========================================
    public synchronized void inserirCliente(Cliente c) {
        if (c.getId() == null) {
            c.setId(seqCliente.incrementAndGet());
        } else if (c.getId() > seqCliente.get()) {
            seqCliente.set(c.getId());
        }
        tabelaClientes.put(c.getId(), c);
        salvarClientesCSV();
    }

    public synchronized void atualizarCliente(Cliente c) {
        tabelaClientes.put(c.getId(), c);
        salvarClientesCSV();
    }

    public synchronized void excluirCliente(Integer id) {
        // Verifica se cliente possui pedidos
        for (Pedido p : tabelaPedidos.values()) {
            if (p.getCliente() != null && Objects.equals(p.getCliente().getId(), id)) {
                throw new IllegalStateException("Não é possível excluir o cliente: existem pedidos associados a ele.");
            }
        }
        tabelaClientes.remove(id);
        salvarClientesCSV();
    }

    public synchronized Cliente buscarClientePorId(Integer id) {
        return tabelaClientes.get(id);
    }

    public synchronized List<Cliente> listarClientes() {
        return new ArrayList<>(tabelaClientes.values());
    }

    // ==========================================
    // OPERAÇÕES NA TABELA TB_PRODUTOS
    // ==========================================
    public synchronized void inserirProduto(Produto p) {
        if (p.getId() == null) {
            p.setId(seqProduto.incrementAndGet());
        } else if (p.getId() > seqProduto.get()) {
            seqProduto.set(p.getId());
        }
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            p.setCodigo("PRD-" + p.getId());
        }
        tabelaProdutos.put(p.getId(), p);
        salvarProdutosCSV();
    }

    public synchronized void atualizarProduto(Produto p) {
        tabelaProdutos.put(p.getId(), p);
        salvarProdutosCSV();
    }

    public synchronized void excluirProduto(Integer id) {
        // Verifica se produto está presente em pedidos
        for (Pedido p : tabelaPedidos.values()) {
            for (ItemPedido item : p.getItens()) {
                if (item.getProduto() != null && Objects.equals(item.getProduto().getId(), id)) {
                    throw new IllegalStateException("Não é possível excluir o produto: consta em histórico de pedidos.");
                }
            }
        }
        tabelaProdutos.remove(id);
        salvarProdutosCSV();
    }

    public synchronized Produto buscarProdutoPorId(Integer id) {
        return tabelaProdutos.get(id);
    }

    public synchronized List<Produto> listarProdutos() {
        return new ArrayList<>(tabelaProdutos.values());
    }

    // ==========================================
    // OPERAÇÕES NA TABELA TB_PEDIDOS
    // ==========================================
    public synchronized void inserirPedido(Pedido p) {
        if (p.getId() == null) {
            p.setId(seqPedido.incrementAndGet());
        } else if (p.getId() > seqPedido.get()) {
            seqPedido.set(p.getId());
        }
        if (p.getCodigoPedido() == null || p.getCodigoPedido().trim().isEmpty()) {
            p.setCodigoPedido("PED-" + p.getId());
        }
        tabelaPedidos.put(p.getId(), p);
        salvarPedidosCSV();
        salvarProdutosCSV(); // Atualiza estoques modificados
    }

    public synchronized void atualizarPedido(Pedido p) {
        tabelaPedidos.put(p.getId(), p);
        salvarPedidosCSV();
        salvarProdutosCSV();
    }

    public synchronized void excluirPedido(Integer id) {
        Pedido p = tabelaPedidos.get(id);
        if (p != null) {
            if (p.getStatus() == StatusPedido.FINALIZADO) {
                // Ao excluir um pedido finalizado, estorna estoque por integridade
                p.cancelarVenda();
            }
            tabelaPedidos.remove(id);
            salvarPedidosCSV();
            salvarProdutosCSV();
        }
    }

    public synchronized Pedido buscarPedidoPorId(Integer id) {
        return tabelaPedidos.get(id);
    }

    public synchronized List<Pedido> listarPedidos() {
        return new ArrayList<>(tabelaPedidos.values());
    }

    // ==========================================
    // PERSISTÊNCIA EM ARQUIVOS CSV
    // ==========================================
    public synchronized void salvarTodosCSVs() {
        salvarClientesCSV();
        salvarProdutosCSV();
        salvarPedidosCSV();
    }

    private void salvarClientesCSV() {
        File file = new File(pastaDados, "clientes.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("ID;NOME;CPF;EMAIL;TELEFONE;LOGRADOURO;CIDADE");
            for (Cliente c : tabelaClientes.values()) {
                pw.printf("%d;%s;%s;%s;%s;%s;%s%n",
                        c.getId(),
                        c.getNome() != null ? c.getNome() : "",
                        c.getCpf() != null ? c.getCpf() : "",
                        c.getEmail() != null ? c.getEmail() : "",
                        c.getTelefone() != null ? c.getTelefone() : "",
                        c.getLogradouro() != null ? c.getLogradouro() : "",
                        c.getCidade() != null ? c.getCidade() : "");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar clientes.csv: " + e.getMessage());
        }
    }

    private void carregarClientesCSV(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha = br.readLine(); // Cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length >= 7) {
                    Integer id = Integer.parseInt(p[0]);
                    Cliente c = new Cliente(id, p[1], p[2], p[3], p[4], p[5], p[6]);
                    tabelaClientes.put(id, c);
                    if (id > seqCliente.get()) seqCliente.set(id);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler clientes.csv: " + e.getMessage());
        }
    }

    private void salvarProdutosCSV() {
        File file = new File(pastaDados, "produtos.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("ID;CODIGO;NOME;CATEGORIA;PRECO_UNITARIO;QUANTIDADE_ESTOQUE");
            for (Produto p : tabelaProdutos.values()) {
                pw.printf(Locale.US, "%d;%s;%s;%s;%.2f;%d%n",
                        p.getId(),
                        p.getCodigo() != null ? p.getCodigo() : "",
                        p.getNome() != null ? p.getNome() : "",
                        p.getCategoria() != null ? p.getCategoria() : "",
                        p.getPrecoUnitario(),
                        p.getQuantidadeEstoque());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar produtos.csv: " + e.getMessage());
        }
    }

    private void carregarProdutosCSV(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha = br.readLine(); // Cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length >= 6) {
                    Integer id = Integer.parseInt(p[0]);
                    double preco = Double.parseDouble(p[4]);
                    int estoque = Integer.parseInt(p[5]);
                    Produto prod = new Produto(id, p[1], p[2], p[3], preco, estoque);
                    tabelaProdutos.put(id, prod);
                    if (id > seqProduto.get()) seqProduto.set(id);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler produtos.csv: " + e.getMessage());
        }
    }

    private void salvarPedidosCSV() {
        File file = new File(pastaDados, "vendas.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("ID;CODIGO;CLIENTE_ID;DATA;TOTAL;FORMA_PAGTO;STATUS;QTD_ITENS");
            for (Pedido ped : tabelaPedidos.values()) {
                pw.printf(Locale.US, "%d;%s;%d;%s;%.2f;%s;%s;%d%n",
                        ped.getId(),
                        ped.getCodigoPedido(),
                        ped.getCliente() != null ? ped.getCliente().getId() : 0,
                        ped.getDataVendaFormatada(),
                        ped.getTotalComDesconto(),
                        ped.getFormaPagamento() != null ? ped.getFormaPagamento().name() : "",
                        ped.getStatus() != null ? ped.getStatus().name() : "",
                        ped.getItens().size());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar vendas.csv: " + e.getMessage());
        }
    }
}
