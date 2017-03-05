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

public class US01 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	

	public static void getDatesBeforeCurrentDate() throws SQLException, ParseException {
		
		System.out.println(lineSeparator+"\n****Start of US01****\n");
		
		String bdate = "";
		String dedate = "";
		String mdate = "";
		String didate = "";
		String INDIName = "";
		String Spouse = "";
		String currDate = "";
		Date birDt = new Date();
		Date deathDt = new Date();
		Date divDt = new Date();
		Date marDt = new Date();

		int countBirth = 0, countDeath = 0, countMar = 0, countDiv = 0;

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Calendar today = Calendar.getInstance();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		// fetch birth date, marriage date, death date, divorce date and name of
		// all individuals
		String query = "select i.name,to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'),"
				+ "to_date(NULLIF(f.married,''), 'DD Mon YYYY'),to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),"
				+ "current_date, " + " case "
				+ " when  f.husband_id =i.id then (select i.name from individuals i where i.id=f.wife_id) "
				+ " when  f.wife_id  =i.id then (select i.name from individuals i where i.id=f.husband_id) " + " end "
				+ " from families f RIGHT OUTER JOIN individuals i ON ((i.id = f.husband_id) or (i.id=wife_id));";

		//System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			INDIName = rs.getString(1);
			bdate = rs.getString(2);
			dedate = rs.getString(3);
			mdate = rs.getString(4);
			didate = rs.getString(5);
			currDate = rs.getString(6);
			Spouse = rs.getString(7);

			if (bdate == null) {
				System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS01:\t" + "Birthdate of an Individual "
						+ INDIName + " is not available to compare against today's date:" + currDate);
			}
			if (dedate == null) {
				System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS01:\t" + "Death date of an Individual "
						+ INDIName + " is not available to compare against today's date:" + currDate);
			}
			if (mdate == null) {
				System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS01:\t" + "Marriage date of an Individual "
						+ INDIName + " is not available to compare against today's date:" + currDate);
			}
			if (didate == null) {
				System.out.println(lineSeparator + "INFO:\tINDIVIDUAL:\tUS01:\t" + "Divorce date of an Individual "
						+ INDIName + " is not available to compare against today's date:" + currDate);
			}

			if (bdate != null) {
				birDt = format.parse(bdate);
				Calendar birthDate = Calendar.getInstance();
				birthDate.setTime(birDt);

				if (birthDate.after(today)) {
					countBirth++;
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS01:\t" + "Birthdate (" + bdate + ") of "
							+ INDIName + " occurs in the future");
				}

			}

			if (dedate != null) {
				deathDt = format.parse(dedate);
				Calendar deathDate = Calendar.getInstance();
				deathDate.setTime(deathDt);

				if (deathDate.after(today)) {
					countDeath++;
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS01:\t" + "Death date (" + dedate
							+ ") of " + INDIName + " occurs in the future");
				}

			}

			if (mdate != null) {
				marDt = format.parse(mdate);
				Calendar marriageDate = Calendar.getInstance();
				marriageDate.setTime(marDt);

				if (marriageDate.after(today)) {
					countMar++;
					System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS01:\t" + "Marriage date (" + mdate
							+ ") of couple" + INDIName + " and " + Spouse + " occurs in the future");
				}

			}

			if (didate != null) {
				divDt = format.parse(didate);
				Calendar divorceDate = Calendar.getInstance();
				divorceDate.setTime(divDt);

				if (divorceDate.after(today)) {
					countDiv++;
					System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS01:\t" + "Divorce date (" + didate + ") of "
							+ INDIName + " and " + Spouse + " occurs in the future");
				}

			}

		}
		rs.close();

		if (countBirth == 0 && countDeath == 0 && countMar == 0 && countDiv == 0) {
			// JOptionPane.showMessageDialog(null,"No individual has invalid
			// birth date, death date, marriage date, or divorce date",
			// "Result",JOptionPane.INFORMATION_MESSAGE);
			System.out.println(
					lineSeparator + "No individual has invalid birth date, death date, marriage date, or divorce date");
		}
		
	}
}
