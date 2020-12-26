package Behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class PedirOcupacao extends TickerBehaviour {

    // Esta classe é responsavel pelo  envio  de  mensagens  a estaç̃oes  para  pedir  a  sua  ocupaçao,  para  isso  utiliza  um TickerBehaviour com um tick definido em 4 segundos
    public PedirOcupacao(Agent a, long timeout){
        super(a,timeout);
    }

    protected void onTick(){
        try {
            //Construir a descrição de agente Estação
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Estacao");
            dfd.addServices(sd);

            //Array de Resultados da procura por estações
            DFAgentDescription[] resultados = DFService.search(myAgent, dfd);
            DFAgentDescription dfd1 = resultados[0];
            AID estacao = dfd1.getName();

            String mensagem ="Ocupacao";

            ACLMessage aEnviar = new ACLMessage(ACLMessage.REQUEST);
            aEnviar.addReceiver(estacao);
            aEnviar.setContent(mensagem);
            myAgent.send(aEnviar);

        }
        catch (FIPAException fe){
            fe.printStackTrace();
        }
    }
}
