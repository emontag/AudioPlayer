

import javax.swing.JApplet;
import javax.swing.JOptionPane;










public class MediaMain {
   public static void main(String[] args){
      if(args.length>=1){
         new Player(args[0]);
      }
      else new Player("NUL");
      
     
  

   }
   /*for web use code="MediaMain$Applet.class"*/
   public static class Applet extends JApplet {
      public void init() {
         //new Player();
      }
   }
}

   
