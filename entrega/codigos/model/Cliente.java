package br.com.sisvendas.model;

/**
 * Representa o Cliente no domínio do sistema SisVendas.
 * Herda de EntidadeBase.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class Cliente extends EntidadeBase {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String logradouro;
    private String cidade;

    public Cliente() {
        super();
    }

    public Cliente(Integer id, String nome, String cpf, String email, String telefone, String logradouro, String cidade) {
        super(id);
        this.nome = nome;
        this.cpf = formatarCpf(cpf);
        this.email = email;
        this.telefone = telefone;
        this.logradouro = logradouro;
        this.cidade = cidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = formatarCpf(cpf);
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
        setDataUltimaModificacao(java.time.LocalDateTime.now());
    }

    @Override
    public void validar() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (cpf == null || !validarCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido ou não informado. Deve conter 11 dígitos.");
        }
        if (email != null && !email.trim().isEmpty() && !email.contains("@")) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
    }

    private String formatarCpf(String doc) {
        if (doc == null) return null;
        String limpo = doc.replaceAll("[^0-9]", "");
        if (limpo.length() == 11) {
            return String.format("%s.%s.%s-%s",
                    limpo.substring(0, 3),
                    limpo.substring(3, 6),
                    limpo.substring(6, 9),
                    limpo.substring(9, 11));
        }
        return doc;
    }

    private boolean validarCpf(String doc) {
        String limpo = doc.replaceAll("[^0-9]", "");
        return limpo.length() == 11;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (CPF: %s)", getId(), nome, cpf);
    }
}
