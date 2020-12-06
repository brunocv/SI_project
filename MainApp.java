import Agents.AgenteSystem;
import Interface.GUI_mapSize;
import JadePlatform.MainContainer;
import Util.Coordenadas;
import Util.Mapa;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class MainApp {

    private static Mapa mapa;
    private static MainContainer mainContainer;

    public static void agentStartUp(Mapa mapa){
        MainContainer mc = new MainContainer();

        mc.startInterface(mapa);

        /*
            Começar a criação de estações
            O primeiro ciclo for serve para não o estar a fazer todas as vezes que se cria uma estação nova...
         */
        int numeroDeEstacoes = (int) Math.pow(mapa.getEstacoes(),2);
        Map<String,Double> ocupacaoEstacoes = new HashMap<>();
        for(int i=0;i<numeroDeEstacoes;i++){
            String nome = "Estacao "+(i+1);
            ocupacaoEstacoes.put(nome,(double)50); // No inicio do sistema, todas as estações tem 50% das sua capacidade maxima
                                                   // Pode-se mudar depois se 50 for demasiado baixo...
        }

        for(int i=0;i<numeroDeEstacoes;i++){ // Dar startUp a todas as estações
            String nome = "Estacao "+(i+1);
            mc.startEstacao((i+1),mapa,ocupacaoEstacoes);
        }

        try{ Thread.sleep(1000); } //Sleep para dar tempo as estaçôes de se inscreverem nas Paginas Amarelas
        catch(Exception e){ e.printStackTrace(); }

        //mc.startSystemAgent(mapa);

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
            System.out.println("Não foi desta!");
        }

        generateMapa();
        agentStartUp(mapa);

    }
}
