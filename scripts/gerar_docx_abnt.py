# -*- coding: utf-8 -*-
"""
Gerador de Documento Word Editável (.docx) no Padrão ABNT (NBR 14724)
Instituição: Universidade Veiga de Almeida (UVA)
Disciplina: Programação Orientada a Objetos (POO)

Grupo:
- IASMIM DA SILVA MANN JOSÉ - 1250109566
- REBECA MACHADO DE LIMA - 1250103657
- SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978
- SILVANA GOMES DA SILVA - 1250109767
"""

import os
from docx import Document
from docx.shared import Inches, Pt, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._element.get_or_add_tcPr()
    tcPr.append(parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>'))

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._element.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def criar_documento_abnt(caminho_arquivo):
    doc = Document()
    
    # ----------------------------------------------------
    # CONFIGURAÇÃO DE MARGENS ABNT (NBR 14724)
    # Superior: 3,0 cm | Esquerda: 3,0 cm | Inferior: 2,0 cm | Direita: 2,0 cm
    # ----------------------------------------------------
    for section in doc.sections:
        section.top_margin = Cm(3.0)
        section.left_margin = Cm(3.0)
        section.bottom_margin = Cm(2.0)
        section.right_margin = Cm(2.0)
        section.page_width = Cm(21.0)   # A4
        section.page_height = Cm(29.7)
        section.header.is_linked_to_previous = False
        section.different_first_page_header_footer = True
        
    # Estilo Normal
    style_normal = doc.styles['Normal']
    style_normal.font.name = 'Arial'
    style_normal.font.size = Pt(12)
    style_normal.font.color.rgb = RGBColor(0, 0, 0)
    style_normal.paragraph_format.line_spacing = 1.5
    style_normal.paragraph_format.space_after = Pt(6)

    # ====================================================
    # 1. CAPA (ABNT)
    # ====================================================
    p_inst = doc.add_paragraph()
    p_inst.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p_inst.add_run("UNIVERSIDADE VEIGA DE ALMEIDA – UVA\n")
    r.bold = True
    r.font.size = Pt(14)
    r2 = p_inst.add_run("CURSO DE CIÊNCIA DA COMPUTAÇÃO / ENGENHARIA DE SOFTWARE\nDISCIPLINA: PROGRAMAÇÃO ORIENTADA A OBJETOS (POO)")
    r2.bold = True
    r2.font.size = Pt(11)
    p_inst.paragraph_format.space_after = Pt(70)

    # Integrantes
    p_autores = doc.add_paragraph()
    p_autores.alignment = WD_ALIGN_PARAGRAPH.CENTER
    autores = [
        "IASMIM DA SILVA MANN JOSÉ – Matrícula: 1250109566",
        "REBECA MACHADO DE LIMA – Matrícula: 1250103657",
        "SARAH GABRIELLA VIRGÍNIA DA SILVA – Matrícula: 1250106978",
        "SILVANA GOMES DA SILVA – Matrícula: 1250109767"
    ]
    for idx, a in enumerate(autores):
        r_a = p_autores.add_run(a + ("\n" if idx < len(autores) - 1 else ""))
        r_a.bold = True
        r_a.font.size = Pt(12)
    p_autores.paragraph_format.space_after = Pt(110)

    # Título do Trabalho
    p_titulo = doc.add_paragraph()
    p_titulo.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_tit = p_titulo.add_run("SISTEMA DE GESTÃO COMERCIAL E INFORMAÇÃO\n(SISVENDAS)\n")
    r_tit.bold = True
    r_tit.font.size = Pt(16)
    r_sub = p_titulo.add_run("Desenvolvimento de Sistema de Informação em Java com Orientação a Objetos\nRelatório Técnico da Avaliação A2")
    r_sub.font.size = Pt(12)
    p_titulo.paragraph_format.space_after = Pt(130)

    # Cidade e Ano
    p_rodape = doc.add_paragraph()
    p_rodape.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_cid = p_rodape.add_run("RIO DE JANEIRO – RJ\n2026")
    r_cid.bold = True
    r_cid.font.size = Pt(11)

    doc.add_page_break()

    # ====================================================
    # 2. FOLHA DE ROSTO (ABNT)
    # ====================================================
    p_rosto_aut = doc.add_paragraph()
    p_rosto_aut.alignment = WD_ALIGN_PARAGRAPH.CENTER
    for idx, a in enumerate(autores):
        r_ra = p_rosto_aut.add_run(a + ("\n" if idx < len(autores) - 1 else ""))
        r_ra.bold = True
        r_ra.font.size = Pt(11)
    p_rosto_aut.paragraph_format.space_after = Pt(90)

    p_rosto_tit = doc.add_paragraph()
    p_rosto_tit.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_rtit = p_rosto_tit.add_run("SISTEMA DE GESTÃO COMERCIAL E INFORMAÇÃO\n(SISVENDAS)")
    r_rtit.bold = True
    r_rtit.font.size = Pt(15)
    p_rosto_tit.paragraph_format.space_after = Pt(60)

    # Nota de Apresentação (Recuo à direita 8cm, fonte 10, simples)
    p_nota = doc.add_paragraph()
    p_nota.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p_nota.paragraph_format.left_indent = Cm(8.0)
    p_nota.paragraph_format.line_spacing = 1.0
    r_nota = p_nota.add_run(
        "Trabalho acadêmico e projeto prático apresentado à Universidade Veiga de Almeida (UVA), "
        "como requisito parcial para avaliação da A2 na disciplina de Programação Orientada a Objetos.\n\n"
        "Professores Avaliadores da Disciplina."
    )
    r_nota.font.size = Pt(10)
    p_nota.paragraph_format.space_after = Pt(140)

    # Cidade e Ano
    p_rosto_rod = doc.add_paragraph()
    p_rosto_rod.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_rcid = p_rosto_rod.add_run("RIO DE JANEIRO – RJ\n2026")
    r_rcid.bold = True
    r_rcid.font.size = Pt(11)

    doc.add_page_break()

    # ====================================================
    # 3. SUMÁRIO (ABNT)
    # ====================================================
    p_sum_tit = doc.add_paragraph()
    p_sum_tit.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r_stit = p_sum_tit.add_run("SUMÁRIO")
    r_stit.bold = True
    r_stit.font.size = Pt(14)
    p_sum_tit.paragraph_format.space_after = Pt(18)

    sumario_linhas = [
        ("1. INTRODUÇÃO E CONTEXTUALIZAÇÃO DO MINI-MUNDO", "4"),
        ("2. REQUISITOS DO SISTEMA DE INFORMAÇÃO", "5"),
        ("    2.1. Requisitos Funcionais (RF)", "5"),
        ("    2.2. Requisitos Não Funcionais (RNF)", "5"),
        ("3. MODELAGEM ORIENTADA A OBJETOS (POO)", "6"),
        ("    3.1. Abstração e Encapsulamento", "6"),
        ("    3.2. Herança e Polimorfismo", "6"),
        ("    3.3. Associação, Composição e Agregação", "6"),
        ("    3.4. Descrição Detalhada das Classes de Domínio", "7"),
        ("4. ARQUITETURA E PADRÕES DE PROJETO (DESIGN PATTERNS)", "8"),
        ("    4.1. Camada Model-View-Service-DAO", "8"),
        ("    4.2. Padrão Data Access Object (DAO)", "8"),
        ("    4.3. Padrão Singleton", "8"),
        ("5. IMPLEMENTAÇÃO DAS OPERAÇÕES DO CRUD", "9"),
        ("    5.1. CRUD da Entidade Cliente", "9"),
        ("    5.2. CRUD da Entidade Produto", "9"),
        ("6. IMPLEMENTAÇÃO DA TELA DE NEGÓCIO", "10"),
        ("    6.1. Fluxo Transacional de Emissão de Vendas", "10"),
        ("    6.2. Validação em Tempo Real de Saldo de Estoque", "10"),
        ("    6.3. Cálculo Dinâmico de Descontos e Totalizador", "10"),
        ("    6.4. Baixa Automática de Estoque e Cupom Fiscal", "11"),
        ("7. SIMULAÇÃO DE BASE DE DADOS E PERSISTÊNCIA EM TABELAS", "12"),
        ("    7.1. Tabelas em Memória e Estrutura Relacional", "12"),
        ("    7.2. Sincronização Automática em Arquivos CSV", "12"),
        ("8. INTERFACES GRÁFICAS E ESPECIFICAÇÃO DE ARQUIVOS .FRM", "13"),
        ("9. GUIA DE EXECUÇÃO E ROTEIRO DE TESTES", "14"),
        ("10. CONCLUSÃO", "15"),
        ("11. REFERÊNCIAS BIBLIOGRÁFICAS (NBR 6023)", "16")
    ]

    for item, num_pag in sumario_linhas:
        p_item = doc.add_paragraph()
        p_item.paragraph_format.line_spacing = 1.15
        p_item.paragraph_format.space_after = Pt(3)
        r_i = p_item.add_run(item)
        if not item.startswith("    "):
            r_i.bold = True
        r_i.font.size = Pt(11)
        r_dots = p_item.add_run(" " + "." * (75 - len(item) * 2) + " ")
        r_dots.font.color.rgb = RGBColor(150, 150, 150)
        r_p = p_item.add_run(num_pag)
        r_p.bold = True
        r_p.font.size = Pt(11)

    doc.add_page_break()

    # Função auxiliar para seções numeradas
    def add_sec_title(num_title):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(18)
        p.paragraph_format.space_after = Pt(10)
        r = p.add_run(num_title)
        r.bold = True
        r.font.size = Pt(13)
        return p

    def add_subsec_title(num_title):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(12)
        p.paragraph_format.space_after = Pt(6)
        r = p.add_run(num_title)
        r.bold = True
        r.font.size = Pt(11.5)
        return p

    def add_p(texto):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.first_line_indent = Cm(1.25)
        p.paragraph_format.line_spacing = 1.5
        p.paragraph_format.space_after = Pt(6)
        r = p.add_run(texto)
        r.font.size = Pt(12)
        return p

    def add_bullet(titulo, desc):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.left_indent = Cm(1.25)
        p.paragraph_format.line_spacing = 1.3
        p.paragraph_format.space_after = Pt(4)
        rb = p.add_run("• " + titulo + ": ")
        rb.bold = True
        rd = p.add_run(desc)
        return p

    # ====================================================
    # 4. CONTEÚDO TÉCNICO
    # ====================================================
    add_sec_title("1. INTRODUÇÃO E CONTEXTUALIZAÇÃO DO MINI-MUNDO")
    add_p(
        "O presente trabalho acadêmico consiste no desenvolvimento do SisVendas, um Sistema de Informação "
        "comercial implementado na linguagem de programação Java, desenvolvido pelo grupo de alunos da "
        "Universidade Veiga de Almeida (UVA), como instrumento prático e avaliativo da disciplina de Programação "
        "Orientada a Objetos (POO) para a Avaliação A2."
    )
    add_p(
        "O mini-mundo modelado aborda o contexto de gestão de uma distribuidora de produtos de tecnologia. "
        "O sistema abrange desde o cadastramento de clientes e catálogo de produtos até o núcleo transacional "
        "da empresa: a Tela de Negócio para realização de vendas comerciais com cálculo de descontos, validação "
        "em tempo real de estoque, baixa física de inventário e simulação estruturada de base de dados em tabelas."
    )
    add_p(
        "O projeto cumpre com rigor os requisitos estabelecidos na avaliação A2: presença de três classes de "
        "domínio integradas, operações completas de CRUD (Create, Read, Update, Delete) para cada uma delas, "
        "uma Tela de Negócio funcional, base de dados simulada para persistência e interface gráfica implementada "
        "em Java Swing com fornecimento dos arquivos de formulário com extensão .frm e .form."
    )

    add_sec_title("2. REQUISITOS DO SISTEMA DE INFORMAÇÃO")
    add_subsec_title("2.1. Requisitos Funcionais (RF)")
    add_bullet("RF01 – Cadastro e Gerenciamento de Clientes (CRUD)", "Permite incluir, consultar por busca textual, alterar e excluir clientes, com validação de CPF (11 dígitos numéricos) e preenchimento de campos obrigatórios.")
    add_bullet("RF02 – Cadastro e Gerenciamento de Produtos (CRUD)", "Permite registrar itens no catálogo, definindo código/SKU, descrição, categoria, preço unitário e saldo de estoque físico.")
    add_bullet("RF03 – Tela de Negócio de Emissão de Vendas", "Interface específica onde o operador escolhe um cliente previamente cadastrado, seleciona produtos e quantidades, gerando um carrinho dinâmico.")
    add_bullet("RF04 – Validação de Estoque em Tempo Real", "O sistema impede a adição de quantidades superiores às unidades disponíveis na base de dados, emitindo alerta ao usuário.")
    add_bullet("RF05 – Cálculo de Totais e Descontos", "Aplica automaticamente descontos financeiros conforme a modalidade de liquidação (ex.: 5% de desconto para PIX ou Dinheiro) ou desconto manual concedido.")
    add_bullet("RF06 – Baixa e Atualização Automática de Estoque", "No fechamento da transação comercial, o saldo dos produtos vendidos é debitado automaticamente na base de dados simulada.")
    add_bullet("RF07 – Emissão de Cupom Fiscal / Comprovante", "Exibe ao final da venda um cupom fiscal detalhado contendo código do pedido, dados do cliente, itens adquiridos, descontos e total líquido.")
    add_bullet("RF08 – Histórico e Cancelamento com Estorno", "Permite auditar as vendas registradas e efetuar o cancelamento, devolvendo as mercadorias ao estoque da empresa.")
    add_bullet("RF09 – Persistência Estruturada em Tabelas", "Os dados residem em coleções em memória e são sincronizados continuamente em arquivos tabulares CSV (clientes.csv, produtos.csv e vendas.csv).")

    add_subsec_title("2.2. Requisitos Não Funcionais (RNF)")
    add_bullet("RNF01 – Portabilidade", "Executável em qualquer ambiente operacional com Java SE (JDK 8 ou superior), sem bibliotecas proprietárias externas.")
    add_bullet("RNF02 – Usabilidade e Ergonomia", "Interface gráfica em Java Swing com visual Nimbus, diálogos explicativos e tabelas interativas (JTable).")
    add_bullet("RNF03 – Arquitetura Limpa e Modular", "Divisão estrita em camadas de responsabilidade: Model (Domínio), DAO (Acesso a Dados), Service (Regras de Negócio) e View (Apresentação).")

    add_sec_title("3. MODELAGEM ORIENTADA A OBJETOS (POO)")
    add_subsec_title("3.1. Abstração e Encapsulamento")
    add_p(
        "Todas as entidades do sistema implementam encapsulamento estrito. Os atributos são definidos com modificador "
        "privado (private) ou protegido (protected), acessíveis apenas através de getters e setters que possuem validações "
        "de coerência e integridade."
    )
    add_subsec_title("3.2. Herança e Polimorfismo")
    add_p(
        "Foi criada a classe abstrata EntidadeBase, que centraliza atributos transversais como id (identificador primário), "
        "dataCriacao e dataUltimaModificacao. A classe obriga todas as subclasses a implementar o método abstrato validar(), "
        "garantindo validação polimórfica específica para cada domínio."
    )
    add_subsec_title("3.3. Associação, Composição e Agregação")
    add_p(
        "A modelagem estruturou-se em relações formais da Orientação a Objetos:\n"
        "• Composição: A classe Pedido compõe uma lista de objetos ItemPedido. Cada item só existe no contexto de um pedido.\n"
        "• Agregação: Cada ItemPedido agrega um objeto Produto. O produto independe do pedido e possui ciclo de vida autônomo.\n"
        "• Associação Unidirecional: O Pedido associa-se ao Cliente que realizou a compra."
    )

    add_subsec_title("3.4. Descrição Detalhada das Classes de Domínio")
    
    # Tabela de Classes
    tabela = doc.add_table(rows=1, cols=4)
    tabela.alignment = WD_TABLE_ALIGNMENT.CENTER
    hdr = tabela.rows[0].cells
    hdr[0].text = "Classe"
    hdr[1].text = "Tipo / Relação"
    hdr[2].text = "Atributos Principais"
    hdr[3].text = "Métodos de Negócio"
    for cell in hdr:
        set_cell_background(cell, "184C78")
        set_cell_margins(cell, top=120, bottom=120, left=150, right=150)
        for p in cell.paragraphs:
            p.runs[0].font.bold = True
            p.runs[0].font.color.rgb = RGBColor(255, 255, 255)
            p.runs[0].font.size = Pt(10)

    dados_classes = [
        ("EntidadeBase", "Abstrata (Raiz)", "id, dataCriacao, dataUltimaModificacao", "validar() [abstrato], getId(), setId()"),
        ("Cliente", "Herança EntidadeBase", "nome, cpf, email, telefone, endereco", "validar(), formatarCpf(), toString()"),
        ("Produto", "Herança EntidadeBase", "codigo, nome, categoria, preco, estoque", "debitarEstoque(), creditarEstoque(), validar()"),
        ("ItemPedido", "Agregação / Item", "produto, quantidade, precoUnitario", "getSubtotal(), getQuantidade()"),
        ("Pedido", "Herança EntidadeBase", "cliente, itens, formaPagamento, status", "adicionarItem(), finalizarVenda(), cancelarVenda()"),
        ("FormaPagamento", "Enumeração (Enum)", "DINHEIRO, PIX, DEBITO, CREDITO", "getDescontoPadrao(), getDescricao()"),
        ("StatusPedido", "Enumeração (Enum)", "PENDENTE, FINALIZADO, CANCELADO", "getDescricao()")
    ]

    for c_nome, c_tipo, c_atrib, c_met in dados_classes:
        row = tabela.add_row().cells
        row[0].text = c_nome
        row[1].text = c_tipo
        row[2].text = c_atrib
        row[3].text = c_met
        for idx, cell in enumerate(row):
            set_cell_margins(cell, top=80, bottom=80, left=120, right=120)
            if idx == 0:
                cell.paragraphs[0].runs[0].font.bold = True
            for p in cell.paragraphs:
                p.runs[0].font.size = Pt(9.5)

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    add_sec_title("4. ARQUITETURA E PADRÕES DE PROJETO (DESIGN PATTERNS)")
    add_p(
        "A estrutura de software do SisVendas foi concebida sob o princípio da separação de responsabilidades (SoC), "
        "garantindo facilidade de manutenção, desacoplamento e testabilidade:"
    )
    add_bullet("Model (br.com.sisvendas.model)", "Responsável pelas entidades de negócio, estados, métodos de alteração interna e validações.")
    add_bullet("DAO (br.com.sisvendas.dao)", "Abstrai e gerencia a persistência através da interface genérica DAO<T extends EntidadeBase>.")
    add_bullet("Service (br.com.sisvendas.service)", "Orquestra as regras transacionais: checagem de saldo, baixa de estoque, unicidade cadastral.")
    add_bullet("View (br.com.sisvendas.view)", "Interfaces gráficas Swing vinculadas às especificações visuais dos formulários .frm.")

    add_p(
        "Padrões de Projeto Aplicados:\n"
        "1. Data Access Object (DAO): Padroniza operações de persistência com métodos salvar(), atualizar(), excluir(), "
        "buscarPorId() e listarTodos().\n"
        "2. Singleton: A classe BancoDadosMemoria adota o padrão Singleton garantindo uma única instância compartilhada "
        "de controle das tabelas em memória e escrita concorrente em disco."
    )

    add_sec_title("5. IMPLEMENTAÇÃO DAS OPERAÇÕES DO CRUD")
    add_subsec_title("5.1. CRUD de Clientes (TelaCliente.java / TelaCliente.frm)")
    add_p(
        "O módulo de clientes disponibiliza interface com formulário superior e grade tabular inferior (JTable):\n"
        "• Inclusão: Cadastro com validação de formato de CPF (11 dígitos) e bloqueio de CPF duplicado na base.\n"
        "• Consulta: Campo de busca incremental por nome que filtra a JTable em tempo real.\n"
        "• Alteração: Ao clicar em uma linha da tabela, os dados são carregados nos campos do formulário para edição.\n"
        "• Exclusão: Exclusão com confirmação visual e validação de integridade (impede exclusão de clientes com compras)."
    )
    add_subsec_title("5.2. CRUD de Produtos (TelaProduto.java / TelaProduto.frm)")
    add_p(
        "O módulo de catálogo de produtos oferece manutenção completa dos itens comercializados:\n"
        "• Inclusão: Registro com campos de código/SKU, descrição, seleção de categoria via JComboBox e preço unitário.\n"
        "• Consulta: Busca textual por nome, código ou categoria.\n"
        "• Alteração: Atualização de preços e reposição de saldo de estoque.\n"
        "• Exclusão: Exclusão condicionada à ausência de histórico de vendas.\n"
        "• Monitoramento de Estoque: A tabela alerta visualmente 'ESTOQUE BAIXO' quando a quantidade for inferior a 5 unidades."
    )

    add_sec_title("6. IMPLEMENTAÇÃO DA TELA DE NEGÓCIO")
    add_p(
        "A Tela de Negócio (TelaVenda.java e TelaVenda.frm) constitui o coração operacional do SisVendas, "
        "implementando o fluxo transacional de atendimento:"
    )
    add_p(
        "1. Seleção do Cliente: O vendedor seleciona o cliente cadastrado em lista suspensa. O sistema preenche instantaneamente "
        "os dados cadastrais (CPF, telefone, endereço) na tela.\n"
        "2. Validação de Estoque em Tempo Real: O operador escolhe o produto e a quantidade. O sistema consulta a base "
        "e bloqueia inclusões cuja quantidade acumulada no carrinho exceda o estoque físico disponível.\n"
        "3. Montagem Interativa do Carrinho: Os itens são adicionados a uma JTable com cálculo individual de subtotal.\n"
        "4. Fechamento e Formas de Pagamento: Ao selecionar a forma de liquidação (PIX, Dinheiro, Débito, Crédito), "
        "o sistema recalcula os descontos automaticamente (5% de desconto promocional para PIX/Dinheiro).\n"
        "5. Transação Comercial e Emissão de Cupom: O botão 'Concluir Venda' debita imediatamente o estoque físico de cada item, "
        "registra o pedido na tabela de vendas e exibe na tela o cupom fiscal formatado."
    )

    add_sec_title("7. SIMULAÇÃO DE BASE DE DADOS E PERSISTÊNCIA EM TABELAS")
    add_p(
        "Atendendo ao requisito 'O sistema deverá simular uma base de dados para persistir as informações, através de em uma tabela', "
        "a classe BancoDadosMemoria implementa persistência em duas camadas sincronizadas:"
    )
    add_bullet("Tabelas em Memória", "Estruturas LinkedHashMap indexadas por chaves primárias e sequências automáticas: TB_CLIENTES, TB_PRODUTOS e TB_PEDIDOS.")
    add_bullet("Persistência em Arquivos CSV", "Cada modificação é salva nos arquivos tabulares clientes.csv, produtos.csv e vendas.csv no diretório dados/.")
    add_bullet("Carga Inicial de Dados (Seed)", "O sistema inicia com 5 clientes pré-cadastrados, 8 produtos em diversas categorias e 1 pedido modelo para avaliação imediata pelo professor.")

    add_sec_title("8. INTERFACES GRÁFICAS E ESPECIFICAÇÃO DE ARQUIVOS .FRM")
    add_p(
        "O requisito explícito da avaliação 'Deverá conter um arquivo .frm (interface gráfica)' foi plenamente integrado "
        "ao projeto através do fornecimento duplo:\n"
        "• Arquivos .frm: Arquivos textuais de especificação estrutural de cada tela (TelaPrincipal.frm, TelaCliente.frm, "
        "TelaProduto.frm, TelaVenda.frm).\n"
        "• Arquivos .form: Arquivos declarativos no padrão do NetBeans GUI Builder / Matisse, permitindo edição visual em IDEs."
    )

    add_sec_title("9. GUIA DE EXECUÇÃO E ROTEIRO DE TESTES")
    add_p(
        "A aplicação é fornecida com scripts de execução automatizada para o sistema operacional Windows:\n"
        "1. Para executar imediatamente: Dê um duplo clique no arquivo run.bat na raiz do projeto. O script detecta "
        "o Java instalado e abre a interface gráfica.\n"
        "2. Para recompilar os fontes: Execute o arquivo build.bat.\n"
        "3. Em IDEs (NetBeans / Eclipse / VS Code): Abra a pasta do projeto e execute a classe br.com.sisvendas.Main."
    )

    add_sec_title("10. CONCLUSÃO")
    add_p(
        "O projeto SisVendas atingiu integralmente os objetivos acadêmicos da disciplina de Programação Orientada a Objetos "
        "na Universidade Veiga de Almeida (UVA). Demonstrou-se a aplicação prática dos conceitos fundamentais de POO em um sistema "
        "completo, funcional e robusto, com interfaces amigáveis, regras de negócio consistentes e documentação técnica no padrão ABNT."
    )

    add_sec_title("11. REFERÊNCIAS BIBLIOGRÁFICAS (NBR 6023)")
    add_bullet("ABNT", "ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. NBR 14724: Informação e documentação — Trabalhos acadêmicos — Apresentação. Rio de Janeiro: ABNT, 2011.")
    add_bullet("ABNT", "ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. NBR 6023: Informação e documentação — Referências — Elaboração. Rio de Janeiro: ABNT, 2018.")
    add_bullet("DEITEL", "DEITEL, Paul; DEITEL, Harvey. Java: Como Programar. 10. ed. São Paulo: Pearson Education do Brasil, 2016.")
    add_bullet("GAMMA", "GAMMA, Erich et al. Padrões de Projeto: Soluções Reutilizáveis de Software Orientado a Objetos. Porto Alegre: Bookman, 2000.")
    add_bullet("SIERRA", "SIERRA, Kathy; BATES, Bert. Use a Cabeça! Java. 2. ed. Rio de Janeiro: Alta Books, 2007.")

    doc.save(caminho_arquivo)
    print(f"[OK] Documento Word ABNT editavel gerado com sucesso: {caminho_arquivo}")

if __name__ == "__main__":
    caminho = os.path.join(r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs", "SisVendas_Documentacao_ABNT.docx")
    criar_documento_abnt(caminho)
