package com.example.work.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.work.myapplication.model.Cat
import com.example.work.myapplication.model.Dog
import com.example.work.myapplication.model.Person
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by work on 2018/5/14.
 */

class KotlinMainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "KotlinExampleActivity"
    }

    private lateinit var rootLayout: LinearLayout
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realm_basic_example)

        rootLayout = findViewById(R.id.container)
        rootLayout.removeAllViews()

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        // Delete all persons
        // Using executeTransaction with a lambda reduces code size and makes it impossible
        // to forget to commit the transaction.
        realm.executeTransaction { realm ->
            realm.deleteAll()
        }

        // These operations are small enough that
        // we can generally safely run them on the UI thread.
        basicCRUD(realm)
        basicQuery(realm)
        basicLinkQuery(realm)

        // More complex operations can be executed on another thread, for example using
        // Anko's doAsync extension method.

        //有异常不抛出，自动终止
        doAsync {
            // Open the default realm. All threads must use its own reference to the realm.
            // Those can not be transferred across threads.
            var info = ""
            // Realm implements the Closable interface, therefore
            // we can make use of Kotlin's built-in extension method 'use' (pun intended).
            Realm.getDefaultInstance().use { realm ->
                info += complexReadWrite(realm)
                info += complexQuery(realm)
            }
            uiThread {
                showStatus(info)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    private fun showStatus(text: String) {
        Log.i(TAG, text)
        val textView = TextView(this)
        textView.text = text
        rootLayout.addView(textView)
    }

    @Suppress("NAME_SHADOWING")
    private fun basicCRUD(realm: Realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...")

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction { realm ->
            // Add a person
            val person = realm.createObject<Person>(0)
            person.name = "Young Person"
            person.age = 14
        }

        // Find the first person (no query conditions) and read a field
        val person = realm.where<Person>().findFirst()!!
        showStatus(person.name + ": " + person.age)

        // Update person in a transaction
        realm.executeTransaction { _ ->
            person.name = "Senior Person"
            person.age = 99
            showStatus(person.name + " got older: " + person.age)
        }
    }

    private fun basicQuery(realm: Realm) {
        showStatus("\nPerforming basic Query operation...")
        showStatus("Number of persons: ${realm.where<Person>().count()}")

        val ageCriteria = 99
        val results = realm.where<Person>().equalTo("age", ageCriteria).findAll()

        showStatus("Size of result set: " + results.size)
    }

    private fun basicLinkQuery(realm: Realm) {
        showStatus("\nPerforming basic Link Query operation...")
        showStatus("Number of persons: ${realm.where<Person>().count()}")

        val results = realm.where<Person>().equalTo("cats.name", "Tiger").findAll()

        showStatus("Size of result set: ${results.size}")
    }

    private fun complexReadWrite(realm: Realm): String {
        var status = "\nPerforming complex Read/Write operation..."

        //region    移动
        val persons = mutableListOf<Person>()
        val fido = Dog()//realm.createObject<Dog>()
        fido.name = "fido"
        for (i in 1..9) {
            val person = Person(i.toLong())//realm.createObject<Person>(i.toLong())
            persons.add(person)
            person.name = "Person no. $i"
            person.age = i
            person.dog = fido

            // The field tempReference is annotated with @Ignore.
            // This means setTempReference sets the Person tempReference
            // field directly. The tempReference is NOT saved as part of
            // the RealmObject:
            person.tempReference = 42

            for (j in 0..i - 1) {
                val cat = Cat()//realm.createObject<Cat>()
                cat.name = "Cat_$j"
                person.cats.add(cat)
            }
        }
        //endregion

        // Add ten persons in one transaction
        realm.executeTransaction {
            persons.forEach {
                realm.copyToRealm(it)
            }
            /*
            val fido = realm.createObject<Dog>()
            fido.name = "fido"
            for (i in 1..9) {
                val person = realm.createObject<Person>(i.toLong())
                person.name = "Person no. $i"
                person.age = i
                person.dog = fido

                // The field tempReference is annotated with @Ignore.
                // This means setTempReference sets the Person tempReference
                // field directly. The tempReference is NOT saved as part of
                // the RealmObject:
                person.tempReference = 42

                for (j in 0..i - 1) {
                    val cat = realm.createObject<Cat>()
                    cat.name = "Cat_$j"
                    person.cats.add(cat)
                }
            }*/
        }

        // Implicit read transactions allow you to access your objects
        status += "\nNumber of persons: ${realm.where<Person>().count()}"

        //取出
        val realPersons = realm.where<Person>().between("id",1,9).findAll()
        persons.clear()
        realPersons.forEach {
            persons.add(realm.copyFromRealm(it))
        }
        Log.v(TAG, persons.size.toString())
        // Iterate over all objects
        for (person in realm.where<Person>().findAll()) {
            val dogName: String = person?.dog?.name ?: "None"

            status += "\n${person.name}: ${person.age} : $dogName : ${person.cats.size}"

            // The field tempReference is annotated with @Ignore
            // Though we initially set its value to 42, it has
            // not been saved as part of the Person RealmObject:
            check(person.tempReference == 0)
        }

        // Sorting
        val sortedPersons = realm.where<Person>().sort(Person::age.name, Sort.DESCENDING).findAll()
        status += "\nSorting ${sortedPersons.last()?.name} == ${realm.where<Person>().findAll().first()?.name}"

        return status
    }

    private fun complexQuery(realm: Realm): String {
        var status = "\n\nPerforming complex Query operation..."

        status += "\nNumber of persons: ${realm.where<Person>().count()}"

        // Find all persons where age between 7 and 9 and name begins with "Person".
        val results = realm.where<Person>()
                .between("age", 7, 9)       // Notice implicit "and" operation
                .beginsWith("name", "Person")
                .findAll()

        status += "\nSize of result set: ${results.size}"

        return status
    }
}
