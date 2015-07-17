package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.{FormKeys, Key, ErrorTransformForm}
import uk.gov.gds.ier.step.InprogressApplication

trait MustacheModel extends FormKeys {

  case class Text(value:String = "")
  case class FieldSet (classes:String = "")
  case class Field (
      id:String = "",
      name:String = "",
      classes:String ="",
      value:String = "",
      attributes:String = "",
      optionList:List[SelectOption] = List.empty
  )

  case class SelectOption(value:String, text:String, selected:String = "")
  val Question = uk.gov.gds.ier.mustache.Question
  type Question = uk.gov.gds.ier.mustache.Question

  object FieldSet {
    def apply[T<:InprogressApplication[T]] (
      key: Key
    ) (
      implicit progressForm: ErrorTransformForm[T]
    ): FieldSet = {
      FieldSet(
        classes = if (progressForm(key).hasErrors) "invalid" else ""
      )
    }
  }

  object SelectField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, optionList:List[SelectOption], default:SelectOption)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = progressForm(key).value.getOrElse(""),
        classes = if (progressForm(key).hasErrors) "invalid" else "",
        optionList = default :: optionList)
    }
  }

  object TextField {
    def apply[T]
        (key: Key, default:Option[String] = None)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = progressForm(key).value.orElse(default).getOrElse(""),
        classes = if (progressForm(key).hasErrors) "invalid" else "")
    }
  }

  object HiddenField {
    // Hidden field is "declare intent" style of field, it does not really render field as hidden
    // Hidden fields are usually used to store specific values, hence value is explicitly required
    def apply[T](
        key: Key,
        value: String)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = value,
        classes = "")
    }
  }

  object CheckboxField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, value: String)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = value,
        attributes = if (progressForm(key).value.exists(_ == value)) {
          "checked=\"checked\""
        } else {
          ""
        },
        classes = if (progressForm(key).hasErrors) "invalid" else ""
      )
    }
  }

  object RadioField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, value: String)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(value),
        name = key.key,
        value = value,
        attributes = if (progressForm(key).value.exists(_ == value)) {
          "checked=\"checked\""
        } else {
          ""
        },
        classes = if (progressForm(key).hasErrors) "invalid" else ""
      )
    }
  }
}
