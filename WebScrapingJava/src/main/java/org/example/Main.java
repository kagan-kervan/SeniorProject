package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("sp500_symbols_yahoo.json"));
        JsonParser parser = new JsonParser(br);
        InputRoot root = parser.getJsonElements();
        ArrayList<StockInput> inputs = root.getStockInputs();
        OutputRoot output = parser.readLatestNews(new BufferedReader(new FileReader("latest_news.json")));
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        // Disable sandboxing
        options.addArguments("--no-sandbox");
        // Prevent shared memory issues
        options.addArguments("--disable-dev-shm-usage");
        WebDriver driver = new FirefoxDriver(options);
        WebSiteScraper siteScraper = new WebSiteScraper(driver, output);
        long startTime = System.currentTimeMillis();
        for (StockInput input : inputs) {
           StockNewsInformation information = siteScraper.checkNewsForStock(input.getSymbol());
           if (information != null) {
            parser.writeToJson(information);
           }
        }
        long finishTime = System.currentTimeMillis();
        driver.close();
        parser.writeLatestNewsJson(output);
        System.out.println("Execution Time: "+(finishTime-startTime)+"MS");
    }
}