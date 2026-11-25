# -*- coding: utf-8 -*-
"""
Gerador de PDF de Documentação Técnica no Padrão ABNT (NBR 14724)
Com Inclusão de Screenshots de Testes do Sistema (Subtipo XObject / DCTDecode).

Instituição: Universidade Veiga de Almeida (UVA)
Disciplina: Programação Orientada a Objetos (POO)

Grupo:
- IASMIM DA SILVA MANN JOSÉ - 1250109566
- REBECA MACHADO DE LIMA - 1250103657
- SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978
- SILVANA GOMES DA SILVA - 1250109767
"""

import sys
import os
import io
import zlib
from PIL import Image

class PDFCanvas:
    def __init__(self, filename):
        self.filename = filename
        self.pages = []
        self.current_stream = []
        self.current_page_images = []
        self.images = [] # Lista de dicts {name, bytes, w, h}
        
        # A4 em pontos (72 pontos por polegada)
        self.page_width = 595.28
        self.page_height = 841.89
        
        # Margens ABNT: Superior 3cm, Esquerda 3cm, Inferior 2cm, Direita 2cm
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
            self.pages.append((list(self.current_stream), self.page_number, self.show_page_number, list(self.current_page_images)))
            self.current_stream = []
            self.current_page_images = []
            self.page_number += 1
            
        self.show_page_number = show_number
        self.cursor_y = self.page_height - self.margin_top

    def escape_text(self, text):
        if not text:
            return ""
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

    def add_table(self, headers, rows, col_widths):
        table_h = (len(rows) + 1) * 20
        if self.cursor_y < self.margin_bottom + table_h + 30:
            self.new_page(self.show_page_number)
            
        y_pos = self.cursor_y
        self.draw_rect(self.margin_left, y_pos - 18, sum(col_widths), 18, fill_rgb=(0.15, 0.35, 0.55))
        
        x_cur = self.margin_left
        for h, w in zip(headers, col_widths):
            self.draw_text(x_cur + 5, y_pos - 13, h, font='F2', size=9.5, r=1.0, g=1.0, b=1.0)
            x_cur += w
        y_pos -= 18
        
        for r_idx, row in enumerate(rows):
            bg = (0.95, 0.97, 1.0) if r_idx % 2 == 1 else (1.0, 1.0, 1.0)
            self.draw_rect(self.margin_left, y_pos - 16, sum(col_widths), 16, fill_rgb=bg, stroke_rgb=(0.85, 0.85, 0.85), line_width=0.5)
            x_cur = self.margin_left
            for cell, w in zip(row, col_widths):
                self.draw_text(x_cur + 5, y_pos - 12, str(cell), font='F1', size=9, r=0.15, g=0.15, b=0.15)
                x_cur += w
            y_pos -= 16
            
        self.cursor_y = y_pos - 15

    def add_image(self, img_path, display_w=440, display_h=270, caption=""):
        if not os.path.exists(img_path):
            return
            
        needed_h = display_h + (35 if caption else 10)
        if self.cursor_y < self.margin_bottom + needed_h:
            self.new_page(self.show_page_number)
            
        img = Image.open(img_path).convert('RGB')
        buf = io.BytesIO()
        img.save(buf, format='JPEG', quality=85)
        jpeg_bytes = buf.getvalue()
        
        img_idx = len(self.images) + 1
        xobj_name = f"Im{img_idx}"
        self.images.append({
            'name': xobj_name,
            'bytes': jpeg_bytes,
            'w': img.width,
            'h': img.height,
            'idx': img_idx
        })
        self.current_page_images.append(xobj_name)
        
        x = self.margin_left + (self.content_width - display_w) / 2
        y = self.cursor_y - display_h
        
        # PDF Operator cm: escala e translacao
        cmd = f"q {display_w:.2f} 0 0 {display_h:.2f} {x:.2f} {y:.2f} cm /{xobj_name} Do Q"
        self.current_stream.append(cmd)
        
        # Borda sutil
        self.draw_rect(x, y, display_w, display_h, stroke_rgb=(0.75, 0.82, 0.90), line_width=1.0)
        
        self.cursor_y = y - 14
        if caption:
            cap_w = len(caption) * 5.2
            cap_x = max(self.margin_left, self.margin_left + (self.content_width - cap_w) / 2)
            self.draw_text(cap_x, self.cursor_y, caption, font='F4', size=9, r=0.2, g=0.25, b=0.3)
            self.cursor_y -= 18
        else:
            self.cursor_y -= 8

    def save(self):
        if self.current_stream:
            self.pages.append((list(self.current_stream), self.page_number, self.show_page_number, list(self.current_page_images)))
            
        num_pages = len(self.pages)
        num_images = len(self.images)
        
        # Objetos fixos:
        # 1: Catalog
        # 2: Pages
        # 3: F1 (Helvetica)
        # 4: F2 (Helvetica-Bold)
        # 5: F3 (Courier)
        # 6: F4 (Helvetica-Oblique)
        # 7 .. 7 + num_images - 1: Imagens XObjects
        # Em seguida: Pares de (Page, Content)
        
        first_img_id = 7
        first_page_id = first_img_id + num_images
        
        page_obj_ids = [first_page_id + i*2 for i in range(num_pages)]
        content_obj_ids = [first_page_id + 1 + i*2 for i in range(num_pages)]
        
        objects = []
        
        # 1: Catalog
        objects.append(b"<< /Type /Catalog /Pages 2 0 R >>")
        
        # 2: Pages
        kids_str = " ".join([f"{pid} 0 R" for pid in page_obj_ids])
        pages_obj = f"<< /Type /Pages /Kids [{kids_str}] /Count {num_pages} >>".encode('latin-1')
        objects.append(pages_obj)
        
        # 3-6: Fontes
        objects.append(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>")
        objects.append(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>")
        objects.append(b"<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>")
        objects.append(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Oblique /Encoding /WinAnsiEncoding >>")
        
        # Imagens XObjects
        img_dict_map = {}
        for idx, img_info in enumerate(self.images):
            img_obj_id = first_img_id + idx
            img_dict_map[img_info['name']] = img_obj_id
            stream_data = img_info['bytes']
            header_dict = f"<< /Type /XObject /Subtype /Image /Width {img_info['w']} /Height {img_info['h']} /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length {len(stream_data)} >>\nstream\n".encode('latin-1')
            img_full_obj = header_dict + stream_data + b"\nendstream"
            objects.append(img_full_obj)
            
        # Páginas
        for idx, (stream_cmds, p_num, show_num, p_imgs) in enumerate(self.pages):
            page_id = page_obj_ids[idx]
            content_id = content_obj_ids[idx]
            
            # Cabeçalho / Rodapé ABNT
            if show_num and p_num > 2:
                stream_cmds.append(f"BT /F1 9 Tf 0.4 0.4 0.4 rg {self.page_width - self.margin_right - 20:.2f} {self.page_height - 35:.2f} Td ({p_num}) Tj ET")
                stream_cmds.append(f"0.85 0.85 0.85 RG 0.5 w {self.margin_left:.2f} {self.page_height - 42:.2f} m {self.page_width - self.margin_right:.2f} {self.page_height - 42:.2f} l S")
                stream_cmds.append(f"BT /F4 8 Tf 0.5 0.5 0.5 rg {self.margin_left:.2f} {self.page_height - 35:.2f} Td (Universidade Veiga de Almeida - SisVendas POO - Avaliacao A2) Tj ET")
                # Rodapé
                stream_cmds.append(f"0.85 0.85 0.85 RG 0.5 w {self.margin_left:.2f} 45 m {self.page_width - self.margin_right:.2f} 45 l S")
                stream_cmds.append(f"BT /F1 8 Tf 0.5 0.5 0.5 rg {self.margin_left:.2f} 32 Td (Grupo: Iasmim Jose, Rebeca Lima, Sarah Silva, Silvana Silva) Tj ET")

            stream_data = "\n".join(stream_cmds).encode('latin-1', 'replace')
            compressed = zlib.compress(stream_data)
            
            # Monta dicionario de XObjects da pagina se houver imagens
            xobj_str = ""
            if p_imgs:
                x_entries = " ".join([f"/{xname} {img_dict_map[xname]} 0 R" for xname in p_imgs if xname in img_dict_map])
                xobj_str = f"/XObject << {x_entries} >>"
                
            page_dict = f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {self.page_width:.2f} {self.page_height:.2f}] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R /F4 6 0 R >> {xobj_str} >> /Contents {content_id} 0 R >>".encode('latin-1')
            content_dict = f"<< /Length {len(compressed)} /Filter /FlateDecode >>\nstream\n".encode('latin-1') + compressed + b"\nendstream"
            
            objects.append(page_dict)
            objects.append(content_dict)
            
        header = b"%PDF-1.4\n%\xe2\xe3\xcf\xd3\n"
        offsets = [0]
        cur_offset = len(header)
        body = []
        
        for i, obj in enumerate(objects):
            if isinstance(obj, str):
                obj = obj.encode('latin-1')
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
        print(f"[OK] PDF ABNT com screenshots gerado com sucesso: {self.filename} ({num_pages} paginas)")


def gerar_relatorio_abnt_completo(caminho_pdf):
    doc = PDFCanvas(caminho_pdf)
    pasta_prints = r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs\screenshots"
    
    # ==========================================
    # PÁGINA 1: CAPA (ABNT NBR 14724)
    # ==========================================
    doc.new_page(show_number=False)
    
    doc.draw_text(doc.margin_left, 760, "UNIVERSIDADE VEIGA DE ALMEIDA - UVA", font='F2', size=12, r=0.10, g=0.30, b=0.50)
    doc.draw_text(doc.margin_left, 745, "CURSO DE CIENCIA DA COMPUTACAO / ENGENHARIA DE SOFTWARE", font='F1', size=9.5, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left, 730, "DISCIPLINA: PROGRAMACAO ORIENTADA A OBJETOS (POO)", font='F2', size=10, r=0.1, g=0.3, b=0.5)
    
    doc.draw_line(doc.margin_left, 720, doc.page_width - doc.margin_right, 720, stroke_rgb=(0.2, 0.4, 0.6), line_width=1.5)
    
    doc.draw_text(doc.margin_left, 675, "INTEGRANTES DO GRUPO:", font='F2', size=11, r=0.10, g=0.30, b=0.50)
    doc.draw_text(doc.margin_left, 658, "IASMIM DA SILVA MANN JOSE - RA: 1250109566", font='F2', size=9.5, r=0.15, g=0.15, b=0.15)
    doc.draw_text(doc.margin_left, 644, "REBECA MACHADO DE LIMA - RA: 1250103657", font='F2', size=9.5, r=0.15, g=0.15, b=0.15)
    doc.draw_text(doc.margin_left, 630, "SARAH GABRIELLA VIRGINIA DA SILVA - RA: 1250106978", font='F2', size=9.5, r=0.15, g=0.15, b=0.15)
    doc.draw_text(doc.margin_left, 616, "SILVANA GOMES DA SILVA - RA: 1250109767", font='F2', size=9.5, r=0.15, g=0.15, b=0.15)
    
    doc.draw_rect(doc.margin_left, 430, doc.content_width, 110, fill_rgb=(0.95, 0.97, 1.0), stroke_rgb=(0.2, 0.4, 0.6), line_width=1.0)
    doc.draw_text(doc.margin_left + 20, 505, "SISTEMA DE GESTAO COMERCIAL (SISVENDAS)", font='F2', size=16, r=0.10, g=0.30, b=0.50)
    doc.draw_text(doc.margin_left + 20, 482, "Desenvolvimento de Sistema de Informacao em Java com Orientacao a Objetos", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left + 20, 460, "CRUD Completo, Tela de Negocio, Base Simulada e Prints dos Resultados", font='F4', size=10, r=0.4, g=0.4, b=0.4)
    doc.draw_text(doc.margin_left + 20, 445, "Trabalho Academico - Avaliacao A2", font='F2', size=10, r=0.1, g=0.3, b=0.5)
    
    doc.draw_text(doc.margin_left + 150, 100, "RIO DE JANEIRO - RJ", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left + 190, 85, "2026", font='F2', size=11, r=0.2, g=0.2, b=0.2)
    
    # ==========================================
    # PÁGINA 2: FOLHA DE ROSTO E SUMÁRIO
    # ==========================================
    doc.new_page(show_number=False)
    
    doc.draw_text(doc.margin_left, 780, "INTEGRANTES DO GRUPO:", font='F2', size=9.5, r=0.10, g=0.30, b=0.50)
    doc.draw_text(doc.margin_left, 767, "IASMIM DA SILVA MANN JOSE (1250109566)", font='F1', size=8.5, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left, 755, "REBECA MACHADO DE LIMA (1250103657)", font='F1', size=8.5, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left, 743, "SARAH GABRIELLA VIRGINIA DA SILVA (1250106978)", font='F1', size=8.5, r=0.2, g=0.2, b=0.2)
    doc.draw_text(doc.margin_left, 731, "SILVANA GOMES DA SILVA (1250109767)", font='F1', size=8.5, r=0.2, g=0.2, b=0.2)
    
    doc.draw_text(doc.margin_left, 690, "SISTEMA DE GESTAO COMERCIAL (SISVENDAS)", font='F2', size=14, r=0.10, g=0.30, b=0.50)
    
    nota_x = doc.margin_left + 150
    nota_w = doc.content_width - 150
    doc.draw_rect(nota_x - 5, 595, nota_w + 10, 75, fill_rgb=(0.98, 0.98, 0.98), stroke_rgb=(0.85, 0.85, 0.85), line_width=0.5)
    doc.draw_text(nota_x, 655, "Trabalho academico apresentado a Universidade", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 642, "Veiga de Almeida (UVA) como requisito parcial", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 629, "para obtencao de nota na Avaliacao A2.", font='F1', size=9, r=0.3, g=0.3, b=0.3)
    doc.draw_text(nota_x, 612, "Disciplina: Programacao Orientada a Objetos.", font='F2', size=9, r=0.1, g=0.3, b=0.5)
    
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
        ("9. EVIDENCIAS DE TESTES DO SISTEMA (PRINTS DAS TELAS)", "10"),
        ("10. GUIA DE EXECUCAO E ROTEIRO DE TESTES", "13"),
        ("11. CONCLUSAO", "14"),
        ("12. REFERENCIAS BIBLIOGRAFICAS (ABNT NBR 6023)", "14"),
    ]
    
    for tit, pg in sumario_itens:
        doc.draw_text(doc.margin_left + 5, doc.cursor_y, tit, font='F2' if tit.startswith(('1.', '3.', '6.', '9.', '11.')) else 'F1', size=9.5, r=0.15, g=0.15, b=0.15)
        doc.draw_text(doc.page_width - doc.margin_right - 20, doc.cursor_y, pg, font='F2', size=9.5, r=0.1, g=0.3, b=0.5)
        doc.draw_line(doc.margin_left + len(tit)*5.4 + 10, doc.cursor_y + 2, doc.page_width - doc.margin_right - 30, doc.cursor_y + 2, stroke_rgb=(0.7, 0.7, 0.7), line_width=0.5)
        doc.cursor_y -= 17

    # ==========================================
    # PÁGINAS DE CONTEÚDO TÉCNICO
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("1. INTRODUCAO E CONTEXTUALIZACAO DO MINI-MUNDO")
    doc.add_paragraph("O presente trabalho academico foi desenvolvido pelo grupo de discentes da Universidade Veiga de Almeida (UVA) para a disciplina de Programacao Orientada a Objetos (POO), integrando a avaliacao oficial A2. O software concebido, denominado SisVendas, modela as operacoes de comercializacao e atendimento de uma distribuidora de tecnologia.")
    doc.add_paragraph("A solucao cumpre integralmente os requisitos solicitados: presenca de tres classes de dominio integradas (Cliente, Produto, Pedido), operacoes de CRUD completas para todas as entidades, uma Tela de Negocio transacional para emissao de vendas, base de dados simulada para persistencia de informacoes em tabelas e arquivos de interface grafica (.frm e .form).")

    doc.add_heading1("2. REQUISITOS DO SISTEMA DE INFORMACAO")
    doc.add_heading2("2.1. Requisitos Funcionais (RF)")
    doc.add_bullet("RF01 - CRUD de Clientes", "Cadastro, busca textual, alteracao e exclusao com validacao de CPF e campos obrigatorios.")
    doc.add_bullet("RF02 - CRUD de Produtos", "Gerenciamento de catalogo, controle de precos, estoque fisico e monitoramento de estoque minimo.")
    doc.add_bullet("RF03 - Tela de Negocio (Vendas)", "Selecao de cliente, carrinho dinamico, validacao de saldo em tempo real e emissao de pedidos.")
    doc.add_bullet("RF04 - Validacao em Tempo Real", "Bloqueio automatico de adicao de produtos quando a quantidade solicitada exceder o estoque fisico.")
    doc.add_bullet("RF05 - Calculo de Descontos", "Desconto financeiro automatico conforme a forma de pagamento (PIX e Dinheiro com 5%) ou desconto negociado.")
    doc.add_bullet("RF06 - Baixa e Estorno de Estoque", "Debito imediato ao concluir a venda e estorno automatico em caso de cancelamento.")
    doc.add_bullet("RF07 - Cupom Fiscal / Comprovante", "Emissao de comprovante fiscal detalhado com itens, valores e descontos aplicados.")
    doc.add_bullet("RF08 - Persistencia em Tabelas", "Armazenamento em colecoes relacionais sincronizadas com arquivos tabulares CSV.")

    # Modelagem
    doc.new_page(show_number=True)
    doc.add_heading1("3. MODELAGEM ORIENTADA A OBJETOS (POO)")
    doc.add_paragraph("A arquitetura do SisVendas foi construida sobre os pilares fundamentais da Orientacao a Objetos:")
    doc.add_bullet("Abstracao e Encapsulamento", "Atributos privados e protegidos acessados por getters e setters que possuem validacoes de consistencia.")
    doc.add_bullet("Heranca", "Classe abstrata EntidadeBase compartilhada por Cliente, Produto e Pedido para controle de IDs e auditoria temporal.")
    doc.add_bullet("Polimorfismo", "Metodo abstrato validar() implementado de forma personalizada em cada classe de dominio.")
    doc.add_bullet("Composicao e Agregacao", "O Pedido compoe ItemPedido (ciclo de vida conjunto), que por sua vez agrega Produto (ciclo autonomo).")

    headers_dom = ["Classe", "Tipo / Relacao", "Atributos Principais", "Metodos de Destaque"]
    rows_dom = [
        ["EntidadeBase", "Abstrata (Raiz)", "id, dataCriacao, dataModif.", "validar() [abstrato], getId(), setId()"],
        ["Cliente", "Herda EntidadeBase", "nome, cpf, email, tel, cidade", "validar(), formatarCpf(), toString()"],
        ["Produto", "Herda EntidadeBase", "codigo, nome, preco, estoque", "debitarEstoque(), creditarEstoque()"],
        ["ItemPedido", "Agregacao/Composicao", "produto, quantidade, precoUnit", "getSubtotal(), toString()"],
        ["Pedido", "Herda EntidadeBase", "cliente, itens, formaPagto, total", "adicionarItem(), finalizarVenda()"],
        ["FormaPagto", "Enumeracao (Enum)", "DINHEIRO, PIX, DEBITO, CREDITO", "getDescontoPadrao()"],
        ["StatusPedido", "Enumeracao (Enum)", "PENDENTE, FINALIZADO, CANCELADO", "getDescricao()"]
    ]
    doc.add_table(headers_dom, rows_dom, [70, 110, 150, 120])

    # Arquitetura
    doc.new_page(show_number=True)
    doc.add_heading1("4. ARQUITETURA E PADROES DE PROJETO")
    doc.add_paragraph("O software foi concebido sob a divisao formal em camadas de responsabilidade:")
    doc.add_bullet("Model", "Entidades de negocio com suas regras de integridade e encapsulamento.")
    doc.add_bullet("DAO", "Acesso aos dados simulados atraves da interface polimorfica DAO<T extends EntidadeBase>.")
    doc.add_bullet("Service", "Regras transacionais de estoque, unicidade e orquestracao.")
    doc.add_bullet("View", "Interfaces graficas Java Swing acompanhadas das especificacoes em arquivos .frm.")
    doc.add_paragraph("Padroes de Projeto Utilizados: Data Access Object (DAO) para desacoplamento de persistencia e Singleton (BancoDadosMemoria) para unicidade de controle das tabelas em memoria e arquivos.")

    # CRUDs e Tela de Negócio
    doc.new_page(show_number=True)
    doc.add_heading1("5. IMPLEMENTACAO DOS CRUDS E TELA DE NEGOCIO")
    doc.add_heading2("5.1. CRUDs de Clientes e Produtos")
    doc.add_paragraph("As telas TelaCliente e TelaProduto disponibilizam formularios para inclusao e edicao de dados, grades tabulares JTable para visualizacao geral, busca textual e exclusao com controle de integridade referencial.")
    
    doc.add_heading2("5.2. Tela de Negocio (Processamento de Vendas)")
    doc.add_paragraph("A Tela de Negocio (TelaVenda) implementa o ciclo comercial completo: o operador seleciona o cliente, inclui itens no carrinho com checagem de saldo em tempo real, define a forma de pagamento (com desconto automatico de 5% no PIX ou Dinheiro) e conclui a venda, acionando a baixa automatica no estoque da base de dados e emitindo o cupom fiscal.")

    doc.add_heading1("6. SIMULACAO DE BASE DE DADOS E PERSISTENCIA")
    doc.add_paragraph("Atendendo ao requisito de simular uma base de dados para persistir as informacoes atraves de tabelas, o sistema gerencia as estruturas TB_CLIENTES, TB_PRODUTOS e TB_PEDIDOS em memoria e sincroniza automaticamente todas as alteracoes em arquivos tabulares CSV (clientes.csv, produtos.csv e vendas.csv) na pasta dados/.")

    # ==========================================
    # SEÇÃO 9: EVIDÊNCIAS DE TESTES E PRINTS DAS TELAS
    # ==========================================
    doc.new_page(show_number=True)
    doc.add_heading1("9. EVIDENCIAS DE TESTES DO SISTEMA (PRINTS DAS TELAS)")
    doc.add_paragraph("A fim de comprovar o funcionamento de todos os modulos, foram realizados testes práticos completos no sistema SisVendas. As capturas de tela a seguir evidenciam os resultados obtidos em cada etapa:")

    doc.add_heading2("9.1. Teste 1: Tela Principal e Dashboard de Indicadores")
    doc.add_paragraph("A Tela Principal centraliza as metricas de desempenho comercial (cards de clientes, produtos, vendas e faturamento) e exibe em grade a tabela de ultimas vendas registradas na base de dados.")
    doc.add_image(os.path.join(pasta_prints, "print_teste1_tela_principal.png"), display_w=440, display_h=270, 
                  caption="Figura 1 - Teste 1: Interface Principal com Dashboard de Indicadores e Tabela de Vendas (UVA)")

    # Print 2: CRUD Clientes
    doc.new_page(show_number=True)
    doc.add_heading2("9.2. Teste 2: CRUD de Clientes (Inclusao, Consulta e Filtro)")
    doc.add_paragraph("Demonstracao do modulo de clientes contendo formulario de inclusao/edicao e tabela de consulta com filtro de busca textual por nome (exemplo: busca por 'Ana').")
    doc.add_image(os.path.join(pasta_prints, "print_teste2_crud_clientes.png"), display_w=440, display_h=265,
                  caption="Figura 2 - Teste 2: CRUD de Clientes - Formulario e Consulta Tabular com Filtro por Nome")

    # Print 3: CRUD Produtos
    doc.new_page(show_number=True)
    doc.add_heading2("9.3. Teste 3: CRUD de Produtos e Alerta de Estoque Baixo")
    doc.add_paragraph("Visualizacao do catalogo de produtos na base de dados com indicacao de precos, categorias e destaque visual automatico 'ESTOQUE BAIXO' para itens com saldo inferior a 5 unidades.")
    doc.add_image(os.path.join(pasta_prints, "print_teste3_crud_produtos.png"), display_w=440, display_h=265,
                  caption="Figura 3 - Teste 3: CRUD de Produtos - Catalogo, Precos e Alerta Visual de Estoque Baixo")

    # Print 4: Tela de Negócio
    doc.new_page(show_number=True)
    doc.add_heading2("9.4. Teste 4: Tela de Negocio (Emissao de Vendas e Carrinho)")
    doc.add_paragraph("Demonstracao da Tela de Negocio com selecao do cliente 'Carlos Eduardo Silva', composicao de itens no carrinho, validacao de estoque em tempo real e aplicacao de 5% de desconto no pagamento via PIX.")
    doc.add_image(os.path.join(pasta_prints, "print_teste4_tela_negocio_venda.png"), display_w=440, display_h=275,
                  caption="Figura 4 - Teste 4: Tela de Negocio - Selecao de Cliente, Carrinho e Validacao em Tempo Real")

    # Print 5: Cupom Fiscal
    doc.new_page(show_number=True)
    doc.add_heading2("9.5. Teste 5: Emissao de Cupom Fiscal e Baixa Automatica de Estoque")
    doc.add_paragraph("Comprovante fiscal gerado automaticamente ao concluir a venda, confirmando o debito das unidades vendidas no estoque fisico da base de dados.")
    doc.add_image(os.path.join(pasta_prints, "print_teste5_cupom_fiscal.png"), display_w=400, display_h=300,
                  caption="Figura 5 - Teste 5: Cupom Fiscal Eletronico Emitido com Sucesso e Baixa de Estoque")

    # Print 6: Histórico
    doc.new_page(show_number=True)
    doc.add_heading2("9.6. Teste 6: Auditoria, Historico e Cancelamento com Estorno")
    doc.add_paragraph("Tela de Consulta e Historico de Vendas com divisao em duas tabelas sincronizadas (vendas no topo e itens embaixo) e botao para estorno de mercadorias.")
    doc.add_image(os.path.join(pasta_prints, "print_teste6_historico_vendas.png"), display_w=440, display_h=260,
                  caption="Figura 6 - Teste 6: Auditoria de Vendas e Mecanismo de Cancelamento com Estorno de Estoque")

    # Guia e Conclusão
    doc.new_page(show_number=True)
    doc.add_heading1("10. GUIA DE EXECUCAO E ROTEIRO DE TESTES")
    doc.add_paragraph("A aplicacao conta com facilidades de execucao direta no Windows:")
    doc.add_paragraph("1. Execucao Imediata: Duplo clique no script run.bat na raiz do projeto. O script compila e inicia o sistema com demonstracao completa no terminal e abertura da interface grafica.")
    doc.add_paragraph("2. Em IDEs (NetBeans / Eclipse / VS Code): Abra a pasta do projeto e execute br.com.sisvendas.Main.")

    doc.add_heading1("11. CONCLUSAO")
    doc.add_paragraph("O projeto SisVendas atingiu integralmente os objetivos pedagogicos da disciplina de Programacao Orientada a Objetos na Universidade Veiga de Almeida (UVA). Os testes praticos realizados e documentados comprovam a robustez das classes de dominio, a eficacia da Tela de Negocio e a consistencia da persistencia em tabelas.")

    doc.add_heading1("12. REFERENCIAS BIBLIOGRAFICAS (ABNT NBR 6023)")
    doc.add_bullet("ABNT", "ASSOCIACAO BRASILEIRA DE NORMAS TECNICAS. NBR 14724: Informacao e documentacao - Trabalhos academicos - Apresentacao. Rio de Janeiro: ABNT, 2011.")
    doc.add_bullet("ABNT", "ASSOCIACAO BRASILEIRA DE NORMAS TECNICAS. NBR 6023: Informacao e documentacao - Referencias - Elaboracao. Rio de Janeiro: ABNT, 2018.")
    doc.add_bullet("DEITEL", "DEITEL, Paul; DEITEL, Harvey. Java: Como Programar. 10. ed. Sao Paulo: Pearson Education do Brasil, 2016.")
    doc.add_bullet("GAMMA", "GAMMA, Erich et al. Padroes de Projeto: Solucoes Reutilizaveis de Software Orientado a Objetos. Porto Alegre: Bookman, 2000.")
    doc.add_bullet("SIERRA", "SIERRA, Kathy; BATES, Bert. Use a Cabeca! Java. 2. ed. Rio de Janeiro: Alta Books, 2007.")

    doc.save()

if __name__ == "__main__":
    caminho = sys.argv[1] if len(sys.argv) > 1 else r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs\SisVendas_Documentacao_ABNT.pdf"
    gerar_relatorio_abnt_completo(caminho)
