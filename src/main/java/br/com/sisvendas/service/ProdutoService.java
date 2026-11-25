package br.com.sisvendas.service;

import br.com.sisvendas.dao.ProdutoDAO;
import br.com.sisvendas.model.Produto;
import java.util.List;

/**
 * Camada de Serviço para regras de negócio relacionadas a Produtos.
 * 
 * @author Iasmim José
 */
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAO();
    }

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        produto.validar();

        if (produto.getId() == null) {
            produtoDAO.salvar(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }
        produtoDAO.excluir(id);
    }

    public Produto buscarPorId(Integer id) {
        if (id == null) return null;
        return produtoDAO.buscarPorId(id);
    }

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<Produto> buscarPorNomeOuCodigo(String termo) {
        return produtoDAO.buscarPorNomeOuCodigo(termo);
    }
}
