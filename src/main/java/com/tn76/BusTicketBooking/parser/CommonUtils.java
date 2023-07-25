/**
 * java
 * @version java : $$
 *
 * @module
 *
 * @purpose
 * @see
 *
 * @author K Adithyan
 *
 * @created        Aug 3, 2009 8:46:48 PM
 *   Last modified:
 *   $Id:
 *
 *   @bugs
 */

package com.tn76.BusTicketBooking.parser;

import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static java.util.Base64.getEncoder;


/**
 * The purpose of this class is to offer common utility methods that are not
 * specific to Telecom and are generic in nature.
 * @author K Adithyan
 */
public class CommonUtils
{
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger();

	public static final String STRING_FOR_EMPTY_FIELD = "-";
	public static final String STRING_FOR_NOT_APPLICABLE_FIELD = "-NA-";
	public static final String YES = "Yes";
	public static final String NO = "No";

	public static final char SINGLE_QUOTE = '\'';

	public static final int CALL_FROM_SERVER = 1;
	public static final int CALL_FROM_CLIENT = 2;

	/**
	 * Default vulnerable string
	 */
	public static final String VALIDATE_STRING_DEFAULT_VULNERABLE = ";&:%#$@!'\\";

	public static final List<String> VALIDATE_CSV_MACRO_STRING_VULNERABLE = Arrays.asList(new String[]{"=","-","+","|","\"","\r","\n","@","&","$","!","'",";",":","#","%","\\",">","<"});

	/**
	 * Default date format that is to be used with <code>java.util.SimpleDateFormat</code>
	 * for OTNMS
	 */
	public static final String DATE_FORMAT_FOR_FILE_NAME = "dd-MMM-yyyy_HH-mm-ss";
	private static final SimpleDateFormat date_formatter_for_filename = new SimpleDateFormat(DATE_FORMAT_FOR_FILE_NAME);

	private static final String DATE_FORMAT_FOR_VIEW = "dd-MMM-yyyy HH:mm:ss";
	private static final String DATE_FORMAT_WITH_TZ_FOR_VIEW = "dd-MMM-yyyy HH:mm:ss Z";

