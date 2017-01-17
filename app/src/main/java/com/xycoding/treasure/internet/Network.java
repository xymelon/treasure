package com.xycoding.treasure.internet;

import android.os.Build;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by xuyang on 15/9/17.
 */
public class Network {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private SSLSocketFactory mSSLSocketFactory;

    public Network() {
        mSSLSocketFactory = getSSLSocketFactory();
    }

    public HttpResponse performRequest(BaseRequest request) throws IOException {
        disableConnectionReuseIfNecessary();
        HttpURLConnection connection = openConnection(new URL(request.getURL()), request);
        // 设置头部信息
        for (String headerName : request.getHeaders().keySet()) {
            connection.addRequestProperty(headerName, request.getHeaders().get(headerName));
        }
        // 设置参数
        setConnectionParametersForRequest(connection, request);
        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromConnection(connection));
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                for (String headerValue : header.getValue()) {
                    Header h = new BasicHeader(header.getKey(), headerValue);
                    response.addHeader(h);
                }
            }
        }
        return response;
    }

    /**
     * Opens an {@link java.net.HttpURLConnection} with parameters.
     *
     * @param url
     * @return an open connection
     * @throws java.io.IOException
     */
    private HttpURLConnection openConnection(URL url, BaseRequest request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol()) && mSSLSocketFactory != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(mSSLSocketFactory);
            ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        return connection;
    }

    private void setConnectionParametersForRequest(HttpURLConnection connection,
                                                   BaseRequest request) throws IOException {
        switch (request.getMethod()) {
            case BaseRequest.Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case BaseRequest.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case BaseRequest.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case BaseRequest.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case BaseRequest.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case BaseRequest.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case BaseRequest.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case BaseRequest.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private void addBodyIfExists(HttpURLConnection connection, BaseRequest request)
            throws IOException {
        byte[] body = null;
        if (request.getParams() != null && request.getParams().size() > 0) {
            body = encodeParameters(request.getParams(), request.getParamsEncoding());
        }
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }


    /**
     * Initializes an {@link HttpEntity} from the given {@link HttpURLConnection}.
     *
     * @param connection
     * @return an HttpEntity populated with data from <code>connection</code>.
     */
    private static HttpEntity entityFromConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Trust every server - don't check for any certificate
     */
    private SSLSocketFactory getSSLSocketFactory() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
