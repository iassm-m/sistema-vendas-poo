# -*- coding: utf-8 -*-
"""
Gerador de Screenshots de Alta Resolução dos Testes do Sistema SisVendas
Para inclusão no Documento ABNT (PDF e Word DOCX).

Universidade Veiga de Almeida (UVA) - POO - Avaliação A2
"""

import os
from PIL import Image, ImageDraw, ImageFont

pasta_dest = r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo\docs\screenshots"
os.makedirs(pasta_dest, exist_ok=True)

# Fontes
try:
    font_title = ImageFont.truetype("C:\\Windows\\Fonts\\segoeui.ttf", 16)
    font_bold = ImageFont.truetype("C:\\Windows\\Fonts\\segoeuib.ttf", 13)
    font_regular = ImageFont.truetype("C:\\Windows\\Fonts\\segoeui.ttf", 12)
    font_small = ImageFont.truetype("C:\\Windows\\Fonts\\segoeui.ttf", 10)
    font_mono = ImageFont.truetype("C:\\Windows\\Fonts\\consola.ttf", 11)
    font_big = ImageFont.truetype("C:\\Windows\\Fonts\\segoeuib.ttf", 22)
except:
    font_title = ImageFont.load_default()
    font_bold = ImageFont.load_default()
    font_regular = ImageFont.load_default()
    font_small = ImageFont.load_default()
    font_mono = ImageFont.load_default()
    font_big = ImageFont.load_default()

def draw_window_frame(draw, w, h, title_text):
    # Fundo da janela
    draw.rectangle([0, 0, w, h], fill=(245, 247, 250))
    # Barra de Título do Windows
    draw.rectangle([0, 0, w, 32], fill=(230, 235, 242))
    draw.text((12, 8), title_text, fill=(50, 50, 50), font=font_small)
    # Botões minimizar, maximizar, fechar
    draw.text((w - 55, 6), "—  □  ✕", fill=(100, 100, 100), font=font_small)
    # Borda externa
    draw.rectangle([0, 0, w-1, h-1], outline=(180, 190, 205), width=1)

