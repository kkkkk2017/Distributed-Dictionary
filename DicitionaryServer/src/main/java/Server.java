/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final String endMsg = "#00#";
    private static int counter = 0;
    private static String filePath;

    private static ServerSocket listeningSocket = null;
    private static Socket clientSocket = null;

    private static BufferedReader in = null;
    private static BufferedWriter out = null;

    private static LinkedList<Task> waitingQueue = new LinkedList<>();
    private static ThreadPool threadPool = new ThreadPool(4);
    private static JsonExecutor jsonExecutor = new JsonExecutor();
    private static HashMap<String, ArrayList> cacheDictionary;

    private static Server server = new Server();

    public static Server getInstance(){
        return server;
    }

    public HashMap<String, ArrayList> getDictionary(){
      return cacheDictionary;
    }

    public static void setFilePath(String filePath) {
        Server.filePath = filePath;
    }

    private static void sendToClient(String input){
        try{
            if (input.endsWith("\n")){
                out.write(input);
            }else{
                out.write(input+"\n");
            }
            out.flush();
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
    }

    //allocate the task into the threadPool. If the pool is full, put on the wait list.
    private static void allocateTask(String clientId, String taskType, String value) {
        //check if the value contains both word and meaning
        Task task = null;
        if(value == null){
            task = new Task(clientId, taskType);
        }else{
            if(value.contains(" ")){
                String[] splited = value.split("\\s+", 2);
                task = new Task(clientId, taskType, splited[0], splited[1]);
            } else {
                task = new Task(clientId, taskType, value);
            }
        }
        System.out.println("\nAllocated " + task.toString() + " to the waiting list");
        waitingQueue.add(task);
    }

    private static void shutDownServer(String filePath) {
        try {
            System.out.println("Shutting down server...");
            threadPool.shutdown();
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (listeningSocket != null) {
                listeningSocket.close();
            }
            jsonExecutor.updateJson(filePath, cacheDictionary);
            System.out.println("Server shut down. Thanks for using.");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean checkTask(String task){
        for(TASKLIST t: TASKLIST.values()){
            if (t.toString().equals(task)){
                return true;
            }
        }
        return false;
    }

    public static void executeThreads(){
        while(!waitingQueue.isEmpty()){
            Task task = waitingQueue.poll();
            System.out.println("Add task to the threadPool " + task.toString());
            threadPool.execute(task);
        }
    }

    private static void serveClient(Socket client) {
        try(Socket clientSocket = client){
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

            sendToClient("connected");

            String clientPort = String.valueOf(client.getPort());
            String input = null;
            String taskKey = null;
            String taskValue = null;

            while ((input = in.readLine()) != null ) {
                if (!input.equals("")) {
                    if (input.equals("exit")) {
                        System.out.println("Client " + clientPort + " disconnect.");
                        if (threadPool.getResultList().containsKey(clientPort)) {
                            threadPool.getResultList().remove(clientPort);
                        }
                        client.close();
                        counter--;
                        System.out.println("The connecting client number is " + counter);
                        jsonExecutor.updateJson(filePath, cacheDictionary);
                        break;
                    } else if (input.equals("getAllWord")) {
                        sendToClient("ok");
                        sendToClient(cacheDictionary.keySet().toString());
                        sendToClient(endMsg);
                    } else if (!checkTask(input)) {
                        taskValue = input;
                        allocateTask(clientPort, taskKey, taskValue);
                        executeThreads();

                        if (threadPool.getResultList().containsKey(clientPort)) {
                            String result = threadPool.getResultList().get(clientPort);
                            threadPool.getResultList().remove(clientPort);
                            sendToClient(result);
                            sendToClient(endMsg);
                        }
                    } else {
                        taskKey = input;
                        sendToClient("ok");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try{
            int port = Integer.parseInt(args[0]);
            setFilePath(args[1]);

            ServerSocketFactory factory = ServerSocketFactory.getDefault();
            ServerSocket serverSocket = factory.createServerSocket(port);
            cacheDictionary = jsonExecutor.readJson(args[1]);

            System.out.println("Waiting for client connection");
            //wait for connections
            while(true){
                Socket client = serverSocket.accept();
                counter++;
                System.out.println("Client " + counter + ": Apply for connection!");

                //start new thread for a connection
                Thread t = new Thread(()->serveClient(client));
                t.start();
            }
        }
        catch (NumberFormatException e){
            System.out.println("Port must be a integer.");
        }
        catch (IOException e1) {
            System.out.println(e1.getMessage());
        }
    }
}
