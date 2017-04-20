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
//list upcomming birthdays
//List all living people in a GEDCOM file whose birthdays occur in the next 30 days

public class US38 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void UpcommingBirthdays() throws SQLException, ParseException {

		System.out.println(lineSeparator + "\n****Start of US38****\n");

		String IndiName = "";
		String birthday = "";
		int countAfter = 0;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		// query to retrieve the related information
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY') from individuals i where alive = true";

		ResultSet rs = stmt.executeQuery(query);

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		while (rs.next()) {
			IndiName = rs.getString(1); // Listing all individual's names
			birthday = rs.getString(2);// birthday
			String todaysDate = java.time.LocalDate.now().toString();
			Date todayDate = new Date();
			todayDate = format.parse(todaysDate);

			if (birthday != null) {

				// parsing to proper format

				Date bDate = new Date();
				bDate = format.parse(birthday); // parsing to proper format

				// int yeardiff = DatesCalc.getDiff(todayDate.toString(),
				// bDate.toString(), DatesCalc.YEAR);
				// System.out.println(yeardiff);

				Calendar c = Calendar.getInstance();
				c.setTime(bDate);
				c.set(Calendar.YEAR, 2017);
				Date newDate = c.getTime();
				// System.out.println(newDate);

				// newDate= format.parse(newDate);

				if (newDate.after(todayDate)) {

					// System.out.println(IndiName);

					// int recent =
					// dateadd(,datediff(year,bDate,todayDate),bDate);
					int Recent = DatesCalc.getDiff(newDate.toString(), todayDate.toString(), DatesCalc.DAY);

					if (Recent <= 30) { // display individual if his birthday is
										// in next 30 days
						countAfter++;
						System.out.println(lineSeparator + "INFO:    INDIVIDUAL:    US38:   " + IndiName
								+ " has his/her birthday in next 30 days, Happy birthday to him / her in advance!!");
					}
				}
			}
		}
		if (countAfter == 0)
			System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS38:\t"
					+ "No individual has his/her birthday in next 30 days");
	} // method to calculate the diffrence in days (recent < 30 days)
}