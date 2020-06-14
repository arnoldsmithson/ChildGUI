package src.slide;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ProgramManager extends JFrame {

    //instance fields for record keeping
    public static long[] records = new long[10];
    private static long oldTime;
    public static long totalTime = 0;

    //necessary fields to prep the cursors
    private Toolkit t1 = Toolkit.getDefaultToolkit();
    private Image img1 = t1.getImage(getClass().getResource("images/Fish.png"));
    private Image img2 = t1.getImage(getClass().getResource("images/Turtle.png"));
    private Image img3 = t1.getImage(getClass().getResource("images/Default.png"));
    private Cursor def = Toolkit.getDefaultToolkit().createCustomCursor(img3, new Point(0, 0), "Default");
    private Cursor fis = Toolkit.getDefaultToolkit().createCustomCursor(img1, new Point(0, 0), "Fishy");
    private Cursor turt = Toolkit.getDefaultToolkit().createCustomCursor(img2, new Point(0, 0), "Turtle");
    private static boolean fishActive = false, turtleActive = false, defActive = true;
    private JPanel curs1 = new JPanel(), curs2 = new JPanel();

    //instance fields for the files
    private File fileName;
    private String startSlide;
    private String ord = "1-6";
    private FileWriter cWriter = null;
    private BufferedWriter bWriter = null;
    private static Calendar myCal;

    //instance fields to control the slides
    private int atSlide, backButton = -1;
    private ArrayList<UpdatedSlide> slideOrder16 = new ArrayList<>();
    private ArrayList<UpdatedSlide> slideOrder13 = new ArrayList<>();
    private ArrayList<UpdatedSlide> desiredSlides;
    private int objClicked = 0;
    private UpdatedSlide currentSlide;
    //adjust x coordinate for accuracy in test placement
    int ADJX = 568;
    //adjust y coordinate for accuracy in test placement
    int ADJY = 350;
    int fishClicked = 0, turClicked = 0;
    String firstClick = "";

    //JFrame variable to help the slides show up
    Container p;

    //error window
    public void displayError() {
        JOptionPane.showMessageDialog(this, "Are you sure? Check your click!", "Check", JOptionPane.ERROR_MESSAGE);
    }


    //method to record time for csv
    public static void reactionMeasure(String spot) {

        long newTime = System.nanoTime();
        long reactionTime = (newTime - oldTime) / 1000000;//convert to milliseconds
        totalTime += reactionTime;
        switch (spot) {
            case "Ready"://when ready clicked
                records[0] = reactionTime;
                break;
            case "1ObjSent": //when first object is clicked
                records[1] = reactionTime;
                break;
            case "2ObjNext": //when second object is clicked
                records[2] = reactionTime;
                break;
            case "first": //when first character clicked
                if (records[3] > -1)
                    records[3] += reactionTime;
                else
                    records[3] = reactionTime;
                break;
            case "second": //when second character clicked
                if (records[4] > -1)
                    records[4] += reactionTime;//in case another cursor is clicked before object is clicked
                else
                    records[4] = reactionTime;
                break;
            case "silly": //when silly clicked
                records[5] = reactionTime;
                break;
            case "toNext": //when next clicked from previous action
                records[6] = reactionTime;
                break;
            case "redo": //when redo is pressed
                records[7] = reactionTime;
                break;
        }
        oldTime = System.nanoTime();

    }

    //method to reset the recorded timesfor next slide
    public void resetTimes() {
        for (int i = 0; i < records.length; i++) {
            records[i] = -1;
        }
        totalTime = 0;
    }


    private void makeFile() {//creates both txt file and either creates or adds onto the csv file
        resetTimes();
        String bday = "default";
        startSlide = "0";
        String name = JOptionPane.showInputDialog(this, "Subject ID: ");
        fileName = new File("src/slide/logfiles/" + name + ".txt");
        if (fileName.exists()) {//condition to create new file or load old one
            if (JOptionPane.showConfirmDialog(this, "This file already exists.\nAre you sure you want to continue this test?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {//if selected yes, then continue previous test
                Scanner read = null;
                try {
                    read = new Scanner(new File("src/slide/logfiles/" + name + ".txt"));//load file
                } catch (Exception e) {
                    System.out.println("File Not Found");
                }
                assert read != null;//make sure that read actually has a file before starting
                while (read.hasNextLine()) { //read in file
                    String[] items = read.nextLine().split(" ");
                    if (items[0].equals("Birthday:")) {//grab bday
                        bday = items[items.length - 1];
                    }
                    if (items[0].equals("Order")) {//grab order
                        ord = items[items.length - 1];
                    }
                    if (items[0].equals("Starting")) { //grab starting slide
                        if (Integer.parseInt(items[items.length - 1]) > Integer.parseInt(startSlide))
                            startSlide = items[items.length - 1];
                    }
                }
                // yes option
            } else {
                makeFile();//call again to get new subject ID
            }
        }
        if (!fileName.exists()) { //new test subject if this is true
            int[] dates = new int[3];
            boolean valid = false;//make sure birthday is valid
            while (!valid) {
                bday = JOptionPane.showInputDialog(this, "Please enter your birthday like this:\nmonth/day/year");//prompt for bday
                String[] sections = bday.split("/");
                dates[0] = Integer.parseInt(sections[0]);
                dates[1] = Integer.parseInt(sections[1]);
                dates[2] = Integer.parseInt(sections[2]);
                if (!(1 <= dates[0] && dates[0] < 13) || !(1 <= dates[1] && dates[1] < 32) || !(1900 <= dates[2] && dates[2] < myCal.get(Calendar.YEAR))) {
                    System.out.println(dates[0] + "\n" + dates[1] + "\n" + dates[2]);//make sure bday is legal, valid days, months, years.
                    JOptionPane.showMessageDialog(this, "That is not a valid birthday. \n Please enter your birthday in the month/day/year style.", "Bday", JOptionPane.ERROR_MESSAGE);
                } else {
                    valid = true;
                }
            }

            ord = JOptionPane.showInputDialog(this, "Please choose an order: Type '1-3' or '1-6.'");
            while (!ord.equals("1-6") && !ord.equals("1-3")) { //choose slide order
                ord = JOptionPane.showInputDialog(this, "That is not a valid order. Please type '1-3' or '1-6.'");
            }
        }

        try {
            FileWriter fileWriting = new FileWriter(fileName, true);
            bWriter = new BufferedWriter(fileWriting);
            cWriter = new FileWriter(new File("src/slide/csvfiles/statistics.csv"), true);
            int month = myCal.get(Calendar.MONTH) + 1;
            int year = myCal.get(Calendar.YEAR);
            bWriter.write("\nDate and Time: " + year + "/" + month + "/" + myCal.get(Calendar.DAY_OF_MONTH) + " at " + myCal.get(Calendar.HOUR) + ":" + myCal.get(Calendar.MINUTE) + ":" + myCal.get(Calendar.SECOND));
            if (myCal.get(Calendar.HOUR_OF_DAY) > 12) {
                bWriter.write(" PM\n");
            } else {
                bWriter.write(" AM\n");
            }
            bWriter.write("Birthday: " + bday + "\n");

            //BIRTHDAY DECIMAL - Accurate to .01, give or take .01 in years for rounding
            String[] sections = bday.split("/");
            int[] dates = new int[3];
            dates[0] = Integer.parseInt(sections[0]);
            dates[1] = Integer.parseInt(sections[1]);
            dates[2] = Integer.parseInt(sections[2]);
            LocalDate birthday = LocalDate.of(dates[2], dates[0], dates[1]);
            LocalDate today = LocalDate.of(myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH) + 1, myCal.get(Calendar.DAY_OF_MONTH));
            DecimalFormat df = new DecimalFormat("0.00");//make format for log file
            double decBDay = ChronoUnit.DAYS.between(birthday, today) / 365.24;

            bWriter.write("Birthday in Decimal: " + df.format(decBDay) + "\n");//write birthday with 2-decimal format

            //BIRTHDAY WHOLE
            int bYea = myCal.get(Calendar.YEAR) - dates[2];
            if (myCal.get(Calendar.MONTH) + 1 < dates[0]) {
                bYea -= 1;//if the current month is behind the birth month
            }
            long bMon = ChronoUnit.MONTHS.between(birthday, today) % 12;
            long bd;
            //NEW DAY CALCULATION
            if(myCal.get(Calendar.DAY_OF_MONTH) >= dates[1]){
                bd = myCal.get(Calendar.DAY_OF_MONTH) - dates[1];
            } else{
                switch((int) bMon){
                    case 4:
                        bd =  (30 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;
                    case 2:
                        bd = (28 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;
                    case 6:
                        bd = (30 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;
                    case 9:
                        bd = (30 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;
                    case 11:
                        bd = (30 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;
                    default:
                        bd = (31 - (dates[1] - myCal.get(Calendar.DAY_OF_MONTH)))+1;
                        break;

                }
            }



            //finishing stat collection
            bWriter.write("Birthday in Whole: " + bYea + ";" + bMon + ";" + (int) bd + ";" + "\n");
            bWriter.write("Order chosen: " + ord + "\n");
            bWriter.write("Starting slide: " + startSlide + "\n");
            if (new File("src/slide/csvfiles/statistics.csv").length() == 0)//if empty stats csv
                cWriter.write("Subject ID,Order,Test/Intro,Sentence Num," +
                        "Condition,RT to Ready Btn,1st Action In/Correct (0/1) character," +
                        "1st Action Dist/Targ (0/1) Object,1st Sentence to Char RT,1st-Char to Obj RT," +
                        "1st Sentence Audio Played, 1st Audio Play Amt," +
                        "2nd Action In/Correct (0/1) Character,2nd Action Dist/Targ (0/1) Object," +
                        "2nd Action sentence to 1st Character RT,2nd Char to Obj RT," +
                        "2nd Sentence Audio Played, 2nd Audio Play Amt," +
                        "Prev Action to Next RT," +
                        "Same or Different (1 or 0) object acted upon by Characters,Silly pressed(0/1)," +
                        "RT to Silly Button,Redo button pressed (0/1),RT from last click to Redo," +
                        "Total Time,obj1,obj2,obj3,obj4,obj5,obj6,obj7,obj8,obj9,obj10,obj11,obj12," +
                        "obj13c,obj14c,obj15c,obj16c,obj17c,obj18c\n");//every single CSV column!
            bWriter.flush();
            cWriter.flush();//add both created titles to their respective files
        } catch (IOException ioe) {
            System.out.println("ERROR!");
            System.out.println(ioe);
        }
        if (ord.equals("1-3")) {
            desiredSlides = slideOrder13;//variable to use throughout the manager, based on choice between 1-3 and 1-6.
        } else if (ord.equals("1-6")) {
            desiredSlides = slideOrder16;
        }
    }

    /**
     * Instantiates a new Program manager which handles the initialization of files and processes the input file into
     * the test slides.
     *
     * @param screen the screen number. 0 for main monitor, 1 for second monitor, etc...
     */
    public ProgramManager(int screen) {//Main function to set up everything and start the slides
        myCal = new GregorianCalendar(); //create calendar for use everywhere
        makeFile();//see method for these three
        makeIntro();
        processFile();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        p = this.getContentPane();
        oldTime = System.nanoTime();//start timing things
        if (!startSlide.equals("0")) {//if continuing from a previously stopped test
            resetTimes();
            oldTime = System.nanoTime();
            currentSlide = desiredSlides.get(Integer.parseInt(startSlide));
            atSlide = Integer.parseInt(startSlide) + 1;//correct the variable
        } else {//if new test, starting at beginning
            currentSlide = desiredSlides.get(0);
            atSlide = 1;
        }
        currentSlide.addMouseListener(currentSlide);//add listeners for commands
        currentSlide.addKeyListener(currentSlide);
        currentSlide.setFocusable(true);
        currentSlide.requestFocus();//need to request focus for keyboard to work
        p.add(currentSlide);

        //This sets frame size and which screen the test will show up on
        this.setSize(1350, 800);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        JFrame dummy = new JFrame(gs[screen].getDefaultConfiguration());//[screen] 0 for main monitor, 1 for secondary, etc
        this.setLocationRelativeTo(dummy);//tags main window onto dummy frame to snap it to main or secondary monitor
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        setCursor(def);

        mainLoop();//loops through checkers
    }


    /**
     * Main method Program Manager calls after initializing files, slide order, etc. Handles all slides
     * until the number of slides is exceeded.
     */
    public void mainLoop() {
        while (atSlide <= desiredSlides.size() + 1) {//while in the slides
            currentSlide.setFocusable(true);//need to regrab focus constantly so keyboard controls work all the time
            currentSlide.requestFocus();
            //cursor placement start
            if (currentSlide.getSentCurs2() != null) {
                if (firstClick.equals("") || firstClick.equals("Fish")) {//if fish was clicked first, make sure it stays on bottom
                    currentSlide.moveToFront(curs1);
                    currentSlide.moveToFront(curs2);
                } else {//if turtle clicked first
                    currentSlide.moveToFront(curs2);
                    currentSlide.moveToFront(curs1);

                }
            }
            //cursor placement end

            try {
                Thread.sleep(5);
            } catch (Exception e) {
                System.out.println("No sleep");//need a small sleep just to allow the threads to work safely
            }


            if (currentSlide.getType().equals("end")) {//if end of slides
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println("Bad timing");
                }
                break;//stop mainloop
            }
            if (currentSlide.checkRedo()) {//if restarting the slide
                reactionMeasure("redo");//capture reaction time
                writeSlideRecordToFile();//record slide with information
                objClicked = 0;
                fishClicked = 0;//reset variables
                turClicked = 0;
                firstClick = "";
                resetTimes();
                this.remove(currentSlide);

                desiredSlides.set(atSlide - 1, currentSlide.reset());

                currentSlide = desiredSlides.get(atSlide - 1);
                currentSlide.clickRedo();//basic JFrame actions to properly make the slide
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
                oldTime = System.nanoTime();//restart measuring
            }
            if (atSlide == 3 && !currentSlide.isClicked()) {//special method for calibration slide
                calibrationMethod();
            }
            if (desiredSlides.get(atSlide - 1).isClicked()) {//method to advance slide
                nextSlide();
            }
            if (desiredSlides.get(atSlide - 1).checkCurs1()) {//method to change cursor to fish
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
                    if (objClicked == 0)
                        reactionMeasure("first");
                    else
                        reactionMeasure("second");
                }
                desiredSlides.get(atSlide - 1).unClickCurs1();

            }
            if (desiredSlides.get(atSlide - 1).checkCurs2()) {//method to change cursor to turtle
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
                    if (objClicked == 0)
                        reactionMeasure("first");
                    else
                        reactionMeasure("second");
                }

                desiredSlides.get(atSlide - 1).unClickCurs2();
            }
            if (desiredSlides.get(atSlide - 1).checkRedoCali()) {//method to redo calibration
                popToCalibration();
            }
            if (desiredSlides.get(atSlide - 1).checkRedoPractice()) {//method to redo practice slides
                popToPractice();
            }

            Point dest1;
            Point dest2;
            int x1;
            int y1;
            int x2;
            int y2;
            if (currentSlide.getType().equals("practice") && !currentSlide.checkFinished()) {//logic for practice slides
                //This logic is different because it uses the displayError(); method.
                if (currentSlide.checkSilly()) {
                    if (currentSlide.checkReady()) {
                        if (currentSlide.sillyCorrect()) {
                            currentSlide.setNext();
                        } else {
                            currentSlide.unClickSilly();
                            displayError();
                        }
                    } else {
                        currentSlide.unClickSilly();
                        displayError();
                    }
                } else {//practice, but not the silly button
                    ArrayList<GenButton> buttons = currentSlide.getButtons();
                    //System.out.println("Made Buttons");
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isClicked() && !buttons.get(i).isVisited() && currentSlide.checkReady()) {
                            if (objClicked == 0 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                if (getCursor().getName().equals(currentSlide.getSentCurs1())) {//checks correctness for practice slides
                                    if (buttons.get(i).isCorrect()) {
                                        buttons.get(i).recordClick(true, 1);
                                        currentSlide.nextSentence();
                                        if (currentSlide.getPosInt() == 1) {
                                            buttons.get(buttons.size() - 1).changeCorrect();
                                            buttons.get(0).inCorrect();
                                        } else {
                                            buttons.get(i).correct();
                                        }
                                    } else {
                                        System.out.println("Wrong Object 1");
                                        buttons.get(i).unClick();
                                        displayError();
                                    }
                                } else {//included print statements for debug purposes, disregard
                                    System.out.println("Wrong cursor 1");
                                    System.out.println(getCursor().getName());
                                    System.out.println(currentSlide.getSentCurs1());
                                    buttons.get(i).unClick();
                                    displayError();
                                }
                            } else if (objClicked == 1 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                if (getCursor().getName().equals(currentSlide.getSentCurs2())) {
                                    if (buttons.get(i).isSecVis()) {

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
                            } catch (Exception ignored) {

                            }
                            if (fishActive && buttons.get(i).isClicked() && !buttons.get(i).isVisited()) {
                                curs1 = new JPanel();
                                JLabel fishy = new JLabel(new ImageIcon("src/slide/images/fish.png"));
                                dest1 = new Point(buttons.get(i).getLocation());
                                x1 = dest1.x;
                                y1 = dest1.y + ADJY;
                                curs1.setSize(buttons.get(i).getSize());
                                curs1.add(fishy);
                                curs1.setOpaque(false);
                                curs1.setLocation(x1, y1);
                                currentSlide.add(curs1, 1);
                                if (turClicked == 0) {//to determine if fish stays on top in graphics
                                    firstClick = "Fish";
                                }
                                fishClicked++;


                            } else if (turtleActive && buttons.get(i).isClicked() && !buttons.get(i).isVisited()) {
                                System.out.println("Turtle placed");
                                curs2 = new JPanel();
                                JLabel tur = new JLabel(new ImageIcon("src/slide/images/turtle.png"));
                                dest2 = new Point(buttons.get(i).getLocation());
                                x2 = dest2.x;
                                y2 = dest2.y + ADJY;
                                curs2.setSize(buttons.get(i).getSize());
                                curs2.add(tur);
                                curs2.setOpaque(false);
                                curs2.setLocation(x2, y2);
                                currentSlide.add(curs2, 2);
                                if (fishClicked == 0) {//to determine which label stays on top in graphics
                                    firstClick = "Turtle";
                                }
                                turClicked++;
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
                                                curs2.setLocation(x, y);
                                            } else if (turtleActive) {
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
            if (currentSlide.getType().equals("test") && !currentSlide.checkFinished()) {//logic for test slides, no displayError necessary
                if (currentSlide.checkSilly()) {
                    if (currentSlide.checkReady()) {
                        currentSlide.setNext();
                        if (objClicked == 1) {
                            System.out.println("resetting");

                            resetLocations();//reset if one object has been clicked
                        }
                    } else {
                        currentSlide.unClickSilly();
                    }
                } else {
                    ArrayList<GenButton> buttons = currentSlide.getButtons();//iterate through each button to listen for clicks
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isClicked() && !buttons.get(i).isVisited() && currentSlide.checkReady()) {
                            if (objClicked == 0 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                currentSlide.nextSentence();
                                if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                    buttons.get(i).recordClick(true, 1);//if cursor matches
                                } else {
                                    buttons.get(i).recordClick(false, 1);//if no match
                                }
                            } else if (objClicked == 1 && !(currentSlide.getRule().equals("one") || currentSlide.getRule().equals("control"))) {
                                currentSlide.setNext();
                                if (getCursor().getName().toLowerCase().equals(currentSlide.getSentCurs2().toLowerCase())) {
                                    buttons.get(i).recordClick(true, 2);
                                } else {
                                    buttons.get(i).recordClick(false, 2);
                                }
                            }
                            if (objClicked == 0) {//if no objects clicked and rule is control or one
                                switch (currentSlide.getRule()) {
                                    case "control"://Should this open up the Next Option as well? Check later
                                        if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                            buttons.get(i).recordClick(true, 1);
                                            ProgramManager.reactionMeasure("1ObjSent");
                                            System.out.println("Measured RT control");
                                            System.out.println(records[1]);
                                        } else {
                                            buttons.get(i).recordClick(false, 1);
                                            ProgramManager.reactionMeasure("1ObjSent");
                                            System.out.println("Measured RT one");
                                            System.out.print("RT: ");
                                            System.out.println(records[1]);
                                        }
                                        currentSlide.setNext();
                                        break;
//Reveal Next
                                    case "one":
                                        if (getCursor().getName().equals(currentSlide.getSentCurs1())) {
                                            buttons.get(i).recordClick(true, 1);
                                            ProgramManager.reactionMeasure("1ObjSent");
                                        } else {
                                            buttons.get(i).recordClick(false, 1);
                                            ProgramManager.reactionMeasure("1ObjSent");
                                        }
                                        currentSlide.setNext();
                                        break;
//Reveal Next
                                }
                            }

                            try {
                                Thread.sleep(10);
                            } catch (Exception ignored) { //sleep command to make sure threads are safe

                            }

                            if (fishActive) {//if painting a fish once or twice
                                if (fishClicked == 1) {
                                    currentSlide.remove(curs2);
                                    curs2 = new JPanel();
                                    dest1 = new Point(buttons.get(i).getLocation());
                                    x2 = dest1.x;
                                    y2 = dest1.y + ADJY;
                                    JLabel tur = new JLabel(new ImageIcon("src/slide/images/fish.png"));
                                    curs2.setSize(buttons.get(i).getSize());
                                    // curs2.setBounds(x,y,150,200);
                                    curs2.add(tur);
                                    curs2.setOpaque(false);
                                    curs2.setLocation(x2, y2);
                                    currentSlide.add(curs2, 2);
                                    fishClicked++;

                                } else {
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
                                    fishClicked++;
                                    if (turClicked == 0) {//to determine if fish stays on top in graphics
                                        firstClick = "Fish";
                                    }
                                }

                            } else if (turtleActive) {//if painting a turtle once or twice
                                if (turClicked == 1) {
                                    currentSlide.remove(curs1);
                                    curs1 = new JPanel();
                                    dest2 = new Point(buttons.get(i).getLocation());
                                    x1 = dest2.x;
                                    y1 = dest2.y + ADJY;//have to adjust Y to make coordinates correct
                                    JLabel fishy = new JLabel(new ImageIcon("src/slide/images/turtle.png"));
                                    curs1.setSize(buttons.get(i).getSize());
                                    curs1.add(fishy);
                                    curs1.setOpaque(false);
                                    curs1.setLocation(x1, y1);
                                    currentSlide.add(curs1, 1);
                                    turClicked++;
                                } else {
                                    currentSlide.remove(curs2);
                                    curs2 = new JPanel();
                                    dest2 = new Point(buttons.get(i).getLocation());
                                    x2 = dest2.x;
                                    y2 = dest2.y + ADJY;
                                    JLabel tur = new JLabel(new ImageIcon("src/slide/images/turtle.png"));
                                    curs2.setSize(buttons.get(i).getSize());
                                    curs2.add(tur);
                                    curs2.setOpaque(false);
                                    curs2.setLocation(x2, y2);
                                    currentSlide.add(curs2, 2);
                                    turClicked++;
                                    if (fishClicked == 0) {//to determine which label stays on top in graphics
                                        firstClick = "Turtle";
                                    }
                                }
                            }
                            objClicked++;
                            /* THIS IS WHERE SECOND CURSOR RELOCATION HAPPENS IN TEST SLIDES*/
                            if (objClicked == 2) {
                                for (int j = 0; j < buttons.size(); j++) {
                                    if (buttons.get(j).getClicks() == 2) {//same object
                                        Point a = buttons.get(j).getLocation();//button location

                                        int x = a.x - ADJX;
                                        int y = a.y + ADJY;
                                        if (turClicked == 2 || fishClicked == 2) {
                                            //this allows the second label to show up, showing two fishes or turtles clicked
                                            x -= 25;
                                            y -= 15;
                                        }

                                        if (fishActive) {
                                            if (fishClicked == 2)
                                                curs1.setLocation(x, y);
                                            else
                                                curs2.setLocation(x, y);
                                        } else if (turtleActive) {
                                            if (turClicked == 2)
                                                curs2.setLocation(x, y);
                                            else
                                                curs1.setLocation(x, y);
                                        }
                                        break;
                                    } else {//different object
                                        if (buttons.get(j).isVisited()) {
                                            Point a = buttons.get(j).getLocation();

                                            int x = a.x - ADJX;
                                            int y = a.y + ADJY;

                                            if (fishActive) {
                                                if (fishClicked == 2)
                                                    curs1.setLocation(x, y);
                                                else
                                                    curs2.setLocation(x, y);
                                            } else if (turtleActive) {
                                                if (turClicked == 2)
                                                    curs2.setLocation(x, y);
                                                else
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

    private void resetLocations() {//method to reset first cursor if silly clicked
        ArrayList<GenButton> buttons = currentSlide.getButtons();
        try {
            Thread.sleep(2);
        }catch (Exception e){

        }
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isClicked()) {
                Point a = buttons.get(i).getLocation();

                int x = a.x - ADJX;
                int y = a.y + ADJY;
                if (firstClick.equals("Fish")) {

                    curs1.setLocation(x, y);
                } else {
                    System.out.println("Resetting turtle");

                    curs2.setLocation(x, y);

                }
            }
        }

    }

    /**
     * Goes to the calibration slide to redo the calibration in the event a test is interrupted and
     * the user returns to test again.
     */
    public void popToCalibration() {
        backButton = atSlide;
        desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
        goToSlide(3);
    }

    /**
     * Pop to practice slides in the event a test is interrupted and the examinee returns and wants to practice again.
     */
    public void popToPractice() {
        backButton = atSlide;
        desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
        goToSlide(4);
    }

    /**
     * Calibration method handles the specific calibration slide that procedurally reveals character buttons.
     */
    public void calibrationMethod() {
        currentSlide.unFinish();//don't let next be clicked via keyboard
        int i = 0;
        while (i < 17) {
            System.out.print("");
            if (currentSlide.getButtons().get(i).getClicks() >= 2) {
                i++;
                currentSlide.getButtons().get(i).setVisible(true);//make the next button visible, repaint
                currentSlide.repaint();
                this.repaint();
            }

        }
        currentSlide.finish();//shows that the slide has finished so the next button can be pressed.
    }

    /**
     * Makes intro slides for each set of slides.
     */
    public void makeIntro() {//switching the numbers worked, include in document
        slideOrder16.add(new UpdatedSlide("intro2", 1));
        slideOrder16.add(new UpdatedSlide("intro1", 2));
        slideOrder16.add(new UpdatedSlide("intro3", 3));

        slideOrder13.add(new UpdatedSlide("intro2", 1));
        slideOrder13.add(new UpdatedSlide("intro1", 2));
        slideOrder13.add(new UpdatedSlide("intro3", 3));

    }

    /**
     * Processes the input.txt file for each set of slides. Populates the ArrayList with a number of UpdatedSlides.
     */
    public void processFile() {
        int numSlides16 = 3;
        int numSlides13 = 3;
        int i = 0;
        int j = 0;
        int k = 0;
        BufferedReader txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("input.txt")));
        try {
            String line = txtReader.readLine();
            while (line != null) {
                String[] items = line.split(",");//create individual items
                if (items[0].equals("1-6")) {//if 1-6 order
                    i++;
                    slideOrder16.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9], items[10]));
                    //create test slide based off information
                    numSlides16++;
                    if (i == 15 && numSlides16 <= 60) {//if 15 slides have passed and we're not at the end
                        slideOrder16.add(new UpdatedSlide("break", numSlides16));
                        slideOrder16.add(new UpdatedSlide("ready", numSlides16 + 1));
                        numSlides16 += 2;//for both slides added
                        i = 0;//reset counter
                    }
                } else if (items[0].equals("1-3")) {
                    j++;
                    slideOrder13.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6], items[7], items[8], items[9], items[10]));
                    numSlides13++;
                    if (j == 15 && numSlides13 <= 60) {//same comments as 1-6
                        slideOrder13.add(new UpdatedSlide("break", numSlides13));
                        slideOrder13.add(new UpdatedSlide("ready", numSlides13 + 1));
                        numSlides13 += 2;
                        j = 0;
                    }
                } else if (items[0].equals("Intro")) {//for practice slides
                    k++;
                    slideOrder16.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6]));
                    slideOrder13.add(new UpdatedSlide(items[1], items[2], items[3], items[4], items[5], items[6]));
                    numSlides13++;
                    numSlides16++;
                    if (k == 3) {//add ready slide after three practice slides
                        slideOrder16.add(new UpdatedSlide("ready", numSlides16));
                        slideOrder13.add(new UpdatedSlide("ready", numSlides13));
                        numSlides13++;
                        numSlides16++;
                        k = 0;
                    }
                }
                line = txtReader.readLine();//advance to next input line
            }
        } catch (Exception e) {
            System.out.println("Bad wrap");
            System.err.println(e);
        }
        //add end slides after all test slides have been read
        slideOrder13.add(new UpdatedSlide("end", numSlides13));
        slideOrder16.add(new UpdatedSlide("end", numSlides16));

    }

    /**
     * Next slide advances to the next slide while resetting the previous one in case someone stops mid test.
     */
    public void nextSlide() {
        if (atSlide != desiredSlides.size() && currentSlide.checkFinished()) {
            objClicked = 0;//reset variables
            fishClicked = 0;
            turClicked = 0;
            firstClick = "";
            reactionMeasure("toNext");//add when next was clicked for writing to file
            writeSlideRecordToFile();
            this.remove(currentSlide);
            this.remove(curs1);
            this.remove(curs2);
            curs1 = new JPanel();
            curs2 = new JPanel();
            if (backButton != -1 && currentSlide.getType().equals("intro3")) {// if on third intro slide
                desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
                currentSlide = desiredSlides.get(backButton - 1);
                atSlide = backButton;
                backButton = -1;

            } else if (backButton != -1 && currentSlide.getType().equals("practice")) {//if on a practice slide
                if (atSlide == 6) {//if popped back to practice slide
                    desiredSlides.set(backButton - 1, desiredSlides.get(backButton - 1).reset());
                    currentSlide = desiredSlides.get(backButton - 1);
                    atSlide = backButton;
                    backButton = -1;
                } else {//if proceeding naturally
                    desiredSlides.set(atSlide - 1, desiredSlides.get(atSlide - 1).reset());
                    currentSlide = desiredSlides.get(atSlide);
                    atSlide++;
                }
            } else {//if test slide
                desiredSlides.set(atSlide - 1, desiredSlides.get(atSlide - 1).reset());
                currentSlide = desiredSlides.get(atSlide);
                atSlide++;
            }
            //set up slide, much like in the constructor
            this.add(currentSlide);
            currentSlide.addMouseListener(currentSlide);
            currentSlide.addKeyListener(currentSlide);
            currentSlide.setFocusable(true);
            currentSlide.requestFocus();
            this.setSize(1350, 800);
            this.repaint();
            this.setVisible(true);
            resetTimes();
            oldTime = System.nanoTime();
            setCursor(def);
            fishActive = false;
            turtleActive = false;
            defActive = true;
        } else {//if last slide is reached
            writeSlideRecordToFile();
            this.removeMouseListener(currentSlide);
            this.removeKeyListener(currentSlide);
        }
    }

    /**
     * goToSlide can go to any slide on command. Can be used for debug purposes but used in break slides to "pop"
     * back to the calibration or practice slides.
     *
     * @param num
     */
    private void goToSlide(int num) {//see similar stuff for nextSlide
        reactionMeasure("toNext");
        writeSlideRecordToFile();
        this.remove(currentSlide);
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
        this.setVisible(true);
        resetTimes();
        oldTime = System.nanoTime();

    }

    /**
     * writeSlideRecordToFile preps the slide information for both human-readable txt file and stat-accessible csv file.
     */
    private void writeSlideRecordToFile() {

        String name = fileName.getName();
        name = name.substring(0, name.length() - 4);
        try {
            bWriter.write(desiredSlides.get(atSlide - 1).toString());
            bWriter.flush();//write one line to log file
            cWriter.write(desiredSlides.get(atSlide - 1).toCSV(name, ord));
            cWriter.flush();//write one line to stats file
        } catch (Exception e) {//if something freaky happens
            System.err.println("ERROR\n" + e);
            e.printStackTrace();
        }

    }

}