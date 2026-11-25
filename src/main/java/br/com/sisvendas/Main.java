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

        // 1. Inicialização da Base de Dados Simulada
        System.out.println("[1/4] Inicializando simulador de banco de dados (BancoDadosMemoria)...");
        BancoDadosMemoria bd = BancoDadosMemoria.getInstancia();
        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        VendaService vendaService = new VendaService();

        List<Cliente> clientes = clienteService.listarTodos();
        List<Produto> produtos = produtoService.listarTodos();
        List<Pedido> pedidos = vendaService.listarTodas();

        System.out.printf("      -> TB_CLIENTES: %d clientes carregados na base de dados.%n", clientes.size());
        System.out.printf("      -> TB_PRODUTOS: %d produtos cadastrados com controle de estoque.%n", produtos.size());
        System.out.printf("      -> TB_PEDIDOS:  %d vendas no historico persistido.%n", pedidos.size());

        // 2. Demonstração das Regras de Negócio e CRUD pelo Terminal
        System.out.println("\n[2/4] Executando demonstracao das operacoes de CRUD e Tela de Negocio via terminal...");
        executarDemonstracaoTerminal(clienteService, produtoService, vendaService);

        // 3. Inicialização da Interface Gráfica Swing
        System.out.println("\n[3/4] Configurando Look and Feel da interface visual...");
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

        System.out.println("\n[4/4] Abrindo janela grafica principal (TelaPrincipal - Swing GUI)...");
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

    private static void executarDemonstracaoTerminal(ClienteService cliServ, ProdutoService prodServ, VendaService vendaServ) {
        // Exemplo de Cliente
        Cliente c = cliServ.listarTodos().get(0);
        System.out.printf("   [CLIENTE SELECIONADO] %s (CPF: %s - %s)%n", c.getNome(), c.getCpf(), c.getCidade());

        // Exemplo de Produtos no catálogo e estoque antes da venda
        Produto p1 = prodServ.listarTodos().get(1); // Mouse
        Produto p2 = prodServ.listarTodos().get(2); // Teclado
        System.out.printf("   [ESTOQUE INICIAL] %s: %d unidades disponíveis (R$ %.2f)%n", p1.getNome(), p1.getQuantidadeEstoque(), p1.getPrecoUnitario());
        System.out.printf("   [ESTOQUE INICIAL] %s: %d unidades disponíveis (R$ %.2f)%n", p2.getNome(), p2.getQuantidadeEstoque(), p2.getPrecoUnitario());

        // Simulação da Tela de Negócio: Montagem do Pedido
        System.out.println("   [TELA DE NEGOCIO] Criando novo pedido e adicionando itens ao carrinho...");
        Pedido novoPedido = new Pedido(null, "PED-TERM-01", c, FormaPagamento.PIX);
        novoPedido.adicionarItem(p1, 2); // 2 mouses
        novoPedido.adicionarItem(p2, 1); // 1 teclado

        System.out.printf("   [CARRINHO] Subtotal Bruto: R$ %.2f%n", novoPedido.getSubtotalBruto());
        System.out.printf("   [PAGAMENTO] Forma escolhida: %s -> Desconto automatico de 5%%%n", novoPedido.getFormaPagamento().getDescricao());
        System.out.printf("   [TOTAL FINAL] Valor Liquido a Pagar: R$ %.2f%n", novoPedido.getTotalComDesconto());

        // Processamento da Venda com baixa em estoque
        System.out.println("   [TRANSACAO] Finalizando venda e realizando baixa automatica no estoque...");
        vendaServ.realizarVenda(novoPedido);

        // Verificação do Estoque após a baixa
        System.out.printf("   [ESTOQUE ATUALIZADO] %s: agora restam %d unidades.%n", p1.getNome(), p1.getQuantidadeEstoque());
        System.out.printf("   [ESTOQUE ATUALIZADO] %s: agora restam %d unidades.%n", p2.getNome(), p2.getQuantidadeEstoque());

        // Emissão do comprovante fiscal no terminal
        System.out.println("\n--- CUPOM FISCAL EMITIDO AUTOMATICAMENTE PELA TELA DE NEGOCIO ---");
        System.out.print(vendaServ.gerarComprovanteVenda(novoPedido));
        System.out.println("-----------------------------------------------------------------");
    }
}
