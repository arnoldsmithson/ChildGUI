package slide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class UpdatedSlide extends JPanel implements MouseListener, KeyListener {
    ArrayList<GenButton> testButtons = new ArrayList<>();
    private Font font = new Font("Comic Sans MS", 0,40);
    private Font fontSmall = new Font("Comic Sans MS",0,24);
    private GenButton next = new GenButton(new ImageIcon("src/slide/images/next.png"),"next");
    private GenButton silly = new GenButton(new ImageIcon("src/slide/images/silly.png"),"silly");
    private GenButton redo = new GenButton(new ImageIcon("src/slide/images/redo.png"),"redo");
    private GenButton fish = new GenButton(new ImageIcon("src/slide/images/Fish.png"),"Cursor 1");
    private GenButton turtle = new GenButton(new ImageIcon("src/slide/images/Turtle.png"),"Cursor 2");
    private GenButton sentenceButton;
    private String redoPractice = "Click here to redo \n the practice slides.";
    private GenButton practice = new GenButton("<html>"+redoPractice.replaceAll("\\n","<br>")+"</html>","redoPractice");
    private String calibSentence = "Click here to redo \n the calibration.";
    private GenButton calib = new GenButton("<html>"+calibSentence.replaceAll("\\n","<br>")+"</html>","redoCalibration");
    private String rule,sentence1,sentence2,practiceSentence,audio1,audio2,practiceAudio,distract,target,type,position,targetAmt,targLeft,distAmt;
    private int posInt,targInt,distInt;

    public UpdatedSlide(String typ){
        type = typ;
        switch(type){
            case "intro1":
                intro1();
                break;
            case "intro2":
                intro2();
                break;
            case "intro3":
                intro3();
                break;
            case "ready":
                ready();
                break;
            case "break":
                breakSlide();
                break;
            case "end":
                endSlide();
                break;
        }
    }

    public UpdatedSlide(String spot, String rule, String sentenceBlock, String aud1, String aud2, String targNum,
                        String targSpot, String distractor, String distNum){
        type = "test";
        position = spot.strip();
        this.rule = rule.strip();
        String[] temp = sentenceBlock.strip().split("and");
        String[] sentSplit = temp[0].split(" ");
        target = sentSplit[3].substring(0,1).toUpperCase()+sentSplit[3].substring(1,sentSplit[3].length()-1);
        sentence1 = temp[0];
        sentence2 = temp[1];
        audio1 = aud1.strip();
        audio2 = aud2.strip();
        targLeft = targSpot.strip();
        this.distract = distractor.strip();
        targetAmt = targNum.strip();
        distAmt = distNum.strip();
        posInt = new Integer(position);
        targInt = new Integer(targetAmt);
        distInt = new Integer(distAmt);

        makeSlide();

    }
    public UpdatedSlide(String spot, String sentenceBlock,String aud1, String aud2, String seeAudio){
        type = "practice";
        position = spot.strip();
        String[] temp = sentenceBlock.strip().split("and");
        sentence1 = temp[0];
        String[] sentSplit = sentence1.strip().split(" ");
        target = sentSplit[2];
        sentence2 = temp[1];
        audio1 = "src/slide/audio/"+aud1.strip();
        audio2 = "src/slide/audio/"+aud2.strip();
        if(aud2.equals("#no audio#"))
            audio2 = null;
        practiceAudio = seeAudio;
        switch(position){
            case "1":
                practiceSentence = "See, fishy and turtle can go to different places!";
                practice1();
                break;
            case "2":
                practiceSentence = "See, fishy and turtle can go to the same place!";
                practice2();
                break;
            case "3":
                practiceSentence = "See, sometimes fishy and turtle are just being silly!";
                practice3();
                break;
        }


        //practice slide constructor

    }
    public String getType(){return type;}
    public boolean isClicked(){return next.isClicked();}
    public boolean checkCurs1(){return fish.isClicked();}
    public boolean checkCurs2(){return turtle.isClicked();}
    public boolean checkSilly(){return silly.isClicked();}
    public boolean checkRedo(){return redo.isClicked();}
    public void unClickCurs1(){fish.unClick();}
    public void unClickCurs2(){turtle.unClick();}
    public boolean checkRedoPractice(){return practice.isClicked();}
    public boolean checkRedoCalib(){return calib.isClicked();}
    public void unClickNext(){next.unClick();}


    public UpdatedSlide reset(){
        UpdatedSlide ne = null;
        if(type.equals("intro1") || type.equals("intro2") || type.equals("intro3") || type.equals("ready") || type.equals("break") || type.equals("end")){
            ne = new UpdatedSlide(type);
        }
        else if(type.equals("practice")){

            ne = new UpdatedSlide(position, sentence1+" and "+sentence2,audio1,audio2,practiceAudio);
        }
        else{//type equals test
            ne = new UpdatedSlide(position,rule,sentence1+" and "+sentence2,audio1,audio2,targetAmt,targLeft,distract,distAmt);
        }
        return ne;
    }



    public ArrayList<GenButton> getButtons(){return testButtons;}


    public void makeSlide(){
        this.setLayout(new BorderLayout(5,5));
        JPanel menu = makeMenu("Slide "+posInt);
        JPanel buttonPanel = makeButtons();
        JPanel bottom = makeBottom();
        this.add(menu,BorderLayout.NORTH);
        this.add(buttonPanel,BorderLayout.CENTER);
        this.add(bottom,BorderLayout.SOUTH);
        this.setVisible(true);

    }

    public JPanel makeButtons(){
        JPanel left = new JPanel(new GridLayout(3,0));
        JPanel right = new JPanel(new GridLayout(3,0));
        GenButton tempButton;
        for(int i = 0; i < targInt; i++) {
            if (targLeft.strip().equals("Left")) {
                if(targInt == 3) {
                    left.add(new JPanel());
                    left.add(new JPanel());
                    left.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+target+".png"),target,true));
                }
                else {
                    if (i == 0 || i == 2 || i == 4)
                        left.add(new JPanel());
                    left.add(tempButton = new GenButton(new ImageIcon("src/slide/images/" + target + ".png"), target, true));
                }
                testButtons.add(tempButton);

            } else if (targLeft.strip().equals("Right")) {

                if(targInt == 3) {
                    right.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+target+".png"),target,true));
                    right.add(new JPanel());
                    right.add(new JPanel());
                }
                else{
                    right.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+target+".png"),target,true));
                    if(i == 1 || i == 3 || i == 5)
                        right.add(new JPanel());
                }
                testButtons.add(tempButton);
            } else {
                System.out.println("Error reading!");
            }
        }
        for(int i = 0; i < distInt; i++){
            if(targLeft.equals("Left")){


                if(distInt == 3) {
                    right.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+distract+".png"),distract,false));
                    right.add(new JPanel());
                    right.add(new JPanel());
                }
                else{
                    right.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+distract+".png"),distract,false));
                    if(i == 1 || i == 3 || i == 5)
                        right.add(new JPanel());
                }
                testButtons.add(tempButton);
            }
            else{
                if(distInt == 3) {
                    left.add(new JPanel());
                    left.add(new JPanel());
                    left.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+distract+".png"),distract,true));
                }
                else{
                    if(i == 0 || i == 2 || i ==4)
                        left.add(new JPanel());
                    left.add(tempButton = new GenButton(new ImageIcon("src/slide/images/"+distract+".png"),distract,true));

                }
                testButtons.add(tempButton);
            }
        }
        left.setVisible(true);
        right.setVisible(true);
        return combinePanels(left,right);
    }

    public JPanel combinePanels(JPanel one, JPanel two){
        JPanel combine2 = new JPanel(new BorderLayout(5,5));
        combine2.add(one,BorderLayout.WEST);
        combine2.add(two,BorderLayout.EAST);
        combine2.setVisible(true);
        return combine2;
    }

    public JPanel makeBottom(){
        JPanel bottom = new JPanel(new GridLayout(1,5,0,10));
        for(int j = 0; j < 4; j++) {
            bottom.add(new JPanel());
            if(j == 2 && type.equals("practice"))
                bottom.add(new JTextArea(practiceSentence));
        }

        bottom.add(redo);

        bottom.setVisible(false);
        bottom.setSize(new Dimension(1300,100));
        return bottom;
    }

    public JPanel makeMenu(String title){
        JPanel part1 = new JPanel(new GridLayout(2,1));
        JPanel sec = new JPanel(new GridLayout(1,5,5,5));
        sec.add(next);
        JTextArea titleBlock = new JTextArea("\n"+title);
        titleBlock.setFont(titleBlock.getFont().deriveFont(40f));
        titleBlock.setOpaque(false);

        sentenceButton = new GenButton(new ImageIcon("src/slide/images/ready.png"),"Sentence",sentence1,sentence2,audio1,audio2);
        sec.add(silly);
        sec.add(titleBlock);
        sec.add(fish);
        sec.add(turtle);

        //sentenceButton.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
        part1.add(sec);
        part1.add(sentenceButton);
        part1.setSize(1300,300);
        return part1;
    }


    public void practice1(){
        this.setLayout(new BorderLayout(5,5));
        JPanel part1 = makeMenu("Practice Slide 1");

        JPanel left = new JPanel(new GridLayout(3,0));
        JPanel right = new JPanel(new GridLayout(3,0));
        GenButton object1,object2,object3;
        object1 = new GenButton(new ImageIcon("src/slide/images/Ambulance.png"),"Ambulance",true);
        object2 = new GenButton(new ImageIcon("src/slide/images/Acorn.png"),"Acorn",false);
        object3 = new GenButton(new ImageIcon("src/slide/images/Ape.png"),"Ape",false);
        left.add(new JPanel());
        left.add(object1);
        left.add(new JPanel());
        left.add(object2);
        left.add(new JPanel());
        left.add(object3);
        left.setVisible(true);
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        right.add(object1 = new GenButton(new ImageIcon("src/slide/images/Anchor.png"),"Anchor",false));
        right.add(new JPanel());
        right.add(object2 = new GenButton(new ImageIcon("src/slide/images/Umbrella.png"),"Umbrella",false));
        right.add(new JPanel());
        right.add(object3 = new GenButton(new ImageIcon("src/slide/images/Elephant.png"),"Elephant",true));
        right.add(new JPanel());
        right.setVisible(true);
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        JPanel combine = combinePanels(left,right);
        combine.setVisible(true);
        JPanel bottom = makeBottom();

        this.add(part1,BorderLayout.NORTH);
        this.add(combine,BorderLayout.CENTER);
        this.add(bottom,BorderLayout.SOUTH);
        this.setVisible(true);
    }



    public void practice2(){
        this.setLayout(new BorderLayout(5,5));
        JPanel top = makeMenu("Practice Slide 2");
        JPanel bottom = makeBottom();

        JPanel left = new JPanel(new GridLayout(3,0));
        JPanel right = new JPanel(new GridLayout(3,0));
        GenButton object1,object2,object3;


        left.add(new JPanel());
        left.add(object1 = new GenButton(new ImageIcon("src/slide/images/Ant.png"),"Ant",false));
        left.add(new JPanel());
        left.add(object2 = new GenButton(new ImageIcon("src/slide/images/Egg.png"),"Egg",false));
        left.add(new JPanel());
        left.add(object3 = new GenButton(new ImageIcon("src/slide/images/Apple.png"),"Apple",false));
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        right.add(object1 = new GenButton(new ImageIcon("src/slide/images/Eggplant.png"),"Eggplant",false));
        right.add(new JPanel());
        right.add(object2 = new GenButton(new ImageIcon("src/slide/images/Owl.png"),"Owl",false));
        right.add(new JPanel());
        right.add(object3 = new GenButton(new ImageIcon("src/slide/images/Orange.png"),"Orange",true));
        right.add(new JPanel());
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        JPanel middle = combinePanels(left,right);

        this.add(top,BorderLayout.NORTH);
        this.add(middle,BorderLayout.CENTER);
        this.add(bottom,BorderLayout.SOUTH);
        this.setVisible(true);


    }
    public void practice3(){
        this.setLayout(new BorderLayout(5,5));
        JPanel top = makeMenu("Practice Slide 3");
        JPanel bottom = makeBottom();

        JPanel left = new JPanel(new GridLayout(3,0));
        JPanel right = new JPanel(new GridLayout(3,0));

        GenButton object1,object2,object3;


        left.add(new JPanel());
        left.add(object1 = new GenButton(new ImageIcon("src/slide/images/Octopus.png"),"Octopus",false));
        left.add(new JPanel());
        left.add(object2 = new GenButton(new ImageIcon("src/slide/images/Acorn.png"),"Acorn",false));
        left.add(new JPanel());
        left.add(object3 = new GenButton(new ImageIcon("src/slide/images/Ogre.png"),"Ogre",false));
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        right.add(object1 = new GenButton(new ImageIcon("src/slide/images/Icecream.png"),"Ice Cream",false));
        right.add(new JPanel());
        right.add(object2 = new GenButton(new ImageIcon("src/slide/images/Orange.png"),"Orange",false));
        right.add(new JPanel());
        right.add(object3 = new GenButton(new ImageIcon("src/slide/images/Umbrella.png"),"Umbrella",false));
        right.add(new JPanel());
        testButtons.add(object1);
        testButtons.add(object2);
        testButtons.add(object3);

        JPanel middle = combinePanels(left,right);

        this.add(top,BorderLayout.NORTH);
        this.add(middle,BorderLayout.CENTER);
        this.add(bottom,BorderLayout.SOUTH);
        this.setVisible(true);



    }


    public void intro1(){

        System.out.println("Making first intro slide");
        this.setLayout(new GridLayout(0,1,35,0));
        JTextArea title = new JTextArea("  Welcome to the DET (Determiners in Eye Tracking) study!");
        JTextArea content1 = new JTextArea("  You will look at some pictures while reading/listening to sentences.");
        JTextArea content2 = new JTextArea("  There are 56 sentences, and you will get a break every 20 sentences.");
        JTextArea content3 = new JTextArea("  You will have to click on some pictures according to the instructions in the sentences.");
        JTextArea content4 = new JTextArea("  You will have a choice to not answer any sentences you don’t want to (or can’t) answer – they will be called 'SILLY'");
        JTextArea content5 = new JTextArea("  Your eye will be tracked as you are looking at the screen.");
        JTextArea content6 = new JTextArea("  Your voice will be recorded as you are explaining what you’re thinking as you’re doing the task.");
        JTextArea content7 = new JTextArea("  Your reaction time will be tracked too, so please go as fast as you can but DO NOT sacrifice accuracy.");
        JTextArea content8 = new JTextArea("  In case you change your mind about an answer, you can redo your answer.");
        JTextArea content9 = new JTextArea("  Please ask the experimenter any questions you have now.");
        JPanel smallNext = new JPanel(new GridLayout(1,5));
        for(int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);

        title.setFont(font);
        title.setSize(200,200);
        title.setWrapStyleWord(true);
        title.setLineWrap(true);
        title.setOpaque(false);
        title.setEditable(false);


        content1.setFont(fontSmall);
        content1.setSize(200,200);
        content1.setWrapStyleWord(true);
        content1.setLineWrap(true);
        content1.setOpaque(false);
        content1.setEditable(false);

        content2.setFont(fontSmall);
        content2.setSize(200,200);
        content2.setWrapStyleWord(true);
        content2.setLineWrap(true);
        content2.setOpaque(false);
        content2.setEditable(false);

        content3.setFont(fontSmall);
        content3.setSize(200,200);
        content3.setWrapStyleWord(true);
        content3.setLineWrap(true);
        content3.setOpaque(false);
        content3.setEditable(false);

        content4.setFont(fontSmall);
        content4.setSize(200,200);
        content4.setWrapStyleWord(true);
        content4.setLineWrap(true);
        content4.setOpaque(false);
        content4.setEditable(false);

        content5.setFont(fontSmall);
        content5.setSize(200,200);
        content5.setWrapStyleWord(true);
        content5.setLineWrap(true);
        content5.setOpaque(false);
        content5.setEditable(false);

        content6.setFont(fontSmall);
        content6.setSize(200,200);
        content6.setWrapStyleWord(true);
        content6.setLineWrap(true);
        content6.setOpaque(false);
        content6.setEditable(false);

        content7.setFont(fontSmall);
        content7.setSize(200,200);
        content7.setWrapStyleWord(true);
        content7.setLineWrap(true);
        content7.setOpaque(false);
        content7.setEditable(false);

        content8.setFont(fontSmall);
        content8.setSize(200,200);
        content8.setWrapStyleWord(true);
        content8.setLineWrap(true);
        content8.setOpaque(false);
        content8.setEditable(false);

        content9.setFont(fontSmall);
        content9.setSize(200,200);
        content9.setWrapStyleWord(true);
        content9.setLineWrap(true);
        content9.setOpaque(false);
        content9.setEditable(false);

        this.add(title);
        this.add(content1);
        this.add(content2);
        this.add(content3);
        this.add(content4);
        this.add(content5);
        this.add(content6);
        this.add(content7);
        this.add(content8);
        this.add(content9);
        this.add(smallNext);
        this.setVisible(true);



    }
    public void intro2(){
        System.out.println("Making second intro slide");
        this.setLayout(new GridLayout(4,1,35,0));
        JTextArea title2 = new JTextArea(("  Think Out Loud"));
        JTextArea sentence1 = new JTextArea("  Can you think out loud about how to add 2 by 2?");
        JTextArea sentence2 = new JTextArea("  Can you think out loud about how to multiply 12 by 10?");
        JPanel smallNext = new JPanel(new GridLayout(1,5));
        for(int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);

        title2.setFont(font);
        title2.setWrapStyleWord(true);
        title2.setLineWrap(true);
        title2.setOpaque(false);
        title2.setEditable(false);

        sentence1.setFont(fontSmall);
        sentence1.setWrapStyleWord(true);
        sentence1.setLineWrap(true);
        sentence1.setOpaque(false);
        sentence1.setEditable(false);

        sentence2.setFont(fontSmall);
        sentence2.setWrapStyleWord(true);
        sentence2.setLineWrap(true);
        sentence2.setOpaque(false);
        sentence2.setEditable(false);



        this.add(title2);
        this.add(sentence1);
        this.add(sentence2);
        this.add(smallNext);

    }
    public void intro3(){//calibration slide
        this.setLayout(new BorderLayout(5,5));
        JPanel calibration = new JPanel(new GridLayout(3,6));
        JTextArea sentence = new JTextArea("Eye Tracking Calibration - Please look at each picture as it appears. Please name it." +
                "\n Click on it to listen to the word. Click on it again to continue.");

        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Acorn.png"),"Acorn","src/slide/audio/Acorn.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Ambulance.png"),"Ambulance","src/slide/audio/Ambulance.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Anchor.png"),"Anchor","src/slide/audio/Anchor.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Ant.png"),"Ant","src/slide/audio/Ant.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Ape.png"),"Ape","src/slide/audio/Ape.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Apple.png"),"Apple","src/slide/audio/Apple.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Egg.png"),"Egg","src/slide/audio/Egg.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Eggplant.png"),"Eggplant","src/slide/audio/Eggplant.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Elephant.png"),"Elephant","src/slide/audio/Elephant.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Fish.png"),"Fish","src/slide/audio/Fishy.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Icecream.png"),"Ice Cream","src/slide/audio/Icecream.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Octopus.png"),"Octopus","src/slide/audio/Octopus.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Ogre.png"),"Ogre","src/slide/audio/Ogre.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Orange.png"),"Orange","src/slide/audio/Orange.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Owl.png"),"Owl","src/slide/audio/Owl.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Turtle.png"),"Turtle","src/slide/audio/Turtle.wav"));
        testButtons.add(new GenButton(new ImageIcon("src/slide/images/Umbrella.png"),"Umbrella","src/slide/audio/Umbrella.wav"));
        testButtons.add(next);
        sentence.setFont(new Font("Comic Sans MS",0,24));
        sentence.setWrapStyleWord(true);
        sentence.setLineWrap(true);
        sentence.setOpaque(false);
        sentence.setEditable(false);


        this.add(sentence,BorderLayout.NORTH);
        for(int i = 0; i< testButtons.size(); i++){
            testButtons.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
            testButtons.get(i).setHorizontalTextPosition(0);
            testButtons.get(i).setPreferredSize(new Dimension(150,200));
            testButtons.get(i).setVisible(true);
            if(i != 0)
                testButtons.get(i).setVisible(false);
            calibration.add(testButtons.get(i));

        }
        this.add(calibration,BorderLayout.CENTER);
        calibration.setVisible(true);

        this.setVisible(true);

    }

    public void ready(){
        this.setLayout(new GridLayout(0,1,35,0));
        JTextArea r = new JTextArea("\t\tAre you Ready?");

        r.setFont(font);
        r.setSize(200,200);
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        r.setOpaque(false);
        r.setEditable(false);

        //make gridlayout that centers the testButtons
        JPanel practiceButton = new JPanel(new GridLayout(1,3));
        JPanel calibButton = new JPanel(new GridLayout(1,3));


        practiceButton.add(new JPanel());
        practiceButton.add(practice);
        practiceButton.add(new JPanel());
        calibButton.add(new JPanel());
        calibButton.add(calib);
        calibButton.add(new JPanel());
        practice.setFont(new Font("Comic Sans MS",0,30));
        calib.setFont(new Font("Comic Sans MS",0,30));


        JTextArea start = new JTextArea("\t       Click 'Next' to begin the test slides!");

        start.setFont(font);
        start.setSize(200,200);
        start.setWrapStyleWord(true);
        start.setLineWrap(true);
        start.setOpaque(false);
        start.setEditable(false);

        JPanel smallNext = new JPanel(new GridLayout(1,5));
        for(int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);
        this.add(r);
        this.add(practiceButton);
        this.add(calibButton);
        this.add(start);
        this.add(smallNext);
    }
    public void breakSlide(){
        this.setLayout(new GridLayout(0,1,35,0));
        JTextArea r = new JTextArea("\t          Time to take a break!");
        JTextArea s = new JTextArea("\t     Let your eyes and fingers rest!");
        JPanel smallNext = new JPanel(new GridLayout(1,5));
        for(int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);


        r.setFont(font);
        r.setSize(200,200);
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        r.setOpaque(false);
        r.setEditable(false);

        s.setFont(font);
        s.setSize(200,200);
        s.setWrapStyleWord(true);
        s.setLineWrap(true);
        s.setOpaque(false);
        s.setEditable(false);

        this.add(r);
        this.add(s);
        this.add(smallNext);

    }
    public void endSlide(){
        this.setLayout(new GridLayout(0,1,35,0));
        JTextArea r = new JTextArea("\n\n\n\n\t        You've reached the end!\n\n\t        Thank you so much for participating!");

        r.setFont(font);
        r.setSize(200,200);
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        r.setOpaque(false);
        r.setEditable(false);
        this.add(r);
    }









    public void mouseClicked(MouseEvent event) {

    }

    public void mousePressed(MouseEvent event) {

    }

    public void mouseReleased(MouseEvent event) {

    }

    public void mouseEntered(MouseEvent event) {

    }

    public void mouseExited(MouseEvent event) {

    }
    public void keyPressed(KeyEvent e) {


    }
    public void keyTyped(KeyEvent e){
        System.out.println(e);
        char key = e.getKeyChar();
        switch(key){
            case ' ':
                sentenceButton.click();
                break;
            case 'r'://redo is always first
                redo.click();
                break;
            case 's'://silly is always second
                silly.click();
                break;
            case 'n'://next is always last button in list
                next.click();
                break;
            case 'p'://Ready is always fifth
                sentenceButton.click();
                break;

        }
    }
    public void keyReleased(KeyEvent e){

    }

}
