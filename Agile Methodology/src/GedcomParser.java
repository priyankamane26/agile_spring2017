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
 ************************************************************				  
 */


import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
import java.util.Objects;
import java.sql.Statement;



public class GedcomParser {
	static Scanner scan;
	static BufferedWriter outFile;
	static String name = "";
	static String[] tagString;
	static String tag = "";
	static String argument = "";
	static String level="";
	static String arg="";
	static String[] argString;
	static String id="";
	
	// Derived columns
	static boolean isDivorced=true;
	static boolean alive=false;
	
	static Connection connection = null;
	static Statement stmt = null;
	

// Parsing the GEDCOM file 
public static void parse() throws IOException, ParseException, SQLException {
    //scan = new Scanner(new FileReader("My-Family-30-Jan-2017-424.ged"));
    //scan = new Scanner(new FileReader("My-Family-29-Jan-2017-377.ged"));
	scan = new Scanner(new FileReader("US_07.ged"));
    String reader = "";
    int count=0;
    String name="";
    String surname="";
    String sex="";
    String birth="";
    String death="";
    String marr="";
    String div="";
    String famcID="";
    String famsID="";
    String birthdateString[];
    String deathdateString[];
    String marriagedateString[];
    String divorsedateString[];
    String husbID="";
    String wifeID="";
    String childID="";

    
    // Getting each line from the gedcom file and saving it in the ArrayList.
    ArrayList<String> inputArray = new ArrayList<>();
    while (scan.hasNextLine()) {
    	reader = scan.nextLine();
    	inputArray.add(reader);
    	// System.out.println(reader);
    }
    
    // Prosessing on each line.
    for(int i=0;i< inputArray.size();i++){
    	tagString=inputArray.get(i).split("\\s+");
    	
    	// System.out.println(tagString[0]);
    	
    	// Consider only "INDI" and "FAM" tags for level 0.
    	if("0".equals(tagString[0]) && ((tagString.length >= 3)|| "TRLR".equals(tagString[1]))){	
    		if(count > 0)
    		{
    			// Calling insert Individual data function for each Individual.
    			if("INDI".equals(argument)){
    				insertINDIData(name,id,surname,sex,birth,death,famsID,famcID);
        			name="";
        			id="";
        			surname="";
        			sex="";
        			birth="";	//birthdate=null;
        			death="";
        			famsID="";
        			famcID="";
        			count=0;
    			}
    			// Calling insert Family data function for each Family.
    			else if("FAM".equals(argument)){
    				insertFAMData(id,marr,div,husbID, wifeID, childID);
    				id="";
    				marr="";
    				div="";
    				husbID="";
    				wifeID="";
    				childID="";
    			}
	
    		}
    		tag=tagString[1];
			id=tag.replaceAll("@","");
			if(!"TRLR".equals(tagString[1])){
				argument=tagString[2];
			}
			
    	
    		
    	}
    	else if(id != null &&(("1").equals(tagString[0]) || ("2").equals(tagString[0]))){
    		

    			if("GIVN".equals(tagString[1].replaceAll(" ","")))
        			name=tagString[2];
        		if("SURN".equals(tagString[1].replaceAll(" ","")))
        			surname=tagString[2];
        		if("SEX".equals(tagString[1].replaceAll(" ","")))
        			sex=tagString[2];
        		if("BIRT".equals(tagString[1].replaceAll(" ","")))
        		{
        			i=i+1;
        			birthdateString=inputArray.get(i).split("\\s+");
        			
        			for(int j=0; j<birthdateString.length;j++){
        				if(j>1)
        				{
        					if(birth==null)
            				{
            					birth="";
            					birth+=birthdateString[j]+" ";
            				}
            				else{
            					birth+=birthdateString[j]+" ";
        					}
        				}
        				
        			}
        		
        		}
        		if("DEAT".equals(tagString[1].replaceAll(" ","")))
        		{
        			i=i+1;
        			deathdateString=inputArray.get(i).split("\\s+");
        			
        			for(int j=0; j<deathdateString.length;j++){
        				if(j>1)
        				{
        					if(death==null)
            				{
        						death="";
        						death+=deathdateString[j]+" ";
            				}
            				else{
            					death+=deathdateString[j]+" ";
        					}
        				}
        				
        			} 
        		}
        		
        		if("MARR".equals(tagString[1].replaceAll(" ","")))
        		{
        			i=i+1;
        			marriagedateString=inputArray.get(i).split("\\s+");
        			
        			for(int j=0; j<marriagedateString.length;j++){
        				if(j>1)
        				{
        					if(marr==null)
            				{
        						marr="";
        						marr+=marriagedateString[j]+" ";
            				}
            				else{
            					marr+=marriagedateString[j]+" ";
        					}
        				}
        				
        			} 
        		}
        		
        		if("DIV".equals(tagString[1].replaceAll(" ","")))
        		{
        			i=i+1;
        			divorsedateString=inputArray.get(i).split("\\s+");
        			
        			for(int j=0; j<divorsedateString.length;j++){
        				if(j>1)
        				{
        					if(div==null)
            				{
        						div="";
        						div+=divorsedateString[j]+" ";
            				}
            				else{
            					div+=divorsedateString[j]+" ";
        					}
        				}
        				
        			} 
        		}
        		 
        		if("FAMS".equals(tagString[1].replaceAll(" ","")))
        			famsID=tagString[2].replaceAll("@","");
        		if("FAMC".equals(tagString[1].replaceAll(" ","")))
        			famcID=tagString[2].replaceAll("@","");
    			if("HUSB".equals(tagString[1].replaceAll(" ","")))
        			husbID=tagString[2].replaceAll("@","");
    			if("WIFE".equals(tagString[1].replaceAll(" ","")))
        			wifeID=tagString[2].replaceAll("@","");
    			if("CHIL".equals(tagString[1].replaceAll(" ",""))){
    				childID+=tagString[2].replaceAll("@","")+" ";
    				
    			}
    				
  
    			count++;
    			
    	}
    	
    	
    }
}

public static void insertINDIData(String name, String id,String surname,String sex,String birth,String death,String famsID,String famcID) throws ParseException, SQLException{
	DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
	Date birthdate = format.parse(birth);
	

	/*System.out.println("ID "+id);
	System.out.println(name);
	System.out.println(surname);
	System.out.println(sex);
	System.out.println(birthdate);
	System.out.println(deathdate);
	System.out.println(famsID);*/

	if(death==null || death==""){
		alive=true;
	}
	else{
		alive=false;
	}
	 
	 int age=getAge(birthdate);
	 		 
	String query = "INSERT INTO Individuals VALUES("+"'"+id+"'"+",'"+name+"','"+sex+"','"+birth+"','"+age+"','"+alive+"','"+death+"','"+famcID+"','"+famsID+"')";
	//System.out.println(query);
	stmt.executeUpdate(query);
	
}

public static void insertFAMData(String id, String marr,String div, String husb, String wife, String child) throws SQLException{
	
	
	
	/*System.out.println("ID "+id);
	System.out.println(husb);
	System.out.println(wife);
	System.out.println(child);*/
	
	if(div==null || div==""){
		isDivorced=false;
	}
	else{
		isDivorced=true;
	}
	
	
	String query = "INSERT INTO Families VALUES("+"'"+id+"','"+marr+"','"+isDivorced+"','"+div+"','"+husb+"',NULL,'"+wife+"',NULL,'"+child+"')";
	//System.out.println(query);
	stmt.executeUpdate(query);
	
	
}


public static int getAge(Date dateOfBirth) {

    Calendar today = Calendar.getInstance();
    Calendar birthDate = Calendar.getInstance();

    int age = 0;

    birthDate.setTime(dateOfBirth);
    
    //Already implemented in User Story 01 getDatesBeforeCurrentDate()//
    // Code changes after running the test cases - testForInvalidBirthDate
    /*if (birthDate.after(today)) {
        throw new IllegalArgumentException("Invalid birth date!");
    }*/

    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

    // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
    if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
            (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
        age--;

     // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
    }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
              (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
        age--;
    }

    // return age;
    // Code changes after running the test cases - testBirthdayToday
    /*if(age<0)
    {
    	throw new IllegalArgumentException("Age cannot be negative");
    }else{*/
    	return age;
	//}
    
}


//-----------------------User Stories by Priyanka Mane----------------------//
public static void getDivAfterDeathINDI() throws SQLException, ParseException{
	String div="";
	String death="";
	String INDIName="";
	String nameNull="";
	int countAfter = 0;
	int countNull = 0;
	Date divDt=new Date();
	Date deathDt=new Date();
	
	String query ="select to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'), i.name from families f, individuals i"
			+" where ((i.id = f.husband_id) or  (i.id=wife_id))";
	//System.out.println(query);
	
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
			{	
				div=rs.getString(1);
				death=rs.getString(2);
				
				if(div=="" || death==null){
					countNull++;
					nameNull = nameNull + "\n " + rs.getString(3);
					
				}
				else if(div!=null && death!=null)
				{
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					divDt = format.parse(div);
					deathDt = format.parse(death);
					
					//System.out.println(divDt);
					
					// logic to check whether the divorce date is grea2ter than death date
					 Calendar divorceDate = Calendar.getInstance();
					 Calendar deathDate = Calendar.getInstance();

					 divorceDate.setTime(divDt);
					 deathDate.setTime(deathDt);

					    if (divorceDate.after(deathDate)) {
					    	countAfter++;
					        //throw new IllegalArgumentException("Invalid divorce date!");
					    	INDIName=INDIName+"\n "+rs.getString(3); // Listing all individual's names	
					    }
					    
				}
			   
			} rs.close();
			
			// Display the results
			if(countAfter>0){
				 	JOptionPane.showMessageDialog(null,"Individual whose divorce date comes"
			   			 	+"\nafter their death is \n"+INDIName, "Result",JOptionPane.INFORMATION_MESSAGE);	    	 
			}
			if(countNull>0){
					JOptionPane.showMessageDialog(null,"Either the divorce date OR death date of Individual(s)\n--------------------"
							+nameNull+"\n--------------------\n is not available!", "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
			}
			if(countAfter==0 || countNull==0){
					JOptionPane.showMessageDialog(null,"No individual has invalid divorce date", "Result",JOptionPane.INFORMATION_MESSAGE);
			}	
			    
}


public static void getINDIAge() throws SQLException{
	String IndiName="";
	String IndiAge="";
	String message="";
	
	String query ="select i.name, i.age from individuals i";
	//System.out.println(query);
	/*try{*/
	
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
			{	
				IndiName=rs.getString(1); // Listing all individual's names
				IndiAge=rs.getString(2); // Listing thier age
				message = message+"<tr><td>"+IndiName+"</td><td>"+IndiAge+"</td></tr>";
			} rs.close();
			  
			if(message==null || message==""){
				JOptionPane.showMessageDialog(null,"No records found!", "Individual's Age",JOptionPane.INFORMATION_MESSAGE);
			}else
			{
				JOptionPane.showMessageDialog(null,"<html><table>" +
					      "<tr><td>NAME</td><td>AGE</td></tr>" +message+"</table>", "Individual's Age",JOptionPane.INFORMATION_MESSAGE);
			}
}


//-----------------------User Stories by Amol Shandilya-----------------------//
public static void getBirthBeforeMarriage() throws SQLException, ParseException{
	String bdate="";
	String mdate="";
	String nameAfter="";
	String nameNull="";
	int countAfter = 0;
	int countNull = 0;
	Date dob=new Date();
	Date dom=new Date();
	
	//fetch all marriage date, birth date and names of all individuals who are married 
	String query = "select to_date(NULLIF(f.married,''),'DD Mon YYYY'), to_date(NULLIF(i.birthday,''),'DD Mon YYYY'), i.name from families f,individuals i"
			+" where f.husband_id=i.id OR f.famid=i.spouse OR f.wife_id=i.id;";

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){	
				mdate = rs.getString(1);
				bdate = rs.getString(2);
				
				//Fetch names of individuals with missing values
				if(bdate == null || mdate == null){
					 countNull++;
					 nameNull = nameNull + "\n " + rs.getString(3);
				}
				
				//Fetch names of individuals with invalid values
				else if (bdate != null && mdate != null) {
   					 DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				 	 dob = format.parse(bdate);
				 	 dom = format.parse(mdate);
				
				 	 // logic to check whether the birth date is greater than marriage date
				 	 Calendar birth = Calendar.getInstance();
				 	 Calendar marriage = Calendar.getInstance();

				 	 birth.setTime(dob);
				 	 marriage.setTime(dom);

					 if(birth.after(marriage)){
					 countAfter++;
				 	 nameAfter = nameAfter + "\n " + rs.getString(3);
					 }
				 }
			} rs.close();
			
			// Display the results
			if(countAfter>0){
			   	JOptionPane.showMessageDialog(null,"Individual(s) whose birth date comes"
			   			 +"\nAFTER their marriage date is \n"+nameAfter, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
			    }
			if(countNull>0){
			   	JOptionPane.showMessageDialog(null,"Individual(s) whose either birth date or marriage date"
			   			 +"\nis MISSING are \n"+nameNull, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
			    }
			if(countAfter==0){
			   	 JOptionPane.showMessageDialog(null,"No individual has invalid birth date", "Result",JOptionPane.INFORMATION_MESSAGE);
			   }				
}


public static void getDatesBeforeCurrentDate() throws SQLException, ParseException{
	String bdate="";
	String dedate="";
	String mdate="";
	String didate="";
	String h_id="";
	String w_id="";
	String id="";
	String alive="";
	String False="f", True="t"; 
	String divorce="";
	String nameBirth="", nameDeath="", nameMar="", nameDiv="" ;
	String nameNullB="", nameNullDe="", nameNullM="", nameNullDi="";
	int countBirth=0, countDeath=0, countMar=0, countDiv=0;
	int countNullB=0, countNullDe=0, countNullM=0, countNullDi=0;
	
	Date tempD = new Date();
	Calendar tempC = Calendar.getInstance();
	Calendar today = Calendar.getInstance();
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	
	//fetch birth date, marriage date, death date, divorce date and name of all individuals 
	String query = "select i.name,to_date(NULLIF(i.birthday,''), 'DD Mon YYYY'),to_date(NULLIF(i.death,''), 'DD Mon YYYY'),"
					+ "to_date(NULLIF(f.married,''), 'DD Mon YYYY'),to_date(NULLIF(f.divorcedate,''), 'DD Mon YYYY'),"
			 		+ "i.alive, f.husband_id, f.wife_id, f.divorced, i.id from families f RIGHT OUTER JOIN individuals i ON ((i.id = f.husband_id) or (i.id=wife_id));";
	
	ResultSet rs = stmt.executeQuery(query);
	while (rs.next()){
		bdate = rs.getString(2);
		dedate = rs.getString(3);
		mdate = rs.getString(4);
		didate = rs.getString(5);
		alive = rs.getString(6);
		h_id = rs.getString(7);
		w_id = rs.getString(8);
		divorce = rs.getString(9);
		id = rs.getString(10);
				
		//Fetch names of individuals with invalid values
		if (bdate != null) {
			 tempD = format.parse(bdate);
		 	 tempC.setTime(tempD);
		 	 if(tempC.after(today)){
				 countBirth++;
			 	 nameBirth = nameBirth + "\n " + rs.getString(1);
				 }
		}
		else{
			countNullB++;
			nameNullB = nameNullB + "\n " + rs.getString(1);
		}
			
		
		if (dedate != null) {
			 tempD = format.parse(dedate);
		 	 tempC.setTime(tempD);
		 	 if(tempC.after(today)){
				 countDeath++;
			 	 nameDeath = nameDeath + "\n " + rs.getString(1);
				 }
		}
		else{
			if(equalsWithNulls(alive,False)){
				countNullDe++;
				nameNullDe = nameNullDe + "\n " + rs.getString(1);
			}
		}
		
		if (mdate != null){
			 tempD = format.parse(mdate);
		 	 tempC.setTime(tempD);
		 	 if(tempC.after(today)){
				 countMar++;
			 	 nameMar = nameMar + "\n " + rs.getString(1);
				 }
		}
		else{
			if(equalsWithNulls(h_id,id) || equalsWithNulls(w_id,id)){
				countNullM++;
				nameNullM = nameNullM + "\n " + rs.getString(1);
			}
		}
		
		if (didate != null) {
			 tempD = format.parse(didate);
		 	 tempC.setTime(tempD);
		 	 if(tempC.after(today)){
				 countDiv++;
			 	 nameDiv = nameDiv + "\n " + rs.getString(1);
				 }
		}
		else{
			if(equalsWithNulls(divorce,True)){
				countNullDi++;
				nameNullDi = nameNullDi + "\n " + rs.getString(1);
			}
		}
	} rs.close();
	
	//Display the names of individuals who have missing values
	if(countNullB>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose BIRTH DATE is"
	   			 +"\nMISSING are \n"+nameNullB, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countNullDe>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose DEATH DATE is"
	   			 +"\nMISSING are \n"+nameNullDe, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countNullM>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose MARRIAGE DATE is"
	   			 +"\nMISSING are \n"+nameNullM, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countNullDi>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose DIVORCE DATE is"
	   			 +"\nMISSING are \n"+nameNullDi, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	// Display the names of individuals who have invalid dates
	if(countBirth>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose BIRTH DATE comes"
	   			 +"\nAFTER CURRENT DATE \n"+nameBirth, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countDeath>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose DEATH DATE comes"
	   			 +"\nAFTER CURRENT DATE \n"+nameDeath, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countMar>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose MARRIAGE DATE comes"
	   			 +"\nAFTER CURRENT DATE \n"+nameMar, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countDiv>0){
	   	JOptionPane.showMessageDialog(null,"Individual(s) whose DIVORCE DATE comes"
	   			 +"\nAFTER CURRENT DATE \n"+nameDiv, "Result",JOptionPane.INFORMATION_MESSAGE);		    	 
	    }
	if(countBirth==0 && countDeath==0 && countMar==0 && countDiv==0){
	   	 JOptionPane.showMessageDialog(null,"No individual has invalid birth date, death date, marriage date, or divorce date", "Result",JOptionPane.INFORMATION_MESSAGE);
	    }	
}

//method to compare objects with null values 
public static final boolean equalsWithNulls(Object a, Object b) {
    if (a==b) return true;
    if ((a==null)||(b==null)) return false;
    return a.equals(b);
  }

//-----------------------User Stories by Palak Gangwal-----------------------//

//---------------------------------------------------------------------------//

public static void main(String[] args) throws IOException, ParseException {
	
	String programTitle = "This program performs following\n"
	+ "1. Parses the gedcom file.\n"
	+ "2. Stores the parsed data in the database.\n"
	+ "3. Performs various tasks mentioned in User stories";

	JOptionPane.showMessageDialog(null, programTitle,
	"Program description", JOptionPane.INFORMATION_MESSAGE);
	
	
	try {

		connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/postgres", "postgres", "root");
		connection.setAutoCommit(false);
		stmt = connection.createStatement();
		
		// Before parsing flushing the table individual and families for a fresh set of data.
		String query1 ="Delete from Individuals";
		String query2 ="Delete from Families";
		stmt.executeUpdate(query1);
		stmt.executeUpdate(query2);
	
	parse();

	String updateHusquery = "UPDATE Families f SET Husband_Name=(SELECT NAME FROM Individuals i WHERE i.ID=f.Husband_ID)";
	String updateWifequery = "UPDATE Families f SET Wife_Name=(SELECT NAME FROM Individuals i WHERE i.ID=f.Wife_ID)";
	stmt.executeUpdate(updateHusquery);
	stmt.executeUpdate(updateWifequery);
	
	
	int option = Integer.parseInt(JOptionPane.showInputDialog(null,
	"Select an option for desired operation.\n1. List age of each individual."
	+ "\n2. Check for individuals with invalid divorce date"
	+ "\n3. Check for individuals whose birth date is before marriage date"
	+ "\n4. Check for individuals whose birth date, death date, marriage date, or divorce date is after current date\n", "Select Option",
	JOptionPane.QUESTION_MESSAGE));
	
	switch (option) {
	// List each individual's age
	case 1: 
		
		getINDIAge();
		break;
	
	// Get individuals with invalid divorce date
	case 2: 
		
		getDivAfterDeathINDI();
		break;
	
	// Get individuals whose birth date is after marriage date 
	case 3: 
		
		getBirthBeforeMarriage();
		break;
	
	// Get records whose marriage date, divorce date, birth date and death date is after current date  
	case 4: 
		
		getDatesBeforeCurrentDate();
		break;
	
	case 5: 
	
		//US03();
		break;
	
	case 6: 
	
		//US05();
		break;
	}
	
	
	
	stmt.close();
	connection.commit();
	connection.close();
	
} catch (SQLException e) {

	System.out.println("Connection Failed! Check output console");
	e.printStackTrace();
	return;

}

}

}