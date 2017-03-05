import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class US29 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void listOfDeseased() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US29****\n");

		String IndiName = "";
		String deathDate = "";

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select distinct i.name, i.death " + " from individuals i " + " where" + " alive=false";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			deathDate = rs.getString(2); // Their death dates

			if (IndiName == null || IndiName == "") {
				System.out.println(lineSeparator + "No records found!");
			} else {
				System.out.println(
						lineSeparator + "Error\tINDIVIDUAL:\t US07:  Individual " + IndiName + " died on " + deathDate);
			}
		}
		rs.close();

	}
}
