package Agents;

import JadePlatform.MainContainer;
import Util.Coordenadas;
import Util.Mapa;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AgenteSystem extends Agent {

    private int numero_utilizador; // id do user
    private Mapa mapa; // info do mapa
    private MainContainer mc;

    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        mc = (MainContainer)args[0];
        mapa = (Mapa)args[1];

        System.out.println("Agente System entrou: " + getAID().getName());

        numero_utilizador = 1;
        this.addBehaviour(new gerarUtilizadores(this,2000));
        this.addBehaviour(new receiveMessages());
        //doDelete();
    }

    protected void takeDown() {
        super.takeDown();
        System.out.println("Agente System terminou: " + getAID().getName());

    }
    // Responsavel pela criação de novos Agentes Utilizador utilizando um ticker behaviour
    private class gerarUtilizadores extends TickerBehaviour{
        public gerarUtilizadores(Agent a,long timeout) {super(a,timeout);}

        protected void onTick(){

            //if(numero_utilizador <= 60) {
                String nome = "Utilizador" + numero_utilizador;
                mc.startUtilizador(nome, mapa);
                numero_utilizador++;
            //}
        }
    }
    // utilizado  para  poder  comunicar  com  o  utilizador  de  modo  a fornecer informaçao que o assiste no calculo da escolha de aceitar ou recusar o incentivo proposto por uma estacao, utilizando um cyrclicBehaviour
    private class receiveMessages extends CyclicBehaviour {
        public void action(){
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                try {
                    HashMap<String,Double> estacoes = (HashMap<String,Double>) msg.getContentObject();

                    HashMap<String, Coordenadas> toReturn = new HashMap<>();

                    Iterator it = estacoes.entrySet().iterator();
                    while(it.hasNext()){

                        Map.Entry pair = (Map.Entry) it.next();
                        String estacao = (String) pair.getKey();
                        int numero = Integer.parseInt(estacao.substring(estacao.indexOf(" ")+1)); //Vai buscar o numero da estação

                        toReturn.put(estacao,mapa.getCoordenadasDaEstacao(numero));
                    }

                    ACLMessage resposta = new ACLMessage();
                    resposta.setConversationId(msg.getConversationId());
                    resposta.setContentObject(toReturn);
                    resposta.addReceiver(msg.getSender());
                    resposta.setPerformative(ACLMessage.INFORM);
                    myAgent.send(resposta);

                } catch (IOException | UnreadableException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

