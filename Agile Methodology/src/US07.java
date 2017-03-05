import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class US07 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getAgeAbove150() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US07****\n");

		String IndiName = "";
		String IndiAge = "";
		String isAlive = "";
		int age = 0;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select i.name, i.age, i.alive from individuals i" + " where i.age >= 150";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			IndiAge = rs.getString(2); // Listing thier age
			isAlive = rs.getString(3);
			age = Integer.parseInt(IndiAge);

			if (IndiName == null || IndiAge == "") {
				System.out.println(lineSeparator + "No records found!");
			} else {

				if ("t".equals(isAlive)) {
					System.out.println(lineSeparator + "Error\tINDIVIDUAL:\t US07: Alive Individual " + IndiName
							+ "'s  age (" + age + ") is above 150 years");
				} else if ("f".equals(isAlive)) {

					System.out.println(lineSeparator + "Error\tINDIVIDUAL:\t US07: Deseased Individual " + IndiName
							+ "'s  age (" + age + ") is above 150 years");
				}
			}

		}
		rs.close();

	}
}
