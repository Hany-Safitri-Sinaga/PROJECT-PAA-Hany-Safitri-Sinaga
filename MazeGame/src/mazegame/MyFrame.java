package mazegame;

import javax.swing.JFrame;

public class MyFrame extends JFrame {

    public MyFrame() {
        setTitle("2101020037_Hany Safitri Sinaga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setResizable(false);
        MyPanel myPanel = new MyPanel();
        add(myPanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MyFrame();
    }
}