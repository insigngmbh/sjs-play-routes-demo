package ch.insign.shared

// A shared class, to be used in both frontend and backend
case class Todo(id: Int, text: String, done: Boolean = false)
