import Agents.AgenteSystem;
import Interface.GUI_mapSize;
import JadePlatform.MainContainer;
import Util.Mapa;

import javax.swing.*;

public class MainApp {

    private static Mapa mapa;
    private static MainContainer mainContainer;

    public static void agentStartUp(){
        MainContainer mc = new MainContainer();
        mc.initMainContainerInPlatform("localhost", "9888", "MainContainer");
        mc.startSystemAgent();
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

        System.out.println(mapa.toString());
        mapa.mapaMatrix();
        System.out.print("\n\n");
        mapa.generateMapa(0,0,0,1);
        //mapa.mapaMatrixPrint();

        agentStartUp();

        //mainContainer = new MainContainer();
        //mainContainer.startInterface(mapa);
    }
}
