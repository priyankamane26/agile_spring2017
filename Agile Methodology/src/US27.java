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

public class US27 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getDivAfterDeathINDI() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US27****\n");
		
		String div = "";
		String death = "";
		String alive = "";
		String isDivorced = "";
		String Spouse="";
		String INDIName = "";
		int countAfter = 0;
		Date divDt = new Date();
		Date deathDt = new Date();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select distinct to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'), i.name, f.divorced, i.alive, "
				+ " case "
				+ " when  f.husband_id =i.id then (select i.name from individuals i where i.id=f.wife_id) "
				+ " when  f.wife_id  =i.id then (select i.name from individuals i where i.id=f.husband_id) "
				+ " end "
				+ " from families f, individuals i"
				+ " where ((i.id = f.husband_id) or  (i.id=wife_id))";
		 //System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			div = rs.getString(1);
			death = rs.getString(2);
			INDIName = rs.getString(3);
			isDivorced = rs.getString(4);
			alive = rs.getString(5);
			Spouse=rs.getString(6);
			
			// Commenting as according to requirements only Errors to be shown
			/*if ("f".equals(alive) && "f".equals(isDivorced)) {
				System.out.println(lineSeparator + "INFO:\tFAMILY:\tUS27:\t" + "Divorce date of an Individual "
						+ INDIName +" married to "+Spouse+" is not available to compare against death date:" + death);
			}*/

			if ("f".equals(alive) && "t".equals(isDivorced)) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				divDt = format.parse(div);
				deathDt = format.parse(death);

				// System.out.println(divDt);

				// logic to check whether the divorce date is greater than death
				// date
				Calendar divorceDate = Calendar.getInstance();
				Calendar deathDate = Calendar.getInstance();

				divorceDate.setTime(divDt);
				deathDate.setTime(deathDt);

				if (divorceDate.after(deathDate)) {
					countAfter++;
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS27:\t" + INDIName + " got divorced("
							+ div + ") after death (" + death + ")");
				}

			}

		}
		if(countAfter>0)
			System.out.println(lineSeparator+"No individual has divorce date after death date");
		rs.close();
	}

}
