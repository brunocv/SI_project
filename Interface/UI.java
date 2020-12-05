package Interface;

import Util.Mapa;

import javax.swing.*;

public class UI {

    private Mapa mapa;
    private JFrame mainFrame;

    public UI(Mapa mapa){
        this.mapa = mapa;

    }

    public JFrame getJFrame(){
        return this.mainFrame;
    }
}
