package br.com.fiap.hc.view;

import br.com.fiap.hc.dao.ConsultaDao;
import br.com.fiap.hc.dao.EnderecoDao;
import br.com.fiap.hc.dao.MedicoDao;
import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.dao.ReceitaDao;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Endereco;
import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;
import br.com.fiap.hc.model.Receita;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Terminal {

    @Inject
    private ConsultaDao consultaDao;

    @Inject
    private EnderecoDao enderecoDao;

    @Inject
    private MedicoDao medicoDao;

    @Inject
    private PacienteDao pacienteDao;

    @Inject
    private ReceitaDao receitaDao;

    @Inject
    private DataSource dataSource;




    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        ConsultaDao consultaDao = null;
        ReceitaDao receitaDao = null;
        PacienteDao pacienteDao = null;
        EnderecoDao enderecoDao = null;
        MedicoDao medicoDao = null;

        try {
            System.out.println("Iniciando Terminal");

            pacienteDao = new PacienteDao();
            enderecoDao = new EnderecoDao();
            medicoDao = new MedicoDao();
            consultaDao = new ConsultaDao();
            receitaDao = new ReceitaDao();

            int opcao;
            Paciente pacienteLogado = null;

            do {
                System.out.println("\nEscolha: ");
                System.out.println("1 - Fazer Cadastro (necessário para consulta)");
                System.out.println("2 - Acessar com Cadastro Existente");
                System.out.println("3 - Agendar Consulta (após cadastro)");
                System.out.println("4 - Visualizar Cadastros (Médicos, Pacientes, Consultas, Endereços)");
                System.out.println("5 - Finalizar Consulta (Mudar Status para REALIZADA)");
                System.out.println("6 - Gerenciar Receitas (Visualizar, Adicionar, Deletar)");
                System.out.println("7 - Deletar Cadastros (Paciente, Médico, Consulta, Receita, Endereço)");
                System.out.println("0 - Sair");
                opcao = leitor.nextInt();
                leitor.nextLine();

                System.out.println("Opção selecionada: " + opcao);

                switch (opcao) {
                    case 1:
                        System.out.println("Iniciando cadastro de paciente");
                        Endereco endereco = lerEndereco(leitor);
                        enderecoDao.cadastrar(endereco);
                        pacienteLogado = lerPaciente(leitor, endereco);
                        pacienteDao.cadastrar(pacienteLogado);
                        System.out.println("Paciente cadastrado com sucesso! ID: " + pacienteLogado.getIdPaciente());
                        break;

                    case 2:
                        System.out.println("Iniciando acesso com ID existente");
                        System.out.println("Digite o ID do paciente:");
                        int idBusca = leitor.nextInt();
                        leitor.nextLine();
                        try {
                            pacienteLogado = pacienteDao.buscar(idBusca);
                            System.out.println("Paciente encontrado: " + pacienteLogado.getNome());
                        } catch (EntidadeNaoEncontradaException e) {
                            System.out.println(e.getMessage());
                            pacienteLogado = null;
                        }
                        break;

                    case 3:
                        if (pacienteLogado == null) {
                            System.out.println("Você precisa estar cadastrado para agendar uma consulta. Escolha a opção 1 ou 2 primeiro");
                            break;
                        }
                        agendarConsulta(leitor, pacienteLogado, medicoDao, consultaDao);
                        break;

                    case 4:
                        visualizarCadastros(leitor, medicoDao, consultaDao, pacienteDao, enderecoDao);
                        break;

                    case 5:
                        finalizarConsulta(leitor, consultaDao);
                        break;

                    case 6:
                        gerenciarReceitas(leitor, receitaDao, consultaDao);
                        break;

                    case 7:
                        deletarCadastros(leitor, pacienteDao, medicoDao, consultaDao, receitaDao, enderecoDao);
                        break;

                    case 0:
                        System.out.println("Finalizando o sistema");
                        break;

                    default:
                        System.out.println("Opção inválida!");
                }

            } while (opcao != 0);

        } catch (Exception e) {
            System.err.println("Erro geral no programa: " + e.getMessage() + (e.getMessage() == null ? " (Verifique a causa raiz no Stack Trace)" : ""));
            e.printStackTrace();
        } finally {
            System.out.println("Fechando Scanner e conexões dos DAOs...");
            leitor.close();
        }
    }

    private static void agendarConsulta(Scanner leitor, Paciente pacienteLogado, MedicoDao medicoDao, ConsultaDao consultaDao) throws SQLException {
        System.out.println("Iniciando agendamento de consulta");
        List<Medico> medicos;
        try {
            medicos = medicoDao.listarMedicos();
            if (medicos.isEmpty()) {
                System.out.println("Nenhum médico cadastrado no sistema. Verifique a tabela T_HC_MEDICO");
                return;
            }

            System.out.println("\nMédicos disponíveis");
            for (int i = 0; i < medicos.size(); i++) {
                System.out.println((i + 1) + " - " + medicos.get(i).getNomeM() + " (Especialidade: " + medicos.get(i).getEspecialidade() + ")");
            }

            System.out.println("Escolha o número do médico pela especialidade desejada:");
            int escolhaMedico = leitor.nextInt() - 1;
            leitor.nextLine();
            if (escolhaMedico < 0 || escolhaMedico >= medicos.size()) {
                System.out.println("Opção inválida!");
                return;
            }

            Medico medicoEscolhido = medicos.get(escolhaMedico);
            System.out.println("Médico escolhido: " + medicoEscolhido.getNomeM());

            System.out.println("Digite o ID da consulta:");
            int idConsulta = leitor.nextInt();
            leitor.nextLine();

            System.out.println("Digite a data e hora da consulta (yyyy-MM-dd HH:mm:ss):");
            String dataHoraStr = leitor.nextLine();
            Date dataHora = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setLenient(false);
                dataHora = dateFormat.parse(dataHoraStr);
                System.out.println("Data convertida com sucesso: " + dataHora);
            } catch (ParseException e) {
                System.out.println("Erro ao converter data: " + e.getMessage() + ". Use o formato yyyy-MM-dd HH:mm:ss (ex.: 2025-10-01 02:18:00)");
                return;
            }

            System.out.println("Digite o status (MARCADA, REALIZADA, CANCELADA):");
            String status = leitor.nextLine();

            String areaMedica = medicoEscolhido.getEspecialidade();
            System.out.println("Área Médica sugerida: " + areaMedica + ". Confirme ou digite outra:");
            String areaConfirmada = leitor.nextLine();

            Consulta novaConsulta = new Consulta(idConsulta, dataHora, status, areaConfirmada.isEmpty() ? areaMedica : areaConfirmada, pacienteLogado, medicoEscolhido);
            System.out.println("Consulta a ser cadastrada: " + novaConsulta.toString());
            consultaDao.cadastrar(novaConsulta);
            System.out.println("Consulta agendada com sucesso! ID: " + idConsulta);
        } catch (SQLException e) {
            System.out.println("Erro ao listar médicos ou agendar consulta: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void finalizarConsulta(Scanner leitor, ConsultaDao consultaDao) {
        System.out.println("Finalizando consulta");
        try {
            System.out.println("Digite o ID da consulta a finalizar:");
            int idConsulta = leitor.nextInt();
            leitor.nextLine();


            Consulta consulta = consultaDao.buscar(idConsulta);

            if (!"MARCADA".equals(consulta.getStatus())) {
                System.out.println("A consulta já foi finalizada ou cancelada");
                return;
            }
            consulta.setStatus("REALIZADA");
            consultaDao.atualizar(consulta);
            System.out.println("Consulta finalizada com sucesso! ID: " + idConsulta);
            System.out.println("Use a Opção 6 para cadastrar uma receita para esta consulta.");

        } catch (SQLException e) {
            System.out.println("Erro ao finalizar consulta: " + e.getMessage());
            e.printStackTrace();
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Erro: Objeto nulo. Verifique se a consulta foi carregada corretamente.");
            e.printStackTrace();
        }
    }

    private static void visualizarCadastros(Scanner leitor, MedicoDao medicoDao, ConsultaDao consultaDao, PacienteDao pacienteDao, EnderecoDao enderecoDao) {
        System.out.println("\n--- VISUALIZAR CADASTROS ---");
        System.out.println("1 - Médicos");
        System.out.println("2 - Consultas");
        System.out.println("3 - Pacientes");
        System.out.println("4 - Endereços");
        System.out.println("0 - Voltar");
        int subOpcao = leitor.nextInt();
        leitor.nextLine();

        try {
            switch (subOpcao) {
                case 1:
                    System.out.println("\n--- MÉDICOS CADASTRADOS ---");
                    List<Medico> medicosTeste = medicoDao.listarMedicos();
                    for (Medico m : medicosTeste) {
                        System.out.println("IdMedico: " + m.getIdMedico() + ", NomeM: " + m.getNomeM() + ", Especialidade: " + m.getEspecialidade());
                    }
                    break;
                case 2:
                    System.out.println("\n--- CONSULTAS CADASTRADAS ---");
                    List<Consulta> consultas = consultaDao.listarConsulta();
                    if (consultas.isEmpty()) {
                        System.out.println("Nenhuma consulta cadastrada no sistema");
                        break;
                    }
                    for (Consulta c : consultas) {
                        String nomePaciente = c.getPaciente() != null ? c.getPaciente().getNome() : "NULO/ID Inválido";
                        String nomeMedico = c.getMedico() != null ? c.getMedico().getNomeM() : "NULO/ID Inválido";

                        System.out.println("ID: " + c.getIdConsulta() + ", Data/Hora: " + c.getDataHora() +
                                ", Status: " + c.getStatus() + ", Paciente: " + nomePaciente + ", Médico: " + nomeMedico);
                    }
                    break;
                case 3:
                    System.out.println("\n--- PACIENTES CADASTRADOS ---");
                    List<Paciente> pacientesTeste = pacienteDao.listar();
                    if (pacientesTeste.isEmpty()) {
                        System.out.println("Nenhum paciente cadastrado.");
                    } else {
                        for (Paciente p : pacientesTeste) {
                            System.out.println("ID: " + p.getIdPaciente() + ", Nome: " + p.getNome() + ", CPF: " + p.getCpf());
                        }
                    }
                    break;
                case 4:
                    System.out.println("\n--- ENDEREÇOS CADASTRADOS ---");
                    List<Endereco> enderecosTeste = enderecoDao.listar();
                    if (enderecosTeste.isEmpty()) {
                        System.out.println("Nenhum endereço cadastrado.");
                    } else {
                        for (Endereco e : enderecosTeste) {
                            System.out.println("ID: " + e.getIdEndereco() + ", Logradouro: " + e.getLogradouro() + ", Cidade/UF: " + e.getCidade() + "/" + e.getEstado());
                        }
                    }
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void gerenciarReceitas(Scanner leitor, ReceitaDao receitaDao, ConsultaDao consultaDao) {
        System.out.println("\n--- GERENCIAMENTO DE RECEITAS ---");
        System.out.println("1 - Adicionar Nova Receita");
        System.out.println("2 - Visualizar Todas as Receitas");
        System.out.println("3 - Deletar Receita");
        System.out.println("0 - Voltar");
        int subOpcao = leitor.nextInt();
        leitor.nextLine();

        try {
            switch (subOpcao) {
                case 1:
                    System.out.println("--- ADICIONAR RECEITA ---");
                    System.out.println("Digite o ID da consulta à qual esta receita pertence:");
                    int idConsulta = leitor.nextInt();
                    leitor.nextLine();

                    Consulta consulta = consultaDao.buscar(idConsulta);

                    System.out.println("Digite o ID da receita:");
                    int idReceita = leitor.nextInt();
                    leitor.nextLine();
                    System.out.println("Digite o medicamento:");
                    String medicamento = leitor.nextLine();
                    System.out.println("Digite a dosagem:");
                    String dosagem = leitor.nextLine();

                    System.out.println("Digite a data de emissão (yyyy-MM-dd):");
                    String dataEmissaoStr = leitor.nextLine();
                    Date dataEmissao = new SimpleDateFormat("yyyy-MM-dd").parse(dataEmissaoStr);

                    Receita receita = new Receita(idReceita, medicamento, dosagem, dataEmissao, consulta);
                    receitaDao.cadastrar(receita);
                    System.out.println("Receita cadastrada com sucesso! ID: " + idReceita);
                    break;
                case 2:
                    System.out.println("--- VISUALIZAR RECEITAS ---");
                    List<Receita> receitas = receitaDao.listar();
                    if (receitas.isEmpty()) {
                        System.out.println("Nenhuma receita cadastrada");
                    } else {
                        System.out.println("\nReceitas encontradas");
                        for (Receita r : receitas) {
                            System.out.println("ID: " + r.getIdReceita() + ", Medicamento: " + r.getMedicamento() +
                                    ", Dosagem: " + r.getDosagem() + ", Data Emissão: " + r.getDataEmissao() +
                                    ", Consulta ID: " + r.getConsulta().getIdConsulta());
                        }
                    }
                    break;
                case 3:
                    System.out.println("--- DELETAR RECEITA ---");
                    System.out.println("Digite o ID da receita a deletar:");
                    int idReceitaDeletar = leitor.nextInt();
                    leitor.nextLine();
                    receitaDao.deletar(idReceitaDeletar);
                    System.out.println("Receita deletada com sucesso! ID: " + idReceitaDeletar);
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (SQLException | ParseException | EntidadeNaoEncontradaException e) {
            System.err.println("Erro no gerenciamento de receitas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deletarCadastros(Scanner leitor, PacienteDao pacienteDao, MedicoDao medicoDao, ConsultaDao consultaDao, ReceitaDao receitaDao, EnderecoDao enderecoDao) {
        System.out.println("\n--- DELETAR CADASTROS ---");
        System.out.println("ATENÇÃO: A exclusão de entidades pode violar chaves estrangeiras.");
        System.out.println("1 - Deletar Paciente");
        System.out.println("2 - Deletar Médico");
        System.out.println("3 - Deletar Consulta");
        System.out.println("4 - Deletar Receita");
        System.out.println("5 - Deletar Endereço");
        System.out.println("0 - Voltar");
        int subOpcao = leitor.nextInt();
        leitor.nextLine();

        try {
            System.out.println("Digite o ID do item a ser deletado:");
            int idDeletar = leitor.nextInt();
            leitor.nextLine();

            switch (subOpcao) {
                case 1:
                    pacienteDao.deletar(idDeletar);
                    System.out.println("Paciente ID " + idDeletar + " removido.");
                    break;
                case 2:
                    medicoDao.deleteMedico(idDeletar);
                    System.out.println("Médico ID " + idDeletar + " removido.");
                    break;
                case 3:
                    consultaDao.deletar(idDeletar);
                    System.out.println("Consulta ID " + idDeletar + " removida.");
                    break;
                case 4:
                    receitaDao.deletar(idDeletar);
                    System.out.println("Receita ID " + idDeletar + " removida.");
                    break;
                case 5:
                    enderecoDao.deletar(idDeletar);
                    System.out.println("Endereço ID " + idDeletar + " removido.");
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar: " + e.getMessage() + " (Verifique chaves estrangeiras dependentes.)");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado ao deletar: " + e.getMessage());
        }
    }

    private static Endereco lerEndereco(Scanner leitor) {
        System.out.println("Digite o ID do endereco: ");
        int id = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Digite o logradouro: ");
        String logradouro = leitor.nextLine().trim();
        while (logradouro == null || logradouro.isEmpty()) {
            System.out.println("Logradouro não pode ser nulo ou vazio. Digite novamente: ");
            logradouro = leitor.nextLine().trim();
        }

        System.out.println("Logradouro lido: [" + logradouro + "]");

        System.out.println("Digite o numero: ");
        int numero = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Digite o bairro: ");
        String bairro = leitor.nextLine().trim();

        System.out.println("Digite a cidade: ");
        String cidade = leitor.nextLine().trim();

        System.out.println("Digite o estado (UF): ");
        String estado = leitor.nextLine().trim();

        System.out.println("Digite o CEP: ");
        String cep = leitor.nextLine().trim();

        return new Endereco(id, logradouro, numero, bairro, cidade, estado, cep);
    }

    private static Paciente lerPaciente(Scanner leitor, Endereco endereco) {
        System.out.println("Digite o ID do paciente:");
        int id = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Digite o nome do paciente:");
        String nome = leitor.nextLine();

        System.out.println("Digite o CPF (apenas números):");
        String cpf = leitor.nextLine().replaceAll("[^0-9]", "");

        System.out.println("Digite a data de nascimento (yyyy-mm-dd):");
        String dataNascimento = leitor.nextLine();

        System.out.println("Digite o telefone:");
        String telefone = leitor.nextLine();

        System.out.println("Digite o email:");
        String email = leitor.nextLine();

        return new Paciente(id, nome, cpf, dataNascimento, telefone, email, endereco);
    }
}