import Agents.AgenteSystem;
import Interface.GUI_mapSize;
import JadePlatform.MainContainer;
import Util.Coordenadas;
import Util.Mapa;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainApp {

    private static Mapa mapa;
    private static MainContainer mainContainer;

    public static void agentStartUp(Mapa mapa){
        MainContainer mc = new MainContainer();

        /*
            Começar a criação de estações
            O primeiro ciclo for serve para não o estar a fazer todas as vezes que se cria uma estação nova...
         */
        int numeroDeEstacoes = (int) Math.pow(mapa.getEstacoes(),2);
        ConcurrentHashMap<String,Double> ocupacaoEstacoes = new ConcurrentHashMap<>(30);
        ConcurrentHashMap<String,Double> ocupacaoFuturaEst = new ConcurrentHashMap<>(30);
        for(int i=0;i<numeroDeEstacoes;i++){
            String nome = "Estacao "+(i+1);
            ocupacaoEstacoes.put(nome,(double)0.50); // No inicio do sistema, todas as estações tem 50% das sua capacidade maxima
                                                   // Pode-se mudar depois se 50 for demasiado baixo...
            ocupacaoFuturaEst.put(nome,(double)0.50);
        }

        for(int i=0;i<numeroDeEstacoes;i++){ // Dar startUp a todas as estações
            mc.startEstacao((i+1),mapa,ocupacaoEstacoes,ocupacaoFuturaEst);
        }

        try{ Thread.sleep(1000); } //Sleep para dar tempo as estaçôes de se inscreverem nas Paginas Amarelas
        catch(Exception e){ e.printStackTrace(); }

        mc.startInterface(mapa);
        mc.startSystemAgent(mapa);

    }

    public static void generateMapa(){
        System.out.println(mapa.toString());
        mapa.mapaMatrix();
        System.out.print("\n\n");
        mapa.generateMapa(0,0,1);
        mapa.mapaMatrixPrint();
        mapa.printEstacoes();
    }

    public static void main(String[] args){

        mapa = new Mapa();

        JFrame frame = new GUI_mapSize("TamanhoMapa",mapa);
        frame.setVisible(true);

        while(mapa.isEscolhido() == false){
            try{
                Thread.sleep(5000);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        generateMapa();
        agentStartUp(mapa);

    }
}
