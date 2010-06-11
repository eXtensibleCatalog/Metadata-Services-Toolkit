import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

//import java.util.Date;

DateTimeFormatter UTC_PARSER = ISODateTimeFormat.dateTimeParser();
DateTimeFormatter UTC_FORMATTER = ISODateTimeFormat.dateTime();
Date d = new Date(UTC_PARSER.parseDateTime("2010-06-11T11:18:02-0400").getMillis());

println d.getTime()/60000;

println System.currentTimeMillis()/60000;

sb = new StringBuffer();
UTC_FORMATTER.printTo(sb, d.getTime());
println sb.toString();

