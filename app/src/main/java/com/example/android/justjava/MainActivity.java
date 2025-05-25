package com.example.android.justjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void increment(View view) {
        if (quantity == 100) {
            Toast.makeText(this, "Maximum 100 coffees", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity++;
        displayQuantity(quantity);
    }

    public void decrement(View view) {
        if (quantity == 1) {
            Toast.makeText(this, "Minimum 1 coffee", Toast.LENGTH_SHORT).show();
        }
        if (quantity > 0) {
            quantity--;
            displayQuantity(quantity);
        }
    }

    public void submitOrder(View view) {
        // Get user inputs
        String name = ((EditText) findViewById(R.id.name_field)).getText().toString();
        boolean hasWhippedCream = ((CheckBox) findViewById(R.id.whipped_cream_checkbox)).isChecked();
        boolean hasChocolate = ((CheckBox) findViewById(R.id.chocolate_checkbox)).isChecked();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity == 0) {
            Toast.makeText(this, "Please select at least 1 coffee", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = calculatePrice(hasWhippedCream, hasChocolate);
        String message = createOrderSummary(name, price, hasWhippedCream, hasChocolate);

        // Show summary in app first
        displayOrderSummary(message);

        // Then try to send email
        sendEmail(name, message);
    }

    private void sendEmail(String name, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // MIME type for email
        intent.putExtra(Intent.EXTRA_SUBJECT, "JustJava Order for " + name);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Send email using:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayOrderSummary(String message) {
        TextView summaryText = findViewById(R.id.order_summary_text);
        summaryText.setText(message);

        MaterialCardView summaryCard = findViewById(R.id.order_summary_card);
        summaryCard.setVisibility(View.VISIBLE);
    }

    private int calculatePrice(boolean addWhippedCream, boolean addChocolate) {
        int basePrice = 5;
        if (addWhippedCream) basePrice += 1;
        if (addChocolate) basePrice += 2;
        return quantity * basePrice;
    }

    private String createOrderSummary(String name, int price, boolean addWhippedCream, boolean addChocolate) {
        return String.format(
                "Name: %s\n" +
                        "Add whipped cream? %b\n" +
                        "Add chocolate? %b\n" +
                        "Quantity: %d\n" +
                        "Total: %s\n" +
                        "Thank you!",
                name, addWhippedCream, addChocolate, quantity,
                NumberFormat.getCurrencyInstance().format(price)
        );
    }

    private void displayQuantity(int number) {
        ((TextView) findViewById(R.id.quantity_text_view)).setText(String.valueOf(number));
    }
}