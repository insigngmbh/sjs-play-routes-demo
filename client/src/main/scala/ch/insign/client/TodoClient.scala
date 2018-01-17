package ch.insign.client

import ch.insign.shared.Todo
import fr.hmil.roshttp.body.{BodyPart, PlainTextBody}
import fr.hmil.roshttp.{HttpRequest, Protocol}
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import routes.controllers.TodoController

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

class TodoClient(protocol: Protocol, host: String, port: Int) {

  implicit def encodeTodo(todo: Todo): BodyPart =
    PlainTextBody(todo.asJson.noSpaces)

  private val OK: Int = 200

  private val CREATED: Int = 201

  private val NO_CONTENT: Int = 204

  implicit private val httpClient =
    HttpRequest()
      .withProtocol(protocol)
      .withHost(host)
      .withPort(port)

  def list(): Future[List[Todo]] =
    TodoController
      .list()
      .send()
      .map(expectStatusCode(OK))
      .map(decodeOrThrow[List[Todo]])

  def get(id: Int): Future[Todo] =
    TodoController
      .show(id)
      .send()
      .map(expectStatusCode(OK))
      .map(decodeOrThrow[Todo])

  def create(todo: String): Future[Todo] =
    TodoController
      .create()
      .send(Todo(id = 0, todo))
      .map(expectStatusCode(CREATED))
      .map(decodeOrThrow[Todo])

  def update(todo: Todo): Future[Todo] =
    TodoController
      .update(todo.id)
      .send(todo)
      .map(expectStatusCode(OK))
      .map(decodeOrThrow[Todo])

  def delete(id: Int): Future[String] =
    TodoController
      .delete(id)
      .send()
      .map(expectStatusCode(NO_CONTENT))

  private def expectStatusCode(expectedStatusCode: Int)(response: SimpleHttpResponse): String =
    if(response.statusCode == expectedStatusCode)
      response.body
    else
      throw new IllegalStateException(s"The status ${response.statusCode} code did not match the expected status code $expectedStatusCode")

  private def decodeOrThrow[T](json: String)(implicit decoder: Decoder[T]): T =
    decode[T](json) match {
      case Left(error) => throw error
      case Right(t) => t
    }

}
