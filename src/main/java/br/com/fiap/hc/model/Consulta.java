package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class Consulta {

    private int idConsulta;
    private Date dataHora;
    private String status;
    private String areaMedica;
    private Paciente paciente;
    private Medico medico;

    public Consulta() {
    }

    public Consulta(int idConsulta, Date dataHora, String status, String areaMedica, Paciente paciente, Medico medico) {
        this.idConsulta = idConsulta;
        this.dataHora = dataHora;
        this.status = status;
        this.areaMedica = areaMedica;
        this.paciente = paciente;
        this.medico = medico;
    }

    public Consulta(int idConsulta, String dataHora, String status, String areaMedica, Paciente pacienteLogado, Medico medicoEscolhido) {
    }

    public Consulta(int idConsulta) {
    }

    @Override
    public String toString() {
        return "idConsulta: " + idConsulta + ", Data e Hora da Consulta: " + dataHora + ", Status: " +
                status + " Area medica da Consulta: " + areaMedica + ", Paciente: " + paciente + ", Medico: " + medico;
    }


}
