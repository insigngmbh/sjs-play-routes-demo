package ch.insign.client.state

import ch.insign.shared.Todo

case class TodoListItemState(todo: Todo, isEditing: Boolean)
