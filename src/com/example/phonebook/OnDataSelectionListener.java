package com.example.phonebook;

import android.view.View;
import android.widget.AdapterView;

public interface OnDataSelectionListener {

	public void onDataSelected(AdapterView<?> parent, View v, int position, long id);
	
}
