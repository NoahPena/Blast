package com.blast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {
    //is there a better way to remember who is selected?
    private ArrayList<Person> personsToProcess;
//l
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //begin relevant

        //list of people to display and know about for contacting
        final ArrayList<Person> people = new ArrayList<Person>();

        //Temp...
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("SMS", "user101"));
        people.add(new Person("Faggot Master", contacts)); //people in list are in same order as in grid
        //end temp

        //setup gridview
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new PersonAdapter(people));

        //toggle selection of people implementation
        personsToProcess = new ArrayList<Person>();

        //listener
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(personsToProcess.contains(people.get(position))) {
                    personsToProcess.remove(people.get(position));
                    Toast.makeText(MainActivity.this, position + " removed from personsToProcess; duplicate entry.", Toast.LENGTH_SHORT).show(); //debug
                }
                else {
                    personsToProcess.add(people.get(position));
                    Toast.makeText(MainActivity.this, position + " added to personsToProcess.", Toast.LENGTH_SHORT).show(); //debug
                }
            }
        });
    }

    public void onBlastPress(View view){
        //Logic for BLAST! button

        //Blast(personsToProcess);

        personsToProcess.clear();
        Toast.makeText(MainActivity.this, "Cleared personsToProcess.", Toast.LENGTH_SHORT).show(); //debug
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //internalized adapter class
    class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter(ArrayList<Person> people) {
            super(MainActivity.this, R.layout.person_grid_cell, R.id.person_caption, people); //person_caption will now be named by the ArrayAdapter
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View cell=super.getView(position, convertView, parent);

            ViewHolder holder = (ViewHolder)cell.getTag();
            if (holder==null) {
                holder=new ViewHolder(cell);
                cell.setTag(holder);
            }
            if(position < 2)//test
                holder.icon.setImageResource(R.drawable.sample_1);
            return cell;
        }
    }
}
