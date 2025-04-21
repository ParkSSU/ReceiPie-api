package com.parkssu.adari_api.ocr;

public class ImageParsingRequest {
    private String base64;

    public ImageParsingRequest() {}

    public ImageParsingRequest(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}