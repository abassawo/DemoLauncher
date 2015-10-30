package abassawo.c4q.nyc.demolauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by c4q-Abass on 10/30/15.
 */

public class LauncherFragment extends Fragment {
@Bind(R.id.fragment_launcher_recyclerview_xml)
    RecyclerView mRecyclerView;
    private static PackageManager pm;

    private static final String TAG = "LauncherFragment";

    public static LauncherFragment newInstance(){
        return new LauncherFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        setupAdapter();
        return view;
    }

    private void setupAdapter(){
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(launcherIntent, 0);

        Log.i(TAG, "Found " + activities.size());

        Comparator<ResolveInfo>resolveInfoComparator = new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.loadLabel(pm).toString(), rhs.loadLabel(pm).toString());
            }
        };
        Collections.sort(activities, resolveInfoComparator);
        mRecyclerView.setAdapter(new ActivityListAdapter(activities));
    }

    private class ActivityListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ResolveInfo mResolveInfo;
        private TextView mTextView;

        public ActivityListHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo){
            mResolveInfo = resolveInfo;
            String appName = mResolveInfo.loadLabel(pm).toString();
            mTextView.setText(appName);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            //Unable to find explicit activity class this way.
            //Intent intent = new Intent(getActivity().getApplicationContext(), mResolveInfo.getClass());

            //This works though!
            Intent i = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
            startActivity(i);
        }
    }

    private class ActivityListAdapter extends RecyclerView.Adapter<ActivityListHolder>{
        private final List<ResolveInfo> mActivities;

        public ActivityListAdapter(List<ResolveInfo> mActivities){
            this.mActivities = mActivities;
        }
        @Override
        public ActivityListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ActivityListHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityListHolder holder, int position) {
            holder.bindActivity(mActivities.get(position));
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }
}
