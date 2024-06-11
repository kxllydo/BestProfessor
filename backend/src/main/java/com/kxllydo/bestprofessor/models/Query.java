package com.kxllydo.bestprofessor.models;

public class Query {
    private String query;
    private String url;
    
    public Query(String search){
        this.query = search.toLowerCase();
        this.url = "https://www.ratemyprofessors.com/";
    }

    private String convertSearchQuery(){
        String[] splitSearch = query.split(" ");
        String last = "";

        for (int i = 0; i < splitSearch.length - 1; i++){
            last += splitSearch[i] + "%20";
        }
        last += splitSearch[splitSearch.length - 1];
        return last;
    }

    public void search(boolean professor){
        String converted = convertSearchQuery();
        if (!professor){
            url += "search/schools?q=" + converted;
        }else{
            url += "search/professors/15866?q=" + converted;
        }
    }

    public void printUrl(){
        System.out.println(url);
    }

    public String url(){
        return this.url;
    }

	public static void main(String[] args) {
        Query hi = new Query("Mark Body");
        hi.search(true);
        hi.printUrl();
    }
}