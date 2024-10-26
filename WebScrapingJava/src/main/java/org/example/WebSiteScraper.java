package org.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class WebSiteScraper {
    private static final Log log = LogFactory.getLog(WebSiteScraper.class);
    public static HashMap<String, String > latestNews;
    private WebDriver webDriver;

    public WebSiteScraper(WebDriver driver, OutputRoot outputRoot) {
        latestNews = outputRoot.getLatestNews();
        webDriver = driver;
    }

    public StockNewsInformation checkNewsForStock(String stockSymbol){
        log.info("Getting stock"+stockSymbol+" site.");
        navigateToSite(stockSymbol);
        log.info("Finished getting "+stockSymbol+" site.");
        log.info("Parsing the site.");
        Document doc = Jsoup.parse(webDriver.getPageSource());
        log.info("Check for latest news for stock "+stockSymbol);
        Element latestNewForStock = findLatestNewsForStock(doc);
        if(latestNewForStock == null){
            log.info("No news found for stock "+stockSymbol);
            return null;
        }
        String latestTitle = latestNewForStock.getElementsByTag("h3").get(0).text();
        if(latestTitle == null)
            return null;
        if(!(isNewSite(stockSymbol,latestTitle))){
            log.info("No new news found for stock "+stockSymbol);
            return null;
        }
        log.info("Getting latest news link..");
        Element newsLinkElement = getLatestNewsLink(latestNewForStock);
        String newsLinkURL = buildFullLink(newsLinkElement.attr("href"));
        if(newsLinkURL.isEmpty()){
            log.info("Cannot find the url for the news link.");
            return null;
        }
        log.info("Getting the"+stockSymbol+" news site with driver.");
        webDriver.get(newsLinkURL);
        log.info("Finished getting "+stockSymbol+" news site.");
        Document newsDoc = Jsoup.parse(webDriver.getPageSource());
        log.info("Extract details from the news..");
        StockNewsInformation stockNewsInformation = extractNewsDetails(newsDoc);
        stockNewsInformation.setStockSymbol(stockSymbol);
        latestNews.put(stockSymbol,stockNewsInformation.getTitle());
        log.info("Finished stock news checking");
        return stockNewsInformation;
    }

    private Element getLatestNewsLink(Element latestNews){
        Elements anchorTags = latestNews.getElementsByTag("a");
        for (Element anchor : anchorTags) {
            if (anchor.hasAttr("href")) {
                return anchor;
            }
        }
        return null;
    }
    private Element findLatestNewsForStock(Document doc){
        ArrayList<Element> listItems = doc.getElementsByTag("li");
        for (int i = 0; i < listItems.size(); i++) {
            Element item = listItems.get(i);
            if (item.classNames().contains("stream-item") && item.classNames().contains("story-item")) {
                return item;
            }
        }
        return null;
    }

    private static StockNewsInformation extractNewsDetails(Document newsDoc) {
        // Extract main fields
        Element titleEl = newsDoc.getElementsByTag("h1")
                .stream()
                .filter(e -> e.classNames().contains("cover-title"))
                .findFirst()
                .orElse(null);

        List<String> stocks = newsDoc.getElementsByTag("span")
                .stream()
                .filter(e -> e.classNames().contains("symbol") || e.classNames().contains("yf-138ga19"))
                .map(Element::text)
                .toList();

        String time = newsDoc.getElementsByTag("time")
                .stream()
                .filter(e -> e.classNames().contains("byline-attr-meta-time"))
                .map(Element::text)
                .findFirst()
                .orElse(null);

        List<String> paragraphs = newsDoc.getElementsByTag("p")
                .stream()
                .filter(e -> e.classNames().contains("yf-1pe5jgt"))
                .map(Element::text)
                .toList();

        // Create and return the StockInformation object
        return new StockNewsInformation(
                titleEl != null ? titleEl.text() : null,
                stocks,
                time,
                paragraphs
        );
    }


    private static String buildFullLink(String link) {
        return link.startsWith("http") ? link : "https://finance.yahoo.com" + link;
    }
    private void navigateToSite(String stockSymbol){
        webDriver.get("https://finance.yahoo.com/quote/" + stockSymbol + "/news/");
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
    }

    private boolean isNewSite(String stockSymbol, String latestTitle){
        String newsTitle = latestNews.getOrDefault(stockSymbol,"not-found");
        if(newsTitle.equalsIgnoreCase("not-found"))
            return true;
        return !(newsTitle.equals(latestTitle));
    }
}
