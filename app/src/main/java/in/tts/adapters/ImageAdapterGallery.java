package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.ImageOcrActivity;
import in.tts.utils.CommonMethod;

public class ImageAdapterGallery extends RecyclerView.Adapter<ImageAdapterGallery.ViewHolder> {
    private Context context;
    private ArrayList<String> images;

    public ImageAdapterGallery(Context activity, ArrayList<String> imagesGallery) {
        context = activity;
        images = imagesGallery;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
//            Log.d("TAG", " IMAGES-G " + i + ":" + images.get(i).trim().replaceAll("\\s", "%20") + ":" + images.get(i));
            Picasso.get()
                    .load("file://" + images.get(i).trim().replaceAll("\\s", "%20"))
                    .resize(220, 220)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(viewHolder.picturesView);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView picturesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picturesView = itemView.findViewById(R.id.ivItem);
            picturesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(new Intent(context, ImageOcrActivity.class).putExtra("PATH", images.get(getAdapterPosition())));
                        CommonMethod.toReleaseMemory();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
        }
    }
}