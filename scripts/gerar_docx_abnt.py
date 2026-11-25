# -*- coding: utf-8 -*-
"""
Gerador de Documento Word Editável (.docx) no Padrão ABNT (NBR 14724)
Com Inclusão de Screenshots de Testes do Sistema (Prints das Telas).

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
    pasta_prints = r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs\screenshots"
    
    # Margens ABNT (NBR 14724): Superior 3cm, Esquerda 3cm, Inferior 2cm, Direita 2cm
    for section in doc.sections:
        section.top_margin = Cm(3.0)
        section.left_margin = Cm(3.0)
        section.bottom_margin = Cm(2.0)
        section.right_margin = Cm(2.0)
        section.page_width = Cm(21.0)
        section.page_height = Cm(29.7)
        section.different_first_page_header_footer = True
        
    style_normal = doc.styles['Normal']
    style_normal.font.name = 'Arial'
    style_normal.font.size = Pt(12)
    style_normal.font.color.rgb = RGBColor(0, 0, 0)
    style_normal.paragraph_format.line_spacing = 1.5
    style_normal.paragraph_format.space_after = Pt(6)

    # 1. CAPA
    p_inst = doc.add_paragraph()
    p_inst.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p_inst.add_run("UNIVERSIDADE VEIGA DE ALMEIDA – UVA\n")
    r.bold = True
    r.font.size = Pt(14)
    r2 = p_inst.add_run("CURSO DE CIÊNCIA DA COMPUTAÇÃO / ENGENHARIA DE SOFTWARE\nDISCIPLINA: PROGRAMAÇÃO ORIENTADA A OBJETOS (POO)")
    r2.bold = True
    r2.font.size = Pt(11)
    p_inst.paragraph_format.space_after = Pt(70)

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
    p_autores.paragraph_format.space_after = Pt(100)

    p_titulo = doc.add_paragraph()
    p_titulo.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_tit = p_titulo.add_run("SISTEMA DE GESTÃO COMERCIAL E INFORMAÇÃO\n(SISVENDAS)\n")
    r_tit.bold = True
    r_tit.font.size = Pt(16)
    r_sub = p_titulo.add_run("Desenvolvimento de Sistema de Informação em Java com Orientação a Objetos\nRelatório Técnico e Evidências de Testes – Avaliação A2")
    r_sub.font.size = Pt(12)
    p_titulo.paragraph_format.space_after = Pt(120)

    p_rodape = doc.add_paragraph()
    p_rodape.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_cid = p_rodape.add_run("RIO DE JANEIRO – RJ\n2026")
    r_cid.bold = True
    r_cid.font.size = Pt(11)

    doc.add_page_break()

    # 2. FOLHA DE ROSTO
    p_rosto_aut = doc.add_paragraph()
    p_rosto_aut.alignment = WD_ALIGN_PARAGRAPH.CENTER
    for idx, a in enumerate(autores):
        r_ra = p_rosto_aut.add_run(a + ("\n" if idx < len(autores) - 1 else ""))
        r_ra.bold = True
        r_ra.font.size = Pt(11)
    p_rosto_aut.paragraph_format.space_after = Pt(80)

    p_rosto_tit = doc.add_paragraph()
    p_rosto_tit.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_rtit = p_rosto_tit.add_run("SISTEMA DE GESTÃO COMERCIAL E INFORMAÇÃO\n(SISVENDAS)")
    r_rtit.bold = True
    r_rtit.font.size = Pt(15)
    p_rosto_tit.paragraph_format.space_after = Pt(60)

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
    p_nota.paragraph_format.space_after = Pt(130)

    p_rosto_rod = doc.add_paragraph()
    p_rosto_rod.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_rcid = p_rosto_rod.add_run("RIO DE JANEIRO – RJ\n2026")
    r_rcid.bold = True
    r_rcid.font.size = Pt(11)

    doc.add_page_break()

    # 3. SUMÁRIO
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
        ("8. INTERFACES GRÁFICAS E ESPECIFICAÇÃO DE ARQUIVOS .FRM", "13"),
        ("9. EVIDÊNCIAS DE TESTES DO SISTEMA (PRINTS DAS TELAS)", "14"),
        ("    9.1. Teste 1: Interface Principal com Dashboard de Indicadores", "14"),
        ("    9.2. Teste 2: CRUD de Clientes – Inclusão e Consulta Tabular", "15"),
        ("    9.3. Teste 3: CRUD de Produtos – Catálogo e Alerta de Estoque Baixo", "16"),
        ("    9.4. Teste 4: Tela de Negócio – Emissão de Vendas e Carrinho Dinâmico", "17"),
        ("    9.5. Teste 5: Emissão de Cupom Fiscal e Baixa Automática de Estoque", "18"),
        ("    9.6. Teste 6: Auditoria, Histórico de Vendas e Cancelamento com Estorno", "19"),
        ("10. GUIA DE EXECUÇÃO E ROTEIRO DE TESTES", "20"),
        ("11. CONCLUSÃO", "21"),
        ("12. REFERÊNCIAS BIBLIOGRÁFICAS (NBR 6023)", "22")
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

    def add_figure(img_path, caption):
        if os.path.exists(img_path):
            p_img = doc.add_paragraph()
            p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_img.paragraph_format.space_before = Pt(8)
            p_img.paragraph_format.space_after = Pt(4)
            p_img.add_run().add_picture(img_path, width=Inches(5.8))
            
            p_cap = doc.add_paragraph()
            p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_cap.paragraph_format.space_after = Pt(12)
            rc = p_cap.add_run(caption)
            rc.font.size = Pt(9.5)
            rc.font.italic = True
            rc.font.color.rgb = RGBColor(70, 70, 70)

    # CONTEÚDO
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

    add_sec_title("2. REQUISITOS DO SISTEMA DE INFORMAÇÃO")
    add_subsec_title("2.1. Requisitos Funcionais (RF)")
    add_bullet("RF01 – Cadastro e Gerenciamento de Clientes (CRUD)", "Permite incluir, consultar por busca textual, alterar e excluir clientes, com validação de CPF e dados obrigatórios.")
    add_bullet("RF02 – Cadastro e Gerenciamento de Produtos (CRUD)", "Permite registrar itens no catálogo, definindo código/SKU, descrição, categoria, preço unitário e saldo de estoque físico.")
    add_bullet("RF03 – Tela de Negócio de Emissão de Vendas", "Interface específica onde o operador escolhe um cliente, seleciona produtos e quantidades, compondo um carrinho dinâmico.")
    add_bullet("RF04 – Validação de Estoque em Tempo Real", "O sistema impede a adição de quantidades superiores às unidades disponíveis na base de dados.")
    add_bullet("RF05 – Cálculo de Totais e Descontos", "Aplica automaticamente descontos financeiros (5% de desconto para PIX ou Dinheiro) ou desconto manual.")
    add_bullet("RF06 – Baixa e Atualização Automática de Estoque", "No fechamento da transação comercial, o saldo dos produtos vendidos é debitado automaticamente na base de dados simulada.")
    add_bullet("RF07 – Emissão de Cupom Fiscal / Comprovante", "Exibe ao final da venda um cupom fiscal detalhado contendo todos os dados da transação.")
    add_bullet("RF08 – Histórico e Cancelamento com Estorno", "Permite auditar as vendas registradas e efetuar o cancelamento, devolvendo as mercadorias ao estoque.")
    add_bullet("RF09 – Persistência Estruturada em Tabelas", "Os dados residem em coleções em memória e são sincronizados continuamente em arquivos tabulares CSV.")

    add_sec_title("3. MODELAGEM ORIENTADA A OBJETOS (POO)")
    add_p("A modelagem estruturou-se em relações formais da Orientação a Objetos: Abstração, Encapsulamento, Herança na classe EntidadeBase, Polimorfismo e Composição.")

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

    add_sec_title("4. ARQUITETURA E PADRÕES DE PROJETO")
    add_p("Separação estrita em camadas: Model (Domínio), DAO (Acesso a Dados com interface DAO<T>), Service (Regras de Negócio) e View (Apresentação gráfica Swing acoplada aos formulários .frm).")

    add_sec_title("5. IMPLEMENTAÇÃO DAS OPERAÇÕES DO CRUD")
    add_p("Foram desenvolvidas interfaces visuais especializadas com validação, consulta textual em tempo real e verificação de integridade referencial.")

    add_sec_title("6. IMPLEMENTAÇÃO DA TELA DE NEGÓCIO")
    add_p("A Tela de Negócio (TelaVenda) orquestra a seleção do cliente, carrinho dinâmico, checagem de saldo físico de estoque em tempo real, cálculo financeiro de descontos, baixa de estoque e emissão de cupom fiscal.")

    add_sec_title("7. SIMULAÇÃO DE BASE DE DADOS E PERSISTÊNCIA EM TABELAS")
    add_p("Persistência em duas camadas: coleções em memória (TB_CLIENTES, TB_PRODUTOS, TB_PEDIDOS) e sincronização contínua em arquivos tabulares CSV (clientes.csv, produtos.csv, vendas.csv).")

    add_sec_title("8. INTERFACES GRÁFICAS E ESPECIFICAÇÃO DE ARQUIVOS .FRM")
    add_p("Atendendo ao requisito 'Deverá conter um arquivo .frm (interface gráfica)', o projeto inclui TelaPrincipal.frm, TelaCliente.frm, TelaProduto.frm e TelaVenda.frm, além dos arquivos .form do NetBeans.")

    # ====================================================
    # SEÇÃO 9: PRINTS DAS TELAS DE TESTES (NOVO REQUISITO)
    # ====================================================
    add_sec_title("9. EVIDÊNCIAS DE TESTES DO SISTEMA (PRINTS DAS TELAS)")
    add_p(
        "A fim de comprovar o funcionamento de todos os módulos desenvolvidos, foram realizados testes práticos "
        "completos no sistema SisVendas. As capturas de tela a seguir comprovam os resultados obtidos em cada etapa:"
    )

    add_subsec_title("9.1. Teste 1: Interface Principal com Dashboard de Indicadores")
    add_p("A tela principal centraliza os cartões de métricas comerciais (Total de Clientes, Produtos, Vendas Concluídas e Faturamento Total) e a tabela de últimas vendas registradas na base de dados.")
    add_figure(os.path.join(pasta_prints, "print_teste1_tela_principal.png"), 
               "Figura 1 – Teste 1: Interface Principal com Dashboard de Indicadores e Tabela de Vendas (SisVendas UVA)")

    add_subsec_title("9.2. Teste 2: CRUD de Clientes – Inclusão e Consulta Tabular")
    add_p("Demonstração da tela de gerenciamento de clientes, contendo formulário de inclusão/edição e tabela de consulta com filtro de busca textual por nome.")
    add_figure(os.path.join(pasta_prints, "print_teste2_crud_clientes.png"), 
               "Figura 2 – Teste 2: CRUD de Clientes – Formulário e Consulta Tabular com Filtro por Nome")

    add_subsec_title("9.3. Teste 3: CRUD de Produtos – Catálogo e Alerta de Estoque Baixo")
    add_p("Visualização do catálogo de produtos na base de dados, com preços unitários, quantidades físicas e destaque visual 'ESTOQUE BAIXO' para itens com saldo crítico.")
    add_figure(os.path.join(pasta_prints, "print_teste3_crud_produtos.png"), 
               "Figura 3 – Teste 3: CRUD de Produtos – Catálogo, Preços e Alerta Visual de Estoque Baixo")

    add_subsec_title("9.4. Teste 4: Tela de Negócio – Emissão de Vendas e Carrinho Dinâmico")
    add_p("Demonstração da Tela de Negócio com seleção do cliente, inclusão de itens no carrinho, validação de saldo físico de estoque em tempo real e aplicação de desconto promocional no PIX.")
    add_figure(os.path.join(pasta_prints, "print_teste4_tela_negocio_venda.png"), 
               "Figura 4 – Teste 4: Tela de Negócio – Seleção de Cliente, Carrinho e Validação em Tempo Real")

    add_subsec_title("9.5. Teste 5: Emissão de Cupom Fiscal e Baixa Automática de Estoque")
    add_p("Comprovante fiscal eletrônico gerado ao concluir a venda, confirmando o débito automático das unidades vendidas no estoque da base de dados.")
    add_figure(os.path.join(pasta_prints, "print_teste5_cupom_fiscal.png"), 
               "Figura 5 – Teste 5: Cupom Fiscal Eletrônico Emitido com Sucesso e Baixa de Estoque")

    add_subsec_title("9.6. Teste 6: Auditoria, Histórico de Vendas e Cancelamento com Estorno")
    add_p("Tela de Consulta e Histórico de Vendas com visão mestre-detalhe (pedidos no topo e itens embaixo) e funcionalidade para estorno de mercadorias devolvidas.")
    add_figure(os.path.join(pasta_prints, "print_teste6_historico_vendas.png"), 
               "Figura 6 – Teste 6: Auditoria de Vendas e Mecanismo de Cancelamento com Estorno de Estoque")

    add_sec_title("10. GUIA DE EXECUÇÃO E ROTEIRO DE TESTES")
    add_p("O sistema conta com o script run.bat para execução com 1 clique no Windows, integrando compilação automática e abertura da interface gráfica.")

    add_sec_title("11. CONCLUSÃO")
    add_p("O projeto SisVendas demonstrou a aplicação prática dos conceitos fundamentais de POO na Universidade Veiga de Almeida (UVA). Os testes práticos documentados asseguram a consistência e confiabilidade do sistema para a avaliação A2.")

    add_sec_title("12. REFERÊNCIAS BIBLIOGRÁFICAS (NBR 6023)")
    add_bullet("ABNT", "ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. NBR 14724: Informação e documentação — Trabalhos acadêmicos — Apresentação. Rio de Janeiro: ABNT, 2011.")
    add_bullet("ABNT", "ASSOCIAÇÃO BRASILEIRA DE NORMAS TÉCNICAS. NBR 6023: Informação e documentação — Referências — Elaboração. Rio de Janeiro: ABNT, 2018.")
    add_bullet("DEITEL", "DEITEL, Paul; DEITEL, Harvey. Java: Como Programar. 10. ed. São Paulo: Pearson Education do Brasil, 2016.")
    add_bullet("GAMMA", "GAMMA, Erich et al. Padrões de Projeto: Soluções Reutilizáveis de Software Orientado a Objetos. Porto Alegre: Bookman, 2000.")
    add_bullet("SIERRA", "SIERRA, Kathy; BATES, Bert. Use a Cabeça! Java. 2. ed. Rio de Janeiro: Alta Books, 2007.")

    doc.save(caminho_arquivo)
    print(f"[OK] Documento Word ABNT com screenshots gerado com sucesso: {caminho_arquivo}")

if __name__ == "__main__":
    caminho = r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs\SisVendas_Documentacao_ABNT.docx"
    criar_documento_abnt(caminho)
