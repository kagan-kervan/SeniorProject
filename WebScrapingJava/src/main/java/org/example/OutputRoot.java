package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class OutputRoot {
    private HashMap<String, String> latestNews;
    public OutputRoot() {
        latestNews = new HashMap<>();
    }

    public HashMap<String, String> getLatestNews() {
        return latestNews;
    }

    public void setLatestNews(HashMap<String, String> latestNews) {
        this.latestNews = latestNews;
    }
}