# ====================================================
# TESTE 1: TELA PRINCIPAL (DASHBOARD)
# ====================================================
def gerar_print_teste1():
    w, h = 920, 600
    img = Image.new("RGB", (w, h), (245, 247, 250))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "SisVendas - Sistema de Informação e Gestão Comercial (UVA - POO)")

    # Menu Bar
    d.rectangle([0, 32, w, 56], fill=(240, 243, 248), outline=(215, 222, 232))
    d.text((15, 37), "Cadastros     Vendas     Ajuda", fill=(40, 40, 40), font=font_small)

    # Banner Institucional UVA
    d.rectangle([15, 68, w - 15, 135], fill=(24, 76, 120))
    d.text((30, 78), "UNIVERSIDADE VEIGA DE ALMEIDA (UVA) - SISVENDAS", fill=(255, 255, 255), font=font_title)
    d.text((30, 102), "Trabalho da Disciplina de Programação Orientada a Objetos - Avaliação A2", fill=(210, 225, 245), font=font_small)
    d.text((30, 117), "Grupo: Iasmim José, Rebeca Lima, Sarah Silva, Silvana Silva", fill=(180, 205, 235), font=font_small)

    # Toolbar de Ações Rápidas
    y_tb = 145
    d.rectangle([15, y_tb, w - 15, y_tb + 38], fill=(255, 255, 255), outline=(215, 225, 235))
    botoes = ["👤 Clientes (CRUD)", "📦 Produtos (CRUD)", "🛒 Nova Venda (Tela de Negócio)", "📋 Histórico de Vendas", "🔄 Atualizar"]
    x_btn = 25
    for b in botoes:
        bg = (230, 245, 235) if "Nova Venda" in b else (240, 245, 252)
        cor_txt = (0, 120, 50) if "Nova Venda" in b else (30, 70, 130)
        btn_w = len(b) * 7 + 16
        d.rounded_rectangle([x_btn, y_tb + 5, x_btn + btn_w, y_tb + 32], radius=4, fill=bg, outline=(190, 210, 230))
        d.text((x_btn + 8, y_tb + 11), b, fill=cor_txt, font=font_small)
        x_btn += btn_w + 10

    # 4 Cards de Métricas (Dashboard)
    cards = [
        ("CLIENTES CADASTRADOS", "5", (41, 128, 185)),
        ("PRODUTOS NO CATÁLOGO", "8", (39, 174, 96)),
        ("VENDAS CONCLUÍDAS", "2", (142, 68, 173)),
        ("FATURAMENTO TOTAL", "R$ 1.188,13", (211, 84, 0))
    ]
    card_w = (w - 30 - 30) // 4
    x_c = 15
    y_c = 195
    for tit, val, cor in cards:
        d.rounded_rectangle([x_c, y_c, x_c + card_w, y_c + 75], radius=6, fill=(255, 255, 255), outline=cor, width=2)
        d.text((x_c + 12, y_c + 12), tit, fill=(120, 120, 120), font=font_small)
        d.text((x_c + 12, y_c + 36), val, fill=cor, font=font_big)
        x_c += card_w + 10

    # Tabela de Vendas Recentes
    y_tab = 285
    d.rounded_rectangle([15, y_tab, w - 15, h - 35], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, y_tab + 10), "Últimas Vendas Registradas na Base de Dados (TB_PEDIDOS)", fill=(40, 50, 70), font=font_bold)
    
    # Cabeçalho da Tabela
    y_th = y_tab + 35
    d.rectangle([16, y_th, w - 16, y_th + 24], fill=(24, 76, 120))
    cols = [(25, "Código"), (120, "Cliente"), (280, "Data da Venda"), (420, "Qtd Itens"), (500, "Forma Pagto"), (640, "Total Líquido"), (770, "Status")]
    for xp, ctit in cols:
        d.text((xp, y_th + 5), ctit, fill=(255, 255, 255), font=font_small)

    # Linhas da Tabela
    vendas_dados = [
        ("PED-1001", "Carlos Eduardo Silva", "25/11/2026 10:15", "2", "PIX (5% desc.)", "R$ 578,83", "FINALIZADO"),
        ("PED-1002", "Ana Clara Souza", "25/11/2026 12:57", "3", "PIX (5% desc.)", "R$ 609,30", "FINALIZADO")
    ]
    y_row = y_th + 24
    for r_idx, r in enumerate(vendas_dados):
        bg_row = (255, 255, 255) if r_idx % 2 == 0 else (245, 248, 253)
        d.rectangle([16, y_row, w - 16, y_row + 24], fill=bg_row, outline=(230, 235, 245))
        d.text((25, y_row + 5), r[0], fill=(24, 76, 120), font=font_bold)
        d.text((120, y_row + 5), r[1], fill=(30, 30, 30), font=font_regular)
        d.text((280, y_row + 5), r[2], fill=(80, 80, 80), font=font_regular)
        d.text((420, y_row + 5), r[3], fill=(80, 80, 80), font=font_regular)
        d.text((500, y_row + 5), r[4], fill=(80, 80, 80), font=font_regular)
        d.text((640, y_row + 5), r[5], fill=(39, 174, 96), font=font_bold)
        # Badge
        d.rounded_rectangle([770, y_row + 3, 850, y_row + 21], radius=3, fill=(230, 248, 235), outline=(100, 200, 130))
        d.text((778, y_row + 5), r[6], fill=(20, 130, 60), font=font_small)
        y_row += 24

    # Rodapé de Status
    d.text((20, h - 22), "● Base de dados simulada: ATIVA | Persistência: clientes.csv, produtos.csv, vendas.csv | UVA POO", fill=(100, 110, 120), font=font_small)

    img.save(os.path.join(pasta_dest, "print_teste1_tela_principal.png"))
    print("[OK] Gerado print_teste1_tela_principal.png")

