# -*- coding: utf-8 -*-
import os, glob

base_dir = r"C:\Users\Iasmim.jose\.gemini\antigravity\scratch\sistema-vendas-poo"

grupo_str_full = "IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767"
grupo_md = "> **Grupo:** IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767."

# 1. Update README.md
readme_path = os.path.join(base_dir, "README.md")
with open(readme_path, "r", encoding="utf-8") as f:
    c = f.read()
c = c.replace("> **Autora:** Iasmim José", grupo_md)
c = c.replace("Autora: Iasmim José", grupo_md)
with open(readme_path, "w", encoding="utf-8") as f:
    f.write(c)
print("README.md updated")

# 2. Update DOCUMENTACAO_PROJETO_ABNT.md
doc_md_path = os.path.join(base_dir, "docs", "DOCUMENTACAO_PROJETO_ABNT.md")
with open(doc_md_path, "r", encoding="utf-8") as f:
    c = f.read()
c = c.replace("**Autora:** Iasmim José", grupo_md)
c = c.replace("Autora: Iasmim José", grupo_md)
with open(doc_md_path, "w", encoding="utf-8") as f:
    f.write(c)
print("DOCUMENTACAO_PROJETO_ABNT.md updated")

# 3. Update Java files (@author)
java_files = glob.glob(os.path.join(base_dir, "src", "main", "java", "**", "*.java"), recursive=True)
for jf in java_files:
    with open(jf, "r", encoding="utf-8") as f:
        content = f.read()
    content = content.replace("@author Iasmim José", f"@author {grupo_str_full}")
    content = content.replace("Aluna: Iasmim José", f"Grupo: {grupo_str_full}")
    if "TelaPrincipal.java" in jf:
        content = content.replace(
            'JLabel lblSubtitulo = new JLabel("Trabalho da Disciplina de Programação Orientada a Objetos - Autora: Iasmim José");',
            'JLabel lblSubtitulo = new JLabel("Trabalho de POO - Grupo: Iasmim José, Rebeca Lima, Sarah Silva, Silvana Silva");'
        )
        content = content.replace(
            '"Autora: Iasmim José\\n" +',
            '"Grupo:\\n - IASMIM DA SILVA MANN JOSÉ (1250109566)\\n - REBECA MACHADO DE LIMA (1250103657)\\n - SARAH GABRIELLA VIRGÍNIA DA SILVA (1250106978)\\n - SILVANA GOMES DA SILVA (1250109767)\\n\\n" +'
        )
    with open(jf, "w", encoding="utf-8") as f:
        f.write(content)
print(f"{len(java_files)} Java files updated")

# 4. Update .frm files
frm_files = glob.glob(os.path.join(base_dir, "src", "main", "java", "**", "*.frm"), recursive=True)
for ff in frm_files:
    with open(ff, "r", encoding="utf-8") as f:
        content = f.read()
    content = content.replace("# Autora: Iasmim José", f"# Grupo: {grupo_str_full}")
    content = content.replace("Autora: Iasmim José", f"Grupo: {grupo_str_full}")
    content = content.replace("Iasmim José", "Iasmim, Rebeca, Sarah, Silvana")
    with open(ff, "w", encoding="utf-8") as f:
        f.write(content)
print(f"{len(frm_files)} .frm files updated")
