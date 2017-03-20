import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class US14 {
	static Connection con = null;
	static Statement stmt = null;
	static String lineSeparator = "======================================================================="
			+ "===========================================================================\n";
	
	public static void multipleBirths() throws SQLException, ParseException {
		System.out.println(lineSeparator + "\n****Start of US14****\n");
		
		int count = 0;
		String birth = "";
		String indiID="";
		boolean multiBirth=false;
		String indiName="";
		String siblings="";
		
		con = JDBCConnect.getConnection();
		stmt = con.createStatement();
	
		//fetch individual(s) birth dates and the count of occurrence of each birth date in table 
		//String query = "select to_date(NULLIF(birthday,''), 'DD Mon YYYY'),id,count(*) from individuals group by birthday,id";
	
		String query = "with count_birth AS("
				+ " select to_date(NULLIF(birthday,''), 'DD Mon YYYY') as \"birthdate\", "
				+ " count(*) as \"no_of_siblings\" "
				+ " from individuals group by birthday"
				+ " ), "
				+ " multiBirth AS("
				+ " select distinct"
				+ " c.birthdate,"
				+ " c.no_of_siblings"
				+ " from individuals i, count_birth c"
				+ " where "
				+ " c.no_of_siblings >= 5"
				+ ") select "
				+ " i.id,"
				+ " m.birthdate,"
				+ " m.no_of_siblings,"
				+ " i.name"
				+ " from individuals i, multiBirth m"
				+ " where "
				+ " to_date(NULLIF(i.birthday,''), 'DD Mon YYYY') = (select birthdate from multiBirth);";
		//System.out.println(query);
		
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			
			indiID=rs.getString(1);
			birth = rs.getString(2);
			count = rs.getInt(3);
			indiName=rs.getString(4);
			
			//birth date should not be null
			if(birth!=null){
				//if more than 5 siblings born at same date
				
					GedcomParser.invalidIndividualRecord.add(indiID);
					multiBirth=true;
					siblings=siblings+"\n"+indiName;
								
			}
		}
		if(multiBirth){
			System.out.println(lineSeparator + "ERROR:\tINDIVIDUAL:\tUS14:\tMore than 5 siblings born on "+birth+" are\n"+siblings);
			for(String indi: GedcomParser.invalidIndividualRecord){
				String queryDeath = "Update Individuals set invalidRecord ='Y' where id='"+indi+"'";
				stmt.executeUpdate(queryDeath);
			}
		}
		
	}
}