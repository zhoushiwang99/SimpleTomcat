package com.zsw.simpletomcat.connector.http;

/** this class copies methods from org.apache.catalina.connector.HttpRequestBase
 *  and org.apache.catalina.connector.http.HttpRequestImpl.
 *  The HttpRequestImpl class employs a pool of HttpHeader objects for performance
 *  These two classes will be explained in Chapter 4.
 */

import com.zsw.simpletomcat.Context;
import com.zsw.simpletomcat.Manager;
import com.zsw.simpletomcat.Session;
import com.zsw.simpletomcat.Wrapper;
import com.zsw.simpletomcat.connector.RequestStream;
import com.zsw.simpletomcat.util.Enumerator;
import com.zsw.simpletomcat.util.ParameterMap;
import com.zsw.simpletomcat.util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpRequest implements HttpServletRequest {

  private String contentType;
  private int contentLength;
  private InetAddress inetAddress;
  private InputStream input;
  private String method;
  private String protocol;

  protected Wrapper wrapper;

  // get请求携带的查询参数
  private String queryString;

  private String requestURI;
  private String serverName;
  private int serverPort;
  private Socket socket;
  private boolean requestedSessionCookie;
  private String requestedSessionId;
  private boolean requestedSessionURL;

  /**
   * post请求携带的表单数据
   */
  private String postString;


  protected HttpResponse response;

  private HttpRequestFacade facade;
  private Session session;

  /**
   * The request attributes for this request.
   */
  protected HashMap<String, Object> attributes = new HashMap<>();
  /**
   * The authorization credentials sent with this Request.
   */
  protected String authorization = null;
  /**
   * The context path for this request.
   */
  protected String contextPath = "";
  /**
   * The set of cookies associated with this Request.
   */
  protected ArrayList<Cookie> cookies = new ArrayList<>();
  /**
   * An empty collection to use for returning empty Enumerations.  Do not
   * add any elements to this collection!
   */
  protected static ArrayList empty = new ArrayList();
  /**
   * The set of SimpleDateFormat formats to use in getDateHeader().
   */
  protected SimpleDateFormat formats[] = {
          new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
          new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
          new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
  };

  protected final HashMap<String, ArrayList<String>> headers = new HashMap<>();

  protected ParameterMap<String, String[]> parameters = null;

  protected boolean parsed = false;
  protected String pathInfo = null;

  protected BufferedReader reader = null;

  protected ServletInputStream stream = null;

  protected Context context;

  public HttpRequest(InputStream input) {
    this.input = input;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public Context getContext() {
    return this.context;
  }

  public void addHeader(String name, String value) {
    name = name.toLowerCase();
    ArrayList<String> values = headers.computeIfAbsent(name, f -> new ArrayList<String>());
    values.add(value);
  }

  public void setResponse(HttpResponse response) {
    this.response = response;
  }

  public String getPostString() {
    return postString;
  }

  public void setPostString(String postString) {
    this.postString = postString;
  }


  /**
   * 解析此请求的参数(parsed = false)
   * 如果url和post的参数中都含有此参数，则将它们合并
   */
  protected void parseParameters() {
    if (parsed) {
      return;
    }
    ParameterMap<String, String[]> results = parameters;
    if (results == null) {
      results = new ParameterMap<>();
    }
    results.setLocked(false);
    String encoding = getCharacterEncoding();
    if (encoding == null) {
      encoding = "UTF-8";
    }
    String queryString = getQueryString();
    try {
      RequestUtil.parseParameters(results, queryString, encoding);
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    // Parse any parameters specified in the input stream
    String contentType = getContentType();
    if (contentType == null) {
      contentType = "";
    }
    int semicolon = contentType.indexOf(';');
    if (semicolon >= 0) {
      contentType = contentType.substring(0, semicolon).trim();
    }
    else {
      contentType = contentType.trim();
    }

    // 若为post请求则继续解析postString
    if ("POST".equals(getMethod()) && (getContentLength() > 0)
      && "application/x-www-form-urlencoded".equals(contentType)) {
      try {
        RequestUtil.parseParameters(results,postString,encoding);
      } catch (Exception e) {
        e.printStackTrace();
      }
      /*try {
        int max = getContentLength();
        int len = 0;
        byte buf[] = new byte[getContentLength()];
        ServletInputStream is = getInputStream();
        while (len < max) {
          int next = is.read(buf, len, max - len);
          if (next < 0 ) {
            break;
          }
          len += next;
        }
        is.close();
        if (len < max) {
          throw new RuntimeException("Content length mismatch");
        }
        RequestUtil.parseParameters(results, buf, encoding);
      }
      catch (UnsupportedEncodingException ue) {
        ;
      }
      catch (IOException e) {
        throw new RuntimeException("Content read fail");
      }*/
    }

    //设置为只读
    results.setLocked(true);
    parsed = true;
    parameters = results;
  }

  public Wrapper getWrapper() {
    return wrapper;
  }

  public void setWrapper(Wrapper wrapper) {
    this.wrapper = wrapper;
  }

  public void addCookie(Cookie cookie) {
    synchronized (cookies) {
      cookies.add(cookie);
    }
  }

  /**
   * Create and return a ServletInputStream to read the content
   * associated with this Request.  The default implementation creates an
   * instance of RequestStream associated with this request, but this can
   * be overridden if necessary.
   *
   * @exception IOException if an input/output error occurs
   */
  public ServletInputStream createInputStream() throws IOException {
    return (new RequestStream(this));
  }

  public InputStream getStream() {
    return input;
  }
  public void setContentLength(int length) {
    this.contentLength = length;
  }

  public void setContentType(String type) {
    this.contentType = type;
  }

  public void setInet(InetAddress inetAddress) {
    this.inetAddress = inetAddress;
  }

  public void setContextPath(String path) {
    if (path == null) {
      this.contextPath = "";
    } else {
      this.contextPath = path;
    }
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setPathInfo(String path) {
    this.pathInfo = path;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }
  /**
   * Set the name of the server (virtual host) to process this request.
   *
   * @param name The server name
   */
  public void setServerName(String name) {
    this.serverName = name;
  }
  /**
   * Set the port number of the server to process this request.
   *
   * @param port The server port
   */
  public void setServerPort(int port) {
    this.serverPort = port;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  /**
   * Set a flag indicating whether or not the requested session ID for this
   * request came in through a cookie.  This is normally called by the
   * HTTP Connector, when it parses the request headers.
   *
   * @param flag The new flag
   */
  public void setRequestedSessionCookie(boolean flag) {
    this.requestedSessionCookie = flag;
  }

  public void setRequestedSessionId(String requestedSessionId) {
    this.requestedSessionId = requestedSessionId;
  }

  public void setRequestedSessionURL(boolean flag) {
    requestedSessionURL = flag;
  }

  /* implementation of the HttpServletRequest*/
  @Override
  public Object getAttribute(String name) {
    synchronized (attributes) {
      return (attributes.get(name));
    }
  }

  @Override
  public Enumeration getAttributeNames() {
    synchronized (attributes) {
      return (new Enumerator(attributes.keySet()));
    }
  }

  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public int getContentLength() {
    return contentLength ;
  }

  @Override
  public long getContentLengthLong() {
    return 0;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }

  @Override
  public Cookie[] getCookies() {
    synchronized (cookies) {
      if (cookies.size() < 1) {
        return (null);
      }
      Cookie results[] = new Cookie[cookies.size()];
      return ((Cookie[]) cookies.toArray(results));
    }
  }

  @Override
  public long getDateHeader(String name) {
    String value = getHeader(name);
    if (value == null) {
      return (-1L);
    }

    // Work around a bug in SimpleDateFormat in pre-JDK1.2b4
    // (Bug Parade bug #4106807)
    value += " ";

    // Attempt to convert the date header in a variety of formats
    for (int i = 0; i < formats.length; i++) {
      try {
        Date date = formats[i].parse(value);
        return (date.getTime());
      } catch (ParseException e) {
        ;
      }
    }
    throw new IllegalArgumentException(value);
  }

  @Override
  public String getHeader(String name) {
    name = name.toLowerCase();
    ArrayList<String> values = headers.get(name);
    if (values != null) {
      return values.get(0);
    } else {
      return null;
    }
  }

  @Override
  public Enumeration getHeaderNames() {
    synchronized (headers) {
      return (new Enumerator(headers.keySet()));
    }
  }

  @Override
  public Enumeration getHeaders(String name) {
    name = name.toLowerCase();
    synchronized (headers) {
      ArrayList values = (ArrayList) headers.get(name);
      if (values != null) {
        return (new Enumerator(values));
      } else {
        return (new Enumerator(empty));
      }
    }
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (reader != null) {
      throw new IllegalStateException("getInputStream has been called");
    }

    if (stream == null) {
      stream = createInputStream();
    }
    return (stream);
  }

  @Override
  public int getIntHeader(String name) {
    String value = getHeader(name);
    if (value == null) {
      return (-1);
    } else {
      return (Integer.parseInt(value));
    }
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public Enumeration getLocales() {
    return null;
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getParameter(String name) {
    parseParameters();
    String values[] = (String[]) parameters.get(name);
    if (values != null) {
      return (values[0]);
    } else {
      return (null);
    }
  }

  @Override
  public Map getParameterMap() {
    parseParameters();
    return (this.parameters);
  }

  @Override
  public Enumeration getParameterNames() {
    parseParameters();
    return (new Enumerator(parameters.keySet()));
  }

  @Override
  public String[] getParameterValues(String name) {
    parseParameters();
    String values[] = (String[]) parameters.get(name);
    return (values);
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getPathTranslated() {
    return null;
  }

  @Override
  public String getProtocol() {
    return protocol;
  }

  @Override
  public String getQueryString() {
    return queryString;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    if (stream != null) {
      throw new IllegalStateException("getInputStream has been called.");
    }
    if (reader == null) {
      String encoding = getCharacterEncoding();
      if (encoding == null) {
        encoding = "ISO-8859-1";
      }
      InputStreamReader isr =
              new InputStreamReader(createInputStream(), encoding);
      reader = new BufferedReader(isr);
    }
    return (reader);
  }

  @Override
  public String getRealPath(String path) {
    return null;
  }

  @Override
  public int getRemotePort() {
    return 0;
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public String getLocalAddr() {
    return null;
  }

  @Override
  public int getLocalPort() {
    return 0;
  }

  @Override
  public ServletContext getServletContext() {
    return null;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    return null;
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    return null;
  }

  @Override
  public boolean isAsyncStarted() {
    return false;
  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    return null;
  }

  @Override
  public DispatcherType getDispatcherType() {
    return null;
  }

  @Override
  public String getRemoteAddr() {
    return null;
  }

  @Override
  public String getRemoteHost() {
    return null;
  }

  @Override
  public String getRemoteUser() {
    return null;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  @Override
  public String getScheme() {
    return null;
  }

  @Override
  public String getServerName() {
    return null;
  }

  @Override
  public int getServerPort() {
    return 0;
  }

  @Override
  public String getRequestedSessionId() {
    return null;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public StringBuffer getRequestURL() {
    return null;
  }

  @Override
  public HttpSession getSession() {
    return getSession(true);
  }

  @Override
  public String changeSessionId() {
    return null;
  }

  @Override
  public HttpSession getSession(boolean create) {
    Session session = doGetSession(true);
    return session == null ? null : session.getSession();
  }

  protected Session doGetSession(boolean create) {
    Context context = getContext();
    if (context == null) {
      return null;
    }

    if (session != null && !session.isValid()) {
      session = null;
    }
    if (session != null) {
      return session;
    }

    Manager manager = context.getManager();
    if (manager == null) {
      return null;
    }
    if (requestedSessionId != null) {
      session = manager.findSession(requestedSessionId);
      if (session != null && !session.isValid()) {
        session = null;
      }
      if (session != null) {
        session.access();
        return session;
      }
    }

    if (!create) {
      return null;
    }
    session = manager.createSession();

    if (session != null) {
      Cookie cookie = new Cookie("JSESSIONID", session.getId());
      response.addSessionCookie(cookie);
    }

    if (session == null) {
      return null;
    }

    session.access();
    return session;
  }

  @Override
  public String getServletPath() {
    return null;
  }

  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return isRequestedSessionIdFromURL();
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    return false;
  }

  @Override
  public void login(String username, String password) throws ServletException {

  }

  @Override
  public void logout() throws ServletException {

  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return null;
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public boolean isUserInRole(String role) {
    return false;
  }

  @Override
  public void removeAttribute(String attribute) {
  }

  @Override
  public void setAttribute(String key, Object value) {
  }

  /**
   * Set the authorization credentials sent with this request.
   *
   * @param authorization The new authorization credentials
   */
  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }

  @Override
  public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
  }

  public HttpServletRequest getRequest() {
    if (facade == null) {
      facade = new HttpRequestFacade(this);
    }
    return facade;
  }
}
