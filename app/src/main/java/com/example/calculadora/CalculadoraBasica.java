package com.example.calculadora;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalculadoraBasica extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculadora_basica, container, false);
    }
}

//    Bundle bundle = new Bundle();
//                bundle.putInt("qtdJogadores", qtdJogadores);
//                        bundle.putInt("nRodadas", nRodadas);