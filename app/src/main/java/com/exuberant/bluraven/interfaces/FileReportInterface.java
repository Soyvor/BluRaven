package com.exuberant.bluraven.interfaces;

import java.util.List;

import io.fotoapparat.result.PhotoResult;

public interface FileReportInterface {

    void addReport(String description, String reportType, List<PhotoResult> pictures);

}
