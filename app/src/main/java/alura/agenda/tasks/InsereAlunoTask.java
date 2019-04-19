package alura.agenda.tasks;

import android.os.AsyncTask;

import alura.agenda.converter.AlunoConverter;
import alura.agenda.modelo.Aluno;
import alura.agenda.web.WebClient;

public class InsereAlunoTask extends AsyncTask {
    private Aluno aluno;

    public InsereAlunoTask(Aluno aluno) {
        this.aluno = aluno;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String json = new AlunoConverter().converteParaJSONCompleto(aluno);
        new WebClient().insere(json);
        return null;
    }
}
