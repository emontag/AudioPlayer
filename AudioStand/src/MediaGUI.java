import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import static javax.swing.ScrollPaneConstants.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

public class MediaGUI extends JFrame implements  Observer{
   private Container container=getContentPane();
   private JTextArea text=new JTextArea("",20,20);//TextArea(" ",25,25, TextArea.SCROLLBARS_VERTICAL_ONLY);
   private JPanel rightPanel=new JPanel();
   private JPanel leftPanel=new JPanel();
   private JButton play=new JButton("Play");//0
   private JButton pause=new JButton("Pause");//1
   private JButton stop=new JButton("Stop");//2
   private JButton add=new JButton("Add");//3
   private JButton remove=new JButton("Remove");//4
   private JButton next=new JButton("Next");//5
   private JButton previous=new JButton("Previous");//6
   private JButton go=new JButton("GoTo");//7
   private JButton load=new JButton("Load Playlist");
   private JButton save=new JButton("Save Playlist");
   private Player player;
   private JScrollPane scroll=new JScrollPane(text);
   private JPanel soundPanel=new JPanel();
   private JButton volUp=new JButton("Up");
   private JButton volDown=new JButton("Down");
   private JSlider slide;
   private Audio1 audio;
   private JSlider slider=new JSlider(JSlider.VERTICAL, 0, 10, 5);
   public MediaGUI(Player p){
      setSize(500,450);
     // play.setActionCommand("Play"); use if not want show text
      //play.setIcon(new ImageIcon(getClass().getResource("play.jpg")));
      setTitle("MediaPlayer");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setLayout(new BorderLayout());
      setLocation(500,100);
      container.add(rightPanel);//,BorderLayout.CENTER);
      container.add(leftPanel,BorderLayout.WEST);
      leftPanel.setLayout(new GridLayout(8,1));
      rightPanel.setBackground(new Color(217,253,255));
      //scroll.add(text);
      scroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);;
      player=p;
      audio=p.getAudio();
      //rightPanel.add(text);
      rightPanel.add(scroll,BorderLayout.CENTER);
      scroll.setVisible(true);
      leftPanel.add(play);
      leftPanel.add(pause);
      leftPanel.add(stop);
      leftPanel.add(add);
      leftPanel.add(remove);
      leftPanel.add(next);
      leftPanel.add(previous);
      leftPanel.add(go);
      soundPanel.setSize(5, 5);
      leftPanel.setBackground(new Color(217,253,255));
      soundPanel.setLayout(new GridLayout(6,1));
      JLabel vLabel=new JLabel("Volume Controls");
      vLabel.setOpaque(false);
      //vLabel.setContentAreaFilled(false);
      //vLabel.setBorderPainted(false);
      container.add(soundPanel,BorderLayout.EAST);
      soundPanel.add(vLabel);
      soundPanel.add(volUp);
      soundPanel.add(volDown);
      soundPanel.add(slider);
      soundPanel.add(load);
      soundPanel.add(save);
      soundPanel.setBackground(new Color(217,253,255));
      slider.setSize(10,10);
      slider.setValue(5);
      slider.setMajorTickSpacing(2);
      slider.setPaintLabels( true );
      play.addActionListener(player);
      pause.addActionListener(player);
      stop.addActionListener(player);
      add.addActionListener(player);
      remove.addActionListener(player);
      next.addActionListener(player);
      previous.addActionListener(player);
      volUp.addActionListener(player);
      volDown.addActionListener(player);
      go.addActionListener(player);
      load.addActionListener(player);
      save.addActionListener(player);
      text.setEditable(false);
      slide=new JSlider();
      slide.setSize(3, 3);
      slide.setEnabled(true);
      //slide.setMinorTickSpacing(1);
      //slide.setMajorTickSpacing(10);
      slide.setPaintLabels( true );
      slide.setVisible(true);
      slide.setValue(0);
      container.add(slide,BorderLayout.SOUTH);
      slider.setOpaque(false);
      slider.setEnabled(false);
      buttonSet();
      setVisible(true);
      
   }
   private void buttonSet(){
      load.setOpaque(false);
      load.setContentAreaFilled(false);
      load.setBorderPainted(false);
      save.setOpaque(false);
      save.setContentAreaFilled(false);
      save.setBorderPainted(false);
      volUp.setOpaque(false);
      volUp.setContentAreaFilled(false);
      volUp.setBorderPainted(false);
      volDown.setOpaque(false);
      volDown.setContentAreaFilled(false);
      volDown.setBorderPainted(false);   
   }
   public JButton getButton(int i){
      if(i==0) return play;
      if(i==1) return pause;
      if(i==2) return stop;
      if(i==3) return add;
      if(i==4) return remove;
      if(i==5) return next;
      if(i==6) return previous;
      if(i==7) return go;
      if(i==8) return load;
      if(i==9) return save;   
      return new JButton();
   }
   public JTextArea getTextArea(){
      return text;
   }
   public JSlider getSlider(){
      return slide;
   }
   public void setJSlider(JSlider s){
      if(slide!=null){
         /*container.remove(slide);
         slide.setVisible(false);
         slide.invalidate();
         invalidate();
         repaint();
         //slide=null;
         //dispose();
         //new MediaGUI(player);*/
         slide.setValue(0);
         slide.invalidate();
         slide.setVisible(false);
         repaint();
      }
      slide=s;
      //slide.addChangeListener(audio);
      //rightPanel.add(slide,BorderLayout.WEST);
      slide.setSize(3, 3);
      slide.setEnabled(true);
      slide.setMinorTickSpacing(1);
      slide.setMajorTickSpacing(1);
      slide.setPaintLabels( true );
      slide.setVisible(true);
      container.add(slide,BorderLayout.SOUTH);
      revalidate();
      repaint();
      setVisible(true);
      
   }
   @Override
   public void update(Observable arg0, Object arg1) {
      //slide.setValue(audio.getCurrentLength());
      //slide.repaint();
   }
   public void addObserve(Audio1 a){
      audio=a;
      audio.addObserver(this);
   }
   public JSlider getSoundSlider(){
      return slider;
   }
   
   
}