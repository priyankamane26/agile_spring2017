
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class US22 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void Uniqueids() throws SQLException {
	System.out.println(lineSeparator + "\n****Start of US22****\n");

	String ids = " ";
    String count = "";
    
	con = JDBCConnect.getConnection();
	stmt = con.createStatement();
	String query = "select i.id,count(*) from Individuals i group by i.id UNION select f.famid,count(*) from Families f group by f.famid;";
	// System.out.println(query);

	ResultSet rs = stmt.executeQuery(query);
	while (rs.next()) {
		 ids = rs.getString(1); // Listing all ids both families and individuals 
		 count = rs.getString(2); //count of all ids
		int Count = Integer.parseInt(count);
		
		 if(Count >1) // check if count is more than 1 for ids
			 System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS27:\t" + " Ids " +  " is not unique "+lineSeparator);
		// else  
	} 
	// display ids as unique
	 System.out.println(lineSeparator +"All ids are unique");	
	}	
	}
