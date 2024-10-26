package org.example;

public class StockInput {
    String symbol;
    String yahoo_finance_url;

    public StockInput() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getYahoo_finance_url() {
        return yahoo_finance_url;
    }

    public void setYahoo_finance_url(String yahoo_finance_url) {
        this.yahoo_finance_url = yahoo_finance_url;
    }
}
