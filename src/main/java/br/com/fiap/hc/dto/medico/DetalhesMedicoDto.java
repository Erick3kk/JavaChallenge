package br.com.fiap.hc.dto.medico;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetalhesMedicoDto {

    private int idMedico;
    private String nomeM;
    private String crm;
    private String especialidade;
    private String email;
    private String telefone;
}
