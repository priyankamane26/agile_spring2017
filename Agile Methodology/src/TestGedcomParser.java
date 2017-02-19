import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;


public class TestGedcomParser{

	//GedcomParser gp = new GedcomParser();
	String birth="26 MAR 1991";
	@SuppressWarnings("deprecation")
	Date dob= new Date(birth);
	int age=25;

	// Testing whether it provides correct age
	@Test
	public void testGetAge(){
		assertEquals("Not a correct age", age, GedcomParser.getAge(dob));
	}

	// Testing exception thrown if the birthdate entered is greater than today's date.
	@Test(expected = IllegalArgumentException.class)
	public void testForInvalidBirthDate(){
		String birth="26 MAR 2017";
		@SuppressWarnings("deprecation")
		Date dob= new Date(birth);
		
		GedcomParser.getAge(dob);
	}
	
		/*
		 *  Exact real age is 1 years 11 months 28 days.
		 *  The function does consider the scenario where the birthdate 
		 *  is in the same month but the birth DAY is greater than today.
		 *  
		 *  Test should fail for age=2, but pass for age=1
		 *  
		 */
		@Test
		public void testSameMonthDifferentDay(){
			String birth="22 Feb 2015";
			@SuppressWarnings("deprecation")
			Date dob= new Date(birth);
			
			int age=1;
			//int age=2;  
			
			assertEquals("Age is incorrect", age, GedcomParser.getAge(dob));
			
		}
	
		// Testing whether the leap years are counted.
				@Test
				public void testLeapYearCalc(){
					String birth="29 Feb 2012";
					@SuppressWarnings("deprecation")
					Date dob= new Date(birth);
					
					int age=4;
					//int age=5;  
					
					assertEquals("Age does not consider leap year", age, GedcomParser.getAge(dob));
					
				}
				
		// Testing for Zero age or person born today.
		@Test
			public void testAgezero(){
				String birth="19 Feb 2017";
				@SuppressWarnings("deprecation")
				Date dob= new Date(birth);
				//age=0;
				//int age=1;  
				assertTrue("Age can not be Zero",GedcomParser.getAge(dob)==0);	
				//assertEquals("Age cannot be zero", age, GedcomParser.getAge(dob));
					
			}	
		
}
