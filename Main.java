import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Container;
import java.awt.GridBagConstraints;  
import java.awt.GridBagLayout;
import java.util.Random;


class Main {
  public static void main(String[] args) {
    Game g = new Game();
  }
}

class Game {
  int x_range, y_range, bombs_number;
  Start_screen st_sc;
  End_screen en_sc;
  Board board;
  public Game(){
    st_sc = new Start_screen(this);
    st_sc.createGUI();
  }
  void set(int x, int y,int b) {
    if(b<=x*y) {
      x_range = x;
      y_range = y; 
      bombs_number = b;
      start();
    }
    else {
      System.out.println("Podałeś więcej bomb niż pól");
    }
  }
  void lose() {
    en_sc = new End_screen(this, "Przegrałeś :(");
    en_sc.createGUI();
  }

  void win() {
    en_sc = new End_screen(this, "Wygrałeś!");
    en_sc.createGUI();
  }

  void restart() {
  	board.showBoard(false);
    st_sc = new Start_screen(this);
    en_sc.hide();
    st_sc.createGUI();
  }

  void start() {
    st_sc.hide();
    board = new Board(this,x_range,y_range,bombs_number);
    board.showBoard(true);
  }

  
}
class Tile implements ActionListener{
  ImageIcon bomb = new ImageIcon("bomb.png");
  int number, xcord, ycord;
  Board board;
  Tile_swing t_s;
  flaged f;
  Boolean checked;
  class flaged implements ActionListener {
    ImageIcon flag = new ImageIcon("flagquality.png");
    boolean bul;
    public flaged() {
      bul = false;
    }
    public void actionPerformed(ActionEvent e) {
      if(bul) {
        t_s.b.setIcon(null);
        bul = ! bul;
        board.b_s.f_cnt.setText(Integer.toString(Integer.valueOf(board.b_s.f_cnt.getText())+1));
      }
      else if(!checked) {
        t_s.b.setIcon(flag);
        bul = ! bul;
        board.b_s.f_cnt.setText(Integer.toString(Integer.valueOf(board.b_s.f_cnt.getText())-1));
      }
    }
  }
  public Tile(Board b,int x,int y, int n) {
    f = new flaged();
    number = n;
    xcord = x;
    ycord = y;
    board = b;
    checked = false;
    t_s = new Tile_swing(this);
  }
  void place_number(int x) {
  	number = x;
  	t_s.setcolor_nmb(x);
  }
  void show() {
    if(number == -1) {
      t_s.b.setIcon(bomb);
    }
    else {
      t_s.b.setIcon(null);
      t_s.b.setText(String.valueOf(number));
      board.goal -= 1;
    }
    t_s.setcolor_tile(0);
    if(board.goal == 0 && !board.lost_board) {
      board.game.win();
    }
    checked = true;
  }
  void check() {
    if(f.bul) {
      f.bul = false;
      board.b_s.f_cnt.setText(Integer.toString(Integer.valueOf(board.b_s.f_cnt.getText())+1));
    }
    if(number == -1) {
      board.show_lose();
    }
    else if(number == 0) {
      board.spread(xcord,ycord);
    }
    else {
      show();
    }
  }
  public void actionPerformed(ActionEvent e) {
    if(checked) {
      board.checkflags(xcord,ycord);
    }
    else {
      check();
    }
  }
}
class Board {
  Tile[][] board;
  Board_swing b_s;
  Game game;
  Boolean lost_board;
  int goal;
  static int[][] moves = new int[][] {{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}};
  public Board(Game g, int x, int y, int bombs) {
    game = g;
    lost_board = false;
    board = new Tile[x+2][y+2];
    b_s = new Board_swing(x,y,bombs);
    goal = x*y-bombs;
    for(int i=0; i<x+2; i++) {
      board[i][0] = new Tile(this,i,0,-99);
      board[i][y+1] = new Tile(this,i,y+1,-99);
    }
    for(int i=0; i<y+2; i++) {
      board[0][i] = new Tile(this,0,i,-99);
      board[x+1][i] = new Tile(this,y+1,i,-99);
    }
    for(int i=1; i<x+1; i++) {
      for(int j=1; j<y+1; j++) {
        board[i][j] = new Tile(this,i,j,0);
        b_s.add(board[i][j].t_s);
      }
    }
    place_bombs(x,y,bombs);
    place_numbers(x,y);
    b_s.pack();
  }
  void showBoard(Boolean b) {
    b_s.setVisible(b);
  }
  void PrintBoard(int x, int y) {
    for(int i=0; i<x+2; i++) {
      for(int j=0; j<y+2; j++) {
        System.out.print(board[i][j].number + " ");
      }
      System.out.print("\n");
    }
  }
  void place_bombs(int x,int y,int bombs) {
    Random rand = new Random();
    while(bombs>0) {
      int randx = rand.nextInt(x) + 1;
      int randy = rand.nextInt(y) + 1;
      if(board[randx][randy].number == 0) {
          board[randx][randy].number = -1;
          bombs -= 1; 
      }
    }
  }
  void place_numbers(int x,int y) {
    for(int i=1; i<x+1; i++) {
      for(int j=1; j<y+1; j++) {
        if(board[i][j].number == 0) {
          int cnt=0;
          for(int k=0; k<8; k++) {
            if(board[i+moves[k][0]][j+moves[k][1]].number == -1) {
              cnt += 1;
            }
          }
          board[i][j].place_number(cnt);
        }
      }
    }
  }
  void show_lose() {
    lost_board = true;
    for(int i=1; i<game.x_range+1; i++) {
      for(int j=1; j<game.y_range+1; j++) {
        board[i][j].show();
      }
    }
    game.lose();
  }
  
