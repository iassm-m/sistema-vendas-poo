package br.com.sisvendas;

import br.com.sisvendas.dao.BancoDadosMemoria;
import br.com.sisvendas.model.*;
import br.com.sisvendas.service.ClienteService;
import br.com.sisvendas.service.ProdutoService;
import br.com.sisvendas.service.VendaService;
import br.com.sisvendas.view.TelaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;

/**
 * Ponto de entrada (Main) do Sistema de Informação SisVendas.
 * 
 * Disciplina: Programação Orientada a Objetos
 * Universidade Veiga de Almeida (UVA)
 * 
 * Grupo: IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class Main {

    public static void main(String[] args) {
        exibirBannerTerminal();

        // 1. Inicialização da Base de Dados Simulada (sem alterar registros existentes)
        System.out.println("[1/3] Inicializando simulador de banco de dados (BancoDadosMemoria)...");
        BancoDadosMemoria.getInstancia();
        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        VendaService vendaService = new VendaService();

        exibirResumoBaseDados(clienteService, produtoService, vendaService);

        // Executa demonstração via terminal apenas quando explicitamente solicitado (--demo)
        for (String arg : args) {
            if ("--demo".equalsIgnoreCase(arg)) {
                System.out.println("\n[DEMO] Executando simulacao de demonstracao via terminal...");
                executarDemonstracaoTerminal(clienteService, produtoService, vendaService);
                break;
            }
        }

        // 2. Inicialização da Interface Gráfica Swing
        System.out.println("\n[2/3] Configurando Look and Feel da interface visual...");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // 3. Abertura da Janela Principal
        System.out.println("[3/3] Abrindo janela grafica principal (TelaPrincipal - Swing GUI)...");
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal principal = new TelaPrincipal();
            principal.setVisible(true);
            System.out.println("      >>> SISTEMA ATIVO! Janela grafica do SisVendas aberta com sucesso! <<<\n");
        });
    }

    private static void exibirBannerTerminal() {
        System.out.println("================================================================================");
        System.out.println("   UNIVERSIDADE VEIGA DE ALMEIDA - UVA");
        System.out.println("   DISCIPLINA: PROGRAMACAO ORIENTADA A OBJETOS (POO) - AVALIACAO A2");
        System.out.println("   SISTEMA DE GESTAO COMERCIAL (SISVENDAS) - JAVA SE");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("   INTEGRANTES DO GRUPO:");
        System.out.println("   - IASMIM DA SILVA MANN JOSE          (Matricula: 1250109566)");
        System.out.println("   - REBECA MACHADO DE LIMA             (Matricula: 1250103657)");
        System.out.println("   - SARAH GABRIELLA VIRGINIA DA SILVA  (Matricula: 1250106978)");
        System.out.println("   - SILVANA GOMES DA SILVA             (Matricula: 1250109767)");
        System.out.println("================================================================================\n");
    }

    private static void exibirResumoBaseDados(ClienteService cliServ, ProdutoService prodServ, VendaService vendaServ) {
        List<Cliente> clientes = cliServ.listarTodos();
        List<Produto> produtos = prodServ.listarTodos();
        List<Pedido> pedidos = vendaServ.listarTodas();

        int vendasFinalizadas = 0;
        double faturamento = 0.0;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.FINALIZADO) {
                vendasFinalizadas++;
                faturamento += p.getTotalComDesconto();
            }
        }

        int estoqueBaixo = 0;
        for (Produto prod : produtos) {
            if (prod.isEstoqueBaixo()) {
                estoqueBaixo++;
            }
        }

        System.out.printf("      -> TB_CLIENTES:     %d clientes carregados na base de dados.%n", clientes.size());
        System.out.printf("      -> TB_PRODUTOS:     %d produtos cadastrados (%d com estoque baixo).%n", produtos.size(), estoqueBaixo);
        System.out.printf("      -> TB_PEDIDOS:      %d pedidos no historico (%d finalizados | Faturamento: R$ %.2f).%n",
                pedidos.size(), vendasFinalizadas, faturamento);
    }

    private static void executarDemonstracaoTerminal(ClienteService cliServ, ProdutoService prodServ, VendaService vendaServ) {
        List<Cliente> clientes = cliServ.listarTodos();
        List<Produto> produtos = prodServ.listarTodos();
        if (clientes.isEmpty() || produtos.isEmpty()) {
            System.out.println("   [AVISO] Cadastre ao menos 1 cliente e 1 produto para executar a demonstracao.");
            return;
        }

        Cliente c = clientes.get(0);
        Produto p1 = produtos.size() > 1 ? produtos.get(1) : produtos.get(0);
        if (p1.getQuantidadeEstoque() < 1) {
            System.out.println("   [AVISO] Produto selecionado sem estoque disponivel para demonstracao.");
            return;
        }

        System.out.printf("   [CLIENTE SELECIONADO] %s (CPF: %s)%n", c.getNome(), c.getCpf());
        System.out.printf("   [ESTOQUE INICIAL] %s: %d unidades disponiveis (R$ %.2f)%n",
                p1.getNome(), p1.getQuantidadeEstoque(), p1.getPrecoUnitario());

        Pedido novoPedido = new Pedido(null, null, c, FormaPagamento.PIX);
        novoPedido.adicionarItem(p1, 1);
        vendaServ.realizarVenda(novoPedido);

        System.out.printf("   [ESTOQUE ATUALIZADO] %s: agora restam %d unidades.%n", p1.getNome(), p1.getQuantidadeEstoque());
        System.out.println("\n--- CUPOM FISCAL EMITIDO PELA DEMONSTRACAO ---");
        System.out.print(vendaServ.gerarComprovanteVenda(novoPedido));
    }
}
