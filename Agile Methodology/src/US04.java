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
		String name1 = "";
		String name2 = "";
		Date mDate = new Date();
		Date divDate = new Date();
		Calendar marriageDate = Calendar.getInstance();
		Calendar divorceDate = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select DISTINCT ON (f.famid) famid, i.name, to_date(NULLIF(f.married,''), 'DD Mon YYYY'), to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY')," 
						+ "case"
						+ "when f.husband_id = i.id then (select i.name from individuals i where i.id=f.wife_id)" 
						+ "when f.wife_id  = i.id then (select i.name from individuals i where i.id = f.husband_id) end" 
						+ "from families f, individuals i where ((i.id = f.husband_id) or (i.id = wife_id)) and f.divorced = true;";

		// System.out.println(query);
		
		ResultSet rs = stmt.executeQuery(query);
		
		while(rs.next()){
			marriage = rs.getString();
			divorce = rs.getString();
		}
		
	}
}
