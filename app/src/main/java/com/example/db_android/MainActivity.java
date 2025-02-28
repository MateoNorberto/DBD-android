package com.example.db_android;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db_android.DatabaseHelper;
import com.example.db_android.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etName, etPhone;
    private Button btnAddContact, btnDeleteContact;
    private ListView listViewContacts;
    private ArrayList<String> contactList;
    private ArrayAdapter<String> adapter;
    private int selectedContactId = -1; // Para almacenar el ID del contacto seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnAddContact = findViewById(R.id.btnAddContact);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
        listViewContacts = findViewById(R.id.listViewContacts);

        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        listViewContacts.setAdapter(adapter);

        // Agregar contacto
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                if (!name.isEmpty() && !phone.isEmpty()) {
                    dbHelper.addContact(name, phone);
                    loadContacts();
                    etName.setText("");
                    etPhone.setText("");
                    Toast.makeText(MainActivity.this, "Contacto agregado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Eliminar contacto
        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContactId != -1) {
                    showDeleteConfirmationDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, selecciona un contacto para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Seleccionar contacto
        listViewContacts.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = dbHelper.getAllContacts();
            cursor.moveToPosition(position);
            selectedContactId = cursor.getInt(cursor.getColumnIndex("id"));
            Toast.makeText(MainActivity.this, "Contacto seleccionado: " + cursor.getString(cursor.getColumnIndex("name")), Toast.LENGTH_SHORT).show();
        });

        loadContacts();
    }

    // Cargar todos los contactos en la lista
    private void loadContacts() {
        contactList.clear();
        Cursor cursor = dbHelper.getAllContacts();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            contactList.add(name + ": " + phone);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // Mostrar el diálogo de confirmación de eliminación
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Eliminar contacto
                        dbHelper.deleteContact(selectedContactId);
                        loadContacts();
                        selectedContactId = -1; // Restablecer el ID del contacto seleccionado
                        Toast.makeText(MainActivity.this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
     // new design

}
