package Agents;

import JadePlatform.MainContainer;
import Util.Mapa;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class AgenteSystem extends Agent {

    private int numero_utilizador;
    private Mapa mapa;
    private MainContainer mc;

    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        mc = (MainContainer)args[0];
        mapa = (Mapa)args[1];

        System.out.println("Agente System entrou: " + getAID().getName());

        numero_utilizador = 0;
        this.addBehaviour(new gerarUtilizadores(this,7500));

        //doDelete();
    }

    protected void takeDown() {
        super.takeDown();
        System.out.println("Agente System terminou: " + getAID().getName());

    }

    private class gerarUtilizadores extends TickerBehaviour{
        public gerarUtilizadores(Agent a,long timeout) {super(a,timeout);}

        protected void onTick(){
            String nome = "Utilizador"+numero_utilizador;
            mc.startUtilizador(nome,mapa);
            System.out.println("Agente Utilizador "+numero_utilizador+" criado.");
            numero_utilizador++;
        }
    }
}

