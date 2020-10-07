package slide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UpdatedSlide extends JLayeredPane implements MouseListener, KeyListener{
    ArrayList<GenButton> testButtons = new ArrayList<>();

    //large and small,dyslexic friendly fonts for buttons
    private final Font font = new Font("Comic Sans MS", 0, 40);
    private final Font fontSmall = new Font("Comic Sans MS", 0, 24);


    //strings for their pertinent buttons
    private final String redoPractice = "Click here to redo \n the practice slides.";
    private final String calibSentence = "Click here to redo \n the calibration.";

    //generic buttons that end up on most, if not all slides
    private final GenButton practice = new GenButton("<html>" + redoPractice.replaceAll("\\n", "<br>") + "</html>", "redoPractice");
    private final GenButton next = new GenButton(new ImageIcon(getClass().getResource("images/Next.png")), "next");
    private final GenButton silly = new GenButton(new ImageIcon(getClass().getResource("images/Silly.png")), "silly");
    private final GenButton redo = new GenButton(new ImageIcon(getClass().getResource("images/Redo.png")), "redo");
    private final GenButton fish = new GenButton(new ImageIcon(getClass().getResource("images/Fish.png")), "Cursor 1");
    private final GenButton turtle = new GenButton(new ImageIcon(getClass().getResource("images/Turtle.png")), "Cursor 2");
    private final GenButton calib = new GenButton("<html>" + calibSentence.replaceAll("\\n", "<br>") + "</html>", "redoCalibration");


    //conditions and data that help buttons perform certain actions
    private String rule, sentence1, sentence2, practiceSentence, audio1,
            audio2, practiceAudio, distract, target, type, position,
            targetAmt, targLeft, distAmt, sentCurs1, sentCurs2, logicRule;

    //button that changes based on every slide
    private GenButton sentenceButton;

    //integers that record the amount of buttons needed
    private int posInt, targInt, distInt;
    //if slide has been ran through or not
    private boolean finished = false;


    /**
     * Instantiates a new Updated slide that isn't a practice or test slide.
     *
     * @param typ      the type necessary for the switch statement to determine which slide to make.
     * @param position the index at which the slide rests in the ArrayList, starting at 1.
     */
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

    /**
     * Instantiates a new Updated slide that is part of the test.
     *
     * @param spot          the spot in which the slide lays in the ArrayList
     * @param rule          the rule, to be used when writing the csv file.
     * @param sentenceBlock the sentence block, the line of one or two sentences.
     * @param aud1          the aud 1, string name of audio file 1 for sentence 1.
     * @param aud2          the aud 2, string name of audio file 2 for sentence 2, if sentence 2 exists.
     * @param targNum       the targ num, number of targets in slide to create.
     * @param targSpot      the targ spot, whether the targets are on the left or right of the slide.
     * @param distractor    the distractor, name of distractor objects to use.
     * @param distNum       the dist num,number of distractor objects to instantiate.
     * @param compRule      the comp rule, so the computer can make decisions more easily.
     */
    public UpdatedSlide(String spot, String rule, String sentenceBlock, String aud1, String aud2, String targNum,
                        String targSpot, String distractor, String distNum, String compRule) {
        type = "test";
        logicRule = compRule;
        position = spot.strip();
        this.rule = rule.strip();
        String[] temp = sentenceBlock.strip().split("&");
        String[] sentSplit = temp[0].split(" ");
        String[] sent2Split = temp[1].split(" ");
        sentCurs1 = sentSplit[0].strip();

        if (logicRule.equals("control")) {
            target = sent2Split[sent2Split.length - 1];
            target = target.substring(0, target.length() - 2);
        }else{
        target = sentSplit[sentSplit.length -1].substring(0, 1).toUpperCase() + sentSplit[sentSplit.length -1].substring(1, sentSplit[sentSplit.length - 1].length() - 1);}

        sentence1 = temp[0];
        sentence2 = temp[1];

        if(logicRule.strip().equals("same") || logicRule.strip().equals("different"))
            sentence1 = sentence1.replace('.',',');

        sentCurs2 = sentence2.split(" ")[2].strip();
        audio1 = aud1.strip();
        if (aud2 != null)
            // Turtle clicks the same orange.
            audio2 = aud2.strip();
        if (logicRule.equals("one") || logicRule.equals("control")) {
            audio2 = null;
        }
        targLeft = targSpot.strip();
        this.distract = distractor.strip();
        targetAmt = targNum.strip();
        distAmt = distNum.strip();
        posInt = Integer.parseInt(position);
        targInt = Integer.parseInt(targetAmt);
        distInt = Integer.parseInt(distAmt);
        this.addMouseListener(this);

        makeSlide();

    }
    //GETTER METHODS
    public boolean checkFinished(){return finished;}
    public boolean checkReady(){
        return sentenceButton.isReady();
    }
    public boolean sillyCorrect(){
        return silly.isSecVis();
    }
    public int getPosInt(){return posInt;}

    /**
     * declicks the silly button in the event of an accidental press or click when slide isn't ready
     */
    public void unClickSilly(){
        silly.unClick();
    }


    /**
     * Instantiates a new Updated slide for practice slides.
     *
     * @param spot          the spot in the ArrayList where the practice slide goes.
     * @param sentenceBlock the sentence block of the sentences.
     * @param aud1          the aud 1, audio file 1 for sentence 1
     * @param aud2          the aud 2, audio file 2 for sentence 2, if applicable
     * @param compRule      the comp rule, for computer to make decisions more easily
     * @param seeAudio      the see audio, audio file 3 for rule explanation after slide finished
     */
    public UpdatedSlide(String spot, String sentenceBlock, String aud1, String aud2, String compRule, String seeAudio) {
        this.addMouseListener(this);
        //translate input variables into their respective instance fields
        type = "practice";
        finished = false;
        logicRule = compRule;
        position = spot.strip();
        posInt = Integer.parseInt(position);
        String[] temp = sentenceBlock.strip().split("&");
        sentence1 = temp[0];
        String[] sentSplit = sentence1.strip().split(" ");
        target = sentSplit[2];
        sentence2 = temp[1];
        audio1 = aud1.strip();
        audio2 = aud2.strip();

        if(logicRule.strip().equals("same") || logicRule.strip().equals("different"))
            sentence1 = sentence1.replace('.',',');

        if(seeAudio.length() > 20)
            practiceAudio = seeAudio.strip().split(" ")[8];
        else
            practiceAudio = seeAudio;

        /**
         * this switch statement determines which practice slide to create and which
         * rule-corresponding sentence to add into it.
         */
        switch (position) {
            case "1":
                practiceSentence = "See, sometimes, fishy and turtle can go to different places!";
                practice1();
                break;
            case "2":
                practiceSentence = "See, sometimes, fishy and turtle can go to the same place!";
                practice2();
                break;
            case "3":
                practiceSentence = "<html>See, sometimes fishy and turtle are being silly.<br /><p align-contents:center>You can click silly button!</p></html>";
                practice3();
                break;
        }

        //practice slide constructor

    }
    //GETTER METHODS
    public boolean checkRedoPractice() { return practice.isClicked(); }

    public boolean checkRedoCali() { return calib.isClicked(); }

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

    public String getRule() { return logicRule; }

    public String getSentCurs1() { return sentCurs1; }

    public String getSentCurs2() { return sentCurs2; }

    public ArrayList<GenButton> getButtons() { return testButtons; }

    //SETTER METHODS
    public void unClickCurs1() {
        fish.unClick();
    }
    public void unClickCurs2() {
        turtle.unClick();
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
    public void clickRedo() { redo.increaseClicks(); }

    /**
     * Sets next button to visible and clickable, and plays the rule audio if applicable.
     */
    public void setNext() {
        if (type.equals("practice")) {
            sentenceButton.pracSent(practiceAudio);//move to rule sentence for practice
            GenButton.playSound(practiceAudio);//play its audio
            next.setVisible(true);
            finished = true;

        } else {
            next.setVisible(true);//setting next for test slides
            finished = true;
        }
        if(!silly.isClicked())
            ProgramManager.reactionMeasure("2ObjNext");
        else
            ProgramManager.reactionMeasure("silly");
    }


    /**
     * Next sentence calls the similar method in GenButton. See GenButton.advanceSentence() for more details.
     */
    public void nextSentence() {
        sentenceButton.advanceSentence();
    }


    /**
     * Reset updated slide to original state. Still records previous data for use.
     *
     * @return the updated slide
     */
    public UpdatedSlide reset() {
        UpdatedSlide ne = null;
        if (type.equals("intro1") || type.equals("intro2") || type.equals("intro3") || type.equals("ready") || type.equals("break") || type.equals("end")) {
            ne = new UpdatedSlide(type,posInt);
        } else if (type.equals("practice")) {

            ne = new UpdatedSlide(position, sentence1 + " &" + sentence2, audio1, audio2, logicRule,practiceAudio);
        } else {//type equals test
            ne = new UpdatedSlide(position, rule, sentence1 + " &" + sentence2, audio1, audio2, targetAmt, targLeft, distract, distAmt, logicRule);
        }
        this.addMouseListener(ne);
        this.addKeyListener(ne);
        ne.setFocusable(true);
        return ne;
    }


    /**
     * makeSlide(): creates slide by invoking makeMenu and makeButtons, then sets visibilty to true.
     */
    public void makeSlide() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel menu = makeMenu("Slide " + posInt);
        JPanel buttonPanel = makeButtons();
        this.add(menu, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setVisible(true);

    }


    /**
     * makeButtons(): uses JFrame's GridLayout to set up the 6-12 buttons for each test/practice slide.
     *
     * @return the j panel
     */
    public JPanel makeButtons() {
        JPanel buttonSlide = new JPanel(new GridLayout(3, 7, 0, 5));
        //uses 7 columns instead of 4 to give space between the set of rows and set of columns.
        GenButton temp1 = null, temp2, temp3, temp4 = null;
        for (int i = 0; i < 12; i += 4) {//interating over each row, easier to make than by each column
            buttonSlide.add(new JPanel());


            if (targLeft.equals("Left")) {//if targets are on the left

                if (targInt <= 3)//make blank spot if target only needs 3 buttons
                    buttonSlide.add(new JPanel());
                else {
                    temp1 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 1+i);
                    buttonSlide.add(temp1);
                }

                temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 2+i);

                if(targInt == 1){
                    if(i != 4)
                        buttonSlide.add(new JPanel());
                    else {
                        temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 6);
                        buttonSlide.add(temp2);
                    }
                }else{
                    buttonSlide.add(temp2);
                }



                buttonSlide.add(new JPanel());
                temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")),false, 3+i);

                if(distInt == 1){
                    if(i != 4)
                        buttonSlide.add(new JPanel());
                    else {
                        temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")),false, 7);
                        buttonSlide.add(temp3);
                    }
                }
                else {
                    buttonSlide.add(temp3);
                }


                if (distInt <= 3)//make blank spot if distractor only has 3 buttons
                    buttonSlide.add(new JPanel());
                else {
                    temp4 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")),false, 4+i);
                    buttonSlide.add(temp4);

                }


            } else {//if targets are on the right

                if (distInt <= 3)//see above
                    buttonSlide.add(new JPanel());
                else {
                    temp1 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), false, 1+i);
                    buttonSlide.add(temp1);
                }

                temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")), false, 2+i);


                if(distInt == 1){
                    if(i != 4)
                        buttonSlide.add(new JPanel());
                    else {
                        temp2 = new GenButton(new ImageIcon(getClass().getResource("images/" + distract + ".png")),false, 6);

                        buttonSlide.add(temp2);
                    }
                }
                else {
                    buttonSlide.add(temp2);
                }

                buttonSlide.add(new JPanel());
                temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 3+i);


                if(targInt == 1){
                    if(i != 4)
                        buttonSlide.add(new JPanel());
                    else {
                        temp3 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 7);
                        buttonSlide.add(temp3);
                    }
                }else{
                    buttonSlide.add(temp3);
                }


                if (targInt <= 3)//see above
                    buttonSlide.add(new JPanel());
                else {
                    temp4 = new GenButton(new ImageIcon(getClass().getResource("images/" + target + ".png")),true, 4+i);
                    buttonSlide.add(temp4);
                }
            }
            if (i != 8)//if not final row, add blank spot at the end
                buttonSlide.add(new JPanel());
            else {
                buttonSlide.add(next);
                next.setVisible(false);
            }
            if (temp1 != null)//add each test button to the list of buttons, if applicable for data entry purposes
                testButtons.add(temp1);
            testButtons.add(temp2);
            testButtons.add(temp3);
            if (temp4 != null)
                testButtons.add(temp4);

        }
        return buttonSlide;
    }

    /**
     * makeMenu: Uses two gridlayouts to create the top part of the slides, where
     * the restart, silly, slide title, and cursor buttons live,
     * as well as the sentence button.
     *
     * @param title the title of the slide
     * @return the j panel
     */
    public JPanel makeMenu(String title) {
        JPanel part1 = new JPanel(new GridLayout(2, 1));//creates north block of buttons and sentence
        JPanel sec = new JPanel(new GridLayout(1, 5, 5, 5));//creates top portion of 5 buttons
        sec.add(redo);
        JTextArea titleBlock = new JTextArea("\n" + title);
        titleBlock.setFont(titleBlock.getFont().deriveFont(40f));
        titleBlock.setOpaque(false);
        if(!type.equals("practice"))//create sentence button
            sentenceButton = new GenButton(new ImageIcon(getClass().getResource("images/ready.png")),sentence1, sentence2,"", audio1, audio2);
        else//if type is practice, add practice audio
            sentenceButton = new GenButton(new ImageIcon(getClass().getResource("images/ready.png")),sentence1, sentence2,practiceSentence, audio1, audio2);
        sec.add(silly);
        sec.add(titleBlock);
        sec.add(fish);
        sec.add(turtle);

        part1.add(sec);
        part1.add(sentenceButton);
        part1.setSize(1300, 300);
        return part1;//return menu portion
    }


    public void practice1() {
        this.setLayout(new BorderLayout(5, 5));
        JPanel part1 = makeMenu("Practice 1");
        sentCurs1 = "Fishy";
        sentCurs2 = "Turtle";

        JPanel middle;

        GenButton object1, object2, object3,object4,object5,object6;//specific objects for practice slides
        object1 = new GenButton(new ImageIcon(getClass().getResource("images/Ambulance.png")), true, 2);
        object2 = new GenButton(new ImageIcon(getClass().getResource("images/Acorn.png")), false, 3);
        object3 = new GenButton(new ImageIcon(getClass().getResource("images/Ape.png")), false, 6);
        object4 = new GenButton(new ImageIcon(getClass().getResource("images/Anchor.png")), false, 7);
        object5 = new GenButton(new ImageIcon(getClass().getResource("images/Umbrella.png")), false, 10);
        object6 = new GenButton(new ImageIcon(getClass().getResource("images/Elephant.png")), false, 11);

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
        GenButton object1 = new GenButton(new ImageIcon(getClass().getResource("images/Ant.png")),false, 2);
        GenButton object2 = new GenButton(new ImageIcon(getClass().getResource("images/Egg.png")),false, 3);
        GenButton object3 = new GenButton(new ImageIcon(getClass().getResource("images/Apple.png")),false, 6);
        GenButton object4 = new GenButton(new ImageIcon(getClass().getResource("images/Eggplant.png")),false, 7);
        GenButton object5 = new GenButton(new ImageIcon(getClass().getResource("images/Owl.png")),false, 10);
        GenButton object6 = new GenButton(new ImageIcon(getClass().getResource("images/Orange.png")),true, 11);

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

        object1 = new GenButton(new ImageIcon(getClass().getResource("images/Octopus.png")), false, 2);
        object2 = new GenButton(new ImageIcon(getClass().getResource("images/Acorn.png")),  false, 3);
        object3 = new GenButton(new ImageIcon(getClass().getResource("images/Ogre.png")),  false, 6);

        object4 = new GenButton(new ImageIcon(getClass().getResource("images/Icecream.png")), false, 7);
        object5 = new GenButton(new ImageIcon(getClass().getResource("images/Orange.png")), false, 10);
        object6 = new GenButton(new ImageIcon(getClass().getResource("images/Umbrella.png")), false, 11);

        silly.changeCorrect();

        /* Note for when Arnold opens this up later: Practice is working perfectly now, but the test slides are placing objects like before again. Check back,
         * Order is 1-6 and it was exclusively the test slides.*/

        JPanel middle = practiceButtons(object1,object2,object3,object4,object5,object6);

        this.add(top, BorderLayout.NORTH);
        this.add(middle, BorderLayout.CENTER);
        this.setVisible(true);


    }


    public void intro1() {//method for intro slide for DET
        posInt = 0;

        this.setLayout(new GridLayout(0, 1, 35, 0));
        JTextArea title = new JTextArea("  Welcome to the DET (Determiners in Eye Tracking) study!");
        JTextArea content1 = new JTextArea("  You will look at some pictures while reading/listening to sentences.");
        JTextArea content2 = new JTextArea("  There are 60 sentences, and you will get a break every 15 sentences.");
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

    public void intro2() {//slide for Intro Think Out Loud
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

    public void ready() {//create ready slide
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

    public void breakSlide() {//creates break slide
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

    public void endSlide() {//creates end slide
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

    //these methods are necessary to create for interface, but not used
    public void mouseClicked(MouseEvent event) { }

    public void mousePressed(MouseEvent event) { }

    public void mouseReleased(MouseEvent event) { }

    public void mouseEntered(MouseEvent event) { }

    public void mouseExited(MouseEvent event) { }

    public void keyPressed(KeyEvent e) { }

    public void keyTyped(KeyEvent e) {//simulate mouse click for certain key presses
        //created to make keypresses simulate clicks
        char key = e.getKeyChar();
        switch (key) {
            case ' ':
                if(type.equals("test"))
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
                if(type.equals("test"))
                    sentenceButton.click();
                break;

        }
    }

    public void keyReleased(KeyEvent e) {

    }

    /**
     * toString: Generates human-readable lines of text for flushing into the txt file.
     * @return
     */
    public String toString() {
        String completeString;

        if (type.equals("practice")) {
            completeString = "On Practice Slide \n Sentences: " + sentence1.strip() + "\t" + sentence2.strip() + "\n";
            completeString += "Rule: " + rule + "\t Redo Clicked: " + redo.getClicks() + "\t Silly Clicked: " + silly.getClicks() + "\n";

            completeString += "Starting Position: " + (posInt + 3) + "\n\n";
            for (int i = 0; i < 6; i += 2) {
                completeString += testButtons.get(i).toString() + " \t " + testButtons.get(i + 1).toString();
                completeString += "\n";
            }

        } else if (type.equals("test")) {
            completeString = "On Slide " + posInt + "\n" + "Sentences: " + sentence1 + ", " + sentence2 + "\n";
            completeString += "Rule: " + rule + "\t Redo Clicked: " + redo.getClicks() + "\t Silly Clicked: " + silly.getClicks() + "\n";
            completeString += "Section target objects are on: " + targLeft + "\n";
            if(1 <= posInt && posInt < 15){//restart at selected break slide
                completeString += "Starting Slide if unfinished test (with break slides taken into account): 6\n\n";
            }
            else if(15 <= posInt && posInt < 30){
                completeString += "Starting Slide if unfinished test (with break slides taken into account): 23\n\n";
            }
            else if(30 <= posInt && posInt < 45){
                completeString += "Starting Position if unfinished test: 40\n\n";
            }
            else if(45 <= posInt && posInt < 60){
                completeString += "Starting Position if unfinished test: 57\n\n";
            }
            else if(60 <= posInt && posInt < 75){
                completeString += "Starting Position if unfinished test: 74\n\n";
            }
            else if(75 <= posInt && posInt < 90){
                completeString += "Starting Position if unfinished test: 91\n\n";
            }
            else if(90 <= posInt && posInt < 105){
                completeString += "Starting Position if unfinished test: 108\n\n";
            }
            else{
                completeString += "Starting Position if unfinished test: 125\n\n";
            }
            int size = testButtons.size();//check which buttons were pressed
            int iterator = size / 3; // how many buttons per row for iteration
            for (int i = 0; i < size; i += iterator) {
                switch (iterator) {
                    case 2://if two buttons per row
                        completeString += "\t"+testButtons.get(i).toString() + "\t\t\t" + testButtons.get(i + 1).toString();
                        break;
                    case 3://if three buttons
                        if (targLeft.equals("Left")) {//if targets on left
                            if(targInt == 3)
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t\t\t" + testButtons.get(i + 1).toString() + "\t\t" + testButtons.get(i + 2).toString();
                            else
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1).toString() + "\t\t\t\t" + testButtons.get(i + 2).toString();
                        } else { //if targets on right
                            if(distInt == 6)
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1).toString() + "\t\t\t\t" + testButtons.get(i + 2).toString();
                            else
                                completeString += "\t"+testButtons.get(i).toString() + "\t\t\t\t" + testButtons.get(i + 1).toString() + "\t\t" + testButtons.get(i + 2).toString();
                        }
                        break;
                    case 4://if four buttons per row
                        completeString += "\t"+testButtons.get(i).toString() + "\t\t" + testButtons.get(i + 1) + "\t\t\t\t" + testButtons.get(i + 2).toString() + "\t\t" + testButtons.get(i + 3).toString();
                        break;
                }
                completeString += "\n\n";
            }
        } else {
            completeString = "On Intermission Slide\n";
            completeString += "Not Practice or Test Slide.\n";
            completeString += "Starting Position: " + (posInt) + "\n\n";
        }
        return completeString;
    }

    private String objType(GenButton t){//determines if target or distractor object clicked
        if(t.getType().equals("target")){
            return "1,";
        }
        else{
            return "0,";
        }
    }

    /**
     * To csv generates a data-friendly interpretation of each slide to make statistics easy.
     *
     * @param id    the id
     * @param order the order
     * @return the string
     */
    public String toCSV(String id, String order){
        boolean same = false;
        String buttonPressed1 = "",buttonPressed2 = "";
        String completeString = id + "," + order + "," + type + "," + position + "," + rule + "," +
                ProgramManager.records[0] + ",";//first six items that are guaranteed to occur
        for (int i = 0; i < testButtons.size(); i++) {
            String result = testButtons.get(i).toString();
            if (!result.equals("0")) {
                if (result.length() > 6) {//same object
                    same = true;
                    String[] results = result.split(",");
                    String result1 = results[0];
                    String result2 = results[1];
                    //first set of data created for the first action
                    buttonPressed1 += result1.substring(0, 1) + ",";//1st Character InCorrect
                    buttonPressed1 += objType(testButtons.get(i));//1st Action Targ/Dist object
                    buttonPressed1 += ProgramManager.records[3] + ",";
                    buttonPressed1 += ProgramManager.records[1] + ",";//adding first Char to Obj

                    //second set of data
                    buttonPressed2 += result2.strip().substring(0, 1) + ",";
                    buttonPressed2 += objType(testButtons.get(i));//2nd Action Targ/Dist object
                    buttonPressed2 += ProgramManager.records[4] + ",";
                    buttonPressed2 += ProgramManager.records[2] + ",";//adding second Char to Obj
                } else {
                    if (result.charAt(2) == '1') {
                        buttonPressed1 += result.substring(0, 1) + ",";//1st Character InCorrect
                        buttonPressed1 += objType(testButtons.get(i));//1st Action Targ/Dist object
                        buttonPressed1 += ProgramManager.records[3] + ",";
                        buttonPressed1 += ProgramManager.records[1] + ",";//adding first Char to Obj
                    } else {
                        buttonPressed2 += result.strip().substring(0, 1) + ",";
                        buttonPressed2 += objType(testButtons.get(i));//2nd Action Targ/Dist object
                        buttonPressed2 += ProgramManager.records[4] + ",";
                        buttonPressed2 += ProgramManager.records[2] + ",";//adding second Char to Obj
                    }
                }
            }
        }
        //after analyzing all the buttons, the rest of the line follows
        if(buttonPressed1.equals("") && buttonPressed2.equals("")){
            //sixth and 7th -1's are sentence 1 audio, 12th and 13th are sentence 2 audio
            completeString += "-1,-1,-1,-1,";
            if(sentenceButton != null){
                if(sentenceButton.getAudOneClicks() > 0){
                    completeString += "1,";
                    completeString += sentenceButton.getAudOneClicks() + ",";
                }else{
                    completeString += "0,-1,";
                }
            }else{
                completeString += "-1,-1,";
            }

            completeString += "-1,-1,-1,-1,";
            if(sentenceButton != null){
                if(sentenceButton.getAudTwoClicks() > 0){
                    completeString += "1,";
                    completeString += sentenceButton.getAudTwoClicks() + ",";
                }else{
                    completeString += "0,-1,";
                }
            }else{
                completeString += "-1,-1,";
            }

            completeString += ProgramManager.records[6]+",";//2nd char/silly to Next
            completeString += "-1,";
        }
        else if(buttonPressed2.equals("")){
            completeString += buttonPressed1;
            if(sentenceButton.getAudOneClicks() > 0){
                completeString += "1,";
                completeString += sentenceButton.getAudOneClicks() + ",";
            }else{
                completeString += "0,-1,";
            }
            completeString += "-1,-1,-1,-1,";
            if(sentenceButton.getAudTwoClicks() > 0){
                completeString += "1,";
                completeString += sentenceButton.getAudOneClicks() + ",";
            }else{
                completeString += "0,-1,";
            }
            //add 0,-1 here for no audio 2
            completeString += ProgramManager.records[6]+",";//2nd char/silly to Next
            completeString += "-1,";
        }

        else{
            completeString += buttonPressed1;
            if(sentenceButton.getAudOneClicks() > 0){
                completeString += "1,";
                completeString += sentenceButton.getAudOneClicks() + ",";
            }else{
                completeString += "0,-1,";
            }
            completeString += buttonPressed2;
            if(sentenceButton.getAudTwoClicks() > 0){
                completeString += "1,";
                completeString += sentenceButton.getAudTwoClicks() + ",";
            }else{
                completeString += "0,-1,";
            }
            completeString += ProgramManager.records[6]+",";//2nd char/silly to Next
            if(same)
                completeString += "1,";
            else
                completeString += "0,";
        }

        if (silly.isClicked()) {
            completeString += "1," + ProgramManager.records[5] + ",";
        } else {
            completeString += "0,-1,";
        }
        if (redo.getClicks() > 0) {
            completeString += "1,";
        } else {
            completeString += "0,";
        }
        completeString += ProgramManager.records[7] + ",";
        //add reaction time from previous thing to Redo.
        //will have either number of milliseconds or -1 for never pressed.
        completeString += ProgramManager.totalTime + ",";
        //for loop to grab number of clicks on each object
        if(type.equals("intro3")){
            for(int a = 0; a < testButtons.size(); a++){
                completeString += testButtons.get(a).getClicks() + ",";
            }
        }else{
            int prevObject = 0;
            for(int c = 0; c < testButtons.size();c++){
                String button = testButtons.get(c).toString();
                if(testButtons.get(c).getObj() - prevObject > 1){
                    for(int g = prevObject+1;g < testButtons.get(c).getObj();g++){
                        completeString += "-1,";
                    }
                }
                if(button.equals("0")){//if button not clicked
                    completeString += "0,";
                }else if(button.length() == 3){
                    if(button.charAt(2) == '1'){
                        completeString += "1,";
                    }else{//if button clicked for one sentence
                        completeString += "2,";
                    }
                }else{//if button clicked for both sentences
                    completeString += "3,";
                }
                prevObject = testButtons.get(c).getObj();
            }
            if(prevObject != 12){
                completeString += "-1";
            }
        }
        completeString += "\n";


        return completeString;
    }
}