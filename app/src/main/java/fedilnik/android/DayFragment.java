package fedilnik.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import fedilnik.android.data.DataStoreCallback;
import fedilnik.android.data.DataStore;
import fedilnik.android.data.Status;

public class DayFragment extends Fragment implements DataStoreCallback {
    private static final String DAY_ARG = "day_arg";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private String dataDelegateUUID;
    private MealAdapter adapter;

    public static DayFragment newInstance(int day) {
        Bundle args = new Bundle();
        args.putInt(DAY_ARG, day);

        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataDelegateUUID = UUID.randomUUID().toString();
        DataStore.getInstance().addCallback(dataDelegateUUID, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        refreshLayout = view.findViewById(R.id.day_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataStore.getInstance().downloadData();
            }
        });

        adapter = new MealAdapter(getContext());

        recyclerView = view.findViewById(R.id.day_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        dataDidChange(Status.SUCCESS);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DataStore.getInstance().removeCallback(dataDelegateUUID);
    }

    @Override
    public void dataDidChange(Status status) {
        refreshLayout.setRefreshing(false);
        if (status != Status.SUCCESS) return;

        int day = getArguments().getInt(DAY_ARG);
        adapter.meals = DataStore.getInstance().getData(day);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dataDidStartRefreshing() {
        refreshLayout.setRefreshing(true);
    }
}
