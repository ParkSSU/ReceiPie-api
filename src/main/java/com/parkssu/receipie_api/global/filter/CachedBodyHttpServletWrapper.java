package com.parkssu.receipie_api.global.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * ✅ Request Body를 캐싱하여 여러 번 읽을 수 있도록 하는 Wrapper 클래스.
 *
 * - 기본적으로 `HttpServletRequest`의 InputStream은 한 번만 읽을 수 있다.
 * - 이 클래스는 InputStream을 byte 배열로 저장해 두고, 요청이 여러 번 읽혀도 동일한 데이터를 제공할 수 있도록 한다.
 */
public class CachedBodyHttpServletWrapper extends HttpServletRequestWrapper {

    // Request Body를 byte 배열로 저장할 필드
    private final byte[] cachedBody;

    public CachedBodyHttpServletWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // Request의 InputStream을 읽어서 byte 배열로 저장
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }

    /**
     * getInputStream() 메서드 오버라이딩
     * - 요청이 여러 번 읽혀도 동일한 데이터를 반환할 수 있도록 ByteArrayInputStream을 생성
     *
     * @return 새로운 ServletInputStream
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new CachedBodyServletInputStream(byteArrayInputStream);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
    }

    /**
     *  내부 클래스 - ServletInputStream 구현체
     * - byte 배열을 기반으로 데이터를 읽을 수 있도록 함
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final InputStream inputStream;

        /**
         * 생성자
         * @param inputStream byte 배열을 기반으로 생성된 InputStream
         */
        public CachedBodyServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * 비동기 읽기를 위한 리스너 설정 (현재 구현에서는 사용하지 않음)
         */
        @Override
        public void setReadListener(ReadListener listener) {
            // 비동기 처리는 현재 구현하지 않음
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
