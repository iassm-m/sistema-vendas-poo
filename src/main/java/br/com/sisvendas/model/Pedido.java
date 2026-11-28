package br.com.sisvendas.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa a Venda / Pedido no domínio do sistema SisVendas.
 * Herda de EntidadeBase e compõe Cliente e Itens de Pedido.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class Pedido extends EntidadeBase {
    private static final long serialVersionUID = 1L;

    private String codigoPedido;
    private Cliente cliente;
    private final ArrayList<ItemPedido> itens = new ArrayList<>();
    private LocalDateTime dataVenda;
    private Double valorDesconto;
    private FormaPagamento formaPagamento;
    private StatusPedido status;
    private String observacao;

    public Pedido() {
        super();
        this.dataVenda = LocalDateTime.now();
        this.valorDesconto = 0.0;
        this.formaPagamento = FormaPagamento.DINHEIRO;
        this.status = StatusPedido.PENDENTE;
    }

    public Pedido(Integer id, String codigoPedido, Cliente cliente, FormaPagamento formaPagamento) {
        super(id);
        this.codigoPedido = codigoPedido;
        this.cliente = cliente;
        this.dataVenda = LocalDateTime.now();
        this.valorDesconto = 0.0;
        this.formaPagamento = formaPagamento != null ? formaPagamento : FormaPagamento.DINHEIRO;
        this.status = StatusPedido.PENDENTE;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(String codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getDataVendaFormatada() {
        if (dataVenda == null) return "";
        return dataVenda.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Double getValorDesconto() {
        return valorDesconto != null ? valorDesconto : 0.0;
    }

    public void setValorDesconto(Double valorDesconto) {
        this.valorDesconto = valorDesconto != null ? Math.max(0.0, valorDesconto) : 0.0;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // ==========================================
    // MÉTODOS DE NEGÓCIO DA POO
    // ==========================================

    /**
     * Adiciona um item ao pedido.
     * Se o produto já existir no pedido, incrementa a quantidade.
     */
    public synchronized void adicionarItem(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser superior a zero.");
        }

        // Verifica se item já existe para somar quantidade
        for (ItemPedido item : itens) {
            if (item.getProduto().equals(produto)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                setDataUltimaModificacao(LocalDateTime.now());
                return;
            }
        }

        int proximoNumero = itens.size() + 1;
        itens.add(new ItemPedido(proximoNumero, produto, quantidade));
        setDataUltimaModificacao(LocalDateTime.now());
    }

    /**
     * Restaura um item a partir da persistência preservando o preço unitário histórico.
     */
    public synchronized void restaurarItem(Integer numeroItem, Produto produto, int quantidade, double precoUnitario) {
        if (produto == null || quantidade <= 0) {
            return;
        }
        int num = (numeroItem != null && numeroItem > 0) ? numeroItem : itens.size() + 1;
        ItemPedido item = new ItemPedido(num, produto, quantidade);
        item.setPrecoUnitario(precoUnitario);
        itens.add(item);
    }

    /**
     * Remove item pelo índice da lista.
     */
    public synchronized void removerItem(int index) {
        if (index >= 0 && index < itens.size()) {
            itens.remove(index);
            // Reorganiza os números dos itens
            for (int i = 0; i < itens.size(); i++) {
                itens.get(i).setNumeroItem(i + 1);
            }
            setDataUltimaModificacao(LocalDateTime.now());
        }
    }

    public synchronized void limparItens() {
        itens.clear();
        setDataUltimaModificacao(LocalDateTime.now());
    }

    /**
     * Calcula o total bruto (sem descontos).
     */
    public double getSubtotalBruto() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * Calcula o desconto efetivo aplicado (maior valor entre desconto automático da
     * forma de pagamento e desconto manual, limitado ao subtotal bruto).
     */
    public double getDescontoEfetivo() {
        double bruto = getSubtotalBruto();
        double descAuto = 0.0;
        if (formaPagamento != null) {
            descAuto = bruto * formaPagamento.getDescontoPadrao();
        }
        double descEfetivo = Math.max(descAuto, getValorDesconto());
        return Math.min(bruto, Math.max(0.0, descEfetivo));
    }

    /**
     * Aplica o desconto padrão da forma de pagamento ou desconto manual.
     */
    public double getTotalComDesconto() {
        return Math.max(0.0, getSubtotalBruto() - getDescontoEfetivo());
    }

    /**
     * Conclui a venda, realizando a baixa atômica no estoque de cada produto.
     */
    public synchronized void finalizarVenda() {
        validar();
        if (this.status != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ser finalizados.");
        }
        // Pré-valida estoque de TODOS os itens antes de debitar (atomicidade)
        for (ItemPedido item : itens) {
            Produto prod = item.getProduto();
            int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
            if (prod == null) {
                throw new IllegalStateException("Item de pedido sem produto associado.");
            }
            int disponivel = prod.getQuantidadeEstoque() != null ? prod.getQuantidadeEstoque() : 0;
            if (disponivel < qtd) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + prod.getNome()
                        + ". Disponível: " + disponivel + ", Solicitado: " + qtd);
            }
        }
        // Debita estoque de cada item
        for (ItemPedido item : itens) {
            item.getProduto().debitarEstoque(item.getQuantidade());
        }
        this.status = StatusPedido.FINALIZADO;
        setDataUltimaModificacao(LocalDateTime.now());
    }

    /**
     * Cancela a venda e devolve os itens ao estoque caso tenha sido finalizada.
     */
    public synchronized void cancelarVenda() {
        if (this.status == StatusPedido.CANCELADO) {
            throw new IllegalStateException("O pedido já está cancelado.");
        }
        if (this.status == StatusPedido.FINALIZADO) {
            // Estorna produtos ao estoque
            for (ItemPedido item : itens) {
                item.getProduto().creditarEstoque(item.getQuantidade());
            }
        }
        this.status = StatusPedido.CANCELADO;
        setDataUltimaModificacao(LocalDateTime.now());
    }

    @Override
    public void validar() {
        if (cliente == null) {
            throw new IllegalArgumentException("O pedido deve possuir um cliente associado.");
        }
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter ao menos um produto.");
        }
    }

    @Override
    public String toString() {
        return String.format("Pedido %s - Cliente: %s - Total: R$ %.2f (%s)",
                codigoPedido != null ? codigoPedido : "#" + getId(),
                cliente != null ? cliente.getNome() : "N/D",
                getTotalComDesconto(),
                status != null ? status.getDescricao() : "N/D");
    }
}
