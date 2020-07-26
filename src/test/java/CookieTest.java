import org.junit.Test;

import javax.servlet.http.Cookie;

/**
 * @author zsw
 * @date 2020/07/25 11:34
 */
public class CookieTest {
	@Test
	public void test() {
		Cookie cookie = new Cookie("JSESSIONID","oiwheoiqhoieqwhoiehqoiwhe");
		System.out.println(cookie);
	}
}
