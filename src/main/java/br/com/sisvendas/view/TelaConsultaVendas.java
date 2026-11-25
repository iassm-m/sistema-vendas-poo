package br.com.sisvendas.view;

import br.com.sisvendas.model.ItemPedido;
import br.com.sisvendas.model.Pedido;
import br.com.sisvendas.model.StatusPedido;
import br.com.sisvendas.service.VendaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Interface gráfica para Consulta, Auditoria e Cancelamento de Vendas.
 * 
 * @author Iasmim José
 */
public class TelaConsultaVendas extends JFrame {

    private final VendaService vendaService = new VendaService();
    private final TelaPrincipal telaPrincipal;

    private JTable tabelaVendas;
    private DefaultTableModel modeloVendas;
    private JTable tabelaItens;
    private DefaultTableModel modeloItens;

    private JComboBox<String> cbFiltroStatus;
    private JButton btnCancelarVenda;
    private JButton btnVerCupom;

    public TelaConsultaVendas(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        initComponents();
        carregarTabelaVendas();
    }

    private void initComponents() {
        setTitle("Consulta e Histórico de Vendas");
        setSize(920, 620);
        setLocationRelativeTo(telaPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(contentPane);

        // Barra de Filtro
        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar Vendas"));

        String[] statusOpcoes = {"TODOS", "FINALIZADO", "CANCELADO", "PENDENTE"};
        cbFiltroStatus = new JComboBox<>(statusOpcoes);
        cbFiltroStatus.addActionListener(e -> filtrarVendas());

        JButton btnRecarregar = new JButton("🔄 Recarregar");
        btnRecarregar.addActionListener(e -> carregarTabelaVendas());

        pnlFiltro.add(new JLabel("Status da Venda:"));
        pnlFiltro.add(cbFiltroStatus);
        pnlFiltro.add(btnRecarregar);

        contentPane.add(pnlFiltro, BorderLayout.NORTH);

        // Divisão: Vendas (Cima) e Itens da Venda Selecionada (Baixo)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Tabela Superior: Pedidos
        String[] colunasVendas = {"ID", "Código", "Cliente", "Data da Venda", "Forma Pagamento", "Total (R$)", "Status"};
        modeloVendas = new DefaultTableModel(colunasVendas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabelaVendas = new JTable(modeloVendas);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setRowHeight(22);
        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaVendas.getSelectedRow() != -1) {
                carregarItensDaVendaSelecionada();
            }
        });

        JScrollPane scrollVendas = new JScrollPane(tabelaVendas);
        scrollVendas.setBorder(BorderFactory.createTitledBorder("Lista de Vendas Registradas"));
        splitPane.setTopComponent(scrollVendas);

        // Tabela Inferior: Itens do Pedido Selecionado
        String[] colunasItens = {"Item #", "Produto", "Categoria", "Preço Unit. (R$)", "Qtd", "Subtotal (R$)"};
        modeloItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloItens);
        tabelaItens.setRowHeight(20);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setBorder(BorderFactory.createTitledBorder("Itens da Venda Selecionada"));
        splitPane.setBottomComponent(scrollItens);

        contentPane.add(splitPane, BorderLayout.CENTER);

        // Painel Inferior de Ações
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        btnVerCupom = new JButton("📄 Reemitir Cupom");
        btnVerCupom.addActionListener(e -> exibirCupom());

        btnCancelarVenda = new JButton("⚠️ Cancelar Venda (Estornar Estoque)");
        btnCancelarVenda.setForeground(Color.RED);
        btnCancelarVenda.addActionListener(e -> cancelarVendaSelecionada());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        pnlAcoes.add(btnVerCupom);
        pnlAcoes.add(btnCancelarVenda);
        pnlAcoes.add(btnFechar);

        contentPane.add(pnlAcoes, BorderLayout.SOUTH);
    }

    private void carregarTabelaVendas() {
        modeloVendas.setRowCount(0);
        modeloItens.setRowCount(0);
        List<Pedido> lista = vendaService.listarTodas();
        for (Pedido p : lista) {
            modeloVendas.addRow(new Object[]{
                    p.getId(),
                    p.getCodigoPedido(),
                    p.getCliente() != null ? p.getCliente().getNome() : "N/D",
                    p.getDataVendaFormatada(),
                    p.getFormaPagamento() != null ? p.getFormaPagamento().getDescricao() : "N/D",
                    String.format("%.2f", p.getTotalComDesconto()),
                    p.getStatus() != null ? p.getStatus().getDescricao() : "N/D"
            });
        }
    }

    private void filtrarVendas() {
        String selecionado = (String) cbFiltroStatus.getSelectedItem();
        if ("TODOS".equals(selecionado)) {
            carregarTabelaVendas();
            return;
        }

        StatusPedido status = StatusPedido.valueOf(selecionado);
        List<Pedido> lista = vendaService.listarPorStatus(status);
        modeloVendas.setRowCount(0);
        modeloItens.setRowCount(0);
        for (Pedido p : lista) {
            modeloVendas.addRow(new Object[]{
                    p.getId(),
                    p.getCodigoPedido(),
                    p.getCliente() != null ? p.getCliente().getNome() : "N/D",
                    p.getDataVendaFormatada(),
                    p.getFormaPagamento() != null ? p.getFormaPagamento().getDescricao() : "N/D",
                    String.format("%.2f", p.getTotalComDesconto()),
                    p.getStatus() != null ? p.getStatus().getDescricao() : "N/D"
            });
        }
    }

    private void carregarItensDaVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        modeloItens.setRowCount(0);
        if (row >= 0) {
            Integer id = (Integer) modeloVendas.getValueAt(row, 0);
            Pedido p = vendaService.buscarPorId(id);
            if (p != null) {
                for (ItemPedido item : p.getItens()) {
                    modeloItens.addRow(new Object[]{
                            item.getNumeroItem(),
                            item.getProduto() != null ? item.getProduto().getNome() : "N/D",
                            item.getProduto() != null ? item.getProduto().getCategoria() : "N/D",
                            String.format("%.2f", item.getPrecoUnitario()),
                            item.getQuantidade(),
                            String.format("%.2f", item.getSubtotal())
                    });
                }
            }
        }
    }

    private void exibirCupom() {
        int row = tabelaVendas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer id = (Integer) modeloVendas.getValueAt(row, 0);
        Pedido p = vendaService.buscarPorId(id);
        if (p != null) {
            String cupom = vendaService.gerarComprovanteVenda(p);
            JTextArea txt = new JTextArea(cupom);
            txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
            txt.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Cupom do Pedido " + p.getCodigoPedido(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancelarVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda para cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) modeloVendas.getValueAt(row, 0);
        int conf = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar esta venda?\nOs produtos serão estornados automaticamente para o estoque da base de dados.",
                "Confirmação de Cancelamento", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (conf == JOptionPane.YES_OPTION) {
            try {
                vendaService.cancelarVenda(id);
                JOptionPane.showMessageDialog(this, "Venda cancelada e estoque estornado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaVendas();
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarDashboard();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
