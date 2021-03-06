package cn.gotoil.unipay.utils;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 *
 * @author SuYajiang syj247@qq.com
 * @Date 2018-12-7 11:21
 */
@SuppressWarnings("ALL")
@Slf4j
public class UtilHttpClient {

    static final int MAXTOTAL = 200;//总最大连接数
    static final int DEFAULTMAXPERROUTE = 100;
    static final int TIMEOUTGOLAB = 50000;
    private static CloseableHttpClient httpclient = null;

    //通知专用
    private static CloseableHttpClient notifyHttpClient = null;


    private static RequestConfig requestConfig(int connectTimeOut, int connectRequestTimeOut, int socketTimeOut) {
        return RequestConfig.custom().setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(connectRequestTimeOut).setSocketTimeout(socketTimeOut).build();

    }



    public static CloseableHttpClient getHttpClient() {

        if (null == httpclient) {
            synchronized (UtilHttpClient.class) {
                if (null == httpclient) {
                    httpclient = getNewHttpClient(TIMEOUTGOLAB, TIMEOUTGOLAB, TIMEOUTGOLAB);
                }
            }
        }

        return httpclient;
    }


    public static CloseableHttpClient getNotifyHttpClient() {

        if (null == notifyHttpClient) {
            synchronized (UtilHttpClient.class) {
                if (null == notifyHttpClient) {
                    notifyHttpClient = getNewHttpClient(3000, 3000, 3000);
                }
            }
        }
        return notifyHttpClient;
    }

    private static CloseableHttpClient getNewHttpClient(int connectTimeOut, int connectRequestTimeOut, int socketTimeOut) {


        // 设置连接池
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http"
                , plainsf).register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 配置最大连接数
        cm.setMaxTotal(MAXTOTAL);
        // 配置每条线路的最大连接数
        cm.setDefaultMaxPerRoute(DEFAULTMAXPERROUTE);
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // 如果已经重试了2次，就放弃
                if (executionCount >= 2) {
                    return false;
                }
                // 如果服务器丢掉了连接，那么就重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // 不要重试SSL握手异常
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                // 超时
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                // 目标服务器不可达
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // 连接被拒绝
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();

                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }

        };

        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                Args.notNull(response, "HTTP response");
                final HeaderElementIterator it =
                        new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    final HeaderElement he = it.nextElement();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && "timeout".equalsIgnoreCase(param)) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (final NumberFormatException ignore) {
                        }
                    }
                }
                return 1;
            }

        };


        CloseableHttpClient newHttpclient = null;

        newHttpclient =
                HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig(connectTimeOut,
                        connectRequestTimeOut, socketTimeOut)).setConnectionManagerShared(true)
                //                .setKeepAliveStrategy(myStrategy)
                //                .setRetryHandler(httpRequestRetryHandler)
                .build();

        return newHttpclient;
    }


    /**
     * 带参数的get请求
     *
     * @param url
     * @param map 无参数可以传空
     * @return
     */
    public static String doGet(String url, Map<String, Object> map) {
        CloseableHttpClient client = UtilHttpClient.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = UtilHttpClient.getHttpClient().execute(httpGet);

            if (response.getEntity() != null) {
                return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("请求出错{}", e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("client关闭出错{}", e.getMessage());
            }
            try {
                response.close();
            } catch (IOException e) {
                log.error("response关闭出错{}", e.getMessage());
            }
        }
        return "";
    }


    /**
     * 带参数的post请求
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> map) {
        CloseableHttpClient client = UtilHttpClient.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (map != null) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Charsets.UTF_8);
                httpPost.setEntity(formEntity);
            }
            response = client.execute(httpPost);

            if (response.getEntity() != null) {
                return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("https|Https请求出错{}", e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("client关闭出错{}", e.getMessage());
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("response关闭出错{}", e.getMessage());
                }
            }

        }
        return "";
    }


    /**
     * 带参数的post请求
     *
     * @param url
     * @param str
     * @return
     * @throws Exception
     */
    public static String doPostStr(String url, String str) {
        CloseableHttpClient client = UtilHttpClient.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(str, Charsets.UTF_8);
            httpPost.setEntity(entity);
            log.debug("POST {}{}",url,EntityUtils.toString(entity));
            response = client.execute(httpPost);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode() && response.getEntity() != null) {
                return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("https|Https请求出错{}", e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("client关闭出错{}", e.getMessage());
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("response关闭出错{}", e.getMessage());
                }
            }

        }
        return "";
    }


    public static String notifyPost(String url, Map<String, Object> map) throws IOException, UnknownHostException,
            ClientProtocolException {
        CloseableHttpClient client = UtilHttpClient.getNotifyHttpClient();
        CloseableHttpResponse response = null;

        HttpPost httpPost = new HttpPost(url);
        if (map != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString())) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }

            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, Charsets.UTF_8);
            httpPost.setEntity(formEntity);
        }
        response = client.execute(httpPost);

        String reStr = new String();
        if (response.getEntity() != null) {
            reStr = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        }
        client.close();
        response.close();
        return reStr;
    }



    /**
     * 用https的方式想某个地址发送post请求
     * @param postUrl post地址 必须https开头
     * @param bodyStr  body 字符串
     * @param certPath 证书路径
     * @param key 证书秘钥
     * @return
     * @throws Exception
     */
    public static String postConnWithCert(String postUrl,String bodyStr,String certPath,String key) throws Exception{

        assert  postUrl.toUpperCase().startsWith("HTTPS");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(certPath));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray()).build();
        SSLConnectionSocketFactory sslsf =
                new SSLConnectionSocketFactory(sslcontext,new DefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            // 设置响应头信息
            HttpPost httpost = new HttpPost(postUrl);
            httpost.setEntity(new StringEntity(bodyStr, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
            try {
                HttpEntity entity = response.getEntity();

                String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            } catch (Exception e) {
                response.close();
            }
        }catch (Exception e){
            httpclient.close();
        }
        return "";
    }

}
