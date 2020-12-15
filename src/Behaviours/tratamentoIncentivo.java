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

public class tratamentoIncentivo extends CyclicBehaviour {

    AgenteUtilizador au;
    HashMap<String,Double> ocupsAnteriores;
    AID senderMensagemOriginal;

    public tratamentoIncentivo(AgenteUtilizador utilizador){
        au = utilizador;
    }

    public void action() {

        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                try {

                    ACLMessage paraSystem = new ACLMessage();
                    paraSystem.setContentObject(msg.getContentObject());
                    ocupsAnteriores = (HashMap<String,Double>) msg.getContentObject();
                    senderMensagemOriginal = msg.getSender();

                    paraSystem.setPerformative(ACLMessage.REQUEST);

                    AID sistema = new AID();
                    sistema.setLocalName("System");

                    paraSystem.addReceiver(sistema);

                    myAgent.send(paraSystem);

                } catch (IOException |UnreadableException e) { e.printStackTrace(); }
            }else if(msg.getPerformative() == ACLMessage.INFORM) {

                try {
                    HashMap<String, Coordenadas> proposicoes = (HashMap<String,Coordenadas>) msg.getContentObject();
                    Coordenadas estacaoOrigem = au.getOrigem();
                    Map<String, Double> fatorDeEscolha = new HashMap<>();

                    Iterator it = proposicoes.entrySet().iterator();
                    while(it.hasNext()) {

                        Map.Entry pair = (Map.Entry) it.next();
                        Coordenadas posicao = (Coordenadas) pair.getValue();
                        String estacao = (String) pair.getKey();

                        if(!(posicao.equals(estacaoOrigem))) {
                            if (ocupsAnteriores.containsKey(estacao)) {
                                Coordenadas posUtilizador = au.getPosicao();
                                Coordenadas destUtilizador = au.getDestino();

                                Utilidade ut = new Utilidade();
                                //Distancai da posição do utilizador a uma estação recomendada
                                Double distNU = ut.distancia2pontos(posUtilizador.getCoordX(), posUtilizador.getCoordY(), posicao.getCoordX(), posicao.getCoordY());
                                //Distancia da posição do utilizador ao destino original
                                Double distOU = ut.distancia2pontos(posUtilizador.getCoordX(), posUtilizador.getCoordY(), destUtilizador.getCoordX(), destUtilizador.getCoordY());

                                if (distNU > distOU) {
                                    Double divDistancia = distNU / distOU;
                                    if (divDistancia > 2.0)
                                        divDistancia = 2.0; // Fator Máximo

                                    Double extraOcupacao = (0.2 - ocupsAnteriores.get(estacao)) * 3.0;
                                    Double fator = divDistancia + extraOcupacao + (Math.abs(au.getIncentivo()) * 0.1);

                                    Double desconto = fator * 12.5;

                                    Double fatorFinal = desconto / (distNU - distOU);
                                    System.out.println("ME: "+ myAgent.getAID().getName() + " FATOR: "+ fatorFinal +" Station: " + estacao);
                                    if (fatorFinal >= 0.8) {
                                        fatorDeEscolha.put(estacao, fatorFinal);
                                    }
                                } else {
                                    fatorDeEscolha.put(estacao, 20.0);
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

                        au.setNewDestino( maxEntry.getKey() , proposicoes.get(maxEntry.getKey()) );

                        respostaIncentivo.setContent(maxEntry.getKey());
                        respostaIncentivo.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                    }else{
                        respostaIncentivo.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        au.reduceValue();
                    }

                    myAgent.send(respostaIncentivo);

                } catch (UnreadableException e) { e.printStackTrace(); }
            }
        }
    }
}
