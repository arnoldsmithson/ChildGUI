package slide;



import jdk.swing.interop.SwingInterOpUtils;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ProgramManager extends JFrame {


    public static long[] records = new long[6];
    public static int recordsFilled = 0;
    private static long oldTime, newTime;

    private Toolkit t1 = Toolkit.getDefaultToolkit();
    private Image img1 = t1.getImage(getClass().getResource("images/Fish.png"));
    private Image img2 = t1.getImage(getClass().getResource("images/Turtle.png"));
    private Image img3 = t1.getImage(getClass().getResource("images/Default.png"));
    private Cursor def = Toolkit.getDefaultToolkit().createCustomCursor(img3, new Point(0, 0), "Default");
    private Cursor fis = Toolkit.getDefaultToolkit().createCustomCursor(img1, new Point(0, 0), "Fishy");
    private Cursor turt = Toolkit.getDefaultToolkit().createCustomCursor(img2, new Point(0, 0), "Turtle");

    private Point dest1, dest2;
    private int x1, y1, x2, y2;
    private int objClicked = 0;

    private final int ADJX = 538;//adjust x coordinate for accuracy in test placement
    private final int ADJY = 350;//adjust y coordinate for accuracy in test placement

    private static boolean fishActive = false, turtleActive = false, defActive = true;
    

    private FileWriter fileWriting;
    private FileWriter cWriter = null;

    private static Calendar myCal;

    private ArrayList<UpdatedSlide> slideOrder16 = new ArrayList<>();
    private ArrayList<UpdatedSlide> slideOrder13 = new ArrayList<>();
    private ArrayList<UpdatedSlide> desiredSlides;

    private UpdatedSlide currentSlide;
    private BufferedWriter bWriter = null;
    private int atSlide, backButton = -1;
    private File fileName;
    private String name, startSlide, ord = "1-6";
    private JPanel curs1 = new JPanel(), curs2 = new JPanel();
    Container p;

    public final static void displayError() {
        JOptionPane.showMessageDialog(null, "Are you sure? Check your click!", "Check", JOptionPane.ERROR_MESSAGE);
    }

    public static void reactionMeasure(String spot) {
        newTime = System.nanoTime();
        long reactionTime = (newTime - oldTime) / 1000000;
        switch (spot) {
            case "Ready":
                records[0] = reactionTime;
                break;
            case "1ObjSent":
                records[1] = reactionTime;
                break;
            case "2ObjNext":
                records[2] = reactionTime;
                break;
            case "fish click":
                records[3] = reactionTime;
                break;
            case "turtle click":
                records[4] = reactionTime;
                break;
            case "silly":
                records[5] = reactionTime;
                break;
        }
        oldTime = System.nanoTime();
        newTime = 0;

    }

    public void resetTimes() {
        for (int i = 0; i < records.length; i++) {
            records[i] = -1;
        }
        recordsFilled = 0;
    }


    private void makeFile() {
        for (int i = 0; i < records.length; i++)
            records[i] = -1;
        String bday = "default";
        startSlide = "0";
        name = JOptionPane.showInputDialog("Subject ID: ");
        fileName = new File("src/slide/logfiles/" + name + ".txt");
        if (fileName.exists()) {
            if (JOptionPane.showConfirmDialog(null, "This file already exists.\nAre you sure you want to continue this test?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Scanner read = null;
                try {
                    read = new Scanner(new File("src/slide/logfiles/" + name + ".txt"));
                } catch (Exception e) {
                    System.out.println("File Not Found");
                }
                while (read.hasNextLine()) {
                    String[] items = read.nextLine().split(" ");
                    if (items[0].equals("Birthday:")) {
                        bday = items[items.length - 1];
                    }
                    if (items[0].equals("Order")) {
                        ord = items[items.length - 1];
                    }
                    if (items[0].equals("Starting")) {
                        if (new Integer(items[items.length - 1]) > new Integer(startSlide))
                            startSlide = items[items.length - 1];
                    }
                }
                // yes option
            } else {
                makeFile();//call again to get new subject ID
            }
        }
        if (!fileName.exists()) {
            int[] dates = new int[3];
            boolean valid = false;
            while(!valid){
                bday = JOptionPane.showInputDialog("Please enter your birthday like this:\nmonth/day/year");
                String[] sections = bday.split("/");
                dates[0] = new Integer(sections[0]);
                dates[1] = new Integer(sections[1]);
                dates[2] = new Integer(sections[2]);
                if (!(1 <= dates[0] && dates[0] < 13) || !(1 <= dates[1] && dates[1] < 32) || !(1900 <= dates[2] && dates[2] < 2019)) {
                    System.out.println(dates[0]+"\n"+dates[1]+"\n"+dates[2]);
                    JOptionPane.showMessageDialog(null,"That is not a valid birthday. \n Please enter your birthday in the month/day/year style.","Bday",JOptionPane.ERROR_MESSAGE);
                }
                else{
                    valid = true;
                }
            }

            ord = JOptionPane.showInputDialog("Please choose an order: Type '1-3' or '1-6.'");
            while (!ord.equals("1-6") && !ord.equals("1-3")) {
                ord = JOptionPane.showInputDialog("That is not a valid order. Please type '1-3' or '1-6.'");
            }
        }

        try {
            fileWriting = new FileWriter(fileName, true);
            bWriter = new BufferedWriter(fileWriting);
            cWriter = new FileWriter(new File("src/slide/csvfiles/statistics.csv"), true);
            int month = myCal.get(Calendar.MONTH)+1;
            bWriter.write("\nDate and Time: " + month + "/" + myCal.get(Calendar.DAY_OF_MONTH) + " at " + myCal.get(Calendar.HOUR_OF_DAY) + ":" + myCal.get(Calendar.MINUTE) + ":" + myCal.get(Calendar.SECOND) + "\n");
            bWriter.write("Birthday: " + bday + "\n");
            bWriter.write("Order chosen: " + ord + "\n");
            bWriter.write("Starting slide: " + startSlide + "\n");
            if (new File("src/slide/csvfiles/statistics.csv").length() == 0)
                cWriter.write("Subject ID,Order,Test/Intro,Sentence Num," +
                        "Condition,RT to Ready Btn,1st Action In/Correct (0/1) character," +
                        "1st Action Dist/Targ (0/1) Object,1st Sentence to Char RT,1st-Char to Obj RT," +
                        "2nd Action In/Correct (0/1) Character,2nd Action Dist/Targ (0/1) Object," +
                        "2nd Action sentence to 1st Character RT,2nd Char to Obj RT," +
                        "Same or Different (1 or 0) object acted upon by Characters,Silly pressed(0/1)," +
                        "RT to Silly Button,Redo button pressed (0/1)\n");
            bWriter.flush();
            cWriter.flush();
        } catch (IOException ioe) {
            System.out.println("ERROR!");
            System.out.println(ioe);
        }
        if (ord.equals("1-3")) {
            desiredSlides = slideOrder13;
        } else if (ord.equals("1-6")) {
            desiredSlides = slideOrder16;
        }
    }

    public ProgramManager(int screen) {
        myCal = new GregorianCalendar();
        makeFile();
        makeIntro();
        processFile();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        p = this.getContentPane();
        if (!startSlide.equals("0")) {
            resetTimes();
            oldTime = System.nanoTime();
            currentSlide = desiredSlides.get(new Integer(startSlide));
            atSlide = new Integer(startSlide) + 1;
        } else {
            currentSlide = desiredSlides.get(0);
            atSlide = 1;
        }
        currentSlide.addMouseListener(currentSlide);
        currentSlide.addKeyListener(currentSlide);
        currentSlide.setFocusable(true);
        currentSlide.requestFocus();
        p.add(currentSlide);


        this.setSize(1350, 800);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        JFrame dummy = new JFrame(gs[screen].getDefaultConfiguration());
        this.setLocationRelativeTo(dummy);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        setCursor(def);

        checkNext();
    }


    public void checkNext() {
        while (atSlide <= desiredSlides.size() + 1) {
            currentSlide.setFocusable(true);
            currentSlide.requestFocus();
            currentSlide.moveToFront(curs1);
            currentSlide.moveToFront(curs2);
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                System.out.println("No sleep");
            }
            if (currentSlide.checkRedo()) {
                objClicked = 0;
                resetTimes();
                this.remove(currentSlide);
/*                this.remove(curs1);
                this.remove(curs2);*/
                desiredSlides.set(atSlide - 1, currentSlide.reset());
/*                curs1 = new JPanel();
                curs2 = new JPanel();*/
                currentSlide = desiredSlides.get(atSlide - 1);
                currentSlide.clickRedo();
                currentSlide.addKeyListener(currentSlide);
                currentSlide.addMouseListener(currentSlide);
                currentSlide.setFocusable(true);
                currentSlide.requestFocus();
                this.add(currentSlide);
                setCursor(def);
                fishActive = false;
                turtleActive = false;
                defActive = true;
                this.repaint();
                this.setVisible(true);
                this.setSize(1350, 800);
                oldTime = System.nanoTime();
            }
            if (atSlide == 3 && !currentSlide.isClicked()) {
                calibrationMethod();
            }
            if (desiredSlides.get(atSlide - 1).isClicked()) {
                nextSlide();
                // p.repaint();
            }
            if (desiredSlides.get(atSlide - 1).checkCurs1()) {
                if (fishActive) {
                    fishActive = false;
                    turtleActive = false;
                    defActive = true;
                    setCursor(def);
                } else {
                    fishActive = true;
                    turtleActive = false;
                    defActive = false;
                    setCursor(fis);

                    reactionMeasure("fish click");
                }
                desiredSlides.get(atSlide - 1).unClickCurs1();

            }
            if (desiredSlides.get(atSlide - 1).checkCurs2()) {
                if (turtleActive) {
                    fishActive = false;
                    turtleActive = false;
                    defActive = true;
                    setCursor(def);
                } else {
                    fishActive = false;
                    turtleActive = true;
                    defActive = false;
                    setCursor(turt);
                    //Add turtle label to layer and move it around
                    reactionMeasure("turtle click");
                }

                desiredSlides.get(atSlide - 1).unClickCurs2();
            }
            if (desiredSlides.get(atSlide - 1).checkRedoCalib()) {
                popToCalibration();
            }
            if (desiredSlides.get(atSlide - 1).checkRedoPractice()) {
                popToPractice();
            }
            if (currentSlide.getType().equals("practice") && !currentSlide.checkFinished()) {
                if(currentSlide.checkSilly()){
                    if(currentSlide.checkReady()){
                        if (currentSlide.sillyCorrect()) {
                            reactionMeasure("silly");
                            currentSlide.setNext();
                        } else {
                            currentSlide.unClickSilly();
                            displayError();
                        }
                    }
                    else{
                        currentSlide.unClickSilly();
                        displayError();
                    }
                } else {
                    ArrayList<GenButton> buttons = currentSlide.getButtons();
                    //System.out.println("Made Buttons");
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isClicked() && !buttons.get(i).isVisited() && currentSlide.checkReady()) {
                            if (objClicked == 0 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                    if (buttons.get(i).isCorrect()) {
                                        buttons.get(i).recordClick(true, 1);
                                        currentSlide.nextSentence();
                                        if (currentSlide.getPosInt() == 1) {
                                            buttons.get(buttons.size() - 1).changeCorrect();
                                            buttons.get(0).changeInCorrect();
                                        }
                                    } else {
                                        System.out.println("Wrong Object 1");
                                        buttons.get(i).unClick();
                                        displayError();
                                    }
                                } else {
                                    System.out.println("Wrong cursor 1");
                                    System.out.println(getCursor().getName());
                                    System.out.println(currentSlide.getSentCurs1());
                                    buttons.get(i).unClick();
                                    displayError();
                                }
                            } else if (objClicked == 1 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                if (getCursor().getName().equals(currentSlide.getSentCurs2())) {
                                    if (buttons.get(i).isCorrect()) {
                                        buttons.get(i).recordClick(true, 2);
                                        currentSlide.setNext();
                                    } else {
                                        System.out.println("Wrong Object");
                                        buttons.get(i).unClick();
                                        if (buttons.get(i).getClicks() == 1) {
                                            buttons.get(i).visit();
                                        }
                                        displayError();
                                    }
                                } else {
                                    System.out.println("Wrong Cursor");
                                    buttons.get(i).unClick();
                                    if (buttons.get(i).getClicks() == 1) {
                                        buttons.get(i).visit();
                                    }
                                    displayError();

                                }
                            } else if (!currentSlide.getRule().equals("control")) {
                                currentSlide.setNext();
                            } else {
                                buttons.get(i).unClick();
                                displayError();
                            }
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {

                            }
                            if (fishActive && buttons.get(i).isClicked() && !buttons.get(i).isVisited()) {
                                curs1 = new JPanel();
                                JLabel fishy = new JLabel(new ImageIcon("src/slide/images/fish.png"));
                                dest1 = new Point(buttons.get(i).getLocation());
                                x1 = dest1.x;
                                y1 = dest1.y + ADJY;
                                curs1.setSize(buttons.get(i).getSize());
                                //curs1.setBounds(x1,y1,buttons.get(i).getWidth(),buttons.get(i).getHeight());
                                curs1.add(fishy);
                                curs1.setOpaque(false);
                                curs1.setLocation(x1, y1);
                                //fish.setIgnoreRepaint(true);
                                currentSlide.add(curs1, 1);


                            } else if (turtleActive && buttons.get(i).isClicked() && !buttons.get(i).isVisited()) {
                                System.out.println("Turtle placed");
                                curs2 = new JPanel();
                                JLabel tur = new JLabel(new ImageIcon("src/slide/images/turtle.png"));
                                dest2 = new Point(buttons.get(i).getLocation());
                                x2 = dest2.x;
                                y2 = dest2.y + ADJY;
                                curs2.setSize(buttons.get(i).getSize());
                                //curs2.setBounds(x,y,buttons.get(i).getWidth(),buttons.get(i).getHeight());
                                curs2.add(tur);
                                curs2.setOpaque(false);
                                curs2.setLocation(x2, y2);
                                currentSlide.add(curs2, 2);
                            }
                            if (buttons.get(i).isClicked() && !buttons.get(i).isVisited()) {
                                System.out.println("Object num increased");
                                objClicked++;
                            }

                            if (objClicked == 2) {
                                System.out.println("Fixing positions");
                                for (int j = 0; j < buttons.size(); j++) {
                                    Point a = buttons.get(j).getLocation();

                                    int x = a.x - ADJX;
                                    int y = a.y + ADJY;
                                    if (buttons.get(j).getClicks() == 2) {//same object
                                        if (fishActive) {
                                            curs2.setLocation(x, y);
                                        } else if (turtleActive) {
                                            curs1.setLocation(x, y);
                                        }
                                    } else {//different object
                                        if (buttons.get(j).isVisited() && buttons.get(j).isClicked()) {
                                            if (fishActive) {
                                                System.out.println("Turtle moved to original spot");
                                                curs2.setLocation(x, y);
                                            } else if (turtleActive) {
                                                System.out.println("Fish moved to original Spot");
                                                curs1.setLocation(x, y);
                                            }
                                        }
                                    }
                                }
                            }

                            buttons.get(i).visit();
                            setCursor(def);
                            fishActive = false;
                            turtleActive = false;
                            defActive = true;

                        } else if (buttons.get(i).isClicked() && !currentSlide.checkReady()) {
                            displayError();
                            buttons.get(i).unClick();
                        }
                    }
                }
            }
            if (currentSlide.getType().equals("test") && !currentSlide.checkFinished()) {
                if (currentSlide.checkSilly()) {
                    if (currentSlide.checkReady()) {
                        reactionMeasure("silly");
                        currentSlide.setNext();
                    } else {
                        currentSlide.unClickSilly();
                    }
                } else {
                    ArrayList<GenButton> buttons = currentSlide.getButtons();
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isClicked() && !buttons.get(i).isVisited() && currentSlide.checkReady()) {
                            if (objClicked == 0 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                currentSlide.nextSentence();
                                System.out.println("First object");
                                System.out.println(getCursor().getName());
                                System.out.println(currentSlide.getSentCurs1());
                                if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                    buttons.get(i).recordClick(true, 1);
                                } else {
                                    buttons.get(i).recordClick(false, 1);
                                }
                            } else if (objClicked == 1 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                currentSlide.setNext();
                                System.out.println("Second object");
                                System.out.println(getCursor().getName());
                                System.out.println(currentSlide.getSentCurs2());
                                if (getCursor().getName().equals(currentSlide.getSentCurs2())) {
                                    buttons.get(i).recordClick(true, 2);
                                } else {
                                    buttons.get(i).recordClick(false, 2);
                                }
                            } else {
                                currentSlide.setNext();
                            }
                            switch (currentSlide.getRule()) {
                                case "same":
                                    buttons.get(i).changeCorrect();
                                    for (int k = 0; k < buttons.size(); k++) {
                                        if (buttons.get(k).isCorrect() && !buttons.get(i).equals(buttons.get(k))) {
                                            buttons.get(k).inCorrect();
                                        }
                                    }
                                    break;
                                case "different":
                                    buttons.get(i).inCorrect();
                                    for (int k = 0; k < buttons.size(); k++) {
                                        if (buttons.get(k).isCorrect() && !buttons.get(i).equals(buttons.get(k))) {
                                            buttons.get(k).changeCorrect();
                                        }
                                    }
                                    break;
                                case "control":
                                    if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                        buttons.get(i).recordClick(true, 1);
                                    } else {
                                        buttons.get(i).recordClick(false, 1);
                                    }
                                    break;
//Reveal Next
                                case "one":
                                    currentSlide.setNext();
                                    if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                        buttons.get(i).recordClick(true, 1);
                                    } else {
                                        buttons.get(i).recordClick(false, 1);
                                    }
                                    break;
