import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class SpaceInvaders extends JPanel implements ActionListener, KeyListener{
    //board
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns; //32*16
    int boardHeight = tileSize * rows; // 32*16

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;
    Image gameOverImg;

    //ship
    int shipWidth = tileSize * 2; //64px
    int shipHeight = tileSize; //32px
    int shipX = tileSize*columns/2 - tileSize;
    int shipY = boardHeight - tileSize*2;
    int shipVelocityX = tileSize;
    Block ship;

    //aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize; 
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0; // number of aliens to defeat
    int alienVelocityX = 1; //alien moving speed

    //bullet
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10; //bullet moving speed

    //GameOver
    int gameOverImgWidth = tileSize * 14;
    int gameOverImgHeight = tileSize * 8;
    int gameOverImgX = tileSize;
    int gameOverImgY = tileSize * 4;
    Block gameOverPic;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    SpaceInvaders(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);//for this class to listen to keyListener
        addKeyListener(this);

        //load images
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();
        gameOverImg = new ImageIcon(getClass().getResource("gameover.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();
        gameOverPic = new Block(gameOverImgX, gameOverImgY, gameOverImgWidth, gameOverImgHeight, gameOverImg);

        //game timer
        gameLoop = new Timer(1000/60, this); //1000/60 = 16.7
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //score
        //GameOver
        g.setColor(Color.green);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawImage(gameOverPic.img, gameOverPic.x, gameOverPic.y, gameOverPic.width, gameOverPic.height, null);
            g.setColor(Color.red); 
            g.drawString("Score: " + String.valueOf(score), 10, 35);
            g.setColor(Color.green);   
            g.drawString("Press any key to continue", 64,451 );      
        }else{
            g.drawString(String.valueOf(score), 10, 35);

            //ship
            g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

            //aliens
            for(int i = 0; i < alienArray.size(); i++){
                Block alien = alienArray.get(i);
                if (alien.alive ){
                    g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height,null);
                }
            }

            //bullet
            g.setColor(Color.red);
            for (int i = 0; i < bulletArray.size(); i++){
                Block bullet = bulletArray.get(i);
                if(!bullet.used){
                    g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
                }
            }
        }
    }

    public void move(){
        //aliens
        for(int i = 0; i < alienArray.size(); i++){
            Block alien = alienArray.get(i);
            if (alien.alive){
                alien.x += alienVelocityX;

                //if aliens touch the borders
                if(alien.x + alien.width >= boardWidth || alien.x <= 0){
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;
                    
                    //alines move down by 1 row
                    for(int y = 0; y < alienArray.size(); y++){
                        alienArray.get(y).y += alienHeight;                  
                    }
                }

                if (alien.y >= ship.y){
                    gameOver = true;
                }
            }
        }

        //bullet
        for(int i = 0; i < bulletArray.size(); i++){
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY; 

            //bullet collision with aliens
            for (int y = 0; y < alienArray.size(); y++){
                Block alien = alienArray.get(y);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)){
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score +=100;
                }
            }
        }

        //clear bullets
        while(bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y <0)){
            bulletArray.remove(0); //removes the first element of the array
        }

        //next level
        if (alienCount == 0){
            //increase the number of alien in columns and rows by 1
            score += alienColumns * alienRows * 100; //bonus points for completing level
            alienColumns = Math.min(alienColumns + 1, columns/2 - 2); //max column at 16/2 = 6
            alienRows = Math.min(alienRows + 1, rows - 6); //max row at 16-6 = 10
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1;
            createAliens();
        }
    }

    public void createAliens(){
        Random random =  new Random();
        for (int i = 0; i < alienRows; i++){
            for (int y = 0; y < alienColumns; y++){
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + y*alienWidth, //decides where to place the next alien 
                    alienY + i*alienHeight, 
                    alienWidth, 
                    alienHeight, 
                    alienImgArray.get(randomImgIndex)
                ); 
                alienArray.add(alien);

            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b){
        return a.x < b.x + b.width && //a's top left corner doesn't reach b's top right corner
            a.x + a.width > b.x &&  //a's top right corner passes b's top left corner
            a.y < b.y + b.height && //a's top left corenr doesn't reach b's bottom left corner 
            a.y + a.height > b.y; //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if(gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        if (gameOver){ //any key to restart
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienRows = 2;
            alienColumns = 3;
            gameOver = false;
            createAliens();
            gameLoop.start();
        }else if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0){
            ship.x -= shipVelocityX; // move left 1 tile
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= boardWidth){
            ship.x += shipVelocityX; // move right 1 tile
        }else if (e.getKeyCode() == KeyEvent.VK_SPACE){
            Block bullet = new Block(ship.x + shipWidth*15/32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }

}
