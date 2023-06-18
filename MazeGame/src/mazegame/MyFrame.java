/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mazegame;

import javax.swing.JFrame;

/**
 *
 * @author user
 */

public class MyFrame extends JFrame {

    public MyFrame() {
        setTitle("2101020037_Hany Safitri Sinaga_MazeGame");
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