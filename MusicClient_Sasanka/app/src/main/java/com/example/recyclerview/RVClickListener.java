package com.example.recyclerview;

import android.view.View;

import java.io.IOException;

public interface RVClickListener {

    public void onClick(View view, int position) throws IOException;/* Interface for onclick*/
}
