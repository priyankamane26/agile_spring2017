import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		Date birDt = new Date();
		Date deathDt = new Date();
		Date divDt = new Date();
		Date marDt = new Date();
		String famid="";
		
		

		int countBirth = 0, countDeath = 0, countMar = 0, countDiv = 0;

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Calendar today = Calendar.getInstance();

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		// fetch birth date, marriage date, death date, divorce date and name of
		// all individuals
		String query = "select i.name,to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'),"
				+ "to_date(NULLIF(f.married,''), 'DD Mon YYYY'),to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),"
				+ "f.famid, " + " case "
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
			famid=rs.getString(6);
			Spouse = rs.getString(7);

			// Commenting as according to requirements only Errors to be shown
			/*if (bdate == null) {
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
			}*/

			if (bdate != null) {
				birDt = format.parse(bdate);
				Calendar birthDate = Calendar.getInstance();
				birthDate.setTime(birDt);
				
				if (birthDate.after(today)) {
					countBirth++;
				GedcomParser.invalidBirthRecords.add(INDIName);	
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
					GedcomParser.invalidDeathRecords.add(INDIName);
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
					GedcomParser.invalidMarriageRecords.add(famid);
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
					GedcomParser.invalidDivorceRecords.add(famid);
					System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS01:\t" + "Divorce date (" + didate + ") of "
							+ INDIName + " and " + Spouse + " occurs in the future");
				}

			}

		}
		rs.close();

		if (countBirth == 0 && countDeath == 0 && countMar == 0 && countDiv == 0) {
			System.out.println(
					lineSeparator + "No individual has invalid birth date, death date, marriage date, or divorce date");
		}
		for(String indi: GedcomParser.invalidDeathRecords){
			String queryDeath = "Update Individuals set invalidDeathRecord ='Y' where name='"+indi+"'";
			stmt.executeUpdate(queryDeath);
		}
		for(String indi: GedcomParser.invalidBirthRecords){
			String queryBirth = "Update Individuals set invalidBirthRecord  ='Y' where name='"+indi+"'";
			stmt.executeUpdate(queryBirth);
		}
		for(String fam: GedcomParser.invalidMarriageRecords){
			String queryMarriage = "Update Families set invalidMarriageRecord  ='Y' where famid='"+fam+"'";
			stmt.executeUpdate(queryMarriage);
		}
		for(String fam: GedcomParser.invalidDivorceRecords){
			String queryDivorce = "Update Families set invalidDivorceRecord  ='Y' where famid='"+fam+"'";
			System.out.println(queryDivorce);
			stmt.executeUpdate(queryDivorce);
		}
		
		
	}
}
