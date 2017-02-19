package com.blogspot.dbh4ck.aes_encrypt_decrypt_android;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Nav_AES_Android extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    EditText input_aes, file_name;
    Button btn_aes_enc, btn_aes_dec, btn_write_file;
    TextView result_aes, dec_Txt;

    String output= "";
    String plainText;

    AboutAesDialog aboutAesDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav__aes__android);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
        }

        input_aes = (EditText) findViewById(R.id.txtInput);
        file_name = (EditText) findViewById(R.id.txtFileName);

        btn_aes_enc = (Button) findViewById(R.id.btnEncrypt);
        btn_aes_enc.setOnClickListener(this);

        btn_aes_dec = (Button) findViewById(R.id.btnDecrypt);
        btn_aes_dec.setOnClickListener(this);

        btn_write_file = (Button) findViewById(R.id.btnSaveEncryptFile);
        btn_write_file.setOnClickListener(this);

        result_aes = (TextView) findViewById(R.id.txtOutput);
        dec_Txt = (TextView) findViewById(R.id.txtOriginal);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav__aes__android, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }else if (id == R.id.nav_blog) {
            dbBlog();
        } else if (id == R.id.nav_about) {
            About();
        } else if (id == R.id.nav_exit) {
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void About() {
        aboutAesDialog = new AboutAesDialog(this);
        aboutAesDialog.show();
        aboutAesDialog.setCanceledOnTouchOutside(false);
    }

    private void dbBlog() {


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEncrypt:
                String strInput = input_aes.getText().toString();
                if (strInput.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Input must not be blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Encryption();
                }
                break;
            case R.id.btnDecrypt:
                Decryption();
                break;
            case R.id.btnSaveEncryptFile:
                try {
                    String strName = file_name.getText().toString();
                    if (strName.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "File name is required!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        if(strName.matches("^[a-zA-Z0-9_+-]*$")){
                            String data = input_aes.getText().toString();
                            writeToFile(data);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "File name should contain only a-z A-Z 0-9 +-_ and no spaces.", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void writeToFile(String data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AES-Android-Example");
        dir.mkdir();

        File newFrm = new File(dir + File.separator);
        File file = new File(newFrm, file_name.getText() +".txt");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(data);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File outfile = new File(file+".enc.txt");

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(outfile);

        Cipher encipher = Cipher.getInstance("AES");
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
        encipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, encipher);
        CipherOutputStream cos = new CipherOutputStream(fos, encipher);

        byte[] bArr = new byte[8];
        Toast.makeText(getApplicationContext(), "File is Ecrypted and Saved!", Toast.LENGTH_SHORT).show();
        while (true) {
            int read = fis.read(bArr);
            if (read != -1) {
                cos.write(bArr, 0, read);

            } else {
                cos.close();
                fis.close();
                return;
            }
        }
    }


    private void Decryption() {
        try {
            Cipher decipher = Cipher.getInstance("AES");
            SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            decipher.init(Cipher.DECRYPT_MODE, sks);

            byte[] decodedValue = Base64.decode(output.getBytes(), Base64.DEFAULT);
            byte[] decryptedVal = decipher.doFinal(decodedValue); // Finish
            output = new String(decryptedVal);
            dec_Txt.setText(output);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    private void Encryption() {
        try {

            plainText = input_aes.getText().toString();
            Cipher encipher = Cipher.getInstance("AES");
            SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            encipher.init(Cipher.ENCRYPT_MODE, sks);

            byte[] results = encipher.doFinal(plainText.getBytes("UTF-8"));
            output = Base64.encodeToString(results, Base64.DEFAULT);
            result_aes.setText(output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
