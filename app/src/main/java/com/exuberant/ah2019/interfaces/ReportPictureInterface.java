package com.exuberant.ah2019.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface ReportPictureInterface {

    void showPictures(RecyclerView recyclerView);

    void sendData(String description, String reportType);

}
