package br.com.sisvendas.dao;

import br.com.sisvendas.model.Pedido;
import br.com.sisvendas.model.StatusPedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Access Object para a entidade Pedido / Venda.
 * Implementa a interface genérica DAO<Pedido>.
 * 
 * @author Iasmim José
 */
public class PedidoDAO implements DAO<Pedido> {

    private final BancoDadosMemoria db = BancoDadosMemoria.getInstancia();

    @Override
    public void salvar(Pedido pedido) {
        db.inserirPedido(pedido);
    }

    @Override
    public void atualizar(Pedido pedido) {
        db.atualizarPedido(pedido);
    }

    @Override
    public void excluir(Integer id) {
        db.excluirPedido(id);
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return db.buscarPedidoPorId(id);
    }

    @Override
    public List<Pedido> listarTodos() {
        return db.listarPedidos();
    }

    /**
     * Consulta pedidos por cliente.
     */
    public List<Pedido> buscarPorCliente(Integer clienteId) {
        List<Pedido> res = new ArrayList<>();
        for (Pedido p : listarTodos()) {
            if (p.getCliente() != null && Objects.equals(p.getCliente().getId(), clienteId)) {
                res.add(p);
            }
        }
        return res;
    }

    /**
     * Consulta pedidos filtrando pelo status.
     */
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        List<Pedido> res = new ArrayList<>();
        for (Pedido p : listarTodos()) {
            if (p.getStatus() == status) {
                res.add(p);
            }
        }
        return res;
    }
}
