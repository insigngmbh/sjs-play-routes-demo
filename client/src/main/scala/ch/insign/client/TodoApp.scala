package ch.insign.client

import ch.insign.client.handlers.TodoHandler
import ch.insign.client.state.{TodoListItemState, TodoListState}
import ch.insign.shared.Todo
import diode.Circuit
import fr.hmil.roshttp.Protocol.HTTP

object TodoApp extends Circuit[TodoListState] {

  private implicit val todoClient = new TodoClient(HTTP, "localhost", 9000)

  override def initialModel: TodoListState = TodoListState(List())

  override def actionHandler: HandlerFunction = composeHandlers(
    TodoHandler(zoomRW[List[Todo]](getTodos)(setTodos))
  )

  def getTodos(state: TodoListState): List[Todo] =
    state.todoList.map(_.todo)

  def setTodos(state: TodoListState, todos: List[Todo]): TodoListState =
    state.copy(todoList = todos.map(todo => TodoListItemState(todo, isEditing = false)))

}
