package br.com.fiap.hc.dto.receita;

import br.com.fiap.hc.model.Consulta;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class DetalhesReceitaDto {

    private int idReceita;
    private String medicamento;
    private String dosagem;
    private Date dataEmissao;
    private Consulta consulta;

}
