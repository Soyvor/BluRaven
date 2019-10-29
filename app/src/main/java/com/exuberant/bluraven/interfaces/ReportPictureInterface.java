package com.exuberant.bluraven.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface ReportPictureInterface {

    void showPictures(RecyclerView recyclerView);

    void sendData(String description, String reportType);

}
