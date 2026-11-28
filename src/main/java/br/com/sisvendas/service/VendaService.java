package br.com.sisvendas.service;

import br.com.sisvendas.dao.PedidoDAO;
import br.com.sisvendas.dao.ProdutoDAO;
import br.com.sisvendas.model.*;
import java.util.List;

/**
 * Camada de Serviço que implementa a Lógica de Negócio central (Tela de Negócio)
 * e as operações completas de CRUD para a entidade Pedido.
 * Responsável por orquestrar a realização da venda, validações de estoque,
 * cálculo de descontos, alteração, exclusão, finalização e estorno.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
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
     * Valida e finaliza uma venda completa (Inclusão).
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

        // Valida disponibilidade de estoque e sincroniza referência gerenciada de cada produto
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new IllegalStateException("Item do pedido possui produto inválido.");
            }
            Produto p = produtoDAO.buscarPorId(item.getProduto().getId());
            if (p == null) {
                throw new IllegalStateException("Produto não encontrado na base de dados: " + item.getProduto().getNome());
            }
            if (p.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalStateException(String.format("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                        p.getNome(), p.getQuantidadeEstoque(), item.getQuantidade()));
            }
            item.setProduto(p);
        }

        // Finaliza (debita estoque de forma atômica) e persiste
        pedido.finalizarVenda();
        pedidoDAO.salvar(pedido);
        return pedido;
    }

    /**
     * Atualiza os dados de um pedido existente (Alteração do CRUD de Pedido).
     */
    public synchronized void atualizar(Pedido pedido) {
        if (pedido == null || pedido.getId() == null) {
            throw new IllegalArgumentException("Pedido inválido para atualização.");
        }
        Pedido existente = pedidoDAO.buscarPorId(pedido.getId());
        if (existente == null) {
            throw new IllegalArgumentException("Pedido não encontrado na base de dados.");
        }
        pedido.validar();
        pedidoDAO.atualizar(pedido);
    }

    /**
     * Exclui um pedido da base de dados (Exclusão do CRUD de Pedido).
     * Se o pedido estiver finalizado, o estoque dos produtos é estornado automaticamente.
     */
    public synchronized void excluir(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("ID inválido para exclusão do pedido.");
        }
        Pedido existente = pedidoDAO.buscarPorId(pedidoId);
        if (existente == null) {
            throw new IllegalArgumentException("Pedido não encontrado para exclusão.");
        }
        pedidoDAO.excluir(pedidoId);
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
        if (id == null) return null;
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
        if (p == null) {
            throw new IllegalArgumentException("Pedido inválido para emissão de comprovante.");
        }

        String codigo = p.getCodigoPedido() != null ? p.getCodigoPedido() : ("#" + p.getId());
        String status = p.getStatus() != null ? p.getStatus().getDescricao() : "N/D";
        String nomeCliente = p.getCliente() != null && p.getCliente().getNome() != null ? p.getCliente().getNome() : "N/D";
        String cpfCliente = p.getCliente() != null && p.getCliente().getCpf() != null ? p.getCliente().getCpf() : "N/D";
        String telCliente = p.getCliente() != null && p.getCliente().getTelefone() != null && !p.getCliente().getTelefone().trim().isEmpty()
                ? p.getCliente().getTelefone() : "N/I";
        String formaPagto = p.getFormaPagamento() != null ? p.getFormaPagamento().getDescricao() : "N/D";

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                 SISVENDAS - CUPOM FISCAL           \n");
        sb.append("====================================================\n");
        sb.append(String.format("Pedido: %-15s Data: %s%n", codigo, p.getDataVendaFormatada()));
        sb.append(String.format("Status: %s%n", status));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("CLIENTE: %s%n", nomeCliente));
        sb.append(String.format("CPF: %-18s Tel: %s%n", cpfCliente, telCliente));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-4s %-22s %4s %8s %10s%n", "ITEM", "DESCRICAO", "QTD", "UNIT(R$)", "TOTAL(R$)"));
        sb.append("----------------------------------------------------\n");

        for (ItemPedido item : p.getItens()) {
            String nomeProd = (item.getProduto() != null && item.getProduto().getNome() != null)
                    ? item.getProduto().getNome() : "Desconhecido";
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
        sb.append(String.format("Desconto Aplicado:                  R$ %10.2f%n", p.getDescontoEfetivo()));
        sb.append(String.format("TOTAL LIQUIDO:                      R$ %10.2f%n", p.getTotalComDesconto()));
        sb.append(String.format("Forma de Pagamento: %s%n", formaPagto));
        if (p.getObservacao() != null && !p.getObservacao().trim().isEmpty()) {
            sb.append(String.format("Observacao: %s%n", p.getObservacao().trim()));
        }
        sb.append("====================================================\n");
        sb.append("             Obrigado pela preferência!             \n");
        sb.append("====================================================\n");
        return sb.toString();
    }
}
