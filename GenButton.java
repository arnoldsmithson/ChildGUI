package slide;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class GenButton extends JButton implements MouseListener {
    private String type,sentence1,sentence2,name;//What kind of button it is --Target or Distractor
    private boolean correct, clicked,visited = false;//whether this button with image is correct or not
    private int numClicks = 0,curX,curY;
    public String clickRecord = "0";
    private String audioFileName1, audio2;
    private Font font = new Font("Comic Sans MS",0,40);
    private Font fontBig = new Font("Comic Sans MS",0,60);



    public GenButton(Icon character,String characterName,String audio){//constructor for calibration
        super(characterName, character);


        this.setFont(new Font("Comic Sans MS",0,16));
        audioFileName1 = audio;
        addMouseListener(this);
        type = "calibration";
        //character for imageIcon and caption
        //type for
        //whethor correct or not
        //audio for audio file
    }

    public GenButton(Icon picture, String name, boolean corr){//Constructor for non-calibration testButtons
        super(picture);
        type = "test";

        this.name = name;
        correct = corr;
        addMouseListener(this);


    }
    public GenButton(Icon icon,String content){//for Redo, Silly testButtons
        super(icon);
        addMouseListener(this);

        if(content.equals("Redo")) {
            correct = true;
            type = "redo";
            audioFileName1 = null;
            audio2 = null;

        }
        else if(content.equals("Silly")) {
            type = "silly";
            audioFileName1 = null;
            audio2 = null;

        }
        else if(content.equals("Cursor 1")){
            type = "cursor1";
        }
        else if(content.equals("Cursor 2")){
            type = "cursor2";
        }
        else
            type = "next";
    }
    public GenButton(Icon icon, String name, String sen1, String sen2,String aud1, String aud2){ //for sentence testButtons
        super(icon);
        addMouseListener(this);

        type = "sentences";
        sentence1 = sen1;
        sentence2 = sen2;
        audioFileName1 = aud1;
        audio2 = aud2;
    }
    public GenButton(String sentence,String typ){
        super(sentence);
        type = typ;
        addMouseListener(this);

    }
    public int[] click(){
        int x = 0, y = 0;
        System.out.println("Being Clicked");
        clicked = true;
        numClicks++;
        if(numClicks == 1 && (type.equals("sentences") || type.equals("calibration"))){
            if (type.equals("sentences")) {
                this.setIcon(null);
                this.setText(sentence1);
                this.setFont(font);
            }
            else if(type.equals("calibration"))
                playSound(this.audioFileName1);
        }
        else if(numClicks>=2){
            if(type.equals("sentences")){
                if(this.getText().equals(sentence1)){
                    playSound(this.audioFileName1);
                }
                else{
                    playSound(audio2);
                }
            }
        }
        if(type.equals("test")){
            x = this.getLocation().x;
            y = this.getLocation().y;
            System.out.println("X location: "+x);
            System.out.println("Y Location: "+y);
            System.out.println("Test Button clicked");
            if(!correct){
                ProgramManager.displayError();
            }
        }
        int[] coords = {x,y};
        return coords;
    }

    public boolean isClicked(){return clicked;}
    public boolean isVisited(){return visited;}
    public void visit(){visited = true;}
    public boolean isCorrect() { return correct; }
    public void inCorrect(){correct = false;}
    public void correct(){correct = true;}
    public void unClick(){
        numClicks--;
        if(numClicks == 0)
            clicked = false;
    }

    public int getClicks(){return numClicks;}
    /*Method for playing the sound*/

    private void playSound(String filename)
    {
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile( ));
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception ex)
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }


    public void mousePressed(MouseEvent e){/**/}
    public void mouseReleased(MouseEvent e){
        /*Assess correct click, alert message otherwise*/}
    public void mouseClicked(MouseEvent e){
            curX = e.getX();
            curY = e.getY();
        System.out.println(curX);
        System.out.println(curY);
            click();

        /**/}
    public void mouseEntered(MouseEvent e){/*need nothing*/}
    public void mouseExited(MouseEvent e){/**/}
}
