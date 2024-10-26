package org.example;

import java.util.List;

public class StockNewsInformation {
    private String stockSymbol;
    private String title;
    private List<String> stocks;
    private String time;
    private List<String> paragraphs;

    public StockNewsInformation(String title, List<String> stocks, String time, List<String> paragraphs) {
        this.title = title;
        this.stocks = stocks;
        this.time = time;
        this.paragraphs = paragraphs;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getStocks() { return stocks; }
    public void setStocks(List<String> stocks) { this.stocks = stocks; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public List<String> getParagraphs() { return paragraphs; }
    public void setParagraphs(List<String> paragraphs) { this.paragraphs = paragraphs; }
}
