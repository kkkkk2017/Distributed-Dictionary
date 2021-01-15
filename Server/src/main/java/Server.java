/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static ServerSocket listeningSocket = null;
    private static Socket clientSocket = null;

    private static ArrayList<ClientHandler> clientList = new ArrayList<ClientHandler>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private static JsonExecutor jsonExecutor;
    private static HashMap<String, ArrayList> cacheDictionary;

    private static Server server = new Server();

    public static Server getInstance(){
        return server;
    }

    public HashMap<String, ArrayList> getDictionary(){
      return cacheDictionary;
    }

    public static JsonExecutor getJsonExecutor() {
        return jsonExecutor;
    }

    private static void shutDownServer(String filePath) {
        try {
            System.out.println("Shutting down server...");
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (listeningSocket != null) {
                listeningSocket.close();
            }
            jsonExecutor.updateJson();
            System.out.println("Server shut down. Thanks for using.");
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);

            ServerSocket serverSocket = new ServerSocket(port);
            jsonExecutor = new JsonExecutor(args[1]);
            cacheDictionary = jsonExecutor.readJson();

            while (true) {
                System.out.println("======== Server is running ========");
                System.out.println("Waiting for client connection...");
                Socket client = serverSocket.accept();
                System.out.println("*** Client " + client.getPort() + ": Apply for connection! ***");
                ClientHandler clientThread = new ClientHandler(client);
                clientList.add(clientThread);

                pool.execute(clientThread);
            }
        } catch (NumberFormatException e) {
            System.err.println("Port must be a integer.");
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        } finally {
            shutDownServer(args[0]);
        }

    }
}
