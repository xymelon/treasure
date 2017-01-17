package com.xycoding.treasure.internet;

import java.util.Collections;
import java.util.Map;

/**
 * Created by xuyang on 15/4/8.
 */
public abstract class BaseRequest {

    /**
     * Supported request methods.
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * 必填：设置url地址
     *
     * @return
     */
    public abstract String getURL();

    /**
     * 选填：请求方法{@link Method}，默认POST
     *
     * @return
     */
    public int getMethod() {
        return Method.POST;
    }

    /**
     * 选填：设置Headers
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        return Collections.EMPTY_MAP;
    }

    /**
     * 选填：传输参数
     *
     * @return
     */
    public Map<String, String> getParams() {
        return null;
    }

    /**
     * 选填：超时时间，单位ms
     *
     * @return
     */
    public int getTimeoutMs() {
        return -1;
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     */
    public String getParamsEncoding() {
        return "UTF-8";
    }

}
