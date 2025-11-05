package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ConsultaDao {

    @Inject
    private DataSource dataSource;

    private PacienteDao pacienteDao;
    private MedicoDao medicoDao;

    public ConsultaDao() throws SQLException, ClassNotFoundException {
        this.pacienteDao = new PacienteDao();
        this.medicoDao = new MedicoDao();
    }



    public void cadastrar(Consulta consulta) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {

            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO T_HC_CONSULTA (ID_CONSULTA, DT_HORA, ST_STATUS, DS_AREA_MEDICA, ID_PACIENTE, ID_MEDICO) " +
                    "VALUES (SQ_HC_CONSULTA.nextval, ?, ?, ?, ?, ?)", new String[]{"ID_CONSULTA"});

            stmt.setTimestamp(1, new Timestamp(consulta.getDataHora().getTime()));
            stmt.setString(2, consulta.getStatus());
            stmt.setString(3, consulta.getAreaMedica());
            stmt.setInt(4, consulta.getPaciente().getIdPaciente());
            stmt.setInt(5, consulta.getMedico().getIdMedico());

            stmt.executeUpdate();


            int rowsAffected = stmt.executeUpdate();
            System.out.println("Consulta cadastrada com sucesso. Linhas afetadas: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar consulta: " + e.getMessage());
            throw e;
        }
    }


    public Consulta buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Consulta não encontrado");
            //se encontrou recupera os valores e retorna
            return parseConsulta(rs);
        }
    }


    public List<Consulta> listarConsulta() throws SQLException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_CONSULTA");
            ResultSet rs = stmt.executeQuery();
            List<Consulta> consultas = new ArrayList<>();
            while (rs.next()){
                Consulta consulta = parseConsulta(rs);
                consultas.add(consulta);
            }
            return consultas;
        }
    }

    public void atualizar(Consulta consulta) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_CONSULTA SET DT_HORA = ?, ST_STATUS = ?, DS_AREA_MEDICA = ?, ID_PACIENTE = ?, ID_MEDICO = ? WHERE ID_CONSULTA = ?");

            stmt.setTimestamp(1, new Timestamp(consulta.getDataHora().getTime()));
            stmt.setString(2, consulta.getStatus());
            stmt.setString(3, consulta.getAreaMedica());
            stmt.setInt(4, consulta.getPaciente().getIdPaciente());
            stmt.setInt(5, consulta.getMedico().getIdMedico());
            stmt.setInt(6, consulta.getIdConsulta());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Consulta com ID " + consulta.getIdConsulta() + " não foi encontrada para atualização.");
            } else {
                System.out.println("Consulta atualizada com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }


    public void deletar(int idConsulta) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()){
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?");
            stmt.setInt(1, idConsulta);

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não tem consulta para deletar");
        }
    }

    private Consulta parseConsulta(ResultSet rs) throws SQLException, EntidadeNaoEncontradaException {
        int idPaciente = rs.getInt("ID_PACIENTE");
        int idMedico = rs.getInt("ID_MEDICO");

        Paciente paciente = pacienteDao.buscar(idPaciente);
        Medico medico = medicoDao.buscar(idMedico);

        return new Consulta(
                rs.getInt("ID_CONSULTA"),
                rs.getTimestamp("DT_HORA"),
                rs.getString("ST_STATUS"),
                rs.getString("DS_AREA_MEDICA"),
                paciente,
                medico
        );
    }


}