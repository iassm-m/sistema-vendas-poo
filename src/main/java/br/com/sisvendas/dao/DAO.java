package br.com.sisvendas.dao;

import br.com.sisvendas.model.EntidadeBase;
import java.util.List;

/**
 * Interface genérica para operações de CRUD (Data Access Object).
 * Demonstra abstração, polimorfismo e interfaces na POO.
 * 
 * @param <T> Tipo da entidade que estende EntidadeBase
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public interface DAO<T extends EntidadeBase> {

    /**
     * Insere uma nova entidade na base de dados simulada.
     * @param entidade a ser salva
     */
    void salvar(T entidade);

    /**
     * Atualiza os dados de uma entidade existente.
     * @param entidade atualizada
     */
    void atualizar(T entidade);

    /**
     * Remove uma entidade pelo seu identificador primário.
     * @param id identificador da entidade
     */
    void excluir(Integer id);

    /**
     * Consulta uma entidade pelo seu ID.
     * @param id identificador
     * @return Entidade encontrada ou null
     */
    T buscarPorId(Integer id);

    /**
     * Retorna todos os registros da tabela correspondente.
     * @return Lista contendo todas as entidades
     */
    List<T> listarTodos();
}
