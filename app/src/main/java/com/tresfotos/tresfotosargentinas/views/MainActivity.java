package com.tresfotos.tresfotosargentinas.views;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jinatonic.confetti.CommonConfetti;
import com.google.firebase.database.FirebaseDatabase;
import com.tresfotos.tresfotosargentinas.model.pojo.Palabra;
import com.tresfotos.tresfotosargentinas.R;
import com.tresfotos.tresfotosargentinas.model.pojo.User;
import com.tresfotos.tresfotosargentinas.database.AppDatabase;

public class MainActivity extends AppCompatActivity {

    public static final Integer totalLettersToFill = 12;
    private AppDatabase appDatabase;
    private Integer numeroDeMonedasASumar;
    private ConstraintLayout layout;
    private LinearLayout linearLayoutWord;
    private LinearLayout linearLayoutLettersFirstLine;
    private LinearLayout linearLayoutLettersSecondLine;
    private ImageButton imageButtonHint;
    private Button buttonSend;
    private ViewPager viewPager;
    private ViewpagerAdapter viewpagerAdapter;
    private Palabra palabraRandom;
    private List<Palabra> palabraList;
    private Toolbar toolbar;
    private TextView textviewPlata;
    private TextView textviewNivel;
    private TextView errorTextview;
    private User user;
    private Integer numeroPuntos;
    private String pistas;
    private String username;
    private String word;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        appDatabase = AppDatabase.getInMemoryDatabase(this);
        appDatabase.userDao().insertUser(new User("Tanifero"));
        user = appDatabase.userDao().getUser();
        username = appDatabase.userDao().getUser().getName();
        populateDatabaseWithArray();
        chequearSiElUsuarioYaGano();

        palabraList = appDatabase.palabraDao().getAllPalabras(false);
        palabraRandom = getPalabraFromArray(palabraList);
        checkIfThereIsPalabraInNivelActual();
        setPistaIfExist();
        word = palabraRandom.getNombrePalabra().toUpperCase();
        appDatabase.palabraDao().updateIfIsNivelActual(true, word.toLowerCase());

        numeroDeMonedasASumar = 14;
        numeroPuntos = 50;
        toolbar = findViewById(R.id.toolbar); setSupportActionBar(toolbar);
        layout = findViewById(R.id.layout_main_activity);
        linearLayoutWord = findViewById(R.id.linear_layout_answer);
        linearLayoutLettersFirstLine = findViewById(R.id.linear_layout_letters_first_line);
        linearLayoutLettersSecondLine = findViewById(R.id.linear_layout_letters_second_line);
        textviewNivel = findViewById(R.id.textview_nivel); textviewNivel.setText(user.getNivel().toString());
        imageButtonHint = findViewById(R.id.imagebutton_clue);
        buttonSend = findViewById(R.id.button_send);
        textviewPlata = findViewById(R.id.textview_toolbar_plata); textviewPlata.setText(user.getPlata().toString());
        errorTextview = findViewById(R.id.error_textview);

        viewPager = findViewById(R.id.imageview_center);
        viewpagerAdapter = new ViewpagerAdapter(this, palabraRandom);
        viewPager.setAdapter(viewpagerAdapter);
        cambiarPuntosAlVerNuevaFoto();

        fillWordToComplete();
        fillLetters(createWord(setRandomOrderToWord(createArrayOfAllLetters(word))), linearLayoutLettersFirstLine, linearLayoutLettersSecondLine);
        chequearSiHayPistaYPonerla();

        imageButtonHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sacarTodasLasLetrasPuestasSiNoSonHint();
                putRandomLetterOnHint();
                sacarPlataYCambiarElTextviewPlata();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = getPalabraQuePusoElUsuario();
                if (answer.equals(word)){
                    CommonConfetti.rainingConfetti(layout, new int[] { Color.BLUE }).oneShot();
                    popUpParaPasarAlSiguienteNivel();
                } else {
                    setTextviewError();
                    deleteErrorText();
                }
            }
        });
    }

    // Sirve en los metodos que crean programaticamente cosas del xml para usar dp en vez de pixels
    private int pixelsToDp(int dps){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    // Llena los espacios de la palabra que deberan ser llenados para pasar de nivel, con todas sus funcionalidades
    private void fillWordToComplete(){
        for (int i = 0; i < word.length() ; i++) {
            final TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pixelsToDp(34), pixelsToDp(34));
            layoutParams.setMargins(0, 0, 5, 0);
            textView.setLayoutParams(layoutParams);
            textView.setId(i);
            textView.setTextColor(Color.WHITE);
            textView.setBackground(getDrawable(R.drawable.textview_letters_with_corner_word));
            textView.setPadding(5, 5, 5, 5);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
//            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER);
            linearLayoutWord.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!textView.getText().toString().equals("")){
                        for (int j = 0; j < totalLettersToFill ; j++) {
                            TextView textView1 = findViewById(j + 10);
                            if (textView1.getText().toString().equals("") && textView.getCurrentTextColor() != Color.parseColor("#FFC107")
                            ){
                                textView1.setText(textView.getText().toString());
                                textView.setText("");
                            }
                        }
                    }
                }
            });
        }
    }

    // Llena las letras con las que se va a poder contestar, con todas sus funcionalidades
    private void fillLetters(final String randomLetters, LinearLayout linearLayoutLettersFirstLine, LinearLayout linearLayoutLettersSecondLine){
        for (int i = 0; i < randomLetters.length() ; i++) {
            final TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pixelsToDp(40), pixelsToDp(40));
            layoutParams.setMargins(0, 0, pixelsToDp(2), 0);
            textView.setLayoutParams(layoutParams);
            textView.setText(String.valueOf(randomLetters.charAt(i)));
            textView.setBackground(getDrawable(R.drawable.textview_letters_with_corner));
            textView.setPadding(5, 5, 5, 5);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(Gravity.CENTER);
//            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setId(i + 10);
            if (i < 6){ linearLayoutLettersFirstLine.addView(textView); } else { linearLayoutLettersSecondLine.addView(textView); }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < word.length(); j++) {
                        TextView textView1 = findViewById(j);
                        if (textView1.getText().toString().equals("")){
                            textView1.setText(textView.getText().toString());
                            textView.setText("");
                            break;
                        }
                    }
                }
            });
        }
    }

    // Recibe la palabra y crea un array que contiene las letras de la palabra mas las otras letras random
    private ArrayList<String> createArrayOfAllLetters(String word){
        ArrayList<String> arrayListAllLetters = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            arrayListAllLetters.add(String.valueOf(word.charAt(i)).toUpperCase());
        }
        for (int i = 0; i < (12 - word.length()) ; i++) {
            arrayListAllLetters.add(getRandomLetter().toUpperCase());
        }
        return arrayListAllLetters;
    }

    // devuelve una letra al azar del abecedario
    private String getRandomLetter(){
        Random r = new Random();
        return String.valueOf((char)(r.nextInt(26) + 'a'));
    }

    // Recibe un array de letras y devuelve un String
    private String createWord(ArrayList<String> arrayList ){
        String string = "";
        for (int i = 0; i < arrayList.size(); i++) {
            string = string + arrayList.get(i);
        }
        return string;
    }

    //Recibe un array de letras, las desordena aleatoriamente y devuelve ese resultado desordenado aleatoriamente
    private ArrayList<String> setRandomOrderToWord(ArrayList<String> arrayList){
        ArrayList<String> arrayListDesordered = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String element = "";
            element = arrayList.get(new Random().nextInt(arrayList.size()));
            arrayListDesordered.add(element);
            arrayList.remove(element);
        }
        return arrayListDesordered;
    }

    // Saca todas las letras menos las que son hint, esto se usa cuando apreta el hint
    private void sacarTodasLasLetrasPuestasSiNoSonHint(){
        for (int i = 0; i < word.length(); i++) {
            TextView textView = findViewById(i);
            if (!textView.getText().toString().equals("") && textView.getCurrentTextColor() != Color.parseColor("#FFC107")){
                for (int j = 0; j < totalLettersToFill; j++) {
                    TextView textView1 = findViewById(j + 10);
                    if (textView1.getText().toString().equals("")){
                        textView1.setText(textView.getText().toString());
                        textView.setText("");
                    }
                }
            }
        }
    }

    // Saca la letra que la pasas por parametro de las letras para llenar la palabra
    private void sacarLetra(String letra){
        for (int i = 0; i < totalLettersToFill; i++) {
            TextView textView = findViewById(i + 10);
            if (textView.getText().toString().equals(letra)){
                textView.setText("");
                break;
            }
        }
    }

    // Funcion que recibe un array de palabras, devuelve una palabra random del array y la quita
    private Palabra getPalabraFromArray(List<Palabra> arrayDePalabras){
        Palabra palabra = arrayDePalabras.get(new Random().nextInt(arrayDePalabras.size()));
        arrayDePalabras.remove(palabra);
        return palabra;
    }


    // Intent a Activity ganaste cuando ganas el juego
    public void intentToActivityGanaste(){
        Intent intent = new Intent(this, GanasteActivity.class);
        startActivity(intent);
    }

    // Se fija si el usuario ya estaba en algun nivel y lo hace el nivel actual. Esto lo hace haciendo que la palabra
    // "palabraRandom" sea la palabra en la cual el usuario ya estaba, de esa palabra sale la "word"
    private void checkIfThereIsPalabraInNivelActual(){
        for (Palabra palabra : palabraList) {
            if (palabra.getEsNivelActual() != null && palabra.getEsNivelActual()){
                palabraRandom = palabra;
                break;
            }
        }
    }

    //  Pone una latra random en el hint, este metodo se usa al usar la pista
    private void putRandomLetterOnHint(){
        String wordLetterForHint = "";
        for (int i = 0; i < 50; i++) {
            int indexOfwordLetterForHint = new Random().nextInt(word.length());
            wordLetterForHint = String.valueOf(word.charAt(indexOfwordLetterForHint));
            TextView hintedTextview = findViewById(indexOfwordLetterForHint);
            if (hintedTextview.getText().toString().equals("")){
                hintedTextview.setTextColor(Color.parseColor("#FFC107"));
                hintedTextview.setText(wordLetterForHint);
                sacarLetra(wordLetterForHint);
                agregarPistaApalabra(String.valueOf(indexOfwordLetterForHint));
                break;
            }
        }
    }

    // Devuelve la palabra que puso el usuario sumando todas las letras, se usa cuando el usuario pone "enviar"
    private String getPalabraQuePusoElUsuario(){
        String answer = "";
        for (int i = 0; i < word.length(); i++) {
            TextView textView = findViewById(i);
            String letterOfTextview = textView.getText().toString();
            answer = answer + letterOfTextview;
        }
        return answer;
    }

    // Cuando clickea "enviar", esta funcion es lo que pasa en caso de que la respuesta sea correcta
    private void funcionEnCasoDeQueLaPalabraSeaCorrecta(){
        appDatabase.palabraDao().updateIfIsAdivinada(true, word.toLowerCase());
        finish();
        if (appDatabase.palabraDao().getAllPalabras(false).size() < 1){
            intentToActivityGanaste();
        } else {
            appDatabase.palabraDao().updateIfIsNivelActual(false, word.toLowerCase());
            startActivity(getIntent());
        }
    }

    // Extrae 14 pesos de la base de datos y cambia el textview donde se ve la plata
    private void sacarPlataYCambiarElTextviewPlata(){
        if (appDatabase.userDao().getUser().getPlata() >= 14){
            appDatabase.userDao().updatePlata(appDatabase.userDao().getUser().getPlata() - 14);
            textviewPlata.setText(appDatabase.userDao().getUser().getPlata().toString());
        }
    }

    // Si el usuario ya gano y no empezo el juego devuelta lo manda al GanasteActivity
    private void chequearSiElUsuarioYaGano(){
        if (appDatabase.palabraDao().getAllPalabras(false).size() < 1){
            intentToActivityGanaste();
        }
    }

    // Funcion que suma un nivel
    private void sumarUnNivel(){
        appDatabase.userDao().updateLevel(user.getNivel() + 1);
    }

    // Funcion para que al cambiar de pagina cambien los puntos que sumas
    private void cambiarPuntosAlVerNuevaFoto(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 1 && numeroPuntos == 50){
                    numeroPuntos = 30;
                } else if (position == 2 && numeroPuntos == 30){
                    numeroPuntos = 15;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (numeroDeMonedasASumar == 14){
                    numeroDeMonedasASumar = 7;
                }
            }
        });
    }

    // Funcion donde esta el modal que aparece cuando ganas, lo muestra, y ejecuta las acciones al pasar de nivel
    private void popUpParaPasarAlSiguienteNivel(){
        ArrayList<String> frasesGanadoras = new ArrayList<>();
        Random rand = new Random();
        frasesGanadoras.add("Segui asi, " + username + "!");frasesGanadoras.add("Bueena " + username + "!");
        frasesGanadoras.add("Que jugador!");frasesGanadoras.add("Buena máquina!");
        frasesGanadoras.add("Fuaa que talento!");frasesGanadoras.add("De que planeta viniste?");
        frasesGanadoras.add("Te admiro, " + username);frasesGanadoras.add("Atr perro!");
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pasaste_de_nivel);
        TextView fraseGanadora = dialog.findViewById(R.id.frase_ganadora);
        TextView dialogPts = dialog.findViewById(R.id.puntos_ganados_dialog);
        TextView dialogPlata = dialog.findViewById(R.id.plata_ganada_dialog);
        fraseGanadora.setText(frasesGanadoras.get(rand.nextInt(frasesGanadoras.size())));
        dialogPts.setText("+" + numeroPuntos + " pts.");
        dialogPlata.setText("+" + numeroDeMonedasASumar + " pe");
        Button botonDialog = dialog.findViewById(R.id.boton_siguiente_dialog_pasar_nivel);
        botonDialog.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                funcionEnCasoDeQueLaPalabraSeaCorrecta();
                sumarUnNivel();
                appDatabase.userDao().updatePlata(appDatabase.userDao().getUser().getPlata() + numeroDeMonedasASumar);
            }
        });
        botonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcionEnCasoDeQueLaPalabraSeaCorrecta();
                sumarUnNivel();
                appDatabase.userDao().updatePlata(appDatabase.userDao().getUser().getPlata() + numeroDeMonedasASumar);
            }
        });

        dialog.show();
    }

    // Agrega pista a la palabra en la base de datos
    private void agregarPistaApalabra(String indiceLetraHinteada){
        Palabra palabra = appDatabase.palabraDao().getPalabraByName(palabraRandom.getNombrePalabra());
        pistas = pistas + indiceLetraHinteada;
        String pistasAEnviar = pistas;
        appDatabase.palabraDao().addPistaDePalabra(pistasAEnviar, palabra.getNombrePalabra());
    }

    // Se fija si hay pistas y las pone donde van
    private void chequearSiHayPistaYPonerla(){
        String pistasUtilizadas = palabraRandom.getPistasUtilizadas();
        if (pistasUtilizadas != null){
            for (int i = 0; i < pistasUtilizadas.length(); i++) {
                String string = String.valueOf(pistasUtilizadas.charAt(i));
                Integer textviewId = Integer.parseInt(string);
                TextView textView = findViewById(textviewId);
                textView.setTextColor(Color.parseColor("#FFC107"));
                textView.setText(String.valueOf(palabraRandom.getNombrePalabra().charAt(Integer.valueOf(string))).toUpperCase());
            }
        }
    }

    // Se fija si en la palabra que esta el usuario hay pistas y pone el atributo pistas con las mismas
    private void setPistaIfExist(){
        if (palabraRandom.getPistasUtilizadas() != null){
            pistas = palabraRandom.getPistasUtilizadas();
        } else {
            pistas = "";
        }
    }

    //Te devuelve cuantas letras hay actualmente
    public Integer getNumberOfCurrentWords(){
        Integer totalLetters = 0;
        for (int i = 0; i < word.length(); i++) {
            TextView textView = findViewById(i);
            totalLetters = totalLetters + 1;
        }
        return totalLetters;
    }

    // Saca el Textview de mensaje de error a los 3 segundos y medio
    private void deleteErrorText(){
        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        errorTextview.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, 3500);
    }

    // Pone el Textview de mensaje de error
    private void setTextviewError(){
        errorTextview.setText("Dale " + username + ", vos podes!");
        errorTextview.setVisibility(View.VISIBLE);
    }


    // Llena la base de datos interna con todas las palabras. Esto deberia ser un Json en el futuro.
    private void populateDatabaseWithArray() {
//        if (appDatabase.palabraDao().getAllPalabras(false).size() < 1) {
            List<Palabra> palabraList = new ArrayList<>();
            palabraList.add(new Palabra("atr"));palabraList.add(new Palabra("gato"));
            palabraList.add(new Palabra("bala"));palabraList.add(new Palabra("potro"));
            palabraList.add(new Palabra("coca"));palabraList.add(new Palabra("rifle"));
            palabraList.add(new Palabra("canas"));palabraList.add(new Palabra("tano"));
            palabraList.add(new Palabra("humo"));palabraList.add(new Palabra("flor"));
            palabraList.add(new Palabra("skere"));palabraList.add(new Palabra("copa"));
            palabraList.add(new Palabra("mina"));palabraList.add(new Palabra("mole"));
            palabraList.add(new Palabra("dios"));palabraList.add(new Palabra("masa"));
            palabraList.add(new Palabra("patón"));palabraList.add(new Palabra("chino"));
            palabraList.add(new Palabra("piscis"));palabraList.add(new Palabra("gaucho"));
            palabraList.add(new Palabra("tanque"));palabraList.add(new Palabra("chueco"));
            palabraList.add(new Palabra("bocha"));palabraList.add(new Palabra("escabio"));
            palabraList.add(new Palabra("alfajor"));palabraList.add(new Palabra("achuras"));
            palabraList.add(new Palabra("boliche"));palabraList.add(new Palabra("romano"));
            palabraList.add(new Palabra("jurado"));palabraList.add(new Palabra("burrito"));
            palabraList.add(new Palabra("piquete"));palabraList.add(new Palabra("vegano"));
            palabraList.add(new Palabra("corneta"));palabraList.add(new Palabra("cornudo"));
            palabraList.add(new Palabra("casamiento"));palabraList.add(new Palabra("emperador"));
            palabraList.add(new Palabra("tucumán"));palabraList.add(new Palabra("sarmiento"));
            palabraList.add(new Palabra("pingüino"));palabraList.add(new Palabra("mochila"));
            palabraList.add(new Palabra("wachiturro"));palabraList.add(new Palabra("clásico"));
            palabraList.add(new Palabra("taladro"));palabraList.add(new Palabra("karina"));
            palabraList.add(new Palabra("reja"));palabraList.add(new Palabra("huracán"));
            palabraList.add(new Palabra("varón"));palabraList.add(new Palabra("salta"));
            palabraList.add(new Palabra("sapo"));palabraList.add(new Palabra("coco"));
            palabraList.add(new Palabra("toto"));palabraList.add(new Palabra("bananero"));
            palabraList.add(new Palabra("ogro"));palabraList.add(new Palabra("baranda"));
            palabraList.add(new Palabra("chivo"));palabraList.add(new Palabra("duro"));
            palabraList.add(new Palabra("tuca"));palabraList.add(new Palabra("calafate"));
            palabraList.add(new Palabra("gigolo"));palabraList.add(new Palabra("faso"));
            palabraList.add(new Palabra("polo"));palabraList.add(new Palabra("mangos"));
            palabraList.add(new Palabra("resaca"));palabraList.add(new Palabra("inunda"));
            palabraList.add(new Palabra("princesa"));palabraList.add(new Palabra("bife"));
            palabraList.add(new Palabra("cuis"));palabraList.add(new Palabra("millonario"));
            palabraList.add(new Palabra("loca"));palabraList.add(new Palabra("gol"));
            palabraList.add(new Palabra("palmeras"));palabraList.add(new Palabra("leo"));
            palabraList.add(new Palabra("bambi"));palabraList.add(new Palabra("descenso"));
            palabraList.add(new Palabra("pucho"));palabraList.add(new Palabra("egresados"));
            palabraList.add(new Palabra("cheto"));palabraList.add(new Palabra("diva"));
            palabraList.add(new Palabra("uruguay"));palabraList.add(new Palabra("diez"));
            palabraList.add(new Palabra("conductor"));palabraList.add(new Palabra("lucha"));
            palabraList.add(new Palabra("china"));palabraList.add(new Palabra("palermo"));
            palabraList.add(new Palabra("grasa"));palabraList.add(new Palabra("pato"));
            palabraList.add(new Palabra("camionero"));palabraList.add(new Palabra("mezcla"));
            palabraList.add(new Palabra("calamar"));palabraList.add(new Palabra("mortero"));
            palabraList.add(new Palabra("lugano"));palabraList.add(new Palabra("rocío"));
            palabraList.add(new Palabra("oliva"));palabraList.add(new Palabra("tecla"));
            palabraList.add(new Palabra("segundo"));palabraList.add(new Palabra("campeón"));
            palabraList.add(new Palabra("doctor"));palabraList.add(new Palabra("lio"));
            palabraList.add(new Palabra("facturas"));palabraList.add(new Palabra("canosa"));
            palabraList.add(new Palabra("vacío"));palabraList.add(new Palabra("caño"));
            for (Palabra palabra : palabraList) {
                try {
                    appDatabase.palabraDao().insertPalabra(palabra);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//        }
    }
}
