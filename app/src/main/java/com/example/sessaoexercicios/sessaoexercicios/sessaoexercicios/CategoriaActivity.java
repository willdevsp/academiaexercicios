package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sessaoexercicios.R;

import java.util.logging.Logger;

public class CategoriaActivity extends AppCompatActivity {

    private final Logger logger = Logger.getLogger(String.valueOf(CategoriaActivity.class));

    private SQLiteDatabase bancoDeDados;

    EditText categoria;
    Button botaoSalvar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        categoria = findViewById(R.id.categoria);
        botaoSalvar = (Button) findViewById(R.id.cadadastrar_categoria);
        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incluir();
            }
        });
    }

    private void incluir() {
        logger.info("Incluindo nova categoria de exercicio");

        OpenOrCreateBancoDados();

        String categoriaInformada = categoria.getText().toString();
        if(categoriaInformada.isEmpty())
            throw new RuntimeException("É necessário informar uma categoria");


        String sql = "INSERT INTO categoria_exercicio(nome) values(?)";
        SQLiteStatement stmt = bancoDeDados.compileStatement(sql);
        stmt.bindString(1,categoriaInformada);

        stmt.executeInsert();

        bancoDeDados.close();
        logger.info(String.format("Categoria %s inclusa com sucesso",categoriaInformada));
        finish();
    }


    private void OpenOrCreateBancoDados() {
        bancoDeDados = openOrCreateDatabase("academiaApp", MODE_PRIVATE, null);
    }
}
