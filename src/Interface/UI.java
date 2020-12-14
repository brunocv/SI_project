package Interface;

import Agents.AgenteInterface;
import Util.Coordenadas;
import Util.Mapa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UI {

    private Mapa mapa;
    private JFrame mainFrame;
    private JFrame graphics;
    private JButton button;
    private JPanel panel_1; //mapa
    private JPanel panel_2; //ocupacao
    private JPanel panel_3; //graphic 1
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
        this.panel_2 = new JPanel();
        this.panel_2 = mapaui.getPanel();
        this.button = new JButton("Graphics");
        this.button.setBounds(1250,650,150,80);

        initiGraphics();

        this.button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                graphics.setVisible(true);

            }
        });

        frameInitialize();

    }

    public void initiGraphics(){
        graphics = new JFrame();
        graphics.setTitle("Gráficos");
        graphics.getContentPane().setLayout(null);
        graphics.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        graphics.setSize(900,900);
        graphics.setLocationRelativeTo(null);
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
        mainFrame.add(panel_2);
        mainFrame.add(panel_1);
        mainFrame.add(button);

    }

    public void drawOcupacaoEstacao(String ocupacoes){

        texto.setText(ocupacoes);
        panel_1.add(texto);
        panel_1.revalidate();

    }

    public void drawUtilizadores(List<Coordenadas> utilizadores){
        mapaui.drawUtilizadores(utilizadores);
        mapaui.draw(1,mapa.getPosicaoEstacoes());
        panel_2.repaint();
    }
}