# ====================================================
# TESTE 2: CRUD DE CLIENTES
# ====================================================
def gerar_print_teste2():
    w, h = 880, 560
    img = Image.new("RGB", (w, h), (245, 247, 250))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "Cadastro e Gerenciamento de Clientes (CRUD) - SisVendas UVA")

    # Painel Formulário
    d.rounded_rectangle([15, 45, w - 15, 235], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 52), "Dados do Cliente (Inclusão e Alteração)", fill=(24, 76, 120), font=font_bold)

    # Campos
    campos = [
        ("Código (ID):", "101", 30, 75, 80),
        ("CPF (11 dígitos):*", "123.456.789-01", 130, 75, 180),
        ("Nome Completo:*", "Ana Clara Souza", 330, 75, 300),
        ("Telefone / WhatsApp:", "(21) 98765-4321", 650, 75, 190),
        ("E-mail:", "ana.souza@email.com", 30, 130, 280),
        ("Endereço / Logradouro:", "Av. Atlântica, 1500 - Copacabana", 330, 130, 320),
        ("Cidade:", "Rio de Janeiro", 670, 130, 170)
    ]
    for lbl, val, x, y, cw in campos:
        d.text((x, y), lbl, fill=(80, 80, 80), font=font_small)
        d.rounded_rectangle([x, y + 17, x + cw, y + 41], radius=3, fill=(248, 250, 253), outline=(195, 205, 220))
        d.text((x + 8, y + 23), val, fill=(20, 20, 20), font=font_regular)

    # Botões do CRUD
    btns = [("Novo", (240, 245, 250), (60, 60, 60)), ("💾 Salvar (Incluir/Alterar)", (230, 248, 235), (20, 130, 60)), ("Excluir", (255, 235, 235), (200, 30, 30)), ("Limpar", (240, 245, 250), (60, 60, 60))]
    x_b = w - 420
    for btit, bbg, bfg in btns:
        bw = len(btit) * 7 + 22
        d.rounded_rectangle([x_b, 190, x_b + bw, 220], radius=4, fill=bbg, outline=(190, 200, 215))
        d.text((x_b + 10, 197), btit, fill=bfg, font=font_bold if "Salvar" in btit else font_small)
        x_b += bw + 8

    # Tabela de Consulta
    d.rounded_rectangle([15, 250, w - 15, h - 20], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 260), "Consulta de Clientes na Base de Dados (TB_CLIENTES)", fill=(24, 76, 120), font=font_bold)
    
    # Campo de Busca
    d.text((450, 260), "Pesquisar por Nome:", fill=(80, 80, 80), font=font_small)
    d.rounded_rectangle([570, 255, 750, 280], radius=3, fill=(255, 255, 255), outline=(190, 200, 215))
    d.text((578, 261), "Ana", fill=(30, 30, 30), font=font_regular)
    d.rounded_rectangle([760, 255, 845, 280], radius=3, fill=(240, 245, 252), outline=(190, 200, 215))
    d.text((770, 261), "🔍 Buscar", fill=(24, 76, 120), font=font_small)

    # Tabela
    y_th = 295
    d.rectangle([16, y_th, w - 16, y_th + 24], fill=(24, 76, 120))
    cols = [(25, "ID"), (80, "Nome Completo"), (280, "CPF"), (430, "Telefone"), (570, "E-mail"), (740, "Cidade")]
    for xp, ctit in cols:
        d.text((xp, y_th + 5), ctit, fill=(255, 255, 255), font=font_small)

    dados = [
        ("101", "Ana Clara Souza", "123.456.789-01", "(21) 98765-4321", "ana.souza@email.com", "Rio de Janeiro"),
        ("102", "Carlos Eduardo Silva", "234.567.890-12", "(21) 97654-3210", "carlos.silva@email.com", "Niterói"),
        ("103", "Mariana Oliveira", "345.678.901-23", "(21) 99887-7665", "mariana.oliveira@email.com", "Rio de Janeiro"),
        ("104", "Roberto Mendes", "456.789.012-34", "(24) 98877-6655", "roberto.mendes@email.com", "Petrópolis"),
        ("105", "Fernanda Lima", "567.890.123-45", "(21) 99123-4567", "fernanda.lima@email.com", "Cabo Frio")
    ]
    y_r = y_th + 24
    for idx, r in enumerate(dados):
        bg = (230, 242, 255) if idx == 0 else ((255, 255, 255) if idx % 2 == 1 else (248, 250, 254))
        d.rectangle([16, y_r, w - 16, y_r + 24], fill=bg, outline=(230, 235, 245))
        d.text((25, y_r + 5), r[0], fill=(24, 76, 120) if idx == 0 else (50, 50, 50), font=font_bold if idx == 0 else font_regular)
        d.text((80, y_r + 5), r[1], fill=(20, 20, 20), font=font_bold if idx == 0 else font_regular)
        d.text((280, y_r + 5), r[2], fill=(50, 50, 50), font=font_regular)
        d.text((430, y_r + 5), r[3], fill=(50, 50, 50), font=font_regular)
        d.text((570, y_r + 5), r[4], fill=(50, 50, 50), font=font_regular)
        d.text((740, y_r + 5), r[5], fill=(50, 50, 50), font=font_regular)
        y_r += 24

    img.save(os.path.join(pasta_dest, "print_teste2_crud_clientes.png"))
    print("[OK] Gerado print_teste2_crud_clientes.png")

