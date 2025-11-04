package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class Endereco {

    private int idEndereco;
    private String logradouro;
    private int numero;
    private String complemento;
    private String bairro;
    private String estado;
    private String cidade;
    private String cep;

    public Endereco() {
    }

    public Endereco(int idEndereco, String logradouro, int numero, String complemento, String bairro, String estado, String cidade, String cep) {
        this.idEndereco = idEndereco;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.estado = estado;
        this.cidade = cidade;
        this.cep = cep;
    }
    public Endereco(int idEndereco, String logradouro, int numero, String bairro, String cidade, String estado, String cep) {
        this.idEndereco = idEndereco;
        if (logradouro == null || logradouro.isEmpty()) {
            System.out.println("Erro: Logradouro recebido como null ou vazio no construtor!");
        }
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public Endereco(int idEndereco) {
    }





    @Override
    public String toString() {
        return "idEndereco: " + idEndereco + ", Logradouro: " + logradouro + ", Numero: " +
                numero + " Bairro: " + bairro + ", Estado: " + estado + ", CEP: " + cep;
    }
}

