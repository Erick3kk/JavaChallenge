package br.com.fiap.hc.dto.endereco;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CadastroEnderecoDto {

    @NotBlank
    @Size(max = 100)
    private String logradouro;

    @NotBlank
    @PositiveOrZero
    private int numero;

    @NotBlank
    @Size(max = 50)
    private String complemento;

    @NotBlank
    @Size(max = 50)
    private String bairro;

    @NotBlank
    @Size(max = 50)
    private String cidade;

    @NotBlank
    @Size(max = 2)
    private String estado;

    @NotBlank
    @Size(max = 8)
    private String cep;

}
