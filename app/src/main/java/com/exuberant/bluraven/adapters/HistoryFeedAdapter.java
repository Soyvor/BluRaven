package com.exuberant.bluraven.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exuberant.bluraven.R;
import com.exuberant.bluraven.models.Report;
import com.exuberant.bluraven.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HistoryFeedAdapter extends RecyclerView.Adapter<HistoryFeedAdapter.HistoryFeedViewHolder> {

    List<String> reportList;
    DatabaseReference mReportsReference;
    User user;


    public HistoryFeedAdapter(List<String> reportList, DatabaseReference mReportsReference, User user) {
        this.reportList = reportList;
        this.mReportsReference = mReportsReference;
        this.user = user;
    }

    @NonNull
    @Override
    public HistoryFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_history_feed, parent, false);
        return new HistoryFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryFeedViewHolder holder, int position) {
        String reportId = reportList.get(position);
        mReportsReference.child(reportId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);
                holder.reportDescription.setText(report.getDescription());
                List<String> upVotes = report.getUpVotes();
                List<String> downVotes = report.getDownVotes();
                if (upVotes == null || upVotes.size() == 0){
                    holder.upVoteCount.setText("0");
                } else {
                    holder.upVoteCount.setText(String.valueOf(upVotes.size()));
                }
                if (downVotes == null || downVotes.size() == 0){
                    holder.downVoteCount.setText("0");
                } else {
                    holder.downVoteCount.setText(String.valueOf(downVotes.size()));
                }
                holder.authorName.setText(user.getDisplayName());
                holder.reportType.setText(report.getReportType());
                holder.date.setText(report.getDate());

                if (report.isStatus()){
                    holder.unresolvedTextView.setVisibility(View.GONE);
                    holder.resolvedTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.resolvedTextView.setVisibility(View.GONE);
                    holder.unresolvedTextView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class HistoryFeedViewHolder extends RecyclerView.ViewHolder{

        TextView reportDescription, downVoteCount, upVoteCount, reportType, authorName, date, resolvedTextView, unresolvedTextView;

        HistoryFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            reportDescription = itemView.findViewById(R.id.tv_report_title);
            downVoteCount = itemView.findViewById(R.id.tv_downvote_count);
            upVoteCount = itemView.findViewById(R.id.tv_upvote_count);
            reportType = itemView.findViewById(R.id.tv_report_type);
            authorName = itemView.findViewById(R.id.tv_author_name);
            date = itemView.findViewById(R.id.tv_report_date);
            resolvedTextView = itemView.findViewById(R.id.tv_resolved);
            unresolvedTextView = itemView.findViewById(R.id.tv_unresolved);
        }
    }

}
