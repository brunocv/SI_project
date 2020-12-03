package Agents;

import JadePlatform.DFManager;
import Util.Coordenadas;
import jade.core.Agent;

import java.util.List;
import java.util.Map;

public class AgenteEstacao extends Agent {

    private Coordenadas posicaoEstacao;
    private int capacidadeEstacao;
    private List<Coordenadas> areaDeControlo;
    private Map<String,Coordenadas> utilizadorNaArea;
    private Map<String,Double> ocupacaoEstacao;

    protected void setup(){
        super.setup();
        System.out.println("Agente estacao entrou: " + getAID().getName());

        DFManager.registarAgente(this,"Estacao");

        //doDelete();
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente estacao terminou: " + getAID().getName());
        DFManager.deRegister(this);

    }
}
