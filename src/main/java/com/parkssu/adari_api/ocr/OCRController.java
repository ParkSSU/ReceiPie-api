package com.parkssu.adari_api.ocr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ocr")
public class OCRController {

    @Autowired
    private ClovaOCRService service;

    @PostMapping("/test")
    public String testOCR(@RequestBody ImageParsingRequest request) {
        return service.execute(request);
    }
}
