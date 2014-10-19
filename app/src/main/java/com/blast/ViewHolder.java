package com.blast;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class ViewHolder {
    ImageView icon=null;
    TextView caption=null;

    ViewHolder(View cell) {
        this.icon=(ImageView)cell.findViewById(R.id.person_icon);
        this.caption=(TextView)cell.findViewById(R.id.person_caption);
    }

}
