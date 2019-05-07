package alura.agenda.firebase;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import alura.agenda.dto.AlunosSync;
import alura.agenda.event.AtualizaListaAlunosEvent;
import alura.agenda.sinc.AlunoSincronizador;

public class AgendaMessagingService extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("Mensagem recebida", String.valueOf(mensagem));

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {
        String chaveDeAcesso = "alunoSync";
        if(mensagem.containsKey(chaveDeAcesso)){
            String json = mensagem.get(chaveDeAcesso);
            ObjectMapper mapper = new ObjectMapper();
            try {
                AlunosSync alunosSync = mapper.readValue(json, AlunosSync.class);
                new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunosSync);

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunosEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}