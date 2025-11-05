package br.com.fiap.hc.dto.receita;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class AtualizarReceitaDto {

    @Size(max = 150)
    private String medicamento;

    @NotBlank
    private String dosagem;

    @NotBlank
    private Date dataEmissao;
}
