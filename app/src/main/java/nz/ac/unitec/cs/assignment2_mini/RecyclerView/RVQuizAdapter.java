package nz.ac.unitec.cs.assignment2_mini.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import nz.ac.unitec.cs.assignment2_mini.R;

public class RVQuizAdapter extends RecyclerView.Adapter<RVQuizAdapter.RVQuizViewHoder>{

    ArrayList list;

    public RVQuizAdapter(ArrayList list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RVQuizViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament, parent, false);
        RVQuizAdapter.RVQuizViewHoder viewHoder = new RVQuizAdapter.RVQuizViewHoder(view);
        return viewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull RVQuizViewHoder holder, int position) {

        HashMap<String, String> quiz = (HashMap<String, String>) list.get(position);
        String startDate = quiz.get("start_date");
        String[] startDateDiv = startDate.split("-");
        String endDate = quiz.get("end_date");
        String[] endDateDiv = endDate.split("-");
        Calendar today = Calendar.getInstance();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Integer.parseInt(startDateDiv[2]) , Integer.parseInt(startDateDiv[1])-1 , Integer.parseInt(startDateDiv[0]));
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(Integer.parseInt(endDateDiv[2]) , Integer.parseInt(endDateDiv[1])-1 , Integer.parseInt(endDateDiv[0]));

        try {
            holder.tvTerm.setText(startDate);
            holder.tvProgress.setText(endDate);
            holder.tvTitle.setText(quiz.get("name"));
            holder.tvStatus.setText(quiz.get("name"));
            holder.tvKey.setText(quiz.get("key"));
            if(today.after(calendarEnd)) {
                holder.tvStatus.setText("Finished");
                holder.tvStatus.setBackgroundResource(R.drawable.btn_finished);
                holder.rvCardTournament.setBackgroundResource(R.drawable.border_round_disable);
            } else if(today.before(calendarStart)) {
                holder.tvStatus.setText("Up coming");
                holder.tvStatus.setBackgroundResource(R.drawable.btn_up_coming);
                holder.rvCardTournament.setBackgroundResource(R.drawable.border_round);
            } else {
                holder.tvStatus.setText("On going");
                holder.tvStatus.setBackgroundResource(R.drawable.btn_on_going);
                holder.rvCardTournament.setBackgroundResource(R.drawable.border_round);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RVQuizViewHoder extends RecyclerView.ViewHolder {

        FrameLayout rvCardTournament;
        TextView tvTitle;
        TextView tvTerm;
        TextView tvProgress;
        TextView tvStatus;
        TextView tvKey;

        public RVQuizViewHoder(@NonNull View itemView) {
            super(itemView);

            rvCardTournament = itemView.findViewById(R.id.rv_card_tournament);
            tvTitle = itemView.findViewById(R.id.tv_rv_item_title);
            tvTerm = itemView.findViewById(R.id.tv_rv_item_term);
            tvProgress = itemView.findViewById(R.id.tv_rv_item_progress);
            tvStatus = itemView.findViewById(R.id.tv_rv_item_status);
            tvKey = itemView.findViewById(R.id.tv_rv_item_key);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myRVClickListener != null) {
                        myRVClickListener.itemClickListener(tvKey.getText().toString());
                    }
                }
            });

        }
    }

    public interface RVClickListener {
        void itemClickListener(String quizListKey);
    }

    RVClickListener myRVClickListener;

    public void setMyRVClickListener(RVClickListener myRVClickListener){
        this.myRVClickListener = myRVClickListener;
    }
}
