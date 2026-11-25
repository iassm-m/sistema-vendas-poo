package br.com.sisvendas.view;

import br.com.sisvendas.service.ClienteService;
import br.com.sisvendas.service.ProdutoService;
import br.com.sisvendas.service.VendaService;
import br.com.sisvendas.model.Pedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela Principal do SisVendas (Menu e Dashboard).
 * Interface gráfica desenvolvida com Java Swing.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class TelaPrincipal extends JFrame {

    private final ClienteService clienteService = new ClienteService();
    private final ProdutoService produtoService = new ProdutoService();
    private final VendaService vendaService = new VendaService();

    private JLabel lblTotalClientes;
    private JLabel lblTotalProdutos;
    private JLabel lblTotalVendas;
    private JLabel lblFaturamento;
    private JTable tabelaUltimasVendas;
    private DefaultTableModel modeloTabela;

    public TelaPrincipal() {
        initComponents();
        atualizarDashboard();
    }

    private void initComponents() {
        setTitle("SisVendas - Sistema de Informação e Gestão Comercial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        // Barra de Menus
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemClientes = new JMenuItem("Gerenciar Clientes (CRUD)");
        itemClientes.addActionListener(e -> abrirTelaClientes());
        JMenuItem itemProdutos = new JMenuItem("Gerenciar Produtos (CRUD)");
        itemProdutos.addActionListener(e -> abrirTelaProdutos());
        JMenuItem itemSair = new JMenuItem("Sair do Sistema");
        itemSair.addActionListener(e -> System.exit(0));

        menuCadastros.add(itemClientes);
        menuCadastros.add(itemProdutos);
        menuCadastros.addSeparator();
        menuCadastros.add(itemSair);

        JMenu menuVendas = new JMenu("Vendas");
        JMenuItem itemNovaVenda = new JMenuItem("Realizar Venda (Tela de Negócio)");
        itemNovaVenda.addActionListener(e -> abrirTelaVenda());
        JMenuItem itemConsultarVendas = new JMenuItem("Histórico e Consulta de Vendas");
        itemConsultarVendas.addActionListener(e -> abrirTelaConsultaVendas());

        menuVendas.add(itemNovaVenda);
        menuVendas.add(itemConsultarVendas);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre o Projeto ABNT");
        itemSobre.addActionListener(e -> exibirSobre());
        menuAjuda.add(itemSobre);

        menuBar.add(menuCadastros);
        menuBar.add(menuVendas);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);

        // Painel Principal
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // Cabeçalho / Banner Superior
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(24, 76, 120));
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("SisVendas - Sistema de Gestão Comercial");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Trabalho de POO - Grupo: Iasmim José, Rebeca Lima, Sarah Silva, Silvana Silva");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(220, 230, 242));

        pnlHeader.add(lblTitulo, BorderLayout.NORTH);
        pnlHeader.add(lblSubtitulo, BorderLayout.SOUTH);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // Barra de Ações Rápidas (Toolbar)
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(new EmptyBorder(5, 0, 10, 0));

        JButton btnNavClientes = new JButton("👤 Clientes");
        btnNavClientes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNavClientes.addActionListener(e -> abrirTelaClientes());

        JButton btnNavProdutos = new JButton("📦 Produtos");
        btnNavProdutos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNavProdutos.addActionListener(e -> abrirTelaProdutos());

        JButton btnNavVenda = new JButton("🛒 Nova Venda (Negócio)");
        btnNavVenda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNavVenda.setForeground(new Color(0, 102, 0));
        btnNavVenda.addActionListener(e -> abrirTelaVenda());

        JButton btnNavHistorico = new JButton("📋 Histórico Vendas");
        btnNavHistorico.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNavHistorico.addActionListener(e -> abrirTelaConsultaVendas());

        JButton btnAtualizar = new JButton("🔄 Atualizar");
        btnAtualizar.addActionListener(e -> atualizarDashboard());

        toolBar.add(btnNavClientes);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(btnNavProdutos);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(btnNavVenda);
        toolBar.add(Box.createHorizontalStrut(8));
        toolBar.add(btnNavHistorico);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(btnAtualizar);

        // Painel Central: Indicadores (KPIs) + Tabela de Vendas Recentes
        JPanel pnlCentro = new JPanel(new BorderLayout(15, 15));
        pnlCentro.add(toolBar, BorderLayout.NORTH);

        // Cartões de Métricas (Dashboard)
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 15, 0));

        lblTotalClientes = new JLabel("0", SwingConstants.CENTER);
        lblTotalProdutos = new JLabel("0", SwingConstants.CENTER);
        lblTotalVendas = new JLabel("0", SwingConstants.CENTER);
        lblFaturamento = new JLabel("R$ 0,00", SwingConstants.CENTER);

        pnlCards.add(criarCardMetrica("CLIENTES CADASTRADOS", lblTotalClientes, new Color(41, 128, 185)));
        pnlCards.add(criarCardMetrica("PRODUTOS NO CATÁLOGO", lblTotalProdutos, new Color(39, 174, 96)));
        pnlCards.add(criarCardMetrica("VENDAS REALIZADAS", lblTotalVendas, new Color(142, 68, 173)));
        pnlCards.add(criarCardMetrica("FATURAMENTO TOTAL", lblFaturamento, new Color(211, 84, 0)));

        // Tabela de Vendas Recentes
        JPanel pnlTabelaWrapper = new JPanel(new BorderLayout(5, 5));
        pnlTabelaWrapper.setBorder(BorderFactory.createTitledBorder("Últimas Vendas Registradas na Base de Dados"));

        String[] colunas = {"Código", "Cliente", "Data da Venda", "Qtd Itens", "Forma Pagamento", "Total Líquido", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaUltimasVendas = new JTable(modeloTabela);
        tabelaUltimasVendas.setRowHeight(24);
        tabelaUltimasVendas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollTabela = new JScrollPane(tabelaUltimasVendas);

        pnlTabelaWrapper.add(pnlCards, BorderLayout.NORTH);
        pnlTabelaWrapper.add(scrollTabela, BorderLayout.CENTER);

        pnlCentro.add(pnlTabelaWrapper, BorderLayout.CENTER);
        contentPane.add(pnlCentro, BorderLayout.CENTER);

        // Rodapé
        JPanel pnlRodape = new JPanel(new BorderLayout());
        pnlRodape.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel lblStatusBD = new JLabel("Base de dados simulada: ATIVA (Persistência em memória e CSV) | Padrão POO");
        lblStatusBD.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatusBD.setForeground(Color.DARK_GRAY);
        pnlRodape.add(lblStatusBD, BorderLayout.WEST);
        contentPane.add(pnlRodape, BorderLayout.SOUTH);
    }

    private JPanel criarCardMetrica(String titulo, JLabel lblValor, Color corBorda) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(Color.GRAY);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(corBorda);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    public void atualizarDashboard() {
        int totalCli = clienteService.listarTodos().size();
        int totalProd = produtoService.listarTodos().size();
        List<Pedido> pedidos = vendaService.listarTodas();

        double faturamento = 0.0;
        int vendasConcluidas = 0;

        modeloTabela.setRowCount(0);
        for (Pedido p : pedidos) {
            if (p.getStatus() == br.com.sisvendas.model.StatusPedido.FINALIZADO) {
                faturamento += p.getTotalComDesconto();
                vendasConcluidas++;
            }
            modeloTabela.addRow(new Object[]{
                    p.getCodigoPedido(),
                    p.getCliente() != null ? p.getCliente().getNome() : "N/D",
                    p.getDataVendaFormatada(),
                    p.getItens().size(),
                    p.getFormaPagamento() != null ? p.getFormaPagamento().getDescricao() : "N/D",
                    String.format("R$ %.2f", p.getTotalComDesconto()),
                    p.getStatus() != null ? p.getStatus().getDescricao() : "N/D"
            });
        }

        lblTotalClientes.setText(String.valueOf(totalCli));
        lblTotalProdutos.setText(String.valueOf(totalProd));
        lblTotalVendas.setText(String.valueOf(vendasConcluidas));
        lblFaturamento.setText(String.format("R$ %.2f", faturamento));
    }

    private void abrirTelaClientes() {
        new TelaCliente(this).setVisible(true);
    }

    private void abrirTelaProdutos() {
        new TelaProduto(this).setVisible(true);
    }

    private void abrirTelaVenda() {
        new TelaVenda(this).setVisible(true);
    }

    private void abrirTelaConsultaVendas() {
        new TelaConsultaVendas(this).setVisible(true);
    }

    private void exibirSobre() {
        String msg = "SisVendas - Sistema de Informação Comercial\n" +
                "Desenvolvido para a Disciplina de Programação Orientada a Objetos\n\n" +
                "Grupo:\n - IASMIM DA SILVA MANN JOSÉ (1250109566)\n - REBECA MACHADO DE LIMA (1250103657)\n - SARAH GABRIELLA VIRGÍNIA DA SILVA (1250106978)\n - SILVANA GOMES DA SILVA (1250109767)\n\n" +
                "Requisitos Atendidos:\n" +
                " - Mini-mundo de Gestão de Vendas com 3 classes de domínio (Cliente, Produto, Pedido)\n" +
                " - CRUD Completo para todas as classes\n" +
                " - Tela de Negócio (Realização de Venda com baixa em estoque e cupom)\n" +
                " - Base de dados simulada com persistência em tabelas\n" +
                " - Interface Gráfica Swing com arquivos .frm/.form\n" +
                " - Relatório ABNT em PDF incluído na documentação";
        JOptionPane.showMessageDialog(this, msg, "Sobre o Projeto", JOptionPane.INFORMATION_MESSAGE);
    }
}
