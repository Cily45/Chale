package fr.cily.chale;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private final String SAVE_FILE = "save.txt";
    private TextView compteurG;
    private TextView compteurAD;
    private TextView compteurF;
    private TextView maille;
    private Button buttonMinus;
    private Button buttonPlus;
    private TextView instruction;
    private int[] compteurs; // general, fantaisie, augmentation reduction, mailles
    private String compteurString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        compteurs = new int[4];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView lienHypertext = findViewById(R.id.lienPatron);
        lienHypertext.setLinksClickable(true);
        this.compteurG = (TextView) findViewById(R.id.compteurG);
        this.buttonMinus = (Button) findViewById(R.id.buttonMinus);
        this.buttonPlus = (Button) findViewById(R.id.buttonPlus);
        this.compteurAD = (TextView) findViewById(R.id.compteurAD);
        this.compteurF = (TextView) findViewById(R.id.compteurF);
        this.maille = (TextView) findViewById(R.id.maille);
        this.instruction = (TextView) findViewById(R.id.instruction);
        onResume();
        affichageCompteur();
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compteurs[0]++;
                compteurs[2] = modificationCompteurAugDim('+');
                compteurs[1] = modificationCompteurFantaisie('+');
                if (compteurs[2] == 1) {
                    compteurs[3]++;
                }
                affichageCompteur();

            }
        });
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compteurs[0] != 1) {
                    compteurs[0]--;
                    compteurs[2] = modificationCompteurAugDim('-');
                    compteurs[1] = modificationCompteurFantaisie('-');
                    if (compteurs[2] == 4 && compteurs[0] > 1) {
                        compteurs[3]--;
                    }
                    affichageCompteur();
                }

            }
        });
        onPause();
    }
    private void affichageCompteur() {
        compteurG.setText("Numeros du rang: " + compteurs[0]);
        compteurAD.setText("Compteur des " + (compteurs[3] <= 76? "augmentations: ":"diminutions: ") + compteurs[2]);
        compteurF.setText("Compteur du point fantaisie: " + compteurs[1]);
        maille.setText("Nombres de mailles: " + compteurs[3]);
        StringBuilder instru = new StringBuilder();

        if (compteurs[2] == 4 && compteurs[3] <= 76) {
            instru.append("Tricoter 2 fois la 1ère maille sur l'endroit").append("\n");
        } else if (compteurs[2] == 4) {
            instru.append("Tricoter les 2 premières mailles ens à l'endroit").append("\n");
        }

        if (compteurs[1] < 9) {
            instru.append("Tricoter toutes les mailles à l'endroit.");
        } else if (compteurs[1] == 9) {
            instru.append("A l'endroit, * 2 mailles ens à l'end, 1 double jeté *, répéter de *-*.");
        } else {
            instru.append("A l'endroit, tricoter le 1er des 2 jetés, lâcher le 2ème. Répéter ces 10 rangs.");
        }

        instruction.setText(instru.toString());
    }

    private int modificationCompteurAugDim(char operateur) {
        if (operateur == '+') {
            return compteurs[2] == 4 ? 1 : compteurs[2] + 1;
        } else {
            return compteurs[2] == 1 ? 4 : compteurs[2] - 1;
        }
    }

    private int modificationCompteurFantaisie(char operateur) {
        if (operateur == '+') {
            return compteurs[1] == 10 ? 1 : compteurs[1] + 1;
        } else {
            return compteurs[1] == 1 ? 10 : compteurs[1] - 1;
        }
    }
    private void getCompteurs(){
        String result = "";
        for(int i : compteurs){
            result += i + " ";
        }
        compteurString = result.trim();
    }

    private void setCompteurs(){
        String[] split = compteurString.split(" ");
        for(int i = 0; i < 4; i++){
            compteurs[i] = Integer.parseInt(split[i]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getCompteurs();
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString("compteurString", compteurString);
        myEdit.apply();

    }

    @Override
    protected void onResume() {
        SharedPreferences sh = getSharedPreferences("save", MODE_PRIVATE);
        String s1 = sh.getString("compteurString", "0 0 0 4");
        compteurString = s1;
        setCompteurs();
        super.onResume();
    }


}