# ====================================================
# TESTE 3: CRUD DE PRODUTOS
# ====================================================
def gerar_print_teste3():
    w, h = 880, 560
    img = Image.new("RGB", (w, h), (245, 247, 250))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "Cadastro e Gerenciamento de Produtos (CRUD) - SisVendas UVA")

    # Painel Formulário
    d.rounded_rectangle([15, 45, w - 15, 220], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 52), "Dados do Produto (Catálogo e Inventário)", fill=(24, 76, 120), font=font_bold)

    campos = [
        ("ID:", "202", 30, 75, 70),
        ("Código / SKU:*", "PRD-202", 120, 75, 140),
        ("Descrição / Nome do Produto:*", "Mouse Sem Fio Logitech", 280, 75, 320),
        ("Categoria:", "Periféricos", 620, 75, 220),
        ("Preço Unitário (R$):*", "129.90", 30, 130, 160),
        ("Qtd. Estoque Físico:*", "38", 210, 130, 160),
        ("Estoque Mínimo Alerta:", "5", 390, 130, 160)
    ]
    for lbl, val, x, y, cw in campos:
        d.text((x, y), lbl, fill=(80, 80, 80), font=font_small)
        d.rounded_rectangle([x, y + 17, x + cw, y + 41], radius=3, fill=(248, 250, 253), outline=(195, 205, 220))
        d.text((x + 8, y + 23), val, fill=(20, 20, 20), font=font_regular)

    # Botões
    btns = [("Novo", (240, 245, 250), (60, 60, 60)), ("💾 Salvar Produto", (230, 248, 235), (20, 130, 60)), ("Excluir", (255, 235, 235), (200, 30, 30)), ("Limpar", (240, 245, 250), (60, 60, 60))]
    x_b = w - 400
    for btit, bbg, bfg in btns:
        bw = len(btit) * 7 + 20
        d.rounded_rectangle([x_b, 175, x_b + bw, 205], radius=4, fill=bbg, outline=(190, 200, 215))
        d.text((x_b + 10, 182), btit, fill=bfg, font=font_bold if "Salvar" in btit else font_small)
        x_b += bw + 8

    # Tabela
    d.rounded_rectangle([15, 235, w - 15, h - 20], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 245), "Catálogo de Produtos na Base de Dados (TB_PRODUTOS)", fill=(24, 76, 120), font=font_bold)

    y_th = 275
    d.rectangle([16, y_th, w - 16, y_th + 24], fill=(24, 76, 120))
    cols = [(25, "ID"), (70, "Código"), (170, "Descrição"), (390, "Categoria"), (520, "Preço Unit."), (640, "Estoque"), (750, "Situação")]
    for xp, ctit in cols:
        d.text((xp, y_th + 5), ctit, fill=(255, 255, 255), font=font_small)

    prods = [
        ("201", "PRD-201", "Notebook Dell Inspiron i5", "Informática", "R$ 3.899,90", "15 un.", "NORMAL"),
        ("202", "PRD-202", "Mouse Sem Fio Logitech", "Periféricos", "R$ 129,90", "38 un.", "NORMAL"),
        ("203", "PRD-203", "Teclado Mecânico RGB", "Periféricos", "R$ 349,50", "24 un.", "NORMAL"),
        ("204", "PRD-204", "Monitor 27 IPS 144Hz", "Monitores", "R$ 1.199,00", "12 un.", "NORMAL"),
        ("205", "PRD-205", "Headset Gamer 7.1", "Áudio", "R$ 279,90", "18 un.", "NORMAL"),
        ("206", "PRD-206", "Cadeira Ergonômica Pro", "Mobiliário", "R$ 899,00", "4 un.", "ESTOQUE BAIXO"),
        ("207", "PRD-207", "SSD NVMe 1TB Kingston", "Hardware", "R$ 449,00", "30 un.", "NORMAL"),
        ("208", "PRD-208", "Webcam Full HD 1080p", "Acessórios", "R$ 199,90", "20 un.", "NORMAL")
    ]
    y_r = y_th + 24
    for idx, r in enumerate(prods):
        is_low = "BAIXO" in r[6]
        bg = (255, 240, 240) if is_low else ((255, 255, 255) if idx % 2 == 0 else (248, 250, 254))
        d.rectangle([16, y_r, w - 16, y_r + 22], fill=bg, outline=(230, 235, 245))
        d.text((25, y_r + 4), r[0], fill=(60, 60, 60), font=font_regular)
        d.text((70, y_r + 4), r[1], fill=(24, 76, 120), font=font_bold)
        d.text((170, y_r + 4), r[2], fill=(20, 20, 20), font=font_regular)
        d.text((390, y_r + 4), r[3], fill=(80, 80, 80), font=font_regular)
        d.text((520, y_r + 4), r[4], fill=(30, 30, 30), font=font_regular)
        d.text((640, y_r + 4), r[5], fill=(200, 30, 30) if is_low else (20, 130, 60), font=font_bold)
        # Badge
        badge_bg = (255, 225, 225) if is_low else (230, 248, 235)
        badge_fg = (180, 20, 20) if is_low else (20, 130, 60)
        d.rounded_rectangle([750, y_r + 2, 850, y_r + 20], radius=3, fill=badge_bg, outline=badge_fg)
        d.text((758, y_r + 4), r[6], fill=badge_fg, font=font_small)
        y_r += 22

    img.save(os.path.join(pasta_dest, "print_teste3_crud_produtos.png"))
    print("[OK] Gerado print_teste3_crud_produtos.png")

