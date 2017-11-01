package ch.insign.client.ui

import ch.insign.client.events.{DeleteTodo, UpdateTodo}
import mdl.InputText
import ch.insign.client.state.TodoListItemState
import diode.Dispatcher
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element

import scalatags.JsDom.attrs.cls
import scalatags.JsDom.short._

class TodoListItem(dispatch: Dispatcher) {

  var s: TodoListItemState = null // hack

  val textElement: Span =
    mdl.bigText("").render

  val inputElement: InputText =
    new InputText()

  val spacer: Div =
    mdl.spacer().render

  val editButton: Button =
    mdl.button("edit", onEdit).render

  val doneButton: Button =
    mdl.button("done", onDone).render

  val deleteButton: Button =
    mdl.button("delete", onDelete).render

  val element: LI =
    li(
      cls := "mdl-list__item",
      span(
        cls := "mdl-list__item-primary-content",
        textElement,
        spacer,
        inputElement.render,
        editButton,
        doneButton,
        deleteButton
      )
    ).render

  import mdl._

  def onEdit(el: Element): Unit =
    if(s.isEditing)
      save()
    else
      updateUI(s.copy(todo = s.todo, isEditing = true))

  def onDone(el: Element): Unit = {
    val updated = s.todo.copy(done = !s.todo.done)
    dispatch.dispatch(UpdateTodo(updated))
    updateUI(s.copy(todo = updated, isEditing = false))
  }

  def onDelete(el: Element): Unit = {
    dispatch.dispatch(DeleteTodo(s.todo))
  }

  private def save(): Unit = {
    val updated = s.todo.copy(text = inputElement.input.value)
    dispatch.dispatch(UpdateTodo(updated))
    updateUI(s.copy(todo = updated, isEditing = false))
  }

  def updateUI(state: TodoListItemState): Unit = {
    s = state // hack
    inputElement.input.value = state.todo.text
    textElement.innerHTML = state.todo.text
    if(state.isEditing) {
      textElement.hide()
      spacer.hide()
      inputElement.container.show()
      editButton.addClass("mdl-button--colored")
    } else {
      textElement.show()
      spacer.show()
      inputElement.container.hide()
      editButton.removeClass("mdl-button--colored")
    }
    if(state.todo.done) {
      textElement.show()
      textElement.addClass("done")
      spacer.show()
      inputElement.container.hide()
      editButton.hide()
      doneButton.show()
      deleteButton.show()
    } else {
      textElement.removeClass("done")
      editButton.show()
      doneButton.show()
      deleteButton.hide()
    }

  }

}
