package alura.agenda.sinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import alura.agenda.dao.AlunoDAO;
import alura.agenda.dto.AlunosSync;
import alura.agenda.event.AtualizaListaAlunosEvent;
import alura.agenda.modelo.Aluno;
import alura.agenda.preferences.AlunoPreferences;
import alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    public void buscaTodos(){
        if(preferences.temVersao()){
            buscaNovos();
        }else{
            buscaAlunos();
        }
    }

    private void buscaNovos() {
        String versao = preferences.getVersao();
        Call<AlunosSync> call = new RetrofitInicializador().getAlunoService().novos(versao);
        call.enqueue(buscaAlunoCallback());
    }

    private void buscaAlunos() {
        Call<AlunosSync> call = new RetrofitInicializador().getAlunoService().lista();
        call.enqueue(buscaAlunoCallback());
    }

    @NonNull
    private Callback<AlunosSync> buscaAlunoCallback() {
        return new Callback<AlunosSync>() {
            @Override
            public void onResponse(Call<AlunosSync> call, Response<AlunosSync> response) {
                AlunosSync alunosSync = response.body();
                sincroniza(alunosSync);

                Log.i("versao", preferences.getVersao());

                bus.post(new AtualizaListaAlunosEvent());
                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunosSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                bus.post(new AtualizaListaAlunosEvent());
            }
        };
    }

    public void sincroniza(AlunosSync alunosSync) {
        String versao = alunosSync.getMomentoDaUltimaModificacao();

        if(temVersaoNova(versao)) {

            preferences.salvaVersao(versao);

            Log.i("versao atual", preferences.getVersao());

            AlunoDAO dao = new AlunoDAO(context);
            dao.sincroniza(alunosSync.getAlunos());
            dao.close();
        }
    }

    private boolean temVersaoNova(String versao) {

        if(!preferences.temVersao())
            return true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date dataExterna = format.parse(versao);
            String versaoInterna = preferences.getVersao();

            Log.i("versao interna", versaoInterna);

            Date dataInterna = format.parse(versaoInterna);
            return dataExterna.after(dataInterna);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sincronizaAlunosInternos(){
        AlunoDAO dao = new AlunoDAO(context);

        List<Aluno> alunos = dao.listaNaoSincronizados();

        dao.close();

       Call<AlunosSync> call = new RetrofitInicializador().getAlunoService().atualiza(alunos);
       call.enqueue(new Callback<AlunosSync>() {
           @Override
           public void onResponse(Call<AlunosSync> call, Response<AlunosSync> response) {
               AlunosSync alunosSync = response.body();
               sincroniza(alunosSync);
           }

           @Override
           public void onFailure(Call<AlunosSync> call, Throwable t) {

           }
       });
    }

    public void deleta(Aluno aluno) {
        Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AlunoDAO dao = new AlunoDAO(context);
                dao.deleta(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}