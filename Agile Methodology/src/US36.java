import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class US36 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void listOfDeseased() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US36****\n");
		
		System.out.println(lineSeparator +"List of Individuals who died in Last 30 days\n");
		
		System.out.format("%-10s%2s%-10s","Individual"," ","Death Date");
		System.out.println();
		System.out.format("%-10s%2s%-10s","=========="," ","==========");
		System.out.println();
		
		String IndiName = "";
		String deathDate = "";
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String todaysDate =  java.time.LocalDate.now().toString();
		Date todayDate  = new Date();
		todayDate = format.parse(todaysDate);
		
				
		Date dDate  = new Date();
		
		boolean noRecords=false;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select distinct i.name, to_date(NULLIF(i.death,''), 'DD Mon YYYY') " + " from individuals i " + " where" + " alive=false";
		//System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			deathDate = rs.getString(2); // Their death dates
			
			
			dDate = format.parse(deathDate); 
			
			
			if (IndiName == null || IndiName == "") {
				noRecords=true;
			} else {
				if(DatesCalc.getDiff(todayDate.toString(),dDate.toString(), DatesCalc.DAY) <= 30){
					System.out.format("%-10s%2s%-10s",IndiName," ",deathDate);
					System.out.println();
				}
				
			}
		}
		rs.close();
		if(noRecords){
			System.out.println(lineSeparator + "No records found!");
		}
		System.out.println("\nTodays Date - "+todayDate);
	}
}
