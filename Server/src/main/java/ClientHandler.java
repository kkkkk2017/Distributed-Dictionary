import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private final String endMsg = "#00#";
    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
    }

    private void sendToClient(String input){
        System.out.println(" -----> SEND TO CLIENT " + client.getPort() + " : "+ input);
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

    private boolean checkTask(String task){
        for(TASKLIST t: TASKLIST.values()){
            if (t.toString().equals(task)){
                return true;
            }
        }
        return false;
    }

    private Task createTask(String clientId, String taskType, String value) {
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
        return task;
    }

    @Override
    public void run() {
        try{
            while (true){
                sendToClient("connected");

                String clientPort = String.valueOf(client.getPort());
                String input;
                String taskKey = null;
                String taskValue;

                while ((input = in.readLine()) != null ) {
                    System.out.println("<----- RECEIVED FROM CLIENT " + clientPort + ": " + input);
                    if (!input.equals("")) {
                        if (input.equals("exit")) {
                            System.out.println("CLIENT " + clientPort + " DISCONNECT.");
                            client.close();
                            break;
                        } else if (input.equals("getAllWord")) {
                            sendToClient("ok");
                            sendToClient(Server.getInstance().getDictionary().keySet().toString());
                            sendToClient(endMsg);
                        } else if (!checkTask(input)) {
                            taskValue = input;
                            Task task = createTask(clientPort, taskKey, taskValue);
                            task.run();

                            String result = task.getResult();
                            if (result != null){
                                sendToClient(result);
                                sendToClient(endMsg);
                            }
                        } else {
                            taskKey = input;
                            sendToClient("ok");
                        }
                    }
            }
            }
        } catch (SocketException e){
            System.out.println("*** Client " + client.getPort() + " disconnect! ***");
        }catch (IOException e) {
            System.err.println("IO exception in client handler.");
            System.err.println(e.getMessage());
        }
        finally {
            try {
                in.close();
                out.close();

                Server.getInstance().getJsonExecutor().updateJson();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
