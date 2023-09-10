package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sessaoexercicios.R;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Exercicio;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDeDados;
    private ListView listViewDados;

    private final Logger logger = Logger.getLogger(String.valueOf(MainActivity.class));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewDados = (ListView) findViewById(R.id.listViewDados);

        criarBancoDados();
        popularDados();
        listarDados();
    }

    private void listarDados() {
        try {
            logger.info("Iniciando consulta de exercicios");
            OpenOrCreateBancoDados();
            Cursor cursor = bancoDeDados.rawQuery("SELECT id, tipo, nome_exercicio, serie, sessao," +
                    " data_criacao, data_ultima_alteracao  from academia",null);

            ArrayList<Exercicio> linha = new ArrayList<>();
            ArrayAdapter meuAdapter = new ArrayAdapter<Exercicio>(this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linha);
            listViewDados.setAdapter(meuAdapter);
            cursor.moveToFirst();


            int quantidadeRegistro = cursor.getCount();

            logger.info("Quantidade: {}"+quantidadeRegistro);


            for(int i =0; i< cursor.getCount();i++){

                Exercicio exercicio = new Exercicio();

                exercicio.setId(cursor.getInt(0));
                exercicio.setTipo(cursor.getString(1));
                exercicio.setNomeExercicio(cursor.getString(2));
                exercicio.setSerie(cursor.getString(3));
                exercicio.setSessao(cursor.getString(4));
                exercicio.setDataInclusao(cursor.getString(5));
                exercicio.setDataAlteracao(cursor.getString(6));

                linha.add(exercicio);
                cursor.moveToNext();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void criarBancoDados() {
        try{

            logger.info("Criando banco de dados");
            OpenOrCreateBancoDados();
            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS academia("+
                    "id INTEGER primary key AUTOINCREMENT," +
                    "tipo VARCHAR," +
                    "nome_exercicio VARCHAR," +
                    "serie LONG," +
                    "sessao LONG," +
                    "data_criacao default current_timestamp," +
                    "data_ultima_alteracao DATE)");
            bancoDeDados.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void OpenOrCreateBancoDados() {
        bancoDeDados = openOrCreateDatabase("academiaApp", MODE_PRIVATE, null);
    }

    private void popularDados(){
        try {
            logger.info("Popoluando banco de dados");
            OpenOrCreateBancoDados();

//            String sqlDelete = "delete from academia";
//            SQLiteStatement stmtDelete = bancoDeDados.compileStatement(sqlDelete);
//            stmtDelete.execute();

            String sql = "INSERT INTO academia (tipo,nome_exercicio,serie,sessao) values(?,?,?,?)";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);

            stmt.bindString(1, "Superior");
            stmt.bindString(2, "Supino Reto");
            stmt.bindLong(3, 4l);
            stmt.bindLong(4, 10L);

            stmt.executeInsert();

            bancoDeDados.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}