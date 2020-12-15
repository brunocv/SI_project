package Interface;

import Agents.AgenteInterface;
import Util.Coordenadas;
import Util.Mapa;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UI {

    private Mapa mapa;
    private JFrame mainFrame;
    private JFrame histogramaBicicletas;
    private JFrame graficoFail;
    private JButton buttonBicicletas;
    private JButton buttonVazio;
    private JPanel panel_1; //mapa
    private JPanel panel_2; //ocupacao
    private JPanel graficoBicicletas; //histograma que diz quantas bicicletas cada estacao tem
    private JPanel graficoFalhas;
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

        this.buttonBicicletas = new JButton("Gráfico Barras Bicicletas");
        this.buttonBicicletas.setBounds(1250,550,250,100);

        this.buttonVazio = new JButton("Gráfico Barras Falha");
        this.buttonVazio.setBounds(1250,700,250,100);

        this.graficoBicicletas = new JPanel();
        this.graficoBicicletas.setBounds(150, 150, 500, 500);
        this.graficoBicicletas.setLayout(new java.awt.BorderLayout());

        this.graficoFalhas = new JPanel();
        this.graficoFalhas.setBounds(150, 150, 500, 500);
        this.graficoFalhas.setLayout(new java.awt.BorderLayout());

        initiGraphics();
        initiGraphics2();

        this.buttonBicicletas.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                histogramaBicicletas.setVisible(true);

            }
        });

        this.buttonVazio.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                graficoFail.setVisible(true);

            }
        });

        frameInitialize();

    }

    public void initiGraphics(){
        histogramaBicicletas = new JFrame();
        histogramaBicicletas.setTitle("Gráfico de barras de disposição de bicicletas");
        histogramaBicicletas.getContentPane().setLayout(null);
        histogramaBicicletas.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        histogramaBicicletas.setSize(900,900);
        histogramaBicicletas.setLocationRelativeTo(null);

    }

    public void initiGraphics2(){
        graficoFail = new JFrame();
        graficoFail.setTitle("Gráfico de barras de vezes que estação teve 0 bicicletas");
        graficoFail.getContentPane().setLayout(null);
        graficoFail.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        graficoFail.setSize(900,900);
        graficoFail.setLocationRelativeTo(null);

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
        mainFrame.add(buttonBicicletas);
        mainFrame.add(buttonVazio);

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


    public void drawBicicletas(int bicicletas[]){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        for(int i = 0 ; i < (mapa.getEstacoes() * mapa.getEstacoes()) ; i++) {
            dataset.addValue(bicicletas[i],"Estacao",""+(i+1));
        }
        JFreeChart chart=ChartFactory.createBarChart(
                "Disposição de Bicicletas", //Chart Title
                "Estação", // Category axis
                "Bicicletas", // Value axis
                dataset,
                PlotOrientation.VERTICAL,
                false,false,false
        );

        ChartPanel CP = new ChartPanel(chart);
        graficoBicicletas.add(CP,BorderLayout.CENTER);
        histogramaBicicletas.add(graficoBicicletas);
        graficoBicicletas.validate();
        histogramaBicicletas.validate();
    }

    public void drawFalhas(int falhas[]){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        for(int i = 0 ; i < (mapa.getEstacoes() * mapa.getEstacoes()) ; i++) {
            dataset.addValue(falhas[i],"Estacao",""+(i+1));
        }
        JFreeChart chart=ChartFactory.createBarChart(
                "Falta de bicicletas", //Chart Title
                "Estação", // Category axis
                "Vezes", // Value axis
                dataset,
                PlotOrientation.VERTICAL,
                false,false,false
        );

        ChartPanel CP = new ChartPanel(chart);
        graficoFalhas.add(CP,BorderLayout.CENTER);
        graficoFail.add(graficoFalhas);
        graficoFalhas.validate();
        graficoFail.validate();
    }
}
