package alura.agenda.converter;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

import alura.agenda.modelo.Aluno;

/**
 * Created by Matheus on 15/02/2019.
 */

public class AlunoConverter {
    public String converteParaJSON(List<Aluno> alunos) {

        JSONStringer js = new JSONStringer();

        try{
            js.object().key("List").array().object().key("aluno").array();
            for(Aluno aluno: alunos){
                js.object();
                js.key("nome").value(aluno.getNome());
                js.key("nota").value(aluno.getNota());
                js.endObject();
            }
            js.endArray().endObject().endArray().endObject();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return js.toString();
    }

    public String converteParaJSONCompleto(Aluno aluno) {
        JSONStringer js = new JSONStringer();

        try {
            js.object()
                    .key("nome").value(aluno.getNome())
                    .key("endereco").value(aluno.getEndereco())
                    .key("site").value(aluno.getSite())
                    .key("telefone").value(aluno.getTelefone())
                    .key("nota").value(aluno.getNota())
                    .endObject();
            return js.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
