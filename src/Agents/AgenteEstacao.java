package Agents;

import JadePlatform.DFManager;
import Util.Coordenadas;
import jade.core.Agent;

import java.util.List;
import java.util.Map;

public class AgenteEstacao extends Agent {

    private Coordenadas posicaoEstacao;
    private int capacidadeEstacao;
    private int bicicletas;
    private List<Coordenadas> areaDeControlo;
    private Map<String,Coordenadas> utilizadorNaArea;
    private Map<String,Double> ocupacaoEstacao;

    protected void setup(){
        super.setup();
        DFManager.registarAgente(this,"Estacao");
        System.out.println("Agente estacao entrou: " + getAID().getName());

        Object[] args = getArguments();
        posicaoEstacao = (Coordenadas) args[0];
        areaDeControlo = (List<Coordenadas>) args[1];
        ocupacaoEstacao = (Map<String, Double>) args[2];
        capacidadeEstacao = 20;
        bicicletas = 10; // Random starting values, depois talvez seja melhor alterado dependendo do numero
                         // de estações que se vai ter. devido ao mapa ser dinamico.

        //doDelete();
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente estacao terminou: " + getAID().getName());
        DFManager.deRegister(this);

    }
}
