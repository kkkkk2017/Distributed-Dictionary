/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonExecutor {
    private Gson gson = new Gson();
    private String filePath;

    public JsonExecutor(String filePath) {
        this.filePath = filePath;
    }

    public HashMap<String, ArrayList> readJson(){
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return gson.fromJson(reader, HashMap.class);
    }

    public void updateJson(){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            gson.toJson(Server.getInstance().getDictionary(), fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error in JsonExecutor updateJson");
            System.err.println(e.getMessage());
        }
    }
}

