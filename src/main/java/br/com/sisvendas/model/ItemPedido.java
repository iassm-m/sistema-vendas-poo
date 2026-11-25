package br.com.sisvendas.model;

import java.io.Serializable;

/**
 * Representa um item componente do Pedido (composição/agregação).
 * 
 * @author Iasmim José
 */
public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer numeroItem;
    private Produto produto;
    private Integer quantidade;
    private Double precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Integer numeroItem, Produto produto, Integer quantidade) {
        this.numeroItem = numeroItem;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto != null ? produto.getPrecoUnitario() : 0.0;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null && (this.precoUnitario == null || this.precoUnitario == 0.0)) {
            this.precoUnitario = produto.getPrecoUnitario();
        }
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Double getSubtotal() {
        if (quantidade == null || precoUnitario == null) return 0.0;
        return quantidade * precoUnitario;
    }

    @Override
    public String toString() {
        return String.format("%d x %s @ R$ %.2f = R$ %.2f",
                quantidade,
                produto != null ? produto.getNome() : "Desconhecido",
                precoUnitario,
                getSubtotal());
    }
}
