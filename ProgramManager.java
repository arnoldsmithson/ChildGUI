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

    private Time keeper;
    private long[] records = new long[7];
    FileWriter fileWriting;

    private Calendar myCal;

    private ArrayList<UpdatedSlide> slideOrder16 = new ArrayList<>();
    private ArrayList<UpdatedSlide> slideOrder13 = new ArrayList<>();
    private Iterator slideIt = slideOrder16.iterator();

    private UpdatedSlide currentSlide;

    private int atSlide = 0,backButton=-1;
    private File fileName;
    private String name;
    Container p = null;

    public final static void displayError(){
        JOptionPane.showMessageDialog(null, "Are you sure? Check your click!", "Check", JOptionPane.ERROR_MESSAGE);
    }

    public ProgramManager() {
        myCal = new GregorianCalendar();
        name = JOptionPane.showInputDialog("Subject ID: ");
        fileName = new File("src/slide/logfiles/" + name + ".txt");
        String bday = JOptionPane.showInputDialog("Please enter your birthday like this:\nday/month/year");
        String ord = JOptionPane.showInputDialog("Please choose an order: Type '1-3' or '1-6.'");
        BufferedWriter bWriter = null;
        try {
            fileWriting = new FileWriter(fileName, true);
            bWriter = new BufferedWriter(fileWriting);
            bWriter.write("Participant ID: " + name + "\n");
            bWriter.write("Date and Time: " + myCal.get(Calendar.MONTH) + "/" + myCal.get(Calendar.DAY_OF_MONTH) + " at " + myCal.get(Calendar.HOUR_OF_DAY) + ":" + myCal.get(Calendar.MINUTE) + ":" + myCal.get(Calendar.SECOND) + "\n");
            bWriter.write("Birthday: " + bday+"\n");
            bWriter.write("Order chosen: "+ord+"\n");
            bWriter.close();
        } catch (IOException ioe) {
            System.out.println("ERROR!");
            System.out.println(ioe);
        }
        System.out.println("Made it through initial file writing");
        makeIntro();
        processFile();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        p = this.getContentPane();
        currentSlide = slideOrder16.get(0);
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
        while(atSlide <= slideOrder16.size()+1){
            //System.out.println("in loop");
            try {
                Thread.sleep(5);
            } catch(Exception e){
                System.out.println("No sleep");
            }
            if(currentSlide.checkRedo()){
                slideOrder16.set(atSlide-1,currentSlide.reset());
            }
            if(atSlide == 3 && !currentSlide.isClicked()) {
                System.out.println("Checking calibrations");
                calibrationMethod();
            }
            if(slideOrder16.get(atSlide-1).isClicked()){
                System.out.println("Moving!");
                nextSlide();
                p.repaint();
            }
            if(slideOrder16.get(atSlide-1).checkCurs1()){
                setCursor(fish);
                slideOrder16.get(atSlide-1).unClickCurs1();

            }
            if(slideOrder16.get(atSlide-1).checkCurs2()){
                setCursor(turtle);
                slideOrder16.get(atSlide-1).unClickCurs2();
            }
            if(slideOrder16.get(atSlide-1).checkRedoCalib()){
                popToCalibration();
            }
            if(slideOrder16.get(atSlide-1).checkRedoPractice()){
                popToPractice();
            }

        }
    }

    public void popToCalibration(){
        backButton = atSlide;
        goToSlide(3);
    }
    public void popToPractice(){
        backButton = atSlide;
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
        if (atSlide != slideOrder16.size()){
            System.out.println("Moving Slide");
            //writeSlideRecordToFile();
            this.remove(currentSlide);
            currentSlide.unClickNext();
            if(backButton != -1 && currentSlide.getType().equals("intro3")){
                currentSlide = slideOrder16.get(backButton-1).reset();
                backButton = -1;
            }
            else if(backButton != -1 && currentSlide.getType().equals("practice")){
                if(atSlide == 6){
                    currentSlide = slideOrder16.get(backButton-1).reset();
                    backButton = -1;
                }
                else{
                    currentSlide = slideOrder16.get(atSlide).reset();
                }
            }
            else
                currentSlide = slideOrder16.get(atSlide);
            this.add(currentSlide);
            UpdatedSlide newSlide = slideOrder16.get(atSlide-1).reset();
            slideOrder16.set(atSlide-1,newSlide);
            currentSlide.addMouseListener(currentSlide);
            currentSlide.addKeyListener(currentSlide);
            currentSlide.setFocusable(true);
            currentSlide.requestFocus();
            this.setSize(1350,800);
            this.repaint();
            this.setVisible(true);
            setCursor(def);
            atSlide++;
            System.out.println(atSlide);
        } else {
            //writeSlideRecordToFile();
          //  this.removeMouseMotionListener(currentSlide);
            //writeRecords();
        }
    }
    private void goToSlide(int num){
       // writeSlideRecordToFile();
        this.remove(currentSlide);
        currentSlide.unClickNext();
        currentSlide = slideOrder16.get(num-1);
        atSlide = num;
        this.add(currentSlide.reset());
        currentSlide.addMouseListener(currentSlide);
        currentSlide.addKeyListener(currentSlide);
        currentSlide.setFocusable(true);
        currentSlide.requestFocus();
        setCursor(def);
        this.repaint();
        this.setSize(1350,800);
        this.setVisible(true);
        System.out.println(atSlide);

    }

}
