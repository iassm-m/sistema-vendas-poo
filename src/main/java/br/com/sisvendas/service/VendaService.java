package br.com.sisvendas.service;

import br.com.sisvendas.dao.PedidoDAO;
import br.com.sisvendas.dao.ProdutoDAO;
import br.com.sisvendas.model.*;
import java.util.List;

/**
 * Camada de Serviço que implementa a Lógica de Negócio central (Tela de Negócio).
 * Responsável por orquestrar a realização da venda, validações de estoque,
 * cálculo de descontos, finalização e estorno.
 * 
 * @author Iasmim José
 */
public class VendaService {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;

    public VendaService() {
        this.pedidoDAO = new PedidoDAO();
        this.produtoDAO = new ProdutoDAO();
    }

    public VendaService(PedidoDAO pedidoDAO, ProdutoDAO produtoDAO) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
    }

    /**
     * Valida e finaliza uma venda completa.
     * Atualiza os estoques e persiste o pedido.
     */
    public synchronized Pedido realizarVenda(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }
        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("Selecione um cliente para a venda.");
        }
        if (pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um produto no carrinho.");
        }

        // Valida disponibilidade de estoque de cada item antes de debitar
        for (ItemPedido item : pedido.getItens()) {
            Produto p = produtoDAO.buscarPorId(item.getProduto().getId());
            if (p == null) {
                throw new IllegalStateException("Produto não encontrado na base de dados: " + item.getProduto().getNome());
            }
            if (p.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalStateException(String.format("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                        p.getNome(), p.getQuantidadeEstoque(), item.getQuantidade()));
            }
        }

        // Finaliza (debita estoque) e salva
        pedido.finalizarVenda();
        pedidoDAO.salvar(pedido);
        return pedido;
    }

    /**
     * Cancela uma venda e estorna o estoque dos produtos.
     */
    public synchronized void cancelarVenda(Integer pedidoId) {
        Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado para cancelamento.");
        }
        pedido.cancelarVenda();
        pedidoDAO.atualizar(pedido);
    }

    public Pedido buscarPorId(Integer id) {
        return pedidoDAO.buscarPorId(id);
    }

    public List<Pedido> listarTodas() {
        return pedidoDAO.listarTodos();
    }

    public List<Pedido> listarPorCliente(Integer clienteId) {
        return pedidoDAO.buscarPorCliente(clienteId);
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoDAO.buscarPorStatus(status);
    }

    /**
     * Gera texto formatado de comprovante / cupom fiscal simples da venda.
     */
    public String gerarComprovanteVenda(Pedido p) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                 SISVENDAS - CUPOM FISCAL           \n");
        sb.append("====================================================\n");
        sb.append(String.format("Pedido: %-15s Data: %s%n", p.getCodigoPedido(), p.getDataVendaFormatada()));
        sb.append(String.format("Status: %s%n", p.getStatus().getDescricao()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("CLIENTE: %s%n", p.getCliente().getNome()));
        sb.append(String.format("CPF: %-18s Tel: %s%n", p.getCliente().getCpf(), p.getCliente().getTelefone()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-4s %-22s %4s %8s %10s%n", "ITEM", "DESCRICAO", "QTD", "UNIT(R$)", "TOTAL(R$)"));
        sb.append("----------------------------------------------------\n");

        for (ItemPedido item : p.getItens()) {
            String nomeProd = item.getProduto().getNome();
            if (nomeProd.length() > 22) {
                nomeProd = nomeProd.substring(0, 19) + "...";
            }
            sb.append(String.format("%-4d %-22s %4d %8.2f %10.2f%n",
                    item.getNumeroItem(),
                    nomeProd,
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()));
        }

        sb.append("====================================================\n");
        sb.append(String.format("Subtotal Bruto:                     R$ %10.2f%n", p.getSubtotalBruto()));
        sb.append(String.format("Desconto:                           R$ %10.2f%n", p.getValorDesconto()));
        sb.append(String.format("TOTAL LIQUIDO:                      R$ %10.2f%n", p.getTotalComDesconto()));
        sb.append(String.format("Forma de Pagamento: %s%n", p.getFormaPagamento().getDescricao()));
        sb.append("====================================================\n");
        sb.append("             Obrigado pela preferência!             \n");
        sb.append("====================================================\n");
        return sb.toString();
    }
}
