package ch.insign.client

import ch.insign.client.events.Initialize
import ch.insign.client.state.TodoListState
import ch.insign.client.ui.TodoList
import diode.ModelRO

import scala.scalajs.js
import fr.hmil.roshttp.Protocol.HTTP
import org.scalajs.dom.document

object SjsPlayRoutesApp extends js.JSApp {

  private val todoClient = new TodoClient(HTTP, "localhost", 9000)

  private val todoListComponent = new TodoList(TodoApp)

  override def main(): Unit = {
    document.getElementById("todoList").appendChild(todoListComponent.element)
    TodoApp.subscribe(TodoApp.zoom(identity))(updateUI)
    TodoApp.dispatch(Initialize)
  }

  private def updateUI(state: ModelRO[TodoListState]): Unit =
    todoListComponent.updateUI(state.value)

}