import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class US39 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void UpcomingAnniversaries() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US39****\n");
		
		String marriage = "";
		String name1 = "";
		String name2 = "";

		int countAfter = 0;
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
// query to retrieve the related information
        String query = "select DISTINCT ON (f.famid) famid, i.name, to_date(NULLIF(f.married,''), 'DD Mon YYYY'), case "
        			  +"when f.husband_id = i.id then (select NULLIF(i.name,'') from individuals i where i.id=f.wife_id and i.alive = true) "
        			  +"when f.wife_id  = i.id then (select NULLIF(i.name,'') from individuals i where i.id = f.husband_id and i.alive = true) end " 
        			  +"from families f, individuals i where ((i.id = f.husband_id) or (i.id = wife_id)) and f.divorced = true and i.alive = true;";

        ResultSet rs = stmt.executeQuery(query);
        
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		while (rs.next()) 
		{
			name1 = rs.getString(2); // Listing all individual's names
			name2 = rs.getString(4);
			marriage = rs.getString(3);// birthday
			String todaysDate =  java.time.LocalDate.now().toString();
			Date todayDate  = new Date();
			todayDate = format.parse(todaysDate);
			
			//if alive, names should not be null
			if(marriage!=null && name1!=null && name2!=null){
				Date mDate  = new Date();
				mDate = format.parse(marriage); //  parsing to proper format		
				
				Calendar c = Calendar.getInstance();
				c.setTime(mDate);
				c.set(Calendar.YEAR, 2017); // set to present year to check anniversary
				Date newDate = c.getTime();
				
				if (newDate.after(todayDate)) {	
					int Recent = DatesCalc.getDiff(newDate.toString(), todayDate.toString() , DatesCalc.DAY); 
					
				    if(Recent <= 30)
				    { // display individual if his anniversary is in next 30 days
				    countAfter++;
				    System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS39:\t" + name1 + " and " + name2 +
							 " are a living couple and have their anniversary in the next 30 days");
				    }
				}			
			}
		}
		if(countAfter==0)
			System.out.println(lineSeparator+"No living couple found whose anniversary is in the next 30 days");
	}
}
