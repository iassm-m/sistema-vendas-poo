package br.com.sisvendas.model;

/**
 * Representa o Produto no domínio do sistema SisVendas.
 * Herda de EntidadeBase.
 * 
 * @author Iasmim José
 */
public class Produto extends EntidadeBase {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private String categoria;
    private Double precoUnitario;
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;

    public Produto() {
        super();
        this.quantidadeEstoque = 0;
        this.estoqueMinimo = 5;
    }

    public Produto(Integer id, String codigo, String nome, String categoria, Double precoUnitario, Integer quantidadeEstoque) {
        super(id);
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = 5;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    /**
     * Regra de negócio: Realiza a baixa (débito) de unidades do estoque.
     * @param quantidade a ser debitada
     * @throws IllegalStateException se a quantidade solicitada for maior que a disponível
     */
    public synchronized void debitarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a debitar deve ser maior que zero.");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + nome + 
                ". Disponível: " + this.quantidadeEstoque + ", Solicitado: " + quantidade);
        }
        this.quantidadeEstoque -= quantidade;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    /**
     * Regra de negócio: Realiza o estorno ou reposição de estoque.
     * @param quantidade a ser creditada
     */
    public synchronized void creditarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a creditar deve ser maior que zero.");
        }
        this.quantidadeEstoque += quantidade;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public boolean isEstoqueBaixo() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public void validar() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (precoUnitario == null || precoUnitario <= 0.0) {
            throw new IllegalArgumentException("O preço unitário deve ser maior que zero.");
        }
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade de estoque não pode ser negativa.");
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - R$ %.2f (Estoque: %d)", codigo != null ? codigo : String.valueOf(getId()), nome, precoUnitario, quantidadeEstoque);
    }
}
