/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import java.io.*;
import java.net.*;

public class Client {
    final private String endMsg = "#00#";
    final private String exptMsg = "#exp#";

    private static Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;

    private String receivedMsg = "";

    public Client Client(){
        return new Client();
    }


    public String getReceivedMsg() {
        if(receivedMsg.contains(endMsg)){
            receivedMsg.substring(0, (receivedMsg.length()-endMsg.length()));
        }
        return receivedMsg;
    }

    public String getEndMsg() {
        return endMsg;
    }

    private String sendToServer(String input){
        if(socket == null){ return exptMsg+"Empty socket"; }
        try{
            System.out.println("sent to server:" + input);
            if (input.contains("\n")){
                out.write(input);
            } else{
                out.write(input+"\n");
            }
            out.flush();
        } catch (IOException e) {
            return exptMsg+"IOException.";
        }
        return "task send";
    }

    protected String connectToServer(String host, int port){
        String result = "";
        try {
            socket = new Socket(host, port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            System.out.println("Connect to server.");
            result = in.readLine();
        }catch (UnknownHostException ex){
            return  exptMsg+"UnknownHost Exception.";
        } catch (IOException ex){
            return exptMsg+"IO Exception.";
        }
        return result;
    }

    protected void closeConnection(){
        if(socket == null){ return; }
        try {
            sendToServer("exit");
            socket.close();
        }catch (IOException ex){
            return;
        }
    }

    protected void sendTask(String task, String word, String meaning){
        if(socket == null){ return; }
        try {
            sendToServer(task);

            String result = null;
            result = in.readLine();
            while (result != null && !result.equals("")) {
                if (result.equals("ok")) {
                    receivedMsg = "";
                    switch (task) {
                        case "query":
                            sendToServer(word);
                            break;
                        case "addWord":
                            sendToServer(word + " " + meaning);
                            break;
                        case "addMeaning":
                            sendToServer(word + " " + meaning);
                            break;
                        case "deleteWord":
                            sendToServer(word);
                            break;
                        case "deleteMeaning":
                            sendToServer(word + " " + meaning);
                            break;
                        case "exit":
                            closeConnection();
                            break;
                    }
                }
                else if (result.contains(endMsg)) {
                    break;
                } else {
                    receivedMsg += result + "\n";
                    System.out.println("Received result: " + receivedMsg);
                }
                result = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
