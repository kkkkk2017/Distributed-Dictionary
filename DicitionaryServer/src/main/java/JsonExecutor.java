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

    public HashMap<String, ArrayList> readJson(String filePath){
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return gson.fromJson(reader, HashMap.class);

    }

    public void updateJson(String filePath, HashMap<String, ArrayList> dictionary){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            gson.toJson(dictionary, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

