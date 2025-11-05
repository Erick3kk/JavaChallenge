package br.com.fiap.hc.dto.medico;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AtualizarMedicoDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, min = 2)
    private String nomeM;

    @Size(max = 20)
    private String crm;

    @NotBlank(message = "Especialidade é obrigatório")
    @Size(max = 100, min = 2)
    private String especialidade;

    @Email(message = "O email deve ter formato valido")
    private String email;

    @NotBlank
    @Size(max = 20)
    private String telefone;
}
