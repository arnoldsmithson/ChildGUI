package slide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UpdatedSlide extends JLayeredPane implements MouseListener, KeyListener{




    ArrayList<GenButton> testButtons = new ArrayList<>();


    private Font font = new Font("Comic Sans MS", 0, 40);
    private Font fontSmall = new Font("Comic Sans MS", 0, 24);

    private String redoPractice = "Click here to redo \n the practice slides.";
    private String calibSentence = "Click here to redo \n the calibration.";


    private GenButton practice = new GenButton("<html>" + redoPractice.replaceAll("\\n", "<br>") + "</html>", "redoPractice");
    private GenButton next = new GenButton(new ImageIcon(getClass().getResource("images/Next.png")), "next");
    private GenButton silly = new GenButton(new ImageIcon(getClass().getResource("images/Silly.png")), "silly");
    private GenButton redo = new GenButton(new ImageIcon(getClass().getResource("images/Redo.png")), "redo");
    private GenButton fish = new GenButton(new ImageIcon(getClass().getResource("images/Fish.png")), "Cursor 1");
    private GenButton turtle = new GenButton(new ImageIcon(getClass().getResource("images/Turtle.png")), "Cursor 2");
    private GenButton sentenceButton;
    private GenButton calib = new GenButton("<html>" + calibSentence.replaceAll("\\n", "<br>") + "</html>", "redoCalibration");

    private String rule, sentence1, sentence2, practiceSentence, audio1,
            audio2, practiceAudio, distract, target, type, position,
            targetAmt, targLeft, distAmt, sentCurs1, sentCurs2, logicRule;


    private int posInt, targInt, distInt;

    private boolean finished = false;

    public UpdatedSlide(String typ,int position) {
        posInt = position;
        finished = true;
        this.addMouseListener(this);
        type = typ;
        switch (type) {
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
                        String targSpot, String distractor, String distNum, String compRule) {
        type = "test";
        logicRule = compRule;
        position = spot.strip();
        this.rule = rule.strip();
        String[] temp = sentenceBlock.strip().split("and");
        String[] sentSplit = temp[0].split(" ");
        String[] sent2Split = temp[1].split(" ");
        sentCurs1 = sentSplit[0].strip();

        target = sentSplit[3].substring(0, 1).toUpperCase() + sentSplit[3].substring(1, sentSplit[3].length() - 1);
        sentence1 = temp[0];
        sentence2 = temp[1];

        sentCurs2 = sentence2.split(" ")[1].strip();
        audio1 = aud1.strip();
        if (aud2 != null)
            audio2 = aud2.strip();
        if (logicRule.equals("one") || logicRule.equals("control")) {
            audio2 = null;
            if (logicRule.equals("control")) {
                target = sent2Split[sent2Split.length - 1];
                target = target.substring(0, target.length() - 2);
            }
        }
        targLeft = targSpot.strip();
        this.distract = distractor.strip();
        targetAmt = targNum.strip();
        distAmt = distNum.strip();
        posInt = new Integer(position);
        targInt = new Integer(targetAmt);
        distInt = new Integer(distAmt);
        this.addMouseListener(this);

        makeSlide();

    }
    public boolean checkReady(){
        return sentenceButton.isReady();
    }
    public boolean sillyCorrect(){
        return silly.isSecVis();
    }
    public void unClickSilly(){
        silly.unClick();
    }
    public boolean checkFinished(){return finished;}

    public int getPosInt(){return posInt;}

    public UpdatedSlide(String spot, String sentenceBlock, String aud1, String aud2, String compRule, String seeAudio) {
        this.addMouseListener(this);
        type = "practice";
        finished = false;
        logicRule = compRule;
        position = spot.strip();
        posInt = new Integer(position);
        String[] temp = sentenceBlock.strip().split("and");
        sentence1 = temp[0];
        String[] sentSplit = sentence1.strip().split(" ");
        target = sentSplit[2];
        sentence2 = temp[1];
        audio1 = aud1.strip();
        audio2 = aud2.strip();

        if(seeAudio.length() > 20)
            practiceAudio = seeAudio.strip().split(" ")[8];
        else
            practiceAudio = seeAudio;
        switch (position) {
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

    public String getType() {
        return type;
    }

    public boolean isClicked() {
        return next.isClicked();
    }

    public boolean checkCurs1() {
        return fish.isClicked();
    }

    public boolean checkCurs2() {
        return turtle.isClicked();
    }

    public boolean checkSilly() {
        return silly.isClicked();
    }

    public boolean checkRedo() {
        return redo.isClicked();
    }

    public void unClickCurs1() {
        fish.unClick();
    }

    public void unClickCurs2() {
        turtle.unClick();
    }

    public boolean checkRedoPractice() {
        return practice.isClicked();
    }

    public boolean checkRedoCalib() {
        return calib.isClicked();
    }

    public void unClickNext() {
        next.unClick();
    }
    public void unFinish(){
        finished = false;
    }
    public void finish(){
        finished = true;
    }

    public void setNext() {
        if (type.equals("practice")) {
            sentenceButton.pracSent();
            GenButton.playSound(practiceAudio);
            next.setVisible(true);
            finished = true;

        } else {
            next.setVisible(true);
            finished = true;
        }
        ProgramManager.reactionMeasure("2ObjNext");
    }

    public String getRule() {
        return logicRule;
    }

    public String getSentCurs1() {
        return sentCurs1;
    }

    public String getSentCurs2() {
        return sentCurs2;
    }

    public void nextSentence() {
        sentenceButton.advanceSentence();
    }

    public UpdatedSlide reset() {
        UpdatedSlide ne = null;
        if (type.equals("intro1") || type.equals("intro2") || type.equals("intro3") || type.equals("ready") || type.equals("break") || type.equals("end")) {
            ne = new UpdatedSlide(type,posInt);
        } else if (type.equals("practice")) {

            ne = new UpdatedSlide(position, sentence1 + " and " + sentence2, audio1, audio2, logicRule,practiceAudio);
        } else {//type equals test
            ne = new UpdatedSlide(position, rule, sentence1 + " and " + sentence2, audio1, audio2, targetAmt, targLeft, distract, distAmt, logicRule);
        }
        this.addMouseListener(ne);
        this.addKeyListener(ne);
        ne.setFocusable(true);
        return ne;
    }

    public ArrayList<GenButton> getButtons() {
        return testButtons;
    }


    public void makeSlide() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel menu = makeMenu("Slide " + posInt);
        JPanel buttonPanel = makeButtons();
        this.add(menu, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setVisible(true);

    }

    public void clickRedo() {
        redo.increaseClicks();
    }

    public JPanel makeButtons() {

        JPanel buttonSlide = new JPanel(new GridLayout(3, 7, 0, 5));
        GenButton temp1 = null, temp2, temp3, temp4 = null;
        for (int i = 0; i < 3; i++) {
            buttonSlide.add(new JPanel());


            if (targLeft.equals("Left")) {

                if (targInt == 3)
                    buttonSlide.add(new JPanel());
                else {
                    temp1 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")), target, true);
                    buttonSlide.add(temp1);
                }

                temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")), target, true);
                buttonSlide.add(temp2);
                buttonSlide.add(new JPanel());
                temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), distract, false);
                buttonSlide.add(temp3);

                if (distInt == 3)
                    buttonSlide.add(new JPanel());
                else {
                    temp4 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), distract, false);
                    buttonSlide.add(temp4);

                }


            } else {

                if (distInt == 3)
                    buttonSlide.add(new JPanel());
                else {
                    temp1 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), distract, false);
                    buttonSlide.add(temp1);
                }

                temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), distract, false);
                buttonSlide.add(temp2);
                buttonSlide.add(new JPanel());
                temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")), target, true);
                buttonSlide.add(temp3);

                if (targInt == 3)
                    buttonSlide.add(new JPanel());
                else {
                    temp4 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")), target, false);
                    buttonSlide.add(temp4);
                }
            }
            if (i != 2)
                buttonSlide.add(new JPanel());
            else {
                buttonSlide.add(next);
                next.setVisible(false);
            }
            if (temp1 != null)
                testButtons.add(temp1);
            testButtons.add(temp2);
            testButtons.add(temp3);
            if (temp4 != null)
                testButtons.add(temp4);

        }
        return buttonSlide;
    }

    public JPanel makeMenu(String title) {
        JPanel part1 = new JPanel(new GridLayout(2, 1));
        JPanel sec = new JPanel(new GridLayout(1, 5, 5, 5));
        sec.add(redo);
        JTextArea titleBlock = new JTextArea("\n" + title);
        titleBlock.setFont(titleBlock.getFont().deriveFont(40f));
        titleBlock.setOpaque(false);
        if(!type.equals("practice"))
            sentenceButton = new GenButton(new ImageIcon(getClass().getResource("images/ready.png")), "Sentence", sentence1, sentence2,"", audio1, audio2);
        else
            sentenceButton = new GenButton(new ImageIcon(getClass().getResource("images/ready.png")), "Sentence", sentence1, sentence2,practiceSentence, audio1, audio2);
        sec.add(silly);
        sec.add(titleBlock);
        sec.add(fish);
        sec.add(turtle);

        //sentenceButton.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
        part1.add(sec);
        part1.add(sentenceButton);
        part1.setSize(1300, 300);
        return part1;
    }


    public void practice1() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel part1 = makeMenu("Practice 1");
        sentCurs1 = "Fishy";
        sentCurs2 = "Turtle";

        JPanel middle;

        GenButton object1, object2, object3,object4,object5,object6;
        object1 = new GenButton(new ImageIcon(getClass().getResource("images/Ambulance.png")), "Ambulance", true);
        object2 = new GenButton(new ImageIcon(getClass().getResource("images/Acorn.png")), "Acorn", false);
        object3 = new GenButton(new ImageIcon(getClass().getResource("images/Ape.png")), "Ape", false);
        object4 = new GenButton(new ImageIcon(getClass().getResource("images/Anchor.png")), "Anchor", false);
        object5 = new GenButton(new ImageIcon(getClass().getResource("images/Umbrella.png")), "Umbrella", false);
        object6 = new GenButton(new ImageIcon(getClass().getResource("images/Elephant.png")), "Elephant", false);

        middle = practiceButtons(object1,object2,object3,object4,object5,object6);

        this.add(part1, BorderLayout.NORTH);
        this.add(middle, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public JPanel practiceButtons(GenButton ob1, GenButton ob2, GenButton ob3, GenButton ob4, GenButton ob5, GenButton ob6){
        JPanel middle = new JPanel(new GridLayout(3,7,5,5));

        middle.add(new JPanel());
        middle.add(new JPanel());
        middle.add(ob1);
        middle.add(new JPanel());
        middle.add(ob2);
        middle.add(new JPanel());
        middle.add(new JPanel());

        middle.add(new JPanel());
        middle.add(new JPanel());
        middle.add(ob3);
        middle.add(new JPanel());
        middle.add(ob4);
        middle.add(new JPanel());
        middle.add(new JPanel());

        middle.add(new JPanel());
        middle.add(new JPanel());
        middle.add(ob5);
        middle.add(new JPanel());
        middle.add(ob6);
        middle.add(new JPanel());
        middle.add(next);

        testButtons.add(ob1);
        testButtons.add(ob2);
        testButtons.add(ob3);
        testButtons.add(ob4);
        testButtons.add(ob5);
        testButtons.add(ob6);
        next.setVisible(false);


        return middle;

    }


    public void practice2() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel top = makeMenu("Practice 2");
        sentCurs1 = "Turtle";
        sentCurs2 = "Fishy";

        JPanel middle;
        GenButton object1 = new GenButton(new ImageIcon(getClass().getResource("images/Ant.png")),"Ant",false);
        GenButton object2 = new GenButton(new ImageIcon(getClass().getResource("images/Egg.png")),"Egg",false);
        GenButton object3 = new GenButton(new ImageIcon(getClass().getResource("images/Apple.png")),"Apple",false);
        GenButton object4 = new GenButton(new ImageIcon(getClass().getResource("images/Eggplant.png")),"Eggplant",false);
        GenButton object5 = new GenButton(new ImageIcon(getClass().getResource("images/Owl.png")),"Owl",false);
        GenButton object6 = new GenButton(new ImageIcon(getClass().getResource("images/Orange.png")),"Orange",true);

        middle = practiceButtons(object1,object2,object3,object4,object5,object6);

        this.add(top, BorderLayout.NORTH);
        this.add(middle, BorderLayout.CENTER);
        this.setVisible(true);


    }

    public void practice3() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel top = makeMenu("Practice 3");

        sentCurs1 = "Turtle";
        GenButton object1, object2, object3,object4,object5,object6;

        object1 = new GenButton(new ImageIcon(getClass().getResource("images/Octopus.png")), "Octopus", false);
        object2 = new GenButton(new ImageIcon(getClass().getResource("images/Acorn.png")), "Acorn", false);
        object3 = new GenButton(new ImageIcon(getClass().getResource("images/Ogre.png")), "Ogre", false);

        object4 = new GenButton(new ImageIcon(getClass().getResource("images/Icecream.png")), "Ice Cream", false);
        object5 = new GenButton(new ImageIcon(getClass().getResource("images/Orange.png")), "Orange", false);
        object6 = new GenButton(new ImageIcon(getClass().getResource("images/Umbrella.png")), "Umbrella", false);

        silly.correct();

        /* Note for when Arnold opens this up later: Practice is working perfectly now, but the test slides are placing objects like before again. Check back,
        * Order is 1-6 and it was exclusively the test slides.*/

        JPanel middle = practiceButtons(object1,object2,object3,object4,object5,object6);

        this.add(top, BorderLayout.NORTH);
        this.add(middle, BorderLayout.CENTER);
        this.setVisible(true);


    }


    public void intro1() {
        posInt = 0;

        this.setLayout(new GridLayout(0, 1, 35, 0));
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
        JPanel smallNext = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);

        title.setFont(font);
        title.setSize(200, 200);
        title.setWrapStyleWord(true);
        title.setLineWrap(true);
        title.setOpaque(false);
        title.setEditable(false);


        content1.setFont(fontSmall);
        content1.setSize(200, 200);
        content1.setWrapStyleWord(true);
        content1.setLineWrap(true);
        content1.setOpaque(false);
        content1.setEditable(false);

        content2.setFont(fontSmall);
        content2.setSize(200, 200);
        content2.setWrapStyleWord(true);
        content2.setLineWrap(true);
        content2.setOpaque(false);
        content2.setEditable(false);

        content3.setFont(fontSmall);
        content3.setSize(200, 200);
        content3.setWrapStyleWord(true);
        content3.setLineWrap(true);
        content3.setOpaque(false);
        content3.setEditable(false);

        content4.setFont(fontSmall);
        content4.setSize(200, 200);
        content4.setWrapStyleWord(true);
        content4.setLineWrap(true);
        content4.setOpaque(false);
        content4.setEditable(false);

        content5.setFont(fontSmall);
        content5.setSize(200, 200);
        content5.setWrapStyleWord(true);
        content5.setLineWrap(true);
        content5.setOpaque(false);
        content5.setEditable(false);

        content6.setFont(fontSmall);
        content6.setSize(200, 200);
        content6.setWrapStyleWord(true);
        content6.setLineWrap(true);
        content6.setOpaque(false);
        content6.setEditable(false);

        content7.setFont(fontSmall);
        content7.setSize(200, 200);
        content7.setWrapStyleWord(true);
        content7.setLineWrap(true);
        content7.setOpaque(false);
        content7.setEditable(false);

        content8.setFont(fontSmall);
        content8.setSize(200, 200);
        content8.setWrapStyleWord(true);
        content8.setLineWrap(true);
        content8.setOpaque(false);
        content8.setEditable(false);

        content9.setFont(fontSmall);
        content9.setSize(200, 200);
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

    public void intro2() {
        posInt = 1;
        this.setLayout(new GridLayout(4, 1, 35, 0));
        JTextArea title2 = new JTextArea(("  Think Out Loud"));
        JTextArea sentence1 = new JTextArea("  Can you think out loud about how to add 2 by 2?");
        JTextArea sentence2 = new JTextArea("  Can you think out loud about how to multiply 12 by 10?");
        JPanel smallNext = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 4; i++)
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

    public void intro3() {//calibration slide
        posInt = 2;
        this.setLayout(new BorderLayout(5, 5));
        JPanel calibration = new JPanel(new GridLayout(3, 6));
        JTextArea sentence = new JTextArea("Eye Tracking Calibration - Please look at each picture as it appears. Please name it." +
                "\n Click on it to listen to the word. Click on it again to continue.");

        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Acorn.png")), "Acorn", "Acorn.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Ambulance.png")), "Ambulance", "Ambulance.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Anchor.png")), "Anchor", "Anchor.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Ant.png")), "Ant", "Ant.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Ape.png")), "Ape", "Ape.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Apple.png")), "Apple", "Apple.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Egg.png")), "Egg", "Egg.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Eggplant.png")), "Eggplant", "Eggplant.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Elephant.png")), "Elephant", "Elephant.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Fish.png")), "Fishy", "Fishy.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Icecream.png")), "Ice Cream", "Icecream.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Octopus.png")), "Octopus", "Octopus.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Ogre.png")), "Ogre", "Ogre.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Orange.png")), "Orange", "Orange.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Owl.png")), "Owl", "Owl.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Turtle.png")), "Turtle", "Turtle.wav"));
        testButtons.add(new GenButton(new ImageIcon(getClass().getResource("images/Umbrella.png")), "Umbrella", "Umbrella.wav"));
        testButtons.add(next);
        sentence.setFont(new Font("Comic Sans MS", 0, 24));
        sentence.setWrapStyleWord(true);
        sentence.setLineWrap(true);
        sentence.setOpaque(false);
        sentence.setEditable(false);


        this.add(sentence, BorderLayout.NORTH);
        for (int i = 0; i < testButtons.size(); i++) {
            testButtons.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
            testButtons.get(i).setHorizontalTextPosition(0);
            testButtons.get(i).setPreferredSize(new Dimension(150, 200));
            testButtons.get(i).setVisible(true);
            if (i != 0)
                testButtons.get(i).setVisible(false);
            calibration.add(testButtons.get(i));

        }
        this.add(calibration, BorderLayout.CENTER);
        calibration.setVisible(true);

        this.setVisible(true);

    }

    public void ready() {
        this.setLayout(new GridLayout(0, 1, 35, 0));
        JTextArea r = new JTextArea("\t\tAre you Ready?");

        r.setFont(font);
        r.setSize(200, 200);
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        r.setOpaque(false);
        r.setEditable(false);

        //make gridlayout that centers the testButtons
        JPanel practiceButton = new JPanel(new GridLayout(1, 3));
        JPanel calibButton = new JPanel(new GridLayout(1, 3));


        practiceButton.add(new JPanel());
        practiceButton.add(practice);
        practiceButton.add(new JPanel());
        calibButton.add(new JPanel());
        calibButton.add(calib);
        calibButton.add(new JPanel());
        practice.setFont(new Font("Comic Sans MS", 0, 30));
        calib.setFont(new Font("Comic Sans MS", 0, 30));


        JTextArea start = new JTextArea("\t       Click 'Next' to begin the test slides!");

        start.setFont(font);
        start.setSize(200, 200);
        start.setWrapStyleWord(true);
        start.setLineWrap(true);
        start.setOpaque(false);
        start.setEditable(false);

        JPanel smallNext = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);
        this.add(r);
        this.add(practiceButton);
        this.add(calibButton);
        this.add(start);
        this.add(smallNext);
    }

    public void breakSlide() {
        this.setLayout(new GridLayout(0, 1, 35, 0));
        JTextArea r = new JTextArea("\t          Time to take a break!");
        JTextArea s = new JTextArea("\t     Let your eyes and fingers rest!");
        JPanel smallNext = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 4; i++)
            smallNext.add(new JPanel());
        smallNext.add(next);


        r.setFont(font);
        r.setSize(200, 200);
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        r.setOpaque(false);
        r.setEditable(false);

        s.setFont(font);
        s.setSize(200, 200);
        s.setWrapStyleWord(true);
        s.setLineWrap(true);
        s.setOpaque(false);
        s.setEditable(false);

        this.add(r);
        this.add(s);
        this.add(smallNext);

    }

    public void endSlide() {
        this.setLayout(new GridLayout(0, 1, 35, 0));
        JTextArea r = new JTextArea("\n\n\n\n\t        You've reached the end!\n\n\t        Thank you so much for participating!");

        r.setFont(font);
        r.setSize(200, 200);
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

    public void keyTyped(KeyEvent e) {

        char key = e.getKeyChar();
        switch (key) {
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
                if(finished){
                    next.click();}
                break;
            case 'p'://Ready is always fifth
                sentenceButton.click();
                break;

        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public String toString() {
        String completeString;

        if (type.equals("practice")) {
            completeString = "On Practice Slide \n Sentences: " + sentence1.strip() + "\t" + sentence2.strip() + "\n";
            completeString += "Rule: " + rule + "\t Redo Clicked: " + redo.getClicks() + "\t Silly Clicked: " + silly.getClicks() + "\n";

            completeString += "Starting Slide: " + (posInt + 3) + "\n\n";
            for (int i = 0; i < 6; i += 2) {
                completeString += testButtons.get(i).toString() + " \t " + testButtons.get(i + 1).toString();
                completeString += "\n";
            }

        } else if (type.equals("test")) {
            completeString = "On Slide " + posInt + "\n" + "Sentences: " + sentence1 + ", " + sentence2 + "\n";
            completeString += "Rule: " + rule + "\t Redo Clicked: " + redo.getClicks() + "\t Silly Clicked: " + silly.getClicks() + "\n";
            completeString += "Section target objects are on: " + targLeft + "\n";
            if(1 <= posInt && posInt < 16){
                completeString += "Starting Slide if unfinished test: 6\n\n";
            }
            else if(16 <= posInt && posInt < 31){
                completeString += "Starting Slide if unfinished test: 23\n\n";
            }
            else if(31 <= posInt && posInt < 46){
                completeString += "Starting Slide if unfinished test: 40\n\n";
            }
            else{
                completeString += "Starting Slide if unfinished test: 57\n\n";
            }
            int size = testButtons.size();
            int iterator = size / 3;
            for (int i = 0; i < size; i += iterator) {
/*                boolean answer = false;
                for(int k = 0; k < iterator; k++){
                    if(testButtons.get(k).toString().length() > 1){
                        answer = true;
                    }
                }*/
                /**
                 * if(answer){ // CASE 2
                 *         completeString += "\t";
                 *         if(testButtons.get(i).toString().length > 3){
                 *         completeString += testButtons.get(i).toString()+"\t";
                 *         }else{
                 *         completeString += testButtons.get(i).toString()+"\t\t";
                 *          }
                 * }else{
                 *     completeString += "\t"+testButtons.get(i).toString() + "\t\t\t" + testButtons.get(i + 1).toString();
                 * }
                 *
                 *
                 *
                 * if(answer){ // CASE 3
                 *          completeString += "\t";
                 *      if(testButtons.get(i).toString().length > 3 || testButtons.get(i+1).toString().length > 3 || testButtons.get(i+2).toString().length > 3){
                 *          if(testButtons.get(i).toString().length > 3){
                 *              completeString += testButtons.get(i)
                 *          }else{
                 *
                 *          }
                 *          if(targLeft.equals("Left")){
                 *
                 *          }else{
                 *
                 *          }
                 *      }
                 *
                 * }else{
                 * }
                 */

                switch (iterator) {
                    case 2:
                        completeString += "\t"+testButtons.get(i).toString() + "\t\t\t" + testButtons.get(i + 1).toString();
                        break;
                    case 3:
                        if (targLeft.equals("Left")) {
                            if(targInt == 3)
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t\t\t" + testButtons.get(i + 1).toString() + "\t\t" + testButtons.get(i + 2).toString();
                            else
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1).toString() + "\t\t\t\t" + testButtons.get(i + 2).toString();
                        } else {
                            if(distInt == 6)
                            completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1).toString() + "\t\t\t\t" + testButtons.get(i + 2).toString();
                            else
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t\t\t" + testButtons.get(i + 1).toString() + "\t\t" + testButtons.get(i + 2).toString();
                        }
                        break;
                    case 4:
                        completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1) + "\t\t\t\t" + testButtons.get(i + 2).toString() + "\t\t" + testButtons.get(i + 3).toString();
                        break;
                }
                completeString += "\n\n";
            }
        } else {
            completeString = "On Slide " + posInt + "\n";
            completeString += "Not Practice or Test Slide.\n";
            completeString += "Starting Slide: " + (posInt) + "\n\n";
        }
        return completeString;
    }
    private String rightClick(String clicked, String cursor){
        String reaction = "";
        if(clicked.equals("1") && cursor.equals("Fishy")){
            reaction += ProgramManager.records[3]+",";
        }
        else if(clicked.equals("0") && cursor.equals("Fishy")){
            reaction += ProgramManager.records[4] + ",";
        }
        else if(clicked.equals("1") && cursor.equals("Turtle")){
            reaction += ProgramManager.records[4]+",";
        }
        else{
            reaction += ProgramManager.records[3]+",";
        }
        return reaction;
    }
    private String objType(GenButton t){
        if(t.getType().equals("target")){
            return "1,";
        }
        else{
            return "0,";
        }
    }

    public String toCSV(String id, String order) {
        int numObjects = 0;
        boolean same = false;
        String completeString = id + "," + order + "," + type + "," + position + "," + rule + "," +
                ProgramManager.records[0] + ",";
        for (int i = 0; i < testButtons.size(); i++) {
            String result = testButtons.get(i).toString();
            if (!result.equals("0")) {
                if (result.length() > 6) {//one object clicked twice
                    numObjects = 1;
                    same = true;
                    String[] results = result.split(",");
                    String result1 = results[0];
                    String result2 = results[1];
                    //cursor check 1
                    completeString += result1.substring(0,1)+",";
                    completeString += objType(testButtons.get(i));
                    completeString += rightClick(result1.substring(0,1),sentCurs1);
                    completeString += ProgramManager.records[1]+",";//adding first Char to Obj
                    completeString += result2.strip().substring(0,1) + ",";
                    completeString += objType(testButtons.get(i));
                    completeString += rightClick(result2.strip().substring(0,1),sentCurs2);
                    completeString += ProgramManager.records[2]+",";//adding first Char to Obj


                    break;
                } else if (result.length() == 3) {//two objects clicked once
                    System.out.println(result.substring(0,1));
                    completeString += result.substring(0,1)+",";
                    /*if(result.substring(result.length()-1).equals("1")){
                        if(testButtons.get(i).isFirstVis()){
                            completeString+= "1,";
                        }
                        else{
                            completeString += "0,";
                        }
                    }
                    else{
                        if(testButtons.get(i).isSecVis()){
                            completeString+= "1,";
                        }
                        else{
                            completeString += "0,";
                        }
                    }*/
                    System.out.println(objType(testButtons.get(i)));
                    completeString += objType(testButtons.get(i));

                    if(numObjects ==0){
                        completeString += rightClick(result.substring(0,1),sentCurs1);
                        completeString += ProgramManager.records[1]+",";
                    }
                    else if(numObjects ==1){
                        completeString += rightClick(result.substring(0,1),sentCurs2);
                        completeString += ProgramManager.records[2]+",";
                    }
                    same = false;
                    numObjects++;
                }
            }
        }
        if (numObjects == 0) {
            completeString += "-1,-1,-1,-1,-1,-1,-1,-1,-1,";
        }
        if (numObjects == 1 && !same){
            completeString += "-1,-1,-1,-1,-1,";
        } else if (numObjects == 2) {
            completeString += "0,";
        }
        else if(same){
            completeString += "1,";
        }
        if (silly.isClicked()) {
            completeString += "1," + ProgramManager.records[5] + ",";
        } else {
            completeString += "0,-1,";
        }
        if (redo.getClicks() > 0) {
            completeString += "1\n";
        } else {
            completeString += "0\n";
        }

        return completeString;
    }
}
