package alura.agenda.dto;

import java.util.List;

import alura.agenda.modelo.Aluno;

public class AlunosSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;


    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

}
