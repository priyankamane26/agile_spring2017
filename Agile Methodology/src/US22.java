
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class US22 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static String Uniqueids() throws SQLException {
		System.out.println(lineSeparator + "\n****Start of US22****\n");

		String ids = "";
		int count = 0;
		String type = "";
		boolean nonUnique = false;
		String nonUniqueIDs="";
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
		String query = "select i.id,count(*),'INDIVIDUAL' as type from Individuals i group by i.id UNION select f.famid,count(*),'FAMILY' as type from Families f group by f.famid;";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			ids = rs.getString(1); // Listing all ids both families and
									// individuals
			count = Integer.parseInt(rs.getString(2)); // count of all ids
			type = rs.getString(3);

			if (count > 1 && "INDIVIDUAL".equals(type)) // check if count is
														// more than 1 for ids
			{
				//GedcomParser.invalidIndividualRecord.add(ids);
				nonUnique = true;
				nonUniqueIDs=nonUniqueIDs+""+ids;
				System.out.println(
						lineSeparator + "ERROR:\tINDIVIDUAL:\tUS22:\tId: " + ids + " is not unique ");
			} else if (count > 1 && "FAMILY".equals(type)) {
				nonUniqueIDs=nonUniqueIDs+""+ids;
				//GedcomParser.invalidFamilyRecord.add(ids);
				nonUnique = true;
				System.out.println(
						lineSeparator + "ERROR:\tFAMILY:\tUS22:\tId: " + ids + " is not unique ");
			}
			// else
		}
		// display ids as unique
		if (!nonUnique) {
			System.out.println(lineSeparator + "All ids are unique");
		}else{
			
			// Seting flag for invalid records
			/*	for(String indi: GedcomParser.invalidIndividualRecord){
					String queryDeath = "Update Individuals set invalidRecord ='Y' where id='"+indi+"'";
					stmt.executeUpdate(queryDeath);
				}
		
				for(String fam: GedcomParser.invalidFamilyRecord){
					String queryBirth = "Update Families set invalidRecord  ='Y' where famid='"+fam+"'";
					stmt.executeUpdate(queryBirth);
				}*/
				
				
			}
	
		return nonUniqueIDs;
}

}
