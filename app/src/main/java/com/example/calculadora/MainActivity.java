package com.example.calculadora;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {
    String expressao = "";
    TextView resultado, idResultadoParcial;
    int finalizado = 0;
    private AlertDialog ajuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultado = findViewById(R.id.resultado);
        idResultadoParcial = findViewById(R.id.idResultadoParcial);

        replaceFragment(new CalculadoraBasica());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    private void ajuda() {
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.activity_ajuda, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setView(view);
        ajuda = builder.create();
        ajuda.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.calculadoraBasica:
                replaceFragment(new CalculadoraBasica());
                break;
            case R.id.calculadoraAvancada:
                replaceFragment(new CalculadoraAvancada());
                break;
            case R.id.ajuda:
                ajuda();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameBotoes, fragment);
        fragmentTransaction.commit();
    }

    public void onClick(View view) {

        if(finalizado == 1){
            resultado.setText("");
            idResultadoParcial.setText(expressao="");
            finalizado = 0;
        }

        String numeroDigitado = ((TextView) view).getText().toString();
        resultado.setText("");
        System.out.println(numeroDigitado);
        if(numeroDigitado.equals("C")){
            idResultadoParcial.setText(expressao="");

        }else if(numeroDigitado.equals("=") && !numeroDigitado.equals("")){
            if(expressao.contains("%")){
                expressao = porcentagem(expressao);
                System.out.println("Expressso: "+expressao);
                resultado.setText(Double.toString(eval(expressao, getApplicationContext())));
                expressao = expressao.replace("(", "");
                expressao = expressao.replace("/100)*", "%");
                System.out.println(expressao);
            }else{
                resultado.setText(Double.toString(eval(expressao, getApplicationContext())));
            }

            finalizado = 1;

//            System.out.println(Double.toString(eval("((4 - 2^3 + 1) * -sqrt(3*3+4*4)) / 2", getApplicationContext())));

        }else{
            if(numeroDigitado.equals("X")){
                expressao += "*";
            }else if(numeroDigitado.equals("÷")){
                expressao += "/";
            }else if(numeroDigitado.equals("L")) {
                expressao = expressao.substring(0, expressao.length()-1);
            }else if(numeroDigitado.equals("√")) {
                expressao += numeroDigitado.replace("√", "sqrt");
            }else{
                expressao += numeroDigitado;
            }

        }
        idResultadoParcial.setText(expressao.replace("sqrt", "√"));
    }

    private String porcentagem(String numeroDigitado) {
        String perc = "(";
        try{
            for (int i=0; i< numeroDigitado.length(); i++) {
                char c = numeroDigitado.charAt(i);
                if(c == '%'){
                    perc+="/100)*";
                }else{
                    perc += c;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return perc;
    }

    public static double eval(final String str, Context applicationContext) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
//                if (pos < str.length())  throw new RuntimeException("Inesperado: " + (char)ch);
                if (pos < str.length()) Toast.makeText(applicationContext, "Expressão mal formada, use o meu ajuda!", Toast.LENGTH_LONG).show();
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // Adiçao
                    else if (eat('-')) x -= parseTerm(); // subtraçao
                    else return x;
                    System.out.println("x: "+x);
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplicaçao
                    else if (eat('/')) x /= parseFactor(); // divisao
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); //
                if (eat('-')) return -parseFactor(); //

                double x = 0;
                int startPos = this.pos;
                if (eat('(')) { // Parenteses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numeros
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // funçoes
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else Toast.makeText(applicationContext, "Funcao desconhecida:"+ func, Toast.LENGTH_LONG).show();
//                    else throw new RuntimeException("Funcao desconhecida: " + func);
                } else {
                    Toast.makeText(applicationContext, "Expressão mal formada, use o meu ajuda!", Toast.LENGTH_LONG).show();
//                    throw new RuntimeException("Inesperado: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponencial

                return x;
            }
        }.parse();
    }

}