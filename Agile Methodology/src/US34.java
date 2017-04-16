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

public class US34 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	public static void ListAgeDifferences() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US34****\n");
		
		String marriage = "";
		String name1 = "";
		String name2 = "";
		String a1 = "";
		String a2 = "";
		int age1 = 0;
		int age2 = 0;
		String todaysDate =  java.time.LocalDate.now().toString();
		Date todayDate  = new Date();
		Date mDate  = new Date();
		int countAfter = 0;
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		// query to retrieve the related information
		
		String query = "with husband_age as ( select i.id,NULLIF(i.name,'') as h_name,i.age from individuals i, families f where i.id=f.husband_id ) "
					  +"select distinct f.famid,h.h_name,h.age as h_age,to_date(NULLIF(f.married,''), 'DD Mon YYYY'),f.wife_name as w_name, case "
					  +"when f.wife_id in (select id from individuals) then i.age end as \"w_age\" from families f, husband_age h, individuals i "
					  +"where f.husband_id = h.id and i.id=f.wife_id;";
		
		ResultSet rs = stmt.executeQuery(query);
	        
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		while (rs.next()) 
		{
			name1 = rs.getString(2); // Listing all individual's names
			a1 = rs.getString(3);
			name2 = rs.getString(5);
			a2 = rs.getString(6);
			marriage = rs.getString(4);// marriage
			
			age1 = Integer.parseInt(a1);
			age2 = Integer.parseInt(a2);
			
			todayDate = format.parse(todaysDate);
			
			//check valid dates and valid age
			if(marriage!=null && a1!=null && a2!=null && age1>=0 && age2>=0){
				mDate = format.parse(marriage); //  parsing to proper format		
				
				//find marriage occured how many years ago
				int diff = DatesCalc.getDiff(mDate.toString(), todayDate.toString() , DatesCalc.YEAR);
				
				age1 = age1 - diff;
				age2 = age2 - diff;
				
				Calendar mar = Calendar.getInstance();
				mar.setTime(mDate);
				
				Calendar now = Calendar.getInstance();
				now.setTime(todayDate);
				
				//check if elder spouse more than twice as old as the younger spouse
				//check if the marriage date is valid
				if(((2*age1 <= age2) || (2*age2 <= age1)) && mar.before(now)){
					countAfter++;
				    System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS34:\t" + name1 + " and " + name2 +
							 " is the couple who got married when the elder spouse was more than twice as old as the younger spouse");
				}
			}
		}
		if(countAfter==0)
			System.out.println(lineSeparator+"No couples found who have large age differences");
	}
}
