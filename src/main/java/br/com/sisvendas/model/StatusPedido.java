package br.com.sisvendas.model;

/**
 * Enumeração dos possíveis estados de um Pedido.
 * 
 * @author Iasmim José
 */
public enum StatusPedido {
    PENDENTE("Pendente"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
