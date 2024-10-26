package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

@Log
public class JsonParser {
    private Gson gson;
    private BufferedReader br;

    public JsonParser(BufferedReader reader) throws IOException {
        br = reader;
        this.gson = new Gson();
    }

    public InputRoot getJsonElements() throws IOException {
        try{
            return gson.fromJson(br, InputRoot.class);
        }
        catch (JsonSyntaxException e){
            throw  e;
        }
    }

    public void writeToJson(StockNewsInformation newsInformation) throws IOException {
        log.info("Started JSON news writing.");
        Gson new_gson = new GsonBuilder().setPrettyPrinting().create();
        String json = new_gson.toJson(newsInformation);
        FileWriter fileWriter =  new FileWriter(newsInformation.getStockSymbol()+"_news.json");
        fileWriter.write(json);
        fileWriter.close();
        log.info("Finished JSON news writing.");
    }

    public void writeLatestNewsJson(OutputRoot outputRoot) throws IOException {
        log.info("Started JSON latest news writing.");
        gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(outputRoot);
        FileWriter writer = new FileWriter("latest_news.json");
        writer.write(json);
        writer.close();
        log.info("Finished JSON latest news writing.");
    }

    public OutputRoot readLatestNews(BufferedReader br) throws IOException {
        try{
            return gson.fromJson(br, OutputRoot.class);
        }catch (JsonSyntaxException e){
            throw  e;
        }
    }

}
