// Stub for GOVUK.registerToVote.message which is generated at runtime
GOVUK.registerToVote.messages = function (key) {
  switch (key) {
    case 'back_button':
      return "Back"
    case 'back_button_non_visual':
      return "to the previous question"
    case 'ordinary_nationality_other_country':
      return "Add another country"
    case 'ordinary_nationality_autocomplete_status':
      return "available, use up and down arrow keys to navigate"
    case 'ordinary_nationality_autocomplete_status_prefix_multiple':
      return "results are"
    case 'ordinary_nationality_autocomplete_status_prefix_singular':
      return "result is"
    case 'ordinary_address_previousAddressTest':
      return "previous address";
    case 'ordinary_address_loading':
      return "Finding address";
    case 'ordinary_address_postcode':
      return "Postcode";
    case 'ordinary_address_continue':
      return "Continue";
    case 'ordinary_address_selectAddress':
      return "Select your address";
    case 'ordinary_address_excuse':
      return "I can't find my address in the list"
    case 'ordinary_address_selectPreviousAddress':
      return "Select your previous address";
    case 'ordinary_address_previousAddressExcuse':
      return "I can't find my previous address in the list";
    default:
      return '';
  }
};
