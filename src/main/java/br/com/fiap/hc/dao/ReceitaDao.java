package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Receita;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ReceitaDao {


    @Inject
    private DataSource dataSource;


    public void cadastrar(Receita receita) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {

            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO T_HC_RECEITA (ID_RECEITA, DS_MEDICAMENTO, DS_DOSAGEM, DT_EMISSAO, ID_CONSULTA) \" +\n" +
                    "                        \"VALUES (?, ?, ?, ?, ?)");

            stmt.setInt(1, receita.getIdReceita());
            stmt.setString(2, receita.getMedicamento());
            stmt.setString(3, receita.getDosagem());
            stmt.setDate(4, new java.sql.Date(receita.getDataEmissao().getTime()));
            stmt.setInt(5, receita.getConsulta().getIdConsulta());
            stmt.executeUpdate();

            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                receita.setIdReceita(resultSet.getInt(1));
            }
        }
    }

    public void atualizar(Receita receita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_RECEITA SET DS_MEDICAMENTO = ?, DS_DOSAGEM = ?, DT_EMISSAO = ?, ID_CONSULTA = ? \" +\n" +
                    "                        \"WHERE ID_RECEITA = ?");

            stmt.setString(1, receita.getMedicamento());
            stmt.setString(2, receita.getDosagem());
            stmt.setDate(3, new java.sql.Date(receita.getDataEmissao().getTime()));
            stmt.setInt(4, receita.getConsulta().getIdConsulta());
            stmt.setInt(5, receita.getIdReceita());
            stmt.executeUpdate();

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não existe Receita para ser atualizado");
        }
    }

    public void deletar(int idReceita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_RECEITA WHERE ID_RECEITA = ?");
            stmt.setInt(1, idReceita);

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não tem Receita para deletar");
        }
    }

    public Receita buscar(int idReceita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_RECEITA WHERE ID_RECEITA = ?");

            stmt.setInt(1, idReceita);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Receita não encontrado");
            return parseReceita(rs);
        }
    }

    private Receita parseReceita(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_RECEITA");
        String medicamento = rs.getString("DS_MEDICAMENTO");
        String dosagem = rs.getString("DS_DOSAGEM");
        Date dataEmissao = rs.getDate("DT_EMISSAO");
        int idConsulta = rs.getInt("ID_CONSULTA");

        return new Receita(id, medicamento, dosagem, dataEmissao, new Consulta(idConsulta));
    }


    public List<Receita> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_RECEITA");
            ResultSet rs = stmt.executeQuery();
            List<Receita> lista = new ArrayList<>();

            while (rs.next()){
                Receita receitas = parseReceita(rs);
                lista.add(receitas);
            }
            return lista;
        }
    }
}