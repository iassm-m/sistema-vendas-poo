package br.com.sisvendas.dao;

import br.com.sisvendas.model.Produto;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Produto.
 * Implementa a interface genérica DAO<Produto>.
 * 
 * @author IASMIM DA SILVA MANN JOSÉ - 1250109566, REBECA MACHADO DE LIMA - 1250103657, SARAH GABRIELLA VIRGÍNIA DA SILVA - 1250106978, SILVANA GOMES DA SILVA - 1250109767
 */
public class ProdutoDAO implements DAO<Produto> {

    private final BancoDadosMemoria db = BancoDadosMemoria.getInstancia();

    @Override
    public void salvar(Produto produto) {
        db.inserirProduto(produto);
    }

    @Override
    public void atualizar(Produto produto) {
        db.atualizarProduto(produto);
    }

    @Override
    public void excluir(Integer id) {
        db.excluirProduto(id);
    }

    @Override
    public Produto buscarPorId(Integer id) {
        return db.buscarProdutoPorId(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return db.listarProdutos();
    }

    /**
     * Consulta produtos por nome ou código.
     */
    public List<Produto> buscarPorNomeOuCodigo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }
        String busca = termo.toLowerCase().trim();
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : listarTodos()) {
            boolean matchNome = p.getNome() != null && p.getNome().toLowerCase().contains(busca);
            boolean matchCod = p.getCodigo() != null && p.getCodigo().toLowerCase().contains(busca);
            boolean matchCat = p.getCategoria() != null && p.getCategoria().toLowerCase().contains(busca);
            if (matchNome || matchCod || matchCat) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
