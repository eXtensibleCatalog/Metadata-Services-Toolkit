import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;

//import java.util.Date;

DateTimeFormatter UTC_PARSER = ISODateTimeFormat.dateTimeParser();
DateTimeFormatter UTC_FORMATTER = ISODateTimeFormat.dateTime();
UTC_FORMATTER = UTC_FORMATTER.withZone(DateTimeZone.UTC);
Date d = new Date(UTC_PARSER.parseDateTime("2010-06-17T09:00:00-0400").getMillis());

/*
println String.format("%d", d.getTime()/60000);
println d.getTime();
println (d.getTime()+"");
println ((d.getTime()+"").substring(0, 8));

println System.currentTimeMillis()/60000;
*/

sb = new StringBuffer();
UTC_FORMATTER.printTo(sb, d.getTime());
println sb.toString();

