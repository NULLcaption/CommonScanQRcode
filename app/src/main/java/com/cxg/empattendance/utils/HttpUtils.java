package com.cxg.empattendance.utils;

import com.cxg.empattendance.application.XPPApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;

/**
* @description: webservice请求接口工具类
* @author xg.chen
* @create 2018/8/27
*/

public class HttpUtils {
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String SSL_DEFAULT_SCHEME = "https";
    private static final int SSL_DEFAULT_PORT = 443;
    private static final int EXECUTION_COUNT = 3;
    private static final int TIMEOUT = 30000;



	/**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
	 * @throws Exception 
     */
    public static String sendGet(String url, String param) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            /*Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	throw e;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复.
     */
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        // 自定义的恢复策略
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            // 设置恢复策略，在发生异常时候将自动重试3次
            if (executionCount >= EXECUTION_COUNT) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }
            HttpRequest request = (HttpRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = request instanceof HttpEntityEnclosingRequest;
            if (!idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }
    };

    /**
     * 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
     * .
     */
    private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        // 自定义响应处理
        public String handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                @SuppressWarnings("deprecation")
                String charset = EntityUtils.getContentCharSet(entity) == null ? CHARSET_UTF8
                        : EntityUtils.getContentCharSet(entity);
                return new String(EntityUtils.toByteArray(entity), charset);
            } else {
                return null;
            }
        }
    };

    /**
     * Get方式提交,URL中包含查询参数, 格式：http://www.g.cn?search=p&name=s.....
     *
     * @param url
     *            提交地址
     * @return 响应消息
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        return get(url, null, null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn.
     *
     * @param url
     *            提交地址
     * @param params
     *            查询参数集, 键/值对
     * @return 响应消息
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params)
            throws Exception {
        return get(url, params, null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn.
     *
     * @param url
     *            提交地址
     * @param params
     *            查询参数集, 键/值对
     * @param charset
     *            参数提交编码集
     * @return 响应消息
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params,
                             String charset) throws Exception {
        String urls = url;

        if (urls == null) {
            return null;
        }

        String charsets = charset;

        List<NameValuePair> qparams = getParamsList(params);
        if (qparams != null && qparams.size() > 0) {
            charsets = charsets == null ? CHARSET_UTF8 : charsets;
            String formatParams = URLEncodedUtils.format(qparams, charsets);
            urls = (urls.indexOf('?')) < 0 ? (urls + "?" + formatParams)
                    : (urls.substring(0, urls.indexOf('?') + 1) + formatParams);
        }
        DefaultHttpClient httpclient = getDefaultHttpClient(charsets);
        HttpGet hg = new HttpGet(urls);

        String responseStr = null;
        try {
            responseStr = httpclient.execute(hg, responseHandler);
        } catch (ClientProtocolException e) {
            throw new Exception("客户端连接协议错误", e);
        } catch (IOException e) {
            throw new Exception("IO操作异常", e);
        } finally {
            abortConnection(hg, httpclient);
        }

        return responseStr;
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn.
     *
     * @param url
     *            提交地址
     * @param params
     *            提交参数集, 键/值对
     * @return 响应消息
     * @throws Exception
     */
    public static String post(String url, Map<String, String> params)
            throws Exception {
        return post(url, params, null);
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn.
     *
     * @param url
     *            提交地址
     * @param params
     *            提交参数集, 键/值对
     * @param charset
     *            参数提交编码集
     * @return 响应消息
     * @throws Exception
     */
    public static String post(String url, Map<String, String> params,
                              String charset) throws Exception {
        if (url == null) {
            return null;
        }
        // 创建HttpClient实例
        DefaultHttpClient httpclient = getDefaultHttpClient(charset);
        UrlEncodedFormEntity formEntity = null;

        try {
            List<NameValuePair> qparams = getParamsList(params);
            if (qparams != null && qparams.size() > 0) {
                formEntity = new UrlEncodedFormEntity(qparams, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new Exception("不支持的编码集", e);
        }
        HttpPost hp = new HttpPost(url);
        hp.setEntity(formEntity);
        // 发送请求，得到响应
        String responseStr = null;
        try {
            responseStr = httpclient.execute(hp, responseHandler);

        } catch (ClientProtocolException e) {
            throw new Exception("客户端连接协议错误", e);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            abortConnection(hp, httpclient);
        }

        return responseStr;
    }

    /**
     * 获取DefaultHttpClient实例.
     *
     * @param charset
     *            参数编码集, 可空
     * @return DefaultHttpClient 对象
     */
    private static DefaultHttpClient getDefaultHttpClient(final String charset) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        // 模拟浏览器，解决一些服务器程序只允许浏览器访问的问题
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        httpclient.getParams().setParameter(
                CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
        httpclient.getParams().setParameter(
                CoreProtocolPNames.HTTP_CONTENT_CHARSET,
                charset == null ? CHARSET_UTF8 : charset);
        httpclient.setHttpRequestRetryHandler(requestRetryHandler);
        httpclient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                TIMEOUT);
        return httpclient;
    }

    /**
     * 释放HttpClient连接.
     *
     * @param hrb
     *            请求对象
     * @param httpclient
     *            client对象
     */
    private static void abortConnection(final HttpRequestBase hrb,
                                        final HttpClient httpclient) {
        if (hrb != null) {
            hrb.abort();
        }
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
        System.gc();
    }

    /**
     * 从给定的路径中加载此 KeyStore.
     *
     * @param url
     *            keystore URL路径
     * @param password
     *            keystore访问密钥
     * @return keystore 对象
     */
    private static KeyStore createKeyStore(final URL url, final String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null");
        }
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = null;
        try {
            is = url.openStream();
            keystore.load(is, password != null ? password.toCharArray() : null);
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }

        return keystore;
    }

    /**
     * 将传入的键/值对参数转换为NameValuePair参数集.
     *
     * @param paramsMap
     *            参数集, 键/值对
     * @return NameValuePair参数集
     */
    private static List<NameValuePair> getParamsList(
            Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }

        return params;
    }

    public static void main(String[] arg) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "xplatform.user.login");
        params.put("passport", "123");
        params.put("password", "123");
        try {
            post("http://dev.exptest.zjxpp.com:7186/mobilePlatform/router/login/admdict",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
