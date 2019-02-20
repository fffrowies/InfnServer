package com.ffrowies.infnserver;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ffrowies.infnserver.Adapter.UserAdapter;
import com.ffrowies.infnserver.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvoicesList extends AppCompatActivity {

    RecyclerView recyclerView;

    final int ITEM_LOAD_COUNT = 21;
    int totalItem = 0, lastVisibleItem;
    UserAdapter adapter;
    boolean isLoading = false, isMaxData = false;

    String lastNode = "", lastKey = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemRefresh)
        {
            isMaxData = false;
            lastNode = adapter.getLastItemId();
            adapter.removeLastItem();
            adapter.notifyDataSetChanged();
            getLastKeyFromFirebase();
            getUsers();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_list);

        recyclerView = (RecyclerView) findViewById(R.id.userRecyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Firebase Recycler Pagination");

        getLastKeyFromFirebase();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new UserAdapter(this);
        recyclerView.setAdapter(adapter);

        getUsers();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItem = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItem <= (lastVisibleItem + ITEM_LOAD_COUNT))
                {
                    getUsers();
                    isLoading = true;
                }
            }
        });

    }

    private void getUsers() {
        if (!isMaxData)
        {
            Query query;
            if (TextUtils.isEmpty(lastNode))
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .orderByKey()
                        .limitToFirst(ITEM_LOAD_COUNT);
            else
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .orderByKey()
                        .startAt(lastNode)
                        .limitToFirst(ITEM_LOAD_COUNT);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren())
                    {
                        List<User> newUsers = new ArrayList<>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                        {
                            newUsers.add(userSnapshot.getValue(User.class));
                        }

                        lastNode = newUsers.get(newUsers.size() - 1).getId();

                        if (!lastNode.equals(lastKey))
                            newUsers.remove(newUsers.size() - 1);
                        else
                            lastNode = "end";   //Fix error infinity load final item

                        adapter.addAll(newUsers);
                        isLoading = false;
                    }
                    else
                    {
                        isLoading = false;
                        isMaxData = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
                }
            });
        }
    }

    private void getLastKeyFromFirebase() {
        Query getLastKey = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .orderByKey()
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastKeySnapshot : dataSnapshot.getChildren())
                    lastKey = lastKeySnapshot.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InvoicesList.this, "Cannot get last key", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
