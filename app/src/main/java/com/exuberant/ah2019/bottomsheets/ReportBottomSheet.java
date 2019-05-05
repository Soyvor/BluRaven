package com.exuberant.ah2019.bottomsheets;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.fragments.CameraFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

public class ReportBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView reportPictureRecyclerView;
    private ChipGroup chipGroup;
    private Chip selectedChip;
    private TextInputLayout descriptionEditText, violationEditText;
    private MaterialButton submitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_report, container, false);
        initialize(view);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportDescription = descriptionEditText.getEditText().getText().toString();
                if (reportDescription == null || reportDescription.length() == 0){
                    descriptionEditText.setError("Description is essential");
                } else {
                    String violationType;
                    if (selectedChip.getId() == R.id.chip_other){
                        violationType = violationEditText.getEditText().getText().toString();
                    } else {
                        violationType = selectedChip.getText().toString();
                    }
                    CameraFragment.getReportPictureInterface().sendData(descriptionEditText.getEditText().getText().toString(), violationType);
                }
            }
        });
        return view;
    }

    private void initialize(View view){
        LayoutTransition transition = new LayoutTransition();
        transition.setAnimateParentHierarchy(false);
        ((ConstraintLayout)view.findViewById(R.id.report_container)).setLayoutTransition(transition);
        violationEditText = view.findViewById(R.id.el_other_violation);
        chipGroup = view.findViewById(R.id.cg_violation_chips);
        Chip chip = view.findViewById(R.id.chip_other);
        chip.setClickable(false);
        selectedChip = chip;
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int id) {
                Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
                if (chipGroup.getCheckedChipId() == R.id.chip_other){
                    violationEditText.setVisibility(View.VISIBLE);
                } else {
                    violationEditText.setVisibility(View.GONE);
                }
                if (chip != null) {
                    for (int i = 0; i < chipGroup.getChildCount(); ++i) {
                        chipGroup.getChildAt(i).setClickable(true);
                    }
                    chip.setClickable(false);
                    selectedChip = chip;
                }
            }
        });
        reportPictureRecyclerView = view.findViewById(R.id.rv_report_pictures);
        descriptionEditText = view.findViewById(R.id.el_report_description);
        submitButton = view.findViewById(R.id.btn_report_submit);
        CameraFragment.getReportPictureInterface().showPictures(reportPictureRecyclerView);
    }
}
