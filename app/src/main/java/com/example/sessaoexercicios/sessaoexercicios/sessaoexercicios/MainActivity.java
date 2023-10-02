package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sessaoexercicios.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Exercicio;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDeDados;
    private ListView listViewDados;

    private Button botaoCadastrar;
    private Button botaoIncluirCategoria;
    public List<Exercicio> exercicios;

    public Exercicio exercicioSelecionado;

    private final Logger logger = Logger.getLogger(String.valueOf(MainActivity.class));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewDados = (ListView) findViewById(R.id.listViewDados);

        botaoCadastrar = (Button) findViewById(R.id.btnCadastrarExercicios);
        botaoIncluirCategoria =  (Button) findViewById(R.id.btnIncluirCategoria);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();
            }
        });

        listViewDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                exercicioSelecionado = exercicios.get(i);
                confirmarExclusao();
                return true;
            }
        });
        botaoIncluirCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaInclusaoCategoria();
            }
        });

        criarBancoDados();
        listarDados();
    }

    private void confirmarExclusao() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
        msgBox.setTitle("Exclusão de exercício");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Você realmente deseja excluir o exercicio '"+exercicioSelecionado.getNomeExercicio()+"'" );
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluirDado();
                Toast.makeText(MainActivity.this, exercicioSelecionado + "Removido com sucesso", Toast.LENGTH_SHORT).show();
                listarDados();
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        msgBox.show();

    }

    private void excluirDado() {
        logger.info("Excluindo exercicio: "+exercicioSelecionado.getNomeExercicio());
        OpenOrCreateBancoDados();
        try{
            String sql = "delete from academia where id = ?";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);
            stmt.bindLong(1,exercicioSelecionado.getId());
            stmt.executeUpdateDelete();
            listarDados();
            bancoDeDados.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void abrirTelaInclusaoCategoria() {
        Intent intent = new Intent(this, CategoriaActivity.class);
        startActivity(intent);
    }

    private void abrirTelaCadastro() {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
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

            logger.info("Quantidade: "+quantidadeRegistro);

            exercicios = new ArrayList<>();

            for(int i =0; i< cursor.getCount();i++){

                Exercicio exercicio = new Exercicio();

                exercicio.setId(cursor.getInt(0));
                exercicio.setTipo(cursor.getString(1));
                exercicio.setNomeExercicio(cursor.getString(2));
                exercicio.setSerie(cursor.getString(3));
                exercicio.setSessao(cursor.getString(4));
                exercicio.setDataInclusao(cursor.getString(5));
                exercicio.setDataAlteracao(cursor.getString(6));
                exercicios.add(exercicio);
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

            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS categoria_exercicio("+
                    "id INTEGER primary key AUTOINCREMENT," +
                    "nome VARCHAR(100))");

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

    @Override
    protected void onResume(){
        super.onResume();
        listarDados();
    }
}