package br.com.sisvendas.dao;

import br.com.sisvendas.model.Cliente;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Cliente.
 * Implementa a interface genérica DAO<Cliente>.
 * 
 * @author Iasmim José
 */
public class ClienteDAO implements DAO<Cliente> {

    private final BancoDadosMemoria db = BancoDadosMemoria.getInstancia();

    @Override
    public void salvar(Cliente cliente) {
        db.inserirCliente(cliente);
    }

    @Override
    public void atualizar(Cliente cliente) {
        db.atualizarCliente(cliente);
    }

    @Override
    public void excluir(Integer id) {
        db.excluirCliente(id);
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        return db.buscarClientePorId(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return db.listarClientes();
    }

    /**
     * Consulta clientes filtrando pelo nome (busca incremental).
     */
    public List<Cliente> buscarPorNome(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }
        String busca = termo.toLowerCase().trim();
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : listarTodos()) {
            if (c.getNome() != null && c.getNome().toLowerCase().contains(busca)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    /**
     * Busca cliente por CPF exato ou parcial.
     */
    public Cliente buscarPorCpf(String cpf) {
        if (cpf == null) return null;
        String limpo = cpf.replaceAll("[^0-9]", "");
        for (Cliente c : listarTodos()) {
            if (c.getCpf() != null && c.getCpf().replaceAll("[^0-9]", "").equals(limpo)) {
                return c;
            }
        }
        return null;
    }
}
