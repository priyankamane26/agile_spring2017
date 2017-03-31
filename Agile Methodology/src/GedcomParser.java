/*
 * @Author: Priyanka Mane
 * Created on: 02/11/2017 
 * 
 * This Program parses the the entire Gedcom file.
 * Gets the data per individual and family.
 * Store it in the Database.
 * 
 * ************MODIFICATION*********************************
 * Date				Description							Who
 * 
 * 02/11/2017	-logic to implement parsing and 		PM
 * 				 Birth Date, Marriage Date, Death
 * 				 date, Divorce date processing.
 * 02/12/2017	-Created Derived column Age, alive,		PM
 * 				 divorced basis on Birthday,
 * 				 death date, divorce date resp.
 * 				-Logic to get the husband and wife
 * 				 names basis on their INDI ID's
 * 02/19/2017	-Code changes to handle the cases shown	PM
 * 				 by junit test cases
 * 02/21/2017	-Logic developed to get the individuals	PM
 * 				 who got divorced after death
 * 02/22/2017	-US06 getDivAfterDeathINDI()			PM
 * 				 and implemented a simple UI to allow 
 * 				 User to select from the choices using
 * 				 JoptionPane
 * 02/23/2017	-US27 getINDIAge() method to display 	PM 
 * 				 the individuals with their age		 		
 * 02/24/2017	-US02 getBirthBeforeMarriage()			AM
 * 02/24/2017	-US01 getDatesBeforeCurrentDate()	 	AM
 * 03/04/2017	-Refactoring for all USer stories 		PM
 * 				 completed in Sprint 1
 ************************************************************				  
 */

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.util.Date;
import java.util.Locale;
import java.sql.Statement;

public class GedcomParser {
	static Scanner scan;
	static BufferedWriter outFile;
	static String name = "";
	static String[] tagString;
	static String tag = "";
	static String argument = "";
	static String level = "";
	static String arg = "";
	static String[] argString;
	static String id = "";

	// Derived columns
	static boolean isDivorced = true;
	static boolean isMarried = true;
	static boolean alive = false;

	static Connection con = null;
	static Statement stmt = null;

