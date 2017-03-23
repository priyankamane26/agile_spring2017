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

public class US02 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getBirthBeforeMarriage() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US02****\n");
		
		String bdate = "";
		String mdate = "";
		String Spouse = "";
		String INDIName = "";
		int countAfter = 0;
		Date dob = new Date();
		Date dom = new Date();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		// fetch all marriage date, birth date and names of all individuals who
		// are married
		String query = "select to_date(NULLIF(f.married,''),'DD Mon YYYY'), to_date(NULLIF(i.birthday,''),'DD Mon YYYY'), i.name, "
				+ " case " + " when  f.husband_id =i.id then (select i.name from individuals i where i.id=f.wife_id) "
				+ " when  f.wife_id  =i.id then (select i.name from individuals i where i.id=f.husband_id) " + " end "
				+ " from families f,individuals i" + " where ((f.husband_id=i.id) OR (f.wife_id=i.id));";

		// System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			mdate = rs.getString(1);
			bdate = rs.getString(2);
			INDIName = rs.getString(3);
			Spouse = rs.getString(4);

			// Fetch names of individuals with missing values
			// Commenting as according to requirements only Errors to be shown
			/*if (bdate == null && mdate != null) {
				System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS27:\t" + "Birthdate date of an Individual "
						+ INDIName + " married to " + Spouse + " is not available to compare against Marriage date:"
						+ mdate);
			}*/

			// Fetch names of individuals with invalid values
			if (bdate != null && mdate != null) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				dob = format.parse(bdate);
				dom = format.parse(mdate);

				// logic to check whether the birth date is greater than
				// marriage date
				Calendar birth = Calendar.getInstance();
				Calendar marriage = Calendar.getInstance();

				birth.setTime(dob);
				marriage.setTime(dom);

				if (birth.after(marriage)) {
					countAfter++;
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS02:\t" + INDIName + " was born on ("
							+ bdate + ") after marriage (" + mdate + ")");
				}
			}
		}
		rs.close();

		if (countAfter == 0) {
			System.out.println(lineSeparator + "No individual has birth date before marriage date");
		}
	}
}
