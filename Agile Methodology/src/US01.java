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
		
		System.out.println(lineSeparator+"\n****Start of US01, US02, US03, US05, US27****\n");
		
		String bdate = "";
		String dedate = "";
		String mdate = "";
		String didate = "";
		String indiID="";
		String INDIName = "";
		String Spouse = "";
		String famid="";
		boolean noRecords=false;
		

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date = new Date();
		//System.out.println(dateFormat.format(date));
		String today=dateFormat.format(date);
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		// fetch birth date, marriage date, death date, divorce date and name of
		// all individuals
		String query = "select i.id, f.famid, i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'),"
				+ "to_date(NULLIF(f.married,''), 'DD Mon YYYY'),to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),"
				+ " case "
				+ " when  f.husband_id =i.id then (select i.name from individuals i where i.id=f.wife_id) "
				+ " when  f.wife_id  =i.id then (select i.name from individuals i where i.id=f.husband_id) " + " end "
				+ " from families f RIGHT OUTER JOIN individuals i ON ((i.id = f.husband_id) or (i.id=wife_id));";

		//System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			indiID=rs.getString(1);
			famid=rs.getString(2);
			INDIName = rs.getString(3);
			bdate = rs.getString(4);
			dedate = rs.getString(5);
			mdate = rs.getString(6);
			didate = rs.getString(7);
			Spouse = rs.getString(8);

			if (indiID == null) {
				noRecords = true;
			}
			
			if (bdate != null) {
				
				if (GedcomParser.dateValidator(bdate, today, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);	
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS01:\t" + "Birthdate (" + bdate + ") of "
							+ INDIName + " occurs in the future");
				}
			}
			
			if(dedate != null){
				
				if (GedcomParser.dateValidator(dedate, today, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS01:\t" + "Death date (" + dedate
							+ ") of " + INDIName + " occurs in the future");
				}
			}
			
			if(mdate != null){
				
				if (GedcomParser.dateValidator(mdate, today, "After")) {	
					//GedcomParser.invalidFamilyRecord.add(famid);
					System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS01:\t" + "Marriage date (" + mdate
							+ ") of couple " + INDIName + " and " + Spouse + " occurs in the future");
				}
			}
			
			if(didate != null){
				if (GedcomParser.dateValidator(didate, today, "After")) {
					//GedcomParser.invalidFamilyRecord.add(famid);
					System.out.println(lineSeparator + "ERROR:\tFAMILY:\tUS01:\t" + "Divorce date (" + didate + ") of "
							+ INDIName + " and " + Spouse + " occurs in the future");
				}
			}
			
			if(bdate !=null && mdate != null){
				if (GedcomParser.dateValidator(bdate, mdate, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS02:\t" + INDIName + " was born on ("
						+ bdate + ") after marriage (" + mdate + ")");
				}
			}
			
			if(bdate != null && dedate != null){
				if (GedcomParser.dateValidator(bdate, dedate, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator +"ERROR:\tINDIVIDUAL:\tUS03:\t" + "Individual " + INDIName + " died on (" + dedate
							+ ") before their birth date (" + bdate+")");
				}
			
			}
			
			if(mdate !=null && dedate!=null){
				if (GedcomParser.dateValidator(mdate, dedate, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS05:\t" + INDIName + " got married(" + mdate
							+ ") after death (" + dedate + ")");
				}
			}
			
			if(didate != null && dedate != null){
				if (GedcomParser.dateValidator(didate, dedate, "After")) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS27:\t" + INDIName + " got divorced("
							+ didate + ") after death (" + dedate + ")");
				}
			}
			
			

		}
		rs.close();

		if (noRecords) {
			System.out.println(
					lineSeparator + "No individual has invalid birth date, death date, marriage date, or divorce date");
		}
		
		// Seting flag for invalid records
		/*for(String indi: GedcomParser.invalidIndividualRecord){
			String queryDeath = "Update Individuals set invalidRecord ='Y' where id='"+indi+"'";
			stmt.executeUpdate(queryDeath);
		}
		for(String fam: GedcomParser.invalidFamilyRecord){
			String queryBirth = "Update Families set invalidRecord  ='Y' where famid='"+fam+"'";
			stmt.executeUpdate(queryBirth);
		}
		*/
		
		
	}
}
