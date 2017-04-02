import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class US18 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getMarriedSiblings() throws SQLException{
		System.out.println(lineSeparator+"\n****Start of US18****\n");

		String husband = "";
		String wife = "";
		String famh = "";
		String famw = "";
		int countAfter = 0;
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		
		String query = 	"select DISTINCT ON (f.famid) famid, f.husband_name, f.wife_name, NULLIF(i.child,''), "+ 
						"case "+
						"when f.husband_id = i.id then (select i.child from individuals i where i.id=f.wife_id) "+
						"when f.wife_id  = i.id then (select i.child from individuals i where i.id = f.husband_id) end "+ 
						"from families f, individuals i where ((i.id = f.husband_id) or (i.id = wife_id));";
		
		ResultSet rs = stmt.executeQuery(query);
		
		while(rs.next()){
			husband = rs.getString(2);
			wife = rs.getString(3);
			famh = rs.getString(4);
			famw = rs.getString(5);
			
			if(famh!=null && famw!=null){
			if(famh.equals(famw)){
				countAfter++;
				System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS18:\t" + "Siblings: " + husband
						+ " and " + wife + " cannot be married");
			
			}
			}
		}
		if(countAfter==0)
			System.out.println(lineSeparator+"No married siblings were found");
	}
}