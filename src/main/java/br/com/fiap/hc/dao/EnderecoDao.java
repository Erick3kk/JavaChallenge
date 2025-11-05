package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Endereco;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EnderecoDao {

    @Inject
    private DataSource dataSource;


    public void cadastrar(Endereco endereco) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {

            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO T_HC_ENDERECO (ID_ENDERECO, DS_LOGRADOURO, NR_NUMERO, DS_COMPLEMENTO, NM_BAIRRO, NM_CIDADE, SG_ESTADO, NR_CEP) +" +
                    "VALUES (SQ_HC_ENDERECO.nextval, ?, ?, ?, ?, ?, ?, ?)", new String[]{"ID_ENDERECO"});

            stmt.setString(1, endereco.getLogradouro());
            stmt.setInt(2, endereco.getNumero());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getCep());
            stmt.executeUpdate();

            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                endereco.setIdEndereco(resultSet.getInt(1));
            }
        }
    }


    public void atualizar(Endereco endereco) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_ENDERECO SET DS_LOGRADOURO = ?, NR_NUMERO = ?, DS_COMPLEMENTO = ?, NM_BAIRRO = ?, NM_CIDADE = ?, SG_ESTADO = ?, NR_CEP = ?" +
                    "                        WHERE ID_ENDERECO = ?");
            //Seta os parametros
            stmt.setString(1, endereco.getLogradouro());
            stmt.setInt(2, endereco.getNumero());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getCep());
            stmt.setInt(8, endereco.getIdEndereco());
            stmt.executeUpdate();

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Nao existe endereco para ser atualizado!!");
        }
    }


    public void deletar(int idEndereco) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_ENDERECO WHERE ID_ENDERECO = ?");
            stmt.setInt(1, idEndereco);
            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não tem endereco para ser deletado");
        }
    }


    public Endereco buscar(int idEndereco) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_ENDERECO WHERE ID_ENDERECO = ?");

            stmt.setInt(1, idEndereco);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Endereco não encontrado");

            return parseEndereco(rs);
        }
    }

    private Endereco parseEndereco(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_ENDERECO");
        String logradouro = rs.getString("DS_LOGRADOURO");
        int numero = rs.getInt("NR_NUMERO");
        String complemento = rs.getString("DS_COMPLEMENTO");
        String bairro = rs.getString("NM_BAIRRO");
        String cidade = rs.getString("NM_CIDADE");
        String estado = rs.getString("SG_ESTADO");
        String cep = rs.getString("NR_CEP");

        return new Endereco(id, logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public List<Endereco> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_ENDERECO");
            ResultSet rs = stmt.executeQuery();

            List<Endereco> lista = new ArrayList<>();
            while (rs.next()){
                Endereco endereco = parseEndereco(rs);
                lista.add(endereco);
            }
            return lista;
        }
    }

}
