package br.com.sisvendas.view;

import br.com.sisvendas.model.*;
import br.com.sisvendas.service.ClienteService;
import br.com.sisvendas.service.ProdutoService;
import br.com.sisvendas.service.VendaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * TELA DE NEGÓCIO: Realização de Vendas Comerciais (SisVendas).
 * Implementa o caso de uso completo de venda:
 * - Seleção de cliente
 * - Composição do carrinho com validação dinâmica de estoque
 * - Aplicação de descontos e formas de pagamento
 * - Baixa automática de estoque na base de dados
 * - Persistência do pedido e emissão de cupom fiscal/comprovante
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class TelaVenda extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient ClienteService clienteService = new ClienteService();
    private final transient ProdutoService produtoService = new ProdutoService();
    private final transient VendaService vendaService = new VendaService();
    private final TelaPrincipal telaPrincipal;

    // Componentes de Seleção de Cliente
    private JComboBox<Cliente> cbClientes;
    private JLabel lblDadosCliente;

    // Componentes de Adição de Produtos
    private JComboBox<Produto> cbProdutos;
    private JSpinner spQuantidade;
    private JLabel lblPrecoUnitario;
    private JLabel lblEstoqueDisponivel;
    private JButton btnAdicionarItem;
    private JButton btnRemoverItem;

    // Tabela do Carrinho (Itens do Pedido)
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;

    // Componentes de Fechamento e Pagamento
    private JComboBox<FormaPagamento> cbFormaPagamento;
    private JTextField txtDesconto;
    private JTextField txtObservacao;
    private JLabel lblSubtotalBruto;
    private JLabel lblValorDesconto;
    private JLabel lblTotalLiquido;
    private JButton btnFinalizarVenda;
    private JButton btnCancelarVenda;

    // Pedido em Construção em Memória
    private Pedido pedidoAtual;

    public TelaVenda(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        this.pedidoAtual = new Pedido();
        initComponents();
        carregarCombos();
    }

    private void initComponents() {
        setTitle("Tela de Negócio - Emissão de Vendas e Pedidos");
        setSize(960, 680);
        setLocationRelativeTo(telaPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(12, 12));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(contentPane);

        // Painel Superior: Seleção de Cliente
        JPanel pnlCliente = new JPanel(new BorderLayout(10, 5));
        pnlCliente.setBorder(BorderFactory.createTitledBorder("1. Seleção do Cliente"));

        cbClientes = new JComboBox<>();
        cbClientes.addActionListener(e -> atualizarDadosCliente());

        lblDadosCliente = new JLabel("Selecione o cliente na lista acima para iniciar a venda.");
        lblDadosCliente.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDadosCliente.setForeground(new Color(60, 60, 60));

        pnlCliente.add(cbClientes, BorderLayout.NORTH);
        pnlCliente.add(lblDadosCliente, BorderLayout.CENTER);

        // Painel Central: Adicionar Produtos + Tabela do Carrinho
        JPanel pnlCentro = new JPanel(new BorderLayout(8, 8));

        // Subpainel: Escolha do Produto e Quantidade
        JPanel pnlAdicionar = new JPanel(new GridBagLayout());
        pnlAdicionar.setBorder(BorderFactory.createTitledBorder("2. Adicionar Itens ao Carrinho (Validação em Tempo Real)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbProdutos = new JComboBox<>();
        cbProdutos.addActionListener(e -> atualizarDadosProduto());

        spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        lblPrecoUnitario = new JLabel("Preço: R$ 0,00");
        lblPrecoUnitario.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblEstoqueDisponivel = new JLabel("Estoque: 0 un.");
        lblEstoqueDisponivel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstoqueDisponivel.setForeground(new Color(0, 102, 204));

        btnAdicionarItem = new JButton("➕ Adicionar ao Pedido");
        btnAdicionarItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdicionarItem.setBackground(new Color(230, 245, 230));
        btnAdicionarItem.addActionListener(e -> adicionarItemAoCarrinho());

        btnRemoverItem = new JButton("🗑️ Remover Item");
        btnRemoverItem.addActionListener(e -> removerItemDoCarrinho());

        gbc.gridx = 0; gbc.gridy = 0; pnlAdicionar.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAdicionar.add(cbProdutos, gbc);
        gbc.weightx = 0.0;
        gbc.gridx = 2; pnlAdicionar.add(lblPrecoUnitario, gbc);
        gbc.gridx = 3; pnlAdicionar.add(lblEstoqueDisponivel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlAdicionar.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; pnlAdicionar.add(spQuantidade, gbc);
        gbc.gridx = 2; pnlAdicionar.add(btnAdicionarItem, gbc);
        gbc.gridx = 3; pnlAdicionar.add(btnRemoverItem, gbc);

        // Subpainel: Tabela dos Itens
        String[] colunas = {"Item #", "Código", "Descrição do Produto", "Preço Unit. (R$)", "Qtd", "Subtotal (R$)"};
        modeloCarrinho = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabelaCarrinho = new JTable(modeloCarrinho);
        tabelaCarrinho.setRowHeight(22);
        tabelaCarrinho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder("Itens do Pedido Atual"));

        pnlCentro.add(pnlAdicionar, BorderLayout.NORTH);
        pnlCentro.add(scrollCarrinho, BorderLayout.CENTER);

        // Painel Inferior: Forma de Pagamento, Desconto e Fechamento
        JPanel pnlFechamento = new JPanel(new BorderLayout(10, 10));
        pnlFechamento.setBorder(BorderFactory.createTitledBorder("3. Fechamento e Pagamento"));

        JPanel pnlPagto = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPagto = new GridBagConstraints();
        gbcPagto.insets = new Insets(4, 6, 4, 6);
        gbcPagto.anchor = GridBagConstraints.WEST;

        cbFormaPagamento = new JComboBox<>(FormaPagamento.values());
        cbFormaPagamento.addActionListener(e -> recalcularTotais());

        txtDesconto = new JTextField("0.00", 7);
        txtDesconto.addActionListener(e -> recalcularTotais());

        JButton btnAplicarDesconto = new JButton("Aplicar Desconto");
        btnAplicarDesconto.addActionListener(e -> aplicarDescontoManual());

        txtObservacao = new JTextField(28);

        gbcPagto.gridx = 0; gbcPagto.gridy = 0; pnlPagto.add(new JLabel("Forma de Pagamento:"), gbcPagto);
        gbcPagto.gridx = 1; pnlPagto.add(cbFormaPagamento, gbcPagto);
        gbcPagto.gridx = 2; pnlPagto.add(new JLabel("Desconto Especial (R$):"), gbcPagto);
        gbcPagto.gridx = 3; pnlPagto.add(txtDesconto, gbcPagto);
        gbcPagto.gridx = 4; pnlPagto.add(btnAplicarDesconto, gbcPagto);

        gbcPagto.gridx = 0; gbcPagto.gridy = 1; pnlPagto.add(new JLabel("Observação:"), gbcPagto);
        gbcPagto.gridx = 1; gbcPagto.gridwidth = 4; gbcPagto.fill = GridBagConstraints.HORIZONTAL;
        pnlPagto.add(txtObservacao, gbcPagto);

        // Painel de Valores Totais
        JPanel pnlTotais = new JPanel(new GridLayout(3, 1, 4, 4));
        pnlTotais.setBorder(new EmptyBorder(5, 10, 5, 20));

        lblSubtotalBruto = new JLabel("Subtotal Bruto: R$ 0,00", SwingConstants.RIGHT);
        lblSubtotalBruto.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblValorDesconto = new JLabel("Desconto Total: R$ 0,00", SwingConstants.RIGHT);
        lblValorDesconto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValorDesconto.setForeground(new Color(180, 0, 0));

        lblTotalLiquido = new JLabel("VALOR FINAL: R$ 0,00", SwingConstants.RIGHT);
        lblTotalLiquido.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalLiquido.setForeground(new Color(0, 120, 0));

        pnlTotais.add(lblSubtotalBruto);
        pnlTotais.add(lblValorDesconto);
        pnlTotais.add(lblTotalLiquido);

        // Ações Finais
        JPanel pnlAcoesFinais = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));

        btnFinalizarVenda = new JButton("✅ Concluir Venda e Emitir Cupom");
        btnFinalizarVenda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFinalizarVenda.setBackground(new Color(39, 174, 96));
        btnFinalizarVenda.setForeground(Color.WHITE);
        btnFinalizarVenda.addActionListener(e -> concluirVenda());

        btnCancelarVenda = new JButton("❌ Cancelar / Novo Pedido");
        btnCancelarVenda.addActionListener(e -> resetarFormulario());

        pnlAcoesFinais.add(btnCancelarVenda);
        pnlAcoesFinais.add(btnFinalizarVenda);

        JPanel pnlSulInferior = new JPanel(new BorderLayout());
        pnlSulInferior.add(pnlPagto, BorderLayout.WEST);
        pnlSulInferior.add(pnlTotais, BorderLayout.EAST);
        pnlSulInferior.add(pnlAcoesFinais, BorderLayout.SOUTH);

        pnlFechamento.add(pnlSulInferior, BorderLayout.CENTER);

        // Montagem Geral
        contentPane.add(pnlCliente, BorderLayout.NORTH);
        contentPane.add(pnlCentro, BorderLayout.CENTER);
        contentPane.add(pnlFechamento, BorderLayout.SOUTH);
    }

    private void carregarCombos() {
        cbClientes.removeAllItems();
        List<Cliente> clientes = clienteService.listarTodos();
        for (Cliente c : clientes) {
            cbClientes.addItem(c);
        }

        cbProdutos.removeAllItems();
        List<Produto> produtos = produtoService.listarTodos();
        for (Produto p : produtos) {
            cbProdutos.addItem(p);
        }

        atualizarDadosCliente();
        atualizarDadosProduto();
    }

    private void atualizarDadosCliente() {
        Cliente c = (Cliente) cbClientes.getSelectedItem();
        if (c != null) {
            String tel = (c.getTelefone() != null && !c.getTelefone().trim().isEmpty()) ? c.getTelefone() : "N/I";
            String cid = (c.getCidade() != null && !c.getCidade().trim().isEmpty()) ? c.getCidade() : "N/I";
            String log = (c.getLogradouro() != null && !c.getLogradouro().trim().isEmpty()) ? " - " + c.getLogradouro() : "";
            lblDadosCliente.setText(String.format("CPF: %s | Telefone: %s | Cidade: %s%s",
                    c.getCpf(), tel, cid, log));
            pedidoAtual.setCliente(c);
        } else {
            lblDadosCliente.setText("Nenhum cliente cadastrado.");
            pedidoAtual.setCliente(null);
        }
    }

    private void atualizarDadosProduto() {
        Produto p = (Produto) cbProdutos.getSelectedItem();
        if (p != null) {
            lblPrecoUnitario.setText(String.format("Preço: R$ %.2f", p.getPrecoUnitario()));
            lblEstoqueDisponivel.setText(String.format("Estoque: %d un.", p.getQuantidadeEstoque()));
            if (p.getQuantidadeEstoque() <= 0) {
                lblEstoqueDisponivel.setForeground(Color.RED);
                lblEstoqueDisponivel.setText("ESGOTADO (0 un.)");
            } else if (p.isEstoqueBaixo()) {
                lblEstoqueDisponivel.setForeground(new Color(200, 100, 0));
            } else {
                lblEstoqueDisponivel.setForeground(new Color(0, 102, 204));
            }
        } else {
            lblPrecoUnitario.setText("Preço: R$ 0,00");
            lblEstoqueDisponivel.setForeground(Color.GRAY);
            lblEstoqueDisponivel.setText("Estoque: 0 un.");
        }
    }

    private void adicionarItemAoCarrinho() {
        Produto selecionado = (Produto) cbProdutos.getSelectedItem();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Produto p = produtoService.buscarPorId(selecionado.getId());
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado na base de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            carregarCombos();
            return;
        }

        int qtd = (Integer) spQuantidade.getValue();
        if (qtd <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validação de regra de negócio: Estoque
        int qtdJaNoCarrinho = 0;
        for (ItemPedido item : pedidoAtual.getItens()) {
            if (item.getProduto().getId().equals(p.getId())) {
                qtdJaNoCarrinho += item.getQuantidade();
            }
        }

        if (p.getQuantidadeEstoque() < (qtdJaNoCarrinho + qtd)) {
            JOptionPane.showMessageDialog(this,
                    String.format("Estoque insuficiente! Disponível: %d un. Já no carrinho: %d un.",
                            p.getQuantidadeEstoque(), qtdJaNoCarrinho),
                    "Estoque Insuficiente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        pedidoAtual.adicionarItem(p, qtd);
        atualizarTabelaCarrinho();
        spQuantidade.setValue(1);
    }

    private void removerItemDoCarrinho() {
        int row = tabelaCarrinho.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela do carrinho para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        pedidoAtual.removerItem(row);
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modeloCarrinho.setRowCount(0);
        for (ItemPedido item : pedidoAtual.getItens()) {
            modeloCarrinho.addRow(new Object[]{
                    item.getNumeroItem(),
                    item.getProduto().getCodigo(),
                    item.getProduto().getNome(),
                    String.format("%.2f", item.getPrecoUnitario()),
                    item.getQuantidade(),
                    String.format("%.2f", item.getSubtotal())
            });
        }
        recalcularTotais();
    }

    private double lerDescontoManualValidado() {
        String texto = txtDesconto.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            return 0.0;
        }
        double valor = Double.parseDouble(texto);
        if (valor < 0.0) {
            throw new IllegalArgumentException("O desconto não pode ser negativo.");
        }
        return valor;
    }

    private void aplicarDescontoManual() {
        try {
            lerDescontoManualValidado();
            recalcularTotais();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um valor numérico válido para o desconto.", "Desconto Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Desconto Inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalcularTotais() {
        FormaPagamento fp = (FormaPagamento) cbFormaPagamento.getSelectedItem();
        pedidoAtual.setFormaPagamento(fp);

        double descManual = 0.0;
        try {
            descManual = Math.max(0.0, lerDescontoManualValidado());
        } catch (Exception ignored) {}

        pedidoAtual.setValorDesconto(descManual);

        lblSubtotalBruto.setText(String.format("Subtotal Bruto: R$ %.2f", pedidoAtual.getSubtotalBruto()));
        lblValorDesconto.setText(String.format("Desconto Total: R$ %.2f", pedidoAtual.getDescontoEfetivo()));
        lblTotalLiquido.setText(String.format("VALOR FINAL: R$ %.2f", pedidoAtual.getTotalComDesconto()));
    }

    private void concluirVenda() {
        try {
            Cliente cli = (Cliente) cbClientes.getSelectedItem();
            if (cli == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente válido.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double descManual = lerDescontoManualValidado();
            pedidoAtual.setCliente(cli);
            pedidoAtual.setFormaPagamento((FormaPagamento) cbFormaPagamento.getSelectedItem());
            pedidoAtual.setValorDesconto(descManual);
            pedidoAtual.setObservacao(txtObservacao.getText().trim());
            recalcularTotais();

            // Executa a transação de negócio (debita estoque e persiste)
            Pedido pedidoFinalizado = vendaService.realizarVenda(pedidoAtual);

            // Emissão do cupom/comprovante
            String cupom = vendaService.gerarComprovanteVenda(pedidoFinalizado);

            JTextArea areaCupom = new JTextArea(cupom);
            areaCupom.setFont(new Font("Monospaced", Font.PLAIN, 12));
            areaCupom.setEditable(false);
            JScrollPane scrollCupom = new JScrollPane(areaCupom);
            scrollCupom.setPreferredSize(new Dimension(460, 360));

            JOptionPane.showMessageDialog(this, scrollCupom, "Venda Realizada com Sucesso! Cupom Fiscal", JOptionPane.INFORMATION_MESSAGE);

            // Atualiza tela principal e reinicia formulário
            if (telaPrincipal != null) {
                telaPrincipal.atualizarDashboard();
            }
            resetarFormulario();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Informe um valor numérico válido para o desconto.", "Desconto Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na operação de negócio: " + ex.getMessage(), "Falha na Venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetarFormulario() {
        this.pedidoAtual = new Pedido();
        modeloCarrinho.setRowCount(0);
        txtDesconto.setText("0.00");
        txtObservacao.setText("");
        spQuantidade.setValue(1);
        carregarCombos();
        recalcularTotais();
    }
}
