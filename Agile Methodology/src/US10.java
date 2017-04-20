import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.sql.ResultSet;

//marraiage after 14
//both the father and the mother should be atleast 14 years of age to get married 
public class US10 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
       public static void Marriageafter14() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US10****\n");	
		
		String name = "";
		String mDate = "";
		String bDate= "";
		Date dob = new Date();
		Date dom = new Date();
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		
		String query =	"select i.name, to_date(NULLIF(f.married,''), 'DD Mon YYYY'), to_date(NULLIF(i.birthday,''), 'DD Mon YYYY') from families f, individuals i where ((i.id = f.husband_id) or (i.id = wife_id));";
				
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			 name = rs.getString(1);   // Listing names 
			 mDate = rs.getString(2);  // Listing marriage dates
			 bDate = rs.getString(3);  // Listing birth date
			 if (mDate!= null){
				 //System.out.println(mDate);
				 
				 DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					dob = format.parse(mDate);
					dom = format.parse(bDate);
			 	 int Recent = DatesCalc.getDiff(dom.toString() ,dob.toString() ,DatesCalc.YEAR); 		 
			 	// System.out.println(Recent);
			 
			 if(Recent< 14 ){
				 System.out.println(lineSeparator+ "ERROR:\tINDIVIDUAL:\tUS10:\t" +"Individual who married before the age of 14 is " + name);	 	 
				 
	        }
			 //else
				// System.out.println(lineSeparator+" Individual married after 14 years of age is "+ name );
		
			 }
}}
       }