package com.example.sev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //cadastrar usuario
        progressBar.setVisibility(View.GONE);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);

                            cadastrar(usuario);

                        } else{
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha o sua senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o seu Email!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o seu Nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cadastrar(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CadastroActivity.this,
                                    "Cadastrado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);

                            String erroExcessao = "";
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                erroExcessao = "Digite uma senha mais forte";
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcessao = "Digite um e-mail válido";
                            } catch (FirebaseAuthUserCollisionException e){
                                erroExcessao = "Essa conta ja foi cadastrada";
                            } catch (Exception e){
                                erroExcessao = "ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    "ERRO:V" + erroExcessao,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void inicializarComponentes(){
        campoNome = findViewById(R.id.nome_cadastro);
        campoEmail = findViewById(R.id.email_login);
        campoSenha = findViewById(R.id.senha_login);
        botaoCadastrar = findViewById(R.id.btn_cadastrar);
        progressBar = findViewById(R.id.progress_cadastro);
    }

    public void abrirLogin(View view){
        Intent i = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(i);
    }
}