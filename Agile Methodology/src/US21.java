import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class US21 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void correctGenderForRole() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US21****\n");

		String famID = "";
		String husband_name="";
		String husband_gender = "";
		String wife_name = "";
		String wife_gender="";
	
	
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = " WITH maleMember AS( "
				+ " select"
				+ " f.famid, f.husband_name, i.gender as \"husband_gender\" "
				+ " from individuals i, families f "
				+ " where"
				+ " i.id=f.husband_id "
				+ " and "
				+ " i.gender <> 'M'"
				+ " ),"
				+ " femaleMember AS("
				+ "select "
				+ " f.famid,f.wife_name,i.gender as \"wife_gender\" "
				+ " from individuals i, families f "
				+ " where i.id=f.wife_id "
				+ " and "
				+ " i.gender <> 'F' "
				+ " ) "
				+ " select "
				+ " f.famid, "
				+ " m.husband_name, "
				+ " m.husband_gender, "
				+ " f.wife_name, "
				+ " f.wife_gender "
				+ " from "
				+ " maleMember m, femaleMember f "
				+ " where "
				+ " m.famid=f.famid ";
		
		//System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			famID = rs.getString(1); // Listing family id's
			husband_name=rs.getString(2); // husband's name
			husband_gender = rs.getString(3); // husband's gender
			wife_name =rs.getString(4); // wife's name
			wife_gender =rs.getString(5); // wife gender
			
			
			if (famID == null || husband_name == ""|| husband_gender == ""|| wife_name == ""|| wife_gender == "") {
				System.out.println(lineSeparator + "No records found!");
			} else {
				
				if("F".equals(husband_gender)){
					System.out.println(lineSeparator + "Error\tFAMILY:\t US21: Husband "+husband_name+" (Current gender - "+husband_gender+") of the Family "+famID+" is not male.");
				}
				 
				if("M".equals(wife_gender)){
					System.out.println(lineSeparator + "Error\tFAMILY:\t US21: Wife "+wife_name+" (Current gender - "+wife_gender+") of the Family "+famID+" is not female.");
				}
				
			}
			
		}
		rs.close();

	}
}
