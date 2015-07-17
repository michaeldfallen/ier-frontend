(function () {
  "use strict";

	var	root = this,
      	$ = root.jQuery,
      	//Searching for any RADIO with SELECTED against it to include it within the SOURCE/TARGET config
      	$selectedRadioInput = $( '.selectable.binary.selected>input'),
      	selectedRadioInput = ( $selectedRadioInput.length > 0 ) ? $selectedRadioInput[0] : null,
      	$target = $( '.local-fix-target' ),
      	$source = $( '.local-fix-source' ),
      	_toggleClass = 'optional-section-open',
      	_inclusionListIds = [
      	    //Array list of source action elements
      	    'previousName_hasPreviousNameOption_true',
            'previousName_hasPreviousNameOption_other',
            'overseasParentName_parentPreviousName_hasPreviousNameOption_true',
            'overseasParentName_parentPreviousName_hasPreviousNameOption_other'
        ];

    function _hideContent () {
      $target.removeClass(_toggleClass);
      $target.attr({
        'aria-hidden' : true,
        'aria-expanded' : false
      });
    }

    function _showContent () {
      $target.addClass(_toggleClass);
      $target.attr({
        'aria-hidden' : false,
        'aria-expanded' : true
      });
    }

    //For every RADIO change, run this function to SHOW/HIDE the target element
    $source.on( 'change', function( event ) {
    	var targetId = event.target.id,
    		show = false;

    	for ( var i=0, max=_inclusionListIds.length; i<max; i++) {
    		if ( _inclusionListIds[i] === targetId ) {
    			show = true;
    			break;
    		}
    	}

    	if ( show ) {
    		_showContent();
    	}
    	else {
    		_hideContent();
    	}
    });

    //If any RADIO is SELECTED on page load, then trigger the change function above
    if ( selectedRadioInput !== null ) {
        selectedRadioInput.checked = true;
        $selectedRadioInput.trigger( 'change');
    }

}.call(this));