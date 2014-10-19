package com.blast;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.blast.apis.Blast;
import com.blast.apis.Debug;
import com.blast.apis.twitter;

import java.util.ArrayList;

import twitter4j.Twitter;


public class MainActivity extends Activity {
    //is there a better way to remember who is selected?
    private ArrayList<Person> personsToProcess;
//l
    twitter t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = new twitter(this);

        //begin relevant

        //list of people to display and know about for contacting
        final ArrayList<Person> people = Debug.people();//new ArrayList<Person>();
/*
        //Temp... debug
        ArrayList<Contact> contacts1 = new ArrayList<Contact>();
        contacts1.add(new Contact("SMS", "user101"));

        people.add(new Person("Faggot Master", contacts1)); //people in list are in same order as in grid
        people.add(new Person("Bane?", contacts1)); //has identical contacts!!! its whatev tho
        people.add(new Person("James?", contacts1));
        people.add(new Person("CIA?", contacts1));
        people.add(new Person("4u", contacts1));
        //end temp debug
*/
        //setup gridview
        GridView gridview = (GridView) findViewById(R.id.gridview);
      //  gridview.setAdapter(new PersonAdapter(people));
        gridview.setAdapter(new PersonAdapter(people));

        //toggle selection of people implementation
        personsToProcess = new ArrayList<Person>();

        //listener
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(personsToProcess.contains(people.get(position))) {
                    personsToProcess.remove(people.get(position));
                    v.setBackgroundColor(Color.alpha(255));
                    //Toast.makeText(MainActivity.this, position + " removed from personsToProcess; duplicate entry.", Toast.LENGTH_SHORT).show(); //debug
                }
                else {
                    personsToProcess.add(people.get(position));
                    v.setBackgroundColor(Color.YELLOW);
                    //Toast.makeText(MainActivity.this, position + " added to personsToProcess.", Toast.LENGTH_SHORT).show(); //debug
                }
            }
        });
    }

    public void onBlastPress(View view){
         //debug

        //Logic for BLAST! button

        //Blast(personsToProcess);
      //  Blast blast = new Blast();
       // blast.update();
        for(Person p: personsToProcess){
            for(Contact c: p.getContacts()){
                if(c.getType().equalsIgnoreCase("twitter")) {
                    t.sendTweet("@" + c.getName(), this);
                    Toast.makeText(this, "TWEET SENT", Toast.LENGTH_SHORT).show();
                }
            }
        }
        personsToProcess.clear();
        //Toast.makeText(MainActivity.this, "Cleared personsToProcess.", Toast.LENGTH_SHORT).show(); //debug
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
            //holder.icon.setImageResource(R.drawable.sample_1);

            return cell;
        }
    }

    @Override
    protected void onStop(){
        t.logoutFromTwitter();
    }

}
