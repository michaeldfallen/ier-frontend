package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

trait OpenRegisterBlocks {
  self: ConfirmationBlock =>

  def openRegister = {
    ConfirmationQuestion(
      title = "Open register",
      editLink = overseas.OpenRegisterStep.routing.editGet.url,
      changeName = "open register",
      content = ifComplete(keys.openRegister) {
        if (form(keys.openRegister.optIn).value == Some("true")){
          List("I want to include my name and address on the open register")
        } else {
          List("I don't want my name and address on the open register")
        }
      }
    )
  }
}
