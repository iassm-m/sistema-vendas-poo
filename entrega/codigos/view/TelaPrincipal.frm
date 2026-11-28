# ==============================================================================
# SisVendas - Definição do Formulário de Interface Gráfica (.frm)
# Formulário: TelaPrincipal.frm
# Grupo: IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
# Disciplina: Programação Orientada a Objetos
# ==============================================================================

[FormDefinition]
Name=TelaPrincipal
Title=SisVendas - Sistema de Informação e Gestão Comercial
Type=javax.swing.JFrame
Width=1000
Height=680
DefaultCloseOperation=EXIT_ON_CLOSE
Layout=BorderLayout

[Components]
Component.1=JMenuBar:menuBar
Component.1.Menu.1=JMenu:menuCadastros (Text="Cadastros")
Component.1.Menu.1.Item.1=JMenuItem:itemClientes (Text="Gerenciar Clientes (CRUD)", Action=abrirTelaClientes)
Component.1.Menu.1.Item.2=JMenuItem:itemProdutos (Text="Gerenciar Produtos (CRUD)", Action=abrirTelaProdutos)
Component.1.Menu.1.Item.3=JMenuItem:itemSair (Text="Sair do Sistema", Action=sair)
Component.1.Menu.2=JMenu:menuVendas (Text="Vendas")
Component.1.Menu.2.Item.1=JMenuItem:itemNovaVenda (Text="Realizar Venda (Tela de Negócio)", Action=abrirTelaVenda)
Component.1.Menu.2.Item.2=JMenuItem:itemConsultarVendas (Text="Histórico e Consulta de Vendas", Action=abrirTelaConsultaVendas)

Component.2=JPanel:pnlHeader (Position=NORTH, Background=#184C78)
Component.2.Label.1=JLabel:lblTitulo (Text="SisVendas - Sistema de Gestão Comercial", Font=Segoe UI Bold 22)
Component.2.Label.2=JLabel:lblSubtitulo (Text="Programação Orientada a Objetos - Iasmim, Rebeca, Sarah, Silvana", Font=Segoe UI 13)

Component.3=JToolBar:toolBar (Position=CENTER_NORTH)
Component.3.Button.1=JButton:btnNavClientes (Text="👤 Clientes")
Component.3.Button.2=JButton:btnNavProdutos (Text="📦 Produtos")
Component.3.Button.3=JButton:btnNavVenda (Text="🛒 Nova Venda (Negócio)")
Component.3.Button.4=JButton:btnNavHistorico (Text="📋 Histórico Vendas")

Component.4=JPanel:pnlDashboardCards (Layout=GridLayout, Columns=4)
Component.4.Card.1=MetricCard:cardClientes (Title="CLIENTES CADASTRADOS", Variable=lblTotalClientes)
Component.4.Card.2=MetricCard:cardProdutos (Title="PRODUTOS NO CATÁLOGO", Variable=lblTotalProdutos)
Component.4.Card.3=MetricCard:cardVendas (Title="VENDAS REALIZADAS", Variable=lblTotalVendas)
Component.4.Card.4=MetricCard:cardFaturamento (Title="FATURAMENTO TOTAL", Variable=lblFaturamento)

Component.5=JTable:tabelaUltimasVendas (Position=CENTER, Columns=[Código, Cliente, Data da Venda, Qtd Itens, Forma Pagamento, Total Líquido, Status])
