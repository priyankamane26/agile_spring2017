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

public class US03 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getBirthAfterDeath() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US03****\n");
		
		String birth = "";
		String death = "";
		String INDIName = "";
		int countAfter = 0;
		Date birthDt = new Date();
		Date deathDt = new Date();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select to_date(NULLIF( individuals.birthday,''), 'DD Mon YYYY'),to_date(NULLIF( individuals.death,''), 'DD Mon YYYY'), individuals.name from individuals "
				+ "where individuals.alive=false ";
		//System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			birth = rs.getString(1);
			death = rs.getString(2);
			INDIName = rs.getString(3);

			if (birth != null && death != null) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				birthDt = format.parse(birth);
				deathDt = format.parse(death);

				// System.out.println(divDt);

				// logic to check whether the marriage date is greater than
				// death date
				Calendar birthDate = Calendar.getInstance();
				Calendar deathDate = Calendar.getInstance();

				birthDate.setTime(birthDt);
				deathDate.setTime(deathDt);

				if (birthDate.after(deathDate)) {
					countAfter++;
					System.out.println(lineSeparator +"INFO:\tINDIVIDUAL:\tUS03:\t" + "Individual " + INDIName + " died on (" + death
							+ ") before their birth date (" + birth+")");
				}
			}

		}
		rs.close();

		if (countAfter == 0) {
			// JOptionPane.showMessageDialog(null,"No individual has invalid
			// birth date", "Result",JOptionPane.INFORMATION_MESSAGE);
			System.out.println(lineSeparator + "No individual has invalid birth date");
		}
	}
}
