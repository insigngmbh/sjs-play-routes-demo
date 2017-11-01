package ch.insign.client.ui

import org.scalajs.dom.html._
import org.scalajs.dom.raw.{Element, HTMLElement}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.JsDom.attrs.{cls, onclick, tpe}
import scalatags.JsDom.short.{div, i, span}

object mdl {

  implicit class ElementOps[T <: HTMLElement](el: T) {

    def hasClass(className: String): Boolean =
      el.className.split(" ").contains(className)

    def addClass(className: String): T = {
      if(!hasClass(className))
        el.className = s"${el.className} $className"
      el
    }

    def removeClass(className: String): T = {
      if(hasClass(className))
        el.className = el.className.split(" ").filterNot(_ == className).mkString(" ")
      el
    }

    def show(): T =
      removeClass("hidden")

    def hide(): T =
      addClass("hidden")

  }

  def bigText(text: String): TypedTag[Span] =
    span(
      cls := "mdl-typography--display-1 mdl-color-text--grey-600",
      text
    )

  def spacer(): TypedTag[Div] =
    div(
      cls := "mdl-layout-spacer"
    )

  def button(icon: String, onClick: Element => Unit): TypedTag[Button] =
    scalatags.JsDom.short.button(
      cls := "mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect",
      onclick := onClick,
      i(
        cls := "material-icons",
        icon
      )
    )

  class InputText(name: String = "", placeholder: String = "") {

    val input: Input = scalatags.JsDom.short.input(
      cls := "mdl-textfield__input mdl-typography--display-1",
      tpe := "text"
    ).render

    val label: Label = scalatags.JsDom.short.label(
      cls := "mdl-textfield__label",
      placeholder
    ).render

    val container: Div = scalatags.JsDom.short.div(
      cls := "mdl-textfield mdl-js-textfield mdl-textfield--floating-label mdl-layout-spacer",
      input,
      label
    ).render


    def value: String =
      input.value

    def setValue(value: String): InputText = {
      input.value = value
      this
    }

    def render: Div =
      container

  }

}
