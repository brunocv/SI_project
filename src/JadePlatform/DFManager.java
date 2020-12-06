package JadePlatform;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class DFManager {

    public static void registarAgente(Agent agente, String type){

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agente.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(agente.getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(agente, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

    }

    public static void deRegister(Agent agente){
        try{
            DFService.deregister(agente);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
