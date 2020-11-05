package com.rica.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rica.crudfirebase.modelos.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listPersona = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    EditText nomP, appP, correoP, passwordP;
    ListView listV_personas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        nomP = findViewById(R.id.editNombre);
        appP = findViewById(R.id.editApellido);
        correoP = findViewById(R.id.editEmail);
        passwordP = findViewById(R.id.editcontra);

        listV_personas = findViewById(R.id.datosPersona);

        IniciarFirebase();
        ListarPersonas();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                nomP.setText(personaSelected.getNombre());
                appP.setText(personaSelected.getApellidos());
                correoP.setText(personaSelected.getCorreo());
                passwordP.setText(personaSelected.getPassword());
            }
        });

    }

    private void ListarPersonas() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPersona.clear();
                for (DataSnapshot objSnaptshot : snapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPersona.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1,listPersona);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void IniciarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String contra = passwordP.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add: {
                if (nombre.equals("")){
                    Validacion();
                }else {
                    Persona p = new  Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellidos(apellido);
                    p.setCorreo(correo);
                    p.setPassword(contra);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "agregado", Toast.LENGTH_SHORT).show();
                    LimpiarCajas();
                    break;
                }
            }

            case R.id.icon_save: {
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombre(nomP.getText().toString().trim());
                p.setApellidos(appP.getText().toString().trim());
                p.setCorreo(correoP.getText().toString().trim());
                p.setPassword(passwordP.getText().toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this, "actualizado", Toast.LENGTH_SHORT).show();
                LimpiarCajas();
                break;
            }

            case R.id.icon_delete: {
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this, "eliminado", Toast.LENGTH_SHORT).show();
                LimpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void LimpiarCajas() {
        nomP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    private void Validacion() {
        String nombre = nomP.getText().toString();
        String apellido = appP.getText().toString();
        String correo = correoP.getText().toString();
        String contra = passwordP.getText().toString();
        if (nombre.equals("")){
            nomP.setError("Required");
        }
        else if (apellido.equals("")){
            appP.setError("Required");
        }
        else if (correo.equals("")){
            correoP.setError("Required");
        }
        else if (contra.equals("")){
            passwordP.setError("Required");
        }
    }
}