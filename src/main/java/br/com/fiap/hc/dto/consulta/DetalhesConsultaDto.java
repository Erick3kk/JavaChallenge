package br.com.fiap.hc.dto.consulta;

import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class DetalhesConsultaDto {

    private int idConsulta;
    private Date dataHora;
    private String status;
    private String areaMedica;
    private Paciente paciente;
    private Medico medico;
}
