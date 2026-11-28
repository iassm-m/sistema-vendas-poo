# ==============================================================================
# SisVendas - Definição do Formulário de Interface Gráfica (.frm)
# Formulário: TelaCliente.frm (CRUD Completo de Clientes)
# Grupo: IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
# Disciplina: Programação Orientada a Objetos
# ==============================================================================

[FormDefinition]
Name=TelaCliente
Title=Cadastro e Gerenciamento de Clientes (CRUD)
Type=javax.swing.JFrame
Width=850
Height=600
DefaultCloseOperation=DISPOSE_ON_CLOSE
Layout=BorderLayout

[FormFields]
Field.1=txtId (Type=JTextField, Editable=false, Label="Código (ID)")
Field.2=txtNome (Type=JTextField, Required=true, Label="Nome Completo")
Field.3=txtCpf (Type=JTextField, Required=true, Label="CPF (11 dígitos)")
Field.4=txtEmail (Type=JTextField, Label="E-mail")
Field.5=txtTelefone (Type=JTextField, Label="Telefone / WhatsApp")
Field.6=txtLogradouro (Type=JTextField, Label="Endereço / Rua")
Field.7=txtCidade (Type=JTextField, Label="Cidade")

[ButtonsCRUD]
Button.1=btnNovo (Text="Novo", Action=limparFormulario)
Button.2=btnSalvar (Text="Salvar (Incluir/Alterar)", Action=salvarCliente)
Button.3=btnExcluir (Text="Excluir", Action=excluirCliente)
Button.4=btnLimpar (Text="Limpar", Action=limparFormulario)

[TableDatabaseSimulation]
Table=tabelaClientes (Type=JTable, DataSource=BancoDadosMemoria.TB_CLIENTES)
Columns=[ID, Nome, CPF, Telefone, E-mail, Cidade]
SelectionMode=SINGLE_SELECTION
SearchField=txtPesquisa (Action=filtrarClientes)
