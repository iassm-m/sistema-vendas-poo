# ==============================================================================
# SisVendas - Definição do Formulário de Interface Gráfica (.frm)
# Formulário: TelaConsultaVendas.frm (Consulta, Alteração, Cancelamento e Exclusão de Pedidos)
# Grupo: IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
# Disciplina: Programação Orientada a Objetos
# ==============================================================================

[FormDefinition]
Name=TelaConsultaVendas
Title=Consulta e Gerenciamento de Vendas / Pedidos (CRUD)
Type=javax.swing.JFrame
Width=960
Height=640
DefaultCloseOperation=DISPOSE_ON_CLOSE
Layout=BorderLayout

[FilterBar]
Component.1=cbFiltroStatus (Type=JComboBox, Options=["TODOS", "FINALIZADO", "CANCELADO", "PENDENTE"], Action=filtrarVendas)
Component.2=btnRecarregar (Type=JButton, Text="🔄 Recarregar Todos", Action=carregarTabelaVendas)

[TableDatabaseSimulation]
Table.1=tabelaVendas (Type=JTable, DataSource=BancoDadosMemoria.TB_PEDIDOS)
Table.1.Columns=[ID, Código, Cliente, Data da Venda, Forma Pagamento, Desconto (R$), Total (R$), Status, Observação]
Table.2=tabelaItens (Type=JTable, DataSource=BancoDadosMemoria.TB_ITENS_PEDIDO)
Table.2.Columns=[Item #, Produto, Categoria, Preço Unit. (R$), Qtd, Subtotal (R$)]

[ButtonsCRUD]
Button.1=btnVerCupom (Text="📄 Reemitir Cupom", Action=exibirCupom)
Button.2=btnAlterarVenda (Text="✏️ Alterar Pedido", Action=alterarVendaSelecionada)
Button.3=btnCancelarVenda (Text="⚠️ Cancelar Venda (Estornar)", Action=cancelarVendaSelecionada)
Button.4=btnExcluirVenda (Text="🗑️ Excluir Pedido", Action=excluirVendaSelecionada)
Button.5=btnFechar (Text="Fechar", Action=dispose)
