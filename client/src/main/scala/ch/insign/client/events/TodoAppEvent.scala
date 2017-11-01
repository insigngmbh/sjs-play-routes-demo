package ch.insign.client.events

import ch.insign.shared.Todo
import diode.Action

trait TodoAppEvent extends Action

case object Initialize extends TodoAppEvent

case class CreateTodo(todo: String) extends TodoAppEvent

case class TodosLoaded(todos: List[Todo]) extends TodoAppEvent

case class TodoCreated(todo: Todo) extends TodoAppEvent

case class UpdateTodo(todo: Todo) extends TodoAppEvent

case class TodoUpdated(todo: Todo) extends TodoAppEvent

case class DeleteTodo(todo: Todo) extends TodoAppEvent

case object TodoDeleted extends TodoAppEvent

case class TodoError(e: Throwable) extends TodoAppEvent
