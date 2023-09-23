package com.example.checkattendance.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkattendance.Models.Detail_Profile;
import com.example.checkattendance.Models.ServiceShowList;
import com.example.checkattendance.R;

import java.util.ArrayList;

public class InformationAdapter extends BaseAdapter {
    final ArrayList<Detail_Profile> arrayList;
    private ServiceShowList serviceShowList;

    public InformationAdapter(ArrayList<Detail_Profile> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    private class ViewHolder {
        TextView txt1, txt2;
        ImageView img;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewproduct;//View hien thi san pham tu arrayList
        ViewHolder viewHolder;//lưu thông tin ánh xạ
        if (view == null) {
            //viewproduct= View.inflate(viewGroup.getContext() , R.layout.actionn jj njn , null);
            viewproduct = View.inflate(viewGroup.getContext(), R.layout.detail_profile, null);
            viewHolder = new ViewHolder();
            viewHolder.txt1 = viewproduct.findViewById(R.id.text1);
            viewHolder.txt2 = viewproduct.findViewById(R.id.text2);
            viewHolder.img = viewproduct.findViewById(R.id.image);
            viewproduct.setTag(viewHolder); // Tạo tag để nắm viewholder mà lưu trữ các thông tin ánh xạ để dùng cho lần sau.


        } else {
            viewproduct = view;
            viewHolder = (ViewHolder) viewproduct.getTag();
        }
        Detail_Profile product = (Detail_Profile) getItem(position);
        viewHolder.txt1.setText(product.getName1());
        viewHolder.txt2.setText(product.getName2());
        viewHolder.img.setImageResource(product.getImageView());
        return viewproduct;
    }

    public void transfer(View view) {
        if (view != null)
            serviceShowList.receive(view);
    }
}

