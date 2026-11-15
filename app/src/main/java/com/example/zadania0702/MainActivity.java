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
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.Calendar;
import java.util.Locale;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private Spinner spDestination;
    private SeekBar sbPeople;
    private TextView tvPeopleValue;
    private EditText etName;
    private Button btnDate, btnSubmit;
    private CheckBox cbTerms;
    private MaterialSwitch swInsurance;
    private TextView tvSummary;

    private Button btnClear;

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
        btnClear = findViewById(R.id.btnClear);

        setUpDestinationSpinner();
        setUpListeners();
        tvPeopleValue.setText(String.valueOf(sbPeople.getProgress() + 1));
    }
    // zdjęcie stanu formularza - wartości domyślne = formularz pusty
    private static class FormState {
        String name = "";
        String date = "";
        int destination = 0;
        int people = 0;
        boolean insurance = false;
        boolean terms = false;
        String summary = "";
    }

    private FormState captureState() {
        FormState s = new FormState();
        s.name = etName.getText().toString();
        s.date = tripDate;
        s.destination = spDestination.getSelectedItemPosition();
        s.people = sbPeople.getProgress();
        s.insurance = swInsurance.isChecked();
        s.terms = cbTerms.isChecked();
        s.summary = tvSummary.getText().toString();
        return s;
    }

    private void restoreState(FormState s) {
        etName.setText(s.name);
        tripDate = s.date;
        btnDate.setText(s.date.isEmpty() ? getString(R.string.btn_pick_date) : s.date);
        spDestination.setSelection(s.destination);
        sbPeople.setProgress(s.people);
        swInsurance.setChecked(s.insurance);
        cbTerms.setChecked(s.terms);
        tvSummary.setText(s.summary);
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
                askForConfirmation();
            }
        });
        btnClear.setOnClickListener(v -> askToClear());
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
    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> submitForm())
                .setNegativeButton(R.string.no, null)
                .show();
    }
    private void submitForm() {
        tvSummary.setText(buildSummary());

        Snackbar.make(findViewById(R.id.main), R.string.sent, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> tvSummary.setText(""))
                .show();
    }
    private void askToClear() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    FormState backup = captureState();     // zapamiętaj PRZED czyszczeniem
                    clearForm();
                    Snackbar.make(findViewById(R.id.main), R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, v -> restoreState(backup))
                            .show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void clearForm() {
        etName.setText("");
        etName.setError(null);              // usuwa komunikat błędu z pola
        spDestination.setSelection(0);      // pozycja 0 = "-- wybierz kierunek --"
        tripDate = "";
        btnDate.setText(R.string.btn_pick_date);
        sbPeople.setProgress(0);
        swInsurance.setChecked(false);
        cbTerms.setChecked(false);    // listener sam zablokuje przycisk wysyłania
        tvSummary.setText("");
    }
}