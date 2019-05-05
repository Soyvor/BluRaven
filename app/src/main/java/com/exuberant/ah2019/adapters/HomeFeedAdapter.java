package com.exuberant.ah2019.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.models.Report;
import com.exuberant.ah2019.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.HomeFeedViewHolder> {

    List<Report> reportList;
    User user;

    public HomeFeedAdapter(List<Report> reportList, User user) {
        this.reportList = reportList;
        this.user = user;
    }

    @NonNull
    @Override
    public HomeFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_home_feed, parent, false);
        return new HomeFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFeedViewHolder holder, int position) {
        Report report = reportList.get(position);
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
        holder.authorName.setText(report.getAuthorName());
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
    public int getItemCount() {
        return reportList.size();
    }

    class HomeFeedViewHolder extends RecyclerView.ViewHolder {

        TextView reportDescription, downVoteCount, upVoteCount, reportType, authorName, date, resolvedTextView, unresolvedTextView;
        FloatingActionButton upVoteButton, downVoteButton;

        public HomeFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            reportDescription = itemView.findViewById(R.id.tv_report_title);
            downVoteCount = itemView.findViewById(R.id.tv_downvote_count);
            upVoteCount = itemView.findViewById(R.id.tv_upvote_count);
            reportType = itemView.findViewById(R.id.tv_report_type);
            authorName = itemView.findViewById(R.id.tv_author_name);
            date = itemView.findViewById(R.id.tv_report_date);
            resolvedTextView = itemView.findViewById(R.id.tv_resolved);
            unresolvedTextView = itemView.findViewById(R.id.tv_unresolved);
            upVoteButton = itemView.findViewById(R.id.btn_upvote);
            downVoteButton = itemView.findViewById(R.id.btn_downvote);
        }
    }

}
