package com.olegych.scastie.client

import com.olegych.scastie.api.SnippetId

import play.api.libs.json._

import japgolly.scalajs.react.extra._

object ModalState {
  implicit val formatModalState: OFormat[ModalState] = Json.format[ModalState]

  def allClosed: ModalState =
    ModalState(isWelcomeModalClosed = true,
               isHelpModalClosed = true,
               None,
               isResetModalClosed = true,
               isNewSnippetModalClosed = true)

  def default: ModalState = ModalState(
    isHelpModalClosed = true,
    isWelcomeModalClosed = false,
    shareModalSnippetId = None,
    isResetModalClosed = true,
    isNewSnippetModalClosed = true
  )
}

case class ModalState(
    isWelcomeModalClosed: Boolean,
    isHelpModalClosed: Boolean,
    shareModalSnippetId: Option[SnippetId],
    isResetModalClosed: Boolean,
    isNewSnippetModalClosed: Boolean
) {
  val isShareModalClosed: SnippetId ~=> Boolean =
    Reusable.fn(
      shareModalSnippetId2 =>
        !shareModalSnippetId.contains(shareModalSnippetId2)
    )

}