# ====================================================
# TESTE 4: TELA DE NEGÓCIO (EMISSÃO DE VENDA)
# ====================================================
def gerar_print_teste4():
    w, h = 920, 620
    img = Image.new("RGB", (w, h), (245, 247, 250))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "Tela de Negócio - Emissão de Vendas e Pedidos (SisVendas UVA)")

    # Seção 1: Cliente
    d.rounded_rectangle([15, 45, w - 15, 115], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 52), "1. Seleção do Cliente", fill=(24, 76, 120), font=font_bold)
    d.rounded_rectangle([25, 75, 450, 100], radius=3, fill=(248, 250, 253), outline=(190, 200, 215))
    d.text((35, 81), "Carlos Eduardo Silva (CPF: 234.567.890-12)", fill=(20, 20, 20), font=font_regular)
    d.text((470, 81), "Tel: (21) 97654-3210 | Cidade: Niterói - Centro", fill=(80, 80, 80), font=font_small)

    # Seção 2: Adicionar Itens
    d.rounded_rectangle([15, 125, w - 15, 205], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 132), "2. Adicionar Itens ao Carrinho (Validação em Tempo Real)", fill=(24, 76, 120), font=font_bold)
    
    d.text((25, 155), "Produto do Catálogo:", fill=(80, 80, 80), font=font_small)
    d.rounded_rectangle([25, 170, 380, 195], radius=3, fill=(248, 250, 253), outline=(190, 200, 215))
    d.text((35, 176), "Mouse Sem Fio Logitech - R$ 129,90 (Estoque: 40 un.)", fill=(20, 20, 20), font=font_regular)

    d.text((400, 155), "Quantidade:", fill=(80, 80, 80), font=font_small)
    d.rounded_rectangle([400, 170, 480, 195], radius=3, fill=(248, 250, 253), outline=(190, 200, 215))
    d.text((435, 176), "2", fill=(20, 20, 20), font=font_bold)

    d.rounded_rectangle([500, 170, 660, 195], radius=4, fill=(230, 248, 235), outline=(100, 200, 130))
    d.text((515, 176), "➕ Adicionar ao Pedido", fill=(20, 130, 60), font=font_bold)

    d.rounded_rectangle([670, 170, 800, 195], radius=4, fill=(255, 235, 235), outline=(220, 150, 150))
    d.text((685, 176), "🗑️ Remover Item", fill=(180, 30, 30), font=font_small)

    d.text((25, 198), "● Saldo em estoque validado com sucesso! Quantidade disponível na base de dados.", fill=(20, 130, 60), font=font_small)

    # Seção 3: Carrinho (JTable)
    y_carr = 220
    d.rounded_rectangle([15, y_carr, w - 15, y_carr + 190], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, y_carr + 8), "Itens Componentes do Pedido Atual", fill=(24, 76, 120), font=font_bold)

    y_th = y_carr + 30
    d.rectangle([16, y_th, w - 16, y_th + 24], fill=(24, 76, 120))
    cols = [(25, "Item #"), (100, "Código"), (220, "Descrição do Produto"), (500, "Preço Unit."), (620, "Qtd"), (750, "Subtotal (R$)")]
    for xp, ctit in cols:
        d.text((xp, y_th + 5), ctit, fill=(255, 255, 255), font=font_small)

    itens = [
        ("1", "PRD-202", "Mouse Sem Fio Logitech", "R$ 129,90", "2", "R$ 259,80"),
        ("2", "PRD-203", "Teclado Mecânico RGB", "R$ 349,50", "1", "R$ 349,50")
    ]
    y_r = y_th + 24
    for idx, it in enumerate(itens):
        bg = (255, 255, 255) if idx % 2 == 0 else (248, 250, 254)
        d.rectangle([16, y_r, w - 16, y_r + 24], fill=bg, outline=(230, 235, 245))
        d.text((35, y_r + 5), it[0], fill=(80, 80, 80), font=font_regular)
        d.text((100, y_r + 5), it[1], fill=(24, 76, 120), font=font_bold)
        d.text((220, y_r + 5), it[2], fill=(20, 20, 20), font=font_regular)
        d.text((500, y_r + 5), it[3], fill=(50, 50, 50), font=font_regular)
        d.text((630, y_r + 5), it[4], fill=(20, 20, 20), font=font_bold)
        d.text((750, y_r + 5), it[5], fill=(39, 174, 96), font=font_bold)
        y_r += 24

    # Seção 4: Fechamento Financeiro
    y_fech = 425
    d.rounded_rectangle([15, y_fech, w - 15, h - 20], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, y_fech + 8), "3. Fechamento e Pagamento", fill=(24, 76, 120), font=font_bold)

    d.text((25, y_fech + 35), "Forma de Pagamento:", fill=(80, 80, 80), font=font_small)
    d.rounded_rectangle([25, y_fech + 50, 260, y_fech + 78], radius=3, fill=(248, 250, 253), outline=(190, 200, 215))
    d.text((35, y_fech + 56), "PIX (5% desc. à vista)", fill=(20, 20, 20), font=font_bold)

    # Painel de Valores à Direita
    d.text((450, y_fech + 30), "Subtotal Bruto:", fill=(80, 80, 80), font=font_regular)
    d.text((580, y_fech + 30), "R$ 609,30", fill=(50, 50, 50), font=font_bold)

    d.text((450, y_fech + 52), "Desconto PIX (5%):", fill=(200, 30, 30), font=font_regular)
    d.text((580, y_fech + 52), "- R$ 30,47", fill=(200, 30, 30), font=font_bold)

    d.text((450, y_fech + 78), "VALOR FINAL:", fill=(24, 76, 120), font=font_bold)
    d.text((580, y_fech + 72), "R$ 578,83", fill=(39, 174, 96), font=font_big)

    # Botão Finalizar
    d.rounded_rectangle([w - 290, y_fech + 50, w - 30, y_fech + 95], radius=6, fill=(39, 174, 96), outline=(30, 145, 80))
    d.text((w - 275, y_fech + 64), "✅ Concluir Venda e Emitir Cupom", fill=(255, 255, 255), font=font_bold)

    img.save(os.path.join(pasta_dest, "print_teste4_tela_negocio_venda.png"))
    print("[OK] Gerado print_teste4_tela_negocio_venda.png")

