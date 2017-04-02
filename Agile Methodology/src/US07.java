import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class US07 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";

	public static void getAgeAbove150() throws SQLException {

		System.out.println(lineSeparator + "\n****Start of US06 and US07****\n");

		String IndiName = "";
		String IndiAge = "";
		String isAlive = "";
		String indiID="";
		String invalidRecord="";
		int age = 0;
		boolean noRecords = false;

		String validAgeINDI = "";

		con = JDBCConnect.getConnection();
		stmt = con.createStatement();

		String query = "select i.name, i.age, i.alive, i.id,i.invalidRecord from individuals i";
		// System.out.println(query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {

			IndiName = rs.getString(1); // Listing all individual's names
			IndiAge = rs.getString(2); // Listing thier age
			isAlive = rs.getString(3);
			indiID=rs.getString(4);
			invalidRecord=rs.getString(5);
			age = Integer.parseInt(IndiAge);

			if (IndiName == null || IndiAge == "") {
				noRecords = true;
			}

			if (age <= 0) {
				System.out.println(lineSeparator + "Error\tINDIVIDUAL:\t US06: Individual " + IndiName + "'s  age ("
						+ age + ") can not be equal to less than zero");
			} else if (age >= 150) {
				if ("t".equals(isAlive)) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "Error\tINDIVIDUAL:\t US07: Alive Individual " + IndiName
							+ "'s  age (" + age + ") is above 150 years");
				} else if ("f".equals(isAlive)) {
					//GedcomParser.invalidIndividualRecord.add(indiID);
					System.out.println(lineSeparator + "Error\tINDIVIDUAL:\t US07: Deseased Individual " + IndiName
							+ "'s  age (" + age + ") is above 150 years");
				}
			} else {

				if("N".equals(invalidRecord)){
					validAgeINDI = validAgeINDI + IndiName + ": " + IndiAge + "\n";
				}
				
			}

		}
		rs.close();

		if (noRecords) {
			System.out.println(lineSeparator + "No records found!");
		} else {
			/*for(String indi: GedcomParser.invalidIndividualRecord){
				String queryDeath = "Update Individuals set invalidRecord ='Y' where id='"+indi+"'";
				stmt.executeUpdate(queryDeath);
			}*/
			System.out.println(lineSeparator + "Individuals with Valid age are\n");
			System.out.println(validAgeINDI);

		}

	}
}
