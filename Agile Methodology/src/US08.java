import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.sql.ResultSet;

public class US08 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void birthBeforeParentsMarriage() throws SQLException, ParseException {
		System.out.println(lineSeparator + "\n****Start of US08****\n");
		
		String name = "";
		String birth = "";
		String marriage = "";
		String isDiv = "";
		String divorce = "";
		Date bDate = new Date();
		Date mDate = new Date();
		Date divDate = new Date();
		Calendar birthDate = Calendar.getInstance();
		Calendar marriageDate = Calendar.getInstance();
		Calendar divorceDate = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		int noRecord = 0;
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
	
		//fetch records of children and corresponding parent details
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'), to_date(NULLIF(f.married,''), 'DD Mon YYYY'),"
					   +"f.divorced, to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY')"
					   +"from individuals i, families f where i.child=f.famid";
	
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {

			name = rs.getString(1); 
			birth = rs.getString(2);
			marriage = rs.getString(3);
			isDiv = rs.getString(4);
			divorce = rs.getString(5);
			
			//birth date and marriage date should not be null
			if(birth != null && marriage != null){
				bDate = format.parse(birth);
				birthDate.setTime(bDate);
			
				mDate = format.parse(marriage);
				marriageDate.setTime(mDate);
				
				//born before marriage of parents
				if(birthDate.before(marriageDate))
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS08:\tIndividual " + name + " born before marriage of parents");
			
				//if divorced
				if ("t".equals(isDiv) && divorce != null){
					divDate = format.parse(divorce);
					divorceDate.setTime(divDate);
					//if birth date after divorce date
					if(birthDate.after(divorceDate)){
						//calculate number of months between two dates using getDiff()
						int months = DatesCalc.getDiff(divDate.toString(), bDate.toString(), DatesCalc.MONTH); //formatted strings as parameters
						//if birth date more than 9 months after divorce date
						if(months>9){
							noRecord++;
							System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS08:\tIndividual " + name + " born more than 9 months after divorce of parents");
						}
					}
				}	
			}
		}
		if(noRecord == 0)
			System.out.println(lineSeparator + "No individual has birth date before marriage date of parents");
	}
}