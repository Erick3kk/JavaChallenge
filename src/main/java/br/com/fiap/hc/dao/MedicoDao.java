package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Medico;
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
public class MedicoDao {
    
    @Inject
    private DataSource dataSource;

    public List<Medico> listarMedicos() throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement(
                    "SELECT * FROM T_HC_MEDICO");
            ResultSet rs = stmt.executeQuery();

            List<Medico> lista = new ArrayList<>();
            while (rs.next()){
                Medico medico = parseMedico(rs);
                lista.add(medico);
            }
            return lista;
        }
    }


    public Medico buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement(
                    "SELECT ID_MEDICO, NM_MEDICO, NR_CRM, DS_ESPECIALIDADE, DS_EMAIL, NR_TELEFONE FROM T_HC_MEDICO WHERE ID_MEDICO = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Medico não existe");


            return parseMedico(rs);
        }
    }


    public void cadastrarMedico(Medico medico) throws SQLException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement(
                    "INSERT INTO T_HC_MEDICO (ID_MEDICO, NM_MEDICO, NR_CRM, DS_ESPECIALIDADE, DS_EMAIL, NR_TELEFONE)" +
                            " VALUES (SQ_HC_MEDICO.nextval, ?, ?, ?, ?, ?)", new String[]{"ID_MEDICO"});

            stmt.setString(1, medico.getNomeM());
            stmt.setString(2, medico.getCrm());
            stmt.setString(3, medico.getEspecialidade());
            stmt.setString(4, medico.getEmail());
            stmt.setString(5, medico.getTelefone());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next())
                medico.setIdMedico(rs.getInt(1));

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar médico: " + e.getMessage());
            throw e;
        }
    }


    public void atualizarMedico(Medico medico) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_MEDICO SET NM_MEDICO = ?, NR_CRM = ?, DS_ESPECIALIDADE = ?, DS_EMAIL = ?, NR_TELEFONE = ? WHERE ID_MEDICO = ?");

            stmt.setString(1, medico.getNomeM());
            stmt.setString(2, medico.getCrm());
            stmt.setString(3, medico.getEspecialidade());
            stmt.setString(4, medico.getEmail());
            stmt.setString(5, medico.getTelefone());
            stmt.setInt(6, medico.getIdMedico());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Nenhum médico com ID " + medico.getIdMedico() + " foi encontrado para atualização.");
            } else {
                System.out.println("Médico atualizado com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }



    public void deleteMedico(int idMedico) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_MEDICO WHERE ID_MEDICO = ?");
            stmt.setInt(1, idMedico);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Nenhum médico com ID " + idMedico + " foi encontrado para exclusão.");
            } else {
                System.out.println("Médico removido com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }

    private Medico parseMedico(ResultSet rs) throws SQLException {

        int idMedico = rs.getInt("ID_MEDICO");
        String nomeM = rs.getString("NM_MEDICO");
        String crm = rs.getString("NR_CRM");
        String especialidade = rs.getString("DS_ESPECIALIDADE");
        String email = rs.getString("DS_EMAIL");
        String telefone = rs.getString("NR_TELEFONE");

        return new Medico(idMedico, nomeM, crm, especialidade, email, telefone);
    }
}