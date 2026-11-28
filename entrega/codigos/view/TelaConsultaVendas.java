package br.com.sisvendas.view;

import br.com.sisvendas.model.FormaPagamento;
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
 * Interface gráfica para Gerenciamento, Consulta, Alteração, Cancelamento e Exclusão de Vendas/Pedidos.
 * Completa o CRUD da 3ª classe de domínio (Pedido) em conjunto com a TelaVenda (Inclusão).
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class TelaConsultaVendas extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient VendaService vendaService = new VendaService();
    private final TelaPrincipal telaPrincipal;

    private JTable tabelaVendas;
    private DefaultTableModel modeloVendas;
    private JTable tabelaItens;
    private DefaultTableModel modeloItens;

    private JComboBox<String> cbFiltroStatus;
    private JButton btnAlterarVenda;
    private JButton btnCancelarVenda;
    private JButton btnExcluirVenda;
    private JButton btnVerCupom;

    public TelaConsultaVendas(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        initComponents();
        carregarTabelaVendas();
    }

    private void initComponents() {
        setTitle("Consulta e Gerenciamento de Vendas / Pedidos (CRUD)");
        setSize(960, 640);
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

        JButton btnRecarregar = new JButton("🔄 Recarregar Todos");
        btnRecarregar.addActionListener(e -> {
            cbFiltroStatus.setSelectedItem("TODOS");
            carregarTabelaVendas();
        });

        pnlFiltro.add(new JLabel("Status da Venda:"));
        pnlFiltro.add(cbFiltroStatus);
        pnlFiltro.add(btnRecarregar);

        contentPane.add(pnlFiltro, BorderLayout.NORTH);

        // Divisão: Vendas (Cima) e Itens da Venda Selecionada (Baixo)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Tabela Superior: Pedidos
        String[] colunasVendas = {"ID", "Código", "Cliente", "Data da Venda", "Forma Pagamento", "Desconto (R$)", "Total (R$)", "Status", "Observação"};
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
        scrollVendas.setBorder(BorderFactory.createTitledBorder("Lista de Vendas Registradas na Base de Dados"));
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

        // Painel Inferior de Ações (CRUD completo de Pedido)
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));

        btnVerCupom = new JButton("📄 Reemitir Cupom");
        btnVerCupom.addActionListener(e -> exibirCupom());

        btnAlterarVenda = new JButton("✏️ Alterar Pedido");
        btnAlterarVenda.addActionListener(e -> alterarVendaSelecionada());

        btnCancelarVenda = new JButton("⚠️ Cancelar Venda (Estornar)");
        btnCancelarVenda.setForeground(new Color(180, 90, 0));
        btnCancelarVenda.addActionListener(e -> cancelarVendaSelecionada());

        btnExcluirVenda = new JButton("🗑️ Excluir Pedido");
        btnExcluirVenda.setForeground(Color.RED);
        btnExcluirVenda.addActionListener(e -> excluirVendaSelecionada());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        pnlAcoes.add(btnVerCupom);
        pnlAcoes.add(btnAlterarVenda);
        pnlAcoes.add(btnCancelarVenda);
        pnlAcoes.add(btnExcluirVenda);
        pnlAcoes.add(btnFechar);

        contentPane.add(pnlAcoes, BorderLayout.SOUTH);
    }

    private void preencherTabelaVendas(List<Pedido> lista) {
        modeloVendas.setRowCount(0);
        modeloItens.setRowCount(0);
        for (Pedido p : lista) {
            modeloVendas.addRow(new Object[]{
                    p.getId(),
                    p.getCodigoPedido(),
                    p.getCliente() != null ? p.getCliente().getNome() : "N/D",
                    p.getDataVendaFormatada(),
                    p.getFormaPagamento() != null ? p.getFormaPagamento().getDescricao() : "N/D",
                    String.format("%.2f", p.getDescontoEfetivo()),
                    String.format("%.2f", p.getTotalComDesconto()),
                    p.getStatus() != null ? p.getStatus().getDescricao() : "N/D",
                    p.getObservacao() != null ? p.getObservacao() : ""
            });
        }
    }

    private void carregarTabelaVendas() {
        preencherTabelaVendas(vendaService.listarTodas());
    }

    private void filtrarVendas() {
        String selecionado = (String) cbFiltroStatus.getSelectedItem();
        if (selecionado == null || "TODOS".equals(selecionado)) {
            carregarTabelaVendas();
            return;
        }
        StatusPedido status = StatusPedido.valueOf(selecionado);
        preencherTabelaVendas(vendaService.listarPorStatus(status));
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
            JScrollPane scroll = new JScrollPane(txt);
            scroll.setPreferredSize(new Dimension(460, 360));
            JOptionPane.showMessageDialog(this, scroll, "Cupom do Pedido " + p.getCodigoPedido(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void alterarVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para alterar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) modeloVendas.getValueAt(row, 0);
        Pedido p = vendaService.buscarPorId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Pedido não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean cancelado = p.getStatus() == StatusPedido.CANCELADO;

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<FormaPagamento> cbPagto = new JComboBox<>(FormaPagamento.values());
        cbPagto.setSelectedItem(p.getFormaPagamento());
        cbPagto.setEnabled(!cancelado);

        JTextField txtDesc = new JTextField(String.format(java.util.Locale.US, "%.2f", p.getValorDesconto()), 10);
        txtDesc.setEnabled(!cancelado);

        JTextField txtObs = new JTextField(p.getObservacao() != null ? p.getObservacao() : "", 22);

        gbc.gridx = 0; gbc.gridy = 0; pnl.add(new JLabel("Pedido:"), gbc);
        gbc.gridx = 1; pnl.add(new JLabel(p.getCodigoPedido() + " (" + p.getStatus().getDescricao() + ")"), gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnl.add(new JLabel("Forma de Pagamento:"), gbc);
        gbc.gridx = 1; pnl.add(cbPagto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnl.add(new JLabel("Desconto Manual (R$):"), gbc);
        gbc.gridx = 1; pnl.add(txtDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnl.add(new JLabel("Observação:"), gbc);
        gbc.gridx = 1; pnl.add(txtObs, gbc);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Alterar Pedido " + p.getCodigoPedido(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            try {
                if (!cancelado) {
                    String descStr = txtDesc.getText().trim().replace(",", ".");
                    double novoDesconto = descStr.isEmpty() ? 0.0 : Double.parseDouble(descStr);
                    if (novoDesconto < 0.0) {
                        throw new IllegalArgumentException("O desconto não pode ser negativo.");
                    }
                    p.setFormaPagamento((FormaPagamento) cbPagto.getSelectedItem());
                    p.setValorDesconto(novoDesconto);
                }
                p.setObservacao(txtObs.getText().trim());
                vendaService.atualizar(p);

                JOptionPane.showMessageDialog(this, "Pedido alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                filtrarVendas();
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarDashboard();
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Informe um valor numérico válido para o desconto.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao alterar pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
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
                filtrarVendas();
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarDashboard();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) modeloVendas.getValueAt(row, 0);
        int conf = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir permanentemente este pedido?\nSe o pedido estiver FINALIZADO, os itens serão estornados ao estoque automaticamente.",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (conf == JOptionPane.YES_OPTION) {
            try {
                vendaService.excluir(id);
                JOptionPane.showMessageDialog(this, "Pedido excluído com sucesso!", "Excluído", JOptionPane.INFORMATION_MESSAGE);
                filtrarVendas();
                if (telaPrincipal != null) {
                    telaPrincipal.atualizarDashboard();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
