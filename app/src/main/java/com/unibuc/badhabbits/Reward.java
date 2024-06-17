package com.unibuc.badhabbits;

public class Reward {
    private String name;
    private int requiredDays;
    private boolean isCollected;

    public Reward(String name, int requiredDays) {
        this.name = name;
        this.requiredDays = requiredDays;
        this.isCollected = false;
    }

    public String getName() {
        return name;
    }

    public int getRequiredDays() {
        return requiredDays;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}