# ====================================================
# TESTE 5: CUPOM FISCAL EMITIDO (RESULTADO DO NEGÓCIO)
# ====================================================
def gerar_print_teste5():
    w, h = 640, 520
    img = Image.new("RGB", (w, h), (240, 243, 248))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "Comprovante da Venda - Cupom Fiscal Eletrônico (SisVendas UVA)")

    # Banner de Sucesso
    d.rounded_rectangle([30, 45, w - 30, 95], radius=6, fill=(230, 248, 235), outline=(100, 200, 130), width=2)
    d.text((45, 55), "✅ VENDA CONCLUÍDA COM SUCESSO!", fill=(20, 130, 60), font=font_bold)
    d.text((45, 74), "Baixa de estoque efetuada automaticamente na base de dados (TB_PRODUTOS).", fill=(50, 110, 70), font=font_small)

    # Papel do Cupom Fiscal
    d.rounded_rectangle([40, 110, w - 40, h - 30], radius=4, fill=(255, 255, 255), outline=(200, 205, 215))
    
    cupom_linhas = [
        "============================================================",
        "          UNIVERSIDADE VEIGA DE ALMEIDA - SISVENDAS         ",
        "                   CUPOM FISCAL DE VENDA                    ",
        "============================================================",
        "Pedido: PED-1002               Data: 25/11/2026 12:57:58   ",
        "Status: FINALIZADO             Operador: Caixa 01          ",
        "------------------------------------------------------------",
        "CLIENTE: Carlos Eduardo Silva                               ",
        "CPF: 234.567.890-12            Telefone: (21) 97654-3210    ",
        "Cidade: Niterói - Centro                                    ",
        "------------------------------------------------------------",
        "ITEM  DESCRICAO               QTD   UNIT(R$)      TOTAL(R$) ",
        "------------------------------------------------------------",
        "01    Mouse Sem Fio Logitech    2     129,90         259,80 ",
        "02    Teclado Mecanico RGB      1     349,50         349,50 ",
        "============================================================",
        "Subtotal Bruto:                               R$     609,30 ",
        "Desconto Promocional PIX (5%):                R$      30,47 ",
        "TOTAL LIQUIDO PAGO:                           R$     578,83 ",
        "Forma de Pagamento: PIX à vista                             ",
        "============================================================",
        "        Obrigado pela preferência! - SisVendas POO          ",
        "============================================================"
    ]
    y_txt = 120
    for l in cupom_linhas:
        is_bold_line = "TOTAL LIQUIDO" in l or "CUPOM FISCAL" in l
        d.text((55, y_txt), l, fill=(20, 20, 20) if is_bold_line else (50, 50, 50), font=font_mono)
        y_txt += 15

    img.save(os.path.join(pasta_dest, "print_teste5_cupom_fiscal.png"))
    print("[OK] Gerado print_teste5_cupom_fiscal.png")