//Reveal Next
                            }
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {

                            }

                            if (fishActive) {
                                currentSlide.remove(curs1);
                                curs1 = new JPanel();
                                dest1 = new Point(buttons.get(i).getLocation());
                                x1 = dest1.x;
                                y1 = dest1.y + ADJY;
                                JLabel fishy = new JLabel(new ImageIcon("src/slide/images/fish.png"));
                                curs1.setSize(buttons.get(i).getSize());
                                //curs1.setBounds(x,y,buttons.get(i).getWidth(),buttons.get(i).getHeight());
                                curs1.add(fishy);
                                curs1.setOpaque(false);
                                curs1.setLocation(x1, y1);
                                //fish.setIgnoreRepaint(true);
                                currentSlide.add(curs1, 1);


                            } else if (turtleActive) {
                                currentSlide.remove(curs2);
                                curs2 = new JPanel();
                                dest2 = new Point(buttons.get(i).getLocation());
                                x2 = dest2.x;
                                y2 = dest2.y + ADJY;
                                JLabel tur = new JLabel(new ImageIcon("src/slide/images/turtle.png"));
                                curs2.setSize(buttons.get(i).getSize());
                                // curs2.setBounds(x,y,150,200);
                                curs2.add(tur);
                                curs2.setOpaque(false);
                                curs2.setLocation(x2, y2);
                                currentSlide.add(curs2, 2);
                            }
                            objClicked++;
                            System.out.println(objClicked);
                            /* THIS IS WHERE SECOND CURSOR RELOCATION HAPPENS IN TEST SLIDES*/
                            if (objClicked == 2) {
                                for (int j = 0; j < buttons.size(); j++) {
                                    System.out.println(buttons.get(j).getClicks());
                                    if (buttons.get(j).getClicks() == 2) {//same object
                                        System.out.println("Same Object");
                                        Point a = buttons.get(j).getLocation();//button location

                                        int x = a.x-ADJX;
                                        int y = a.y+ADJY;

                                        if (fishActive) {
                                            curs2.setLocation(x, y);
                                        } else if (turtleActive) {
                                            curs1.setLocation(x, y);
                                        }
                                        break;
                                    } else {//different object
                                        System.out.println("Diff Object");
                                        if (buttons.get(j).isVisited()) {
                                            Point a = buttons.get(j).getLocation();

                                            int x = a.x - ADJX;
                                            int y = a.y + ADJY;
                                            if (fishActive) {
                                                System.out.println("Turtle moved");
                                                curs2.setLocation(x, y);
                                            } else if (turtleActive) {
                                                System.out.println("Fish Moved");
                                                curs1.setLocation(x, y);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }

                            buttons.get(i).visit();
                            setCursor(def);
                            fishActive = false;
                            turtleActive = false;
                            defActive = true;

                        } else if (!currentSlide.checkReady()) {
                            buttons.get(i).unClick();
                            buttons.get(i).unVisit();
                        }
                    }
                }


            }


        }
    }

    public void popToCalibration() {
        backButton = atSlide;
        desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
        goToSlide(3);
    }

    public void popToPractice() {
        backButton = atSlide;
        desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
        goToSlide(4);
    }

    public void calibrationMethod() {
        currentSlide.unFinish();
        int i = 0;
        while (i < 17) {
            System.out.print("");
            if (currentSlide.getButtons().get(i).getClicks() >= 2) {
                i++;
                currentSlide.getButtons().get(i).setVisible(true);
                currentSlide.repaint();
                this.repaint();
            }

        }
        currentSlide.finish();
    }

    public void makeIntro() {
        slideOrder16.add(new UpdatedSlide("intro1", 1));
        slideOrder16.add(new UpdatedSlide("intro2", 2));
        slideOrder16.add(new UpdatedSlide("intro3", 3));

        slideOrder13.add(new UpdatedSlide("intro1", 1));
        slideOrder13.add(new UpdatedSlide("intro2", 2));
        slideOrder13.add(new UpdatedSlide("intro3", 3));

    }

    public void processFile() {
        int numSlides16 = 3;
        int numSlides13 = 3;
        int i = 0;
        int j = 0;
        int k = 0;
        BufferedReader txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/slide/input.txt")));
        try {
            String line = txtReader.readLine();
            while (line != null) {
                String items[] = line.split(",");
                if (items[0].equals("1-6")) {
                    i++;
                    //System.out.println("Adding to 1-6");
                    slideOrder16.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9], items[10]));
                    numSlides16++;
                    if (i == 15) {
                        slideOrder16.add(new UpdatedSlide("break", numSlides16));
                        slideOrder16.add(new UpdatedSlide("ready", numSlides16 + 1));
                        numSlides16 += 2;
                        i = 0;
                    }
                } else if (items[0].equals("1-3")) {
                    j++;
                   // System.out.println("Adding to 1-3");
                    slideOrder13.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9], items[10]));
                    numSlides13++;
                    if (j == 15) {
                        slideOrder13.add(new UpdatedSlide("break", numSlides13));
                        slideOrder13.add(new UpdatedSlide("ready", numSlides13 + 1));
                        numSlides13 += 2;
                        j = 0;
                    }
                } else if (items[0].equals("Intro")) {
                    k++;
                    slideOrder16.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6]));
                    slideOrder13.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6]));
                    numSlides13++;
                    numSlides16++;
                    if (k == 3) {
                        slideOrder16.add(new UpdatedSlide("ready", numSlides16));
                        slideOrder13.add(new UpdatedSlide("ready", numSlides13));
                        numSlides13++;
                        numSlides16++;
                        k = 0;
                    }
                }
                line = txtReader.readLine();
            }
        } catch (Exception e) {
            System.out.println("Bad wrap");
        }
        slideOrder13.add(new UpdatedSlide("end", numSlides13));
        slideOrder16.add(new UpdatedSlide("end", numSlides16));

    }

    public void nextSlide() {
        if (atSlide != desiredSlides.size() && currentSlide.checkFinished()) {
            objClicked = 0;
            writeSlideRecordToFile();
            this.remove(currentSlide);
            this.remove(curs1);
            this.remove(curs2);
            curs1 = new JPanel();
            curs2 = new JPanel();
            if (backButton != -1 && currentSlide.getType().equals("intro3")) {
                desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
                currentSlide = desiredSlides.get(backButton - 1);
                atSlide = backButton;
                backButton = -1;

            } else if (backButton != -1 && currentSlide.getType().equals("practice")) {
                if (atSlide == 6) {
                    desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
                    currentSlide = desiredSlides.get(backButton - 1);
                    atSlide = backButton;
                    backButton = -1;
                } else {
                    desiredSlides.set(atSlide - 1, desiredSlides.get(atSlide - 1).reset());
                    currentSlide = desiredSlides.get(atSlide);
                    atSlide++;
                }
            } else {
                desiredSlides.set(atSlide - 1, desiredSlides.get(atSlide - 1).reset());
                currentSlide = desiredSlides.get(atSlide);
                atSlide++;
            }
            this.add(currentSlide);
            currentSlide.addMouseListener(currentSlide);
            currentSlide.addKeyListener(currentSlide);
            currentSlide.setFocusable(true);
            currentSlide.requestFocus();
            this.setSize(1350, 800);
            //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.repaint();
            this.setVisible(true);
            resetTimes();
            oldTime = System.nanoTime();
            setCursor(def);
            fishActive = false;
            turtleActive = false;
            defActive = true;
        } else {
            writeSlideRecordToFile();
            this.removeMouseListener(currentSlide);
            this.removeKeyListener(currentSlide);
        }
    }

    private void goToSlide(int num) {
        writeSlideRecordToFile();
        this.remove(currentSlide);
        resetTimes();
        objClicked = 0;
        currentSlide.unClickNext();
        desiredSlides.set(num - 1, desiredSlides.get(num - 1).reset());
        currentSlide = desiredSlides.get(num - 1);
        atSlide = num;
        currentSlide.addMouseListener(currentSlide);
        currentSlide.addKeyListener(currentSlide);
        currentSlide.setFocusable(true);
        currentSlide.requestFocus();
        setCursor(def);
        fishActive = false;
        turtleActive = false;
        defActive = true;
        this.add(currentSlide);
        this.repaint();
        this.setSize(1350, 800);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        resetTimes();
        oldTime = System.nanoTime();

    }

    private void writeSlideRecordToFile() {

        String name = fileName.getName();
        name = name.substring(0, name.length() - 4);
        try {
            bWriter.write(desiredSlides.get(atSlide - 1).toString());
            bWriter.flush();
            cWriter.write(desiredSlides.get(atSlide - 1).toCSV(name, ord));
            cWriter.flush();
        } catch (Exception e) {
            System.err.println("ERROR\n" + e);
            e.printStackTrace();
        }

    }

}
