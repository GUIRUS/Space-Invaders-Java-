import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //window variables
        int tileSize = 32;
        int rows = 16; 
        int columns = 16;
        int boardWidth = tileSize * columns; //32*16 = 512px
        int boardHeight = tileSize * rows; //32*16 = 512px

        //frame setup
        JFrame frame = new JFrame("Space Invaders");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        frame.pack(); //sets content above their preferred size
        spaceInvaders.requestFocus();
        frame.setVisible(true);
    }
}