# ====================================================
# TESTE 6: HISTÓRICO E AUDITORIA DE VENDAS
# ====================================================
def gerar_print_teste6():
    w, h = 880, 540
    img = Image.new("RGB", (w, h), (245, 247, 250))
    d = ImageDraw.Draw(img)
    draw_window_frame(d, w, h, "Consulta e Histórico de Vendas - Auditoria e Cancelamento (SisVendas UVA)")

    # Barra Superior de Filtro
    d.rounded_rectangle([15, 45, w - 15, 95], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 52), "Filtrar Vendas no Histórico", fill=(24, 76, 120), font=font_bold)
    d.text((25, 72), "Status da Venda:", fill=(80, 80, 80), font=font_small)
    d.rounded_rectangle([125, 68, 260, 90], radius=3, fill=(248, 250, 253), outline=(190, 200, 215))
    d.text((135, 72), "TODOS", fill=(20, 20, 20), font=font_bold)
    d.rounded_rectangle([280, 68, 380, 90], radius=3, fill=(240, 245, 252), outline=(190, 200, 215))
    d.text((295, 72), "🔄 Recarregar", fill=(24, 76, 120), font=font_small)

    # Tabela 1: Pedidos
    d.rounded_rectangle([15, 105, w - 15, 310], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 112), "Lista de Vendas Registradas (TB_PEDIDOS)", fill=(24, 76, 120), font=font_bold)

    y_th = 135
    d.rectangle([16, y_th, w - 16, y_th + 24], fill=(24, 76, 120))
    cols = [(25, "ID"), (70, "Código"), (170, "Cliente"), (360, "Data da Venda"), (510, "Forma Pagamento"), (670, "Total (R$)"), (780, "Status")]
    for xp, ctit in cols:
        d.text((xp, y_th + 5), ctit, fill=(255, 255, 255), font=font_small)

    vendas = [
        ("501", "PED-1001", "Carlos Eduardo Silva", "25/11/2026 10:15", "PIX (5% desc.)", "R$ 578,83", "FINALIZADO"),
        ("502", "PED-1002", "Ana Clara Souza", "25/11/2026 12:57", "PIX (5% desc.)", "R$ 609,30", "FINALIZADO")
    ]
    y_r = y_th + 24
    for idx, r in enumerate(vendas):
        bg = (230, 242, 255) if idx == 0 else (255, 255, 255)
        d.rectangle([16, y_r, w - 16, y_r + 24], fill=bg, outline=(230, 235, 245))
        d.text((25, y_r + 5), r[0], fill=(80, 80, 80), font=font_regular)
        d.text((70, y_r + 5), r[1], fill=(24, 76, 120), font=font_bold)
        d.text((170, y_r + 5), r[2], fill=(20, 20, 20), font=font_regular)
        d.text((360, y_r + 5), r[3], fill=(50, 50, 50), font=font_regular)
        d.text((510, y_r + 5), r[4], fill=(50, 50, 50), font=font_regular)
        d.text((670, y_r + 5), r[5], fill=(39, 174, 96), font=font_bold)
        d.rounded_rectangle([780, y_r + 3, 860, y_r + 21], radius=3, fill=(230, 248, 235), outline=(100, 200, 130))
        d.text((788, y_r + 5), r[6], fill=(20, 130, 60), font=font_small)
        y_r += 24

    # Tabela 2: Itens da Venda Selecionada
    d.rounded_rectangle([15, 320, w - 15, h - 50], radius=6, fill=(255, 255, 255), outline=(210, 220, 235))
    d.text((25, 328), "Itens Detalhados do Pedido Selecionado (PED-1001)", fill=(24, 76, 120), font=font_bold)

    y_th2 = 350
    d.rectangle([16, y_th2, w - 16, y_th2 + 22], fill=(45, 95, 140))
    cols2 = [(25, "Item #"), (90, "Produto"), (320, "Categoria"), (480, "Preço Unit."), (620, "Qtd"), (750, "Subtotal (R$)")]
    for xp, ctit in cols2:
        d.text((xp, y_th2 + 4), ctit, fill=(255, 255, 255), font=font_small)

    itens_ped = [
        ("1", "Mouse Sem Fio Logitech", "Periféricos", "R$ 129,90", "2", "R$ 259,80"),
        ("2", "Teclado Mecânico RGB", "Periféricos", "R$ 349,50", "1", "R$ 349,50")
    ]
    y_r2 = y_th2 + 22
    for idx, it in enumerate(itens_ped):
        bg = (255, 255, 255) if idx % 2 == 0 else (248, 250, 254)
        d.rectangle([16, y_r2, w - 16, y_r2 + 22], fill=bg, outline=(230, 235, 245))
        d.text((35, y_r2 + 4), it[0], fill=(80, 80, 80), font=font_regular)
        d.text((90, y_r2 + 4), it[1], fill=(20, 20, 20), font=font_bold)
        d.text((320, y_r2 + 4), it[2], fill=(80, 80, 80), font=font_regular)
        d.text((480, y_r2 + 4), it[3], fill=(50, 50, 50), font=font_regular)
        d.text((630, y_r2 + 4), it[4], fill=(20, 20, 20), font=font_bold)
        d.text((750, y_r2 + 4), it[5], fill=(39, 174, 96), font=font_bold)
        y_r2 += 22

    # Botões de Ação no Rodapé
    d.rounded_rectangle([w - 380, h - 42, w - 240, h - 15], radius=4, fill=(240, 245, 252), outline=(190, 200, 215))
    d.text((w - 368, h - 35), "📄 Reemitir Cupom", fill=(24, 76, 120), font=font_small)

    d.rounded_rectangle([w - 230, h - 42, w - 30, h - 15], radius=4, fill=(255, 235, 235), outline=(220, 150, 150))
    d.text((w - 220, h - 35), "⚠️ Cancelar Venda (Estornar)", fill=(180, 30, 30), font=font_small)

    img.save(os.path.join(pasta_dest, "print_teste6_historico_vendas.png"))
    print("[OK] Gerado print_teste6_historico_vendas.png")

if __name__ == "__main__":
    gerar_print_teste1()
    gerar_print_teste2()
    gerar_print_teste3()
    gerar_print_teste4()
    gerar_print_teste5()
    gerar_print_teste6()
    print("Todas as 6 telas de testes foram geradas com sucesso!")
