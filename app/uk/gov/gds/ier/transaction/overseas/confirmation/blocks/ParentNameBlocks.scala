package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

trait ParentNameBlocks {
  self: ConfirmationBlock =>

  def parentName = {
    val nameStr = List(
      form(keys.overseasParentName.parentName.firstName).value,
      form(keys.overseasParentName.parentName.middleNames).value,
      form(keys.overseasParentName.parentName.lastName).value
    ).flatten.mkString(" ")

    ConfirmationQuestion(
      title = "Parent's or guardian's name",
      editLink = overseas.ParentNameStep.routing.editGet.url,
      changeName = "full name",
      content = ifComplete(keys.overseasParentName.parentName) {
        List(nameStr)
      }
    )
  }

  def parentPreviousName = {
    val havePreviousName = form(keys.overseasParentName.parentPreviousName.hasPreviousName).value
    val prevNameStr =  havePreviousName match {
      case `hasPreviousName` => {
        List(
          form(keys.overseasParentName.parentPreviousName.previousName.firstName).value,
          form(keys.overseasParentName.parentPreviousName.previousName.middleNames).value,
          form(keys.overseasParentName.parentPreviousName.previousName.lastName).value
        ).flatten.mkString(" ")
      }
      case _ => "They haven't changed their name since they left the UK"
    }
    ConfirmationQuestion(
      title = "Parent's or guardian's previous name",
      editLink = overseas.ParentNameStep.routing.editGet.url,
      changeName = "previous name",
      content = ifComplete(keys.overseasParentName.parentPreviousName) {
        List(prevNameStr)
      }
    )
  }

  private val under18 = Some(true)
  private val withLimit = Some(true)
  private val hasPreviousName = Some("true")
}
