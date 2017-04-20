import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
//import java.util.GregorianCalendar;
import java.util.Locale;

public class US35 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "============================================================================\n";

	@SuppressWarnings("deprecation")
	public static void RecentBirths() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US35****\n");

		System.out.println(lineSeparator + "List of Individuals who's birthday's were in Last 30 days\n");

		System.out.format("%-10s%2s%-10s", "Individual", " ", "Birth Date");
		System.out.println();
		System.out.format("%-10s%2s%-10s", "==========", " ", "==========");
		System.out.println();

		String IndiName = "";
		String birthday = "";
		String todaysDate = java.time.LocalDate.now().toString();
		Date todayDate = new Date();
		Date bDate = new Date();
		int dateDiff=0;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		//
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY') from individuals i";
		//System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			birthday = rs.getString(2);// birthday
			
		
			if (birthday != null) {
				
				todayDate = format.parse(todaysDate); // parsing to proper format
				bDate = format.parse(birthday);// parsing to proper format
				
				dateDiff=(todayDate.getDate()-bDate.getDate());
				
					if(todayDate.getMonth()==bDate.getMonth() && dateDiff<=30 && dateDiff > 0){
						System.out.format("%-10s%2s%-10s", IndiName, " ", birthday);
						System.out.println();
					}
				

			}
		}

	} // method to calculate the diffrence in days (recent birth >= 30 days)
}
