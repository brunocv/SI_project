package Behaviours;

import Agents.AgenteUtilizador;
import Util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.*;

// O objetivo desta classe é eceber as propostas de destinos alternativos propostos pela estação e efetuar uma decisão em relaç̃ao ao incentivo proposto
public class TratamentoIncentivo extends CyclicBehaviour {

    AgenteUtilizador au;
    HashMap<String,Double> ocupsAnteriores;
    AID senderMensagemOriginal;

    public TratamentoIncentivo(AgenteUtilizador utilizador){
        au = utilizador;
    }

    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.PROPOSE) { parseProposals(msg); }

            else if(msg.getPerformative() == ACLMessage.INFORM) { parseInforms(msg); }

            else if(msg.getPerformative() == ACLMessage.CONFIRM){ myAgent.doDelete(); }
        }
    }

    public void parseProposals(ACLMessage msg){
        try {
            ACLMessage paraSystem = new ACLMessage(ACLMessage.REQUEST);
            paraSystem.setConversationId(msg.getConversationId());
            paraSystem.setContentObject(msg.getContentObject());
            ocupsAnteriores = (HashMap<String,Double>) msg.getContentObject();
            senderMensagemOriginal = msg.getSender();

            AID sistema = new AID();
            sistema.setLocalName("System");

            paraSystem.addReceiver(sistema);

            myAgent.send(paraSystem);

        } catch (IOException |UnreadableException e) { e.printStackTrace(); }
    }

    public void parseInforms(ACLMessage msg){
        try {
            HashMap<String, Coordenadas> proposicoes = (HashMap<String,Coordenadas>) msg.getContentObject();
            Coordenadas estacaoOrigem = au.getOrigem();
            Map<String, Double> fatorDeEscolha = new HashMap<>();

            Iterator it = proposicoes.entrySet().iterator();
            while(it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();
                Coordenadas posicao = (Coordenadas) pair.getValue();
                String estacao = (String) pair.getKey();

                if(!(posicao.equals(estacaoOrigem))) { // Verificar que o q vai ser recomendado não é a estação inicio
                    if (ocupsAnteriores.containsKey(estacao)) {
                        Coordenadas posUtilizador = au.getPosicao();
                        Coordenadas destUtilizador = au.getDestino();

                        Utilidade ut = new Utilidade();
                        //Distancai da posição do utilizador a uma estação recomendada
                        Double distNU = ut.distancia2pontos(posUtilizador.getCoordX(), posUtilizador.getCoordY(), posicao.getCoordX(), posicao.getCoordY());
                        //Distancia da posição do utilizador ao destino original
                        Double distOU = ut.distancia2pontos(posUtilizador.getCoordX(), posUtilizador.getCoordY(), destUtilizador.getCoordX(), destUtilizador.getCoordY());

                        String tipoIncentivo = msg.getConversationId();
                        if (distNU > distOU) {

                            Double divDistancia = distNU / distOU;

                            Double extraOcupacao = 0.0;
                            if (divDistancia > 2.0){ divDistancia = 2.0; }// Fator Máximo

                            if(tipoIncentivo.equals("Incentivo-1")){
                                extraOcupacao = (0.2 - ocupsAnteriores.get(estacao)) * 3.0;

                            }else if(tipoIncentivo.equals("Incentivo-2")){
                                extraOcupacao = (0.5 - ocupsAnteriores.get(estacao)) * 3.0;
                            }

                            Double fator = divDistancia + extraOcupacao + (Math.abs(au.getIncentivo()) * 0.1);
                            Double desconto = fator * 12.5;
                            Double fatorFinal = desconto / (distNU - distOU);

                            Double greediness = 0.1 * au.getGreed();

                            if (fatorFinal >= ( 0.9+greediness )) {
                                fatorDeEscolha.put(estacao, fatorFinal);
                            }
                        } else {
                            double fator = 1.2;

                            Double greediness = 0.1 * au.getGreed();

                            if (fator >= ( 0.9+greediness )) {
                                fatorDeEscolha.put(estacao, fator);
                            }
                        }
                    }
                }
            }

            ACLMessage respostaIncentivo = new ACLMessage();
            respostaIncentivo.addReceiver(senderMensagemOriginal);

            if(fatorDeEscolha.size() > 0){

                Map.Entry<String,Double> maxEntry = null;

                for(Map.Entry<String, Double> entry : fatorDeEscolha.entrySet()){
                    if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        maxEntry = entry;
                }

                System.out.println("Eu: "+ myAgent.getAID().getName() + " aceitei o incentivo: "+ maxEntry.getValue() +" para a estação : " + maxEntry.getKey());
                au.setNewDestino( maxEntry.getKey() , proposicoes.get(maxEntry.getKey()) );

                respostaIncentivo.setContent(maxEntry.getKey());
                respostaIncentivo.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

            }else{
                System.out.println("Eu: "+ myAgent.getAID().getName() + " não aceitei incentivos ");
                respostaIncentivo.setPerformative(ACLMessage.REJECT_PROPOSAL);
                au.reduceValue();
            }

            myAgent.send(respostaIncentivo);

        } catch (UnreadableException e) { e.printStackTrace(); }
    }
}
