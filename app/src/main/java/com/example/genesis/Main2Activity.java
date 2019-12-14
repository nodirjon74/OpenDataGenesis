package com.example.genesis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String BASE_URL = "http://libgen.is/search.php?req=";
    private ArrayList<Book> bookList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    boolean doubleBackToExitPressedOnce = false;
    private static int REQUEST_CODE=1;
    public ZBarScannerView mScannerView;
    public boolean autosearch = true; //TODO: add switching (new Activity, along with menu of bookrefs)
    public static final int PERMISSION_REQUEST_CAMERA = 1;
    // private ProgressBar progressBar;
    private SearchView searchView;
    private FloatingActionButton fltb;
    private Button button;


    //  private Spinner spn;
    //private Toolbar tlb;



    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);
        super.onCreate(savedInstanceState);
        //   tlb=(Toolbar)findViewById(R.id.activity_scan_barcode);
        // setSupportActionBar(tlb);
        setContentView(R.layout.activity_main);
        //  spn=(Spinner)findViewById(R.id.spinner2);
        fltb=(FloatingActionButton) findViewById(R.id.scanner);
        recyclerView = findViewById(R.id.book_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        booksAdapter = new BooksAdapter(this, bookList);
        recyclerView.setAdapter(booksAdapter);
        //    progressBar = findViewById(R.id.progress_bar_cyclic);
        //  progressBar.setVisibility(View.GONE);

        // ArrayAdapter<CharSequence> myAdapter=ArrayAdapter.createFromResource(this,R.array.array,android.R.layout.simple_spinner_dropdown_item);
        //  myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn.setAdapter(myAdapter);
        searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        fltb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAct();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
//                progressBar.setVisibility(View.VISIBLE);
                findBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void openAct() {
        Intent intent=new Intent(this, Main3Activity.class);
        startActivity(intent);
    }

    // private void setSupportActionBar(Toolbar tlb) {
    // }

    private void findBooks(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bookList.clear();
                    Document doc = Jsoup.connect(BASE_URL + query).get();
                    if (doc == null) return;
                    Elements elements = doc.getElementsByTag("tr");
                    for (Element element : elements) {
                        if ("top".equals(element.attr("valign")) && ("#C6DEFF".equals(element.attr("bgcolor")) || "".equals(element.attr("bgcolor")))) {
                            Elements bookElements = element.children();
                            Book book = new Book();
                            book.setID(bookElements.get(0).text());
                            Elements authors = bookElements.get(1).children();
                            StringBuilder author = new StringBuilder();
                            for (Element authorElements : authors) {
                                author.append(authorElements.text());
                                author.append(", ");
                            }
                            author.setLength(author.length() - 2);
                            book.setAuthor(author.toString());
                            book.setTitle((bookElements.get(2).children()).get(0).text());
                            book.setPublisher(bookElements.get(3).text());
                            book.setYear(bookElements.get(4).text());
                            book.setPages(bookElements.get(5).text());
                            book.setLanguage(bookElements.get(6).text());
                            book.setSize(bookElements.get(7).text());
                            book.setExtension(bookElements.get(8).text());

                            //Replace the old code here with the new one that gets the MD5
                            book.setDownloadLink((bookElements.get(10).children()).get(0).attr("href"));
                            book.setDownloadLink(book.getDownloadLink().substring(book.getDownloadLink().lastIndexOf("=") + 1));
                            bookList.add(book);
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (bookList.size() == 0)
                                Toast.makeText(Main2Activity.this, "Nothing Found.", Toast.LENGTH_SHORT).show();
                            else {
                                updateListView();
                                //  progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateListView() {
        booksAdapter.notifyDataSetChanged();
        Log.v(TAG, bookList.size() + "");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    public void Voice_sound(View view) {
    }
}

