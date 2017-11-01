package ch.insign.client.handlers

import ch.insign.client.TodoClient
import ch.insign.client.events._
import ch.insign.client.state.TodoListState
import ch.insign.shared.Todo
import diode.{ActionHandler, ActionResult, Effect, ModelRW}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.languageFeature.higherKinds

object TodoHandler {

  def apply(state: ModelRW[TodoListState, List[Todo]])
           (implicit todoClient: TodoClient): ActionHandler[TodoListState, List[Todo]] =
    new ActionHandler[TodoListState, List[Todo]](state) {

      override def handle: PartialFunction[Any, ActionResult[TodoListState]] = {

        case Initialize =>
          effectOnly(
            Effect(
              loadAll()
            )
          )

        case TodosLoaded(todos) =>
          updated(todos)


        case CreateTodo(todo) =>
          effectOnly(
            Effect(
              create(todo)
            )
          )

        case TodoCreated(_) =>
          effectOnly(
            Effect(
              loadAll()
            )
          )

        case UpdateTodo(todo) =>
          effectOnly(
            Effect(
              update(todo)
            )
          )

        case TodoUpdated(_) =>
          effectOnly(
            Effect(
              loadAll()
            )
          )

        case DeleteTodo(todo) =>
          effectOnly(
            Effect(
              delete(todo)
            )
          )

        case TodoDeleted =>
          effectOnly(
            Effect(
              loadAll()
            )
          )

        case TodoError(t) =>
          println(t.getMessage)
          t.printStackTrace()
          noChange

      }

    }

  def loadAll()(implicit todoClient: TodoClient): Future[TodoAppEvent] =
    todoClient
      .list()
      .map(TodosLoaded.apply)

  def create(todo: String)(implicit todoClient: TodoClient): Future[TodoAppEvent] =
    todoClient
      .create(todo)
      .map(TodoCreated.apply)
      .recover {
        case t => TodoError(t)
      }

  def update(todo: Todo)(implicit todoClient: TodoClient): Future[TodoAppEvent] =
    todoClient
      .update(todo)
      .map(TodoUpdated.apply)
      .recover {
        case t => TodoError(t)
      }

  def delete(todo: Todo)(implicit todoClient: TodoClient): Future[TodoAppEvent] =
    todoClient
      .delete(todo.id)
      .map(_ => TodoDeleted)
      .recover {
        case t => TodoError(t)
      }

}
