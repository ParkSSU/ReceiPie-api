package com.parkssu.receipie_api.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *  OCRRequestFilter
 * - `/ocr/analyze` 엔드포인트에 대해서만 CachedBodyHttpServletWrapper를 적용하는 필터
 * - Request Body를 여러 번 읽을 수 있도록 Wrapper로 감싼다.
 */
@Component
public class OCRRequestFilter implements Filter {

    /**
     * 필터 초기화 시 호출되는 메서드
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // 별도의 초기화가 필요하지 않으므로 비워둠
    }

    /**
     * 필터 로직이 실행되는 메서드
     *
     * @param request  원본 HttpServletRequest
     * @param response HttpServletResponse
     * @param chain    필터 체인
     * @throws IOException, ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // /ocr/analyze 경로에 대해서만 Wrapper 적용
        if (httpRequest.getRequestURI().startsWith("/ocr/analyze")) {
            CachedBodyHttpServletWrapper wrappedRequest = new CachedBodyHttpServletWrapper(httpRequest);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 필터가 소멸될 때 호출되는 메서드
     */
    @Override
    public void destroy() {
        // 리소스 해제 시 필요한 로직이 있다면 여기에 작성
    }
}
