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

public class US08 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void birthBeforeParentsMarriage() throws SQLException, ParseException {
		System.out.println(lineSeparator + "\n****Start of US08****\n");
		
		String name = "";
		String birth = "";
		String marriage = "";
		String isDiv = "";
		String divorce = "";
		String indiID="";
		String invalidINDIRecord="";
		String invalidFAMRecord="";
		Date bDate = new Date();
		Date divDate = new Date();
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
	
		//fetch records of children and corresponding parent details
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'), to_date(NULLIF(f.married,''), 'DD Mon YYYY'),"
					   +"f.divorced, to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'), i.id, i.invalidRecord,f.invalidRecord "
					   +"from individuals i, families f where i.child=f.famid";
	
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {

			name = rs.getString(1); 
			birth = rs.getString(2);
			marriage = rs.getString(3);
			isDiv = rs.getString(4);
			divorce = rs.getString(5);
			indiID=rs.getString(6);
			invalidINDIRecord=rs.getString(7);
			invalidFAMRecord=rs.getString(8);
			//birth date and marriage date should not be null
			if(birth != null && marriage != null && "N".equals(invalidINDIRecord) && "N".equals(invalidFAMRecord)){
				
				//born before marriage of parents
				if(GedcomParser.dateValidator(birth, marriage, "Before")){
					GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS08:\tIndividual " + name + " born before marriage of parents");
				}
					
			
				//if divorced
				if ("t".equals(isDiv) && divorce != null){
					
					//if birth date after divorce date
					if(GedcomParser.dateValidator(birth, divorce, "After")){
						//calculate number of months between two dates using getDiff()
						int months = DatesCalc.getDiff(divDate.toString(), bDate.toString(), DatesCalc.MONTH); //formatted strings as parameters
						//if birth date more than 9 months after divorce date
						if(months>9){
							GedcomParser.invalidIndividualRecord.add(indiID);
							System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS08:\tIndividual " + name + " born more than 9 months after divorce of parents");
						}
							
					}
				}
			}
		}
		for(String indi: GedcomParser.invalidIndividualRecord){
			String queryDeath = "Update Individuals set invalidRecord ='Y' where id='"+indi+"'";
			stmt.executeUpdate(queryDeath);
		}
	}
}