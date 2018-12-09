package mpip.finki.ukim.mk.lab2_1.Adapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;
import mpip.finki.ukim.mk.lab2_1.R;


public class CardItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView txtItemInfo;
    private View parent;

    public CardItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = (ImageView)itemView.findViewById(R.id.imageView);
        this.txtItemInfo = (TextView) itemView.findViewById(R.id.txtItemInfo);
    }

    public void bind(final Movie entity) {
        Glide.with(itemView.getContext())
                .load(entity.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .crossFade()
                .into(getImageView());
        getTxtItemInfo().setText("title: " +entity.getTitle()+" year:"+entity.getYear());

    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTxtItemInfo() {
        return txtItemInfo;
    }

    public void setTxtItemInfo(TextView txtItemInfo) {
        this.txtItemInfo = txtItemInfo;
    }

    public View getParent() {
        return parent;
    }

    public void setParent(View parent) {
        this.parent = parent;
    }
}

