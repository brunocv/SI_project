package Agents;

import jade.core.Agent;

public class AgenteSystem extends Agent {

    protected void setup() {
        super.setup();
        System.out.println("Agente System entrou: " + getAID().getName());
        //doDelete();
    }

    protected void takeDown() {
        super.takeDown();
        System.out.println("Agente System terminou: " + getAID().getName());

    }
}
