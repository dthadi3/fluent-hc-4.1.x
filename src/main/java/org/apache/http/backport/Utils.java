package org.apache.http.backport;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.backport.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This is a collection of backported utility functions to allow fluent-hc to
 * play nicely with older versions of http-components.
 */
public class Utils {

    // pulled from HTTP.DEF_CONTENT_CHARSET in 4.2.1
    public static final Charset HTTP_DEF_CONTENT_CHARSET = Consts.ISO_8859_1;

    public static StringEntity newStringEntity(final String string, final ContentType contentType) {
        try {
            return new StringEntity(string, contentType.getMimeType(), contentType.getCharset().name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileEntity newFileEntity(final File file, final ContentType contentType) {
        return new FileEntity(file, contentType.toString());
    }

    public static UrlEncodedFormEntity newUrlEncodedFormEntity(final Iterable <? extends NameValuePair> formParams,
                                                               final Charset charset) {
        try {
            return new UrlEncodedFormEntity(newArrayList(formParams.iterator()), charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteArrayEntity newByteArrayEntity(final byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) < 0) || ((off + len) > b.length)) {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
        }

        return new ByteArrayEntity(Arrays.copyOfRange(b, off, b.length - off));
    }

    public static ByteArrayEntity newByteArrayEntity(final byte[] b, ContentType contentType) {
        // 4.1.1 doesn't appear to be capable of doing anything useful based on the ContentType passed in here
        return new ByteArrayEntity(b);
    }

    public static InputStreamEntity newInputStreamEntity(final InputStream instream,
                                                         final long length,
                                                         final ContentType contentType) {
        // 4.1.1 doesn't appear to be capable of doing anything useful based on the ContentType passed in here
        return new InputStreamEntity(instream, length);
    }

    public static HttpRequestBase reset(final HttpRequestBase requestBase) {
        // TODO this will need to be tested, since there isn't a clean way to reset an existing HttpRequestBase in 4.1.1
        try {
            HttpRequestBase clone = (HttpRequestBase) requestBase.clone();
            //requestBase.abort(); // TODO might need this...
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthScope newAuthScope(HttpHost host) {
        return new AuthScope(host.getHostName(), host.getPort(), AuthScope.ANY_REALM, host.getSchemeName());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = new ArrayList<E>();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }
}
