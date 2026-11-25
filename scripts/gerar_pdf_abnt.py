# -*- coding: utf-8 -*-
"""
Gerador de PDF de Documentação Técnica no Padrão ABNT (NBR 14724)
Sem dependências externas - utiliza especificação PDF 1.4 pura em Python.

Autora: Iasmim José
Projeto: SisVendas - Programação Orientada a Objetos
"""

import sys
import os
import zlib

class PDFCanvas:
    def __init__(self, filename):
        self.filename = filename
        self.objects = []
        self.pages = []
        self.current_stream = []
        
        # A4 em pontos (72 pontos por polegada)
        self.page_width = 595.28
        self.page_height = 841.89
        
        # Margens ABNT: Superior 3cm, Esquerda 3cm, Inferior 2cm, Direita 2cm
        # 1 cm = 28.3465 pontos
        self.margin_left = 3.0 * 28.3465   # ~85 pt
        self.margin_right = 2.0 * 28.3465  # ~56.7 pt
        self.margin_top = 3.0 * 28.3465    # ~85 pt
        self.margin_bottom = 2.0 * 28.3465 # ~56.7 pt
        
        self.content_width = self.page_width - self.margin_left - self.margin_right
        self.cursor_y = self.page_height - self.margin_top
        self.page_number = 1
        self.show_page_number = False

    def new_page(self, show_number=True):
        if self.current_stream:
            self.pages.append((list(self.current_stream), self.page_number, self.show_page_number))
            self.current_stream = []
            self.page_number += 1
            
        self.show_page_number = show_number
        self.cursor_y = self.page_height - self.margin_top

    def escape_text(self, text):
        if not text:
            return ""
        # Substitui caracteres especiais para PDF WinAnsi / Latin-1
        replacements = {
            '\\': '\\\\', '(': '\\(', ')': '\\)',
            '“': '"', '”': '"', '’': "'", '‘': "'", '—': '-', '–': '-',
            '•': '*', '…': '...'
        }
        for k, v in replacements.items():
            text = text.replace(k, v)
        return text

    def draw_text(self, x, y, text, font='F1', size=11, r=0.0, g=0.0, b=0.0):
        t = self.escape_text(text)
        try:
            raw = t.encode('latin-1', 'replace').decode('latin-1')
        except:
            raw = t
        cmd = f"BT /{font} {size} Tf {r:.2f} {g:.2f} {b:.2f} rg {x:.2f} {y:.2f} Td ({raw}) Tj ET"
        self.current_stream.append(cmd)

    def draw_rect(self, x, y, w, h, fill_rgb=None, stroke_rgb=None, line_width=1.0):
        cmd = ""
        if fill_rgb:
            cmd += f"{fill_rgb[0]:.2f} {fill_rgb[1]:.2f} {fill_rgb[2]:.2f} rg "
        if stroke_rgb:
            cmd += f"{stroke_rgb[0]:.2f} {stroke_rgb[1]:.2f} {stroke_rgb[2]:.2f} RG {line_width:.2f} w "
        cmd += f"{x:.2f} {y:.2f} {w:.2f} {h:.2f} re "
        if fill_rgb and stroke_rgb:
            cmd += "B"
        elif fill_rgb:
            cmd += "f"
        elif stroke_rgb:
            cmd += "S"
        self.current_stream.append(cmd)

    def draw_line(self, x1, y1, x2, y2, stroke_rgb=(0,0,0), line_width=1.0):
        cmd = f"{stroke_rgb[0]:.2f} {stroke_rgb[1]:.2f} {stroke_rgb[2]:.2f} RG {line_width:.2f} w {x1:.2f} {y1:.2f} m {x2:.2f} {y2:.2f} l S"
        self.current_stream.append(cmd)

    def add_heading1(self, text):
        if self.cursor_y < self.margin_bottom + 120:
            self.new_page(self.show_page_number)
        else:
            self.cursor_y -= 15
        
        # Barra decorativa lateral azul ABNT
        self.draw_rect(self.margin_left, self.cursor_y - 3, 4, 18, fill_rgb=(0.10, 0.30, 0.50))
        self.draw_text(self.margin_left + 12, self.cursor_y, text, font='F2', size=13, r=0.10, g=0.30, b=0.50)
        self.cursor_y -= 22

    def add_heading2(self, text):
        if self.cursor_y < self.margin_bottom + 80:
            self.new_page(self.show_page_number)
        else:
            self.cursor_y -= 10
        self.draw_text(self.margin_left, self.cursor_y, text, font='F2', size=11.5, r=0.15, g=0.25, b=0.35)
        self.cursor_y -= 18

    def add_paragraph(self, text, indent=True, line_spacing=15, font='F1', size=10.5):
        words = text.split()
        if not words:
            return
            
        start_x = self.margin_left + (25 if indent else 0)
        max_chars = 80 if indent else 85
        
        lines = []
        cur_line = []
        cur_len = 0
        for w in words:
            if cur_len + len(w) + 1 > max_chars:
                lines.append(" ".join(cur_line))
                cur_line = [w]
                cur_len = len(w)
            else:
                cur_line.append(w)
                cur_len += len(w) + 1
        if cur_line:
            lines.append(" ".join(cur_line))

        for idx, line in enumerate(lines):
            if self.cursor_y < self.margin_bottom + 40:
                self.new_page(self.show_page_number)
            x = start_x if idx == 0 else self.margin_left
            self.draw_text(x, self.cursor_y, line, font=font, size=size, r=0.1, g=0.1, b=0.1)
            self.cursor_y -= line_spacing
        self.cursor_y -= 4

    def add_bullet(self, title, desc):
        if self.cursor_y < self.margin_bottom + 45:
            self.new_page(self.show_page_number)
        self.draw_text(self.margin_left + 10, self.cursor_y, "*", font='F2', size=12, r=0.1, g=0.3, b=0.5)
        self.draw_text(self.margin_left + 22, self.cursor_y, title + ": ", font='F2', size=10, r=0.1, g=0.1, b=0.1)
        
        # Descrição com quebra simples
        title_offset = len(title) * 6 + 30
        desc_words = desc.split()
        lines = []
        cur = []
        cur_len = 0
        max_c = 75
        for w in desc_words:
            if cur_len + len(w) + 1 > max_c:
                lines.append(" ".join(cur))
                cur = [w]
                cur_len = len(w)
            else:
                cur.append(w)
                cur_len += len(w) + 1
        if cur:
            lines.append(" ".join(cur))
            
        for i, l in enumerate(lines):
            if i == 0:
                self.draw_text(self.margin_left + 22 + (len(title)*5.5) + 8, self.cursor_y, l, font='F1', size=10, r=0.2, g=0.2, b=0.2)
            else:
                self.cursor_y -= 13
                if self.cursor_y < self.margin_bottom + 40:
                    self.new_page(self.show_page_number)
                self.draw_text(self.margin_left + 22, self.cursor_y, l, font='F1', size=10, r=0.2, g=0.2, b=0.2)
        self.cursor_y -= 16

    def add_box(self, title, items):
        if self.cursor_y < self.margin_bottom + len(items)*18 + 50:
            self.new_page(self.show_page_number)
        box_h = len(items)*16 + 28
        y_top = self.cursor_y
        self.draw_rect(self.margin_left, y_top - box_h, self.content_width, box_h, fill_rgb=(0.96, 0.97, 0.99), stroke_rgb=(0.75, 0.82, 0.90), line_width=1.0)
        self.draw_text(self.margin_left + 10, y_top - 16, title, font='F2', size=10.5, r=0.10, g=0.25, b=0.45)
        self.draw_line(self.margin_left + 10, y_top - 20, self.margin_left + self.content_width - 10, y_top - 20, stroke_rgb=(0.8, 0.85, 0.90), line_width=0.5)
        
        cur_y = y_top - 34
        for it in items:
            self.draw_text(self.margin_left + 15, cur_y, it, font='F3', size=9.5, r=0.15, g=0.15, b=0.15)
            cur_y -= 16
        self.cursor_y = y_top - box_h - 15

    def add_table(self, headers, rows, col_widths):
        table_h = (len(rows) + 1) * 20
        if self.cursor_y < self.margin_bottom + table_h + 30:
            self.new_page(self.show_page_number)
            
        y_pos = self.cursor_y
        
        # Header background
        self.draw_rect(self.margin_left, y_pos - 18, sum(col_widths), 18, fill_rgb=(0.15, 0.35, 0.55))
        
        # Header text
        x_cur = self.margin_left
        for h, w in zip(headers, col_widths):
            self.draw_text(x_cur + 5, y_pos - 13, h, font='F2', size=9.5, r=1.0, g=1.0, b=1.0)
            x_cur += w
        y_pos -= 18
        
        # Rows
        for r_idx, row in enumerate(rows):
            bg = (0.95, 0.97, 1.0) if r_idx % 2 == 1 else (1.0, 1.0, 1.0)
            self.draw_rect(self.margin_left, y_pos - 16, sum(col_widths), 16, fill_rgb=bg, stroke_rgb=(0.85, 0.85, 0.85), line_width=0.5)
            x_cur = self.margin_left
            for cell, w in zip(row, col_widths):
                self.draw_text(x_cur + 5, y_pos - 12, str(cell), font='F1', size=9, r=0.15, g=0.15, b=0.15)
                x_cur += w
            y_pos -= 16
            
        self.cursor_y = y_pos - 15

    def save(self):
        if self.current_stream:
            self.pages.append((list(self.current_stream), self.page_number, self.show_page_number))
            
        # Gera documento PDF
        objects = []
        
        # 1: Catalog, 2: Pages
        objects.append(b"<< /Type /Catalog /Pages 2 0 R >>")
        
        # Fontes: 3: Helvetica, 4: Helvetica-Bold, 5: Courier, 6: Helvetica-Oblique
        font_helv = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"
        font_helv_bold = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"
        font_cour = b"<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"
        font_helv_oblique = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Oblique /Encoding /WinAnsiEncoding >>"
        
        # IDs dos objetos:
        # 1: Catalog
        # 2: Pages
        # 3: F1 (Helvetica)
        # 4: F2 (Helvetica-Bold)
        # 5: F3 (Courier)
        # 6: F4 (Helvetica-Oblique)
        # Páginas começam em 7.
        num_pages = len(self.pages)
        page_obj_ids = [7 + i*2 for i in range(num_pages)]
        content_obj_ids = [8 + i*2 for i in range(num_pages)]
        
        kids_str = " ".join([f"{pid} 0 R" for pid in page_obj_ids])
        pages_obj = f"<< /Type /Pages /Kids [{kids_str}] /Count {num_pages} >>".encode('latin-1')
        
        objects.append(pages_obj)
        objects.append(font_helv)
        objects.append(font_helv_bold)
        objects.append(font_cour)
        objects.append(font_helv_oblique)
        
        for idx, (stream_cmds, p_num, show_num) in enumerate(self.pages):
            page_id = page_obj_ids[idx]
            content_id = content_obj_ids[idx]
            
            # Adiciona numeração de página no canto superior direito conforme ABNT
            if show_num and p_num > 2:
                stream_cmds.append(f"BT /F1 9 Tf 0.4 0.4 0.4 rg {self.page_width - self.margin_right - 20:.2f} {self.page_height - 35:.2f} Td ({p_num}) Tj ET")
                stream_cmds.append(f"0.85 0.85 0.85 RG 0.5 w {self.margin_left:.2f} {self.page_height - 42:.2f} m {self.page_width - self.margin_right:.2f} {self.page_height - 42:.2f} l S")
                stream_cmds.append(f"BT /F4 8 Tf 0.5 0.5 0.5 rg {self.margin_left:.2f} {self.page_height - 35:.2f} Td (SisVendas - Relatorio Tecnico ABNT) Tj ET")
                # Rodapé
                stream_cmds.append(f"0.85 0.85 0.85 RG 0.5 w {self.margin_left:.2f} 45 m {self.page_width - self.margin_right:.2f} 45 l S")
                stream_cmds.append(f"BT /F1 8 Tf 0.5 0.5 0.5 rg {self.margin_left:.2f} 32 Td (Programacao Orientada a Objetos - Avaliacao A2 - Autora: Iasmim Jose) Tj ET")

            stream_data = "\n".join(stream_cmds).encode('latin-1', 'replace')
            compressed = zlib.compress(stream_data)
            
            page_dict = f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {self.page_width:.2f} {self.page_height:.2f}] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R /F4 6 0 R >> >> /Contents {content_id} 0 R >>".encode('latin-1')
            content_dict = f"<< /Length {len(compressed)} /Filter /FlateDecode >>\nstream\n".encode('latin-1') + compressed + b"\nendstream"
            
            objects.append(page_dict)
            objects.append(content_dict)
            
        header = b"%PDF-1.4\n%\xe2\xe3\xcf\xd3\n"
        offsets = [0]
        cur_offset = len(header)
        body = []
        
        for i, obj in enumerate(objects):
            obj_bytes = f"{i+1} 0 obj\n".encode('latin-1') + obj + b"\nendobj\n"
            offsets.append(cur_offset)
            cur_offset += len(obj_bytes)
            body.append(obj_bytes)
            
        xref = f"xref\n0 {len(offsets)}\n0000000000 65535 f \n"
        for o in offsets[1:]:
            xref += f"{o:010d} 00000 n \n"
            
        trailer = f"trailer\n<< /Size {len(offsets)} /Root 1 0 R >>\nstartxref\n{cur_offset}\n%%EOF\n"
        
        os.makedirs(os.path.dirname(self.filename), exist_ok=True)
        with open(self.filename, 'wb') as f:
            f.write(header + b"".join(body) + xref.encode('latin-1') + trailer.encode('latin-1'))
        print(f"[OK] PDF ABNT gerado com sucesso: {self.filename} ({num_pages} paginas)")


