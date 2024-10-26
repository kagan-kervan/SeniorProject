package org.example;

import java.util.ArrayList;

public class InputRoot {
    ArrayList<StockInput> root;

    public InputRoot(ArrayList<StockInput> stockInputs) {
        this.root = stockInputs;
    }

    public ArrayList<StockInput> getStockInputs() {
        return root;
    }

    public void setStockInputs(ArrayList<StockInput> stockInputs) {
        this.root = stockInputs;
    }
}
