package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class utc2Local {
	
	public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
		SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
		utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));//
		Date gpsUTCDate = null;
		try {
			gpsUTCDate = utcFormater.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return utcTime;
		}
		SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(gpsUTCDate.getTime());
		return localTime;
	}
	
}
