package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

trait NameBlocks {
  self: ConfirmationBlock =>

  def name = {
    val nameStr = List(
      form(keys.name.firstName).value,
      form(keys.name.middleNames).value,
      form(keys.name.lastName).value
    ).flatten.mkString(" ")

    ConfirmationQuestion(
      title = "Full name",
      editLink = overseas.NameStep.routing.editGet.url,
      changeName = "full name",
      content = ifComplete(keys.overseasName.name) {
        List(nameStr)
      }
    )
  }

  def previousName = {
    val hasPreviousName = form(keys.previousName.hasPreviousName).value
    val nameChangeReason = form(keys.previousName.reason).value match {
      case Some(reason) if reason.nonEmpty => List("Reason for the name change:", reason)
      case _ => List.empty
    }

    val prevNameContent =  hasPreviousName match {
      case Some("true") => {
        List(
          List(
            form(keys.previousName.previousName.firstName).value,
            form(keys.previousName.previousName.middleNames).value,
            form(keys.previousName.previousName.lastName).value
          ).flatten.mkString(" ")
        ) ++ nameChangeReason
      }
      case _ => List("I have not changed my name in the last 12 months")
    }
    ConfirmationQuestion(
      title = "Previous name",
      editLink = overseas.NameStep.routing.editGet.url,
      changeName = "previous name",
      content = ifComplete(keys.overseasName.previousName) {
        prevNameContent
      }
    )
  }
}
