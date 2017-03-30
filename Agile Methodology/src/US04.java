import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class US04 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getMarriageBeforeDivorce() throws SQLException{
		System.out.println(lineSeparator+"\n****Start of US04****\n");

		String marriage = "";
		String divorce = "";
		Date mDate = new Date();
		Date divDate = new Date();
		Calendar marriageDate = Calendar.getInstance();
		Calendar divorceDate = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = " select i.name, to_date(NULLIF(f.married,''), 'DD Mon YYYY'), to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY')" + 
					   "from families f, individuals i where f.divorced = true and ((i.id = f.husband_id) or (i.id=wife_id));";
		// System.out.println(query);
		
		ResultSet rs = stmt.executeQuery(query);
		
		while(rs.next()){
			
		}
		
	}
}
