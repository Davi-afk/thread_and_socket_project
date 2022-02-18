import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Reads all text from a file then sends to server for further operations.
 *
 * <p>Purdue University -- CS18000 -- Fall 2020 -- Project 5</p>
 *
 * @version December 4, 2020
 * @authors Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B
 */
public class ReadServer extends Thread {
    private BufferedReader bfr;  // Create BufferedReader instance variable to read in the text messages
    private String username;
    private String recipient;
    private JFrame frame;
    private DataInputStream dis;
    private String file;

    // construct a readserver object
    public ReadServer(DataInputStream dis, String username, String recipient, JFrame frame)
            throws IOException, InterruptedException {
        this.dis = dis;
        this.username = username;
        this.recipient = recipient;
        this.frame = frame;
        file = "Project5_Java/src/" + this.username + "_" + this.recipient + ".txt";
    }

    //run method for read server
    @Override
    public void run() {
        try {
            while (true) {
                //Read from the server
                String message = dis.readUTF();
                System.out.println("MESSAGE : " + message);
                // if it quits
                if (message.equals("QUIT")) {
                    SignUpLoginClient.frame.setVisible(false);
                    break;
                }
                SendServer.readConvo(file, recipient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}