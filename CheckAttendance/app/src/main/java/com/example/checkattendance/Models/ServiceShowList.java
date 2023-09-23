package com.example.checkattendance.Models;

import android.view.View;
import android.widget.ListView;

public class ServiceShowList {
    private ListView listView;

    public ServiceShowList(ListView listView) {
        this.listView = listView;
    }

    public void receive(View view) {
        listView.setEmptyView(view);
    }
}
