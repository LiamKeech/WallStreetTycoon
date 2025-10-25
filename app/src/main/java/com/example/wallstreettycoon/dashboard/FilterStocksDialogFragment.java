package com.example.wallstreettycoon.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.example.wallstreettycoon.R;

public class FilterStocksDialogFragment extends DialogFragment {

    public interface FilterListener {
        void onFilterApplied(String filter, String search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_stocks_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton btnSearch = view.findViewById(R.id.imgSearch);
        btnSearch.setOnClickListener(v -> {
            RadioGroup radioGroup = view.findViewById(R.id.rdgFilter);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String selectedFilter = null;
            if (selectedId != -1) { // item has been selected:
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedFilter = selectedRadioButton.getText().toString();
            }

            SearchView searchView = view.findViewById(R.id.searchText);
            String searchCriteria = searchView.getQuery().toString();

            if (getActivity() instanceof FilterListener) {
                ((FilterListener) getActivity()).onFilterApplied(selectedFilter, searchCriteria);
            }

            // close when search button selected:
            dismiss();
        });

        ImageButton btnBack = view.findViewById(R.id.imgBack);
        btnBack.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}