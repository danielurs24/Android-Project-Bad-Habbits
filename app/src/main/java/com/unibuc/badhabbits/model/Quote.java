package com.unibuc.badhabbits.model;

public class Quote {
    private String q; // Quote text
    private String a; // Author

    public Quote(String q, String a) {
        this.q = q;
        this.a = a;
    }

    public String getQuoteText() {
        return q;
    }

    public String getAuthor() {
        return a;
    }
}