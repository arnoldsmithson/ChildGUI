package src.slide;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;

public class GenButton extends JButton implements MouseListener {

    private ArrayList<String> recordsForFile = new ArrayList<>();
    boolean deselected;
    private String type, sentence1, sentence2, practiceSentence;//What kind of button it is --Target or Distractor
    private boolean correct, clicked, ready = false, visited = false,secVis=false;//whether this button with image is correct or not
    private int numClicks = 0,audOneClicks = 0,audTwoClicks = 0, obj;
    private String audioFileName1, audio2;
    private Font font = new Font("Comic Sans MS", 0, 40);


    public GenButton(Icon character, String characterName, String audio) {//constructor for calibration
        super(characterName, character);
        //calibration means the slide where every object button is introduced. They
        //each have their own name as text, and audio that plays when clicked.

        this.setFont(new Font("Comic Sans MS", 0, 24));
        audioFileName1 = audio;
        addMouseListener(this);
        type = "calibration";
        //character for imageIcon and caption
        //type for
        //whether correct or not
        //audio for audio file
    }

    public GenButton(Icon picture, boolean corr, int objNum) {//Constructor for non-calibration testButtons
        super(picture);
        if (corr) {
            type = "target";
        } else {
            type = "distract";
        }
        correct = corr;
        obj = objNum;
        addMouseListener(this);


    }
    public int getObj(){return obj;}

    public String getType() {
        return type;
    }

    public int getAudOneClicks() {
        return audOneClicks;
    }

    public int getAudTwoClicks() {
        return audTwoClicks;
    }

    public GenButton(Icon icon, String content) {//for Redo, Silly testButtons
        super(icon);
        addMouseListener(this);

        if (content.equals("Redo")) {
            correct = true;
            type = "redo";
            audioFileName1 = null;
            audio2 = null;

        } else if (content.equals("Silly")) {
            type = "silly";
            audioFileName1 = null;
            audio2 = null;

        } else if (content.equals("Cursor 1")) {
            type = "cursor1";
        } else if (content.equals("Cursor 2")) {
            type = "cursor2";
        } else
            type = "next";
    }

    public GenButton(Icon icon, String sen1, String sen2, String sen3, String aud1, String aud2) { //for sentence testButtons
        super(icon);
        addMouseListener(this);
        practiceSentence = sen3;
        type = "sentences";
        sentence1 = sen1;
        sentence2 = sen2;
        audioFileName1 = aud1;
        audio2 = aud2;
    }

    public GenButton(String sentence, String typ) {//buttons for redo practice or calibration
        super(sentence);
        type = typ;
        addMouseListener(this);

    }

    public boolean isReady() {
        return ready;
    }

    public void advanceSentence() {//puts the next sentence on sentence button
        this.setIcon(null);
        this.setText(null);
        this.setText(sentence2);
        this.setFont(font);
        ready = true;
        ProgramManager.reactionMeasure("1ObjSent");
    }

    public void pracSent() {//in case it's a practice slide
        this.setText(null);
        this.setText(practiceSentence);
        this.setFont(font);
    }

    public void increaseClicks() {
        numClicks++;
    }

    public void click() {//general method when a button is clicked
        clicked = true;
        numClicks++;
        visited = false;
        if (numClicks == 1 && (type.equals("sentences") || type.equals("calibration"))) {
            if (type.equals("sentences")) {//if sentence button
                this.setIcon(null);
                this.setText(sentence1);
                this.setFont(font);
                this.ready = true;
                ProgramManager.reactionMeasure("Ready");
            } else {
                playSound(this.audioFileName1);
                audOneClicks++;
            }
        } else if (numClicks >= 2) {
            if (type.equals("sentences")) {
                if (this.getText().equals(sentence1)) {
                    playSound(this.audioFileName1);
                    audOneClicks++;
                } else {
                    playSound(audio2);
                    audTwoClicks++;
                }
            }else if(type.equals("calibration") && numClicks > 2){//if calibration button
                playSound(this.audioFileName1);
                audOneClicks++;
            }
        }
    }

    public void recordClick(boolean cursor, int sentenceNum) {//store information in button for txt file
        String temp = "";
        if (cursor) {//if cursor user used was the right one for the sentence
            temp += "1";

        } else {
            temp += "0";
        }
        temp += "-" + sentenceNum;
        recordsForFile.add(temp);
    }

    public boolean isClicked() {
        return clicked;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void inCorrect() {
        secVis = false;
    }

    public void correct(){secVis = true;}

    public void changeCorrect() {
        type = "target";
        secVis = true;
    }
    public boolean isSecVis() {
        return secVis;
    }

    public void unClick() {
        numClicks--;
        if (numClicks < 1) {
            numClicks = 0;
            clicked = false;
        }
    }

    public void unVisit() {
        visited = false;
    }

    public String toString() {
        String temp = "";
        if (recordsForFile.size() == 0) {
            temp = "0";
        } else {
            for (int i = 0; i < recordsForFile.size(); i++) {
                temp += recordsForFile.get(i);
                if (recordsForFile.size() > 1 && i == 0)
                    temp += ", ";
            }
        }
        return temp;
    }

    public int getClicks() {
        return numClicks;
    }
    /*Method for playing the sound*/

    public static void playSound(String filename) {//method to play audio
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/slide/audio/" + filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }


    public void mousePressed(MouseEvent e) {/**/}
    public void mouseReleased(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { click(); }
    public void mouseEntered(MouseEvent e) {
        deselected = false;
    }
    public void mouseExited(MouseEvent e) {
        deselected = true;
    }
}