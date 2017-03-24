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
	
	public static void RecentBirths() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US35****\n");

		String IndiName = "";
		String birthday = "";
		
		int age = 0;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
//
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY') from individuals i";
		
		ResultSet rs = stmt.executeQuery(query);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		while (rs.next()) {
		IndiName = rs.getString(1); // Listing all individual's names
		birthday = rs.getString(2);// birthday
		String todaysDate =  java.time.LocalDate.now().toString();
		if(birthday!=null){
		Date todayDate  = new Date();
		todayDate = format.parse(todaysDate); //  parsing to proper format
		
		Date bDate  = new Date();
		bDate = format.parse(birthday);//  parsing to proper format
		int Recent = DatesCalc.getDiff(bDate.toString() ,todayDate.toString() ,DatesCalc.DAY); 
	
		if(Recent< 30)// display individual name is born in lst 30 days
		System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS35:\tIndividual " + IndiName + " born in last 30 days");
	  
	}	
}
		
         // else display no indivauls
		// System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS35:\t" +"No individual was born in last 30 days");
	}	// method to calculate the diffrence in days (recent birth >= 30 days)
}
