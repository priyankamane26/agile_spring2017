import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
//Living Single
 
public class US31 {
        static Connection con = null;
        static Statement stmt = null;
        static String lineSeparator = "======================================================================="
                       + "===========================================================================\n";
 
        public static void listLivingSingle() throws SQLException, ParseException {
 
                System.out.println(lineSeparator + "\n****Start of US31****\n");
               
                System.out.println(lineSeparator +"List Living Single \n");
                System.out.format("Individual");
                System.out.println();
                System.out.format("==========");
                System.out.println();
               
                String IndiName = "";
                String spouse = "";
                int countAfter = 0;

                con = JDBCConnect.getConnection();
                stmt = con.createStatement();
 
                String query = "select i.name,NULLIF(i.spouse,'') from individuals i where alive = true and i.age > 30 ";

                // System.out.println(query);
 
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
 
                       IndiName = rs.getString(1); // Listing all individual's names who are single and above 30
                       spouse = rs.getString(2);
                       
                       if (spouse == null){
                    	   countAfter++;
                           System.out.format(IndiName);
                           System.out.println();
                           }       
                       }
                rs.close();
                if(countAfter == 0){
                       System.out.println(lineSeparator + "No records found!");
                }
 
        }
} 
