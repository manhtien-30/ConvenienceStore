package com.example.checkattendance.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.checkattendance.Models.DetailMessage;
import com.example.checkattendance.R;

import java.util.ArrayList;

public class NotificationAdapter extends BaseAdapter {
    private ArrayList<DetailMessage> arrayList = new ArrayList<>();

    public NotificationAdapter(ArrayList<DetailMessage> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txt_message, txt_date, txt_time;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewproduct;//View hien thi san pham tu arrayList
        NotificationAdapter.ViewHolder viewHolder;//lưu thông tin ánh xạ
        if (view == null) {
            //viewproduct= View.inflate(viewGroup.getContext() , R.layout.actionn jj njn , null);
            viewproduct = View.inflate(viewGroup.getContext(), R.layout.activity_notification_cell, null);
            viewHolder = new NotificationAdapter.ViewHolder();
            viewHolder.txt_message = viewproduct.findViewById(R.id.message);
            viewHolder.txt_date = viewproduct.findViewById(R.id.textdate);
            viewHolder.txt_time = viewproduct.findViewById(R.id.texttime);
            viewproduct.setTag(viewHolder); // Tạo tag để nắm viewholder mà lưu trữ các thông tin ánh xạ để dùng cho lần sau.

        } else {
            viewproduct = view;
            viewHolder = (NotificationAdapter.ViewHolder) viewproduct.getTag();
        }
        DetailMessage product = (DetailMessage) getItem(position);
        viewHolder.txt_message.setText(product.getMessage());
        viewHolder.txt_date.setText(product.getDate());
        viewHolder.txt_time.setText(product.getTime());
        return viewproduct;
    }
}
