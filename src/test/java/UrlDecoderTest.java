import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author zsw
 * @date 2020/07/25 16:36
 */

public class UrlDecoderTest {

	/**
	 * url解码
	 *
	 * @param s        待解码字符串
	 * @param encoding 字符编码
	 * @return 解码后的字符串
	 */
	private static String urlDecode(String s, String encoding) {
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return urlDecode(s);
		}
	}

	private static String urlDecode(String s) {
		System.out.println(s + s.length());
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	String urlencoder = "value=%26%2321608%3B%26%2319990%3B%26%2326106%3B%26%2329233%3B%26%2338647%3B%26%2320016%3B%26%2333993%3B";

	@Test
	public void urlDecoderTest(){
		System.out.println(urlDecode(urlencoder));
	}
}
