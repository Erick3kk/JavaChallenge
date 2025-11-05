package br.com.fiap.hc.dto.endereco;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetalhesEnderecoDto {

    private int idEndereco;
    private String logradouro;
    private int numero;
    private String complemento;
    private String bairro;
    private String estado;
    private String cidade;
    private String cep;

}
