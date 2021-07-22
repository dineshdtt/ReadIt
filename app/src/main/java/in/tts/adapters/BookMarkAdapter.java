package in.tts.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.BrowserActivity;
import in.tts.model.PrefManager;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;

    public BookMarkAdapter(Context _context, ArrayList<String> _list) {
        context = _context;
        list = _list;
    }

    @NonNull
    @Override
    public BookMarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.bookmark_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.ViewHolder viewHolder, int position) {
        try {
            viewHolder.tv_title.setText(list.get(position));
            viewHolder.tv_link.setText(list.get(position));
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_link;
        RelativeLayout rl;

        public ViewHolder(@NonNull View view) {
            super(view);
            try {
                rl = view.findViewById(R.id.rlbm);
                tv_title = view.findViewById(R.id.title);
                tv_link = view.findViewById(R.id.link);
                tv_link.setVisibility(View.GONE);

                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent in = new Intent(context, BrowserActivity.class);
                            in.putExtra("url", list.get(getAdapterPosition()));
                            context.startActivity(in);
                        } catch (Exception | Error e) {
                            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e); FirebaseCrash.report(e);
                        }
                    }
                });

                rl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteBookmark(getAdapterPosition());
                        return true;
                    }
                });
            } catch (Exception | Error e) {
                e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e); FirebaseCrash.report(e);
            }
        }
    }

    private void deleteBookmark(final int position) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("DELETE")
                    .setMessage("Confirm that you want to delete this bookmark?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                list.remove(position);
                                new PrefManager(context).setSearchResult(list);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(position, list.size());
                                dialogInterface.dismiss();
                            } catch (Exception | Error e) {
                                e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e); FirebaseCrash.report(e);
                            }
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }
}