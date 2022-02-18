import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Main Client for FaceBook for Penguins.
 *
 * <p>Purdue University -- CS18000 -- Fall 2020 -- Project 5</p>
 *
 * @version December 4, 2020
 * @authors Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B
 */
public class SignUpLoginClient {
    // buttons for login screen, logging in, editing account, confirming changes, deleting account and viewing profile
    static JButton loginButton;
    static JButton createButton;
    static JButton createVerifyButton;
    static JButton editButton;
    static JButton editVerifyButton;
    static JButton deleteButton;
    static JButton messageListButton;
    static JButton newMessageButton;
    static JButton profileButton;
    static JButton addParticipantButton;
    static String recipientName;
    static JButton createGroupButton;
    static JTextField typeMessage;
    static Socket socket; // socket for each user
    static JFrame frame; // gui display
    static JTextField username; // text fields to enter userdata
    static JTextField password;
    static JTextField name;
    static JTextField age;
    static JTextField gender;
    static JTextField email;
    static JTextField participant;
    static JButton b;
    static ArrayList<JButton> privButtons = new ArrayList<JButton>();
    static ArrayList<JButton> groupButtons = new ArrayList<>();
    static BufferedReader bfr;
    static PrintWriter pw;
    static JLabel userNameText;
    static JLabel passwordText;
    static JButton cancelSignUp;
    static JButton cancelEditButton;
    static JButton privateMsg;
    static JButton groupMsg;
    static JButton createGroupVerify;
    static JTextField addUsers;
    static JButton backToGroupButton;
    static JButton backToPrivateButton;
    static JButton backToLoginButton;
    static JTextField gcName;
    static JPanel chatNames = new JPanel();
    static JButton exitGroups;
    static JButton sendMessage;
    static boolean priv = false;
    static boolean createdGroup = false;
    static String msg;
    static JButton exitPrivate;
    static HashMap<String, String> gcNames;
    static ArrayList<String> usernames = new ArrayList<>();
    static String groupChat;
    static String userId;
    static String userName;
    static String userAge;
    static String userGender;
    static String userEmail;
    static JButton showUsers;
    static JButton backToMessageScreen;
    static JLabel welcomeText;
    static JLabel userInfoText;
    static JLabel nameTextProfile;
    static JLabel ageIntProfile;
    static JLabel genderTextProfile;
    static JLabel emailTextProfile;
    static JLabel bottomDashes;
    static JLabel updateMessage;
    static JLabel devNames;
    static JLabel picture;
    static JLabel gifForLoginScreen;
    static JLabel titleForLoginScreen;
    static JLabel pictureForMessageScreen;
    static JLabel messagePicture;
    static JButton deleteConvo;
    static JButton deleteGroupConvo;
    static JButton deletePrivateConvo;
    static JButton createPrivateConvo;
    static JPanel privChats = new JPanel();
    static HashMap<String, String> privNames = new HashMap<>();
    static JButton refresh;


