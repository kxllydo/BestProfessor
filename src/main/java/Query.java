
public class Query {
    private String search;
    private String query;
    
    public Query(String search){
        this.search = search.toLowerCase();
        this.query = null;
    }

    public void searchToQuery(){
        String[] splitSearch = search.split(" ");
        String last = "";

        for (int i = 0; i < splitSearch.length - 1; i++){
            last += splitSearch[i] + "%20";
        }
        last += splitSearch[splitSearch.length - 1];
        query = "q=" + last;
    }

    public void query(){
        System.out.println(query);
    }

	public static void main(String[] args) {
        Query hi = new Query("Drexel Univ");
        hi.searchToQuery();
        hi.query();
    }
}