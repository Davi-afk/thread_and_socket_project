import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Server for FaceBook for Penguins, handles all request from client.
 *
 * <p>Purdue University -- CS18000 -- Fall 2020 -- Project 5</p>
 *
 * @version December 4, 2020
 * @authors Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B
 */
public class SignUpLoginServer implements Runnable {

    Socket socket; // specific socket for each user
    private ArrayList usernames = new ArrayList<String>(); // list of all the usernames
    private HashMap userData = new HashMap<String, String>(); // list of all the user data keyed to usernames
    private DataInputStream dis; // initializes output stream
    private DataOutputStream dos; // initializes input stream
    private PrintWriter pw;    // Hashmap of usernames mapped with their PrintWriters
    //Hashmap of usernames mapped with their PrintWriters
    private static HashMap<String, DataOutputStream> writers = new HashMap<>();
    //Hashmap of group chats mapped with their usernames
    private static HashMap<String, String> gcNames = new HashMap<>();

    // Constructor for the server when it receives a new socket connection
    public SignUpLoginServer(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());    // Input stream of socket
        this.dos = new DataOutputStream(socket.getOutputStream());  // Output Stream of socket
        gcNames = new HashMap<>();
        pw = new PrintWriter(socket.getOutputStream());     // PrintWriter for the socket
    }

    // creates server socket and allows multiple clients to connect
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);
        while (true) {
            Socket clientSocket = serverSocket.accept();    // Accepts the client connection
            // Starts a new server thread for the client
            SignUpLoginServer server = new SignUpLoginServer(clientSocket);
            new Thread(server).start(); // starts run method
        }

    }


    @Override
    public void run() {
        try {
            usernames = new ArrayList<String>(); //initializes username arraylist
            userData = new HashMap<String, String>(); // initializes userdata hashmap
            // reads userdata from file (may need to change filename when downloaded from github)
            readFromFile("Project5_Java/src/Userdata.txt");
            if (true) {
                String next = dis.readUTF(); // reads the result of the users button click
                if (next.equals("login")) { // if statement for if user selects login
                    boolean run = true;
                    while (run) { // while loop for user login in case username or password was wrong
                        String username = dis.readUTF();
                        String password = dis.readUTF();
                        String user = (String) userData.get(username); // checks if user is in database
                        if (user == null) { // no user
                            dos.writeUTF("false"); // so login failed
                            dis.readUTF();
                            dos.flush();
                        } else if (password.equals(user.substring(0, user.indexOf(",")))) {

                            // checks if password matches usernames password from hashmap database
                            String data = (String) userData.get(username); // splits userdata into its parts
                            String userPassword = data.substring(0, data.indexOf(","));
                            data = data.substring(userPassword.length() + 1);
                            String name = data.substring(0, data.indexOf(","));
                            data = data.substring(name.length() + 1);
                            String age = data.substring(0, data.indexOf(","));
                            data = data.substring(age.length() + 1);
                            String gender = data.substring(0, data.indexOf(","));
                            data = data.substring(gender.length() + 1);
                            String email = data;

                            // tells client that login is confirmed and then sends the data to be displayed in profile
                            dos.writeUTF("true");
                            // reads the group chats from the file
                            readGroupChats("Project5_Java/src/gcNames.txt", username);
                            // add the username and data output stream to the hashmap
                            writers.put(username, this.dos);
                            try {
                                writeProfile(name, username, age, gender, email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            boolean run2 = true;
                            while (run2) { // second while loop for editing/deleting/messaging purposes
                                String editOrDelete = dis.readUTF(); // gets result of users button push
                                // edit button pushed so it sends data to text fields to be edited
                                if (editOrDelete.equals("edit")) {
                                    String change = dis.readUTF();
                                    // when user confirms changes the old username and data is removed from data strucs
                                    if (change.equals("true")) {
                                        userData.remove(username);
                                        for (int i = 0; i < usernames.size(); i++) {
                                            if (usernames.get(i).equals(username)) {
                                                usernames.remove(i);
                                            }
                                        }
                                        // new data from users text fields is sent to server
                                        username = dis.readUTF();
                                        password = dis.readUTF();
                                        name = dis.readUTF();
                                        age = dis.readUTF();
                                        gender = dis.readUTF();
                                        email = dis.readUTF();

                                        // new data is then added to arraylist, hashmap and then printed to file
                                        usernames.add(username);
                                        userData.put(username, password + "," + name + "," + age + "," +
                                                gender + "," + email);
                                        writeUsersToFile("Project5_Java/src/Userdata.txt");
                                        String profile = dis.readUTF();

                                        // data is then sent back to be displayed in profile again
                                        if (profile.equals("profile")) {
                                            try {
                                                writeProfile(name, username, age, gender, email);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        String profile = dis.readUTF();
                                        // data is then sent back to be displayed in profile again
                                        if (profile.equals("profile")) {
                                            try {
                                                writeProfile(name, username, age, gender, email);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (editOrDelete.equals("delete")) {
                                    // if user clicks delete the username is removed from arraylist and hashmap
                                    userData.remove(username);
                                    for (int i = 0; i < usernames.size(); i++) {
                                        if (usernames.get(i).equals(username)) {
                                            usernames.remove(i);
                                        }
                                    }
                                    // the leftover users are then stored back in the file and program starts over
                                    writeUsersToFile("Project5_Java/src/Userdata.txt");
                                    run();
                                } else if (editOrDelete.equals("messages")) {
                                    String messageOrProfile = dis.readUTF();
                                    // System.out.println("MESSAGE OR PROFILE: " + messageOrProfile);
                                    // sends user data to be displayed in profile
                                    if (messageOrProfile.equals("profile")) {
                                        try {
                                            writeProfile(name, username, age, gender, email);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (messageOrProfile.equals("private")) {
                                        // If they enter a private conversation
                                        String output = dis.readUTF();
                                        // System.out.println("OUTPUT: " + output);
                                        while (!(output.equals("Exit"))) {
                                            // if the output is exit, they wish to exit the private convos

                                            // if the output is send,
                                            // then read message again to see the message they sent
                                            if (output.equals("Send") || output.contains(username)) {
                                                String message = dis.readUTF();
                                                while (true) {
                                                    // if the message is send, read message again to see true message
                                                    if (message.equals("Send")) {
                                                        message = dis.readUTF();
                                                    }
                                                    // System.out.println("MESSAGE IS: " + message);
                                                    if (message.contains("Edited/Deleted")) {

                                                        // if the DOS for the client is not null
                                                        if (writers.get(message.split("/")[2]) != null) {

                                                            // write to the client that message has been edited/deleted
                                                            writers.get(message.split("/")[2])
                                                                    .writeUTF("edited/deletedd");

                                                            // read the client's next action
                                                            message = dis.readUTF();

                                                            // continue with while loop and start at the top
                                                            continue;
                                                        }
                                                        // if the dataoutput stream is null,
                                                        // read next message and continue
                                                        message = dis.readUTF();
                                                        continue;
                                                    }
                                                    // if the user wishes to exit out of conversation
                                                    if (message.equals("back") || message == null) {
                                                        // write to the ReadServer to quit
                                                        dos.writeUTF("QUIT");
                                                        dos.flush();
                                                        // break out of while loop
                                                        break;
                                                    }
                                                    // if none of the above "if" statements occurred, then read message
                                                    String[] msgCont = new String[3];
                                                    msgCont = message.split(",");
                                                    System.out.println(Arrays.toString(msgCont));
                                                    if (msgCont[3].equals("true")) {
                                                        msgCont[1] = msgCont[1].replaceAll("#", ",");
                                                    }
                                                    // retrieve current user, the message, and recepient
                                                    String currentUser = msgCont[0];
                                                    String msg = msgCont[1];
                                                    String recepient = msgCont[2];
                                                    msg = currentUser + " says " + msg;

                                                    // if the recepient is active
                                                    if (writers.get(recepient) != null) {
                                                        for (String identifier : writers.keySet()) {
                                                            // write the message to the recepient's ReadServer
                                                            if (identifier.equals(recepient)) {
                                                                //System.out.println(identifier);
                                                                //System.out.println(writers.get(identifier));
                                                                writers.get(identifier).writeUTF(msg);
                                                                writers.get(identifier).flush();
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    // else if the recepient is not active, write to files
                                                    writeConversationToFile("Project5_Java/src/" +
                                                            currentUser +
                                                            "_" + recepient + ".txt", msg);
                                                    writeConversationToFile("Project5_Java/src/" +
                                                            recepient + "_"
                                                            + currentUser + ".txt", msg);

                                                    // Read the next message
                                                    System.out.println(message);
                                                    message = dis.readUTF();
                                                }

                                            }
                                            // If broken out of singular convo,
                                            // read if users wants to exit out of all priv conovs
                                            output = dis.readUTF();
                                        }
                                    } else if (messageOrProfile.equals("group")) {
                                        // If they enter group
                                        String output = dis.readUTF();
                                        while (!(output).equals("Exit")) {
                                            if (output.equals("create")) {
                                                // DO THIS IF THEY WANT TO CREATE A GROUP
                                                String verifiedYN = dis.readUTF();
                                                System.out.println(verifiedYN);
                                                String gcName;
                                                String users;
                                                if (verifiedYN.equals("verified create")) {
                                                    String nameAndUsers = dis.readUTF();
                                                    System.out.println(nameAndUsers);
                                                    if (nameAndUsers == null) {
                                                        continue;
                                                    }
                                                    gcName = nameAndUsers.split(",")[0];
                                                    users = nameAndUsers.split(",")[1];
                                                    // Add the groupchat name and users names to the hashmap
                                                    readGroupChats("Project5_Java/src/gcNames.txt", username);
                                                    gcNames.put(gcName, users);
                                                    writeGroupChatsToFile("Project5_Java/src/gcNames.txt");
                                                }
                                            }
                                            // IF THE ENTER A GROUP CONVO

                                            if (output.equals("Convo Started")) {
                                                String message = dis.readUTF();
                                                while (!output.equals("Exit")) {
                                                    String[] msgCont;
                                                    String currentUser;
                                                    String msg;
                                                    // If the message they receive is send,
                                                    // then read again for true msg
                                                    if (message.equals("Send")) {
                                                        message = dis.readUTF();
                                                    }
                                                    // If the message is edited/deleted then write with the clients DOS
                                                    if (message.contains("Edited/Deleted")) {
                                                        String gc = message.split("/")[2];
                                                        String[] recipients = gcNames.get(gc).split("/");
                                                        for (int i = 0; i < recipients.length; i++) {
                                                            if (writers.get(recipients[i]) != null) {
                                                                try {
                                                                    writers.get(recipients[i])
                                                                            .writeUTF("edited/deleted");
                                                                    writers.get(recipients[i]).flush();
                                                                } catch (Exception e) {
                                                                    continue;
                                                                }

                                                            }
                                                        }
                                                        // Read message again and go back to top of while loop
                                                        message = dis.readUTF();
                                                        continue;
                                                    }
                                                    // If the message is back to message, or show, then read again
                                                    if (message.equals("back to message") ||
                                                            message.contains("show,")) {
                                                        message = dis.readUTF();
                                                        continue;
                                                    }
                                                    // if they wish to exit out of a single group convo
                                                    if (message.equals("back")) {
                                                        dos.writeUTF("QUIT");
                                                        break;
                                                    }
                                                    // if it is a null message
                                                    if (!message.contains(",")) {
                                                        message = dis.readUTF();
                                                        continue;
                                                    }
                                                    // split the input by the comma
                                                    msgCont = message.split(",");

                                                    // if the user's message has a comma,
                                                    // then replace all the # with ,'s
                                                    if (msgCont[3].equals("true")) {
                                                        msgCont[1] = msgCont[1].replaceAll("#", ",");
                                                    }
                                                    currentUser = msgCont[0];
                                                    msg = msgCont[1];
                                                    if (msg.equals("")) {
                                                        message = dis.readUTF();
                                                        continue;
                                                    }
                                                    String recipient = msgCont[2];
                                                    msg = currentUser + " says " + msg;

                                                    // write to all clients in the group chat
                                                    for (String identifier : gcNames.keySet()) {
                                                        if (identifier.equals(recipient)) {
                                                            String[] recipients =
                                                                    gcNames.get(identifier).split("/");
                                                            for (int i = 0; i < recipients.length; i++) {
                                                                if (writers.get(recipients[i]) != null) {
                                                                    try {
                                                                        writers.get(recipients[i]).writeUTF(msg);
                                                                        writers.get(recipients[i]).flush();
                                                                    } catch (Exception e) {
                                                                        System.out.println(msg);
                                                                        System.out.println(recipients[i]);
                                                                        writeConversationToFile(
                                                                                "Project5_Java/src/" +
                                                                                recipients[i] + "_" + identifier
                                                                                        + ".txt", msg);
                                                                        continue;
                                                                    }

                                                                }
                                                                if (recipients[i].equals(currentUser)) {
                                                                    continue;
                                                                }
                                                                writeConversationToFile("Project5_Java/src/" +
                                                                        recipients[i] + "_" + identifier
                                                                        + ".txt", msg);
                                                            }
                                                        }
                                                    }
                                                    // write conversation to the file for current user
                                                    writeConversationToFile("Project5_Java/src/" +
                                                            currentUser +
                                                            "_" + recipient + ".txt", msg);

                                                    // read the user's next input
                                                    System.out.println(message);
                                                    message = dis.readUTF();
                                                }
                                            }
                                            // Check if user wants to exit out of all group convos
                                            output = dis.readUTF();
                                        }
                                    }
                                }
                            }
                        } else { // if user fails to login
                            dos.writeUTF("false");
                            dis.readUTF();
                        }
                    }
                } else if (next.equals("create")) { // when user clicks create button
                    boolean run = true;
                    while (run) { // while loop in case username is taken or fields are empty
                        String username = dis.readUTF(); // reads in text fields from create account
                        if (username.equals("login screen")) {
                            run();
                        }
                        String password = dis.readUTF();
                        String name = dis.readUTF();
                        String age = dis.readUTF();
                        String gender = dis.readUTF();
                        String email = dis.readUTF();
                        readFromFile("Project5_Java/src/Userdata.txt");
                        String user = (String) userData.get(username); // makes sure there isnt already a user
                        if (user == null) { // no user so data is stored in arraylist, hashmap then file
                            usernames.add(username);
                            userData.put(username, password + "," + name + "," + age + "," + gender + "," + email);
                            // may need to change file name
                            writeUsersToFile("Project5_Java/src/Userdata.txt");
                            dos.writeUTF("true"); // lets user know creation was successful
                            String cont = dis.readUTF();
                            if (cont.equals("main")) { // reruns from login screen
                                run = false;
                                run();
                            }
                        } else if (usernames.contains(username)) {
                            dos.writeUTF("false"); // lets user know username is taken
                        } else { // no user so data is stored in arraylist, hashmap then file
                            usernames.add(username);
                            userData.put(username, password + "," + name + "," + age + "," + gender + "," + email);
                            // may need to change file name
                            writeUsersToFile("Project5_Java/src/Userdata.txt");
                            readGroupChats("Project5_Java/src/gcNames.txt", username);
                            dos.writeUTF("true"); // lets user know creation was successful
                            String cont = dis.readUTF();
                            if (cont.equals("main")) { // reruns from login screen
                                run = false;
                                run();
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            try {
                dis.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    // reads user data from file and stores usernames in an arraylist and other data in hashmap
    // also tested manually with files
    public void readFromFile(String filename) throws IOException {
        usernames = new ArrayList();
        userData = new HashMap();
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String lines = bfr.readLine();
        while (lines != null) {
            String username = lines.substring(0, lines.indexOf(","));
            usernames.add(username);
            lines = lines.substring(username.length() + 1);
            String password = lines.substring(0, lines.indexOf(","));
            lines = lines.substring(password.length() + 1);
            String name = lines.substring(0, lines.indexOf(","));
            lines = lines.substring(name.length() + 1);
            String age = lines.substring(0, lines.indexOf(","));
            lines = lines.substring(age.length() + 1);
            String gender = lines.substring(0, lines.indexOf(","));
            lines = lines.substring(gender.length() + 1);
            String email = lines;
            userData.put(username, password + "," + name + "," + age + "," + gender + "," + email);
            lines = bfr.readLine();
        }
    }

    // reads all the group chats currently on the server
    // also tested manually with files
    public static void readGroupChats(String filename, String username) throws IOException {
        gcNames = new HashMap<>();
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String lines = bfr.readLine();
        while (lines != null) {
            if (lines.contains(",")) {
                String[] lineCont = lines.split(",");
                gcNames.put(lineCont[0], lineCont[1]);
            }
            lines = bfr.readLine();
        }
    }

    // writes user data to file by getting usernames from arraylist and using them to access userdata from hashmap
    // also tested manually by checking file being written to
    public void writeUsersToFile(String filename) throws IOException {
        File export = new File(filename);
        if (!export.exists()) {
            export.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(export, false);
        PrintWriter pw = new PrintWriter(fos);
        for (int i = 0; i < usernames.size(); i++) {
            pw.println(usernames.get(i) + "," + (String) userData.get(usernames.get(i)));
            pw.flush();
        }
        pw.close();
        fos.close();
    }

    // writes the groupchats into the file if there was a new chat created
    // also tested manually by checking file being written to
    public void writeGroupChatsToFile(String filename) throws IOException {
        System.out.println(gcNames.toString());
        File export = new File(filename);
        if (!export.exists()) {
            export.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(export, false);
        PrintWriter pw = new PrintWriter(fos);
        for (String key : gcNames.keySet()) {
            pw.println(key + "," + gcNames.get(key));
            pw.flush();
        }
        pw.close();
        fos.close();
    }

    // writes the profile to the clients stream
    // used in run() method
    public void writeProfile(String name, String username, String age, String gender, String email) throws Exception {
        dos.writeUTF("go");
        dos.writeUTF(name);
        dos.writeUTF(username);
        dos.writeUTF(age);
        dos.writeUTF(gender);
        dos.writeUTF(email);
    }

    // writes the individual conversations to a file
    // also tested manually by checking file being written to
    public void writeConversationToFile(String filename, String message) throws IOException {
        File conversation = new File(filename);
        if (!conversation.exists()) {
            conversation.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(conversation, true);
        PrintWriter pw = new PrintWriter(fos);
        pw.println(message);
        pw.flush();
    }
}