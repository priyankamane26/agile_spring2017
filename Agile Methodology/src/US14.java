import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class US14 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void multipleBirths() throws SQLException, ParseException {
		System.out.println(lineSeparator + "\n****Start of US14****\n");
		
		int count = 0;
		String birth = "";
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
	
		//fetch individual(s) birth dates and the count of occurrence of each birth date in table 
		String query = "select to_date(NULLIF(birthday,''), 'DD Mon YYYY'),count(*) from individuals group by birthday;";
	
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			birth = rs.getString(1);
			count = rs.getInt(2);
			
			//birth date should not be null
			if(birth!=null){
				//if more than 5 siblings born at same date
				if(count>5)
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS14:\tIndividuals born on " + birth + " have more than 5 siblings born at the same time");
			}
		}
	}
}