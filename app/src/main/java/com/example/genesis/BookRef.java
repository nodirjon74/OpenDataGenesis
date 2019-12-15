package com.example.genesis;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;

public class BookRef {
    private static ArrayList<BookRef> bookList = new ArrayList<>(); //currently dead, but likely will help with history
    private List<BarcodeFormat> allowedFormats = Arrays.asList(BarcodeFormat.ISBN10, BarcodeFormat.ISBN13);

    private String id;
    private BarcodeFormat format;
    private boolean opened;
    private Main3Activity parent;


    public BookRef(String id, BarcodeFormat format, boolean opened, Main3Activity parent) throws IllegalArgumentException {


        if (!isAllowed(format)) {
            throw new IllegalArgumentException("Format not supported");
        }
        this.id = id;
        this.format = format;
        this.opened = opened;
        this.parent = parent;
    }




    public boolean isAllowed(BarcodeFormat b) {
        return this.allowedFormats.contains(b);
    }

    public String getId() {
        return id;
    }

    public boolean isOpened() {
        return opened;
    }

    public BarcodeFormat getFormat() {
        return format;
    }

    public static void addToList(BookRef b) {
        bookList.add(b);
    }

    //TODO: add more libraries, possibly ways of handling other types of barcodes (search by UPC?)
    public void searchBook() {
        opened = true;
        String url = "http://libgen.is/search.php?req=" + id +
                "&lg_topic=libgen&open=0&view=simple&res=25&phrase=1&column=identifier";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        parent.fire(browserIntent);

    }


}
