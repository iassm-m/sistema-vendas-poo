package br.com.sisvendas.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Classe abstrata base para entidades do sistema.
 * Demonstra os conceitos de Herança, Polimorfismo e Encapsulamento da POO.
 * 
 * @author Iasmim José
 */
public abstract class EntidadeBase implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected LocalDateTime dataCriacao;
    protected LocalDateTime dataUltimaModificacao;

    public EntidadeBase() {
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaModificacao = LocalDateTime.now();
    }

    public EntidadeBase(Integer id) {
        this();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.dataUltimaModificacao = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaModificacao() {
        return dataUltimaModificacao;
    }

    public void setDataUltimaModificacao(LocalDateTime dataUltimaModificacao) {
        this.dataUltimaModificacao = dataUltimaModificacao;
    }

    public String getDataCriacaoFormatada() {
        if (dataCriacao == null) return "";
        return dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntidadeBase that = (EntidadeBase) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Valida os atributos da entidade conforme as regras de negócio.
     * @throws IllegalArgumentException caso alguma regra seja violada.
     */
    public abstract void validar();
}
