package com.example.zadania0702;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spDestination;
    private SeekBar sbPeople;
    private TextView tvPeopleValue;
    private EditText etName;
    private Button btnDate, btnSubmit;
    private CheckBox cbTerms;
    private MaterialSwitch swInsurance;
    private TextView tvSummary;

    private String tripDate = "";   // pusty = nie wybrano
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        spDestination = findViewById(R.id.spDestination);
        sbPeople = findViewById(R.id.sbPeople);
        tvPeopleValue = findViewById(R.id.tvPeopleValue);
        etName = findViewById(R.id.etName);
        btnDate = findViewById(R.id.btnDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        cbTerms = findViewById(R.id.cbTerms);
        swInsurance = findViewById(R.id.swInsurance);
        tvSummary = findViewById(R.id.tvSummary);

        setUpDestinationSpinner();
        setUpListeners();
        tvPeopleValue.setText(String.valueOf(sbPeople.getProgress() + 1));
    }

    private void setUpDestinationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.destinations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDestination.setAdapter(adapter);
    }

    private void setUpListeners() {
        // regulamin steruje przyciskiem (zależność między polami)
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) ->
                btnSubmit.setEnabled(isChecked));

        btnDate.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                submitForm();
            }
        });
        // 3 metody w interfejsie = klasa anonimowa
        sbPeople.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPeopleValue.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    tripDate = String.format(Locale.US, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);    // miesiące od zera
                    btnDate.setText(tripDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);  // tylko przyszłość
        dialog.show();
    }

    private void submitForm() {
        tvSummary.setText(buildSummary());
    }

    private String buildSummary() {
        String insurance = swInsurance.isChecked()
                ? getString(R.string.yes_short)
                : getString(R.string.no_short);

        return getString(R.string.summary_format,
                etName.getText().toString().trim(),
                spDestination.getSelectedItem().toString(),
                tripDate,
                sbPeople.getProgress() + 1,
                insurance);
    }
    private boolean validateForm() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name));
            etName.requestFocus();
            return false;
        }

        if (spDestination.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_destination, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tripDate.isEmpty()) {
            Toast.makeText(this, R.string.error_date, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}