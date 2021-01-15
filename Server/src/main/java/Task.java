/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import java.util.ArrayList;

public class Task extends Thread {
    private String clientPort;
    private String taskType;
    private String word;
    private String meaning;
    private String result;

    public Task(String clientPort, String taskType, String word) {
        this.clientPort = clientPort;
        this.taskType = taskType;
        this.word = word;
    }

    public Task(String clientPort, String taskType, String word, String meaning) {
        this.clientPort = clientPort;
        this.taskType = taskType;
        this.word = word;
        this.meaning = meaning;
    }

    public Task(String clientPort, String taskType) {
        this.clientPort = clientPort;
        this.taskType = taskType;
    }

    public String getClientPort() {
        return clientPort;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "clientPort='" + clientPort + '\'' +
                ", taskType='" + taskType + '\'' +
                ", word='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                '}';
    }


    @Override
    public void run() {
        TaskExecutor executor = new TaskExecutor();
        System.out.println("Executing: " + this.toString());
        switch (taskType){
            case "query":
                setResult(executor.queryWord(this.word));
                break;
            case "addWord":
                setResult(executor.addWord(this.word, this.meaning));
                break;
            case "addMeaning":
                setResult(executor.addMeaning(this.word, this.meaning));
                break;
            case "deleteWord":
                setResult(executor.deleteWord(this.word));
                break;
            case "deleteMeaning":
                setResult(executor.deleteMeaning(this.word, this.meaning));
                break;
        }
    }
}

class TaskExecutor{

    protected String queryWord(String word){
        if (Server.getInstance().getDictionary().containsKey(word)){
            ArrayList<String> resultList = Server.getInstance().getDictionary().get(word);
            if (resultList.size() == 1){
                if(resultList.get(0).equals("") || resultList.get(0).equals(" ")){
                    return "Does not have meaning yet.\n";
                }
            }
            String result = "";
            for (String meaning: resultList) {
                if(!meaning.equals("") && !meaning.equals("\n")){
                    result += meaning+"\n";
                }
            }
            return result;
        }
        return "Word Not Found\n";
    }

    protected String addWord(String word, String meaning){
        if(Server.getInstance().getDictionary().containsKey(word)){
            return "Word Existed";
        }
        updateDictionary(word, meaning);
        return "Word Added";
    }

    protected String addMeaning(String word, String meaning){
        if(!Server.getInstance().getDictionary().containsKey(word)){
            return "Word Not Found";
        }

        updateDictionary(word, meaning);
        return "Meaning Added";
    }

    protected String deleteWord(String word){
        if(!Server.getInstance().getDictionary().containsKey(word)){
            return "Word Not Found";
        }
        deleteFromDictionary(word, null);
        return "Word Deleted";
    }

    protected String deleteMeaning(String word, String meaning){
        deleteFromDictionary(word, meaning);
        return "Meaning Deleted";
    }

    private void updateDictionary(String word, String meaning){
        ArrayList<String> update = new ArrayList<>();
        //if the word existed, insert the meaning
        //else, add the new word with new meaning
        if (Server.getInstance().getDictionary().containsKey(word)){
            update = Server.getInstance().getDictionary().get(word);
            update.add(meaning);
        } else {
            update.add(meaning);
        }
        Server.getInstance().getDictionary().put(word, update);
    }

    private void deleteFromDictionary(String word, String meaning){
        if (Server.getInstance().getDictionary().containsKey(word)){
            //if meaning is empty means delete the word,
            //else delete the meaning from the word
            if (meaning == null || meaning.isEmpty()){
                Server.getInstance().getDictionary().remove(word);
            }else {
                ArrayList<String> update = Server.getInstance().getDictionary().get(word);
                System.out.println("remove meaning: " + meaning);
                System.out.println("deleted word: " + update);
                if(update.contains(meaning)){
                    update.remove(meaning);
                }
                if(update.size() == 0){
                    Server.getInstance().getDictionary().remove(word);
                } else{
                    Server.getInstance().getDictionary().put(word, update);
                }
            }
        }
    }

}
