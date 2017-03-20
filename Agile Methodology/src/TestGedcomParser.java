import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class TestGedcomParser {

	// GedcomParser gp = new GedcomParser();
	String birth = "26 MAR 1991";
	@SuppressWarnings("deprecation")
	Date dob = new Date(birth);
	int age = 25;

	// Testing whether it provides correct age
	@Test
	public void testGetAge() {
		assertEquals("Not a correct age", age, GedcomParser.getAge(dob));
	}

	// Testing exception thrown if the birthdate entered is greater than today's
	// date.
	@Test
	public void testForInvalidDates() throws ParseException {

		// Case when dates are invalid, the GedcomParser function should return
		// true.
		String d1 = "2018-03-26";
		String d2 = "2016-03-26";

		/*
		 * Case when dates are valid, the GedcomParser function should return
		 * false. String d1="2015-03-26"; String d2="2016-03-26";
		 */

		assertTrue("Dates are valid", GedcomParser.dateValidator(d1, d2, "After"));

	}

	/*
	 * Exact real age is 1 years 11 months 28 days. The function does consider
	 * the scenario where the birthdate is in the same month but the birth DAY
	 * is greater than today.
	 * 
	 * Test should fail for age=2, but pass for age=1
	 * 
	 */
	@Test
	public void testSameMonthDifferentDay() {
		String birth = "22 March 2015";
		@SuppressWarnings("deprecation")
		Date dob = new Date(birth);

		int age = 1;
		// int age=2;

		assertEquals("Age is incorrect", age, GedcomParser.getAge(dob));

	}

	// Testing whether the leap years are counted.
	@Test
	public void testLeapYearCalc() {
		String birth = "29 March 2012";
		@SuppressWarnings("deprecation")
		Date dob = new Date(birth);

		int age = 4;
		// int age=5;

		assertEquals("Age does not consider leap year", age, GedcomParser.getAge(dob));

	}

	// Testing for Zero age or person born today.
	@Test
	public void testAgezero() {
		String birth = "19 Feb 2017";
		@SuppressWarnings("deprecation")
		Date dob = new Date(birth);
		// age=0;
		// int age=1;
		assertTrue("Age can not be Zero", GedcomParser.getAge(dob) == 0);
		// assertEquals("Age cannot be zero", age, GedcomParser.getAge(dob));

	}
	
	// Testing the dateDiff function
	@Test
	public void testDateDiff(){
		int days = 374;
		int months = 12;
		int years = 1;

		String D2 = "1 MAR 2016";
		String D1 = "21 FEB 2015";
		
		assertEquals("Function returns incorrect number of days between two dates!",DatesCalc.getDiff(D1,D2,DatesCalc.DAY),days);
		assertEquals("Function returns incorrect number of days between two dates!",DatesCalc.getDiff(D1,D2,DatesCalc.MONTH),months);
		assertEquals("Function returns incorrect number of days between two dates!",DatesCalc.getDiff(D1,D2,DatesCalc.YEAR),years);

	}
	
	@Test
	public void testUniqueID() throws SQLException{
		String id="I16";
		
		assertEquals("The listed Unique ID's aren't matching",id, US22.Uniqueids());
		
	}
	
	@Test
	public void testMultipleBirth() throws SQLException, ParseException{
		String expectedSiblingList="\nPriyanka\nRadha\nPrashant\nSheetal\nPriya\nRishi";
		assertEquals("Sibling List doesn't match", expectedSiblingList, US14.multipleBirths());
	}

}