# ==============================================================================
# SisVendas - Definição do Formulário de Interface Gráfica (.frm)
# Formulário: TelaVenda.frm (TELA DE NEGÓCIO: Realização de Vendas)
# Autora: Iasmim José
# Disciplina: Programação Orientada a Objetos
# ==============================================================================

[FormDefinition]
Name=TelaVenda
Title=Tela de Negócio - Emissão de Vendas e Pedidos
Type=javax.swing.JFrame
Width=960
Height=680
DefaultCloseOperation=DISPOSE_ON_CLOSE
Layout=BorderLayout

[Section.1:CustomerSelection]
Component=cbClientes (Type=JComboBox<Cliente>, DataSource=BancoDadosMemoria.TB_CLIENTES)
LabelInfo=lblDadosCliente (Format="CPF: {cpf} | Tel: {telefone} | Cidade: {cidade}")

[Section.2:ProductSelectionAndValidation]
Component.1=cbProdutos (Type=JComboBox<Produto>, DataSource=BancoDadosMemoria.TB_PRODUTOS)
Component.2=spQuantidade (Type=JSpinner, Min=1, Max=999, Default=1)
Component.3=lblPrecoUnitario (Display="Preço: R$ {preco}")
Component.4=lblEstoqueDisponivel (Display="Estoque: {qtd} un.", RealTimeStockValidation=true)
Component.5=btnAdicionarItem (Text="➕ Adicionar ao Pedido", Action=adicionarItemAoCarrinho)
Component.6=btnRemoverItem (Text="🗑️ Remover Item", Action=removerItemDoCarrinho)

[Section.3:CartTableSimulation]
Table=tabelaCarrinho (Type=JTable)
Columns=[Item #, Código, Descrição do Produto, Preço Unit. (R$), Qtd, Subtotal (R$)]

[Section.4:PaymentAndTotals]
Component.1=cbFormaPagamento (Type=JComboBox<FormaPagamento>, Options=["DINHEIRO", "PIX", "CARTAO_DEBITO", "CARTAO_CREDITO"])
Component.2=txtDesconto (Type=JTextField, Format="Currency")
Component.3=lblSubtotalBruto (Display="Subtotal Bruto: R$ {valor}")
Component.4=lblValorDesconto (Display="Desconto Total: R$ {valor}")
Component.5=lblTotalLiquido (Display="VALOR FINAL: R$ {valor}", Style="Bold Green 18pt")

[Section.5:BusinessActions]
Button.1=btnFinalizarVenda (Text="✅ Concluir Venda e Emitir Cupom", Action=concluirVenda, Effects=["Estoque.Debitar", "Pedido.Salvar", "CupomFiscal.Exibir"])
Button.2=btnCancelarVenda (Text="❌ Cancelar / Novo Pedido", Action=resetarFormulario)
