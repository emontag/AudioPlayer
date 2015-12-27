import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.util.Observable;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
public class Audio1 extends Observable implements AudioSpectrumListener,ChangeListener,MouseListener{
   private String audioFilePath;
   private MediaPlayer audioClip;
   private String name;
   private double volume;
   private MediaGUI gui;
   private JSlider slide;
   private JSlider slider;
   private boolean moving;
    public Audio1(String audioFilePat,MediaGUI g){
       new JFXPanel();
       audioFilePath=audioFilePat;
       File f = new File(audioFilePat);
       name=f.getName();
       URI u = f.toURI();
       Media b=new Media(u.toString()) ;
       audioClip=new MediaPlayer(b);
       audioClip.setAudioSpectrumListener(this);
       audioClip.setOnEndOfMedia(new Runnable() {
          public void run() {
             setChanged();
             notifyObservers();
             
          }
       });   
       gui=g;
       slide=gui.getSlider();
       slide.setMinimum(0);
       slide.setMaximum(getTotalTime());
       slide.setValue(0);
       slide.setMajorTickSpacing(60);
       slide.addChangeListener(this);
       gui.addObserve(this);
       slider=gui.getSoundSlider();
       slide.addMouseListener(this);
       moving=true;
    }
   @Override
   public void spectrumDataUpdate(double arg0, double arg1, float[] arg2,
         float[] arg3) {
      if(moving){
         slide.setValue((int)audioClip.getCurrentTime().toSeconds());
         slide.repaint();
      }
      if(slide.getMaximum()==0)slide.setMaximum(getTotalTime());
   }     
   void play()  {
      audioClip.play();
  }
  public void stop() {
     audioClip.stop();
  }
  public void pause() {
     audioClip.pause(); 
  }
  public void raiseVolume(double a){
     if(volume+a>1) return;
     volume+=a;
     audioClip.setVolume(volume);
     int v=(int)(volume*10+.5);
     slider.setValue(v);
  }
  public void lowerVolume(double a){
     if(volume-a<0) return;
     volume-=a;
     audioClip.setVolume(volume); 
     int v=(int)(volume*10+.5);
      slider.setValue(v);  
     //
  }
  public void setVolume(double i){
     volume=i;
     audioClip.setVolume(volume);
     int v=(int)(i*10+.5);
     slider.setValue(v);
     
  }
  public boolean isOpen(){
     return !audioClip.getStatus().equals(Status.DISPOSED);
  }
 
  
  public MediaPlayer getAudioClip() {
      return audioClip;
  }
  public String getName(){
     return name;
  }
  public void close() {
     audioClip.dispose();
     
  }
  public boolean isPlaying(){
     return audioClip.getStatus().equals(Status.PLAYING);
  }

  public void setCurrentLength(int place) {
    Duration d=new Duration((double)place);
    audioClip.seek(d);
  } 
  public int getCurrentLength(){
     Duration d=audioClip.getCurrentTime();
     return (int) d.toSeconds();
  }
  public int getTotalTime(){
     return (int)audioClip.getTotalDuration().toSeconds();
  }
  @Override
  public void stateChanged(ChangeEvent arg0) {
    
  }
  @Override
  public void mouseClicked(MouseEvent arg0) {
     
  }
  @Override
  public void mouseEntered(MouseEvent arg0) {
     // TODO Auto-generated method stub
     
  }
  @Override
  public void mouseExited(MouseEvent arg0) {
     // TODO Auto-generated method stub
     
  }
  @Override
  public void mousePressed(MouseEvent arg0) {
     moving=false;
       
  }
  @Override
  public void mouseReleased(MouseEvent arg0) {
     try{
        JSlider s=(JSlider)arg0.getSource();
        int value=s.getValue();
        slide.setValue(value);
        slide.repaint();
        setCurrentLength(value*1000);
     }catch(NullPointerException e){}
     finally{moving=true;}
     
     
     
  }


 

}
