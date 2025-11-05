package br.com.fiap.hc.dto.paciente;

import br.com.fiap.hc.model.Endereco;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetalhesPacienteDto {

    private int idPaciente;
    private String nome;
    private String cpf;
    private String dataNascimeto;
    private String telefone;
    private String email;
    private Endereco endereco;

}
