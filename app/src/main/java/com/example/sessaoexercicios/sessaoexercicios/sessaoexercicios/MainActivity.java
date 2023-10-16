package com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios;

import static java.util.Objects.nonNull;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sessaoexercicios.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Categoria;
import com.example.sessaoexercicios.sessaoexercicios.sessaoexercicios.model.Exercicio;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDeDados;
    private ListView listViewDados;

    private Button botaoCadastrar;
    private Button botaoIncluirCategoria;
    public List<Exercicio> exercicios;

    public List<Categoria> categorias;

    public Exercicio exercicioSelecionado;

    private Categoria categoriaSelecionada;

    private Spinner spinner;

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
        spinnerAcao();
        listaCategorias();

    }

    private void spinnerAcao() {
        spinner = findViewById(R.id.listaCategoria);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                categoriaSelecionada = categorias.get(position);
                listarDados(categoriaSelecionada.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                listarDados(categoriaSelecionada.getId());
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
            String sql = "delete from exercicios where id = ?";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);
            stmt.bindLong(1,exercicioSelecionado.getId());
            stmt.executeUpdateDelete();
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


    private void listaCategorias() {
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

            ArrayAdapter adapter = new ArrayAdapter<Categoria>(this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    categorias);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void listarDados(int idCategoria) {
        try {
            logger.info("Iniciando consulta de exercicios");
            OpenOrCreateBancoDados();
            Cursor cursor = bancoDeDados.rawQuery("SELECT ex.id as id, ce.nome as tipo," +
                    " ex.nome_exercicio as nome_exercicio, ex.serie as serie, ex.sessao as sessao," +
                    " data_criacao, data_ultima_alteracao  from exercicios ex " +
                    " inner join categoria_exercicio ce on ce.id = ex.id_categoria_exercicios where ce.id = ?",new String[] {String.valueOf(idCategoria)});

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
                exercicio.setCategoria(new Categoria(cursor.getString(1)));
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

            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS exercicios("+
                    "id INTEGER primary key AUTOINCREMENT," +
                    "id_categoria_exercicios INTEGER," +
                    "nome_exercicio VARCHAR," +
                    "serie LONG," +
                    "sessao LONG," +
                    "data_criacao default current_timestamp," +
                    "data_ultima_alteracao DATE," +
                    "CONSTRAINT FK_cat_exercicios foreign key(id_categoria_exercicios) references categoria_exercicio(id))");
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
        listaCategorias();
        if(nonNull(categoriaSelecionada))
            listarDados(categoriaSelecionada.getId());
    }
}