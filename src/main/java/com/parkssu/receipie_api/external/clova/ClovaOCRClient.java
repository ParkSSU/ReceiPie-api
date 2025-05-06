package com.parkssu.receipie_api.external.clova;

import org.json.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * ClovaOCRClient는 네이버 Clova OCR API를 호출해 이미지를 텍스트로 변환하는 기능을 담당합니다.
 * 외부 API이므로 외부와 통신하는 책임만 가집니다.
 */
@Component  // Bean으로 등록하여 다른 클래스에서 @Autowired 또는 생성자 주입으로 사용 가능
public class ClovaOCRClient {

    @Value("${clova.api-url}") // application.yml에서 설정값을 주입받습니다.
    private String apiUrl;

    @Value("${clova.secret}")  // Clova OCR의 인증 시크릿 키
    private String secretKey;

    /**
     * 클라이언트로부터 받은 base64 이미지를 OCR 텍스트로 변환합니다.
     */
    public String extractText(String base64) throws IOException {
        HttpURLConnection connection = createConnection();      // 1. 연결 생성
        sendRequestBody(connection, base64);                    // 2. 요청 전송
        String json = getRawResponse(connection);               // 3. 응답 수신
        return parseInferTextFromJson(json);                    // 4. 텍스트 추출
    }

    // 1. HTTP 연결 객체 생성
    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setDoInput(true);  // 응답 수신
        connection.setDoOutput(true); // 요청 전송
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;");
        connection.setRequestProperty("X-OCR-SECRET", secretKey); // 인증 헤더
        return connection;
    }

    // 2. base64 이미지를 포함한 JSON 요청 바디 전송
    private void sendRequestBody(HttpURLConnection connection, String base64) throws IOException {
        JSONObject image = new JSONObject();
        image.put("format", "jpg");  // 이미지 포맷 (jpg, png 등)
        image.put("name", "receipt"); // 아무 이름
        image.put("data", cleanBase64(base64)); // base64 앞의 헤더 제거

        JSONArray images = new JSONArray().put(image); // Clova는 배열로 전송해야 함

        JSONObject body = new JSONObject();
        body.put("version", "V2");
        body.put("requestId", UUID.randomUUID().toString()); // 고유 ID
        body.put("timestamp", System.currentTimeMillis());   // 현재 시간
        body.put("images", images);

        connection.connect(); // 연결 시작
        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8)); // JSON 전송
        }
    }

    // 3. 응답을 문자열로 수신 (성공/실패 상관없이 읽음)
    private String getRawResponse(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();  // HTTP 상태 코드 확인
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code == 200 ? conn.getInputStream() : conn.getErrorStream()
        ));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();
        return response.toString();
    }

    // 4. 응답 JSON에서 inferText만 추출
    private String parseInferTextFromJson(String rawJson) {
        JSONObject json = new JSONObject(rawJson);
        JSONArray fields = json.getJSONArray("images")
                .getJSONObject(0)
                .getJSONArray("fields");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            result.append(fields.getJSONObject(i).getString("inferText")).append(" ");
        }
        return result.toString();
    }

    // base64 앞의 이미지 헤더 제거
    private String cleanBase64(String base64) {
        return base64
                .replace("data:image/jpeg;base64,", "")
                .replace("data:image/png;base64,", "");
    }
}
