package com.example.database_test;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
        implements SubjectDialogFragment.OnSubjectEnteredListener {

    private StudyDatabase mStudyDb;
    private SubjectAdapter mSubjectAdapter;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private int[] mSubjectColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subject);

        mSubjectColors = getResources().getIntArray(R.array.subjectColors);

        // Singleton
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());

        mRecyclerView = findViewById(R.id.subjectRecyclerView);

        // Create 2 grid layout columns
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Shows the available subjects
        mSubjectAdapter = new SubjectAdapter(loadSubjects());
        mRecyclerView.setAdapter(mSubjectAdapter);
    }

    @Override
    public void onSubjectEntered(String subject) {

        if (subject.length() > 0) {
            Subject sub = new Subject(subject);
            if (mStudyDb.addSubject(sub)) {
                mSubjectAdapter.addSubject(sub);
                //mImageView.setImageResource(R.drawable.subbacknew);
                Toast.makeText(this, "Added " + subject, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.subject_exists, subject);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void addSubjectClick(View view) {
        // Prompt user to type new subject
        FragmentManager manager = getFragmentManager();
        SubjectDialogFragment dialog = new SubjectDialogFragment();
        dialog.show(manager, "subjectDialog");
    }

    private List<Subject> loadSubjects() {
        return mStudyDb.getSubjects(StudyDatabase.SubjectSortOrder.UPDATE_DESC);
    }


    private Subject mSelectedSubject;
    private int mSelectedSubjectPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    private class SubjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Subject mSubject;
        private TextView mTextView;

        public SubjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.subjectTextView);
            mImageView = itemView.findViewById(R.id.subjectImageView);
            itemView.setOnLongClickListener(this);
        }


        public void bind(Subject subject, int position) {
            mSubject = subject;
            mTextView.setText(subject.getText());

            if (mSelectedSubjectPosition == position) {

                mImageView.setImageResource(R.drawable.subbackred);
            } else {

            }
        }

        @Override
        public void onClick(View view) {
            // Start QuestionActivity, indicating what subject was clicked
            Intent intent = new Intent(SubjectActivity.this, QuestionActivity.class);
            intent.putExtra(QuestionActivity.EXTRA_SUBJECT, mSubject.getText());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }

            mSelectedSubject = mSubject;
            mSelectedSubjectPosition = getAdapterPosition();

            // Re-bind the selected item
            mSubjectAdapter.notifyItemChanged(mSelectedSubjectPosition);

            mActionMode = SubjectActivity.this.startActionMode(mActionModeCallback);

            return true;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Provide context menu for CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Process action item selection
            switch (item.getItemId()) {
                case R.id.delete:
                    // Delete from the database and remove from the RecyclerView
                    mStudyDb.deleteSubject(mSelectedSubject);
                    mSubjectAdapter.removeSubject(mSelectedSubject);

                    // Close the CAB
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mSubjectAdapter.notifyItemChanged(mSelectedSubjectPosition);
            mSelectedSubjectPosition = RecyclerView.NO_POSITION;
        }
    };


    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {

        private List<Subject> mSubjectList;

        public SubjectAdapter(List<Subject> subjects) {
            mSubjectList = subjects;
        }

        @Override
        public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new SubjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SubjectHolder holder, int position){
            holder.bind(mSubjectList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mSubjectList.size();
        }

        public void addSubject(Subject subject) {
            // Add the new subject at the beginning of the list
            mSubjectList.add(0, subject);

            // Notify the adapter that item was added to the beginning of the list
            notifyItemInserted(0);

            // Scroll to the top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeSubject(Subject subject) {
            // Find subject in the list
            int index = mSubjectList.indexOf(subject);
            if (index >= 0) {
                // Remove the subject
                mSubjectList.remove(index);

                // Notify adapter of subject removal
                notifyItemRemoved(index);
            }
        }
    }


}
