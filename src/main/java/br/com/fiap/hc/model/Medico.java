package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class Medico {

    private int idMedico;
    private String nomeM;
    private String crm;
    private String especialidade;
    private String email;
    private String telefone;

    public Medico() {
    }

    public Medico(int idMedico, String nomeM, String crm, String especialidade, String email, String telefone) {
        this.idMedico = idMedico;
        this.nomeM = nomeM;
        this.crm = crm;
        this.especialidade = especialidade;
        this.email = email;
        this.telefone = telefone;
    }

    public Medico(int idMedico) {
    }

    @Override
    public String toString() {
        return "idMedico: " + idMedico + ", Nome: " + nomeM + ", CRM: " +
                crm + " Especialidade: " + especialidade + ", Email: " + email + ", Telefone: " + telefone;
    }
}