    public SignUpLoginClient(Socket socket) throws IOException { // initializes socket and creates/displays main menu
        this.socket = socket;
        frame = new JFrame("Facebook for Penguins");
        loginButton = new JButton("Login");
        loginButton.addActionListener(actionListener);
        createButton = new JButton("Create Account");
        createButton.addActionListener(actionListener);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        gcNames = new HashMap<>();

        username = new JTextField(10);
        password = new JTextField(10);

        userNameText = new JLabel("Username:");
        passwordText = new JLabel("Password:");
        titleForLoginScreen = new JLabel("Welcome to FaceBook for Penguins");

        titleForLoginScreen.setBounds(190, -60, 1000, 200);
        panel.add(titleForLoginScreen);

        username.setBounds(150, 80, 165, 25);
        panel.add(username);

        password.setBounds(150, 160, 165, 25);
        panel.add(password);

        passwordText.setBounds(75, 160, 80, 25);
        panel.add(passwordText);

        userNameText.setBounds(75, 80, 80, 25);
        panel.add(userNameText);

        loginButton.setBounds(50, 210, 80, 25);
        panel.add(loginButton);

        createButton.setBounds(150, 210, 165, 25);
        panel.add(createButton);


        devNames = new JLabel(("Developed by: Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B"));

        devNames.setBounds(0, 250, 1000, 200);
        panel.add(devNames);


        ImageIcon Penguin = new ImageIcon("Project5_Java/src/tenor.gif");
        Image image = Penguin.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        Penguin = new ImageIcon(scaledImage);
        gifForLoginScreen = new JLabel(Penguin);
        gifForLoginScreen.setBounds(300, 80, 280, 170);
        panel.add(gifForLoginScreen);


        frame.add(panel);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream());
    }

    public static void main(String[] args) throws Exception { // establish connection with server
        Socket socket = new Socket("localhost", 4242);
        new SignUpLoginClient(socket);
    }

    static ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) { // used for when any button is clicked - tested exhaustively
            try {
                // for sending/receiving data to/from server
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                if (e.getSource() == createButton) {
                    // if create account button is pressed allow user to enter all the necessary user data
                    createButton(dos);
                }
                if (e.getSource() == cancelSignUp) {
                    frame.setVisible(false);
                    dos.writeUTF("menu");
                    new SignUpLoginClient(socket);
                }
                // when login button from login screen is pressed sends username and password to server
                if (e.getSource() == loginButton) {
                    loginButton(dis, dos);
                }
                // makes sure data for creating the account is within parameters then is sent to server
                if (e.getSource() == createVerifyButton) {
                    createVerify(dis, dos);
                }
                // if user clicks edit profile button all userdata appears in the text fields to be edited
                if (e.getSource() == editButton) {
                    editAct(dis, dos);
                }
                if (e.getSource() == cancelEditButton) {
                    cancelEdit(dis, dos);
                }
                // makes sure new data for the account is within parameters then is sent to server
                if (e.getSource() == editVerifyButton) {
                    editVerify(dis, dos);
                }
                // if delete button is pressed message asking to confirm appears
                if (e.getSource() == deleteButton) {
                    deleteAct(dis, dos);
                }
                // supposed to display message list (to be implemented)
                if (e.getSource() == messageListButton) {
                    messageList(dis, dos);
                }
                // If the user clicks the button for private messages
                if (e.getSource() == privateMsg) {
                    priv = true;
                    try {
                        privateMsgConvo(dis, dos);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                //Group message button
                // If the user clicks the button for the group messages
                if (e.getSource() == groupMsg) {
                    try {
                        groupMsgConvo(dis, dos);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                if (e.getSource() == refresh) {
                    if (priv) {
                        privateMsgConvo(dis, dos);
                    } else {
                        groupMsgConvo(dis, dos);
                    }
                }

                if (e.getSource() == createGroupButton) { // If the user clicks the button to create a new group
                    try {
                        createGroup(dis, dos, createdGroup);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                if (e.getSource() == createGroupVerify) { // If the user clicks the button to verify the created group
                    String file = "Project5_Java/src/Userdata.txt";
                    readUsernames(file);
                    int count = 1;
                    int previousCount = 0;
                    String anomaly = "";
                    String[] idvUsers = addUsers.getText().split("/");
                    for (int i = 0; i < idvUsers.length; i++) {
                        createdGroup = false;
                        previousCount = count;
                        for (int j = 0; j < usernames.size(); j++) {
                            if (usernames.get(j).equals(idvUsers[i])) {
                                if (!usernames.get(j).equals(userName)) {
                                    createdGroup = true;
                                    count++;
                                }
                                break;
                            }
                        }
                        if (count == previousCount) {
                            anomaly = idvUsers[i];
                        }
                    }
                    if (count == idvUsers.length) {
                        JOptionPane.showMessageDialog(null, "Group Succesfully Created! Press "
                                        + "Back to return!",
                                "Valid Usernames", JOptionPane.PLAIN_MESSAGE);
                        dos.writeUTF("verified create");
                        b = new JButton(gcName.getText()); // Add a new button with what user inputted
                        b.addActionListener(actionListener);
                        groupButtons.add(b);
                        chatNames.add(b);
                        gcNames.put(gcName.getText(), addUsers.getText());
                        dos.writeUTF(gcName.getText() + "," + addUsers.getText());
                    } else {
                        JOptionPane.showMessageDialog(null, "User: " + anomaly + " is " +
                                        "invalid, Try Again!",
                                "Create New group", JOptionPane.PLAIN_MESSAGE);
                    }

                }
                // If the user clicks the back button to go back to the group convos
                if (e.getSource() == backToGroupButton) {
                    groupButtons = new ArrayList<>();
                    backToGroup(dis, dos);
                }
                if (e.getSource() == backToPrivateButton) {
                    try {
                        backToPrivate(dis, dos);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                if (e.getSource() == exitGroups) { // If the user clicks the exit groups button
                    dos.writeUTF("Exit");
                    chatNames = new JPanel();
                    groupButtons = new ArrayList<>();
                    messageList(dis, dos);
                }
                if (e.getSource() == exitPrivate) {
                    dos.writeUTF("Exit");
                    privButtons = new ArrayList<>();
                    usernames = new ArrayList<>();
                    messageList(dis, dos);
                }
                if (e.getSource() == showUsers) {
                    dos.writeUTF("show," + groupChat);
                    String list = gcNames.get(groupChat);
                    showUsers(dis, list);
                }
                if (e.getSource() == backToLoginButton) {
                    frame.setVisible(false);
                    dos.writeUTF("login screen");
                    new SignUpLoginClient(socket);
                }
                if (e.getSource() == backToMessageScreen) {
                    dos.writeUTF("back to message");
                    SendServer.readConvo("Project5_Java/src/" + userName + "_" + groupChat + ".txt", groupChat);
                }
                // lets server know to send userdata for profile then displays profile
                if (e.getSource() == profileButton) {
                    dos.writeUTF("profile");
                    profile(dis, dos);
                    displayProfile();
                }
                if (e.getSource() == deleteGroupConvo) {
                    ArrayList<String> options = new ArrayList<>();
                    for (String s : gcNames.keySet()) {
                        options.add(s);
                    }
                    String response = "";
                    if (options.size() != 0) {
                        response = (String) JOptionPane.showInputDialog(null,
                                "What conversation would you like to delete?", "Delete",
                                JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));
                    } else {
                        JOptionPane.showMessageDialog(null, "No Convos to Delete!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    String file = "Project5_Java/src/gcNames.txt";
                    if (response != null) {
                        updateGroupConvos(file, response);
                        clearMessages(userName, response);
                        groupMsgConvo(dis, dos);
                    }
                }
                if (e.getSource() == deletePrivateConvo) {
                    String options = privNames.get(userName);
                    if (options != null) {
                        String[] convos = new String[1];
                        if (options.contains(",")) {
                            convos = privNames.get(userName).split(",");
                        } else {
                            convos[0] = options;
                        }
                        String response = (String) JOptionPane.showInputDialog(null,
                                "What conversation would you like to delete?", "Delete",
                                JOptionPane.QUESTION_MESSAGE, null, convos, convos[0]);
                        if (response != null) {
                            updatePrivateConvos(response);
                            clearMessages(userName, response);
                            privateMsgConvo(dis, dos);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No Convos to Delete!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
                if (e.getSource() == createPrivateConvo) {
                    ArrayList<String> options = new ArrayList<>();
                    boolean exists = false;
                    for (String s : usernames) {
                        if (s.equals(userName)) {
                            continue;
                        }
                        if (!options.contains(s)) {
                            options.add(s);
                        }
                    }
                    String file = "Project5_Java/src/privChats.txt";
                    //readPrivChat(file, userName);
                    if (options.size() != 0) {
                        String response = (String) JOptionPane.showInputDialog(null,
                                "Choose User: ", "Create", JOptionPane.INFORMATION_MESSAGE,
                                null, options.toArray(), options.get(0));
                        if (privNames.get(userName) != null) {
                            if (privNames.get(userName).contains(",")) {
                                String[] users = privNames.get(userName).split(",");
                                for (int i = 0; i < users.length; i++) {
                                    if (users[i].equals(response)) {
                                        JOptionPane.showMessageDialog(null, "Chat already Exists!",
                                                "Sorry!", JOptionPane.PLAIN_MESSAGE);
                                        exists = true;
                                    }
                                }
                            } else if (privNames.get(userName).equals(response)) {
                                JOptionPane.showMessageDialog(null, "Chat already Exists!",
                                        "Sorry!", JOptionPane.PLAIN_MESSAGE);
                                exists = true;
                            }
                        }
                        if (!exists) {
                            if (response != null) {
                                readPrivChat("Project5_Java/src/privChats.txt");
                                System.out.println(privNames.toString());
                                if (privNames.get(userName) != null) {
                                    if (!(privNames.get(userName).contains(userName))) {
                                        privNames.replace(userName, privNames.get(userName) + "," + response);
                                        System.out.println("HERE " + privNames.toString());
                                    }
                                } else {
                                    privNames.put(userName, response);
                                    System.out.println("NEW  " + privNames.toString());
                                }
                                if (privNames.get(response) != null) {
                                    if (!(privNames.get(response).contains(userName))) {
                                        privNames.replace(response, privNames.get(response) + "," + userName);
                                        System.out.println("HERE #2" + privNames.toString());
                                    }
                                } else {
                                    privNames.put(response, userName);
                                    System.out.println("NEW  #2" + privNames.toString());
                                }
                                System.out.println(privNames.toString());
                                writePrivChats("Project5_Java/src/privChats.txt", privNames);
                                privateMsgConvo(dis, dos);
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "No Convos to Create!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }


                if (priv) { // This code  will be executed if private messages are being accessed.
                    for (int i = 0; i < privButtons.size(); i++) {
                        if (e.getActionCommand().toString().equals(privButtons.get(i).getText().toString())) {
                            recipientName = privButtons.get(i).getText().toString();
                            //System.out.println(users.toString());
                            //System.out.println(users.get(recepientName));
                            //displayMessageScreen();
                            //startThreads(dis,dos,recipientName,priv);
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" + recipientName + ".txt",
                                    recipientName);
                            startThreads(dis, dos, recipientName, priv);
                        }
                    }
                }
                if (priv == false) {    // This code  will be executed if group chats are being accessed.
                    for (int i = 0; i < groupButtons.size(); i++) {
                        if (e.getActionCommand().toString().equals(groupButtons.get(i).getText().toString())) {
                            dos.writeUTF("Convo Started");
                            groupChat = groupButtons.get(i).getText().toString();
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" + groupChat + ".txt",
                                    groupChat);
                            startThreads(dis, dos, groupChat, priv);
                            break;
                        }
                    }
                }
                if (e.getSource() == sendMessage) {
                    frame.setVisible(false);
                    dos.writeUTF("Send");
                    dos.flush();
                    msg = typeMessage.getText();
                    typeMessage.setText("");
                    System.out.println("MSG:" + msg);
                    if (priv) {
                        if (msg.contains(",")) {
                            msg = msg.replaceAll(",", "#");
                            dos.writeUTF(userName + "," + msg + "," + recipientName + "," + "true");
                            dos.flush();
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" +
                                    recipientName + ".txt", recipientName);
                        } else {
                            msg = msg.replaceAll(",", "#");
                            dos.writeUTF(userName + "," + msg + "," + recipientName + "," + "false");
                            dos.flush();
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" +
                                    recipientName + ".txt", recipientName);
                        }

                    } else {
                        if (msg.contains(",")) {
                            msg = msg.replaceAll(",", "#");
                            dos.writeUTF(userName + "," + msg + "," + groupChat + "," + "true");
                            dos.flush();
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" +
                                    groupChat + ".txt", groupChat);
                        } else {
                            msg = msg.replaceAll(",", "#");
                            dos.writeUTF(userName + "," + msg + "," + groupChat + "," + "false");
                            dos.flush();
                            SendServer.readConvo("Project5_Java/src/" + userName + "_" +
                                    groupChat + ".txt", groupChat);
                        }
                    }

                }

            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }

    };

    //deletes a groupchat which is given in the parameters
    //also tested manually using files
    private static void updateGroupConvos(String file, String gcToDelete) throws Exception {
        ArrayList<String> lines = new ArrayList<>();
        FileReader fr = new FileReader(file);
        BufferedReader bfr = new BufferedReader(fr);
        String line = bfr.readLine();
        String gcName = "";
        boolean found = false;
        while (line != null) {
            if (line.contains(",")) {
                gcName = line.split(",")[0];
            }
            if (gcName.equals(gcToDelete)) {
                if (line.contains("/")) {
                    String[] idvUsers = line.substring(line.indexOf(",") + 1).split("/");
                    for (String s : idvUsers) {
                        if (s.equals(userName)) {
                            found = true;
                        }
                    }
                } else {
                    if (line.split(",")[1].equals(gcName)) {
                        found = true;
                    }
                }
                if (found) {
                    if (line.contains("/")) {
                        line = line.replaceAll(userName, " ");
                        if (line.contains("//")) {
                            line = line.replace("/ /", "/");
                            line = line.trim();
                        } else if (line.contains(" /")) {
                            line = line.replaceAll(" /", "");
                            line = line.trim();
                        } else if (line.contains("/ ")) {
                            line = line.replaceAll("/ ", "");
                            line = line.trim();
                        }
                    } else {
                        line = bfr.readLine();
                        continue;
                        //line = line.replaceAll(userName," ");
                    }

                }
                lines.add(line);
            } else {
                lines.add(line);
            }
            line = bfr.readLine();
        }
        bfr.close();
        fr.close();
        FileOutputStream fs = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fs);
        for (String s : lines) {
            pw.println(s);
            pw.flush();
        }
        pw.close();
        fs.close();
    }

    //this method shows all the users in a group convo
    //tested manually using GUI
    public static void showUsers(DataInputStream dis, String list) throws IOException {
        frame.setVisible(false);
        frame = new JFrame("Users in Conversation");
        String input = list;
        System.out.println(list);
        String[] users = input.split("/");
        JList j = new JList(users);
        JPanel panel = new JPanel();
        JPanel buttonPanel = new JPanel();
        panel.add(j);
        if (priv) {
            backToPrivateButton = new JButton("Back to Private convos");
            backToPrivateButton.addActionListener(actionListener);
            buttonPanel.add(backToPrivateButton);
        } else {
            backToMessageScreen = new JButton("Back to Conversation");
            backToMessageScreen.addActionListener(actionListener);
            buttonPanel.add(backToMessageScreen);
        }
        Container content = frame.getContentPane();
        content.add(panel);
        content.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    //this method runs the threads for the ReadServer and SendServer Classes
    //tested in conjunction with action listeners through GUI and also monitored through print statements in console
    //prior to final version
    public static void startThreads(DataInputStream dis, DataOutputStream dos, String recipientName, boolean priv)
            throws IOException, InterruptedException {
        // create a new read server thread
        ReadServer rs = new ReadServer(dis, username.getText(), recipientName, frame);
        SendServer ss = new SendServer(dos, username.getText(), recipientName, msg); // create a new send server thread
        frame.setVisible(false);
        rs.start(); // start the threads to allow for client to read from the server
        ss.start(); // start the thread to allow for client to send to the server.
    }

    // gets userdata from server
    // tested using GUI
    public static void profile(DataInputStream dis, DataOutputStream dos) throws IOException {
        frame.setVisible(false);
        if (dis.readUTF().equals("go")) {
            userId = dis.readUTF();
            userName = dis.readUTF();
            userAge = dis.readUTF();
            userGender = dis.readUTF();
            userEmail = dis.readUTF();
        }
        displayProfile();

    }

    // displays user data to the user
    // tested using GUI
    public static void displayProfile() {
        frame.setVisible(false);
        frame = new JFrame("Facebook for Penguins");
        Container content = frame.getContentPane();
        JPanel profilePanel = new JPanel();
        // profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setLayout(null);

        welcomeText = new JLabel("Welcome Back: " + userName);
        userInfoText = new JLabel("----User Information----");
        nameTextProfile = new JLabel("Name: " + userId);
        ageIntProfile = new JLabel(("Age: " + userAge));
        genderTextProfile = new JLabel(("Gender: " + userGender));
        emailTextProfile = new JLabel(("Email: " + userEmail));
        bottomDashes = new JLabel(("--------------------------------"));
        updateMessage = new JLabel(("Updates : Welcome to Facebook for Penguins"));
        devNames = new JLabel(("Developed by: Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B"));


        welcomeText.setBounds(240, 0, 180, 30);
        profilePanel.add(welcomeText);

        updateMessage.setBounds(170, 20, 1000, 30);
        profilePanel.add(updateMessage);

        userInfoText.setBounds(232, 40, 180, 30);
        profilePanel.add(userInfoText);

        nameTextProfile.setBounds(50, 120, 180, 30);
        profilePanel.add(nameTextProfile);

        ageIntProfile.setBounds(50, 150, 180, 30);
        profilePanel.add(ageIntProfile);

        genderTextProfile.setBounds(50, 180, 180, 30);
        profilePanel.add(genderTextProfile);

        emailTextProfile.setBounds(50, 210, 180, 30);
        profilePanel.add(emailTextProfile);


        bottomDashes.setBounds(10, 140, 180, 30);
        profilePanel.add(bottomDashes);


        bottomDashes.setBounds(240, 270, 180, 30);
        profilePanel.add(bottomDashes);


        devNames.setBounds(97, 295, 1000, 30);
        profilePanel.add(devNames);

        bottomDashes.setBounds(225, 305, 180, 30);
        profilePanel.add(bottomDashes);

        //picture

        ImageIcon Penguin = new ImageIcon("Project5_Java/src/penguinImage.png");
        Image image = Penguin.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
        Penguin = new ImageIcon(scaledImage);
        picture = new JLabel(Penguin);

        picture.setBounds(300, 80, 200, 200);
        profilePanel.add(picture);


        editButton = new JButton("Edit Account");
        editButton.addActionListener(actionListener);
        deleteButton = new JButton("Delete Account");
        deleteButton.addActionListener(actionListener);
        messageListButton = new JButton("Message List");
        messageListButton.addActionListener(actionListener);
        JPanel profilePanelTwo = new JPanel();
        profilePanelTwo.add(editButton);
        profilePanelTwo.add(deleteButton);
        profilePanelTwo.add(messageListButton);
        content.add(profilePanel, BorderLayout.CENTER);
        content.add(profilePanelTwo, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //Shows the screen to create a new account
    //tested using GUI
    public static void createButton(DataOutputStream dos) throws IOException {
        frame.setVisible(false);
        dos.writeUTF("create");
        frame = new JFrame("Facebook for Penguins");
        Container content = frame.getContentPane();
        username = new JTextField(10);
        password = new JTextField(10);
        JPanel createPanel = new JPanel();
        createPanel.setLayout(new BoxLayout(createPanel, BoxLayout.Y_AXIS));
        JPanel createPanelTwo = new JPanel();
        createPanelTwo.setLayout(new BoxLayout(createPanelTwo, BoxLayout.Y_AXIS));
        JPanel createPanelThree = new JPanel();
        //createPanelThree.setLayout(new BoxLayout(createPanelThree, BoxLayout.Y_AXIS));
        // do not need what both buttons centered (dhanpreet).
        createPanel.add(new JLabel("Username:"));
        createPanel.add(username);
        createPanel.add(new JLabel("Password:"));
        createPanel.add(password);
        content.add(createPanel, BorderLayout.NORTH);
        name = new JTextField(10);
        createPanelTwo.add(new JLabel("Name:"));
        createPanelTwo.add(name);
        age = new JTextField(10);
        createPanelTwo.add(new JLabel("Age:"));
        createPanelTwo.add(age);
        gender = new JTextField(10);
        createPanelTwo.add(new JLabel("Gender:"));
        createPanelTwo.add(gender);
        email = new JTextField(10);
        createPanelTwo.add(new JLabel("Email:"));
        createPanelTwo.add(email);
        // when pushed makes sure data is within parameters then is sent to server
        createVerifyButton = new JButton("Create");
        createVerifyButton.addActionListener(actionListener);
        createPanelThree.add(createVerifyButton);
        // when pushed makes sure data is within parameters then is sent to server
        backToLoginButton = new JButton("Return to login screen");
        backToLoginButton.addActionListener(actionListener);
        createPanelThree.add(backToLoginButton);
        content.add(createPanelTwo, BorderLayout.CENTER);
        content.add(createPanelThree, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // makes a button for the user to log in to their account
    // tested using GUI
    public static void loginButton(DataInputStream dis, DataOutputStream dos) throws IOException {
        dos.writeUTF("login");
        try {
            dos.writeUTF(username.getText());
            dos.flush();
            dos.writeUTF(password.getText());
            dos.flush();
            String login = dis.readUTF();
            if (login.equals("true")) {
                profile(dis, dos);
            } else if (login.equals("false")) {
                // username or password is incorrect so message is displayed and user can try again
                JOptionPane.showMessageDialog(null, "Username or password was incorrect",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                username.setText("");
                password.setText("");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // button used to create a groupchat based on information provided in the text fields
    // tested using GUI
    public static void createVerify(DataInputStream dis, DataOutputStream dos) throws IOException {
        boolean run = true;
        while (run) { // while loop in case data needs to be reentered
            int x = 0;
            try {
                Integer.parseInt(age.getText()); // check if age is a number if not error message
            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(null, "Age must be a number",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                x = 1;
            }
            if (x == 1) {
                age.setText("");
                return;
            } // check if any fields are blank if so error message
            else if (username.getText().equals("") || password.getText().equals("") ||
                    name.getText().equals("") || age.getText().equals("") || gender.getText().equals("")
                    || email.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "You cannot leave a field blank",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else if (username.getText().contains(",") || password.getText().contains(",") ||
                    name.getText().contains(",") || age.getText().contains(",") || gender.getText().contains(",")
                    || email.getText().contains(",")) {
                JOptionPane.showMessageDialog(null, "A field contains an illegal character",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                // check if any fields contain commas if so error message
                username.setText("");
                password.setText("");
                name.setText("");
                age.setText("");
                gender.setText("");
                email.setText("");
                return;
            } else { // if all fields are viable data is sent to server to be stored
                dos.writeUTF(username.getText());
                dos.writeUTF(password.getText());
                dos.writeUTF(name.getText());
                dos.writeUTF(age.getText());
                dos.writeUTF(gender.getText());
                dos.writeUTF(email.getText());
                String next = dis.readUTF(); // checks if username is already taken
                if (next.equals("true")) {
                    frame.setVisible(false);
                    run = false;
                    dos.writeUTF("main");
                    new SignUpLoginClient(socket);
                } else if (next.equals("false")) { // username is is already taken
                    JOptionPane.showMessageDialog(null, "Username already taken",
                            "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }

    // button allows the user to edit their account information
    // tested using GUI
    public static void editAct(DataInputStream dis, DataOutputStream dos) throws IOException {
        String pass = password.getText();
        frame.setVisible(false);
        dos.writeUTF("edit");
        frame = new JFrame("Facebook for Penguins");
        Container content = frame.getContentPane();
        username = new JTextField(10);
        username.setText(userName);
        password = new JTextField(10);
        password.setText(pass);
        JPanel createPanel = new JPanel();
        createPanel.setLayout(new BoxLayout(createPanel, BoxLayout.Y_AXIS));
        JPanel createPanelTwo = new JPanel();
        createPanelTwo.setLayout(new BoxLayout(createPanelTwo, BoxLayout.Y_AXIS));
        JPanel createPanelThree = new JPanel();
        // createPanelThree.setLayout(new BoxLayout(createPanelThree, BoxLayout.Y_AXIS));
        // do not need this (dhanpreet)
        createPanel.add(new JLabel("Username: " + username.getText()));
        createPanel.add(new JLabel("Password:"));
        createPanel.add(password);
        content.add(createPanel, BorderLayout.NORTH);
        name = new JTextField(10);
        createPanelTwo.add(new JLabel("Name:"));
        createPanelTwo.add(name);
        name.setText(userId);
        age = new JTextField(10);
        createPanelTwo.add(new JLabel("Age:"));
        createPanelTwo.add(age);
        age.setText(userAge);
        gender = new JTextField(10);
        createPanelTwo.add(new JLabel("Gender:"));
        createPanelTwo.add(gender);
        gender.setText(userGender);
        email = new JTextField(10);
        createPanelTwo.add(new JLabel("Email:"));
        createPanelTwo.add(email);
        email.setText(userEmail);
        editVerifyButton = new JButton("Confirm Changes"); // button for when user wants to confirm changes
        editVerifyButton.addActionListener(actionListener);

        cancelEditButton = new JButton("Cancel Edit");
        cancelEditButton.addActionListener(actionListener);

        createPanelThree.add(cancelEditButton);
        createPanelThree.add(editVerifyButton);

        content.add(createPanelTwo, BorderLayout.CENTER);
        content.add(createPanelThree, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // allows the user to go back from the edit account screen
    // tested using GUI
    public static void cancelEdit(DataInputStream dis, DataOutputStream dos) throws IOException {
        dos.writeUTF("false");
        dos.writeUTF("profile"); // lets server know to send data for profile
        profile(dis, dos); // displays profile
        displayProfile();

    }

    // verifies the edits the user has made to their account
    // tested using GUI
    public static void editVerify(DataInputStream dis, DataOutputStream dos) throws IOException {
        boolean run = true;
        while (run) { // while loop in case data needs to be reentered
            int x = 0;
            try {
                Integer.parseInt(age.getText()); // check if age is a number if not error message
            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(null, "Age must be a number",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                x = 1;
            }
            if (x == 1) {
                age.setText("");
                return;
            } else if (username.getText().equals("") || password.getText().equals("") ||
                    name.getText().equals("") || age.getText().equals("") || gender.getText().equals("")
                    || email.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "You cannot leave a field blank",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                // check if any fields are blank if so error message
                return;
            } else if (username.getText().contains(",") || password.getText().contains(",") ||
                    name.getText().contains(",") || age.getText().contains(",") ||
                    gender.getText().contains(",") || email.getText().contains(",")) {
                JOptionPane.showMessageDialog(null, "A field contains an illegal character",
                        "Facebook for Penguins", JOptionPane.INFORMATION_MESSAGE);
                // check if any fields have an illegal character if so error message
                password.setText("");
                name.setText("");
                age.setText("");
                gender.setText("");
                email.setText("");
                return;
            } else { // if all fields are viable data is sent to server to be stored
                dos.writeUTF("true");
                userId = name.getText();
                userName = username.getText();
                userAge = age.getText();
                userGender = gender.getText();
                userEmail = email.getText();
                dos.writeUTF(username.getText());
                dos.writeUTF(password.getText());
                dos.writeUTF(name.getText());
                dos.writeUTF(age.getText());
                dos.writeUTF(gender.getText());
                dos.writeUTF(email.getText());
                dos.writeUTF("profile"); // lets server know to send data for profile
                run = false;
                profile(dis, dos);
            }
        }
    }

    //this button allows the user to delete their account
    //tested using GUI
    public static void deleteAct(DataInputStream dis, DataOutputStream dos) throws IOException {
        int reply = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete your account",
                "Facebook for Penguins",
                JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) { // is yes is pressed server deletes account
            dos.writeUTF("delete");
            frame.setVisible(false);
            new SignUpLoginClient(socket);
        } else { // if no returns to profile
            return;  // ** Calling profile close the program
            //used return  instead (dhanpreet).
        }
    }

    //this button shows the user an option to go into group or private messages
    //tested using GUI
    public static void messageList(DataInputStream dis, DataOutputStream dos) throws IOException {
        dos.writeUTF("messages");
        frame.setVisible(false);
        frame = new JFrame("Facebook for Penguins");
        Container content = frame.getContentPane();
        JPanel messagePanel = new JPanel();
        //Box box = Box.createVerticalBox();
        privateMsg = new JButton("Enter private conversation");
        privateMsg.addActionListener(actionListener);
        groupMsg = new JButton("Enter group conversation");
        groupMsg.addActionListener(actionListener);
        messagePanel.add(privateMsg);
        messagePanel.add(groupMsg);
        //messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileButton = new JButton("Profile");
        profileButton.addActionListener(actionListener);
        messagePanel.add(profileButton);
        //messagePanel.add(box);
        //messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelTwo = new JPanel();
        panelTwo.setLayout(null);


        messagePicture = new JLabel("--Please select what chat room you want to join--");
        messagePicture.setBounds(160, -470, 1000, 1000);
        panelTwo.add(messagePicture);


        ImageIcon penguinGroup = new ImageIcon("Project5_Java/src/social_structure.jpg");
        Image image = penguinGroup.getImage();
        Image scaledImage = image.getScaledInstance(300, 200, Image.SCALE_DEFAULT);
        penguinGroup = new ImageIcon(scaledImage);
        pictureForMessageScreen = new JLabel(penguinGroup);
        pictureForMessageScreen.setBounds(40, -80, 500, 500);
        panelTwo.add(pictureForMessageScreen);


        frame.add(panelTwo);
        //content.add(panelTwo, BorderLayout.SOUTH);
        content.add(messagePanel, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //pressing this button displays all private messages to the screen
    //tested using GUI
    public static void privateMsgConvo(DataInputStream dis, DataOutputStream dos) throws Exception {
        privChats = new JPanel();
        privButtons = new ArrayList<>();
        priv = true;
        dos.writeUTF("private"); // Write to the server that its a private message
        readUsernames("Project5_Java/src/Userdata.txt");
        readPrivChat("Project5_Java/src/privChats.txt");
        frame.setVisible(false);
        frame = new JFrame(userName + "'s Private Conversation");
        for (String key : privNames.keySet()) {
            if (key.equals(userName)) {
                String[] users = privNames.get(key).split(",");
                for (int i = 0; i < users.length; i++) {
                    b = new JButton(users[i]);
                    b.addActionListener(actionListener);
                    privButtons.add(b);
                    privChats.add(b);
                }
            }
        }
        JPanel bottom = new JPanel();
        JScrollPane scrollPane = new JScrollPane(privChats);
        exitPrivate = new JButton("Back to Main");
        exitPrivate.addActionListener(actionListener);
        createPrivateConvo = new JButton("New Private Convo");
        createPrivateConvo.addActionListener(actionListener);
        deletePrivateConvo = new JButton("Delete Convo");
        deletePrivateConvo.addActionListener(actionListener);
        refresh = new JButton("Refresh");
        refresh.addActionListener(actionListener);
        bottom.add(refresh);
        bottom.add(createPrivateConvo);
        bottom.add(deletePrivateConvo);
        bottom.add(exitPrivate);

        Container content = frame.getContentPane();
        content.add(scrollPane);
        content.add(bottom, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //pressing this button displays all groupchats to the screen
    //tested using GUI
    public static void groupMsgConvo(DataInputStream dis, DataOutputStream dos) throws Exception {
        //readGroupChats("Project5_Java/src/gcNames.txt", userId);
        priv = false;
        dos.writeUTF("group");
        // Retrieve the hashmap with all the groupchat names
        readGroupChats("Project5_Java/src/gcNames.txt", username.getText());
        for (String key : gcNames.keySet()) {
            b = new JButton(key);
            b.addActionListener(actionListener);
            groupButtons.add(b);
            chatNames.add(b);
        }
        JScrollPane scrollPane = new JScrollPane(chatNames);
        frame.setVisible(false);
        frame = new JFrame(userName + "'s Group Conversations");
        Container content = frame.getContentPane();
        content.add(scrollPane);
        createGroupButton = new JButton("Create New Group");
        createGroupButton.addActionListener(actionListener);
        JPanel panel = new JPanel();
        exitGroups = new JButton("Exit Group Convos");
        exitGroups.addActionListener(actionListener);
        refresh = new JButton("Refresh");
        refresh.addActionListener(actionListener);
        panel.add(refresh);
        panel.add(createGroupButton);
        content.add(panel, BorderLayout.SOUTH);
        deleteGroupConvo = new JButton("Delete Conversation");
        deleteGroupConvo.addActionListener(actionListener);
        panel.add(deleteGroupConvo);
        panel.add(exitGroups);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //pressing this button brings the user to a screen which allows them to create a new groupchat
    //tested using GUI
    public static void createGroup(DataInputStream dis, DataOutputStream dos, boolean createdGrp) throws Exception {
        dos.writeUTF("create");
        frame.setVisible(false);
        frame = new JFrame("Create New GroupChat");
        Container content = frame.getContentPane();
        createGroupVerify = new JButton("Verify");
        createGroupVerify.addActionListener(actionListener);
        backToGroupButton = new JButton("Back");
        backToGroupButton.addActionListener(actionListener);
        JOptionPane.showMessageDialog(null,
                "After inputting details, ensure to click 'Verify', then 'Back'",
                "IMPORTANT", JOptionPane.INFORMATION_MESSAGE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JLabel currentUsers = new JLabel();
        usernames = new ArrayList<>();
        readUsernames("Project5_Java/src/Userdata.txt");
        JLabel userLabel = new JLabel("Current Users: ");
        userLabel.setBounds(100, 10, 165, 25);
        panel.add(userLabel);
        currentUsers.setText(Arrays.toString(usernames.toArray()));
        System.out.println(currentUsers.getText());
        currentUsers.setBounds(240, 10, 300, 25);
        panel.add(currentUsers);
        JLabel nameLabel = new JLabel("Groupchat Name:");
        JLabel users = new JLabel("Add users (Separate usernames by '/' and include your username):");
        JPanel messagePanel = new JPanel();
        gcName = new JTextField(20);
        addUsers = new JTextField(userName + "/");

        nameLabel.setBounds(100, 80, 165, 25);
        panel.add(nameLabel);
        gcName.setBounds(240, 80, 250, 25);
        panel.add(gcName);

        users.setBounds(100, 160, 1000, 25);
        panel.add(users);
        addUsers.setBounds(240, 180, 250, 25);
        panel.add(addUsers);

        messagePanel.add(createGroupVerify, BorderLayout.WEST);
        messagePanel.add(backToGroupButton, BorderLayout.EAST);
        content.add(panel, BorderLayout.CENTER);
        content.add(messagePanel, BorderLayout.SOUTH);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //this button allows the user to leave an individual conversation and look at all private messages
    //tested using GUI
    public static void backToPrivate(DataInputStream dis, DataOutputStream dos) throws Exception {
        frame.setVisible(false);
        dos.writeUTF("back");
        dos.writeUTF("messages");
        privateMsgConvo(dis, dos);
    }

    // this button allows the user to leave a group conversation and view all groupchats
    // tested using GUI
    public static void backToGroup(DataInputStream dis, DataOutputStream dos) throws Exception {
        chatNames = new JPanel();
        readGroupChats("Project5_Java/src/gcNames.txt", username.getText());
        for (String key : gcNames.keySet()) {
            b = new JButton(key);
            b.addActionListener(actionListener);
            groupButtons.add(b);
            chatNames.add(b);
        }
        frame.setVisible(false);
        frame = new JFrame("Group Conversation");
        dos.writeUTF("back");
        dos.writeUTF("messages");
        groupMsgConvo(dis, dos);
    }

    //this method reads the content inside a groupchat file
    //tested using files
    public static void readGroupChats(String filename, String username) throws IOException {
        gcNames = new HashMap<>();
        chatNames = new JPanel();
        File f = new File(filename);
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String lines = bfr.readLine();
        while (lines != null) {
            String[] lineCont = lines.split(",");
            if ((lineCont[1].contains(username))) {
                gcNames.put(lineCont[0], lineCont[1]);
            }
            lines = bfr.readLine();
        }
    }

    // reads user data from file and stores usernames in an arraylist and other data in hashmap
    // tested using files
    public static void readUsernames(String filename) throws IOException {
        File f = new File(filename);
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String lines = bfr.readLine();
        while (lines != null) {
            String username = lines.substring(0, lines.indexOf(","));
            usernames.add(username);
            lines = bfr.readLine();
        }
    }

    //Writes list of private chats a user has and puts them in a hashmap
    //tested using files
    public static void writePrivChats(String filename, HashMap<String, String> username) throws Exception {
        privNames = username;
        FileOutputStream fs = new FileOutputStream(filename);
        PrintWriter pw = new PrintWriter(fs);
        for (String s : privNames.keySet()) {
            pw.println(s + ":" + privNames.get(s));
            pw.flush();
        }
    }

    //reads which users the individual has private chats with
    //tested using files
    public static void readPrivChat(String filename) throws Exception {
        privNames = new HashMap<>();
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileReader fr = new FileReader(filename);
        BufferedReader bfr = new BufferedReader(fr);
        String line = bfr.readLine();
        while (line != null) {
            if (line.split(":").length != 2) {
                line = bfr.readLine();
                continue;
            }
            privNames.put(line.split(":")[0], line.split(":")[1]);
            line = bfr.readLine();
        }
        fr.close();
        bfr.close();
    }

    //Updates list of private chats a user has and puts them in a hashmap
    //tested by tracking contents of files
    public static void updatePrivateConvos(String response) throws Exception {
        String receps = privNames.get(userName);
        if (receps.contains(response + ",")) {
            receps = receps.replaceAll(response + ",", "");
        } else if (receps.contains("," + response)) {
            receps = receps.replaceAll("," + response, "");
        } else if (receps.contains(response)) {
            receps = receps.replaceAll(response, "");
        }
        privNames.replace(userName, receps);
        System.out.println(privNames.toString());
        writePrivChats("Project5_Java/src/privChats.txt", privNames);
    }

    //deletes the text file which contains the conversation the user wants to delete
    //tested by tracking existence of files
    public static void clearMessages(String userName, String response) throws Exception {
        File f = new File("Project5_Java/src/" + userName + "_" + response + ".txt");
        if (f.exists()) {
            f.delete();
        }
    }
}