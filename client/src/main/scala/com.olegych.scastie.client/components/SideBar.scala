package com.olegych.scastie
package client
package components

import com.olegych.scastie.api._

import japgolly.scalajs.react._
import vdom.all._
import extra._

import scala.scalajs.js
import js.annotation._
@JSImport("resources/images/icon-scastie.png", JSImport.Namespace)
@js.native
object ScastieLogo extends js.Object

object Assets {
  def logoUrl: String = ScastieLogo.asInstanceOf[String]
}

final case class SideBar(isDarkTheme: Boolean,
                         status: StatusState,
                         inputs: Inputs,
                         ensimeConfigurationLoading: Boolean,
                         toggleTheme: Reusable[Callback],
                         view: StateSnapshot[View],
                         openHelpModal: Reusable[Callback],
                         updateEnsimeConfig: Reusable[Callback]) {
  @inline def render: VdomElement = SideBar.component(this)
}

object SideBar {

  implicit val reusability: Reusability[SideBar] =
    Reusability.caseClass[SideBar]

  private def render(props: SideBar): VdomElement = {
    val toggleThemeLabel =
      if (props.isDarkTheme) "Light"
      else "Dark"

    val selectedIcon =
      if (props.isDarkTheme) "fa fa-sun-o"
      else "fa fa-moon-o"

    val themeButton =
      li(onClick --> props.toggleTheme,
         role := "button",
         title := s"Select $toggleThemeLabel Theme (F2)",
         cls := "btn")(
        i(cls := s"fa $selectedIcon"),
        span(toggleThemeLabel)
      )

    val helpButton =
      li(onClick --> props.openHelpModal,
         role := "button",
         title := "Show help Menu",
         cls := "btn")(
        i(cls := "fa fa-question-circle"),
        span("Help")
      )

    val runnersStatusButton = {
      val ensimeLoading =
        !props.status.ensimeReady(props.inputs) &&
          !props.ensimeConfigurationLoading &&
          props.status.ensimeRunnersCount.map(_ > 0).getOrElse(false)

      val (statusIcon, statusClass, statusLabel) =
        (props.status.sbtRunnerCount, ensimeLoading) match {
          case (None, _) =>
            ("fa-times-circle", "status-unknown", "Unknown")

          case (Some(0), _) =>
            ("fa-times-circle", "status-down", "Down")

          case (Some(_), true) =>
            ("fa-circle-o-notch fa-spin", "status-up", "Loading")

          case (Some(_), false) =>
            ("fa-check-circle", "status-up", "Up")
        }

      li(onClick --> props.view.setState(View.Status),
         role := "button",
         title := "Show runners status",
         cls := s"btn $statusClass")(
        i(cls := s"fa $statusIcon"),
        span(statusLabel)
      )
    }

    val editorButton = ViewToggleButton(
      currentView = props.view,
      forView = View.Editor,
      buttonTitle = "Editor",
      faIcon = "fa-edit",
      onClick = props.updateEnsimeConfig
    ).render

    val buildSettingsButton = ViewToggleButton(
      currentView = props.view,
      forView = View.BuildSettings,
      buttonTitle = "Build Settings",
      faIcon = "fa-gear",
      onClick = reusableEmpty
    ).render

    nav(cls := "sidebar")(
      div(cls := "actions-container")(
        div(cls := "logo")(
          img(src := Assets.logoUrl),
          h1("Scastie")
        ),
        ul(cls := "actions-top")(
          editorButton,
          buildSettingsButton
        ),
        ul(cls := "actions-bottom")(
          themeButton,
          helpButton,
          runnersStatusButton
        )
      )
    )
  }

  private val component =
    ScalaComponent
      .builder[SideBar]("SideBar")
      .render_P(render)
      .configure(Reusability.shouldComponentUpdate)
      .build
}
