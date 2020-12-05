package Agents;

import Util.Mapa;
import jade.core.Agent;

public class AgenteInterface extends Agent {

    private Mapa mapa;

    protected void setup(){
        super.setup();
        System.out.println("Agente interface entrou: " + getAID().getName());

        Object[] args = this.getArguments();
        this.mapa = (Mapa) args[0];
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente interface terminou: " + getAID().getName());

    }
}
