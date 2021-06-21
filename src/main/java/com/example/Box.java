package com.example;

public class Box {
    private String name;
    private String color;
    private String width;
    private String height;
    private String id;

    public String getId(){
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public String getWidth(){
        return this.width;
    }

    public String getHeight(){
        return this.height;
    }

    public void setId(String i){
        this.id = i;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setColor(String c) {
        this.color = c;
    }

    public void setWidth(String w){
        this.width = w;
    }

    public void setHeight(String h){
        this.height = h;
    }
}
