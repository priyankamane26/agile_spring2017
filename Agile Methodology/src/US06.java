import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.sql.Connection;

public class US06 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getINDIAge() throws SQLException {
		
		System.out.println(lineSeparator+"\n****Start of US06****\n");
		
		String IndiName = "";
		String IndiAge = "";
		String message = "";
		int age = 0;

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select i.name, i.age from individuals i";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			IndiAge = rs.getString(2); // Listing thier age
			age = Integer.parseInt(IndiAge);
			if (age <= 0) {
				System.out.println(
						lineSeparator + "Error\tINDIVIDUAL:\t US06: Individual " + IndiName + "'s  age ("+age+") is Invalid");
			} else {
				message = message + "" + IndiName + ":" + IndiAge + "\n";
			}

		}
		rs.close();

		if (message == null || message == "") {
			System.out.println(lineSeparator + "No records found!");
		} else {
			System.out.println(lineSeparator + "Individuals with thier age are");
			System.out.println(message);
		}
	}
}
