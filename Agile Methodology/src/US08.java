import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class US08 {

	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void birthBeforeParentsMarriage() throws SQLException {
		System.out.println(lineSeparator + "\n****Start of US08****\n");
		
		String name = "";
		String bDate = "";
		String mDate = "";
		String isDiv = "";
		String divDate = "";
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
	
		String query = "select i.name, to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'), f.married, f.divorced, f.divorcedate"+
					   "from individuals i, families f where i.child=f.famid";
	
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {

			name = rs.getString(1); 
			bDate = rs.getString(2);
			isDiv = rs.getString(4);

			if (isDiv == "t") {
				System.out.println(lineSeparator + "No records found!");
			} else {
				System.out.println(
						lineSeparator + "Error\tINDIVIDUAL:\t US07:  Individual " + name + " born before marriage date " + mdate);
			}
		}

	}

}
