package slide;

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

public class GenButton extends JButton implements MouseListener, MouseMotionListener {

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {

    }

    private ArrayList<String> recordsForFile = new ArrayList<>();
    boolean deselected;
    private String type, sentence1, sentence2, practiceSentence, name;//What kind of button it is --Target or Distractor
    private boolean correct, clicked, ready = false, visited = false, firstVis, secVis = false;//whether this button with image is correct or not
    private int numClicks = 0, curX, curY;
    private String audioFileName1, audio2;
    private Font font = new Font("Comic Sans MS", 0, 40);


    public GenButton(Icon character, String characterName, String audio) {//constructor for calibration
        super(characterName, character);


        this.setFont(new Font("Comic Sans MS", 0, 24));
        audioFileName1 = audio;
        addMouseListener(this);
        type = "calibration";
        //character for imageIcon and caption
        //type for
        //whethor correct or not
        //audio for audio file
    }

    public GenButton(Icon picture, String name, boolean corr) {//Constructor for non-calibration testButtons
        super(picture);
        if (corr) {
            type = "target";
        } else {
            type = "distract";
        }
        this.removeMouseMotionListener(this);
        this.name = name;
        correct = corr;
        addMouseListener(this);


    }

    public String getType() {
        return type;
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

    public GenButton(Icon icon, String name, String sen1, String sen2, String sen3, String aud1, String aud2) { //for sentence testButtons
        super(icon);
        addMouseListener(this);
        practiceSentence = sen3;
        type = "sentences";
        sentence1 = sen1;
        sentence2 = sen2;
        audioFileName1 = aud1;
        audio2 = aud2;
    }

    public GenButton(String sentence, String typ) {
        super(sentence);
        type = typ;
        addMouseListener(this);

    }

    public boolean isReady() {
        return ready;
    }

    public void advanceSentence() {
        this.setIcon(null);
        this.setText(null);
        this.setText(sentence2);
        this.setFont(font);
        ready = true;
        ProgramManager.reactionMeasure("1ObjSent");
    }

    public void pracSent() {
        this.setText(null);
        this.setText(practiceSentence);
        this.setFont(font);
    }

    public void increaseClicks() {
        numClicks++;
    }

    public void click() {
        clicked = true;
        numClicks++;
        visited = false;
        if (numClicks == 1 && (type.equals("sentences") || type.equals("calibration"))) {
            if (type.equals("sentences")) {
                this.setIcon(null);
                this.setText(sentence1);
                this.setFont(font);
                this.ready = true;
                ProgramManager.reactionMeasure("Ready");
            } else if (type.equals("calibration"))
                playSound(this.audioFileName1);
        } else if (numClicks >= 2) {
            if (type.equals("sentences")) {
                if (this.getText().equals(sentence1)) {
                    playSound(this.audioFileName1);
                } else {
                    playSound(audio2);
                }
            }
        }
    }

    public void recordClick(boolean cursor, int sentenceNum) {
        String temp = "";
        if (cursor) {
            temp += "1";

        } else {
            temp += "0";
        }
        temp += "-" + sentenceNum;
        if (numClicks == 1) {
            firstVis = correct;
        } else if (numClicks == 2) {
            secVis = correct;
        }
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

    public void sameVisit() {
        clicked = false;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void inCorrect() {
        secVis = false;
    }

    public void changeCorrect() {
        correct = true;
        type = "target";
        secVis = true;
    }

    public void changeInCorrect() {
        correct = false;
    }

    public boolean isFirstVis() {
        return firstVis;
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

    public static void playSound(String filename) {
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

    public void mouseReleased(MouseEvent e) {
        /*Assess correct click, alert message otherwise*/
    }

    public void mouseClicked(MouseEvent e) {
        curX = e.getX();
        curY = e.getY();
        //System.out.println(curX);
        //System.out.println(curY);
        click();

        /**/
    }

    public void mouseEntered(MouseEvent e) {
        deselected = false;
    }

    public void mouseExited(MouseEvent e) {
        deselected = true;
    }
}