	// Parsing the GEDCOM file
	public static void parse(String file) throws IOException, ParseException, SQLException {
		
		scan = new Scanner(new FileReader(file));
		String reader = "";
		int count = 0;
		String name = "";
		String surname = "";
		String sex = "";
		String birth = "";
		String death = "";
		String marr = "";
		String div = "";
		String famcID = "";
		String famsID = "";
		String birthdateString[];
		String deathdateString[];
		String marriagedateString[];
		String divorsedateString[];
		String husbID = "";
		String wifeID = "";
		String childID = "";

		// Getting each line from the gedcom file and saving it in the
		// ArrayList.
		ArrayList<String> inputArray = new ArrayList<>();
		while (scan.hasNextLine()) {
			reader = scan.nextLine();
			inputArray.add(reader);
			// System.out.println(reader);
		}

		// Prosessing on each line.
		for (int i = 0; i < inputArray.size(); i++) {
			tagString = inputArray.get(i).split("\\s+");

			// System.out.println(tagString[0]);

			// Consider only "INDI" and "FAM" tags for level 0.
			if ("0".equals(tagString[0]) && ((tagString.length >= 3) || "TRLR".equals(tagString[1]))) {
				if (count > 0) {
					// Calling insert Individual data function for each
					// Individual.
					if ("INDI".equals(argument)) {
						insertINDIData(name, id, surname, sex, birth, death, famsID, famcID);
						name = "";
						id = "";
						surname = "";
						sex = "";
						birth = ""; // birthdate=null;
						death = "";
						famsID = "";
						famcID = "";
						count = 0;
					}
					// Calling insert Family data function for each Family.
					else if ("FAM".equals(argument)) {
						insertFAMData(id, marr, div, husbID, wifeID, childID);
						id = "";
						marr = "";
						div = "";
						husbID = "";
						wifeID = "";
						childID = "";
					}

				}
				tag = tagString[1];
				id = tag.replaceAll("@", "");
				if (!"TRLR".equals(tagString[1])) {
					argument = tagString[2];
				}

			} else if (id != null && (("1").equals(tagString[0]) || ("2").equals(tagString[0]))) {

				if ("GIVN".equals(tagString[1].replaceAll(" ", "")))
					name = tagString[2];
				if ("SURN".equals(tagString[1].replaceAll(" ", "")))
					surname = tagString[2];
				if ("SEX".equals(tagString[1].replaceAll(" ", "")))
					sex = tagString[2];
				if ("BIRT".equals(tagString[1].replaceAll(" ", ""))) {
					i = i + 1;
					birthdateString = inputArray.get(i).split("\\s+");

					for (int j = 0; j < birthdateString.length; j++) {
						if (j > 1) {
							if (birth == null) {
								birth = "";
								birth += birthdateString[j] + " ";
							} else {
								birth += birthdateString[j] + " ";
							}
						}

					}

				}
				if ("DEAT".equals(tagString[1].replaceAll(" ", ""))) {
					i = i + 1;
					deathdateString = inputArray.get(i).split("\\s+");

					for (int j = 0; j < deathdateString.length; j++) {
						if (j > 1) {
							if (death == null) {
								death = "";
								death += deathdateString[j] + " ";
							} else {
								death += deathdateString[j] + " ";
							}
						}

					}
				}

				if ("MARR".equals(tagString[1].replaceAll(" ", ""))) {
					i = i + 1;
					marriagedateString = inputArray.get(i).split("\\s+");

					for (int j = 0; j < marriagedateString.length; j++) {
						if (j > 1) {
							if (marr == null) {
								marr = "";
								marr += marriagedateString[j] + " ";
							} else {
								marr += marriagedateString[j] + " ";
							}
						}

					}
				}

				if ("DIV".equals(tagString[1].replaceAll(" ", ""))) {
					i = i + 1;
					divorsedateString = inputArray.get(i).split("\\s+");

					for (int j = 0; j < divorsedateString.length; j++) {
						if (j > 1) {
							if (div == null) {
								div = "";
								div += divorsedateString[j] + " ";
							} else {
								div += divorsedateString[j] + " ";
							}
						}

					}
				}

				if ("FAMS".equals(tagString[1].replaceAll(" ", "")))
					famsID = tagString[2].replaceAll("@", "");
				if ("FAMC".equals(tagString[1].replaceAll(" ", "")))
					famcID = tagString[2].replaceAll("@", "");
				if ("HUSB".equals(tagString[1].replaceAll(" ", "")))
					husbID = tagString[2].replaceAll("@", "");
				if ("WIFE".equals(tagString[1].replaceAll(" ", "")))
					wifeID = tagString[2].replaceAll("@", "");
				if ("CHIL".equals(tagString[1].replaceAll(" ", ""))) {
					childID += tagString[2].replaceAll("@", "") + " ";

				}

				count++;

			}

		}
	
		String updateHusquery = "UPDATE Families f SET Husband_Name=(SELECT NAME FROM Individuals i WHERE i.ID=f.Husband_ID)";
		String updateWifequery = "UPDATE Families f SET Wife_Name=(SELECT NAME FROM Individuals i WHERE i.ID=f.Wife_ID)";
		stmt.executeUpdate(updateHusquery);
		stmt.executeUpdate(updateWifequery);
	 
	
	}

