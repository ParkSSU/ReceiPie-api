package com.parkssu.adari_api.ocr;

// 클라이언트가 보내는 이미지 요청 데이터를 담기 위한 클래스
public class ImageParsingRequest {

    // Base64 인코딩된 이미지 데이터를 저장할 필드
    private String base64;

    // 기본 생성자 (Spring에서 객체 생성 시 필요)
    public ImageParsingRequest() {}

    // base64 필드를 초기화하는 생성자
    public ImageParsingRequest(String base64) {
        this.base64 = base64;
    }

    // Getter: 외부에서 base64 값을 가져올 때 사용
    public String getBase64() {
        return base64;
    }

    // Setter: 외부에서 base64 값을 설정할 때 사용
    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
