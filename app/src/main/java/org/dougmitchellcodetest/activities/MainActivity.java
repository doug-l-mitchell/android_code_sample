package org.dougmitchellcodetest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dougmitchellcodetest.R;
import org.dougmitchellcodetest.ThisApp;
import org.dougmitchellcodetest.common.PeopleTouchHelper;
import org.dougmitchellcodetest.common.Events;
import org.dougmitchellcodetest.model.Person;
import org.dougmitchellcodetest.services.PersonRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton addPersonButton;
    @BindView(R.id.list) RecyclerView recyclerView;

    @Inject PersonRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ThisApp)getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        PeopleAdapter peopleAdapter = new PeopleAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(peopleAdapter);

        ItemTouchHelper.Callback callback = new PeopleTouchHelper(this, peopleAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        Events.click(addPersonButton)
            .subscribe(new Action1<View>() {
            @Override public void call(View view) {
                Intent intent = new Intent(getApplicationContext(), PersonDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    public class PeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Person> people;
        private Subscription subscription;

        public PeopleAdapter() {
            this.people = repository.getAll();

            subscription = repository.updatesObservable()
                    .throttleLast(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1() {
                        @Override public void call(Object o) {
                            people = repository.getAll();
                            notifyDataSetChanged();
                        }
                    });
        }

        @Override protected void finalize() throws Throwable {
            super.finalize();
            if(!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch(viewType) {
                case VH_EMPTY:
                    return new EmptyViewHolder(inflater.inflate(R.layout.no_people_item, parent, false));
                case VH_PERSON:
                    return new PersonViewHolder(inflater.inflate(R.layout.person_item, parent, false));
            }
            return null;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof PersonViewHolder) {
                Person person = people.get(position);
                ((PersonViewHolder)holder).setPerson(person);
            }
        }

        private static final int VH_EMPTY = 0;
        private static final int VH_PERSON = 1;

        @Override public int getItemViewType(int position) {
            if(people.size() == 0)
                return VH_EMPTY;
            return VH_PERSON;
        }

        @Override public int getItemCount() {
            return Math.max(1, people.size());
        }

        public void remove(int position) {
            Person person = people.get(position);
            people.remove(position);
            repository.remove(person);
            notifyItemRemoved(position);
        }


        class EmptyViewHolder extends RecyclerView.ViewHolder {

            public EmptyViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private Person person;
            @BindView(R.id.name) TextView name;

            public PersonViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            public void setPerson(Person person) {
                this.person = person;
                name.setText(String.format("%s %s", person.getFirstName(), person.getLastName()).replace("null", ""));
            }

            @Override public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PersonDetailActivity.class);
                i.putExtra("id", person.getId());
                startActivity(i);
            }
        }
    }
}
