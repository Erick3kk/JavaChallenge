package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class Receita {

    private int idReceita;
    private String medicamento;
    private String dosagem;
    private Date dataEmissao;
    private Consulta consulta;

    public Receita() {
    }

    public Receita(int idReceita, String medicamento, String dosagem, Date dataEmissao, Consulta consulta) {
        this.idReceita = idReceita;
        this.medicamento = medicamento;
        this.dosagem = dosagem;
        this.dataEmissao = dataEmissao;
        this.consulta = consulta;
    }

    @Override
    public String toString() {
        return "idReceita: " + idReceita + ", Medicamento: " + medicamento + ", Dosagem: " +
                dosagem + " Data de Emissao: " + dataEmissao;
    }
}
