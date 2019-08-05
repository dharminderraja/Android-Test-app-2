package com.example.yumemitest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.view.inputmethod.InputMethodManager
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.view.View


class MainActivity : AppCompatActivity() {

    var todos: MutableList<Todo>? = mutableListOf()
    var db: TodoDatabase? = null
    var todoadapter: TodoListAdapter? = null
    var myExecutor: ExecutorService? = null
    var all = true
    var active = false
    var completed = false
    var completedall = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TodoDatabase.invoke(applicationContext)


        myExecutor = Executors.newSingleThreadExecutor()
        myExecutor!!.execute {
            todos = db!!.todoDao().getAll()
            todoadapter = TodoListAdapter(todos!!, this)
            if (todos!!.count { todo: Todo -> !todo.status!! } == 0)
            {
                completedall = true
            }
            runOnUiThread {
                todolist.adapter = todoadapter
                calculateremaining()
            }
        }

        todolist.layoutManager = LinearLayoutManager(this)
        clearcompleted.paintFlags = clearcompleted.paintFlags or UNDERLINE_TEXT_FLAG

        addtodo.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addTodo(addtodo.text.toString())
                addtodo.setText("")
                handled = true
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm!!.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            handled
        }


        buttonall.setOnClickListener {
            if (!all){
                all = true
                active = false
                completed = false
                var todostemp:MutableList<Todo>
                myExecutor!!.execute {
                    todostemp = db!!.todoDao().getAll()
                    runOnUiThread {
                        todos!!.clear()
                        todos = todostemp
                        todolist.invalidate()
                        todoadapter = TodoListAdapter(todos!!, this)
                        todolist.adapter = todoadapter
                        calculateremaining()
                    }
                }
                buttonall.background = getResources().getDrawable(R.drawable.button_pressed)
                buttonactive.background = getResources().getDrawable(R.drawable.button_default)
                buttoncompleted.background = getResources().getDrawable(R.drawable.button_default)
            }
        }

        buttonactive.setOnClickListener {
            if (!active){
                all = false
                active = true
                completed = false
                var todostemp:MutableList<Todo>
                myExecutor!!.execute {
                    todostemp = db!!.todoDao().getAll()
                    var filtereditems = todostemp.filter { todo: Todo -> !todo.status!! }.toMutableList()
                    runOnUiThread {
                        todos!!.clear()
                        todos = filtereditems
                        todolist.invalidate()
                        todoadapter = TodoListAdapter(todos!!, this)
                        todolist.adapter = todoadapter
                        calculateremaining()
                    }
                }
                buttonall.background = getResources().getDrawable(R.drawable.button_default)
                buttonactive.background = getResources().getDrawable(R.drawable.button_pressed)
                buttoncompleted.background = getResources().getDrawable(R.drawable.button_default)
            }
        }

        buttoncompleted.setOnClickListener {
            if (!completed){
                all = false
                active = false
                completed = true
                var todostemp:MutableList<Todo>
                myExecutor!!.execute {
                    todostemp = db!!.todoDao().getAll()
                    var filtereditems = todostemp.filter { todo: Todo -> todo.status!! }.toMutableList()
                    runOnUiThread {
                        todos!!.clear()
                        todos = filtereditems
                        todolist.invalidate()
                        todoadapter = TodoListAdapter(todos!!, this)
                        todolist.adapter = todoadapter
                        calculateremaining()
                    }
                }
                buttonall.background = getResources().getDrawable(R.drawable.button_default)
                buttonactive.background = getResources().getDrawable(R.drawable.button_default)
                buttoncompleted.background = getResources().getDrawable(R.drawable.button_pressed)
            }
        }

        clearcompleted.setOnClickListener {
            myExecutor!!.execute {
                var todostemp = db!!.todoDao().getAll()
                var filtereditems = todostemp.filter { todo: Todo -> todo.status!! }.toMutableList()
                db!!.todoDao().deletemany(filtereditems)
                todos!!.clear()
                todos = db!!.todoDao().getAll()
                runOnUiThread {
                    if (completed){
                        completed = false
                        all = true
                        buttonall.background = getResources().getDrawable(R.drawable.button_pressed)
                        buttonactive.background = getResources().getDrawable(R.drawable.button_default)
                        buttoncompleted.background = getResources().getDrawable(R.drawable.button_default)
                    }
                    todolist.invalidate()
                    todoadapter = TodoListAdapter(todos!!, this)
                    todolist.adapter = todoadapter
                    calculateremaining()
                }
            }
        }

        completeall.setOnClickListener {
            myExecutor!!.execute {
                var todostemp = db!!.todoDao().getAll()
                var filtereditems: MutableList<Todo>? = null
                if (!completedall)
                    filtereditems = todostemp.filter { todo: Todo -> !todo.status!! }.toMutableList()
                else
                    filtereditems = todostemp.filter { todo: Todo -> todo.status!! }.toMutableList()
                for (item in filtereditems){
                    item.status = !completedall
                }
                completedall = !completedall
                db!!.todoDao().updatemany(filtereditems)
                todos!!.clear()
                todos = db!!.todoDao().getAll()
                runOnUiThread {
                    todolist.invalidate()
                    todoadapter = TodoListAdapter(todos!!, this)
                    todolist.adapter = todoadapter
                    calculateremaining()
                }
            }
        }

    }

    fun addTodo(text:String){
        val todo = Todo(0,text, false)
        myExecutor!!.execute {
            db!!.todoDao().insert(todo)
            todos = db!!.todoDao().getAll()
            runOnUiThread {
                todoadapter!!.updateData(todos!!)
                calculateremaining()
            }
        }
    }

    fun updateStatus(position: Int, status: Boolean){
        todos!![position].status = status
        myExecutor!!.execute {
            db!!.todoDao().update(todos!![position])
        }
        calculateremaining()
    }

    fun deleteTodo(item: Todo){
        myExecutor!!.execute {
            db!!.todoDao().delete(item)
        }
        calculateremaining()
    }

    fun calculateremaining(){
        itemcounter.text = todos!!.count { todo: Todo -> !todo.status!! }.toString() + " Items Left"

        if (todos!!.count { todo: Todo -> todo.status!! } == 0){
            clearcompleted.visibility = View.GONE
        } else {
            clearcompleted.visibility = View.VISIBLE
        }
    }
}
