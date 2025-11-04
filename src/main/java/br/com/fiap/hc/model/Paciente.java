package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Paciente {

    private int idPaciente;
    private String nome;
    private String cpf;
    private String dataNascimeto;
    private String telefone;
    private String email;
    private Endereco endereco;

    public Paciente() {
    }

    public Paciente(int idPaciente, String nome, String cpf, String dataNascimeto, String telefone, String email, Endereco endereco) {
        this.idPaciente = idPaciente;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimeto = dataNascimeto;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
    }

    public Paciente(int idPaciente) {
    }

    @Override
    public String toString() {
        return "idPaciente: " + idPaciente + ", Nome: " + nome + ", CPF: " +
                cpf + " Data de Nascimento: " + dataNascimeto + ", Telefone: " + telefone + ", Email: " + email + "Endereco: " + endereco;
    }
}
