package bestProfessor.bestProfessor;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


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

    public void schoolOptions(Query query){
        String url = query.url();
        System.out.println(url);
        try{
            Document doc = Jsoup.connect("https://www.ratemyprofessors.com/search/schools?q=drexel").get();
            // Elements schools  = doc.select("div.SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0 bAQoPm");
            String title = doc.title();
            System.out.println(title);
            // for (Element el : schools) {
            //     System.out.print("HIHIHIHI");
            //     String text = el.text();
            //     System.out.println(text);
            // }
        }catch(IOException e) {
            e.printStackTrace();
            System.out.println("noo");
        }
    }
    // String element = "<div class=\"SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0 bAQoPm\">West Chester University of Pennsylvania</div>";
    
    public static void main(String[] args) {
        Query q = new Query("drexel");
        q.search(false);

        School s = new School();
        s.schoolOptions(q);

    }
}
