package com.exuberant.ah2019.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.bottomsheets.ReportBottomSheet;
import com.exuberant.ah2019.activities.MainActivity;
import com.exuberant.ah2019.adapters.DisplayPictureAdapter;
import com.exuberant.ah2019.adapters.ReportPictureAdpater;
import com.exuberant.ah2019.interfaces.PictureRemoveInterface;
import com.exuberant.ah2019.interfaces.ReportPictureInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraView;

public class CameraFragment extends Fragment {

    private Fotoapparat fotoapparat;
    private CameraView cameraView;
    private MaterialButton captureButton, submitButton;
    private RecyclerView pictureRecyclerView;
    private TextView cameraSuggestTextView;
    private ReportBottomSheet reportBottomSheet;
    private List<PhotoResult> photoResultList = new ArrayList<>();
    private static PictureRemoveInterface pictureRemoveInterface;
    private static ReportPictureInterface reportPictureInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_camera, container, false);

        initialize(view);

        pictureRemoveInterface = new PictureRemoveInterface() {
            @Override
            public void removePicture(PhotoResult photoResult) {
                photoResultList.remove(photoResult);
                displayPictures();
            }
        };

        reportPictureInterface = new ReportPictureInterface() {
            @Override
            public void showPictures(RecyclerView recyclerView) {
                ReportPictureAdpater adpater = new ReportPictureAdpater(photoResultList);
                recyclerView.setAdapter(adpater);
            }

            @Override
            public void sendData(String description, String reportType) {
                MainActivity.getFileReportInterface().addReport(description, reportType, photoResultList);
                photoResultList = new ArrayList<>();
                displayPictures();
                reportBottomSheet.dismiss();
                Snackbar snackbar = Snackbar.make(view.findViewById(R.id.camera_container), "Report Submitted", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundResource(R.color.colorSuccessSnackbar);
                snackbar.show();
            }
        };

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoResultList.size() >= 3) {
                    Snackbar snackbar = Snackbar.make(view.findViewById(R.id.camera_container), "You can't add more than 3 pictures", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundResource(R.color.colorErrorSnackbar);
                    snackbar.show();
                } else {
                    photoResultList.add(fotoapparat.takePicture());
                    displayPictures();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoResultList.size() < 1) {
                    Snackbar snackbar = Snackbar.make(view.findViewById(R.id.camera_container), "You need atleast 1 picture to file a report", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundResource(R.color.colorErrorSnackbar);
                    snackbar.show();
                } else {
                    openReportDialogue();
                }
            }
        });

        return view;
    }

    private void initialize(View view) {
        cameraView = view.findViewById(R.id.cv_camera);
        cameraSuggestTextView = view.findViewById(R.id.tv_camera_message);
        captureButton = view.findViewById(R.id.btn_capture);
        submitButton = view.findViewById(R.id.btn_photos_submit);
        pictureRecyclerView = view.findViewById(R.id.rv_picture);
        fotoapparat = Fotoapparat
                .with(getContext())
                .into(cameraView)           // view which will draw the camera preview
                .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
                .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                .lensPosition(LensPositionSelectorsKt.back())       // we want back camera
                .focusMode(SelectorsKt.firstAvailable(  // (optional) use the first focus mode which is supported by device
                        FocusModeSelectorsKt.continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),        // in case if continuous focus is not available on device, auto focus will be used
                        FocusModeSelectorsKt.fixed()             // if even auto focus is not available - fixed focus mode will be used
                ))
                .flash(SelectorsKt.firstAvailable(      // (optional) similar to how it is done for focus mode, this time for flash
                        FlashSelectorsKt.autoRedEye(),
                        FlashSelectorsKt.autoFlash(),
                        FlashSelectorsKt.torch()
                ))
                .build();
        fotoapparat.start();
    }

    private void displayPictures() {
        if (photoResultList.size() == 0){
            cameraSuggestTextView.setText("We recommend that first picture should be of the problem");
        } else if (photoResultList.size() == 1){
            cameraSuggestTextView.setText("It's recommended that second picture should be of the surrounding");
        } else if (photoResultList.size() == 2){
            cameraSuggestTextView.setText("Third photo can be any miscellaneous picture related to problem");
        }
        DisplayPictureAdapter adapter = new DisplayPictureAdapter(photoResultList);
        pictureRecyclerView.setAdapter(adapter);
    }

    void openReportDialogue() {
        reportBottomSheet = new ReportBottomSheet();
        reportBottomSheet.show(getActivity().getSupportFragmentManager(), "Report");
    }

    public static PictureRemoveInterface getPictureRemoveInterface() {
        return pictureRemoveInterface;
    }

    public static ReportPictureInterface getReportPictureInterface() {
        return reportPictureInterface;
    }


}
