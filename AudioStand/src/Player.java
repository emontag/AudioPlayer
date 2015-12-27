import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


public class Player implements ActionListener,Observer,CaretListener{
   private JTextArea area;
   private JButton[] button;
   private Playlist playlist;
   private Audio1 audio;
   private Iterator iterator;
   private boolean started;
   private boolean stopped;
   private String n;//used for next
   private String previous;
   private double volume;
   private MediaGUI gui;
   public Player(String args) {
      gui=new MediaGUI(this);
      area=gui.getTextArea();
      playlist=new Playlist();
      button=new JButton[8];
      setButtons(gui);
      started=false;
      stopped=false;
      volume=.5;
      area.addCaretListener(this);
      setStart(args);
   }
   private void setStart(String args){
      if(args.matches("")) return;
      File f=new File(args);
      if(args!="NUL"){
        playlist.put(args, f.getName());
        button[0].setEnabled(true);
        button[5].setEnabled(true);
        button[4].setEnabled(true);
        button[6].setEnabled(true);
        button[7].setEnabled(true);
         if(!started){
            iterator=playlist.entrySet().iterator();
         }
         //slide.setEnabled(true);
         started=true;
        }
        Iterator it=playlist.entrySet().iterator();
        while(it.hasNext()){
           Map.Entry qq=(Map.Entry) it.next();
           area.append(qq.getValue().toString()+"\n");
        }
   } 
   public void setButtons(MediaGUI gui){
      for(int i=0;i<8;i++){
         button[i]=gui.getButton(i);
         button[i].setEnabled(false);
         button[i].setOpaque(false);
         button[i].setContentAreaFilled(false);
         button[i].setBorderPainted(false);
      }
      button[3].setEnabled(true);
   }
   @Override
   public void actionPerformed(ActionEvent arg0) {
      String event=arg0.getActionCommand();
      if(event=="Up"){
         raiseVolume();  
      }
      if(event=="Down"){
         lowerVolume();
      }
      if(event=="Load Playlist"){
         loadPlaylist();
      }
      if(event== "Save Playlist"){
         savePlaylist();
      }
      if(event=="Add"){
         add();
         
      }//end ADD
      if(event=="Remove"){
         remove();
      }
      if(event=="GoTo"){
         goTo();
      }
      if(event=="Play"){
        play();
      }
      if(event=="Previous"){
        prev();
      }
      if(event=="Next"){
         next();
      }
      if(event=="Pause") {
         if(audio.equals(null)) return;
         audio.pause();
         button[0].setEnabled(true);
         stopped=true;
      }
      if(event=="Stop") {
         audio.stop();
         button[0].setEnabled(true);
         stopped=true;
         button[2].setEnabled(false);
         button[1].setEnabled(false);
      }
      
   }
   private void play() {
      if(!button[2].isEnabled()) button[2].setEnabled(true);
      if(!button[1].isEnabled()) button[1].setEnabled(true);
      if(!button[5].isEnabled()) button[5].setEnabled(true);
      if(stopped){
         if(!audio.isOpen()) ;//move on
         else{
            audio.play();
            //slide.setValue(audio.getCurrentLength());
            button[0].setEnabled(false);
            stopped=false;
            n=null;
            //slide.setMaximum(audio.getTotalTime());
            return;
         }
      }
      Map.Entry pairs = null;
      try{
         pairs = (Map.Entry)iterator.next();
      }
      catch(ConcurrentModificationException e1){
         iterator=playlist.entrySet().iterator();
         while(iterator.hasNext() && n!=null){
            Map.Entry p=(Map.Entry)iterator.next();
            if(p.getValue().toString().equals(n)) {
               break;
            }
         }
         n=null;
         try{
            pairs=(Map.Entry)iterator.next();
         }catch(NoSuchElementException | NullPointerException e2){
            iterator=playlist.entrySet().iterator();
            pairs=(Map.Entry)iterator.next();
         }
      }
      catch(NoSuchElementException  | NullPointerException f){
         iterator=playlist.entrySet().iterator();
         pairs=(Map.Entry)iterator.next();
      }
      String play=pairs.getKey().toString();
      audio=new Audio1(play,gui);
      audio.addObserver(this);
      //audio.setCurrentLength(0);
      audio.play();
      audio.setVolume(volume);
      started=true;
      button[0].setEnabled(false);
      highlight();
   }
   private void highlight(){
      Highlighter high = area.getHighlighter();
      high.removeAllHighlights();
      String alias=audio.getName();
      int pos=area.getText().indexOf(alias);
      int pos2=pos+alias.length();
      try {
         area.getHighlighter().addHighlight(pos, pos2 ,
               new DefaultHighlighter.DefaultHighlightPainter(Color.yellow));
         Rectangle viewRect = area.modelToView(pos);
         area.scrollRectToVisible(viewRect);
      } catch (BadLocationException e) {
         return;
      }            
   }
   public File[] read (File[] f){
      return f;
   }
   public File read (File f){
      return f;
   }
   @Override
   public void update(Observable arg0, Object arg1) {
      
      next();
      //play();
   }
   public void next(){
      if(audio==null) {
         play();
         return;
      }
      n=audio.getName();
      previous=n;
      audio.stop();
      stopped=false;
      audio.close();
      button[0].setEnabled(true);
      play();
   }
   public Audio1 getAudio(){
      return audio;
   }
   @Override
   public void caretUpdate(CaretEvent e) {//combine with goto
      try{
      int pos=area.getSelectionStart();
      int pos1=area.getSelectionEnd();
      String line=area.getText().substring(pos, pos1);
      //line.trim();
      if(playlist.containsValue(line)){
         if(audio!=null && line.equals(audio.getName())) return;
         if(audio!=null){
            audio.stop();
            audio.close();
         }
         iterator=playlist.entrySet().iterator();
         Map.Entry q=null;
         Map.Entry p=null;
         while(iterator.hasNext()){
            q=p;
            p=(Map.Entry) iterator.next();
            if(line.equals(p.getValue())) break;  
         }
         iterator=playlist.entrySet().iterator();
         Map.Entry r=q;
         if(r==null) {
            previous=null;
            play();
            return;
         }
         while(iterator.hasNext()){
            q=p;
            p=(Map.Entry) iterator.next();
            if(r.getValue().equals(p.getValue())) break;  
         }
         previous=r.getValue().toString();
         play();
      
         
      }
      }catch(NullPointerException f){
         
      }
      
   }
   public void raiseVolume(){
      if(volume+.1>1) return;
      volume+=.1;
      try{
         audio.raiseVolume(.1);
      }catch(NullPointerException e){
         return;
      }
      return;
   }
   public void lowerVolume(){
      if(volume-.1<0) return;
      volume-=.1;
      try{
         audio.lowerVolume(.1);
      }catch(NullPointerException e){
         return;
      }
      return;
   }
   public void loadPlaylist(){
      /*FileNameExtensionFilter filter = new  FileNameExtensionFilter("Text (Playlist) Files", "txt");
      JFileChooser chooser;
      int status;
      chooser = new JFileChooser(System.getProperty("user.home"));
      chooser.setFileFilter(filter); 
      status = chooser.showOpenDialog(null);
      if(status!=JFileChooser.APPROVE_OPTION) return;
      File file=read(chooser.getSelectedFile());*/
      final FileChooser chooser=new FileChooser();
      chooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
      chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("*.txt", "*.txt")
        );
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
           //javaFX operations should go here
            File file=null;
           try{
              file=chooser.showOpenDialog(null); 
              if(file==null) return;
           }catch(NullPointerException e){
              return;
           }
      BufferedReader in=null;
      try{
        in=new BufferedReader(
            new InputStreamReader(
                  new FileInputStream(file.getAbsolutePath())));
  } catch ( IOException ioe )  {
     JOptionPane.showMessageDialog(null, "Error Occurred");
     return;
  }  // catch; 
      String line = null;
      try {
         line = in.readLine();
      } catch (IOException e1) {  
         try {
            in.close();
         } catch (IOException e) {
         }
         return;
      }
      while(line!=null){
         try{
            File f=new File(line);
            if(playlist.containsValue(f.getName())) ;
            else{
               Audio1 au=new Audio1(f.getAbsolutePath(),gui);//check if valid
               au.close();
               playlist.put(f.getAbsolutePath(),f.getName());
               area.append(f.getName()+"\n"); 
            }
    
         }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
         }
         finally{ 
            try {
            line=in.readLine();
         } catch (IOException e) {
            return;
         }
         }
      }
      try {
         in.close();
      } catch (IOException e) {
         return;
      }
      if(playlist.size()>0){
         button[0].setEnabled(true);
         button[5].setEnabled(true);
         button[4].setEnabled(true);
         button[6].setEnabled(true);
         button[7].setEnabled(true);
          if(!started){
             iterator=playlist.entrySet().iterator();
          }
          //slide.setEnabled(true);
          started=true;
      }
         }
      });
   }
   public void savePlaylist(){
      if(playlist.size()==0) return;
      /*JFileChooser chooser;
      int status;
      chooser = new JFileChooser(System.getProperty("user.home"));
      status = chooser.showSaveDialog(null);
      if(status!=JFileChooser.APPROVE_OPTION) return;
      File f=read(chooser.getSelectedFile());*/
      final FileChooser chooser=new FileChooser();
      chooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
           //javaFX operations should go here
            File f=null;
           try{
              f=chooser.showSaveDialog(null); 
              if(!f.getName().endsWith(".txt")){
                 String path=f.getAbsolutePath();
                 path+=".txt";
                 f=new File(path);
              }
           }catch(NullPointerException e){
              return;
           }
         
      Writer writer = null;
      try {
         writer = new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(f.getAbsolutePath()), "utf-8"));
         Iterator it=playlist.entrySet().iterator();
         while(it.hasNext()){
            Map.Entry p=(Map.Entry) it.next();
            writer.write(p.getKey().toString()+"\n");
         }
      } catch (IOException e) {
         JOptionPane.showMessageDialog(null, "Problem With Operation");
      }finally{try {
         writer.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      }
         }
      });
         
   }
   public void add(){
      //FileNameExtensionFilter filter = new  FileNameExtensionFilter("Media Files", "wav","mp3");
      /*JFileChooser chooser;
      chooser = new JFileChooser(System.getProperty("user.home"));*/
      //int status;
      final FileChooser chooser=new FileChooser();
      chooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
      List<String> types = new ArrayList<String>();
      types.add("*.mp3");
      types.add("*.wav");
      chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Supported Media (.mp3, .wav", types)
            /*new FileChooser.ExtensionFilter("MP3", "*.mp3"),
            new FileChooser.ExtensionFilter("WAV", "*.wav"),
            new FileChooser.ExtensionFilter("Music", "*.*")*/
        );
      //chooser.setFileFilter(filter);
      //chooser.setMultiSelectionEnabled(true);
      //status = chooser.showOpenDialog(null);
      new JFXPanel();
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
           //javaFX operations should go here
            Object[] f=null;
           try{
              f=chooser.showOpenMultipleDialog(null).toArray(); 
           }catch(NullPointerException e){
              return;
           }
            
            File[] file=new File[f.length];
            for(int i=0;i<file.length;i++){
               file[i]=new File(f[i].toString());
            }
            for(int j=0;j<file.length;j++){
               try{
                  if(playlist.containsValue(file[j].getName())) return;
                  playlist.put(file[j].getAbsolutePath(),file[j].getName());
               }
               catch(NullPointerException e){
                  return;
               }
               area.append(file[j].getName()+"\n");  
               button[0].setEnabled(true);
               button[5].setEnabled(true);
               button[4].setEnabled(true);
               button[6].setEnabled(true);
               button[7].setEnabled(true);
                if(!started){
                   iterator=playlist.entrySet().iterator();
                }
                //slide.setEnabled(true);
                started=true;
               }
         }
    });
      
      //if(status!=JFileChooser.APPROVE_OPTION) return;
      //File[] file=read(chooser.getSelectedFiles());
      /*for(int j=0;j<file.length;j++){
      try{
         if(playlist.containsValue(file[j].getName())) return;
         playlist.put(file[j].getAbsolutePath(),file[j].getName());
      }
      catch(NullPointerException e){
         return;
      }
      area.append(file[j].getName()+"\n");  
      button[0].setEnabled(true);
      button[5].setEnabled(true);
      button[4].setEnabled(true);
      button[6].setEnabled(true);
      button[7].setEnabled(true);
       if(!started){
          iterator=playlist.entrySet().iterator();
       }
       //slide.setEnabled(true);
       started=true;
      }*/
   }
   public void remove(){
      String [] list=new String[playlist.size()];
      Iterator ite=playlist.entrySet().iterator();
      for(int i=0;i<list.length;i++){
         Map.Entry pairs = (Map.Entry)ite.next();
         list[i]=pairs.getValue().toString();
      }
      /*Object a=JOptionPane.showInputDialog(null, "Pick which song to remove", "Input", JOptionPane.QUESTION_MESSAGE,
            null, list, "Titan");
      if(a==null) return;*/
      JList places = new JList(list) ; // creating JList object; 
      JScrollPane scrollPane=new JScrollPane();
      scrollPane.setViewportView(places);
      places.setVisibleRowCount(5);   
      places.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
      int o=JOptionPane.showConfirmDialog(
            null, scrollPane, "Remove Media Files", JOptionPane.OK_CANCEL_OPTION);
      if(JOptionPane.OK_OPTION!=o) return;
      List removeList=places.getSelectedValuesList();
      if(audio!=null && removeList.contains(audio.getName())){
         button[0].setEnabled(true);
         iterator=playlist.entrySet().iterator();
         audio.close();
      }
      for (Object a : removeList) {
         Iterator it=playlist.entrySet().iterator();
         String p=null;
         while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            if(pairs.getValue().equals(a)) {
               p=pairs.getKey().toString();
               //System.out.println(pairs.getKey());
               it.remove();
               break;
            }
         }
      }//end for
      /*if(audio!=null&& a.toString().equals(audio.getName())) {//stops playback problem
         button[0].setEnabled(true);
         iterator.next();
         audio.close();   
      }*/
      //playlist.remove(p);
      /*area.setText(area.getText().replaceAll(a.toString(), "").trim());
      String line=area.getText();
      String[] lines=line.split("\n");
      area.setText("");
      for(int i=0;i<lines.length;i++){
         if(lines[i].trim().isEmpty()) continue;
         area.append(lines[i]+"\n");
      }
      if(audio!=null && audio.isPlaying()) highlight();
      if(playlist.size()==0) {
         for(int i=0;i<8;i++){
            button[i].setEnabled(false);
         }
         button[3].setEnabled(true);
      }*/
      Iterator it=playlist.entrySet().iterator();
      String p=null;
      area.setText("");
      while(it.hasNext()){
         Map.Entry pairs = (Map.Entry)it.next();
         p=pairs.getValue().toString();
         area.append(p+"\n");
      }
      if(audio!=null && audio.isPlaying()) highlight();
      if(playlist.size()==0) {
         for(int i=0;i<8;i++){
            button[i].setEnabled(false);
         }
         button[3].setEnabled(true);
      }
      
   }
   public void goTo(){
      Object[] list=playlist.values().toArray();
      Object a=JOptionPane.showInputDialog(null, "Pick which song to go to:", "Input", JOptionPane.QUESTION_MESSAGE,
            null, list, "Titan");
      if(a==null) return;
      if(audio!=null && a.toString().equals(audio.getName())) return;
      if(audio!=null){
         audio.stop();
         audio.close();
      }
      iterator=playlist.entrySet().iterator();
      Map.Entry q=null;
      Map.Entry p=null;
      while(iterator.hasNext()){
         q=p;
         p=(Map.Entry) iterator.next();
         if(a.toString().equals(p.getValue())) break;  
      }
      iterator=playlist.entrySet().iterator();
      Map.Entry r=q;
      if(r==null) {
         previous=null;
         play();
         return;
      }
      while(iterator.hasNext()){
         q=p;
         p=(Map.Entry) iterator.next();
         if(r.getValue().equals(p.getValue())) break;  
      }
      previous=r.getValue().toString();
      play();
   }
   public void prev(){
      if(previous==null) return;
      audio.stop();
      audio.close();
      ListIterator<String> iter =
            new ArrayList(playlist.values()).listIterator(playlist.size());

      String key = null;
      while (iter.hasPrevious()) {
             key = iter.previous();
            if(key.equals(previous)) break;
        }
        iterator=playlist.entrySet().iterator();
       Map.Entry q=null;
       Map.Entry p=null;
        while(iterator.hasNext()){
           q=p;
           p=(Map.Entry)iterator.next();
           if(p.getValue().toString().equals(key)) {
              break;
           }
        }
        iterator=playlist.entrySet().iterator();
        int i=0;
        while(iterator.hasNext()){
           p=(Map.Entry)iterator.next();
           i++;
           if(i==playlist.size()) {
              break;
           }
           if(q!=null && p!=null && q.equals(p)) {
              break;
           }
        }
        previous=p.getValue().toString();
        play();
   }

}
