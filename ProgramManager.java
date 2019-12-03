package slide;

import java.sql.Time;
import java.util.*;
import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.io.*;

public class ProgramManager extends JFrame {//possible goal is just to move the slides

    private Toolkit t1 = Toolkit.getDefaultToolkit();
    private Image img = t1.getImage("src/slide/images/Fish.png");
    private Image img2 = t1.getImage("src/slide/images/Turtle.png");
    private Image img3 = t1.getImage("src/slide/images/Default.png");
    private Cursor def = Toolkit.getDefaultToolkit().createCustomCursor(img3,new Point(0,0),"Default");
    private Cursor fish = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "Fish");
    private Cursor turtle = Toolkit.getDefaultToolkit().createCustomCursor(img2,new Point(0,0),"Turtle");
    boolean fishActive = false,turtleActive = false,defActive = true;

    private Time keeper;
    private long[] records = new long[7];
    FileWriter fileWriting;

    private Calendar myCal;

    private ArrayList<UpdatedSlide> slideOrder16 = new ArrayList<>();
    private ArrayList<UpdatedSlide> slideOrder13 = new ArrayList<>();
    private ArrayList<UpdatedSlide> desiredSlides;
    private Iterator slideIt;

    private UpdatedSlide currentSlide;

    private int atSlide = 0,backButton=-1;
    private File fileName;
    private String name;
    Container p = null;

    public final static void displayError(){
        JOptionPane.showMessageDialog(null, "Are you sure? Check your click!", "Check", JOptionPane.ERROR_MESSAGE);
    }


    private void makeFile(){
        String bday = "default";
        String ord  = "1-6";
        name = JOptionPane.showInputDialog("Subject ID: ");
        fileName = new File("src/slide/logfiles/" + name + ".txt");
        if (fileName.exists()) {
            if (JOptionPane.showConfirmDialog(null, "This file already exists.\nAre you sure you want to continue this test?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Scanner read = null;
                try{
                    read = new Scanner(new File("src/slide/logfiles/"+name+".txt"));
                }catch(Exception e){
                    System.out.println("File Not Found");
                }
                for(int i = 0; i < 3; i++) {
                    String[] items = read.nextLine().split(" ");
                    if (items[0].equals("Birthday:")) {
                        bday = items[items.length - 1];
                    }
                    if (items[0].equals("Order")) {
                        ord = items[items.length - 1];
                    }
                }
                // yes option
            } else {
                makeFile();//call again to get new subject ID
            }
        }
        if(!fileName.exists()) {
            bday = JOptionPane.showInputDialog("Please enter your birthday like this:\nmonth/day/year");
            while (bday.length() <= 8) {
                bday = JOptionPane.showInputDialog("That is not a valid birthday. \n Please enter your birthday in the month/day/year style.");
            }
            ord = JOptionPane.showInputDialog("Please choose an order: Type '1-3' or '1-6.'");
            while (!ord.equals("1-6") && !ord.equals("1-3")) {
                ord = JOptionPane.showInputDialog("That is not a valid order. Please type '1-3' or '1-6.'");
            }
        }
        BufferedWriter bWriter = null;
        try {
            fileWriting = new FileWriter(fileName, true);
            bWriter = new BufferedWriter(fileWriting);
            bWriter.write("Date and Time: " + myCal.get(Calendar.MONTH) + "/" + myCal.get(Calendar.DAY_OF_MONTH) + " at " + myCal.get(Calendar.HOUR_OF_DAY) + ":" + myCal.get(Calendar.MINUTE) + ":" + myCal.get(Calendar.SECOND) + "\n");
            bWriter.write("Birthday: " + bday+"\n");
            bWriter.write("Order chosen: "+ord+"\n");
            bWriter.close();
        } catch (IOException ioe) {
            System.out.println("ERROR!");
            System.out.println(ioe);
        }
        System.out.println("Made it through initial file writing");
        if(ord.equals("1-3")){
            desiredSlides = slideOrder13;
            System.out.println("Using 1-3");
        }
        else if(ord.equals("1-6")){
            System.out.println("Using 1-6");
            desiredSlides = slideOrder16;
        }
    }

    public ProgramManager() {
        myCal = new GregorianCalendar();
        makeFile();
        slideIt = desiredSlides.iterator();
        makeIntro();
        processFile();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        p = this.getContentPane();
        currentSlide = desiredSlides.get(0);
        currentSlide.addMouseListener(currentSlide);
        currentSlide.addKeyListener(currentSlide);
        currentSlide.setFocusable(true);
        currentSlide.requestFocus();
        p.add(currentSlide);


        this.setSize(1350,800);
        setLocationRelativeTo(null);
        this.setVisible(true);
        setCursor(def);
        atSlide = 1;
        checkNext();
    }


    public void checkNext(){
        while(atSlide <= desiredSlides.size()+1){
            currentSlide.requestFocus();
            //System.out.println("in loop");
            try {
                Thread.sleep(5);
            } catch(Exception e){
                System.out.println("No sleep");
            }
            if(currentSlide.checkRedo()){
                this.remove(currentSlide);
                desiredSlides.set(atSlide-1,currentSlide.reset());
                currentSlide = desiredSlides.get(atSlide-1);
                this.add(currentSlide);
                setCursor(def);
                fishActive = false;
                turtleActive = false;
                defActive = true;
                this.repaint();
                this.setVisible(true);
            }
            if(atSlide == 3 && !currentSlide.isClicked()) {
                System.out.println("Checking calibrations");
                calibrationMethod();
            }
            if(desiredSlides.get(atSlide-1).isClicked()){
                System.out.println("Moving!");
                nextSlide();
                p.repaint();
            }
            if(desiredSlides.get(atSlide-1).checkCurs1()){
                fishActive = true;
                turtleActive = false;
                defActive = false;
                setCursor(fish);
                desiredSlides.get(atSlide-1).unClickCurs1();

            }
            if(desiredSlides.get(atSlide-1).checkCurs2()){
                fishActive = false;
                turtleActive = true;
                defActive = false;
                setCursor(turtle);
                desiredSlides.get(atSlide-1).unClickCurs2();
            }
            if(desiredSlides.get(atSlide-1).checkRedoCalib()){
                popToCalibration();
            }
            if(desiredSlides.get(atSlide-1).checkRedoPractice()){
                popToPractice();
            }

        }
    }

    public void popToCalibration(){
        backButton = atSlide;
        desiredSlides.set(backButton-1,desiredSlides.get(backButton-1).reset());
        goToSlide(3);
    }
    public void popToPractice(){
        backButton = atSlide;
        desiredSlides.set(backButton-1,desiredSlides.get(backButton-1).reset());
        goToSlide(4);
    }

    public void calibrationMethod(){

        int i = 0;
        while(i < 17){
            System.out.print("");
            if(currentSlide.getButtons().get(i).getClicks() >= 2) {
                i++;
                currentSlide.getButtons().get(i).setVisible(true);
                currentSlide.repaint();
                this.repaint();
            }

        }
    }

    public void makeIntro(){
        slideOrder16.add(new UpdatedSlide("intro1"));
        slideOrder16.add(new UpdatedSlide("intro2"));
        slideOrder16.add(new UpdatedSlide("intro3"));

        slideOrder13.add(new UpdatedSlide("intro1"));
        slideOrder13.add(new UpdatedSlide("intro2"));
        slideOrder13.add(new UpdatedSlide("intro3"));

    }
    public void processFile(){
        Scanner fin = null;
        try{
            fin = new Scanner(new File("src/slide/input.txt"));
        }catch(Exception e){
            System.err.println("File not Found");
        }
        int i = 0;
        int j = 0;
        int k = 0;
        while(fin.hasNextLine()){
            String items[] = fin.nextLine().split(",");

            if(items[0].equals("1-6")){
                i++;
                slideOrder16.add(new UpdatedSlide(items[1],items[2],items[3],items[4],items[5],items[6],items[7],items[8],items[9]));
                if(i == 20) {
                    slideOrder16.add(new UpdatedSlide("break"));
                    slideOrder16.add(new UpdatedSlide("ready"));
                    i = 0;
                }
            } else if (items[0].equals("1-3")) {
                j++;
                slideOrder13.add(new UpdatedSlide(items[1],items[2],items[3],items[4],items[5],items[6],items[7],items[8],items[9]));
                if(j==20) {
                    slideOrder13.add(new UpdatedSlide("break"));
                    slideOrder13.add(new UpdatedSlide("ready"));
                    j = 0;
                }
            }
            else if(items[0].equals("Intro")){
                k++;
                slideOrder16.add(new UpdatedSlide(items[1],items[2],items[3],items[4],items[5]));
                slideOrder13.add(new UpdatedSlide(items[1],items[2],items[3],items[4],items[5]));
                if(k==3){
                    slideOrder16.add(new UpdatedSlide("ready"));
                    slideOrder13.add(new UpdatedSlide("ready"));
                    k = 0;
                }
            }
        }
        slideOrder13.add(new UpdatedSlide("end"));
        slideOrder16.add(new UpdatedSlide("end"));

    }
    public void nextSlide() {
        if (atSlide != desiredSlides.size()){
            System.out.println("Moving Slide");
           // writeSlideRecordToFile();
            this.remove(currentSlide);
            currentSlide.unClickNext();
            if(backButton != -1 && currentSlide.getType().equals("intro3")){
                desiredSlides.set(backButton-1,desiredSlides.get(backButton-1).reset());
                currentSlide = desiredSlides.get(backButton-1); //FIX NO LONGER PART OF ARRAYLIST YOU DUM DUM
                atSlide = backButton;
                backButton = -1;

            }
            else if(backButton != -1 && currentSlide.getType().equals("practice")){
                if(atSlide == 6){
                    desiredSlides.set(backButton-1,desiredSlides.get(backButton-1).reset());
                    currentSlide = desiredSlides.get(backButton-1);
                    atSlide = backButton;//FIX NO LONGER PART OF ARRAYLIST YOU DUM DUM
                    backButton = -1;
                }
                else{
                    desiredSlides.set(atSlide-1,desiredSlides.get(atSlide-1).reset());
                    currentSlide = desiredSlides.get(atSlide); //FIX NO LONGER PART OF ARRAYLIST YOU DUM DUM
                    atSlide++;
                }
            }
            else {
                desiredSlides.set(atSlide-1,desiredSlides.get(atSlide-1).reset());
                currentSlide = desiredSlides.get(atSlide);
                atSlide++;
            }
            this.add(currentSlide);
            currentSlide.addMouseListener(currentSlide);
            currentSlide.addKeyListener(currentSlide);
            currentSlide.setFocusable(true);
            currentSlide.requestFocus();
            this.setSize(1350,800);
            this.repaint();
            this.setVisible(true);
            setCursor(def);
            fishActive = false;
            turtleActive = false;
            defActive = true;
            System.out.println(atSlide);
        } else {
        //    writeSlideRecordToFile();
            this.removeMouseListener(currentSlide);
            this.removeKeyListener(currentSlide);
        }
    }
    private void goToSlide(int num){
    //    writeSlideRecordToFile();
        this.remove(currentSlide);

        currentSlide.unClickNext();
        desiredSlides.set(num-1,desiredSlides.get(num-1).reset());
        currentSlide = desiredSlides.get(num-1);
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
        this.setSize(1350,800);
        this.setVisible(true);
        System.out.println(atSlide);

    }

}
