package com.example.unitconverterapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Categories
    private final String[] categories = {"Length", "Weight", "Temperature", "Volume"};

    // Units per category (same order as categories)
    private final String[][] units = {
            {"Millimetre", "Centimetre", "Metre", "Kilometre", "Inch", "Foot", "Mile"},
            {"Milligram", "Gram", "Kilogram", "Ounce", "Pound"},
            {"Celsius", "Fahrenheit", "Kelvin"},
            {"Millilitre", "Litre", "Gallon (US)", "Cup (US)"}
    };

    // Factors to convert each unit into the base unit
    // Length -> metre, Weight -> gram, Volume -> litre
    // (Temperature is handled separately with formulas)
    private final double[][] factors = {
            {0.001, 0.01, 1.0, 1000.0, 0.0254, 0.3048, 1609.344},
            {0.001, 1.0, 1000.0, 28.3495, 453.592},
            {},
            {0.001, 1.0, 3.78541, 0.236588}
    };

    private Spinner spinnerCategory, spinnerFrom, spinnerTo;
    private EditText editValue;
    private Button btnConvert;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        editValue = findViewById(R.id.editValue);
        btnConvert = findViewById(R.id.btnConvert);
        textResult = findViewById(R.id.textResult);

        // Category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // When category changes, reset the unit spinners
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadUnits(position);
                textResult.setText("Result will appear here");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnConvert.setOnClickListener(v -> convert());
    }

    private void loadUnits(int categoryIndex) {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, units[categoryIndex]);
        spinnerFrom.setAdapter(unitAdapter);
        spinnerTo.setAdapter(unitAdapter);
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(1);
    }

    private void convert() {
        String input = editValue.getText().toString().trim();

        // Validation: empty
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation: non-numeric
        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        int category = spinnerCategory.getSelectedItemPosition();
        int fromIndex = spinnerFrom.getSelectedItemPosition();
        int toIndex = spinnerTo.getSelectedItemPosition();

        double result;
        if (category == 2) {
            result = convertTemperature(value, fromIndex, toIndex);
        } else {
            double baseValue = value * factors[category][fromIndex];
            result = baseValue / factors[category][toIndex];
        }

        DecimalFormat df = new DecimalFormat("#.######");
        String fromUnit = units[category][fromIndex];
        String toUnit = units[category][toIndex];
        textResult.setText(df.format(value) + " " + fromUnit + " = " + df.format(result) + " " + toUnit);
    }

    // 0 = Celsius, 1 = Fahrenheit, 2 = Kelvin
    private double convertTemperature(double value, int from, int to) {
        // Convert to Celsius first
        double celsius;
        if (from == 0) {
            celsius = value;
        } else if (from == 1) {
            celsius = (value - 32) * 5.0 / 9.0;
        } else {
            celsius = value - 273.15;
        }

        // Then from Celsius to target
        if (to == 0) {
            return celsius;
        } else if (to == 1) {
            return celsius * 9.0 / 5.0 + 32;
        } else {
            return celsius + 273.15;
        }
    }
}