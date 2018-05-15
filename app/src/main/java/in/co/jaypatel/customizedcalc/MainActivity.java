package in.co.jaypatel.customizedcalc;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnModulo, R.id.btnPower};
    private TextView txtScreen;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtScreen = findViewById(R.id.txtScreen);
        txtScreen.setMovementMethod(new ScrollingMovementMethod());
        setNumericOnClickListener();
        setOperatorOnClickListener();
        if (savedInstanceState == null) {
            lastNumeric = true;
        } else {
            txtScreen.setText(savedInstanceState.getString("txtScreen"));
            lastNumeric = savedInstanceState.getBoolean("lastNumeric");
            stateError = savedInstanceState.getBoolean("stateError");
            lastDot = savedInstanceState.getBoolean("lastDot");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtScreen", txtScreen.getText().toString());
        outState.putBoolean("lastNumeric", lastNumeric);
        outState.putBoolean("stateError", stateError);
        outState.putBoolean("lastDot", lastDot);
    }

    //Find and set OnClickListener to numeric buttons.
    private void setNumericOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (stateError) {
                    txtScreen.setText(button.getText());
                    stateError = false;
                } else {
                    String text = txtScreen.getText().toString();
                    if (text.length() == 1 && text.equals("0")) {
                        txtScreen.setText(button.getText());
                    } else {
                        txtScreen.append(button.getText());
                    }
                }
                lastNumeric = true;
            }
        };
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    //Find and set OnClickListener to operator buttons, equal button and decimal point button.
    private void setOperatorOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false;
                }
            }
        };
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot) {
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });
        findViewById(R.id.btnCE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtScreen.getText().toString();
                if (text.length() != 1) {
                    if (!stateError) {
                        text = text.substring(0, text.length() - 1);
                        txtScreen.setText(text);
                    } else {
                        txtScreen.setText("0");
                    }
                } else {
                    txtScreen.setText("0");
                }

                text = txtScreen.getText().toString();
                if (text.length() > 0) {
                    char temp = text.charAt(text.length() - 1);
                    if (temp == '.') {
                        lastDot = true;
                        lastNumeric = false;
                    } else if (temp == '+' || temp == '-' || temp == '^' || temp == '*' || temp == '/' || temp == '%') {
                        lastNumeric = false;
                        lastDot = false;
                    } else {
                        lastDot = false;
                        lastNumeric = true;
                    }
                }
            }
        });
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("0");
                lastNumeric = true;
                stateError = false;
                lastDot = false;
            }
        });
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });
    }

    //Logic to calculate the solution.
    private void onEqual() {
        if (lastNumeric && !stateError) {
            String txt = txtScreen.getText().toString();
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                txtScreen.setText(formatValue(result));
            } catch (ArithmeticException ex) {
                txtScreen.setText(ex.getMessage());
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public String formatValue(double d) {
        if (d == (long) d) {
            lastDot = false;
            return String.format("%d", (long) d);
        } else {
            lastDot = true;
            return new DecimalFormat("##.####").format(d);
        }
    }

}
