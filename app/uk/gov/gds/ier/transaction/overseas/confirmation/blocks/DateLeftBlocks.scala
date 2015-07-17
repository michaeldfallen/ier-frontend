package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.LastRegisteredType._

trait DateLeftBlocks {
  self: ConfirmationBlock =>

  def dateLeftSpecial = {
    form.lastRegisteredType match {
      case Some(Forces) => dateLeftForces
      case Some(Crown) => dateLeftCrown
      case Some(Council) => dateLeftCouncil
      case _ => throw new IllegalArgumentException(
        "Last registered type invalid, must be either Forces, Crown or Council"
      )
    }
  }

  def dateLeftUk = {
    val yearMonth = form.dateLeftUk map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Left the UK",
      editLink = overseas.DateLeftUkStep.routing.editGet.url,
      changeName = "date you left the UK",
      content = ifComplete(keys.dateLeftUk) {
        List(yearMonth)
      }
    )
  }

  def dateLeftForces = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Left the armed forces",
      editLink = overseas.DateLeftArmyStep.routing.editGet.url,
      changeName = "date you left the armed forces",
      content = ifComplete(keys.dateLeftSpecial) {
        List(yearMonth)
      }
    )
  }

  def dateLeftCrown = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you ceased to be a Crown Servant",
      editLink = overseas.DateLeftCrownStep.routing.editGet.url,
      changeName = "date you ceased to be a Crown Servant",
      content = ifComplete(keys.dateLeftSpecial) {
        List(yearMonth)
      }
    )
  }

  def dateLeftCouncil = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you left the British Council",
      editLink = overseas.DateLeftCrownStep.routing.editGet.url,
      changeName = "date you left the British Council",
      content = ifComplete(keys.dateLeftSpecial) {
        List(yearMonth)
      }
    )
  }
}
