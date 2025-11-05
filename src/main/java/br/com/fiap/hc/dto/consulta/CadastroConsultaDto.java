package br.com.fiap.hc.dto.consulta;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import java.util.Date;

@Getter
@Setter

public class CadastroConsultaDto {

    @Future(message = "A data e hora deve estar no futuro")
    private Date dataHora;

    @NotBlank
    private String status;

    @NotBlank
    @Size(max = 75)
    private String areaMedica;

}
