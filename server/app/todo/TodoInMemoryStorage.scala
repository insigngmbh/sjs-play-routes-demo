package todo

import ch.insign.shared.Todo

import scala.collection.mutable
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class TodoInMemoryStorage {

  private val todos: mutable.Buffer[Todo] = mutable.Buffer(
    Todo(1, "Drink a coffee"),
    Todo(2, "Get the mail"),
    Todo(3, "Pay the bills")
  )

  def getAll: Future[List[Todo]] =
    Future {
      todos.toList
    }

  def getById(id: Int): Future[Option[Todo]] =
    Future {
      todos.find(_.id == id)
    }

  def create(todo: String): Future[Todo] =
    Future {
      val newTodo = Todo(autoIncrementId(), todo)
      todos += Todo(autoIncrementId(), todo)
      newTodo
    }

  def update(todo: Todo): Future[Option[Todo]] =
    Future {
      todos
        .find(_.id == todo.id)
        .map { oldTodo =>
          val index = todos.indexOf(oldTodo)
          todos.update(index, todo)
          todo
        }
    }

  def delete(id: Int): Future[Boolean] =
    Future {
      todos
        .find(_.id == id)
        .map { oldTodo =>
          val index = todos.indexOf(oldTodo)
          todos.remove(index)
        }.isDefined
    }

  private def autoIncrementId(): Int =
    todos.map(_.id).max + 1

}
