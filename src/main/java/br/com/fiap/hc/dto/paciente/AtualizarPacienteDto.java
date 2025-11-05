package br.com.fiap.hc.dto.paciente;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AtualizarPacienteDto {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 12)
    private String cpf;

    @NotBlank
    @Past(message = "A data deve estar no passado")
    private String dataNascimeto;

    @NotBlank
    private String telefone;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter formato valido")
    private String email;

}
