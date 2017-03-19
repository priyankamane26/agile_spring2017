import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class US29 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void listOfDeseased() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US29****\n");
		
		System.out.println(lineSeparator +"List of Deceased\n");
		System.out.format("%-10s%2s%-10s","Individual"," ","Death Date");
		System.out.println();
		System.out.format("%-10s%2s%-10s","=========="," ","==========");
		System.out.println();
		
		String IndiName = "";
		String deathDate = "";
		String invalidDeath="";
		
		
		boolean noRecords=false;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select distinct i.name, i.death, i.invalidDeathRecord  " + " from individuals i " + " where" + " alive=false";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			deathDate = rs.getString(2); // Their death dates
			invalidDeath=rs.getString(3);

			if (IndiName == null || IndiName == "") {
				noRecords=true;
			} else {
				if("N".equals(invalidDeath)){
					System.out.format("%-10s%2s%-10s",IndiName," ",deathDate);
					System.out.println();
				}
				
			}
		}
		rs.close();
		if(noRecords){
			System.out.println(lineSeparator + "No records found!");
		}

	}
}
