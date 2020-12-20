package Interface;

import JadePlatform.MainContainer;
import Util.Mapa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_mapSize extends JFrame{
    private JPanel panelMain;
    private JButton btnOK;
    private JComboBox cbTamanho;
    private JComboBox cbEstacoes;
    private JComboBox cbBicicletas;
    private int tamanho;
    private int estacoes;
    private int bicicletas;

    public GUI_mapSize(String title, Mapa m) {
        super(title);
        this.setContentPane(panelMain);
        this.setSize(400,300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.tamanho = 20;
        this.estacoes = 2;
        this.bicicletas = 10;

        cbTamanho.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                tamanho = Integer.parseInt(cbTamanho.getSelectedItem().toString());
                m.setTamanho(tamanho);
            }
        });

        cbEstacoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                estacoes = Integer.parseInt(cbEstacoes.getSelectedItem().toString());
                m.setEstacoes(estacoes);

            }
        });

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Tamanho: " + tamanho + " Estações: " + estacoes + " Bicicletas: " + bicicletas + " \n");
                m.setEscolhido(true);
                dispose();
            }
        });

        cbBicicletas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bicicletas = Integer.parseInt(cbBicicletas.getSelectedItem().toString());
                m.setBicicletas(bicicletas);
            }
        });
    }

}
