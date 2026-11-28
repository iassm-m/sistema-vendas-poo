package br.com.sisvendas.dao;

import br.com.sisvendas.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulação de Base de Dados Relacional em Memória e Arquivos Tabulares (CSV).
 * Implementa o Padrão de Projeto Singleton.
 * 
 * Simula 4 tabelas relacionais de banco de dados:
 * - TB_CLIENTES     (dados/clientes.csv)
 * - TB_PRODUTOS     (dados/produtos.csv)
 * - TB_PEDIDOS      (dados/vendas.csv)
 * - TB_ITENS_PEDIDO (dados/vendas_itens.csv)
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class BancoDadosMemoria {
    private static BancoDadosMemoria instancia;
    private static final DateTimeFormatter DATA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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
    // SEED / CARREGAMENTO DE DADOS
    // ==========================================
    private void carregarOuInicializarDados() {
        File fClientes = new File(pastaDados, "clientes.csv");
        File fProdutos = new File(pastaDados, "produtos.csv");
        File fVendas = new File(pastaDados, "vendas.csv");
        File fItens = new File(pastaDados, "vendas_itens.csv");

        if (fClientes.exists() && fProdutos.exists()) {
            carregarClientesCSV(fClientes);
            carregarProdutosCSV(fProdutos);
            if (fVendas.exists()) {
                carregarPedidosCSV(fVendas);
            }
            if (fItens.exists()) {
                carregarItensPedidoCSV(fItens);
            }
        } else {
            inicializarMassaDeDados();
            salvarTodosCSVs();
        }
    }

    private void inicializarMassaDeDados() {
        // Clientes de exemplo
        registrarClienteEmMemoria(new Cliente(null, "Ana Clara Souza", "123.456.789-01", "ana.souza@email.com", "(11) 98765-4321", "Av. Paulista, 1000", "São Paulo"));
        registrarClienteEmMemoria(new Cliente(null, "Carlos Eduardo Silva", "234.567.890-12", "carlos.silva@email.com", "(11) 97654-3210", "Rua das Flores, 250", "Campinas"));
        registrarClienteEmMemoria(new Cliente(null, "Mariana Oliveira", "345.678.901-23", "mariana.oliveira@email.com", "(21) 99887-7665", "Rua Copacabana, 50", "Rio de Janeiro"));
        registrarClienteEmMemoria(new Cliente(null, "Roberto Mendes", "456.789.012-34", "roberto.mendes@email.com", "(31) 98877-6655", "Av. Afonso Pena, 120", "Belo Horizonte"));
        registrarClienteEmMemoria(new Cliente(null, "Fernanda Lima", "567.890.123-45", "fernanda.lima@email.com", "(41) 99123-4567", "Rua XV de Novembro, 300", "Curitiba"));

        // Produtos de exemplo
        registrarProdutoEmMemoria(new Produto(null, "PRD-001", "Notebook Dell Inspiron i5", "Informática", 3899.90, 15));
        registrarProdutoEmMemoria(new Produto(null, "PRD-002", "Mouse Sem Fio Logitech", "Periféricos", 129.90, 40));
        registrarProdutoEmMemoria(new Produto(null, "PRD-003", "Teclado Mecânico RGB", "Periféricos", 349.50, 25));
        registrarProdutoEmMemoria(new Produto(null, "PRD-004", "Monitor 27 Polegadas IPS", "Monitores", 1199.00, 12));
        registrarProdutoEmMemoria(new Produto(null, "PRD-005", "Headset Gamer 7.1", "Áudio", 279.90, 18));
        registrarProdutoEmMemoria(new Produto(null, "PRD-006", "Cadeira Ergonômica Pro", "Mobiliário", 899.00, 8));
        registrarProdutoEmMemoria(new Produto(null, "PRD-007", "SSD NVMe 1TB Kingston", "Hardware", 449.00, 30));
        registrarProdutoEmMemoria(new Produto(null, "PRD-008", "Webcam Full HD 1080p", "Acessórios", 199.90, 20));

        // Pedido de exemplo inicial
        Cliente c1 = buscarClientePorId(101);
        Produto pMouse = buscarProdutoPorId(202);
        Produto pTeclado = buscarProdutoPorId(203);
        if (c1 != null && pMouse != null && pTeclado != null) {
            Pedido p1 = new Pedido(null, "PED-1001", c1, FormaPagamento.PIX);
            p1.adicionarItem(pMouse, 2);
            p1.adicionarItem(pTeclado, 1);
            p1.finalizarVenda();
            registrarPedidoEmMemoria(p1);
        }
    }

    private void registrarClienteEmMemoria(Cliente c) {
        if (c.getId() == null) {
            c.setId(seqCliente.incrementAndGet());
        } else if (c.getId() > seqCliente.get()) {
            seqCliente.set(c.getId());
        }
        tabelaClientes.put(c.getId(), c);
    }

    private void registrarProdutoEmMemoria(Produto p) {
        if (p.getId() == null) {
            p.setId(seqProduto.incrementAndGet());
        } else if (p.getId() > seqProduto.get()) {
            seqProduto.set(p.getId());
        }
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            p.setCodigo("PRD-" + p.getId());
        }
        tabelaProdutos.put(p.getId(), p);
    }

    private void registrarPedidoEmMemoria(Pedido p) {
        if (p.getId() == null) {
            p.setId(seqPedido.incrementAndGet());
        } else if (p.getId() > seqPedido.get()) {
            seqPedido.set(p.getId());
        }
        if (p.getCodigoPedido() == null || p.getCodigoPedido().trim().isEmpty()) {
            p.setCodigoPedido("PED-" + p.getId());
        }
        tabelaPedidos.put(p.getId(), p);
    }

    // ==========================================
    // OPERAÇÕES NA TABELA TB_CLIENTES
    // ==========================================
    public synchronized void inserirCliente(Cliente c) {
        registrarClienteEmMemoria(c);
        salvarClientesCSV();
    }

    public synchronized void atualizarCliente(Cliente c) {
        if (c == null || c.getId() == null) {
            throw new IllegalArgumentException("Cliente inválido para atualização.");
        }
        Cliente existente = tabelaClientes.get(c.getId());
        if (existente != null && existente != c) {
            // Atualiza em memória mantendo a referência gerenciada (preserva integridade em Pedidos)
            existente.setNome(c.getNome());
            existente.setCpf(c.getCpf());
            existente.setEmail(c.getEmail());
            existente.setTelefone(c.getTelefone());
            existente.setLogradouro(c.getLogradouro());
            existente.setCidade(c.getCidade());
        } else {
            tabelaClientes.put(c.getId(), c);
        }
        salvarClientesCSV();
    }

    public synchronized void excluirCliente(Integer id) {
        if (!tabelaClientes.containsKey(id)) {
            throw new IllegalArgumentException("Cliente não encontrado para exclusão.");
        }
        // Verifica integridade referencial com TB_PEDIDOS
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
        registrarProdutoEmMemoria(p);
        salvarProdutosCSV();
    }

    public synchronized void atualizarProduto(Produto p) {
        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("Produto inválido para atualização.");
        }
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            p.setCodigo("PRD-" + p.getId());
        }
        Produto existente = tabelaProdutos.get(p.getId());
        if (existente != null && existente != p) {
            // Atualiza em memória mantendo a referência gerenciada (preserva integridade em ItemPedido)
            existente.setCodigo(p.getCodigo());
            existente.setNome(p.getNome());
            existente.setCategoria(p.getCategoria());
            existente.setPrecoUnitario(p.getPrecoUnitario());
            existente.setQuantidadeEstoque(p.getQuantidadeEstoque());
            existente.setEstoqueMinimo(p.getEstoqueMinimo());
        } else {
            tabelaProdutos.put(p.getId(), p);
        }
        salvarProdutosCSV();
    }

    public synchronized void excluirProduto(Integer id) {
        if (!tabelaProdutos.containsKey(id)) {
            throw new IllegalArgumentException("Produto não encontrado para exclusão.");
        }
        // Verifica integridade referencial com TB_ITENS_PEDIDO
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
        registrarPedidoEmMemoria(p);
        salvarPedidosCSV();
        salvarItensPedidoCSV();
        salvarProdutosCSV(); // Atualiza estoques modificados
    }

    public synchronized void atualizarPedido(Pedido p) {
        if (p == null || p.getId() == null || !tabelaPedidos.containsKey(p.getId())) {
            throw new IllegalArgumentException("Pedido não encontrado para atualização.");
        }
        tabelaPedidos.put(p.getId(), p);
        salvarPedidosCSV();
        salvarItensPedidoCSV();
        salvarProdutosCSV();
    }

    public synchronized void excluirPedido(Integer id) {
        Pedido p = tabelaPedidos.get(id);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado para exclusão.");
        }
        if (p.getStatus() == StatusPedido.FINALIZADO) {
            // Ao excluir um pedido finalizado, estorna estoque por integridade
            p.cancelarVenda();
        }
        tabelaPedidos.remove(id);
        salvarPedidosCSV();
        salvarItensPedidoCSV();
        salvarProdutosCSV();
    }

    public synchronized Pedido buscarPedidoPorId(Integer id) {
        return tabelaPedidos.get(id);
    }

    public synchronized List<Pedido> listarPedidos() {
        return new ArrayList<>(tabelaPedidos.values());
    }

    // ==========================================
    // PERSISTÊNCIA EM ARQUIVOS TABULARES (CSV)
    // ==========================================
    public synchronized void salvarTodosCSVs() {
        salvarClientesCSV();
        salvarProdutosCSV();
        salvarPedidosCSV();
        salvarItensPedidoCSV();
    }

    private String limparCampoCSV(String valor) {
        if (valor == null) return "";
        return valor.replace(";", ",").replace("\r", " ").replace("\n", " ").trim();
    }

    private void salvarClientesCSV() {
        File file = new File(pastaDados, "clientes.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("ID;NOME;CPF;EMAIL;TELEFONE;LOGRADOURO;CIDADE");
            for (Cliente c : tabelaClientes.values()) {
                pw.printf("%d;%s;%s;%s;%s;%s;%s%n",
                        c.getId(),
                        limparCampoCSV(c.getNome()),
                        limparCampoCSV(c.getCpf()),
                        limparCampoCSV(c.getEmail()),
                        limparCampoCSV(c.getTelefone()),
                        limparCampoCSV(c.getLogradouro()),
                        limparCampoCSV(c.getCidade()));
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
                    Integer id = Integer.parseInt(p[0].trim());
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
            pw.println("ID;CODIGO;NOME;CATEGORIA;PRECO_UNITARIO;QUANTIDADE_ESTOQUE;ESTOQUE_MINIMO");
            for (Produto p : tabelaProdutos.values()) {
                pw.printf(Locale.US, "%d;%s;%s;%s;%.2f;%d;%d%n",
                        p.getId(),
                        limparCampoCSV(p.getCodigo()),
                        limparCampoCSV(p.getNome()),
                        limparCampoCSV(p.getCategoria()),
                        p.getPrecoUnitario(),
                        p.getQuantidadeEstoque(),
                        p.getEstoqueMinimo() != null ? p.getEstoqueMinimo() : 5);
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
                    Integer id = Integer.parseInt(p[0].trim());
                    double preco = Double.parseDouble(p[4].trim().replace(",", "."));
                    int estoque = Integer.parseInt(p[5].trim());
                    Produto prod = new Produto(id, p[1], p[2], p[3], preco, estoque);
                    if (p.length >= 7 && !p[6].trim().isEmpty()) {
                        prod.setEstoqueMinimo(Integer.parseInt(p[6].trim()));
                    }
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
            pw.println("ID;CODIGO;CLIENTE_ID;DATA;TOTAL;FORMA_PAGTO;STATUS;QTD_ITENS;DESCONTO;OBSERVACAO");
            for (Pedido ped : tabelaPedidos.values()) {
                pw.printf(Locale.US, "%d;%s;%d;%s;%.2f;%s;%s;%d;%.2f;%s%n",
                        ped.getId(),
                        limparCampoCSV(ped.getCodigoPedido()),
                        ped.getCliente() != null ? ped.getCliente().getId() : 0,
                        ped.getDataVendaFormatada(),
                        ped.getTotalComDesconto(),
                        ped.getFormaPagamento() != null ? ped.getFormaPagamento().name() : "",
                        ped.getStatus() != null ? ped.getStatus().name() : "",
                        ped.getItens().size(),
                        ped.getValorDesconto(),
                        limparCampoCSV(ped.getObservacao()));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar vendas.csv: " + e.getMessage());
        }
    }

    private void carregarPedidosCSV(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha = br.readLine(); // Cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length >= 8) {
                    Integer id = Integer.parseInt(p[0].trim());
                    String codigo = p[1].trim();
                    Integer clienteId = Integer.parseInt(p[2].trim());
                    Cliente cliente = tabelaClientes.get(clienteId);

                    FormaPagamento fp = FormaPagamento.DINHEIRO;
                    if (!p[5].trim().isEmpty()) {
                        try {
                            fp = FormaPagamento.valueOf(p[5].trim());
                        } catch (IllegalArgumentException ignored) {}
                    }

                    StatusPedido status = StatusPedido.FINALIZADO;
                    if (!p[6].trim().isEmpty()) {
                        try {
                            status = StatusPedido.valueOf(p[6].trim());
                        } catch (IllegalArgumentException ignored) {}
                    }

                    Pedido ped = new Pedido(id, codigo, cliente, fp);
                    ped.setStatus(status);

                    if (!p[3].trim().isEmpty()) {
                        try {
                            ped.setDataVenda(LocalDateTime.parse(p[3].trim(), DATA_FMT));
                        } catch (Exception ignored) {}
                    }

                    if (p.length >= 9 && !p[8].trim().isEmpty()) {
                        try {
                            ped.setValorDesconto(Double.parseDouble(p[8].trim().replace(",", ".")));
                        } catch (NumberFormatException ignored) {}
                    }

                    if (p.length >= 10) {
                        ped.setObservacao(p[9]);
                    }

                    tabelaPedidos.put(id, ped);
                    if (id > seqPedido.get()) seqPedido.set(id);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler vendas.csv: " + e.getMessage());
        }
    }

    private void salvarItensPedidoCSV() {
        File file = new File(pastaDados, "vendas_itens.csv");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("PEDIDO_ID;NUMERO_ITEM;PRODUTO_ID;QUANTIDADE;PRECO_UNITARIO");
            for (Pedido ped : tabelaPedidos.values()) {
                for (ItemPedido item : ped.getItens()) {
                    pw.printf(Locale.US, "%d;%d;%d;%d;%.2f%n",
                            ped.getId(),
                            item.getNumeroItem() != null ? item.getNumeroItem() : 0,
                            item.getProduto() != null ? item.getProduto().getId() : 0,
                            item.getQuantidade() != null ? item.getQuantidade() : 0,
                            item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar vendas_itens.csv: " + e.getMessage());
        }
    }

    private void carregarItensPedidoCSV(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String linha = br.readLine(); // Cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";", -1);
                if (p.length >= 5) {
                    Integer pedidoId = Integer.parseInt(p[0].trim());
                    Integer numeroItem = Integer.parseInt(p[1].trim());
                    Integer produtoId = Integer.parseInt(p[2].trim());
                    int quantidade = Integer.parseInt(p[3].trim());
                    double precoUnitario = Double.parseDouble(p[4].trim().replace(",", "."));

                    Pedido ped = tabelaPedidos.get(pedidoId);
                    Produto prod = tabelaProdutos.get(produtoId);
                    if (ped != null && prod != null) {
                        ped.restaurarItem(numeroItem, prod, quantidade, precoUnitario);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler vendas_itens.csv: " + e.getMessage());
        }
    }
}
