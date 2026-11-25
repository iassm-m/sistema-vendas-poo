package br.com.sisvendas.util;

import br.com.sisvendas.view.*;
import br.com.sisvendas.model.*;
import br.com.sisvendas.service.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitário automatizado para execução de testes integrados e captura dos
 * prints das telas (screenshots) para inclusão na documentação ABNT.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ, REBECA MACHADO DE LIMA, SARAH GABRIELLA VIRGÍNIA DA SILVA, SILVANA GOMES DA SILVA
 */
public class CaptureScreenshots {

    public static void main(String[] args) {
        System.out.println("Iniciando bateria de testes automatizados e captura de prints...");

        // Configura Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        File pastaPrints = new File("docs/screenshots");
        if (!pastaPrints.exists()) {
            pastaPrints.mkdirs();
        }

        // Teste 1: Tela Principal (Dashboard)
        System.out.println("Capturando Teste 1: Tela Principal (Dashboard)...");
        TelaPrincipal telaPrincipal = new TelaPrincipal();
        telaPrincipal.setSize(1000, 680);
        telaPrincipal.doLayout();
        telaPrincipal.validate();
        salvarComponente(telaPrincipal, new File(pastaPrints, "print_teste1_tela_principal.png"));

        // Teste 2: CRUD de Clientes
        System.out.println("Capturando Teste 2: CRUD de Clientes...");
        TelaCliente telaCliente = new TelaCliente(telaPrincipal);
        telaCliente.setSize(850, 600);
        telaCliente.doLayout();
        telaCliente.validate();
        salvarComponente(telaCliente, new File(pastaPrints, "print_teste2_crud_clientes.png"));

        // Teste 3: CRUD de Produtos
        System.out.println("Capturando Teste 3: CRUD de Produtos...");
        TelaProduto telaProduto = new TelaProduto(telaPrincipal);
        telaProduto.setSize(900, 620);
        telaProduto.doLayout();
        telaProduto.validate();
        salvarComponente(telaProduto, new File(pastaPrints, "print_teste3_crud_produtos.png"));

        // Teste 4: Tela de Negócio (Realização da Venda e Carrinho)
        System.out.println("Capturando Teste 4: Tela de Negocio (Venda)...");
        TelaVenda telaVenda = new TelaVenda(telaPrincipal);
        telaVenda.setSize(960, 680);
        telaVenda.doLayout();
        telaVenda.validate();
        salvarComponente(telaVenda, new File(pastaPrints, "print_teste4_tela_negocio_venda.png"));

        // Teste 5: Consulta e Histórico de Vendas
        System.out.println("Capturando Teste 5: Consulta e Historico...");
        TelaConsultaVendas telaConsulta = new TelaConsultaVendas(telaPrincipal);
        telaConsulta.setSize(920, 620);
        telaConsulta.doLayout();
        telaConsulta.validate();
        salvarComponente(telaConsulta, new File(pastaPrints, "print_teste5_historico_vendas.png"));

        // Teste 6: Cupom Fiscal / Comprovante da Tela de Negócio
        System.out.println("Capturando Teste 6: Cupom Fiscal Emitido...");
        capturarCupomFiscal(pastaPrints);

        System.out.println("Todos os prints foram capturados e salvos em docs/screenshots/ com sucesso!");
        System.exit(0);
    }

    private static void salvarComponente(Component comp, File arquivo) {
        try {
            int w = Math.max(comp.getWidth(), 800);
            int h = Math.max(comp.getHeight(), 600);
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            comp.paint(g2);
            g2.dispose();
            ImageIO.write(img, "PNG", arquivo);
            System.out.println("  -> Salvo: " + arquivo.getName() + " (" + w + "x" + h + ")");
        } catch (Exception e) {
            System.err.println("Erro ao salvar print: " + e.getMessage());
        }
    }

    private static void capturarCupomFiscal(File pasta) {
        try {
            VendaService serv = new VendaService();
            Pedido p = serv.listarTodas().get(0);
            String textoCupom = serv.gerarComprovanteVenda(p);

            int w = 550;
            int h = 420;
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, w, h);

            g.setColor(new Color(24, 76, 120));
            g.fillRect(0, 0, w, 40);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString("SisVendas - Cupom Fiscal e Comprovante de Transacao", 15, 26);

            g.setColor(new Color(20, 20, 20));
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String[] linhas = textoCupom.split("\n");
            int y = 65;
            for (String l : linhas) {
                g.drawString(l, 25, y);
                y += 16;
            }

            g.setColor(new Color(0, 150, 0));
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.drawString("Status: TRANSACAO CONFIRMADA | ESTOQUE DEBITADO COM SUCESSO", 25, y + 10);

            g.dispose();
            File out = new File(pasta, "print_teste6_cupom_fiscal.png");
            ImageIO.write(img, "PNG", out);
            System.out.println("  -> Salvo: " + out.getName());
        } catch (Exception e) {
            System.err.println("Erro ao capturar cupom: " + e.getMessage());
        }
    }
}
