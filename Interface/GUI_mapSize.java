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
    private int tamanho;
    private int estacoes;

    public GUI_mapSize(String title, Mapa m) {
        super(title);
        this.setContentPane(panelMain);
        this.setSize(400,300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.tamanho = 0;
        this.estacoes = 2;

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
                JOptionPane.showMessageDialog(null, "Tamanho: " + tamanho + " Estações: " + estacoes + " \n");
                m.setEscolhido(true);
                dispose();
            }
        });

    }

}
