import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * This class allows for a specific client to send messages to the server.
 *
 * <p>Purdue University -- CS18000 -- Fall 2020 -- Project 5</p>
 *
 * @version December 4, 2020
 * @authors Dhanpreet S , Rishabh K , Jordan D , Pranav D ,  Kuba B
 */
public class SendServer extends Thread {
    private static DataOutputStream dos;
    private static String username;
    private static String recipientName;
    private Scanner sc = new Scanner(System.in);
    private JFrame frame;
    private String msg;
    private static String file;
    private static HashMap<Integer, JButton> messageButtons = new HashMap<>();
    private static JButton listener = new JButton();

    public SendServer(DataOutputStream dos, String username, String receipientName, String msg)
            throws IOException, InterruptedException {
        this.dos = dos;
        this.username = username;
        this.recipientName = receipientName;
        this.msg = msg;
        file = "Project5_Java/src/" + this.username + "_" + this.recipientName + ".txt";
        frame = new JFrame("Conversations");
        readConvo(file, recipientName);
    }

    @Override
    public synchronized void run() {
        //TODO: GUI IMPLEMENTATION
        try {
            if (msg.contains(",")) {
                msg = msg.replaceAll(",", "#");
                dos.writeUTF(username + "," + msg + "," + recipientName + "," + "true");
                dos.flush();
                readConvo(file, recipientName);
            } else {
                msg = msg.replaceAll(",", "#");
                dos.writeUTF(username + "," + msg + "," + recipientName + "," + "false");
                dos.flush();
                readConvo(file, recipientName);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String action = "";
            String messageRecieved = "";
            boolean delete = false;
            System.out.println(messageButtons.size());
            for (int count : messageButtons.keySet()) {
                if (messageButtons.size() == 0) {
                    break;
                }
                if (messageButtons.get(count) != null) {
                    if (messageButtons.get(count).getText().equals(e.getActionCommand().toString())) {
                        if (messageButtons.get(count).getText().contains(Integer.toString(count))) {
                            messageRecieved = messageButtons.get(count).getText();
                            String[] options = {"Edit Message", "Delete Message"};
                            action = (String) JOptionPane.showInputDialog(null,
                                    "Choose the following", "Selection",
                                    JOptionPane.PLAIN_MESSAGE, null, options, "Edit");
                            if (action != null) {
                                if (action.equals("Edit Message")) {
                                    String edited = JOptionPane.showInputDialog(null,
                                            "Original: " + messageRecieved,
                                            "Edit Message", JOptionPane.PLAIN_MESSAGE);
                                    if (edited != null) {
                                        try {
                                            delete = false;
                                            String file = "Project5_Java/src/" + recipientName + "_" +
                                                    username + ".txt";
                                            HashMap<Integer, String> users = new HashMap<>();
                                            HashMap<Integer, String> others = new HashMap<>();
                                            if (SignUpLoginClient.priv) {
                                                users = getContents(SendServer.file);
                                                others = getContents(file);
                                                replaceLine(username, count, users, messageRecieved, edited, delete);
                                                replaceLine(recipientName, count, others, messageRecieved,
                                                        edited, delete);
                                            } else {
                                                String received = SignUpLoginClient.gcNames.get(recipientName);
                                                String[] recipients = received.split("/");
                                                for (int i = 0; i < recipients.length; i++) {
                                                    users = getContents("Project5_Java/src/" + recipients[i]
                                                            + "_" + recipientName + ".txt");
                                                    replaceLine(recipients[i], count, users, messageRecieved,
                                                            edited, delete);
                                                }
                                            }

                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                } else {
                                    delete = true;
                                    try {
                                        String reversedFile = "Project5_Java/src/" + recipientName + "_" +
                                                username + ".txt";
                                        HashMap<Integer, String> users = new HashMap<>();
                                        HashMap<Integer, String> others = new HashMap<>();
                                        if (SignUpLoginClient.priv) {
                                            users = getContents(SendServer.file);
                                            others = getContents(reversedFile);
                                            replaceLine(username, count, users, messageRecieved,
                                                    messageRecieved, delete);
                                            replaceLine(recipientName, count, others, messageRecieved,
                                                    messageRecieved, delete);
                                        } else {
                                            String received = SignUpLoginClient.gcNames.get(recipientName);
                                            String[] recipients = received.split("/");
                                            for (int i = 0; i < recipients.length; i++) {
                                                users = getContents("Project5_Java/src/" + recipients[i]
                                                        + "_" + recipientName + ".txt");
                                                replaceLine(recipients[i], count, users, messageRecieved,
                                                        messageRecieved, delete);
                                            }
                                        }


                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            }

                        }
                    }
                }

            }

        }
    };

    //replaces the line corresponding to the given line number with whichever edits the user makes
    //void method used with GUI when an action is performed - tested by editing lines in GUI as a user
    public static void replaceLine(String name, int lineNo, HashMap<Integer, String> u,
                                   String edit, String message, boolean delete) throws Exception {
        HashMap<Integer, String> users = u;
        edit = edit.substring((edit.lastIndexOf("]") + 2));
        System.out.println(u.toString());
        if (u.get(lineNo) != null) {
            if (u.get(lineNo).contains(edit)) {
                if (delete) {
                    u.remove(lineNo);
                } else {
                    u.replace(lineNo, u.get(lineNo), username + " says " + message);
                }

            } else {
                for (int c : u.keySet()) {
                    if (u.get(c).contains(edit)) {
                        if (delete) {
                            u.remove(c);
                        } else {
                            u.replace(c, u.get(c), username + " says " + message);
                        }
                        break;
                    }
                }
            }
        } else {
            for (int c : u.keySet()) {
                if (u.get(c).contains(edit)) {
                    if (delete) {
                        u.remove(c);
                    } else {
                        u.replace(c, u.get(c), username + " says " + message);
                    }
                    break;
                }
            }
        }

        System.out.println("AFTER REMOVAL: " + name + " : " + u.toString());
        if (SignUpLoginClient.priv) {
            if (username.equals(name)) {
                updateFile(username, recipientName, u.values());
            } else {
                updateFile(recipientName, username, u.values());
            }
        } else {
            updateFile(name, recipientName, u.values());
        }

        dos.writeUTF("Edited/Deleted/" + recipientName);
        dos.flush();
        SendServer.readConvo(file, recipientName);

    }

    //updates the content of the file which corresponds with the conversation between the user and the recipient
    //helper method for replaceLine which is already tested
    public static void updateFile(String username, String recipient, Collection<String> allLines) throws Exception {
        String file = "Project5_Java/src/" + username + "_" + recipient + ".txt";
        if (!SignUpLoginClient.priv) {
            file = "Project5_Java/src/" + username + "_" + recipientName + ".txt";
        }
        FileOutputStream fs = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fs);
        for (String s : allLines) {
            pw.println(s);
            pw.flush();
        }
        pw.close();
        fs.close();
    }

    //reads a private or group conversation in order to display it on the gui
    public synchronized static void readConvo(String file, String recipientName)
            throws IOException, InterruptedException {
        messageButtons = new HashMap<>();
        SignUpLoginClient.frame.setVisible(false);
        //System.out.println("HERE FROM " + recipientName);
        SignUpLoginClient.frame = new JFrame("User: " + username + " Conversation with: " + recipientName);

        SignUpLoginClient.typeMessage = new JTextField(10);
        SignUpLoginClient.typeMessage.setBounds(150, 100, 165, 25);
        SignUpLoginClient.frame.add(SignUpLoginClient.typeMessage);
        SignUpLoginClient.msg = SignUpLoginClient.typeMessage.getText();

        JButton message;
        JPanel p = new JPanel();
        Container content = SignUpLoginClient.frame.getContentPane();
        JPanel text = new JPanel();
        JPanel buttonPanel = new JPanel();
        JScrollPane pane = new JScrollPane();
        buttonPanel.add(SignUpLoginClient.typeMessage);
        File f = new File(file);
        int y = 0;
        if (!f.exists()) {
            if (recipientName != null) {
                f.createNewFile();
            }
            return;
        }
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String line = bfr.readLine();
        content = SignUpLoginClient.frame.getContentPane();
        int count = 1;
        while (line != null) {
            message = new JButton();
            message.setText("[" + count + "] " + line);
            if (message.getText().contains(username + " says ")) {
                message.setBackground(Color.blue);
                message.setOpaque(true);
                listener = message;
                listener.addActionListener(actionListener);
                messageButtons.put(count, listener);
            }
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.add(message);
            line = bfr.readLine();
            count++;
        }
        pane = new JScrollPane(p);
        pane.setPreferredSize(new Dimension(600, 600));
        content.add(pane, BorderLayout.WEST);
        SignUpLoginClient.sendMessage = new JButton("Send Message");
        SignUpLoginClient.sendMessage.addActionListener(SignUpLoginClient.actionListener);
        buttonPanel.add(SignUpLoginClient.sendMessage);
        if (SignUpLoginClient.priv) {
            SignUpLoginClient.backToPrivateButton = new JButton("Back to Private Messages");
            SignUpLoginClient.backToPrivateButton.addActionListener(SignUpLoginClient.actionListener);
            buttonPanel.add(SignUpLoginClient.backToPrivateButton);
        } else {
            SignUpLoginClient.backToGroupButton = new JButton("Back to Group Messages");
            SignUpLoginClient.backToGroupButton.addActionListener(SignUpLoginClient.actionListener);
            SignUpLoginClient.showUsers = new JButton("Show Users");
            SignUpLoginClient.showUsers.addActionListener(SignUpLoginClient.actionListener);
            buttonPanel.add(SignUpLoginClient.backToGroupButton);
        }

        if (!SignUpLoginClient.priv) {
            buttonPanel.add(SignUpLoginClient.showUsers);
        }
        content.add(buttonPanel, BorderLayout.SOUTH);
        SignUpLoginClient.frame.setSize(700, 700);
        SignUpLoginClient.frame.setLocationRelativeTo(null);
        SignUpLoginClient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SignUpLoginClient.frame.setVisible(true);
    }

    //puts each file of a text file in a hashmap with its line number
    public static HashMap<Integer, String> getContents(String file) throws Exception {
        HashMap<Integer, String> allLines = new HashMap<>();
        File f = new File(file);
        if (!f.exists()) {
            return allLines;
        }
        FileReader fr = new FileReader(file);
        BufferedReader bfr = new BufferedReader(fr);
        String line = bfr.readLine();
        int count = 1;
        while (line != null) {
            allLines.put(count, line);
            line = bfr.readLine();
            count++;
        }
        fr.close();
        bfr.close();
        return allLines;
    }
}