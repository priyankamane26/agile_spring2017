import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class US28 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void siblingAgeDesc() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US28****\n");
		System.out.println(lineSeparator + "List\tFAMILY:\t US28: ");
		System.out.println();

		System.out.format("%16s%16s%16s%16s", "FAMILY", "Name", "Surname","Age");
		System.out.println();

		String famID = "";
		String siblingName = "";
		String siblingSurname = "";
		String siblingAge = "";
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "WITH  FamilyINDI AS(  " 
				+ " select " 
				+ " f.famid, "
				+ " regexp_split_to_table(trim(both ' ' from f.children),E'\\\\s+') as \"indi\" " 
				+ " from families f "
				+ " ) " 
				+ " select distinct " 
				+ " fi.famid, " 
				+ " i.name, " 
				+ " i.surname, " 
				+ " i.age "
				+ " from  FamilyINDI fi, individuals i " 
				+ " where  fi.indi=i.id  "
				+ " order by fi.famid asc ,i.age desc";

		//System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			famID = rs.getString(1); // Listing family id's
			siblingName = rs.getString(2); // Male family person's name
			siblingSurname = rs.getString(3); // Listing thier surnames
			siblingAge = rs.getString(4);

			if (famID == null || siblingName == "" || siblingSurname == "" || siblingAge == "") {
				System.out.println(lineSeparator + "No records found!");
			} else {

				System.out.format("%16s%16s%16s%16s",famID ,siblingName ,siblingSurname,siblingAge);
				System.out.println();
			}

		}
		rs.close();

	}
}
