/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class UserScreen {
    private JButton exitButton;
    private JTextArea resultArea;
    private JButton addWordButton;
    private JButton queryButton;
    private JTextField wordText;
    private JButton deleteWordButton;
    private JButton addMeaningButton;
    private JButton deleteMeaningButton;
    private JLabel wordBoxLabel;
    private JLabel resultBoxLabel;
    private JLabel dltWordLabel;
    private JLabel addWordLabel;
    private JLabel addMeaningLabel;
    private JLabel dltMeaningLabel;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JTextArea dictArea;
    private JScrollPane dictScrollPane;

    private final Client client;

    private URL plusImgPath = getClass().getClassLoader().getResource("plus.png");
    private URL minusImgPath = getClass().getClassLoader().getResource("minus.png");

    public UserScreen(final Client client) {
        this.client = client;

        final JFrame frame = new JFrame();
        frame.setSize(555+20,400);
        frame.setLayout(null);

        titleLabel.setBounds(10, 0, 300, 40);
        titleLabel.setFont(new Font(titleLabel.getText(), Font.BOLD, 15));

        exitButton.setBounds(280+130, 10, 100, 30);
        queryButton.setBounds(280+130, 50+5, 100, 30);
        addWordButton.setIcon(new ImageIcon(plusImgPath));
        addWordButton.setBounds(280+130, 95,30, 30);
        addWordLabel.setBounds(315+130, 105, 120, 15);
        deleteWordButton.setIcon(new ImageIcon(minusImgPath));
        deleteWordButton.setBounds(280+130, 135, 30, 30);
        dltWordLabel.setBounds(315+130, 145, 120, 15);

        addMeaningButton.setIcon(new ImageIcon(plusImgPath));
        addMeaningButton.setBounds(280+130, 195+20, 30, 30);
        addMeaningLabel.setBounds(315+130, 205+20, 120, 15);
        deleteMeaningButton.setIcon(new ImageIcon(minusImgPath));
        deleteMeaningButton.setBounds(280+130, 235+20, 30, 30);
        dltMeaningLabel.setBounds(315+130, 245+20, 120, 15);

        wordBoxLabel.setLocation(10+130, 40);
        wordBoxLabel.setSize(50+130, 10);
        wordText.setBounds(10+130, 55, 260, 30);
        wordText.setBackground(Color.white);
        wordText.setEditable(true);

        resultBoxLabel.setLocation(10+130, 90);
        resultBoxLabel.setSize(50+130, 10);

        resultArea.setSize(260+130, 250);
        resultArea.setBackground(Color.white);
        resultArea.setEditable(false);
        scrollPane.setBounds(10+130, 100+5, 260, 250);
        scrollPane.getViewport().setView(resultArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        dictArea.setSize(120, 300);
        dictArea.setBackground(Color.white);
        dictArea.setEditable(false);
        dictScrollPane.setBounds(10, 50, 120, 300);
        dictScrollPane.getViewport().setView(dictArea);
        dictScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(exitButton);
        frame.add(wordText);
        frame.add(queryButton);
        frame.add(addWordButton);
        frame.add(addMeaningButton);
        frame.add(scrollPane);
        frame.add(deleteWordButton);
        frame.add(deleteMeaningButton);
        frame.add(wordBoxLabel);
        frame.add(resultBoxLabel);
        frame.add(addWordLabel);
        frame.add(addMeaningLabel);
        frame.add(dltWordLabel);
        frame.add(dltMeaningLabel);
        frame.add(titleLabel);
        frame.add(dictScrollPane);

        displayDictionary();

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    client.closeConnection();
                    System.exit(0);
            }
        });

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!wordText.getText().isEmpty()){
                    client.sendTask("query", wordText.getText(), null);
                    String result = client.getReceivedMsg();
                    result = parseResult(result);
                    resultArea.setText(result);
                }else{
                    JOptionPane.showMessageDialog(frame, "Enter a word to query.");
                }
            }
        });

        addWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String addWord = null;
                if(!wordText.getText().isEmpty()){
                    addWord = wordText.getText();
                }
                addWord = JOptionPane.showInputDialog(frame,
                        "Enter the word you want to add meaning", addWord);
                if(addWord != null){
                    String addMeaning =  JOptionPane.showInputDialog(frame,
                            "Enter the meaning you want to add for \"" + addWord + "\"");
                    while (addMeaning == null){
                        addMeaning =  JOptionPane.showInputDialog(frame,
                                "The meaning can not be empty.\nEnter the meaning you want to add for \"" + addWord + "\"");
                    }
                    client.sendTask("addWord", wordText.getText(), addMeaning);
                    String result = parse(client.getReceivedMsg());
                    JOptionPane.showMessageDialog(frame, result);
                    displayDictionary();
                }
            }
        });

        addMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String addWord = JOptionPane.showInputDialog(frame,
                        "Enter the word you want to add meaning", wordText.getText());
                if(addWord != null){
                    String addMeaning =  JOptionPane.showInputDialog(frame,
                            "Enter the meaning you want to add for \"" + addWord + "\"");
                    if (addMeaning != null){
                        client.sendTask("addMeaning", addWord, addMeaning);
                        String result = parse(client.getReceivedMsg());
                        JOptionPane.showMessageDialog(frame, result);
                    }
                }
            }
        });

        deleteWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wordText.getText().isEmpty()){
                    JOptionPane.showMessageDialog(frame, "No chosen word to delete.");
                }else{
                    String deleteWord = wordText.getText();
                    int answer = JOptionPane.showConfirmDialog(frame, "Are you sure delete \"" + deleteWord + "\" ?",
                            "Alert", JOptionPane.YES_NO_OPTION);
                    if(answer == 0){
                        client.sendTask("deleteWord", deleteWord, null);
                        String result = parse(client.getReceivedMsg());
                        JOptionPane.showMessageDialog(frame, result);
                        displayDictionary();
                    }
                }
            }
        });

        deleteMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resultArea.getText().isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Please query a word and look at its meaning first.");
                }else {
                    String deleteWord = wordText.getText();
                    String[] choices = resultArea.getText().split("\\d+\\.\\s");

                    String deleteMeaning = (String) JOptionPane.showInputDialog(null,
                            "Choose the meaning to delete for \"" + wordText.getText() + "\"",
                            "Delete meaning", JOptionPane.QUESTION_MESSAGE, null,
                            choices, // Array of choices
                            choices[1]); // Initial choice
                    if (deleteWord != null && deleteMeaning != null){
                        String result = null;
                        client.sendTask("deleteMeaning", deleteWord, deleteMeaning);
                        result = parse(client.getReceivedMsg());
                        JOptionPane.showMessageDialog(frame, result);
                        displayDictionary();
                    }
                }
            }
        });

        wordText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                wordText.setText("");
                resultArea.setText("");
            }
        });

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    private void displayDictionary(){
        client.sendTask("getAllWord", null, null);
        String result = client.getReceivedMsg();
        result = parseResult(result);
        dictArea.setText(result);
    }
    /**
     * Parse result string into numbered string
     * @param input
     * @return numbered string
     */
    private String parseResult(String input){
        String output = "";
        String[] toArray = null;
        if(input.startsWith("[")){
            input = input.substring(1,input.length()-2);
            toArray = input.split(", ");
        }else{
            toArray = input.split("\n");
        }
        for (int i = 0; i < toArray.length; i++) {
            if(!toArray[i].contains(client.getEndMsg())){
                output+=(i+1)+". "+toArray[i]+"\n";
            }
        }
        return output;
    }

    //get rid of the end message
    private String parse(String input){
        String[] a = input.split(client.getEndMsg());
        return a[0];
    }

    public static void main(String[] args)
    {
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1].trim());

            Client client = new Client();
            if(client.connectToServer(host, port).equals("connected")){
                new UserScreen(client);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Server is not up\nPlease try run the server first." );
                System.exit(0);
            }

        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(new JFrame(), "The connection port error.\nPlease try again." );
            System.exit(0);
        }
    }
}
