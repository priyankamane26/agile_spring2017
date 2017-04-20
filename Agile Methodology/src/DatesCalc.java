import java.util.Date;

public class DatesCalc {

	static float DAY = 1 , MONTH = 30.4f ,YEAR = 365.25f;
	
	@SuppressWarnings("deprecation")
	public static int getDiff(String D1, String D2, float unit){
		Date date1 = new Date(D1);
		Date date2 = new Date(D2);
		int diff = (int)(Math.round(((date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24)))/unit); 
		return Math.abs(diff);
	}
	
}
