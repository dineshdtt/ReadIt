package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfShowingActivity;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetPdfFiles;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private LinearLayout mLl;

    public PdfListAdapter(Context _context, ArrayList<String> _list) {
        context = _context;
        list = _list;
    }

    @NonNull
    @Override
    public PdfListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_pdf, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PdfListAdapter.ViewHolder viewHolder, int i) {
        try {
            if (list.get(i).trim().length() > 0 && !list.get(i).trim().equals("\\")) {
                File file = new File(list.get(i).trim().replaceAll("%20", " "));
                viewHolder.mtv.setText(file.getName());
                viewHolder.mtv1.setText(CommonMethod.getFileSize(file));
                viewHolder.mtv2.setText(String.valueOf(new PdfReader(file.getAbsolutePath()).getNumberOfPages()) + " Pages");
            } else {
                toRemove(i);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toRemove(final int i) {
        try {
            mLl.post(new Runnable() {
                @Override
                public void run() {
                    list.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();
                    if (list.size() == 0) {
                        ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), context);
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mtv, mtv1, mtv2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                mtv = itemView.findViewById(R.id.tv_name);
                mtv1 = itemView.findViewById(R.id.tv_duration);
                mtv2 = itemView.findViewById(R.id.tv_remaining);

                mLl = itemView.findViewById(R.id.mLlPdfListItem);

                mLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            CommonMethod.toCallLoader(context, "Loading...");
                            Intent intent = new Intent(context, PdfShowingActivity.class);
                            intent.putExtra("file", list.get(getAdapterPosition()));
                            context.startActivity(intent);
                            CommonMethod.toCloseLoader();
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    }
                });
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }
    }
}