package Interface;

import Agents.AgenteInterface;
import Util.Mapa;

import javax.swing.*;

public class UI {

    private Mapa mapa;
    private JFrame mainFrame;
    private JPanel panel_1;
    private AgenteInterface agente;
    private MapaUI mapaui;

    public UI(Mapa mapa, AgenteInterface agente){
        this.mapa = mapa;
        this.agente = agente;
        this.mapaui = new MapaUI(mapa);

        this.panel_1= new JPanel();
        panel_1.setBounds(200, 200, 500, 500);

        frameInitialize(panel_1);

    }

    public JFrame getJFrame(){
        return this.mainFrame;
    }

    private void frameInitialize(JPanel panel_1) {

        mainFrame = new JFrame();
        mainFrame.setTitle("Sistema de Partilha de Bicicletas");
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1600,1000);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().add(mapaui.getPanel());

    }
}
