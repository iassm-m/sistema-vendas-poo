package br.com.sisvendas.model;

/**
 * Enumeração das formas de pagamento aceitas no SisVendas.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public enum FormaPagamento {
    DINHEIRO("Dinheiro (5% desc.)", 0.05),
    PIX("PIX (5% desc.)", 0.05),
    CARTAO_DEBITO("Cartão de Débito", 0.00),
    CARTAO_CREDITO("Cartão de Crédito", 0.00);

    private final String descricao;
    private final double descontoPadrao;

    FormaPagamento(String descricao, double descontoPadrao) {
        this.descricao = descricao;
        this.descontoPadrao = descontoPadrao;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getDescontoPadrao() {
        return descontoPadrao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