	private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER_FOR_VIEW = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT_FOR_VIEW);
		}
	};
	
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER_WITH_TZ_FOR_VIEW = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT_WITH_TZ_FOR_VIEW));

	private static final String NULL = "null";

	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, Integer.MAX_VALUE, 24 * 60 * 60, 
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), getNamedThreadFactory("CommonUtilsExecutor-"));

    public static <T> T readObject(String fileName)
    {
        long s = System.currentTimeMillis();
        File file = new File(fileName);

        if (!file.exists())
        {
            logger.info("There is no object for fileName = " + fileName);
            return null;
        }

        ObjectInputStream ois = null;
        try
        {
            ois = new ObjectInputStream(new FileInputStream(file));
            return (T) ois.readObject();
        }
        catch (Exception ex)
        {
            logger.error("Error while reading file:"+ fileName, ex);
            return null;
        }
        finally
        {
            try
            {
                if (ois != null)
                    ois.close();
            }
            catch (IOException ex)
            {
                logger.error("Error while reading file:"+ fileName, ex);
            }

            long e = System.currentTimeMillis();

            if(logger.isDebugEnabled())
                logger.debug("Finished file : " + fileName + ". Time taken = " + (e - s) + " ms");
        }
    }


    /**
     *
     * @param fileName
     * @param o
     * @param <T>
     * @return
     */
    public static <T> boolean writeObject(String fileName, T o)
    {
        File file = new File(fileName);
        return writeObject(file, o);
    }

    /**
     *
     * @param file
     * @param o
     * @param <T>
     * @return
     */
    public static <T> boolean writeObject(File file, T o)
    {
        long s = System.currentTimeMillis();

        ObjectOutputStream oos = null;
        boolean res = false;
        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(o);
            return (res = true);
        }
        catch (IOException ex)
        {
            logger.error("Error while writing file:"+ file.getName(), ex);
            return (res = false);
        }
        finally
        {
            try
            {
                if (oos != null)
                    oos.close();
            }
            catch (IOException ex)
            {
                logger.error("Error while writing file:" + file.getName(), ex);
            }
            long e = System.currentTimeMillis();

            if(logger.isDebugEnabled())
                logger.debug("Finished writing in file : " + file.getName() + ". Result = " + res + ". Time taken = " + (e - s) + " ms");
        }
    }

	/**
	 * The purpose of this method is to validate vulnerable characters in a text.
	 * The method finds the characters of <code>vulnerable</code> object in
	 * <code>text</code> object.
	 *
	 * @param text String that is to be validated against the list of vulnerable
	 * characters.
	 * @param vulnerable String containing only vulnerable characters.
	 * @return If any one character in <code>vulnerable</code> argument is available in
	 * <code>text</code> argument, then the method will return false.
	 * Otherwise, true.
	 */
	public static boolean validateString(String text, String vulnerable)
	{
		for (int i = 0; i < vulnerable.length(); i++)
			if (text.indexOf(vulnerable.charAt(i)) != -1)
				return false;

		return true;
	}

	/**
	 * This method calls
	 * <code>validateString(text, VALIDATE_STRING_DEFAULT_VULNERABLE)</code>
	 * @param text String that is to be validated against the list of vulnerable
	 * characters.
	 * @return value returned by
	 * <code>validateString(text,  VALIDATE_STRING_DEFAULT_VULNERABLE)</code>
	 * @see CommonUtils#validateString(String, String)
	 * @see CommonUtils#VALIDATE_STRING_DEFAULT_VULNERABLE
	 */
	public static boolean validateString(String text)
	{
		return validateString(text, VALIDATE_STRING_DEFAULT_VULNERABLE);
	}

	public static long parseFormattedTimeStamp(String formattedTimeStamp) throws ParseException
	{
		if (nullOrEmpty(formattedTimeStamp))
			return -1;

		synchronized (date_formatter_for_filename)
		{
			return date_formatter_for_filename.parse(formattedTimeStamp).getTime();
		}
	}

	public static String getFormattedTimeStampForNow()
	{
		return getFormattedTimeStamp(System.currentTimeMillis());
	}

	public static String getFormattedTimeStamp(Timestamp timestamp){
		return getFormattedTimeStamp(timestamp.getTime());
	}

	public static String getFormattedTimeStamp(long timeInMillis)
    {
		if(timeInMillis <= 0) 
			return STRING_FOR_EMPTY_FIELD;
		
        Date date = new Date(timeInMillis);
		synchronized (date_formatter_for_filename)
		{
			return date_formatter_for_filename.format(date);
		}
    }

	public static String getFormattedTimeStampForView(Timestamp timestamp)
	{
		return getFormattedTimeStampForView(timestamp.getTime());
	}

	public static String getFormattedTimeStampForView(long timeInMillis)
    {
		if(timeInMillis <= 0) 
			return STRING_FOR_EMPTY_FIELD;
		
        Date date = new Date(timeInMillis);
		return DATE_FORMATTER_FOR_VIEW.get().format(date);
    }
	
	public static String getFormattedTimeStampForView(Date date)
	{
		return DATE_FORMATTER_FOR_VIEW.get().format(date);
	}

	public static String getFormattedTimeStampWithTimeZoneForView(Date date)
	{
		return DATE_FORMATTER_WITH_TZ_FOR_VIEW.get().format(date);
	}

	/**
	 * added to get time in milliseconds from given the Date String
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static long getTimeInMilliSecs(String time)
    {
		Date date;
		synchronized (date_formatter_for_filename) {
			try {
				date = date_formatter_for_filename.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
				return 0L;
			}
			return date.getTime();
		}
    }

	/**
	 * Given an Input duration is MilliSeconds, this Method returns the duration in hh:mm:ss format.
	 * For ex: if Input is 60000 the output is 00:01:00
	 */
	public static String convertMillisToHours(long millis)
	{
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
	}

	public static String[] getImageWriterFormatNames()
	{
		String formats[] = ImageIO.getWriterFormatNames();
		List<String> v = new ArrayList<String>();

		for (int i = 0; i < formats.length; i++)
		{
			String lc = formats[i].toLowerCase();

			if (lc.equals("wbmp") || lc.equals("gif"))
				continue;

			if (!v.contains(lc))
				v.add(lc);
		}

		return v.toArray(new String[0]);
	}

	/**
	 * This method is to be used only for shot living tasks. This method is
	 * available to avoid thread creation overhead for short living jobs.
	 *
	 * If this method is used for long running tasks, then it is possible that
	 * this pool is exhausted and threads are not available for short living
	 * tasks for which this is designed.
	 *
	 * @param r
	 */
	public static void executeInNewThread(Runnable r)
	{
		threadPoolExecutor.execute(r);
	}

	public static ThreadPoolExecutor getThreadPoolExecutor()
	{
		return threadPoolExecutor;
	}

	public static <T> String getCommaSeparatedValues(Collection<T> collection)
	{
		return getDelimitedStringOfValues(collection, ',', false);
	}
	
	public static <T> String getCommaSeparatedValues(Collection<T> collection, boolean isSpaceRequired)
	{
		return getDelimitedStringOfValues(collection, ',', false, isSpaceRequired);
	}

	/*public static String escapeCsv(String string)
    {
		for (int i = 0; i < VALIDATE_CSV_MACRO_STRING_VULNERABLE.length; i++)
			string = org.apache.commons.lang3.StringUtils.removeStart(string, VALIDATE_CSV_MACRO_STRING_VULNERABLE[i]);
		return string;
        //return org.apache.commons.lang.StringUtils.replaceEach(string, VALIDATE_CSV_MACRO_STRING_VULNERABLE, VALIDATE_CSV_MACRO_STRING_REPLACEMENT);
    }*/


	public static String getHyphenSeparatedValues(String... items)
	{
		return getDelimitedStringOfValues(Arrays.asList(items), '-', false);
	}

	public static String getCommaSeparatedValues(String... items)
	{
		return getDelimitedStringOfValues(Arrays.asList(items), ',', false);
	}

	public static String getDollarSeparatedValues(String... items) {
        return getDelimitedStringOfValues(Arrays.asList(items), '$', false);
    }
    
    public static String getUnderscoreSeparatedValues(String... items)
    {
	    return getDelimitedStringOfValues(Arrays.asList(items), '_', false);
    }

	public static String getSlashSeparatedValues(String... items)
	{
		return getDelimitedStringOfValues(Arrays.asList(items), '/', false);
	}

	/**
	 *
	 * @param str
	 * @return
     */
	public static String encloseWithSingleQuotes(String str)
	{
		if (str == null)
		{
			str = "";
		}

		return SINGLE_QUOTE + str + SINGLE_QUOTE;
	}

	public static <T> String getDelimitedStringOfValues(Collection<T> collection, char delimiter, boolean isQuotesRequired, boolean isSpaceRequired)
	{
		if (collection == null || collection.isEmpty())
			return "";

		int size = collection.size();
		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (Iterator<T> itr = collection.iterator(); itr.hasNext(); i++)
		{
			boolean isLast = (i == size - 1);
			if(isQuotesRequired)
				sb.append("'");

			T o = itr.next();

			String str;

			if (o == null)
				str = "";
			else
				str = o.toString();

			sb.append(str);

			if(isQuotesRequired)
				sb.append("'");

			if (!isLast)
				sb.append(delimiter);
			
			if(isSpaceRequired && !isLast)
				sb.append(" ");
		}

		return sb.toString();
	}

	public static <T> String getDelimitedStringOfValues(Collection<T> collection, char delimiter, boolean isQuotesRequired)
	{
		return getDelimitedStringOfValues(collection, delimiter, isQuotesRequired, false);
	}

	/**
	 * This method replaces the <code>"</code> character with
	 * <code>\\\\\"</code> so that the returned string can be
	 * used in where clause of SQL queries.
	 * @param str input string for replacement
	 * @return modified string
	 */
	public static String getDBString(String str)
	{
        if (CommonUtils.nullOrEmpty(str))
            return str;

        if (str.indexOf("'") == -1)
			return str;
		else
			return str.replaceAll("'", "''");
	}

    public static <T> T createNewInstanceOf(Class<T> clazz) throws Exception
    {
        if (clazz == null)
            throw new Exception("class name is null or empty");

        Class<T> c = (Class<T>) Class.forName(clazz.getName());
        return c.newInstance();
    }

	public static <T> T createNewInstanceOf(String className) throws Exception
	{
		if (nullOrEmpty(className))
			throw new Exception("class name is null or empty");

		Class<T> c = (Class<T>) Class.forName(className);
		return c.newInstance();
	}

	public static <T> T createNewInstanceOf(String className, Class[] parameterTypes, Object[] initargs) throws Exception
	{
		Class<T> cls = (Class<T>) Class.forName(className);
		Constructor<T> cons = cls.getConstructor(parameterTypes);
		return cons.newInstance(initargs);
	}

	/**
	 * This method returns the difference between two maps. In other words, it
	 * removes the mappings with the same keys from both the maps and thus ensures
	 * that each of the returned maps contain only the mappings that are not
	 * available in the other map. This method will not modify the input maps.
	 * Instead it makes a local copy of the input maps. Common mappings are removed
	 * only in the copied maps and returned as an array of just 2 maps in the same
	 * order as the input parameters
	 *
	 * @param <K>	Definition of Key in the Map
	 * @param <V>	Definition Value in the Map
	 * @param map1	One of the the maps for which difference is to be found
	 * @param map2	Second of the maps for which difference is to be found
	 * @return	This method returns an array containing two diffed maps in same
	 * order as the maps are supplied to this method as parameter
	 */
	public static <K, V> Map<K, V>[] diff(Map<K, V> map1, Map<K, V> map2) throws Exception
	{
		Map<K, V> map1Copy = map1.getClass().newInstance();
		Map<K, V> map2Copy = map2.getClass().newInstance();

		map1Copy.putAll(map1);
		map2Copy.putAll(map2);

		Set<Entry<K, V>> commonElementsSet = new HashSet(map1Copy.entrySet());
		commonElementsSet.retainAll(map2Copy.entrySet());

		map1Copy.entrySet().removeAll(commonElementsSet);
		map2Copy.entrySet().removeAll(commonElementsSet);

		return new Map[]{map1Copy, map2Copy};
	}

	/**
	 * This method returns the difference between two sets. In other words, it
	 * removes the mappings with the same keys from both the sets and thus ensures
	 * that each of the returned sets contain only the elements that are not
	 * available in the other set. This method will not modify the input sets.
	 * Instead it makes a local copy of the input sets. Common elements are
	 * removed only in the copied sets and returned as an array of just 2 sets in
	 * the same order as the inputs parameters
	 *
	 * @param <K>	Definition of Key in the Map
	 * @param set1	One of the the maps for which difference is to be found
	 * @param set2	Second of the maps for which difference is to be found
	 * @param onlyInFirst empty set for filling the elements that are only in set1
	 * @param onlyInSecond empty set for filling the elements tht are only in set2
	 * @return	This method returns an array containing two diffed sets in same
	 * order as the sets are supplied to this method as parameter
	 */
	public static <K> void diff(Set<K> set1, Set<K> set2, Set<K> onlyInFirst, Set<K> onlyInSecond)
	{
		onlyInFirst.addAll(set1);
		onlyInSecond.addAll(set2);

		Set<K> commonElementsSet = new HashSet<K>(onlyInFirst);
		commonElementsSet.retainAll(onlyInSecond);

		onlyInFirst.removeAll(commonElementsSet);
		onlyInSecond.removeAll(commonElementsSet);
	}

	public static String preparePlaceHolders(int length) {
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < length;) {
	        builder.append("?");
	        if (++i < length) {
	            builder.append(",");
	        }
	    }
	    return builder.toString();
	}



	/**
	 *
	 * @param <K>	Definition of Key in the Map
	 * @param set1	One of the the maps for which difference is to be found
	 * @param set2	Second of the maps for which difference is to be found
	 * @param onlyInFirst empty set for filling the elements that are only in set1
	 * @param onlyInSecond empty set for filling the elements that are only in set2
	 * @param commonElements empty set for filling the elements that are common to both the sets
	 */
	public static <K> void diff(Set<K> set1, Set<K> set2, Set<K> onlyInFirst, Set<K> onlyInSecond, Set<K> commonElements)
	{
		onlyInFirst.addAll(set1);
		onlyInSecond.addAll(set2);

		commonElements.addAll(set1);
		commonElements.retainAll(onlyInSecond);

		onlyInFirst.removeAll(commonElements);
		onlyInSecond.removeAll(commonElements);
	}

	public static String[] convert2DArrayWithOneColumnTo1DArray(String[][] array)
	{
		if (array == null)
			return null;

		if (array.length == 0)
			return new String[0];

		String[] ret = new String[array.length];

		for (int i = 0; i < array.length; i++)
			ret[i] = array[i][0];

		return ret;
	}

	public static long[] convert2DArrayWithOneColumnTo1DLongArray(String[][] array)
	{
		if (array == null)
			return null;

		if (array.length == 0)
			return new long[0];

		long[] ret = new long[array.length];

		for (int i = 0; i < array.length; i++)
			try {
				ret[i] = Long.parseLong(array[i][0]);
			} catch (NumberFormatException e) {
				ret[i] = 0L;
			}

		return ret;
	}


	public static List<String> convert2DArrayWithOneColumnToListOfString(String[][] array)
	{
		if (array == null)
			return null;

		if (array.length == 0)
			return Collections.emptyList();

		List<String> ret = new ArrayList<String>();

		for (int i = 0; i < array.length; i++)
			ret.add(array[i][0]);

		return ret;
	}

	public static List<Integer> parseNumberRanges(String str)
	{
		String[] ranges = str.split(",");

		List<Integer> list = new ArrayList<Integer>();

		for (String range : ranges)
		{
			if (range == null || range.isEmpty())
				continue;

			String v = range.trim();

			if (v.startsWith("-"))
				continue;

			if (v.contains("-"))
			{
				String[] ns = v.split("-");

				int first = Integer.parseInt(ns[0]);
				int second = Integer.parseInt(ns[1]);

				if (first > second)
					continue;

				for (int i = first; i <= second; i++)
					list.add(i);
			}
			else
			{
				list.add(Integer.parseInt(v));
			}
		}

		return list;
	}

	/**
	 * Currently, this method does not support equality check for Array objects
	 *
	 * @param <T>
	 * @param one
	 * @param two
	 * @return
	 */
	public static <T> boolean objectEquals(T one, T two)
	{
		if (one == two)
			return true;

		if (one == null || two == null)
			return false;
		
		return one.equals(two);
	}

	public static <T> boolean objectNotEquals(T one, T two)
	{
		if (one == two)
			return false;

		if (one == null || two == null)
			return true;

		return !one.equals(two);
	}

    public static String getPaddedString(String prefix, String suffix, char filler, int length) {
		if (prefix.length() + suffix.length() >= length) {
			return prefix + suffix;
    }
		StringBuilder sb = new StringBuilder(length);
		sb.append(prefix);
		for (int i = prefix.length(), max = length - suffix.length(); i < max; i++) {
			sb.append(filler);
		}
		sb.append(suffix);
		return sb.toString();
	}

    public static class StringOutputStream extends OutputStream
	{
		private StringBuilder builder = new StringBuilder();

		@Override
		public void write(int b) throws IOException
		{
			builder.append((char) b);
		}

		public String getString()
		{
			return builder.toString();
		}
	}

	public static class StringInputStream extends InputStream
	{
		private String s;
		private int loc = 0;

		public StringInputStream(String s)
		{
			this.s = s;
		}

		@Override
		public int read() throws IOException
		{
			if (loc > s.length() - 1)
				return -1;

			int ch = s.charAt(loc);
			loc++;
			return ch;
		}
	}

	/**
	 * @deprecated Use {@link #nullOrEmpty(Collection)}
	 *
	 * @param collection
	 * @return
	 */
	@Deprecated
	public static boolean isCollectionNullOrEmpty(Collection<?> collection)
	{
		return collection == null || collection.isEmpty();
	}

	/**
	 * @deprecated Use {@link #(Collection)}
	 *
	 * @param collection
	 * @return
	 */
	@Deprecated
	public static boolean isCollectionNotNullAndNotEmpty(Collection<?> collection)
	{
		return collection != null && !collection.isEmpty();
	}

	/**
	 * @deprecated Use {@link #nullOrEmpty(Map)}
	 *
	 * @param map
	 * @return
	 */
	@Deprecated
	public static boolean isMapNullOrEmpty(Map<?, ?> map)
	{
		return map == null || map.isEmpty();
	}

	/**
	 * @deprecated Use {@link #(Map)}
	 * @param map
	 * @return
	 */
	@Deprecated
	public static boolean isMapNotNullAndNotEmpty(Map<?, ?> map)
	{
		return map != null && !map.isEmpty();
	}

	private static int batches[] ={ 1 , 3 , 7 , 17 , 29 , 37 , 47 , 103 };

	public static List<Integer> fetchDBBatchSizes (int n , int maxBatchSize)
	{
		List<Integer> listOfPlaceHolders = new LinkedList<Integer>();
		int totalValues = n;
		int length = batches.length-1;
		while(totalValues > 0)
		{
			for(int i = length ; i >= 0  ; i-- )
			{
				if( batches[i] <= maxBatchSize && totalValues >= batches[i] )
				{
					totalValues -= batches[i];
					listOfPlaceHolders.add(batches[i]);
					length = i;
					break;
				}
			}
		}

		return listOfPlaceHolders;

	}

	/**
	 * @deprecated Use {@link #nullOrEmpty(String)}
	 * @param string
	 * @return
	 */
	@Deprecated
	public static boolean isStringNullOrEmpty(String string)
	{
		return nullOrEmpty(string, true);
	}

	/**
	 * Use NOT of {@link #nullOrEmpty(String)}
	 * @param string
	 * @return
	 */
	@Deprecated
	public static boolean isStringNotNullAndNotEmpty(String string)
	{
		return !nullOrEmpty(string, true);
	}

	public static boolean nullOrEmpty(String string, boolean trimBeforeEmptyCheck)
	{
		if (string == null)
			return true;

		return trimBeforeEmptyCheck ? string.trim().isEmpty() : string.isEmpty();
	}

	/**
	 * @deprecated Use {@link #nullOrEmpty(String, boolean)}
	 * @param string
	 * @param trimBeforeEmptyCheck
     * @return
     */
	public static boolean isStringNullOrEmpty(String string, boolean trimBeforeEmptyCheck)
	{
		return nullOrEmpty(string, trimBeforeEmptyCheck);
	}

	public static Process executeCommand(CommandInput commandInput) throws Exception
	{
		logger.info("Executing command [" + commandInput + "]");

		ProcessBuilder pb = new ProcessBuilder(commandInput.getCommands());

		if (commandInput.getWorkingDirectory() != null)
			pb.directory(new File(commandInput.getWorkingDirectory()));

		return pb.start();
	}

	public static class CommandInput
	{
		private String workingDirectory;
		private List<String> commands;

		public CommandInput(String workingDirectory, List<String> commands)
		{
			this.workingDirectory = workingDirectory;
			this.commands = commands;
		}

		public void addToCommands(String command)
		{
			if (commands == null)
				commands = new ArrayList<String>();

			commands.add(command);
		}

		public String getWorkingDirectory()
		{
			return workingDirectory;
		}

		public void setWorkingDirectory(String workingDirectory)
		{
			this.workingDirectory = workingDirectory;
		}

		public List<String> getCommands()
		{
			return commands;
		}

		public void setCommands(List<String> commands)
		{
			this.commands = commands;
		}

		@Override
		public String toString()
		{
			return "CommandInput{" + "workingDirectory=" + workingDirectory + ", commands=" + commands + '}';
		}
	}

	/**
	 * @author Mr.Ramachandran
	 *
	 */
	private static class NamedThreadFactory implements ThreadFactory
	{
		private String threadName;
		private boolean appendThreadNo;
		private ThreadFactory customThreadFactory;

		private AtomicInteger counter;

		NamedThreadFactory(String threadName, boolean appendThreadNo, ThreadFactory customThreadFactory)
		{
			this.threadName = threadName;
			this.appendThreadNo = appendThreadNo;
			this.customThreadFactory = customThreadFactory;

			if (this.appendThreadNo)
				this.counter = new AtomicInteger(1);
		}

		private Thread getThread(Runnable r, String name)
		{
			if (customThreadFactory == null)
			{
				return new Thread(r, name);
			}
			else
			{
				Thread t = customThreadFactory.newThread(r);
				t.setName(name);
				return t;
			}
		}

		private String getName()
		{
			if (counter == null)
				return threadName;
			else
				return threadName + counter.getAndIncrement();
		}

		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = getThread(r, getName());
			return t;
		}
	}

	/**
	 * @see #getNamedThreadFactory(String, boolean, ThreadFactory)
	 * @param threadNamePrefix
	 * @return
	 */
	public static ThreadFactory getNamedThreadFactory(String threadNamePrefix)
	{
		return getNamedThreadFactory(threadNamePrefix, true, null);
	}

	/**
	 * getNameThreadFactory --
	 *    @param threadName - ThreadName/prefix for generating name for new threads.
	 *    @param appendThreadNo - boolean for deciding whether to append thread no in the name of the Thread.
	 *    @param customFactory - ThreadFactory for generating new Threads can be specified, it is an optional parameter.
	 *                            null can also be passed in situations where user don't need custom thread factory.
	 *
	 *    @return ThreadFactory
	 *    @author Mr.Ramachandran
	 */
	public static ThreadFactory getNamedThreadFactory(String threadName, boolean appendThreadNo, ThreadFactory customFactory)
	{
		return new NamedThreadFactory(threadName, appendThreadNo, customFactory);
	}

	public static String getLineSeparator()
	{
		return System.getProperty("line.separator");
	}

	public static <T> List<List<T>> getListOfList(Collection<T> values)
	{
		List<List<T>> listOfList = new ArrayList<List<T>>(values.size());
		for (T v : values)
			listOfList.add(Collections.singletonList(v));
		return listOfList;
	}

	public static <T> List<List<T>> split(List<T> items, int batchCount)
	{
		if (nullOrEmpty(items))
			return Collections.emptyList();

		if(items.size() < batchCount)
			return Collections.singletonList(items);

		List<List<T>> ret = new ArrayList<List<T>>();

		int size = items.size();

		for (int i = 0; i < size; i += batchCount)
		{
			List<T> thisList = new ArrayList<T>(batchCount);

			int end = i + batchCount;
			if (end > size)
				end = size;
			for (int r = i; r < end; r++)
				thisList.add(items.get(r));

			ret.add(thisList);
		}

		return ret;
	}

	public static <K, V> Map<V, K> getReverseMap(Map<K, V> map) throws Exception
	{
		if (map == null)
			return null;

		Map<V, K> newMap = map.getClass().newInstance();
		for (Entry<K, V> entry : map.entrySet()) {
			newMap.put(entry.getValue(), entry.getKey());
		}
		return newMap;
	}

	public static void sleep(long millis)
	{
		 try
		 {
			 Thread.sleep(millis);
		 }
		 catch (InterruptedException ex)
		 {
			 logger.info("", ex);
		 }
	}

	public static void sleep(long howlong, TimeUnit timeUnit)
	{
		sleep(timeUnit.toMillis(howlong));
	}

	/**
	 * arrayAsList --
	 *    @param array - You can give both primitive arrays, as well as object arrrays.
	 *    @return List<U> - In the primitive case It'll return List of Primitive Wrapper objects.
	 *                      For other object array case, It'll return the list of those objects.
	 *    @see
	 *    @bugs
	 */
	public static <U, V> List<U> arrayAsList(V array)
	{
		if (array == null)
			return null;

		Class<? extends Object> arrayClass = array.getClass();

		if (!arrayClass.isArray())
			throw new IllegalArgumentException("Only arrays are expected...");

		int l = Array.getLength(array);

		if (l == 0)
			return null;

		List<U> list = new ArrayList<U>(l);

		for (int idx = 0; idx < l; idx++)
		{
			Object o = Array.get(array, idx);
			list.add((U) o);
		}

		return list;
	}

	public static List<String> getLocalNetworkAddresses() throws Exception
	{
		return getLocalNetworkAddresses(true);
	}

	public static List<String> getLocalIPV4NetworkAddresses() throws Exception
	{
		return getLocalNetworkAddresses(false);
	}
	
	public static String getFirstLocalIPV4Address() throws Exception
	{
		List<String> addresses = getLocalNetworkAddresses(false);
		return nullOrEmpty(addresses) ? "localhost" : addresses.get(0);
	}

	public static List<String> getLocalNetworkAddresses(boolean includeIPv6Addresses) throws Exception
	{
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

		List<String> ret4 = new ArrayList<>();
		List<String> ret6 = new ArrayList<>();

		while (nis.hasMoreElements())
		{
			NetworkInterface ni = nis.nextElement();
			Enumeration<InetAddress> ias = ni.getInetAddresses();

			if (ni.isLoopback())
				continue;

			while (ias.hasMoreElements())
			{
				InetAddress ia = ias.nextElement();
				if (ia instanceof Inet4Address)
				{
					String ha = ia.getHostAddress();
					if (!ret4.contains(ha))
						ret4.add(ha);
				}

				if (includeIPv6Addresses)
				{
					if (ia instanceof Inet6Address)
					{
						String ha = ia.getHostAddress();
						if (!ret6.contains(ha))
							ret6.add(ha);
					}
				}
			}
		}

		List<String> ret = new ArrayList<>();
		ret.addAll(ret4);
		ret.addAll(ret6);
		return ret;
	}

	public static String getMd5ChecksumForFile(String fileName)
	{
		FileChannel fc = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			Path path = Paths.get(fileName);
			fc = FileChannel.open(path, StandardOpenOption.READ);

			ByteBuffer bb = ByteBuffer.allocate((int) fc.size());

			fc.read(bb);
			bb.position(0);
			md.update(bb);

			byte[] digest = md.digest();
			return convertByteArrayToHexString(digest);
		}
		catch (Exception ex)
		{
			logger.error("Exception while finding the MD5 of the file [" + fileName + "]", ex);
			return null;
		}
		finally
		{
			if (fc != null)
			{
				try
				{
					fc.close();
				}
				catch (IOException ex)
				{
					logger.error("", ex);
				}
			}
		}
	}

	public static String convertByteArrayToHexString(byte[] array)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++)
		{
			String hex = Integer.toHexString(0xff & array[i]);
			if (hex.length() == 1)
			{
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 		MD5Checksum for the given string.
	 *
	 * @param s
	 * @return
	 */

	/**
	 * 		MD5 hash code for the given string. Given string is converted to MD5Checksum and it's hash code is returned.
	 *
	 * @param s
	 * @return
	 */

	/**
	 * @deprecated Use
	 * @param array
	 * @param <T>
	 * @return
	 */
	@Deprecated
	public static<T> boolean isArrayNullOrEmpty(T[] array)
	{
		return array == null || array.length == 0;
	}

	/**
	 * @deprecated Use
	 * @param array
	 * @param <T>
	 * @return
	 */
	@Deprecated
	public static<T> boolean isArrayNotNullAndNotEmpty(T[] array)
	{
		return array != null && array.length != 0;
	}

	public static String replaceSlashWithFileSeparator(String path)
	{
		return path.replace('/', File.separatorChar);
	}

	/**
	 * @deprecated Use {@link #getFileContentAsString(String)}
	 * @param path
	 * @return
	 */
	@Deprecated
	public static String getTextFileContent(String path)
	{
		try
		{
			return getFileContentAsString(path);
		}
		catch (Exception ex)
		{
			return "";
		}
	}

	public static <T> boolean collectonContains(Collection<T> collection, T object)
	{
		return !nullOrEmpty(collection) ? collection.contains(object) : false;
	}

	public static File[] listXmlFiles(String directoryName)
	{
		return listFilesWithExtension(directoryName, "xml");
	}

	public static File[] listXmlFilesRecursively(String directoryName)
	{
		if(CommonUtils.nullOrEmpty(directoryName, true))
			return new File[0];

		return listXmlFilesRecursivelyWithExtension(new File(directoryName), "xml").toArray(new File[0]);
	}

	public static List<File> listXmlFilesRecursivelyWithExtension(File dir, String fileExtension)
	{
		List<File> filesList = new ArrayList<>();
		File[] files = listValidFiles(dir, fileExtension);

		if(CommonUtils.nullOrEmpty(Arrays.asList(files)))
			return filesList;

		for (File file : files)
		{
			if (file.isDirectory())
				filesList.addAll(listXmlFilesRecursivelyWithExtension(file, fileExtension));
			else
				filesList.add(file);
		}

		return filesList;
	}

	public static File[] listFilesWithExtension(String directoryName, final String fileExtension)
	{
		if (nullOrEmpty(directoryName) || nullOrEmpty(fileExtension))
			return new File[0];

		File directory = new File(directoryName);
		File[] files = listValidFiles(directory, fileExtension);

		 return files;
	}

	private static File[] listValidFiles(File directory, final String fileExtension)
	{
		File[] files = directory.listFiles(new FilenameFilter()
		 {
			 @Override
			 public boolean accept(File dir, String name)
			 {
				 return !name.startsWith(".") && name.toLowerCase().endsWith(fileExtension.toLowerCase());
			 }
		 });

		 return files;
	}







	/**
	 * @deprecated Use {@link #(Collection)}
	 * @param collection
	 * @return
	 */
	@Deprecated
	public static int getSizeOfCollection(Collection<?> collection)
	{
		return collection == null ? 0 : collection.size();
	}

	/**
	 * @deprecated Use {@link #(Object[])}
	 * @param array
	 * @return
	 */
	@Deprecated
	public static int getSizeOfArray(Object[] array)
	{
		return array == null ? 0 : array.length;
	}

	/**
	 * Use {@link #(Map)}
	 * @param map
	 * @return
	 */
	@Deprecated
	public static int getSizeOfMap(Map<?, ?> map)
	{
		return map == null ? 0 : map.size();
	}

	public static String getFileContentAsString(String fileName) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		return readBufferedReaderAndClose(bufferedReader);
	}

	/**
	 * The passed InputStream will not be closed by this method. The caller to ensure that
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static String readInputStreamAsString(InputStream inputStream) throws Exception
	{
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
		return readBufferedReaderAndClose(bufferedReader);
	}
	
	public static String readBufferedReaderAndClose(BufferedReader bufferedReader) throws Exception
	{
		try
		{
			StringBuilder builder = new StringBuilder("");
			String line;
			while((line = bufferedReader.readLine())!=null)
			{
				builder.append(line);
				builder.append("\n");
			}
			return builder.toString();
		}
		finally
		{
			try
			{
				if (bufferedReader != null)
					bufferedReader.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	public static void deleteDir(File dir)
	{
		if (!dir.exists())
			return;

		File[] children = dir.listFiles();
		if (!nullOrEmpty(Arrays.asList(children)))
		{
			for (File child : children)
			{
				if (child.isDirectory())
					deleteDir(child);
				else
					child.delete();
			}
		}

		dir.delete();
	}

	public static void deleteOnExit(File fileOrDir)
	{
		if (!fileOrDir.exists())
			return;

		File[] children = fileOrDir.listFiles();

		assert children != null;
		if (!nullOrEmpty(Arrays.asList(children)))
		{
			for (File child : children)
			{
				if (child.isDirectory())
					deleteDir(child);
				else
					child.deleteOnExit();
			}
		}

		fileOrDir.deleteOnExit();
	}

	public static byte[] serialize(Object obj) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5120);
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		return bos.toByteArray();
	}

	public static <T> T deserialize(byte[] bytes) throws Exception
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);

		T v = (T) ois.readObject();
		bis.close();
		ois.close();
		return v;
	}

	/**
	 * 	Converts the given Object into a Long using {@link BigDecimal#longValue()} if it is a BigDecimal. Else it is type casted to Long and returned.
	 *
	 * @param o
	 * @return
	 */
	public static Long getLongIfBigDecimal(Object o)
	{
		if(o instanceof BigDecimal)
			return ((BigDecimal) o).longValue();
		else if (o instanceof String)
			return Long.valueOf((String) o);

		return (Long) o;
	}

	/**
	 * 	Converts the given Object into an Integer using {@link BigDecimal#intValue()} if it is a BigDecimal. Else it is type casted to Integer and returned.
	 *
	 * @param o
	 * @return
	 */
	public static Integer getIntegerIfBigDecimal(Object o)
	{
		if(o instanceof BigDecimal)
			return ((BigDecimal) o).intValue();
		return (Integer) o;
	}

	public static byte[] compress(byte[] data) throws IOException
	{
		long st = System.nanoTime();
		if (data == null)
			return null;

		Deflater deflater = new Deflater();
		deflater.setStrategy(Deflater.BEST_SPEED);

		deflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

		deflater.finish();
		byte[] buffer = new byte[10240];
		while (!deflater.finished())
		{
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		deflater.end();

		byte[] output = outputStream.toByteArray();

		double orig = data.length;
		double compressed = output.length;
		long end = System.nanoTime();
		logger.trace("actualLength = {}, compressedLength = {}, Compression ratio = {} %. Time taken to compress = {} micro sec",data.length, output.length, (100 - (compressed * 100 / orig)), (end - st) / 1000);

		return output;
	}

	public static byte[] uncompress(byte[] data) throws IOException, DataFormatException
	{
		if (data == null)
			return null;

		Inflater inflater = new Inflater();
		inflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[10240];

		while (!inflater.finished())
		{
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}

		outputStream.close();
		inflater.end();
		byte[] output = outputStream.toByteArray();

		return output;
	}

	public static boolean like(String str, String expr) {
	    expr = expr.toLowerCase(); // ignoring locale for now
	    expr = expr.replace(".", "\\."); // "\\" is escaped to "\" (thanks, Alan M)
	    // ... escape any other potentially problematic characters here
	    expr = expr.replace("?", ".");
	    expr = expr.replace("%", ".*");
	    str = str.toLowerCase();
	    return str.matches(expr);
	}

	public static List<String> collectChildrenXmlFilesRecursively(String directory)
	{
		return collectChildrenFileListRecursively(directory, null, "xml");
	}


	public static List<String> collectChildrenFilesRecursively(String directory)
	{
		return collectChildrenFileListRecursively(directory, null, null);
	}

	public static List<String> collectChildrenFilesRecursivelyAndReturnRelativePaths(String directory)
	{
		Path source = Paths.get(directory);
		
		List<String> children = collectChildrenFileListRecursively(directory, null, null);

		List<String> relativePaths = new ArrayList<>();
		
		for (String child : children)
			relativePaths.add(source.relativize(new File(child).toPath()).toFile().getPath());

		return relativePaths;
	}

	public static List<String> collectChildrenFileListRecursively(String directory, Set<String> exclusionPaths, String... requiredExtensions)
	{
		return FileTreeWalker.collectChildrenFileListRecursively(directory, exclusionPaths, requiredExtensions);
	}

	/**
	 * @param data
	 * @return Object
	 * @purpose  'STRING_FOR_EMPTY_FIELD' - hyphen will be returned
	 *  		 	- When the data is null
	 *  			- When the given data is a String Object and its value is null or empty.
	 *
	 *           Returns the object itself if it is not an instance of String.
	 */
	public static Object replaceWithHyphenIfNullOrEmpty(Object data)
	{
		if(data == null)
		{
			return STRING_FOR_EMPTY_FIELD;
		}

		if(data instanceof String)
		{
			String string = data.toString().trim();
			if(string.isEmpty() || string.equalsIgnoreCase(NULL))
				return STRING_FOR_EMPTY_FIELD;
		}
		return data;
	}

	private static class FileTreeWalker
	{
		public static List<String> collectChildrenFileListRecursively(String directory, Set<String> exclusionPaths, String... requiredExtensions)
		{
			Set<String> requiredExtSet = new HashSet<>();
			if (requiredExtensions != null)
				requiredExtSet.addAll(Arrays.asList(requiredExtensions));

			List<String> fileList = new ArrayList<>();

			fillChildrenFiles(new File(directory), fileList, directory, exclusionPaths, requiredExtSet);

			return fileList;
		}

		private static void fillChildrenFiles(File path, List<String> fileListWithAbsolutePath, String ultimateInputDirectory, Set<String> exclusionPaths, Set<String> requiredExtSet)
		{
			boolean excluded = getExclusionResult(exclusionPaths, path, ultimateInputDirectory, requiredExtSet);

			if (excluded)
				return;

			if (path.isFile())
			{
				fileListWithAbsolutePath.add(path.getAbsolutePath());
				return;
			}

			File[] children = path.listFiles();

			if (children == null || children.length == 0)
				return;

			for (File child : children)
				fillChildrenFiles(child, fileListWithAbsolutePath, ultimateInputDirectory, exclusionPaths, requiredExtSet);
		}

		private static boolean getExclusionResult(Set<String> exclusionPaths, File thisFile, String ultimateInputDirectory, Set<String> requiredExtSet)
		{
			if (thisFile.isDirectory())
				return false;

			if (ultimateInputDirectory.equals(thisFile.getAbsolutePath())) //this condition is useful when some file (not directory) is passed to this method
				return false;

			int trimLength = ultimateInputDirectory.length() + 1;

			logger.trace("ultimateInputDirectory = {}, thisFile = {}", ultimateInputDirectory, thisFile.getAbsolutePath());

			String relativePath = thisFile.getAbsolutePath().substring(trimLength);

			if (!nullOrEmpty(requiredExtSet))
			{
				String ext = thisFile.getName().substring(thisFile.getName().lastIndexOf('.') + 1);
				boolean required = requiredExtSet.contains(ext);

				if (!required)
					return true;
			}

			if (!nullOrEmpty(exclusionPaths))
			{
				for (String ex : exclusionPaths)
				{
					if (relativePath.startsWith(ex))
						return true;
				}
			}

			return false;
		}

	}

	/**
	 *
	 * @param objectArray
	 * @return StringArray
	 */
	public static String[] getStringArray(Object[] objectArray)
	{
		if (CommonUtils.nullOrEmpty(Arrays.asList(objectArray)))
			return null;

		String[] strArray = new String[objectArray.length];

		for (int i = 0; i < objectArray.length; i++)
		{
			strArray[i] = String.valueOf(objectArray[i]);
		}

		return strArray;
	}
	
	public static List<String> getStringList(Set<Long> values)
	{
		if (CommonUtils.nullOrEmpty(values))
			return null;

		List<String> list = new ArrayList<>(values.size());
		for (Long value : values)
			list.add(String.valueOf(value));

		return list;
	}
	
	public static List<String> getStringAsList(String value, String delimiter)
	{
		return getStringAsList(value, delimiter, false);
	}

	public static List<String> getStringAsList(String value, String delimiter, boolean trimAfterSplit)
	{
		String[] arr = getStringAsArray(value, delimiter, trimAfterSplit);
		return Arrays.asList(arr);
	}

	public static String[] getStringAsArray(String value, String delimiter)
	{
		return getStringAsArray(value, delimiter, false);
	}

	public static String[] getStringAsArray(String value, String delimiter, boolean trimAfterSplit)
	{
		if (nullOrEmpty(value))
		{
			return (new String[0]);
		}

		if (!trimAfterSplit)
		{
			return (value.trim().split(delimiter));
		}

		String[] arr = value.split(delimiter);

		for (int i = 0; i < arr.length; i++)
		{
			arr[i] = arr[i].trim();
		}

		return arr;
	}

	/**
	 * Converts Image to String to embed to webpage, etc
	 * <p>
	 *     {@link #convertStringifiedImageToImage(String)} will convert the String returned by this method back to
	 *     BufferedImage
	 * </p>
	 * @see #convertStringifiedImageToImage(String)
	 *
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public static String convertImageToString(BufferedImage image) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ImageIO.write(image, "jpeg", baos);
		baos.flush();
		byte[] imageAsByteArray = baos.toByteArray();
		baos.close();

		String imageAsString = new String(getEncoder().encode(imageAsByteArray));
		return imageAsString;
	}

	/**
	 * Extracting byte array from image File path
	 * @param imgPath
	 * @return
	 * @throws IOException
	 */
	public static byte[] extractBytes (File imgPath, String type) throws IOException {
		BufferedImage originalImage=ImageIO.read(imgPath);
		ByteArrayOutputStream baos =new ByteArrayOutputStream();
		ImageIO.write(originalImage, type, baos );
		baos.flush();
		byte[] imageAsByteArray = baos.toByteArray();
		baos.close();
		return imageAsByteArray;
	}

	/**
	 * Converts the Stringified image as returned by {@link #convertImageToString(BufferedImage)} back to BufferedImage
	 *
	 * @param stringifiedImage
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage convertStringifiedImageToImage(String stringifiedImage) throws IOException
	{
		byte[] imageByte = Base64.getDecoder().decode(stringifiedImage);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		BufferedImage image = ImageIO.read(bis);
		bis.close();
		return image;
	}

	public static String convertMapToString(Map<String, String> map)
	{
		if(nullOrEmpty(map))
			return "";

		StringBuilder str = new StringBuilder();
		int noOfKeys = map.size();
		int count = 0;
		for (Entry<String, String> entry : map.entrySet())
		{
			count++;
			str.append(entry.getKey());
			str.append("=");
			str.append(entry.getValue());

			if(count != noOfKeys)
				str.append(",");
		}
		return str.toString();
	}

	public static Map<String, String> convertStringifiedMapToMap(String stringifiedMap)
	{
		if(nullOrEmpty(stringifiedMap))
			return Collections.emptyMap();

		return stringifiedMap.contains(",") ?  convertToMap(stringifiedMap.split(",")) : convertToMap(new String[]{stringifiedMap});
	}

	private static Map<String, String> convertToMap(String[] additionalInfoKeyValue)
	{
		if(nullOrEmpty(Arrays.asList(additionalInfoKeyValue)))
			return Collections.emptyMap();

		Map<String, String> additionalInfoMap = new HashMap<>();
		for (String keyValue : additionalInfoKeyValue)
		{
			int index = keyValue.indexOf("=");

			if (index == -1)
				continue;

			String key = keyValue.substring(0, index);
			String value = keyValue.substring(index + 1, keyValue.length());

			if (!nullOrEmpty(key) && !nullOrEmpty(value))
				additionalInfoMap.put(key, value);
		}
		return additionalInfoMap;
	}
	
	public static <T> boolean arrayContains(T[] array, T objectToSearch)
	{
		if (nullOrEmpty(Arrays.asList(array)))
			return false;

		for (T t : array)
		{
			if (objectEquals(t, objectToSearch))
				return true;
		}
		
		return false;
	}

	public static String getString(Blob blob)
	{
		try {
			return new String(blob.getBytes(1L, (int) blob.length()));
		}
		catch (Exception e)
		{
			logger.error("Cannot convert blog to string due to Exception : ", e);
			return null;
		}
	}

	public static Object getObject(Blob blob)
	{
		try (ObjectInputStream in = new ObjectInputStream(blob.getBinaryStream())) {
			return in.readObject();
		} catch (Exception e) {
			logger.error("Exception while converting blob to userObejct" , e);
		}
		return null;
	}

	public static Blob getBlob(Object obj)
	{
		try
		{
			byte[] bytes = serialize(obj);
			return getBlob(bytes);

		}
		catch (Exception e)
		{
			logger.error("Exception while converting the object to blob ", e);
			return null;
		}
	}

	public static Blob getBlob(byte[] bytes) throws Exception {
		return new SerialBlob(bytes);
	}

	public static String getSopStringForTable(String[] header, Object[][] table)
	{
		if (nullOrEmpty(Collections.singleton(table)))
			return getSopStringForTable(Arrays.asList(header), Collections.emptyList());

		List<String[]> list = new ArrayList<>();

		for (Object[] orow : table)
		{
			String[] srow = new String[orow.length];

			for (int c = 0; c < srow.length; c++)
				srow[c] = String.valueOf(orow[c]);

			list.add(srow);
		}

		return getSopStringForTable(Arrays.asList(header), list);
	}

	public static String getSopStringForTable(List<String> header, List<String[]> table)
	{
		Map<Integer, Integer> colWiseMax = new HashMap<>();
		
		for (int c = 0; c < header.size(); c++)
			colWiseMax.put(c, header.get(c).length());

		for (String[] row : table)
		{
			for (int c = 0; c < row.length; c++)
				colWiseMax.put(c, Math.max(colWiseMax.get(c), String.valueOf(row[c]).length()));
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int c = 0; c < header.size(); c++)
		{
			if (c == 0)
				builder.append("| ");
			
			builder.append("%-").append(colWiseMax.get(c)).append("s |");
			
			if (c != header.size() - 1)
				builder.append(" ");
		}

		String format = builder.toString();
		String titleString = String.format(format, header.toArray(new String[0]));
		
		char[] lineArray = new char[titleString.length()];
		Arrays.fill(lineArray, '-');
		String line = new String(lineArray);

		StringBuilder sb = new StringBuilder();

		sb.append('\n');
		sb.append(line).append('\n');
		sb.append(titleString).append('\n');
		sb.append(line).append('\n');

		for (String[] row : table)
			sb.append(String.format(format, row)).append('\n');
		
		sb.append(line).append('\n');
		return sb.toString();
	}

	public static void main(String[] args)
	{
		List<String> header = Arrays.asList("Sl.No", "Name", "Age");
		List<String[]> data = new ArrayList<>();
		
		data.add(new String[] {"1", "Lemon", "2"});
		data.add(new String[] {"1", "Lemon Water Confusion", "2"});
		data.add(new String[] {"1", "India", null});
		data.add(new String[] {"1", "America", "-1"});

		System.out.println(getSopStringForTable(header, data));
	}

	public static int max(int[] details)
	{
		if (details == null || details.length == 0)
			return 0;

		int max = 0;

		for (int n : details)
		{
			if (n > max)
				max = n;
		}

		return max;
	}


	public static boolean nullOrEmpty(Collection<?> collection)
	{
		return collection == null || collection.isEmpty();
	}

	public static boolean nullOrEmpty(String string)
	{
		return nullOrEmpty(string, true);
	}

	public static boolean hasLength(String string)
	{
		return string != null && !string.isEmpty();
	}

	public static boolean nullOrEmpty(Map<?, ?> map)
	{
		return map == null || map.isEmpty();
	}

}
