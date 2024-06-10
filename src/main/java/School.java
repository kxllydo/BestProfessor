import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class School {
    private String name;
    private int id;

    public School (){
        this.name = "";
        this.id = 0;
    }

    public void setName (String schoolName){
        name = schoolName;
    }

    public void setId (int schoolId){
        id = schoolId;
    }

    
}
