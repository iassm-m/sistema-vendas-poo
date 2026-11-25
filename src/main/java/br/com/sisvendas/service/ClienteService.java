package br.com.sisvendas.service;

import br.com.sisvendas.dao.ClienteDAO;
import br.com.sisvendas.model.Cliente;
import java.util.List;

/**
 * Camada de Serviço para regras de negócio relacionadas a Clientes.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        cliente.validar();

        // Verifica unicidade de CPF
        Cliente existente = clienteDAO.buscarPorCpf(cliente.getCpf());
        if (existente != null && (cliente.getId() == null || !existente.getId().equals(cliente.getId()))) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com o CPF: " + cliente.getCpf());
        }

        if (cliente.getId() == null) {
            clienteDAO.salvar(cliente);
        } else {
            clienteDAO.atualizar(cliente);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }
        clienteDAO.excluir(id);
    }

    public Cliente buscarPorId(Integer id) {
        if (id == null) return null;
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteDAO.buscarPorNome(nome);
    }
}
