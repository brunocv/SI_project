package Interface;

import Agents.AgenteInterface;
import Util.Mapa;

import javax.swing.*;
import java.awt.*;

public class UI {

    private Mapa mapa;
    private JFrame mainFrame;
    private JPanel panel_1;
    private AgenteInterface agente;
    private MapaUI mapaui;
    private JLabel texto;

    public UI(Mapa mapa, AgenteInterface agente){
        this.mapa = mapa;
        this.agente = agente;
        this.mapaui = new MapaUI(mapa);
        this.texto = new JLabel();
        this.panel_1= new JPanel();
        panel_1.setBounds(1150, 20, 400, 450);
        panel_1.setBackground(Color.white);

        frameInitialize();

    }

    public JFrame getJFrame(){
        return this.mainFrame;
    }

    private void frameInitialize() {

        mainFrame = new JFrame();
        mainFrame.setTitle("Sistema de Partilha de Bicicletas");
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1600,1100);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().add(mapaui.getPanel());
        mainFrame.add(panel_1);
    }

    public void drawOcupacaoEstacao(String ocupacoes){

        texto.setText(ocupacoes);
        panel_1.add(texto);
        panel_1.revalidate();

    }
}