  void spread(int x, int y) {
    board[x][y].show();
    for(int i=0; i<8; i++) {
      int X = x+moves[i][0], Y = y+moves[i][1];
      int n = board[X][Y].number;
      if(!board[X][Y].checked) {
        if(n == 0) {
          spread(X,Y);
        }
        else if(n > 0 ) {
          board[X][Y].show();
        }
      }
    }
  }
  
  void checkflags(int x, int y) {
    int flags = 0;
    for(int i=0; i<8; i++) {
      int X = x+moves[i][0], Y = y+moves[i][1];
      if(board[X][Y].f.bul) {
        flags += 1;
      }    
    }
    if(flags >= board[x][y].number) {
      for(int i=0; i<8; i++) {
        int X = x+moves[i][0], Y = y+moves[i][1];
        if(!board[X][Y].f.bul && !board[X][Y].checked) {
          board[X][Y].check();
        }
      }
    }
  }  
}
class End_screen implements ActionListener {
  Game game;
  JFrame frame;
  String title;
  public void actionPerformed(ActionEvent e) {
    game.restart();
  }
  public End_screen(Game g,String s) {
    title = s;
    game = g;
  }
  public void createGUI() {
    frame = new JFrame("Saper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container container = frame.getContentPane();
    GridLayout layout = new GridLayout(3,1);
    container.setLayout(layout);
    
    container.add( new JLabel(title));

    JButton b = new JButton("Restart");
    b.addActionListener(this);
    container.add(b);

    frame.pack();
    frame.setVisible(true);
    
  }
  public void hide() {
    frame.setVisible(false);
  }
}
class Start_screen implements ActionListener {
  Game game;
  JFrame frame;
  JTextField x_range_tf;
  JTextField y_range_tf;
  JTextField bombs_number_tf;
  public Start_screen(Game g) {
    game = g;
  }
  public void actionPerformed(ActionEvent e) {
    try {
      game.set(Integer.valueOf(x_range_tf.getText()),
               Integer.valueOf(y_range_tf.getText()),
               Integer.valueOf(bombs_number_tf.getText()));
    }
    catch(NumberFormatException excp) {
      System.out.println("Wypełnij wszystkie pola przed rozpoczęciem");
    }
  }
  void createGUI() {
    frame = new JFrame("saper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container container = frame.getContentPane();
    GridLayout layout = new GridLayout(4,2);
    container.setLayout(layout);
    
    JLabel x_range_et = new JLabel("Wysokość:");
    container.add(x_range_et);
    x_range_tf = new JTextField("15");
    container.add(x_range_tf);

    JLabel y_range_et = new JLabel("Szerokość:");
    container.add(y_range_et);
    y_range_tf = new JTextField("30");
    container.add(y_range_tf);

    JLabel bombs_number_et = new JLabel("Ilość Bomb:");
    container.add(bombs_number_et);
    bombs_number_tf = new JTextField("75");
    container.add(bombs_number_tf);

    JButton b = new JButton("Graj");
    b.addActionListener(this);
    container.add(b);

    frame.pack();
    frame.setVisible(true);
    
  }
  void hide(){
    frame.setVisible(false);
  }
}

class Board_swing extends JFrame {
  JLabel f_cnt;
  public Board_swing(int x,int y, int bombs) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container container = getContentPane();
    GridLayout layout = new GridLayout(x+1,y,2,2);
    container.setLayout(layout);
    add(new JLabel(new ImageIcon("flagquality.png")));
    f_cnt = new JLabel(Integer.toString(bombs));
    add(f_cnt);
    for(int i=0; i<y-2; i++) {
      add(new JLabel(""));
    }
  } 
}


class Tile_swing extends JPanel{
  static Color[] ctab = new Color[] {new Color(255,255,255),new Color(0,0,204),new Color(76,153,0),new Color(255,0,0),new Color(0,0,102),new Color(102,0,0),new Color(0,204,204),new Color(255,51,153),new Color(102,0,204),new Color(160,160,160)};
  JButton b,b2;
  public Tile_swing(Tile t) {  
    GridBagLayout grid = new GridBagLayout();  
    GridBagConstraints gbc = new GridBagConstraints();  
    setLayout(grid);  
    GridBagLayout layout = new GridBagLayout();  
    setLayout(layout);  
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.ipady = 20;
    gbc.ipadx = 10;
    gbc.gridx = 0;  
    gbc.gridy = 0;
    b = new JButton("");
    b.setOpaque(true);
    b.addActionListener(t);
    add(b, gbc);  
    gbc.fill = GridBagConstraints.HORIZONTAL;  
    gbc.ipady = 1;
    gbc.gridx = 0;  
    gbc.gridy = 1;
    b2 = new JButton(new ImageIcon("flag.png"));
    b2.setOpaque(true);
    b2.addActionListener(t.f);
    add(b2, gbc);  
		setcolor_tile(9);
    }
    void setcolor_nmb(int x) {
    	b.setForeground(ctab[x]);
    } 
    void setcolor_tile(int x) {
      b.setBackground(ctab[x]);
      b2.setBackground(ctab[x]);
    }
}  
