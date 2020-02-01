package slide;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {

    public static void main(String[] args) throws IOException, FontFormatException {
        ProgramManager start = new ProgramManager(0);//USE 0 FOR MAIN MONITOR, 1 FOR SECOND MONITOR
        start.setVisible(true);
    }
}


