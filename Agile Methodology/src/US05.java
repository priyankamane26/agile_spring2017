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

public class US05 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getMarriageAfterDeath() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US05****\n");
		
		String mar = "";
		String death = "";
		String INDIName = "";
		String Spouse = "";
		int countAfter = 0;
		Date marDt = new Date();
		Date deathDt = new Date();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select to_date(NULLIF(f.married,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'), i.name, i.alive, "
				+ " case " + " when  f.husband_id =i.id then (select i.name from individuals i where i.id=f.wife_id) "
				+ " when  f.wife_id  =i.id then (select i.name from individuals i where i.id=f.husband_id) " + " end "
				+ " from families f, individuals i" + " where ((i.id = f.husband_id) or  (i.id=f.wife_id))";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			mar = rs.getString(1);
			death = rs.getString(2);
			INDIName = rs.getString(3);
			Spouse = rs.getString(5);

			if (mar == null && death != null) {

				System.out.println(lineSeparator + "INFO:\tFAMILY:\tUS05:\t" + "Marriage date of an Individual "
						+ INDIName + " with Spouse " + Spouse + " is not available to compare against death date ("
						+ death+")");

			}

			if (mar != null && death != null) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				marDt = format.parse(mar);
				deathDt = format.parse(death);

				// System.out.println(divDt);

				// logic to check whether the marriage date is greater than
				// death date
				Calendar marriageDate = Calendar.getInstance();
				Calendar deathDate = Calendar.getInstance();

				marriageDate.setTime(marDt);
				deathDate.setTime(deathDt);

				if (marriageDate.after(deathDate)) {
					countAfter++;
					// throw new IllegalArgumentException("Invalid marriage
					// date!");
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS27:\t" + INDIName + " got married(" + mar
							+ ") after death (" + death + ")");
				}
			}

		}
		rs.close();

		if (countAfter == 0) {
			System.out.println(lineSeparator + "No individual has invalid marriage date");
		}
	}
}
