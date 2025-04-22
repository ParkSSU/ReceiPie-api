package com.parkssu.adari_api.ocr;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ClovaOCRService {

    // application.properties 또는 application.yml에서 값을 주입받음
    @Value("${clova.secret}")
    private String SECRET;

    @Value("${clova.api-url}")
    private String API_URL;

    // 외부에서 실행되는 메서드. OCR 요청을 보내고 결과 문자열을 반환함
    public String execute(ImageParsingRequest request) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = createConnection(url); // 요청 연결 생성
            createRequestBody(connection, request); // 요청 바디 구성 및 전송
            StringBuilder response = getResponse(connection); // 응답 수신
            return parseResponse(response); // 응답 파싱 후 텍스트 추출
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생: " + e.getMessage();
        }
    }

    // HTTP POST 요청을 위한 기본 설정 구성
    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false); // 캐시 사용 안 함
        connection.setDoInput(true);    // 응답 수신 허용
        connection.setDoOutput(true);   // 요청 전송 허용
        connection.setReadTimeout(5000); // 타임아웃 5초 설정
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;");
        connection.setRequestProperty("X-OCR-SECRET", SECRET); // 인증용 시크릿 키 설정
        return connection;
    }

    // 요청 바디(JSON 형식) 생성 및 전송
    private void createRequestBody(HttpURLConnection connection, ImageParsingRequest request) throws IOException {
        JSONObject image = new JSONObject();
        image.put("format", "jpg"); // 이미지 포맷 고정
        image.put("name", "receiptTestImage"); // 임의 이름 지정
        image.put("data", request.getBase64()
                .replace("data:image/jpeg;base64,", "")
                .replace("data:image/png;base64,", "")); // base64 문자열에서 헤더 제거

        JSONArray images = new JSONArray();
        images.put(image); // 단일 이미지이지만 배열 형태로 감싸줌

        JSONObject requestObject = new JSONObject();
        requestObject.put("version", "V2");
        requestObject.put("requestId", UUID.randomUUID().toString()); // 고유 ID
        requestObject.put("timestamp", System.currentTimeMillis());   // 현재 시간
        requestObject.put("images", images); // 이미지 데이터 포함

        connection.connect(); // 연결
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(requestObject.toString().getBytes(StandardCharsets.UTF_8)); // JSON 전송
        }
    }

    // 응답 받기 (성공/실패에 따라 다른 스트림 사용)
    private StringBuilder getResponse(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code == 200 ? connection.getInputStream() : connection.getErrorStream())); // 오류 발생 시 에러 스트림

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line); // 응답 전체를 문자열로 읽어오기
        }
        reader.close();

        System.out.println("📦 Raw JSON 응답:\n" + result.toString()); // 콘솔에 응답 출력

        return result;
    }

    // 응답(JSON)에서 'inferText' 필드를 추출하여 문자열로 반환
    private String parseResponse(StringBuilder response) {
        JSONObject json = new JSONObject(response.toString());
        JSONArray fields = json.getJSONArray("images")
                .getJSONObject(0) // 첫 번째 이미지 결과
                .getJSONArray("fields"); // 인식된 텍스트 영역 리스트

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            result.append(field.getString("inferText")).append(" "); // 추출된 텍스트 이어붙이기
        }
        return result.toString(); // 최종 OCR 결과 반환
    }
}
