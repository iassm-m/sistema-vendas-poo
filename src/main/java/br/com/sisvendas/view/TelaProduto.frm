# ==============================================================================
# SisVendas - Definição do Formulário de Interface Gráfica (.frm)
# Formulário: TelaProduto.frm (CRUD Completo de Produtos)
# Grupo: IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
# Disciplina: Programação Orientada a Objetos
# ==============================================================================

[FormDefinition]
Name=TelaProduto
Title=Cadastro e Gerenciamento de Produtos (CRUD)
Type=javax.swing.JFrame
Width=900
Height=620
DefaultCloseOperation=DISPOSE_ON_CLOSE
Layout=BorderLayout

[FormFields]
Field.1=txtId (Type=JTextField, Editable=false, Label="ID")
Field.2=txtCodigo (Type=JTextField, Label="Código / SKU")
Field.3=txtNome (Type=JTextField, Required=true, Label="Descrição / Nome")
Field.4=cbCategoria (Type=JComboBox, Options=["Informática", "Periféricos", "Monitores", "Áudio", "Hardware", "Mobiliário", "Acessórios", "Outros"])
Field.5=txtPreco (Type=JTextField, Required=true, Label="Preço Unit. (R$)")
Field.6=txtEstoque (Type=JTextField, Required=true, Label="Qtd. Estoque")
Field.7=txtEstoqueMinimo (Type=JTextField, Default="5", Label="Estoque Mínimo")

[ButtonsCRUD]
Button.1=btnNovo (Text="Novo", Action=limparFormulario)
Button.2=btnSalvar (Text="Salvar (Incluir/Alterar)", Action=salvarProduto)
Button.3=btnExcluir (Text="Excluir", Action=excluirProduto)
Button.4=btnLimpar (Text="Limpar", Action=limparFormulario)

[TableDatabaseSimulation]
Table=tabelaProdutos (Type=JTable, DataSource=BancoDadosMemoria.TB_PRODUTOS)
Columns=[ID, Código, Descrição, Categoria, Preço (R$), Estoque, Situação]
SelectionMode=SINGLE_SELECTION
SearchField=txtPesquisa (Action=filtrarProdutos)
