package controllers

import javax.inject._

import cats.{Monad, StackSafeMonad}
import cats.data.EitherT
import ch.insign.shared.Todo
import play.api.mvc._
import todo.TodoInMemoryStorage
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject() (cc: ControllerComponents, todoStorage: TodoInMemoryStorage) extends AbstractController(cc) {

  // REST-Actions (API)

  def list(): Action[AnyContent] = async {
    for {
      todos <- EitherT(findAll())
    } yield encodeTodoList(todos)
  }

  def show(id: Int): Action[AnyContent] = async {
    for {
      todo <- EitherT(findById(id))
    } yield encodeTodo(todo)
  }

  def create(): Action[AnyContent] = async { request =>
    for {
      text <- EitherT(extractBodyText(request))
      todo <- EitherT(decodeTodo(text))
      created <- EitherT(createTodo(todo.text))
    } yield encodeTodo(created, Created)
  }

  def update(id: Int): Action[AnyContent] = async { request =>
    for {
      text <- EitherT(extractBodyText(request))
      todo <- EitherT(decodeTodo(text))
      updated <- EitherT(updateTodo(todo.copy(id = id)))
    } yield encodeTodo(todo)
  }

  def delete(id: Int): Action[AnyContent] = async {
    EitherT(deleteTodo(id))
  }

  // Codec

  private def encodeTodoList(todoList: List[Todo]): Result =
    Ok(todoList.asJson.noSpaces)

  private def encodeTodo(todo: Todo, status: Status = Ok): Result =
    status(todo.asJson.noSpaces)

  private def decodeTodo(text: String): Future[Either[Result, Todo]] =
    Future {
      decode[Todo](text) match {
        case Right(right) => Right(right)
        case Left(error) => Left(BadRequest(error.getMessage))
      }
    }

  // Utility

  private def extractBodyText(request: Request[AnyContent]): Future[Either[Result, String]] =
    Future {
      request
        .body
        .asText
        .map(Right.apply)
        .getOrElse(Left(BadRequest))
    }

  private def findAll(): Future[Either[Result, List[Todo]]] =
    todoStorage.getAll.map(Right.apply)

  private def findById(id: Int): Future[Either[Result, Todo]] =
    todoStorage.getById(id).map {
      case Some(todo) => Right(todo)
      case None => Left(NotFound)
    }

  private def createTodo(todo: String): Future[Either[Result, Todo]] =
    todoStorage.create(todo).map(Right.apply)

  private def updateTodo(todo: Todo): Future[Either[Result, Todo]] =
    todoStorage.update(todo).map {
      case Some(updated) => Right(updated)
      case None => Left(NotFound)
    }

  private def deleteTodo(id: Int): Future[Either[Result, Result]] =
    todoStorage.delete(id).map {
      case true => Right(NoContent)
      case false => Left(InternalServerError)
    }

  private type AsyncResult[A] = EitherT[Future, Result, A]

  private def async(body: => AsyncResult[Result]): Action[AnyContent] =
    async { request =>
      body
    }

  private def async(body: Request[AnyContent] => AsyncResult[Result]): Action[AnyContent] =
    Action.async { request: Request[AnyContent] =>
      body(request)
        .value
        .map { either =>
          either.fold(identity, identity)
        }
    }

  // Allthough Future is not really a monad, we can use it as if it is one.
  // In a real application, use another form of parallelism!
  // This implementation is most likely also not stack-safe
  implicit val futureMonad: Monad[Future] = new Monad[Future] with StackSafeMonad[Future] {

    override def pure[A](x: A): Future[A] =
      Future { x }

    override def map[A, B](fa: Future[A])(f: (A) => B): Future[B] =
      fa.map(f)

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] =
      fa.flatMap(f)

  }

}