	public static void insertINDIData(String name, String id, String surname, String sex, String birth, String death,
			String famsID, String famcID) throws ParseException, SQLException {
		int age = 0;
		
		if (death == null || death == "") {
			alive = true;
		} else {
			alive = false;
		}

		
		if (birth == "" || birth == null) {
			age = 0;
		} else {
			
			DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
			Date birthdate = format.parse(birth);
			if(death=="" || death==null){
				age = getAge(birthdate);
			}
			else{
				Date deathdate = format.parse(death);
				age = getAge(birthdate,deathdate);
			}
			
		}

		
		String query = "INSERT INTO Individuals VALUES(" + "'" + id + "'" + ",'" + name + "','" + surname + "','" + sex + "','" + birth
				+ "','" + age + "','" + alive + "','" + death + "','" + famcID + "','" + famsID + "')";
		// System.out.println(query);
		stmt.executeUpdate(query);

	}

	public static void insertFAMData(String id, String marr, String div, String husb, String wife, String child)
			throws SQLException {

		if (div == null || div == "") {
			isDivorced = false;
		} else {
			isDivorced = true;
		}
		
		if (marr==null || marr==""){
			isMarried=false;
		}else {
			isMarried=true;
		}

		String query = "INSERT INTO Families VALUES(" + "'" + id + "','" +  isMarried + "','" + marr + "','" + isDivorced + "','" + div
				+ "','" + husb + "',NULL,'" + wife + "',NULL,'" + child + "')";
		// System.out.println(query);
		stmt.executeUpdate(query);

	}

	public static int getAge(Date dateOfBirth) {

		Calendar today = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();
		

		int age = 0;

		birthDate.setTime(dateOfBirth);
	

			age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
			// If birth date is greater than todays date (after 2 days adjustment of
			// leap year) then decrement age one year
			if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3)
					|| (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
				age--;

				// If birth date and todays date are of same month and birth day of
				// month is greater than todays day of month then decrement age
			} else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
					&& (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
				age--;
			}
	
		return age;

	}
	
	
	public static int getAge(Date dateOfBirth, Date death) {

		
		Calendar birthDate = Calendar.getInstance();
		Calendar deathDate = Calendar.getInstance();

		int age = 0;

		birthDate.setTime(dateOfBirth);
		deathDate.setTime(death);

	
			age = deathDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
			
			// If birth date is greater than todays date (after 2 days adjustment of
			// leap year) then decrement age one year
			if ((birthDate.get(Calendar.DAY_OF_YEAR) - deathDate.get(Calendar.DAY_OF_YEAR) > 3)
					|| (birthDate.get(Calendar.MONTH) > deathDate.get(Calendar.MONTH))) {
				age--;

				// If birth date and todays date are of same month and birth day of
				// month is greater than todays day of month then decrement age
			} else if ((birthDate.get(Calendar.MONTH) == deathDate.get(Calendar.MONTH))
					&& (birthDate.get(Calendar.DAY_OF_MONTH) > deathDate.get(Calendar.DAY_OF_MONTH))) {
				age--;
			}	

		return age;

	}

	public static void main(String[] args) throws IOException, ParseException, SQLException {
		String programTitle = "This program performs following\n" + "1. Parses the gedcom file.\n"
				+ "2. Stores the parsed data in the database.\n"
				+ "3. Performs various tasks mentioned in User stories";

		JOptionPane.showMessageDialog(null, programTitle, "Program description", JOptionPane.INFORMATION_MESSAGE);

		con = JDBCConnect.getConnection();

		String query1 = "Delete from Individuals";
		String query2 = "Delete from Families";

		stmt = con.createStatement();

		stmt.executeUpdate(query1);
		stmt.executeUpdate(query2);
		
		US01.getDatesBeforeCurrentDate("Sprint3_inputFile.ged");
		US02.getBirthBeforeMarriage();
		US03.getBirthAfterDeath();
		//US04.getMarriageBeforeDivorce();
		US05.getMarriageAfterDeath();
		US06.getINDIAge();
		US07.getAgeAbove150();
		US08.birthBeforeParentsMarriage();
		US14.multipleBirths();
		US22.Uniqueids();
		US27.getDivAfterDeathINDI();
		US29.listOfDeseased();
		US35.RecentBirths();
		US16.sameFamilySurname();
		US21.correctGenderForRole();
		
		stmt.close();
		
	}

}
