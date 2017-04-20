import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class US16 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void sameFamilySurname() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US16****\n");

		String famID = "";
		String maleName="";
		String famSurname = "";
		String previousFamID = "";
		String previousMaleName="";
		String previousFamSurname = "";
	
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "WITH famMembers AS( "
				+ " select distinct "
				+ " famid as \"famid\", "
				+ " husband_id||' '||wife_id||' '||children as \"members\" "
				+ " from "
				+ " families), "
				+ " FamilyINDI AS( "
				+ " select "
				+ " fm.famid, "
				+ " regexp_split_to_table(trim(both ' ' from fm.members),E'\\\\s+') as \"indi\" "
				+ " from famMembers fm "
				+ " )select distinct"
				+ " fi.famid, "
				+ " i.name, "
				+ " i.surname "
				+ " from "
				+ " FamilyINDI fi, individuals i "
				+ " where "
				+ " fi.indi=i.id "
				+ " and "
				+ " i.gender='M'"
				+ "order by fi.famid";
		
		//System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			famID = rs.getString(1); // Listing family id's
			maleName=rs.getString(2); // Male family person's name
			famSurname = rs.getString(3); // Listing thier surnames
			
			/*System.out.println(famID);
			System.out.println(famSurname);*/
			
			if (famID == null || famSurname == "") {
				System.out.println(lineSeparator + "No records found!");
			} else {
				
				if(previousFamID==""){
				previousFamID=famID;
				previousMaleName=maleName;
				previousFamSurname=famSurname;
				}
				else{
				
					while(previousFamID.equals(famID))
					{
					 	if(previousFamSurname!=famSurname);
					 	{
					 		System.out.println(lineSeparator + "Error\tFAMILY:\t US16: Male Members "+previousMaleName+" ("+previousFamSurname+") and "+maleName+" ("+famSurname+") of Family "+famID+" does not have same surname/lastname.");
					 		previousFamID=famID;
							previousMaleName=maleName;
							previousFamSurname=famSurname;
					 		break;
					 	}
					 	
					} 
				}
				 
				
			}
			
		}
		rs.close();

	}
}
