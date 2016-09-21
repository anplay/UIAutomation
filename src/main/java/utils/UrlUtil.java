package utils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.MalformedURLException;
import java.net.URL;


public class UrlUtil {

	private UrlUtil() {
	}

	public static String getRelativeUrl(String absoluteURL) {
		try {
			URL url = new URL(absoluteURL);
			String path = url.getPath();
			String query = url.getQuery();
			if (isNotEmpty(query))
				return path + "?" + query;
			return path;
		} catch (MalformedURLException e) {
			return absoluteURL;
		}
	}
}
