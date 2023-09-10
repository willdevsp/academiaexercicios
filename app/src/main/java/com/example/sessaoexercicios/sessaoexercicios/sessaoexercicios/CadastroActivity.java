package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sessaoexercicios.R;
import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Exercicio;

import java.util.logging.Logger;


public class CadastroActivity extends AppCompatActivity {

    private SQLiteDatabase bancoDeDados;

    EditText tipoExercicio;
    EditText nomeExercicio;
    EditText serie;
    EditText sessao;
    Button botaoSalvar;

    private final Logger logger = Logger.getLogger(String.valueOf(CadastroActivity.class));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        tipoExercicio = (EditText) findViewById(R.id.tipoExercicio);
        nomeExercicio = (EditText) findViewById(R.id.nomeExercicio);
        serie = (EditText) findViewById(R.id.serie);
        sessao = (EditText) findViewById(R.id.sessao);
        botaoSalvar = (Button) findViewById(R.id.cadadastrar_exercicio);


        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar();
            }
        });
    }

    private void cadastrar(){
        try {
            logger.info("Popoluando banco de dados");
            OpenOrCreateBancoDados();

            Exercicio exercicio = new Exercicio();

//            String sqlDelete = "delete from academia";
//            SQLiteStatement stmtDelete = bancoDeDados.compileStatement(sqlDelete);
//            stmtDelete.execute();

            if(!TextUtils.isEmpty(tipoExercicio.getText().toString())){
                exercicio.setTipo(tipoExercicio.getText().toString());
            }

            if(!TextUtils.isEmpty(nomeExercicio.getText().toString())){
                exercicio.setNomeExercicio(nomeExercicio.getText().toString());
            }

            if(!TextUtils.isEmpty(serie.getText().toString())){
                exercicio.setSerie(serie.getText().toString());
            }
            if(!TextUtils.isEmpty(sessao.getText().toString())){
                exercicio.setSerie(sessao.getText().toString());
            }

            String sql = "INSERT INTO academia (tipo,nome_exercicio,serie,sessao) values(?,?,?,?)";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);

            stmt.bindString(1, exercicio.getTipo());
            stmt.bindString(2, exercicio.getNomeExercicio());
            stmt.bindString(3, exercicio.getSerie());
            stmt.bindString(4, exercicio.getSessao());

            stmt.executeInsert();

            bancoDeDados.close();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void OpenOrCreateBancoDados() {
        bancoDeDados = openOrCreateDatabase("academiaApp", MODE_PRIVATE, null);
    }
}