package com.example.yesterday.yesterday.RecyclerView;

public class FoodItem {
    String food, date, BLDtime;

    public FoodItem(String food, String date, String BLDtime){
        this.food  = food;
        this.date = date;
        this.BLDtime = BLDtime;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBLDtime() {
        return BLDtime;
    }

    public void setBLDtime(String BLDtime) {
        this.BLDtime = BLDtime;
    }
}
