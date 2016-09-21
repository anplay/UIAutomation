package utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import com.google.common.base.Splitter;


public class StringHelper {

	public static final String NEW_LINE = System.getProperty("line.separator");
	private static final int FIRST_LINE_INDEX = 0;
	private static final int ENTIRE_REGEX_GROUP = 0;
	private static final int FIRST_REGEX_GROUP = 1;

	private StringHelper() {
	}

	public static boolean hasTextByRegex(String initialString, String regex) {
		return hasTextByPattern(initialString, Pattern.compile(regex));
	}

	public static String getTextByRegex(String initialString, String regex) {
		return getTextByRegexWithGroup(initialString, regex, ENTIRE_REGEX_GROUP);
	}

	public static String getTextByRegex(String initialString, String regex, String failureMessage) {
		return getTextByRegexWithGroup(initialString, regex, ENTIRE_REGEX_GROUP, failureMessage);
	}

	public static String getTextByFirstRegexGroup(String initialString, String regexWithGroup) {
		return getTextByRegexWithGroup(initialString, regexWithGroup, FIRST_REGEX_GROUP);
	}

	public static String getTextByFirstRegexGroup(String initialString, String regexWithGroup, String failureMessage) {
		return getTextByRegexWithGroup(initialString, regexWithGroup, FIRST_REGEX_GROUP, failureMessage);
	}

	public static String getTextByRegexWithGroup(String initialString, String regexWithGroup, int groupToReturn) {
		return getTextByPattern(initialString, Pattern.compile(regexWithGroup), groupToReturn);
	}

	public static String getTextByRegexWithGroup(String initialString, String regexWithGroup, int groupToReturn,
			String failureMessage) {
		return getTextByPattern(initialString, Pattern.compile(regexWithGroup), groupToReturn, failureMessage);
	}

	private static boolean hasTextByPattern(String initialString, Pattern pattern) {
		return pattern.matcher(initialString).find();
	}

	private static String getTextByPattern(String initialString, Pattern pattern, int groupToReturn) {
		Matcher matcher = pattern.matcher(initialString);
		if (matcher.find())
			return matcher.group(groupToReturn);
		return StringUtils.EMPTY;
	}

	private static String getTextByPattern(String initialString, Pattern pattern, int groupToReturn, String failureMessage) {
		String result = getTextByPattern(initialString, pattern, groupToReturn);
		if (result != StringUtils.EMPTY)
			return result;
		throw new IllegalStateException(failureMessage);
	}

	public static boolean hasEmptyString(Collection<String> stringCollection) {
		return stringCollection.stream().filter(StringUtils::isEmpty).findFirst().isPresent();
	}

	public static BigDecimal getBigDecimalFromString(String value) {
		return BigDecimal.valueOf(Double.parseDouble(value));
	}

	public static String getAmountNumber(String rawText) {
		String pattern = "(\\d+\\,)*(\\d+)(\\.\\d+)?";
		String result = getTextByRegex(rawText.trim(), pattern);
		if (result != StringUtils.EMPTY)
			return result;
		throw new IllegalArgumentException(String.format("Failed to extract number from element text: %s", rawText));
	}

	public static String randomString() {
		return UUID.randomUUID().toString();
	}

	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1)
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		else
			return string;
	}

	public static String getFirstLine(String s) {
		return s.split("\n")[FIRST_LINE_INDEX];
	}

	public static String convertTime(long time) {
		Date date = new Date(time);
		return new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS").format(date);
	}

	public static Map<String, String> getMapFromString(String pairSeparator, String keyValueSeparator, String mapString) {
		return Splitter.on(pairSeparator).withKeyValueSeparator(Splitter.on(keyValueSeparator).trimResults()).split(mapString);
	}

	public static String getHexByRgbColor(String rgbColor) {
		String[] numbers = rgbColor.replace("rgba(", "").replace(")", "").split(",");
		int r = Integer.parseInt(numbers[0].trim());
		int g = Integer.parseInt(numbers[1].trim());
		int b = Integer.parseInt(numbers[2].trim());
		return "#" + addLeadingZero(Integer.toHexString(r)) + addLeadingZero(Integer.toHexString(g))
				+ addLeadingZero(Integer.toHexString(b));
	}

	private static String addLeadingZero(String hex) {
		StringBuilder sb = new StringBuilder();
		sb.append(hex);
		if (sb.length() == 1)
			sb.insert(0, '0');
		return sb.toString();
	}
}
