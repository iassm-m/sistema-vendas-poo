package br.com.sisvendas.view;

import br.com.sisvendas.model.Cliente;
import br.com.sisvendas.service.ClienteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Interface gráfica para o CRUD completo da classe Cliente.
 * Operações: Inclusão, Consulta/Filtro, Alteração e Exclusão.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class TelaCliente extends JFrame {

    private final ClienteService clienteService = new ClienteService();
    private final TelaPrincipal telaPrincipal;

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtLogradouro;
    private JTextField txtCidade;
    private JTextField txtPesquisa;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    public TelaCliente(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        initComponents();
        carregarTabelaClientes();
    }

    private void initComponents() {
        setTitle("Cadastro e Gerenciamento de Clientes (CRUD)");
        setSize(850, 600);
        setLocationRelativeTo(telaPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(contentPane);

        // Formulário de Cadastro (Inclusão e Alteração)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(6);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtNome = new JTextField(25);
        txtCpf = new JTextField(15);
        txtEmail = new JTextField(20);
        txtTelefone = new JTextField(15);
        txtLogradouro = new JTextField(25);
        txtCidade = new JTextField(15);

        // Linha 0: ID e CPF
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Código (ID):"), gbc);
        gbc.gridx = 1; pnlForm.add(txtId, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("CPF (11 dígitos):*"), gbc);
        gbc.gridx = 3; pnlForm.add(txtCpf, gbc);

        // Linha 1: Nome e Telefone
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Nome Completo:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1; pnlForm.add(txtNome, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("Telefone / WhatsApp:"), gbc);
        gbc.gridx = 3; pnlForm.add(txtTelefone, gbc);

        // Linha 2: E-mail e Cidade
        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtEmail, gbc);
        gbc.gridx = 2; pnlForm.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 3; pnlForm.add(txtCidade, gbc);

        // Linha 3: Logradouro / Endereço
        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(new JLabel("Endereço / Rua:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; pnlForm.add(txtLogradouro, gbc);
        gbc.gridwidth = 1;

        // Barra de Botões do CRUD
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> limparFormulario());

        btnSalvar = new JButton("Salvar (Incluir/Alterar)");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalvar.setForeground(new Color(0, 102, 0));
        btnSalvar.addActionListener(e -> salvarCliente());

        btnExcluir = new JButton("Excluir");
        btnExcluir.setForeground(Color.RED);
        btnExcluir.addActionListener(e -> excluirCliente());

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

        // Painel de Consulta e Tabela (Tabela simulando Banco de Dados)
        JPanel pnlConsulta = new JPanel(new BorderLayout(5, 5));
        pnlConsulta.setBorder(BorderFactory.createTitledBorder("Consulta de Clientes na Base de Dados"));

        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        txtPesquisa = new JTextField(20);
        JButton btnBuscar = new JButton("🔍 Buscar por Nome");
        btnBuscar.addActionListener(e -> filtrarClientes());
        JButton btnListarTodos = new JButton("Ver Todos");
        btnListarTodos.addActionListener(e -> carregarTabelaClientes());

        pnlFiltro.add(new JLabel("Pesquisar:"));
        pnlFiltro.add(txtPesquisa);
        pnlFiltro.add(btnBuscar);
        pnlFiltro.add(btnListarTodos);

        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "E-mail", "Cidade"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.setRowHeight(22);
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                carregarClienteSelecionado();
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tabelaClientes);
        pnlConsulta.add(pnlFiltro, BorderLayout.NORTH);
        pnlConsulta.add(scrollTabela, BorderLayout.CENTER);

        contentPane.add(pnlConsulta, BorderLayout.CENTER);
    }

    private void carregarTabelaClientes() {
        modeloTabela.setRowCount(0);
        List<Cliente> clientes = clienteService.listarTodos();
        for (Cliente c : clientes) {
            modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    c.getCpf(),
                    c.getTelefone(),
                    c.getEmail(),
                    c.getCidade()
            });
        }
        if (telaPrincipal != null) {
            telaPrincipal.atualizarDashboard();
        }
    }

    private void filtrarClientes() {
        String termo = txtPesquisa.getText().trim();
        List<Cliente> clientes = clienteService.buscarPorNome(termo);
        modeloTabela.setRowCount(0);
        for (Cliente c : clientes) {
            modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    c.getCpf(),
                    c.getTelefone(),
                    c.getEmail(),
                    c.getCidade()
            });
        }
    }

    private void carregarClienteSelecionado() {
        int row = tabelaClientes.getSelectedRow();
        if (row >= 0) {
            Integer id = (Integer) modeloTabela.getValueAt(row, 0);
            Cliente c = clienteService.buscarPorId(id);
            if (c != null) {
                txtId.setText(String.valueOf(c.getId()));
                txtNome.setText(c.getNome());
                txtCpf.setText(c.getCpf());
                txtEmail.setText(c.getEmail());
                txtTelefone.setText(c.getTelefone());
                txtLogradouro.setText(c.getLogradouro());
                txtCidade.setText(c.getCidade());
            }
        }
    }

    private void salvarCliente() {
        try {
            Integer id = txtId.getText().isEmpty() ? null : Integer.parseInt(txtId.getText());
            Cliente c = new Cliente(
                    id,
                    txtNome.getText().trim(),
                    txtCpf.getText().trim(),
                    txtEmail.getText().trim(),
                    txtTelefone.getText().trim(),
                    txtLogradouro.getText().trim(),
                    txtCidade.getText().trim()
            );

            clienteService.salvar(c);
            JOptionPane.showMessageDialog(this, "Cliente gravado com sucesso na base de dados!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabelaClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCliente() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente selecionado?",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                Integer id = Integer.parseInt(txtId.getText());
                clienteService.excluir(id);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!", "Excluído", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabelaClientes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtLogradouro.setText("");
        txtCidade.setText("");
        tabelaClientes.clearSelection();
        txtNome.requestFocus();
    }
}