def gerar_relatorio_abnt(caminho_pdf):
    doc = PDFCanvas(caminho_pdf)
    
    # ==========================================
    # PÁGINA 1: CAPA (Conforme ABNT NBR 14724)
    # ==========================================
    doc.new_page(show_number=False)
    
    # Cabeçalho da Instituição
    doc.draw_text(doc.margin_left, 760, "CENTRO UNIVERSITARIO - FACULDADE DE TECNOLOGIA", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left, 745, "CURSO DE CIENCIA DA COMPUTACAO / ANALISE E DESENVOLVIMENTO DE SISTEMAS", font='F1', size=9.5, r=0.3, g=0.3, b=0.3)
    doc.draw_text(doc.margin_left, 730, "DISCIPLINA: PROGRAMACAO ORIENTADA A OBJETOS (POO)", font='F2', size=10, r=0.1, g=0.3, b=0.5)
    
    doc.draw_line(doc.margin_left, 720, doc.page_width - doc.margin_right, 720, stroke_rgb=(0.2, 0.4, 0.6), line_width=1.5)
    
    # Autora
    doc.draw_text(doc.margin_left, 660, "AUTORA: IASMIM JOSE", font='F2', size=13, r=0.15, g=0.15, b=0.15)
    
    # Título Central
    doc.draw_rect(doc.margin_left, 450, doc.content_width, 110, fill_rgb=(0.95, 0.97, 1.0), stroke_rgb=(0.2, 0.4, 0.6), line_width=1.0)
    doc.draw_text(doc.margin_left + 20, 525, "SISTEMA DE GESTAO COMERCIAL (SISVENDAS)", font='F2', size=16, r=0.10, g=0.30, b=0.50)
    doc.draw_text(doc.margin_left + 20, 502, "Desenvolvimento de Sistema de Informacao em Java com Orientacao a Objetos", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left + 20, 480, "CRUD Completo, Tela de Negocio, Base Simulada e Interfaces .frm / .form", font='F4', size=10, r=0.4, g=0.4, b=0.4)
    doc.draw_text(doc.margin_left + 20, 465, "Trabalho Academico - Avaliacao A2", font='F2', size=10, r=0.1, g=0.3, b=0.5)
    
    # Cidade e Ano no rodapé da capa
    doc.draw_text(doc.margin_left + 180, 100, "BRASIL", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left + 185, 85, "2026", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    
    # ==========================================
    # PÁGINA 2: FOLHA DE ROSTO E SUMÁRIO
    # ==========================================
    doc.new_page(show_number=False)
    
    doc.draw_text(doc.margin_left, 760, "IASMIM JOSE", font='F2', size=12, r=0.15, g=0.15, b=0.15)
    doc.draw_text(doc.margin_left, 690, "SISTEMA DE GESTAO COMERCIAL (SISVENDAS)", font='F2', size=14, r=0.10, g=0.30, b=0.50)
    
    # Nota de Apresentação ABNT à direita
    nota_x = doc.margin_left + 160
    nota_w = doc.content_width - 160
    doc.draw_rect(nota_x - 5, 595, nota_w + 10, 75, fill_rgb=(0.98, 0.98, 0.98), stroke_rgb=(0.85, 0.85, 0.85), line_width=0.5)
    doc.draw_text(nota_x, 655, "Trabalho pratico apresentado a disciplina de", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 642, "Programacao Orientada a Objetos como requisito", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 629, "parcial para avaliacao da A2.", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 612, "Entrega individual via Microsoft Teams.", font='F2', size=9, r=0.1, g=0.3, b=0.5)
    
    doc.draw_line(doc.margin_left, 570, doc.page_width - doc.margin_right, 570, stroke_rgb=(0.8, 0.8, 0.8), line_width=0.8)
    
    # Sumário ABNT
    doc.cursor_y = 540
    doc.add_heading1("SUMARIO")
    
    sumario_itens = [
        ("1. INTRODUCAO E CONTEXTUALIZACAO DO MINI-MUNDO", "3"),
        ("2. REQUISITOS DO SISTEMA DE INFORMACAO", "3"),
        ("3. MODELAGEM ORIENTADA A OBJETOS (CLASSES E DIAGRAMAS)", "4"),
        ("4. ARQUITETURA E PADROES DE PROJETO (MVC, DAO, SINGLETON)", "5"),
        ("5. IMPLEMENTACAO DO CRUD DAS CLASSES DE DOMINIO", "6"),
        ("6. IMPLEMENTACAO DA TELA DE NEGOCIO (FLUXO TRANSACIONAL)", "7"),
        ("7. SIMULACAO DE BASE DE DADOS E PERSISTENCIA EM TABELAS", "8"),
        ("8. INTERFACES GRAFICAS SWING E ARQUIVOS .FRM / .FORM", "9"),
        ("9. GUIA DE EXECUCAO E ROTEIRO DE TESTES", "10"),
        ("10. CONCLUSAO", "11"),
        ("11. REFERENCIAS BIBLIOGRAFICAS (ABNT NBR 6023)", "11"),
    ]
    
    for tit, pg in sumario_itens:
        doc.draw_text(doc.margin_left + 5, doc.cursor_y, tit, font='F2' if tit.startswith(('1.', '3.', '6.', '10.')) else 'F1', size=9.5, r=0.15, g=0.15, b=0.15)
        doc.draw_text(doc.page_width - doc.margin_right - 20, doc.cursor_y, pg, font='F2', size=9.5, r=0.1, g=0.3, b=0.5)
        # Pontilhado
        doc.draw_line(doc.margin_left + len(tit)*5.4 + 10, doc.cursor_y + 2, doc.page_width - doc.margin_right - 30, doc.cursor_y + 2, stroke_rgb=(0.7, 0.7, 0.7), line_width=0.5)
        doc.cursor_y -= 18

    # ==========================================
    # PÁGINA 3: INTRODUÇÃO E REQUISITOS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("1. INTRODUCAO E CONTEXTUALIZACAO DO MINI-MUNDO")
    
    doc.add_paragraph("O presente trabalho academico tem como objetivo o desenvolvimento de um Sistema de Informacao completo na linguagem Java, aplicando os principios da Programacao Orientada a Objetos (POO). O mini-mundo escolhido modela a operacao de uma distribuidora de tecnologia e suprimentos, denominada SisVendas.")
    doc.add_paragraph("A solucao foi projetada para atender integralmente aos requisitos solicitados na avaliacao A2: presenca de classes de dominio bem estruturadas, operacoes completas de CRUD (Inclusao, Consulta, Alteracao e Exclusao), uma Tela de Negocio funcional e transacional, simulacao de persistencia em tabela/base de dados e integracao de interface grafica atraves de arquivos com extensao .frm e .form.")

    doc.add_heading1("2. REQUISITOS DO SISTEMA DE INFORMACAO")
    doc.add_heading2("2.1. Requisitos Funcionais (RF)")
    doc.add_bullet("RF01 - Manutencao de Clientes", "Cadastro, busca textual, edicao e remocao de clientes com validacao de CPF e dados obrigatorios.")
    doc.add_bullet("RF02 - Manutencao de Produtos", "Cadastro de produtos, categorizacao, controle de precos e controle de estoque minimo.")
    doc.add_bullet("RF03 - Tela de Negocio (Vendas)", "Selecao de cliente, montagem de carrinho dinamicamente e emissao de pedidos.")
    doc.add_bullet("RF04 - Validacao em Tempo Real", "Verificacao automatica de disponibilidade de saldo de estoque antes de cada inclusao de item.")
    doc.add_bullet("RF05 - Calculo de Descontos", "Calculo automatico de descontos por modalidade (PIX e Dinheiro com 5%) ou desconto negociado.")
    doc.add_bullet("RF06 - Baixa e Estorno de Estoque", "Debito no fechamento do pedido e estorno em caso de cancelamento.")
    doc.add_bullet("RF07 - Cupom Fiscal / Comprovante", "Geracao de cupom textual com todos os detalhes da transacao comercial.")
    doc.add_bullet("RF08 - Persistencia Simulada", "Armazenamento em colecoes sincronizadas com arquivos tabulares CSV.")

    doc.add_heading2("2.2. Requisitos Nao Funcionais (RNF)")
    doc.add_bullet("RNF01 - Portabilidade", "Execucao em qualquer maquina com Java SE (JDK 8 ou superior).")
    doc.add_bullet("RNF02 - Usabilidade e Interface", "GUI desktop com Java Swing e arquivos .frm (.form) com feedback claro.")
    doc.add_bullet("RNF03 - Arquitetura em Camadas", "Separacao de responsabilidades em Model, DAO, Service e View.")

    # ==========================================
    # PÁGINA 4: MODELAGEM OO (CLASSES E DIAGRAMAS)
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("3. MODELAGEM ORIENTADA A OBJETOS")
    doc.add_paragraph("A arquitetura de objetos foi construida sobre os pilares fundamentais da POO: Abstracao, Encapsulamento, Heranca e Polimorfismo, alem de relacoes de composicao e agregacao.")
    
    doc.add_heading2("3.1. Detalhamento das Classes de Dominio")
    
    headers_dom = ["Classe", "Tipo / Relacao", "Atributos Principais", "Metodos de Destaque"]
    rows_dom = [
        ["EntidadeBase", "Classe Abstrata (Base)", "id, dataCriacao, dataModif.", "getId(), setId(), validar() [abstrato]"],
        ["Cliente", "Herda EntidadeBase", "nome, cpf, email, tel, cidade", "validar(), formatarCpf(), toString()"],
        ["Produto", "Herda EntidadeBase", "codigo, nome, preco, estoque", "debitarEstoque(), creditarEstoque()"],
        ["ItemPedido", "Agregacao/Composicao", "produto, quantidade, precoUnit", "getSubtotal(), toString()"],
        ["Pedido", "Herda EntidadeBase", "cliente, itens, formaPagto, total", "adicionarItem(), finalizarVenda()"],
        ["FormaPagto", "Enumeracao (Enum)", "DINHEIRO, PIX, CARTAO_DEB/CRED", "getDescontoPadrao()"],
        ["StatusPedido", "Enumeracao (Enum)", "PENDENTE, FINALIZADO, CANCELADO", "getDescricao()"]
    ]
    doc.add_table(headers_dom, rows_dom, [70, 110, 150, 120])
    
    doc.add_heading2("3.2. Estrutura de Heranca e Polimorfismo")
    doc.add_paragraph("A classe abstrata EntidadeBase centraliza o controle temporal e de chave primaria, enquanto o metodo abstrato validar() impoe a cada entidade a responsabilidade polimorfica de validar suas proprias regras de negocio.")
    
    doc.add_box("Especificacao das Regras de Negocio na Modelagem:", [
        "1. Cliente.validar(): Garante que o nome esteja preenchido e que o CPF contenha 11 digitos validos.",
        "2. Produto.validar(): Exige preco unitario estritamente maior que zero e estoque nao negativo.",
        "3. Produto.debitarEstoque(qtd): Operacao sincronizada que lanca excecao caso estoque < qtd.",
        "4. Pedido.adicionarItem(p, qtd): Agrupa automaticamente itens repetidos e recalcula totais.",
        "5. Pedido.finalizarVenda(): Executa a transacao debitando os saldos fisicos de cada produto."
    ])

    # ==========================================
    # PÁGINA 5: ARQUITETURA E DESIGN PATTERNS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("4. ARQUITETURA E PADROES DE PROJETO")
    doc.add_paragraph("O SisVendas adota uma arquitetura em camadas bem delimitadas, promovendo baixo acoplamento e alta coesao entre seus componentes:")
    
    doc.add_bullet("Camada Model (Dominio)", "Encapsula o estado, as regras de integridade e os comportamentos dos objetos de negocio.")
    doc.add_bullet("Camada DAO (Persistencia)", "Abstrai os acessos a base de dados simulada atraves da interface generica DAO<T>.")
    doc.add_bullet("Camada Service (Negocio)", "Contem as regras transacionais, orquestrando gravacoes, consultas e validacoes complexas.")
    doc.add_bullet("Camada View (Apresentacao)", "Interfaces graficas Java Swing acompanhadas dos arquivos de definicao .frm.")

    doc.add_heading2("4.1. Padroes de Projeto Implementados")
    doc.add_paragraph("1. Padrão Data Access Object (DAO): A interface genérica DAO<T extends EntidadeBase> define as operacoes padronizadas de CRUD (salvar, atualizar, excluir, buscarPorId e listarTodos). Suas implementacoes (ClienteDAO, ProdutoDAO e PedidoDAO) permitem que a camada de servico opere de forma totalmente desacoplada do mecanismo de persistencia.")
    doc.add_paragraph("2. Padrão Singleton: A classe BancoDadosMemoria implementa o padrao Singleton para garantir a existencia de um unico ponto central de acesso e gerenciamento de tabelas em memoria e arquivos durante todo o ciclo de vida da aplicacao.")

    doc.add_box("Diagrama Arquitetural de Camadas:", [
        "[ VIEW / .FRM ]  -->  TelaPrincipal, TelaCliente, TelaProduto, TelaVenda",
        "        | ",
        "[ SERVICE ]       -->  ClienteService, ProdutoService, VendaService",
        "        | ",
        "[ DAO INTERFACE ] -->  DAO<T>  <--  ClienteDAO, ProdutoDAO, PedidoDAO",
        "        | ",
        "[ PERSISTENCIA ]  -->  BancoDadosMemoria (Singleton) <--> Arquivos CSV"
    ])

    # ==========================================
    # PÁGINA 6: IMPLEMENTAÇÃO DOS CRUDS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("5. IMPLEMENTACAO DAS OPERACOES DE CRUD")
    doc.add_paragraph("O sistema desenvolve operacoes de CRUD completas e independentes para todas as classes de dominio principais, acessiveis atraves do menu ou barra de ferramentas da tela principal.")
    
    doc.add_heading2("5.1. CRUD de Clientes (TelaCliente.java / .frm)")
    doc.add_bullet("Inclusao", "Preenchimento dos campos cadastrais com geracao automatica de ID incremental e validacao de unicidade de CPF.")
    doc.add_bullet("Consulta / Filtro", "Campo de busca por nome com atualizacao imediata da JTable e exibicao de todos os clientes registrados.")
    doc.add_bullet("Alteracao", "Carregamento automatico dos dados do cliente selecionado na grade para os campos do formulario, permitindo edicao e regravacao.")
    doc.add_bullet("Exclusao", "Remocao com caixa de confirmacao e verificacao de integridade (bloqueia remocao se houver pedidos vinculados).")

    doc.add_heading2("5.2. CRUD de Produtos (TelaProduto.java / .frm)")
    doc.add_bullet("Inclusao", "Cadastro de produtos informando codigo/SKU, descricao, categoria em combobox, preco unitario e estoque inicial.")
    doc.add_bullet("Consulta / Filtro", "Listagem tabular completa com mecanismo de busca por nome, codigo ou categoria.")
    doc.add_bullet("Alteracao", "Edicao agil de precos e reposicao manual de saldo de estoque.")
    doc.add_bullet("Exclusao", "Exclusao controlada que impede a remocao de produtos que compoem o historico de vendas.")
    doc.add_bullet("Alerta Visual", "Destaque automatico na coluna 'Situacao' indicando 'ESTOQUE BAIXO' quando a quantidade for inferior ao estoque minimo.")

    # ==========================================
    # PÁGINA 7: IMPLEMENTAÇÃO DA TELA DE NEGÓCIO
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("6. IMPLEMENTACAO DA TELA DE NEGOCIO (VENDAS)")
    doc.add_paragraph("A Tela de Negocio (TelaVenda.java e TelaVenda.frm) representa o fluxo transacional central do SisVendas, integrando cliente, produtos, estoque, calculos financeiros e emissao de documentos.")

    doc.add_heading2("6.1. Fluxo de Execucao da Operacao Comercial")
    doc.add_paragraph("1. Selecao do Cliente: O operador escolhe o cliente cadastrado em combobox. O sistema exibe instantaneamente seus dados de CPF, telefone e localizacao.")
    doc.add_paragraph("2. Selecao de Produtos e Validacao de Estoque: O operador seleciona o produto desejado e informa a quantidade. O sistema consulta em tempo real o saldo fisico da base de dados e impede a adicao de quantidades superiores ao disponivel.")
    doc.add_paragraph("3. Montagem do Carrinho de Compras: Os produtos adicionados sao listados em uma grade JTable interativa com colunas de item, codigo, descricao, valor unitario, quantidade e subtotal.")
    doc.add_paragraph("4. Aplicacao de Descontos e Pagamento: O operador define a forma de pagamento (Dinheiro, PIX, Cartao). Descontos promocionais sao aplicados automaticamente (ex.: 5% a vista) ou mediante insercao manual de valor negociado.")
    doc.add_paragraph("5. Conclusao Transacional e Baixa de Estoque: Ao acionar 'Concluir Venda', o metodo VendaService.realizarVenda() debita o estoque de cada produto na base simulada, salva o pedido finalizado e abre o Cupom Fiscal emitido.")

    doc.add_box("Exemplo de Cupom Fiscal Emitido na Tela de Negocio:", [
        "====================================================",
        "                 SISVENDAS - CUPOM FISCAL           ",
        "====================================================",
        "Pedido: PED-1002        Data: 25/11/2026 14:30:15   ",
        "Status: Finalizado                                  ",
        "CLIENTE: Carlos Eduardo Silva (CPF: 234.567.890-12) ",
        "ITEM DESCRICAO              QTD  UNIT(R$)  TOTAL(R$)",
        "----------------------------------------------------",
        "1    Mouse Sem Fio Logitech   2    129.90    259.80 ",
        "2    Teclado Mecanico RGB     1    349.50    349.50 ",
        "====================================================",
        "Subtotal Bruto:                     R$     609.30   ",
        "Desconto PIX (5%):                  R$      30.47   ",
        "TOTAL LIQUIDO:                      R$     578.83   ",
        "Forma de Pagamento: PIX (5% desc.)                  "
    ])

    # ==========================================
    # PÁGINA 8: SIMULAÇÃO DE BANCO DE DADOS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("7. SIMULACAO DE BASE DE DADOS E PERSISTENCIA EM TABELAS")
    doc.add_paragraph("Conforme o enunciado 'O sistema devera simular uma base de dados para persistir as informacoes, atraves de em uma tabela', a persistencia foi implementada em duas dimensoes complementares:")

    doc.add_heading2("7.1. Tabelas em Memoria e Estrutura Relacional")
    doc.add_paragraph("A classe BancoDadosMemoria armazena tabelas baseadas em LinkedHashMap<Integer, Entidade>, indexadas por chaves primarias com geradores atomicos de sequencia (AtomicInteger):")
    doc.add_bullet("TB_CLIENTES", "Armazena os registros de clientes com chave primaria (ID).")
    doc.add_bullet("TB_PRODUTOS", "Armazena o catalogo com saldos de estoque e precos.")
    doc.add_bullet("TB_PEDIDOS", "Armazena o cabecalho das vendas e a relacao 1..N com itens.")

    doc.add_heading2("7.2. Persistencia Fisica em Arquivos CSV")
    doc.add_paragraph("Para que as informacoes nao sejam perdidas ao fechar o programa, o sistema sincroniza automaticamente todas as alteracoes em arquivos tabulares com delimitador ';' na pasta dados/:")
    doc.add_bullet("clientes.csv", "ID;NOME;CPF;EMAIL;TELEFONE;LOGRADOURO;CIDADE")
    doc.add_bullet("produtos.csv", "ID;CODIGO;NOME;CATEGORIA;PRECO_UNITARIO;QUANTIDADE_ESTOQUE")
    doc.add_bullet("vendas.csv", "ID;CODIGO;CLIENTE_ID;DATA;TOTAL;FORMA_PAGTO;STATUS;QTD_ITENS")

    doc.add_box("Dados Pre-carregados para Teste Imediato (Seed):", [
        "* Clientes Iniciais: Ana Clara Souza, Carlos Silva, Mariana Oliveira, etc.",
        "* Produtos Iniciais: Notebook Dell i5, Mouse Logitech, Teclado RGB, SSD 1TB, etc.",
        "* Vendas Iniciais: Pedido modelo PED-1001 pre-cadastrado no historico."
    ])

    # ==========================================
    # PÁGINA 9: INTERFACE GRÁFICA E ARQUIVOS .FRM
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("8. INTERFACES GRAFICAS E ARQUIVOS .FRM")
    doc.add_paragraph("O requisito 'Devera conter um arquivo .frm (interface grafica)' foi plenamente contemplado. No ecossistema de desenvolvimento Java e ambientes academicos, a extensao .frm representa as definicoes declarativas de formularios visuais.")
    
    doc.add_heading2("8.1. Arquivos de Formularios Fornecidos")
    headers_frm = ["Formulario", "Arquivo .FRM", "Arquivo .FORM (NetBeans)", "Finalidade"]
    rows_frm = [
        ["TelaPrincipal", "TelaPrincipal.frm", "TelaPrincipal.form", "Menu geral e Dashboard de metricas"],
        ["TelaCliente", "TelaCliente.frm", "TelaCliente.form", "Formulario e tabela do CRUD de Clientes"],
        ["TelaProduto", "TelaProduto.frm", "TelaProduto.form", "Formulario e tabela do CRUD de Produtos"],
        ["TelaVenda", "TelaVenda.frm", "TelaVenda.form", "Tela de Negocio de Venda e Carrinho"],
        ["TelaConsulta", "TelaConsultaVendas.frm", "TelaConsultaVendas.form", "Auditoria e cancelamento de vendas"]
    ]
    doc.add_table(headers_frm, rows_frm, [85, 105, 120, 140])

    doc.add_heading2("8.2. Caracteristicas Visuais dos Formularios Swing")
    doc.add_bullet("Look and Feel", "Configurado prioritariamente para Nimbus ou Native OS para aspecto profissional.")
    doc.add_bullet("Organizacao de Layout", "Uso inteligente de BorderLayout, GridBagLayout e FlowLayout com espacamentos consistentes.")
    doc.add_bullet("Interatividade Tabular", "Selecao de linhas em JTable atualiza automaticamente os formularios.")

    # ==========================================
    # PÁGINA 10: GUIA DE TESTES E EXECUÇÃO
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("9. GUIA DE EXECUCAO E ROTEIRO DE TESTES")
    doc.add_paragraph("Para facilitar a correcao e validacao pelo professor avaliador, o projeto conta com facilidades de execucao direta em qualquer plataforma:")

    doc.add_heading2("9.1. Execucao pelo Windows (Scripts Batch)")
    doc.add_paragraph("1. Duplo clique em 'run.bat': O script compila automaticamente os fontes em 'bin/' e inicia a aplicacao.")
    doc.add_paragraph("2. Em caso de necessidade de recompilacao limpa, execute 'build.bat'.")

    doc.add_heading2("9.2. Execucao em IDEs (NetBeans / Eclipse / VS Code)")
    doc.add_paragraph("Abra a pasta 'sistema-vendas-poo' como projeto Maven ou projeto padrao Java. Execute a classe principal 'br.com.sisvendas.Main'.")

    doc.add_heading2("9.3. Roteiro de Validacao Recomendado")
    doc.add_box("Passo a Passo de Validacao pelo Avaliador:", [
        "Passo 1: Abrir a TelaPrincipal e conferir os 4 cartoes de metricas do Dashboard.",
        "Passo 2: Clicar em 'Clientes' e incluir um novo cliente (testar validacao de CPF).",
        "Passo 3: Clicar em 'Produtos' e verificar os itens pre-carregados e o estoque.",
        "Passo 4: Clicar em 'Nova Venda (Negocio)', escolher o cliente e selecionar 2 produtos.",
        "Passo 5: Observar a validacao de estoque (tente pedir 999 unidades para ver o bloqueio).",
        "Passo 6: Alterar a forma de pagamento para PIX e conferir o calculo do desconto.",
        "Passo 7: Concluir a venda, conferir o cupom fiscal impresso e a baixa automatica de estoque."
    ])

    # ==========================================
    # PÁGINA 11: CONCLUSÃO E REFERÊNCIAS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("10. CONCLUSAO")
    doc.add_paragraph("O projeto SisVendas demonstrou a aplicacao pratica e harmoniosa dos conceitos essenciais da Programacao Orientada a Objetos para a resolucao de problemas reais da engenharia de software.")
    doc.add_paragraph("A modularizacao em camadas, a reutilizacao de codigo por heranca na classe EntidadeBase, a persistencia desacoplada via DAO e a robustez da Tela de Negocio comprovam o dominio sobre os requisitos avaliados na A2. A solucao final entrega um software confiavel, intuitivo, extensivel e plenamente documentado segundo as normas academicas da ABNT.")

    doc.add_heading1("11. REFERENCIAS BIBLIOGRAFICAS (ABNT NBR 6023)")
    doc.add_bullet("ABNT", "ASSOCIACAO BRASILEIRA DE NORMAS TECNICAS. NBR 14724: Informacao e documentacao - Trabalhos academicos - Apresentacao. Rio de Janeiro: ABNT, 2011.")
    doc.add_bullet("ABNT", "ASSOCIACAO BRASILEIRA DE NORMAS TECNICAS. NBR 6023: Informacao e documentacao - Referencias - Elaboracao. Rio de Janeiro: ABNT, 2018.")
    doc.add_bullet("DEITEL", "DEITEL, Paul; DEITEL, Harvey. Java: Como Programar. 10. ed. Sao Paulo: Pearson Education do Brasil, 2016.")
    doc.add_bullet("GAMMA", "GAMMA, Erich et al. Padroes de Projeto: Solucoes Reutilizaveis de Software Orientado a Objetos. Porto Alegre: Bookman, 2000.")
    doc.add_bullet("SIERRA", "SIERRA, Kathy; BATES, Bert. Use a Cabeca! Java. 2. ed. Rio de Janeiro: Alta Books, 2007.")

    doc.save()

if __name__ == "__main__":
    caminho = sys.argv[1] if len(sys.argv) > 1 else "docs/SisVendas_Documentacao_ABNT.pdf"
    gerar_relatorio_abnt(caminho)
