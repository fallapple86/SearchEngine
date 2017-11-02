package com.src.searchengine;
/**
 * Created by qpan on 11/9/2016.
 */


public class ResultElement {
    public String Name;
    public String Url;
    public String Matches;
    private String root;
    public float TermFrequency;

    public ResultElement(String name){
        root = "http://www.csce.uark.edu/~sgauch/5533/files/";
        Name = name.substring(0, name.lastIndexOf('.'));
        Url = root + Name + ".html";
        Matches = "";
        TermFrequency = 0;
    }
}
