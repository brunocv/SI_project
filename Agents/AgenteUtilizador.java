package Agents;

import Util.Coordenadas;
import jade.core.Agent;

public class AgenteUtilizador extends Agent {

    private Coordenadas posicaoInicial;
    private Coordenadas posicaoAtual;
    private Coordenadas posicaoDestino;
    private Coordenadas estacaoDestino;
    private Coordenadas estacaoProxima;
    private double distanciaPercorrida;
    private int aceitouIncentivo;

    protected void setup(){
        super.setup();
        System.out.println("Agente utilizador entrou: " + getAID().getName());
        //doDelete();
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente utilizador terminou: " + getAID().getName());

    }

}
