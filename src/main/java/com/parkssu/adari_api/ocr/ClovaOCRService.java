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

    @Value("${clova.secret}")
    private String SECRET;

    @Value("${clova.api-url}")
    private String API_URL;

    public String execute(ImageParsingRequest request) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = createConnection(url);
            createRequestBody(connection, request);
            StringBuilder response = getResponse(connection);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생: " + e.getMessage();
        }
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;");
        connection.setRequestProperty("X-OCR-SECRET", SECRET);
        return connection;
    }

    private void createRequestBody(HttpURLConnection connection, ImageParsingRequest request) throws IOException {
        JSONObject image = new JSONObject();
        image.put("format", "jpg");
        image.put("name", "receiptTestImage");
        image.put("data", request.getBase64()
                .replace("data:image/jpeg;base64,", "")
                .replace("data:image/png;base64,", ""));

        JSONArray images = new JSONArray();
        images.put(image);

        JSONObject requestObject = new JSONObject();
        requestObject.put("version", "V2");
        requestObject.put("requestId", UUID.randomUUID().toString());
        requestObject.put("timestamp", System.currentTimeMillis());
        requestObject.put("images", images);

        connection.connect();
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(requestObject.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private StringBuilder getResponse(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code == 200 ? connection.getInputStream() : connection.getErrorStream()));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();

        System.out.println("📦 Raw JSON 응답:\n" + result.toString());


        return result;
    }

    private String parseResponse(StringBuilder response) {
        JSONObject json = new JSONObject(response.toString());
        JSONArray fields = json.getJSONArray("images")
                .getJSONObject(0)
                .getJSONArray("fields");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            result.append(field.getString("inferText")).append(" ");
        }
        return result.toString();
    }
}
