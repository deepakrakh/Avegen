package com.avegen;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.avegen.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //View Binding
    private ActivityMainBinding binding;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initialize amount to 1000 rs
        amount = 1000;

        // Value Animator
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,amount);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(valueAnimator1 -> binding.txtCurrentBalance.setText(new StringBuilder().append("₹ ")
                .append(valueAnimator1.getAnimatedValue().toString()).toString()));
        valueAnimator.start();

        // on EditText change listener
        binding.edtAmount.addTextChangedListener(new MyTextWatcher(binding.edtAmount));
        binding.edtAmount.addTextChangedListener(new MyTextWatcher(binding.edtAmount));

        //On button click Add and Subtract perform ATM operation's
        binding.btnAdd.setOnClickListener(view1 -> addAmountToAccount());
        binding.btnSub.setOnClickListener(view12 -> subAmountFromAccount());
    }


    /*
    * TODO: subtract amount from account
    * user can withdraw money from account
    * all validations are performed
    * */
    private void subAmountFromAccount() {
        if (!validateAmount()) {
            return;
        }
        if (!isValidLength()) {
            return;
        }

        try {

            if (amount == 0){
                binding.txtResult.setText(R.string.insufficient_balance);
                clear();
            }
            else {
                int result = amount - Integer.parseInt(binding.edtAmount.getText().toString().replace(",",""));

                if (result < 0){
                    binding.txtResult.setText(R.string.exceeded_limit);
                }
                else {
                    binding.txtCurrentBalance.setText(new StringBuilder().append("\u20B9 ").append(result).toString());
                    amount = result;
                    binding.txtResult.setText(new StringBuilder().append("\u20B9 ").append(binding.edtAmount.getText().toString())
                            .append(" Withdraw from your account").toString());
                    clear();
                }
            }

        }catch (NumberFormatException e){
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
    }

    /*
     * TODO: add amount to account
     * user can deposit money to account
     * all validations are performed
     * */
    private void addAmountToAccount() {
        if (!validateAmount()) {
            return;
        }
        if (!isValidLength()) {
            return;
        }

        try {
            int result = amount + Integer.parseInt(binding.edtAmount.getText().toString().replace(",",""));
            binding.txtCurrentBalance.setText(new StringBuilder().append("\u20B9 ").append(result).toString());
            amount = result;
            binding.txtResult.setText(new StringBuilder().append("\u20B9 ").append(binding.edtAmount.getText().toString())
                    .append(" Credited to your account").toString());
            clear();
        }catch (NumberFormatException e){
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }

    }

    // Clear EditText value after successful add and sub
    private void clear() {
        binding.edtAmount.setText(null);
        binding.txtAmount.setErrorEnabled(false);
    }


    // Validate EditText amount value
    private boolean validateAmount() {
        if (binding.edtAmount.getText().toString().trim().isEmpty()) {
            if (binding.edtAmount.getText().toString().length() > 6){
                binding.txtAmount.setError(getString(R.string.lower_amt));
            }
            else {
                binding.txtAmount.setError(getString(R.string.enter_amount));
            }
            requestFocus(binding.edtAmount);
            return false;

        } else {
            binding.txtAmount.setErrorEnabled(false);
        }

        return true;
    }

    // Validate EditText length
    private boolean isValidLength() {
        if (binding.edtAmount.getText().toString().length() > 8) {
            binding.txtAmount.setError(getString(R.string.lower_amount)+binding.edtAmount.getText().toString());
            requestFocus(binding.edtAmount);
            return false;
        } else {
            binding.txtAmount.setErrorEnabled(false);
        }
        return true;
    }

    // Get EditText focusable
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Implement Inner class TextWatcher
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = charSequence.toString();

            if (!input.isEmpty()) {

                input = input.replace(",", "");

                DecimalFormat format = new DecimalFormat("#,##,###");
                String newPrice = format.format(Double.parseDouble(input));

                binding.edtAmount.removeTextChangedListener(this); //To Prevent from Infinite Loop

                binding.edtAmount.setText(newPrice);
                binding.edtAmount.setSelection(newPrice.length()); //Move Cursor to end of String
                binding.edtAmount.addTextChangedListener(this);
            }
        }

        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.edtAmount) {
                validateAmount();
                isValidLength();
            }
        }
    }
}