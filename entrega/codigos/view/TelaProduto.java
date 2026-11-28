package br.com.sisvendas.view;

import br.com.sisvendas.model.Produto;
import br.com.sisvendas.service.ProdutoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Interface gráfica para o CRUD completo da classe Produto.
 * Operações: Inclusão, Consulta/Filtro, Alteração e Exclusão.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class TelaProduto extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient ProdutoService produtoService = new ProdutoService();
    private final TelaPrincipal telaPrincipal;

    private JTextField txtId;
    private JTextField txtCodigo;
    private JTextField txtNome;
    private JComboBox<String> cbCategoria;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JTextField txtEstoqueMinimo;
    private JTextField txtPesquisa;

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    public TelaProduto(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        initComponents();
        carregarTabelaProdutos();
    }

    private void initComponents() {
        setTitle("Cadastro e Gerenciamento de Produtos (CRUD)");
        setSize(900, 620);
        setLocationRelativeTo(telaPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(contentPane);

        // Formulário de Cadastro (Inclusão e Alteração)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(6);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtCodigo = new JTextField(12);
        txtNome = new JTextField(25);

        String[] categorias = {"Informática", "Periféricos", "Monitores", "Áudio", "Hardware", "Mobiliário", "Acessórios", "Outros"};
        cbCategoria = new JComboBox<>(categorias);

        txtPreco = new JTextField(10);
        txtEstoque = new JTextField(8);
        txtEstoqueMinimo = new JTextField("5", 6);

        // Linha 0: ID e Código de Barras
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtId, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("Código / SKU:"), gbc);
        gbc.gridx = 3; pnlForm.add(txtCodigo, gbc);

        // Linha 1: Descrição e Categoria
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Descrição / Nome:*"), gbc);
        gbc.gridx = 1; pnlForm.add(txtNome, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 3; pnlForm.add(cbCategoria, gbc);

        // Linha 2: Preço e Estoque
        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Preço Unit. (R$):*"), gbc);
        gbc.gridx = 1; pnlForm.add(txtPreco, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("Qtd. Estoque:*"), gbc);
        gbc.gridx = 3; pnlForm.add(txtEstoque, gbc);

        // Linha 3: Estoque Mínimo
        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(new JLabel("Estoque Mínimo:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtEstoqueMinimo, gbc);

        // Barra de Botões do CRUD
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> limparFormulario());

        btnSalvar = new JButton("Salvar (Incluir/Alterar)");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalvar.setForeground(new Color(0, 102, 0));
        btnSalvar.addActionListener(e -> salvarProduto());

        btnExcluir = new JButton("Excluir");
        btnExcluir.setForeground(Color.RED);
        btnExcluir.addActionListener(e -> excluirProduto());

        btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());

        pnlBotoes.add(btnNovo);
        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnExcluir);
        pnlBotoes.add(btnLimpar);

        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.add(pnlForm, BorderLayout.CENTER);
        pnlNorte.add(pnlBotoes, BorderLayout.SOUTH);
        contentPane.add(pnlNorte, BorderLayout.NORTH);

        // Consulta e Tabela (Simulação de Banco de Dados)
        JPanel pnlConsulta = new JPanel(new BorderLayout(5, 5));
        pnlConsulta.setBorder(BorderFactory.createTitledBorder("Catálogo de Produtos na Base de Dados"));

        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        txtPesquisa = new JTextField(20);
        txtPesquisa.addActionListener(e -> filtrarProdutos());
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.addActionListener(e -> filtrarProdutos());
        JButton btnListarTodos = new JButton("Ver Todos");
        btnListarTodos.addActionListener(e -> {
            txtPesquisa.setText("");
            carregarTabelaProdutos();
        });

        pnlFiltro.add(new JLabel("Pesquisar por Nome/Código:"));
        pnlFiltro.add(txtPesquisa);
        pnlFiltro.add(btnBuscar);
        pnlFiltro.add(btnListarTodos);

        String[] colunas = {"ID", "Código", "Descrição", "Categoria", "Preço (R$)", "Estoque", "Situação"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setRowHeight(22);
        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaProdutos.getSelectedRow() != -1) {
                carregarProdutoSelecionado();
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        pnlConsulta.add(pnlFiltro, BorderLayout.NORTH);
        pnlConsulta.add(scrollTabela, BorderLayout.CENTER);

        contentPane.add(pnlConsulta, BorderLayout.CENTER);
    }

    private void carregarTabelaProdutos() {
        modeloTabela.setRowCount(0);
        List<Produto> produtos = produtoService.listarTodos();
        for (Produto p : produtos) {
            String situacao = p.isEstoqueBaixo() ? "ESTOQUE BAIXO" : "NORMAL";
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNome(),
                    p.getCategoria(),
                    String.format("%.2f", p.getPrecoUnitario()),
                    p.getQuantidadeEstoque(),
                    situacao
            });
        }
        if (telaPrincipal != null) {
            telaPrincipal.atualizarDashboard();
        }
    }

    private void filtrarProdutos() {
        String termo = txtPesquisa.getText().trim();
        List<Produto> produtos = produtoService.buscarPorNomeOuCodigo(termo);
        modeloTabela.setRowCount(0);
        for (Produto p : produtos) {
            String situacao = p.isEstoqueBaixo() ? "ESTOQUE BAIXO" : "NORMAL";
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNome(),
                    p.getCategoria(),
                    String.format("%.2f", p.getPrecoUnitario()),
                    p.getQuantidadeEstoque(),
                    situacao
            });
        }
    }

    private void carregarProdutoSelecionado() {
        int row = tabelaProdutos.getSelectedRow();
        if (row >= 0) {
            Integer id = (Integer) modeloTabela.getValueAt(row, 0);
            Produto p = produtoService.buscarPorId(id);
            if (p != null) {
                txtId.setText(String.valueOf(p.getId()));
                txtCodigo.setText(p.getCodigo());
                txtNome.setText(p.getNome());
                cbCategoria.setSelectedItem(p.getCategoria());
                txtPreco.setText(String.format(java.util.Locale.US, "%.2f", p.getPrecoUnitario()));
                txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
                txtEstoqueMinimo.setText(String.valueOf(p.getEstoqueMinimo()));
            }
        }
    }

    private void salvarProduto() {
        try {
            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            double preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
            int estoque = Integer.parseInt(txtEstoque.getText().trim());
            int estoqueMinimo = txtEstoqueMinimo.getText().trim().isEmpty()
                    ? 5 : Integer.parseInt(txtEstoqueMinimo.getText().trim());

            Produto p = new Produto(
                    id,
                    txtCodigo.getText().trim(),
                    txtNome.getText().trim(),
                    (String) cbCategoria.getSelectedItem(),
                    preco,
                    estoque
            );
            p.setEstoqueMinimo(estoqueMinimo);

            produtoService.salvar(p);
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso na base de dados!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabelaProdutos();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Preço, Estoque e Estoque Mínimo devem conter valores numéricos válidos.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirProduto() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o produto selecionado?",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                Integer id = Integer.parseInt(txtId.getText());
                produtoService.excluir(id);
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Excluído", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabelaProdutos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtCodigo.setText("");
        txtNome.setText("");
        cbCategoria.setSelectedIndex(0);
        txtPreco.setText("");
        txtEstoque.setText("");
        txtEstoqueMinimo.setText("5");
        tabelaProdutos.clearSelection();
        txtNome.requestFocus();
    }
}
