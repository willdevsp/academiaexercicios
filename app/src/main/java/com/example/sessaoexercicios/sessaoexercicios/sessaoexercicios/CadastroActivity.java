package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import static java.util.Objects.nonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sessaoexercicios.R;
import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Categoria;
import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Exercicio;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class CadastroActivity extends AppCompatActivity {

    private SQLiteDatabase bancoDeDados;


    EditText nomeExercicio;
    EditText serie;
    EditText sessao;
    Button botaoSalvar;
    Spinner spinner;

    private List<Categoria> categorias;
    private Categoria categoriaSelecionada;

    private final Logger logger = Logger.getLogger(String.valueOf(CadastroActivity.class));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

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

        carregaCategorias();
        spinnerAcao();
    }

    private void spinnerAcao() {
        spinner = findViewById(R.id.listaCategoria);

        ArrayAdapter adapter = new ArrayAdapter<Categoria>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                categoriaSelecionada = categorias.get(position);
                Toast.makeText(parent.getContext(),categoriaSelecionada.getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

            if(nonNull(categoriaSelecionada)){
                exercicio.setCategoria(categoriaSelecionada);
            }

            if(!TextUtils.isEmpty(nomeExercicio.getText().toString())){
                exercicio.setNomeExercicio(nomeExercicio.getText().toString());
            }

            if(!TextUtils.isEmpty(serie.getText().toString())){
                exercicio.setSerie(serie.getText().toString());
            }
            if(!TextUtils.isEmpty(sessao.getText().toString())){
                exercicio.setSessao(sessao.getText().toString());
            }

            String sql = "INSERT INTO exercicios(id_categoria_exercicios,nome_exercicio,serie,sessao) values(?,?,?,?)";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);

            stmt.bindLong(1, exercicio.getCategoria().getId());
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

    private void carregaCategorias() {
        try {
            logger.info("Iniciando consulta de categorias");
            OpenOrCreateBancoDados();
            Cursor cursor = bancoDeDados.rawQuery("SELECT id, nome from categoria_exercicio",null);

            ArrayList<Categoria> linha = new ArrayList<>();
            cursor.moveToFirst();

            int quantidadeRegistro = cursor.getCount();

            logger.info("Quantidade: "+quantidadeRegistro);

            categorias = new ArrayList<>();

            for(int i =0; i< cursor.getCount();i++){

                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                Categoria categoria = new Categoria(id,nome);
                categorias.add(categoria);
                linha.add(categoria);
                cursor.moveToNext();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void OpenOrCreateBancoDados() {
        bancoDeDados = openOrCreateDatabase("academiaApp", MODE_PRIVATE, null);
    }
}