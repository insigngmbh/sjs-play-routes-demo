package ch.insign.client.ui

import ch.insign.client.events.CreateTodo
import ch.insign.client.state.{TodoListItemState, TodoListState}
import ch.insign.shared.Todo
import diode.Dispatcher
import org.scalajs.dom.console
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element

import scala.collection.mutable
import scalatags.JsDom.attrs.cls
import scalatags.JsDom.short._

class TodoList(dispatch: Dispatcher) {

  val textInput: mdl.InputText =
    new mdl.InputText(placeholder = "Add another task...")

  def addItem(el: Element): Unit = {
    console.log(s"add: ${textInput.input.value}")
    dispatch(CreateTodo(textInput.value))
    textInput.setValue("")
  }

  val addTodo: LI =
    li(
      cls := "mdl-list__item",
      textInput.container,
      mdl.button("add", addItem)
    ).render

  val listItems: mutable.MutableList[TodoListItem] = new mutable.MutableList[TodoListItem]

  val element: UList =
    ul(
      cls := "demo-list-item mdl-list",
      addTodo
    ).render

  def infiniteItems: Stream[TodoListItem] =
    Stream
      .from(0)
      .map(i => listItems.get(i).getOrElse(new TodoListItem(dispatch: Dispatcher)))

  def updateUI(todoList: TodoListState): Unit = {
    todoList
      .todoList
      .zip(infiniteItems)
      .zipWithIndex
      .foreach {
        case ((state, item), index) =>
          item.updateUI(state)
          if(!contains(item.element))
            element.insertBefore(item.element, element.childNodes.item(index))
          if(!listItems.contains(item))
            listItems += item
      }
    removeDeletedItems(todoList.todoList)
  }

  private def contains(child: LI): Boolean =
    (for {
      i <- 1 to element.childNodes.length
    } yield element.childNodes.item(i)).contains(child)

  private def removeDeletedItems(todoList: List[TodoListItemState]): Unit =
    for {
      i <- todoList.length until (element.childNodes.length - 1)
    } yield element.removeChild(element.childNodes.item(i))

}
