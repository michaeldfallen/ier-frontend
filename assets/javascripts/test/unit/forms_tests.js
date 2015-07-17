describe("BackButton", function () {

  describe("Constructor", function () {
    it("Should produce an instance with the correct interface", function () {
      var backButton = new GOVUK.registerToVote.BackButton();

      expect(backButton.setup).toBeDefined();
      expect(backButton.bindEvents).toBeDefined();
    });

    it("Should call the right methods when the instance is produced", function () {
      var $container = $('<div><header></header></div>'),
          $header = $container.find('header'),
          backButton;

      spyOn(GOVUK.registerToVote.BackButton.prototype, 'setup');
      spyOn(GOVUK.registerToVote.BackButton.prototype, 'bindEvents');
      backButton = new GOVUK.registerToVote.BackButton($header);

      expect(GOVUK.registerToVote.BackButton.prototype.setup).toHaveBeenCalled();
      expect(GOVUK.registerToVote.BackButton.prototype.bindEvents).toHaveBeenCalled();
    });
  });

  describe("Setup method", function () {
    it("Should add the backlink element correctly", function () {
      var $container = $('<div><header></header></div>'),
          $header = $container.find('header'),
          backButtonMock = { '$header' : $header },
          $backLink,
          $childElements,
          backLinkIndex,
          headerIndex;
      
      GOVUK.registerToVote.BackButton.prototype.setup.call(backButtonMock);
      $backLink = $container.find('a.back-to-previous')[0];
      $childElements = $container.children();
      backLinkIndex = $childElements.index($backLink);
      headerIndex = $childElements.index($header);
      expect(backLinkIndex).toBe(headerIndex - 1);
    });
  });

  describe("BindEvents method", function () {
    it("Should add a jQuery click event to the link element", function () {
      var backButtonMock = { '$link' : $('<a></a>') },
          parameters;
      
      spyOn($.fn, "on").and.callFake(function() {
        parameters = arguments;
      });

      GOVUK.registerToVote.BackButton.prototype.bindEvents.call(backButtonMock);
      expect(parameters[0]).toBe('click');
      expect(typeof parameters[1]).toBe('function');
    });

    it("Should call the history.back method when a click is registered", function () {
      var backButtonMock = { '$link' : $('<a></a>') },
          parameters;
      
      spyOn(window.history, "back");
      GOVUK.registerToVote.BackButton.prototype.bindEvents.call(backButtonMock);
      backButtonMock.$link.trigger('click');
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});

describe("ConditionalControl", function () {
  describe("Constructor", function () {
    var elm;
    beforeEach(function () {
     elm = $('<div></div>');
    });

    it("Should produce an instance with the correct interface", function () {
      var control = new GOVUK.registerToVote.ConditionalControl(elm, 'optional-section');

      expect(control.setup).toBeDefined();
      expect(control.bindEvents).toBeDefined();
      expect(control.adjustVerticalSpace).toBeDefined();
      expect(control.toggle).toBeDefined();
    });

    it("Should call the ToggleObj constructor", function () {
      var cachedToggleObj = GOVUK.registerToVote.ToggleObj,
          control;

      spyOn(GOVUK.registerToVote, "ToggleObj");
      control = new GOVUK.registerToVote.ConditionalControl(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj).toHaveBeenCalled();

      GOVUK.registerToVote.ToggleObj = cachedToggleObj;
    });
  });

  describe("setup method", function () {
    var cachedGetElementById = document.getElementById,
        conditionalControlMock,
        $toggle,
        stubGetElementById;

    stubGetElementById = function (handleElm) { 
      document.getElementById = function (id) {
        var elm = document.createElement("input");

        elm.type = "checkbox";
        elm.id = id;
        if (handleElm) { handleElm(elm); }
        return elm;
      };
    };

    beforeEach(function () {
      $toggle = $("<input type='radio' name='options' id='option-1' />");
      conditionalControlMock = {
        "$content" : $("<div id='mock1' data-condition='option-1'></div>"),
        "adjustVerticalSpace" : function () {},
        "toggle" : function () {}
      };

      $(document.body)
        .append($toggle)
        .append(conditionalControlMock.$content);
    });

    afterEach(function () {
      conditionalControlMock.$content.remove();
      $toggle.remove();
      document.getElementById = cachedGetElementById;
    });

    it("Should return false if the toggle control can't be found", function () {
      var setupResult;

      $toggle.remove();
      setupResult = GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(setupResult).toEqual(false);
    });

    it("Should return true if the toggle control can't be found", function () {
      var setupResult;

      setupResult = GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(setupResult).toEqual(true);
    });

    it("Should get the toggle control id from an attribute value on the content", function () {
      var sentId,
          storeSentId;

      storeSentId = function (elm) {
        sentId = elm.id 
      };
      stubGetElementById(storeSentId);
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);

      expect(sentId).toBe('option-1');
    });

    it("Should add an 'aria-controls' attribute linking the toggle control to the content", function () {
      stubGetElementById();
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.$toggle.attr('aria-controls')).toBe('mock1');
    });

    it("Should call adjustVerticalSpace method", function () {
      stubGetElementById();
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalled();
    });

    it("should call the toggle method if control is checked", function () {
      var controlElm,
          setElmAttribute;

      setElmAttribute = function (elm) {
        elm.checked = true;
      };
      stubGetElementById(setElmAttribute);
      spyOn(conditionalControlMock, "toggle");
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.toggle).toHaveBeenCalled();
    });
  });

  describe("bindEvents method", function () {
    var cachedOn = $.fn.on,
        conditionalControlMock;

    beforeEach(function () {
      conditionalControlMock = {
        "$content" : $("<div id='mock1' data-condition='option-1'></div>"),
        "$toggle" : $("<input type='radio' name='options' id='option-1' />"),
        "adjustVerticalSpace" : function () {},
        "toggle" : function () {}
      };
    });

    afterEach(function () {
      $.fn.on = cachedOn;
    });

    it("Should add a change event to the toggle control", function () {
      var evtWasBound = null;

      $.fn.on = function (evt, callback) {
        if (evtWasBound === null) {
          evtWasBound = ((evt === "change") && (this === conditionalControlMock.$toggle)); 
        }
      };
 
      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      expect(evtWasBound).toBe(true);
    });

    it("Should set a change event to call the toggle method", function () {
      var onChange;

      spyOn(conditionalControlMock, "toggle");
      $.fn.on = function (evt, callback) {
        if ((evt === "change") && (this === conditionalControlMock.$toggle)) {
          onChange = callback;
        }
      };

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      onChange();      
      expect(conditionalControlMock.toggle).toHaveBeenCalled();
    });

    it("Should add a custom event, identified by the radio name, to the document", function () {
      var customName;

      $.fn.on = function (evt, callback) {
        if (this[0] === document) {
          customName = evt;
        }
      }; 

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      expect(customName).toBe("radio:" + conditionalControlMock.$toggle[0].name);
    });

    it("Custom event should call the toggle method", function () {
      var onCustom,
          evtStub = {},
          evtDataStub = { 'selectedControl' : conditionalControlMock.$toggle };

      spyOn(conditionalControlMock, "toggle");
      // stub the event binding and store the callback
      $.fn.on = function (evt, callback) {
        if (this[0] === document) {
          onCustom = callback;
        }
      };

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      // call the callback to simulate the event
      onCustom(evtStub, evtDataStub);
      expect(conditionalControlMock.toggle).toHaveBeenCalledWith(conditionalControlMock.$toggle);
    });
  });

  describe("adjustVerticalSpace method", function () {
    var conditionalControlMock;

    beforeEach(function () {
      var $container = $(
        "<div>" +
          "<input type='radio' name='options' id='option-1' />" +
          "<div id='mock1' data-condition='option-1'></div>" +
        "</div>"
      );

      conditionalControlMock = {
        "$content" : $container.find("input"),
        "$toggle" : $container.find("div"),
        "controlAndContentAreSiblings" : true,
        "marginWhenContentIs" : {
          "hidden" : "20px",
          "shown" : "0px",
        }
      };
    });
    
    it("Should set the margin-bottom of the content to that of the toggle control when the toggle is hidden", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.hidden;

      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'hidden');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe(expectedMarginBottom);
    });
    
    it("Should remove the margin-bottom of the content when it is visible", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.shown;

      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'visible');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe(expectedMarginBottom);
    });
    
    it("Should not change the margin-bottom of the content if it and the toggle control are not siblings", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.hidden,
          originalMarginBottom = conditionalControlMock.$toggle.css("margin-bottom");

      conditionalControlMock.controlAndContentAreSiblings = false;
      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'hidden');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe(originalMarginBottom);
    });
  });

  describe("toggle method", function () {
    var conditionalControlMock,
        cachedJquery = $.fn;

    beforeEach(function () {
      conditionalControlMock = {
        "$content" : $("<div></div>"),
        "$toggle" : $("<input type='radio' id='mock1'>"),
        "toggleClass" : 'expanded-section-open',
        "adjustVerticalSpace" : function () {}
      };
      $(document.body)
        .append("<button id='continue' />");
    });

    afterEach(function () {
      $.fn = cachedJquery;
      $("#continue").remove();
    });

    it("Should hide the content if called from a radio being selected that is not the toggle control", function () {
      var selectedControl = $("<input type='radio' id='mock2' />")[0],
          showCalledWith;

      spyOn($.fn, "show")
        .and.callFake(
          function () {
            showCalledWith = this.selector;
          }
      );
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);

      // if a radio input is sent in as the 1st parameter, the method has been called from a radio selection
      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock, selectedControl); 
      // hiding content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("hidden");
      expect($.fn.show).toHaveBeenCalled();
      expect(showCalledWith).toBe("#continue");
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(false);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('true');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('false');
    });

    it("Should show the content if the toggle control is checked", function () {
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      spyOn($.fn, "trigger");
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' checked='checked' />");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      // showing content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("shown");
      expect($.fn.trigger).toHaveBeenCalledWith("toggle.open", { "$toggle" : conditionalControlMock.$toggle });
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(true);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('false');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('true');
    });

    it("Should hide the content when toggle is unchecked", function () {
      spyOn($.fn, "trigger");
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' />");

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      // hiding content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("hidden");
      expect($.fn.trigger).toHaveBeenCalledWith("toggle.closed", { "$toggle" : conditionalControlMock.$toggle });
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(false);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('true');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('false');
    });

    it("Should hide the continue button when toggle is checked & there is a postcode checker in the page", function () {
      var hideCalledWith;

      spyOn($.fn, "hide")
        .and.callFake(
          function () {
            hideCalledWith = this.selector;
          }
      );
      conditionalControlMock.$content
        .addClass(conditionalControlMock.toggleClass)
        .append("<input type='text' class='postcode' />");
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' checked='checked' />");
      $(document.body).append("<div id='found-addresses'></div>");

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      expect($.fn.hide).toHaveBeenCalled();
      expect(hideCalledWith).toBe("#continue");

      $(".postcode").remove();
      $("#found-addresses").remove();
    });
  });
});

describe("OptionalControl", function () {
  describe("Creating an instance", function () {
    var $content;

    beforeEach(function () {
      $content = $(
            '<div id="add-countries"' +
                 'data-control-text="Other country"' +
                 'data-control-id="other-country"' +
                 'data-control-name="other-country"' +
                 'data-control-value="true"' +
                 'data-control-classes="validate"' +
                 'data-control-attributes=""' +
            '>' +
            '</div>'
      );
      $(document.body).append($content); 
    });

    afterEach(function () {
      $content.remove();
      $('#country-1').parent().remove();
    });

    it("Should produce an instance with the correct interface", function () {
      var elm = $content,
          toggleClass = 'optional-section',
          instance = new GOVUK.registerToVote.OptionalControl(elm, toggleClass);

      expect(instance.setup).toBeDefined();
      expect(instance.bindEvents).toBeDefined();
      expect(instance.adjustVerticalSpace).toBeDefined();
      expect(instance.toggle).toBeDefined();
      expect(instance.setAccessibilityAPI).toBeDefined();
      expect(instance.setInitialState).toBeDefined();
    });
  });

  describe("Setup method", function () {
    var $content,
        $control;

    beforeEach(function () {
      $content = $(
            '<div id="add-countries"' +
                 'data-condition="other-country"' +
                 'data-control-text="Other country"' +
                 'data-control-id="other-country"' +
                 'data-control-name="other-country"' +
                 'data-control-value="true"' +
                 'data-control-classes="validate"' +
                 'data-control-attributes=""' +
            '>' +
            '</div>'
      );
      $control = $(
            '<label for="">' +
              '<input type="checkbox" name="other-country" id="other-country" />' +
            '<label>'
          ),
          optionalControlMock = {
            '$content' : $content,
            'toggle' : function () {},
            'adjustVerticalSpace' : function () {},
            'createControl' : function () {},
            '$toggle' : $control.find('input')
          };
      $(document.body).append($content); 
    });

    afterEach(function () {
      $content.remove();
      $('#country-1').parent().remove();
    });

    it("Should call the createControl method", function () {
      spyOn(optionalControlMock, "createControl").and.callFake(function () {
        return 
      });
      GOVUK.registerToVote.OptionalControl.prototype.setup.call(optionalControlMock);

      expect(optionalControlMock.createControl).toHaveBeenCalled();
    });

    it("Should add the control before the content div", function () {
      spyOn(optionalControlMock, "createControl").and.callFake(function () {
        return $control;
      });
      GOVUK.registerToVote.OptionalControl.prototype.setup.call(optionalControlMock);
      expect($content.prev()[0]).toEqual($control[0]);
    });

    it("Should check the control & show the content div if the content div has a textbox with a value", function () {
      $content.append('<input type="text" name="country-1" id="country-1" value="Algeria" />');
      spyOn(GOVUK.registerToVote.OptionalControl.prototype, "createControl").and.callFake(function () {
        return $control
      });
      GOVUK.registerToVote.OptionalControl.prototype.setup.call(optionalControlMock);
      expect(optionalControlMock.$toggle.is(':checked')).toBe(true);
      expect($content.is(':hidden')).toBe(false);
    });

    it("Should return the same value as ConditionalControl's setup method", function () {
      var conditionalControlReturnValue = GOVUK.registerToVote.ConditionalControl.prototype.setup.call(optionalControlMock),
          optionalControlReturnValue = GOVUK.registerToVote.OptionalControl.prototype.setup.call(optionalControlMock);

      expect(conditionalControlReturnValue).toEqual(optionalControlReturnValue);
    });
  });

  describe("CreateControl method", function () {
    it("Should return the expected HTML", function () {
      var result,
          expectedHtml = '<label class="selectable">' +
                           '<input type="checkbox" id="other-country" name="other-country" value="" class="text"  />' +
                           'Other country' +
                         '</label>',
          optionalControlMock = {
            'controlText' : 'Other country',
            'controlId' : 'other-country',
            'controlName' : 'other-country',
            'controlValue' : '',
            'controlClasses' : 'text',
            'controlAttributes' : ''
          };

      result = GOVUK.registerToVote.OptionalControl.prototype.createControl.call(optionalControlMock);
      expect(result).toEqual(expectedHtml);
    });
  });

  describe("BindEvents method", function () {
    var $content;

    beforeEach(function () {
      $content = $(
            '<div id="add-countries"' +
                 'data-control-text="Other country"' +
                 'data-control-id="other-country"' +
                 'data-control-name="other-country"' +
                 'data-control-value="true"' +
                 'data-control-classes="validate"' +
                 'data-control-attributes=""' +
            '>' +
            '</div>'
      );
      $(document.body).append($content); 
      $content.wrap('<form action="" method="get" />');
    });

    afterEach(function () {
      $content.closest('form').remove();
      $content.remove();
      $('#country-1').parent().remove();
    });

    it("Should call the bindEvents method of ConditionalControl", function () {
      var optionalControlMock = {
            "$content" : $content,
            "filterFormContent" : function () {}
          };
      
      spyOn(GOVUK.registerToVote.ConditionalControl.prototype, "bindEvents");
      GOVUK.registerToVote.OptionalControl.prototype.bindEvents.call(optionalControlMock);      

      expect(GOVUK.registerToVote.ConditionalControl.prototype.bindEvents).toHaveBeenCalled();
    });
  });
});

describe("OtherCountryFields", function () {
  var countryHTMLTemplate = '<div class="added-country">' +
                              '<label for="{{id}}" class="country-label" >' +
                                '{{#countryFieldName}}' +
                                  '{{idx}}' +
                                '{{/countryFieldName}}' +
                              '</label>' +
                              '{{#remove-link}}' +
                                '{{idx}}' +
                              '{{/remove-link}}' +
                              '<div class="validation-wrapper">' +
                                '<input type="text" id="{{id}}" ' +
                                       'name="{{name}}" value="{{value}}" ' +
                                       'autocomplete="off" class="text country-autocomplete long validate" ' +
                                       'data-validation-name="{{data-validation-name}}" ' +
                                       'data-validation-type="field" ' +
                                       'data-validation-rules="nonEmpty validCountry">' +
                              '</div>' +
                            '</div>';

  describe("Setup method", function () {
    var otherCountryFieldsMock = {
      'getCountries' : function () {},
      'bindEvents' : function () {},
      'templates' : {
        'addAnotherLink' : '<a class="duplicate-control" href="#">Add another country</a>'
      }
    };

    beforeEach(function () {
      otherCountryFieldsMock.$container = $('<div id="add-countries" />');
      $(document.body).append(otherCountryFieldsMock.$container);
    });

    afterEach(function () {
      otherCountryFieldsMock.$container.remove();
    });

    it("Should call the getCountries and bindEvents methods", function () {
      spyOn(otherCountryFieldsMock, 'getCountries');
      spyOn(otherCountryFieldsMock, 'bindEvents');

      GOVUK.registerToVote.OtherCountryFields.prototype.setup.call(otherCountryFieldsMock);
      expect(otherCountryFieldsMock.getCountries).toHaveBeenCalled();
      expect(otherCountryFieldsMock.bindEvents).toHaveBeenCalled();
    });

    it("Should add the 'Add another country' link to the container element", function () {
      expect(otherCountryFieldsMock.$container.find('.duplicate-control').length).toEqual(0);
      GOVUK.registerToVote.OtherCountryFields.prototype.setup.call(otherCountryFieldsMock);
      expect(otherCountryFieldsMock.$container.find('.duplicate-control').length).toEqual(1);
    });
  });

  describe("GetCountries method", function () {
    var otherCountryFieldsMock = {
          'addCountryElements' : function () {}
        },
        $countries;
 
    it("Should only add nonEmpty values to the countries array", function () {
      var countriesData = [
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(0),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(0),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(0),
              'value' : 'Germany' 
            },
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(1),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(1),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(1),
              'value' : 'Spain' ,
              'remove-link' : true 
            },
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(2),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(2),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(2),
              'value' : '', 
              'remove-link' : true 
            },
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(3),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(3),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(3),
              'value' : '',
              'remove-link' : true
            }
          ],
          idx;

      for (idx = 0; idx < 4; idx++) {
        if (idx === 0) {
          $countries = $(Mustache.render(countryHTMLTemplate, countriesData[idx]));
        } else {
          $countries = $countries.add(Mustache.render(countryHTMLTemplate, countriesData[idx]));
        }
      }
      GOVUK.registerToVote.OtherCountryFields.prototype.getCountries.call(otherCountryFieldsMock, $countries);
      expect(otherCountryFieldsMock.countries.length).toEqual(2);
    });

    it("Should always leave at least one country element, regardless of it having a value or not", function () {
      var countriesData = [
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(0),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(0),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(0),
              'value' : '' 
            },
            {
              'id' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(1),
              'name' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(1),
              'data-validation-name' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(1),
              'value' : '',
              'remove-link' : true 
            }
          ],
          idx;

      for (idx = 0; idx < 2; idx++) {
        if (idx === 0) {
          $countries = $(Mustache.render(countryHTMLTemplate, countriesData[idx]));
        } else {
          $countries = $countries.add(Mustache.render(countryHTMLTemplate, countriesData[idx]));
        }
      }
      GOVUK.registerToVote.OtherCountryFields.prototype.getCountries.call(otherCountryFieldsMock, $countries);
      expect(otherCountryFieldsMock.countries.length).toEqual(1);
    });
  });

  describe("HandleClicks method", function () {
    var otherCountryFieldsMock,
        eventMock;

    beforeEach(function () {
      otherCountryFieldsMock = {
        'addCountry' : function () {},
        '$addAnotherLink' : $('<a href="" class="duplicate-control">Add another country</a>'),
        'removeCountry' : function () {}
      };
      eventMock = {};
    });

    it("Should add a new country element if the event came from a '.duplicate-control' link", function () {
      eventMock.target = otherCountryFieldsMock.$addAnotherLink[0];
      spyOn(otherCountryFieldsMock, 'addCountry');

      GOVUK.registerToVote.OtherCountryFields.prototype.handleClicks.call(otherCountryFieldsMock, eventMock);
      expect(otherCountryFieldsMock.addCountry).toHaveBeenCalled();
    });

    it("Should remove the country element the event originator is in if the event came from a '.remove-field' link", function () {
      var $removeLink = $(
                          '<a href="#" class="remove-field" data-field="nationality_otherCountries[1]">' +
                            'Remove <span class="visuallyhidden">Country</span>' +
                          '</a>'
                        );

      eventMock.target = $removeLink;
      spyOn(otherCountryFieldsMock, 'removeCountry');

      GOVUK.registerToVote.OtherCountryFields.prototype.handleClicks.call(otherCountryFieldsMock, eventMock);
      expect(otherCountryFieldsMock.removeCountry).toHaveBeenCalled();
    });
  });

  describe("BindEvents method", function () {
    it("Should bind the handleClicks method to all click events on the container element", function () {
      var otherCountryFieldsMock = {
            '$container' : $('<div />'),
            'handleClicks' : function () {}
          },
          callback = null;

      spyOn($.fn, 'on').and.callFake(function (evt, func) {
        if ((this === otherCountryFieldsMock.$container) && (evt === 'click')) {
          callback = func;
        }
      });
      spyOn(otherCountryFieldsMock, 'handleClicks');
      GOVUK.registerToVote.OtherCountryFields.prototype.bindEvents.call(otherCountryFieldsMock);
      callback();
      expect(callback).not.toEqual(null);
      expect(otherCountryFieldsMock.handleClicks).toHaveBeenCalled();
    });

    it("Should bind the updateValidation method to the 'contentUpdate' event", function () {
      var otherCountryFieldsMock = {
            '$container' : $('<div />'),
            'handleClicks' : function () {},
            'updateValidation' : function () {}
          },
          eventsBoundTo,
          functionBoundTo;

      spyOn($.fn, "bind").and.callFake(function (eventStr, callback) {
        if (this[0] === document) {
          eventsBoundTo = eventStr;
          functionBoundTo = callback;
        }
      });
      spyOn(otherCountryFieldsMock, "updateValidation");

      GOVUK.registerToVote.OtherCountryFields.prototype.bindEvents.call(otherCountryFieldsMock);
      expect(eventsBoundTo).toEqual('contentUpdate');
      functionBoundTo({}, { context : otherCountryFieldsMock.$container });
      expect(otherCountryFieldsMock.updateValidation).toHaveBeenCalled();
    });
  });

  describe("UpdateCountryValues method", function () {
    it("Should update the values of the stored countries to match those of the country elements in the document", function () {
      var $countries = $(
            '<div class="added-country">' +
              '<input type="text" id="country-1" value="Belgium" class="country-autocomplete" />' + 
            '</div>' +
            '<div class="added-country">' +
              '<input type="text" id="country-2" value="France" class="country-autocomplete" />' + 
            '</div>' +
            '<div class="added-country">' +
              '<input type="text" id="country-3" value="Sweden" class="country-autocomplete" />' +
            '</div>'
          ),
          otherCountryFieldsMock = {
            'countries' : [ 'Germany', 'France', 'Sweden' ],
            '$container' : $('<div class="add-countries" />')
          };

      otherCountryFieldsMock.$container.append($countries);
      $(document.body).append(otherCountryFieldsMock.$container);
      GOVUK.registerToVote.OtherCountryFields.prototype.updateCountryValues.call(otherCountryFieldsMock);
      expect(otherCountryFieldsMock.countries[0]).toEqual('Belgium');
      otherCountryFieldsMock.$container.remove();
    });
  });

  describe("RemoveCountryValue method", function () {
    it("Should update the values of the stored countries so the one sent as a parameter is removed", function () {
      var otherCountryFieldsMock = {
            'countries' : [ 'Germany', 'France', 'Sweden' ]
          };

      $(document.body).append(otherCountryFieldsMock.$countries);
      GOVUK.registerToVote.OtherCountryFields.prototype.removeCountryValue.call(otherCountryFieldsMock, 1);
      expect(otherCountryFieldsMock.countries).toEqual(['Germany', 'Sweden']);
    });
  });

  describe("GetFieldId method", function () {
    it("Should return the correct format of id", function () {
      var id = GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId(1);

      expect(id).toEqual('nationality_otherCountries[1]');
    });
  });

  describe("GetFieldName method", function () {
    it("Should return the correct format of name", function () {
      var name = GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName(1);

      expect(name).toEqual('nationality.otherCountries[1]');
    });
  });

  describe("GetValidationName method", function () {
    it("Should return the correct format of validation name", function () {
      var id = GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(1);

      expect(id).toEqual('added-country-1');
    });
  });

  describe("RemoveEmptyCountry method", function () {
    var $filledCountry = $(
          '<div class="added-country">' +
            '<input type="text" value="Belgium" class="country-autocomplete" />' +
          '</div>'
        ),
        $emptyCountry = $(
          '<div class="added-country">' +
            '<input type="text" value="" class="country-autocomplete" />' +
          '</div>'
        );

    it("Should remove the country element from the document if its textbox is empty", function () {
      $(document.body).append($emptyCountry);

      expect($('.added-country').length).toEqual(1);
      GOVUK.registerToVote.OtherCountryFields.prototype.removeEmptyCountry($emptyCountry);
      expect($('.added-country').length).toEqual(0);
    });

    it("Should leave the country element in the document if its textbox is not empty", function () {
      $(document.body).append($filledCountry);

      expect($('.added-country').length).toEqual(1);
      GOVUK.registerToVote.OtherCountryFields.prototype.removeEmptyCountry($filledCountry);
      expect($('.added-country').length).toEqual(1);

      $filledCountry.remove();
    });

    it("Should return true if the country element has an empty textbox", function () {
      var result;

      $(document.body).append($emptyCountry);
      result = GOVUK.registerToVote.OtherCountryFields.prototype.removeEmptyCountry($emptyCountry);
      expect(result).toBe(true);
    });

    it("Should return false if the country element has an filled textbox", function () {
      var result;

      $(document.body).append($filledCountry);
      result = GOVUK.registerToVote.OtherCountryFields.prototype.removeEmptyCountry($filledCountry);
      expect(result).toBe(false);

      $filledCountry.remove();
    });
  });

  describe("MakeCountryHTML method", function () {
    var otherCountriesMock = {
          'getFieldId' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId,
          'getFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName,
          'getValidationName' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName,
          'getCountryFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldName,
          'getCountryFieldRemoveText' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldRemoveText,
          'getRemoveLink' : GOVUK.registerToVote.OtherCountryFields.prototype.getRemoveLink,
          'templates' : GOVUK.registerToVote.OtherCountryFields.prototype.templates,
          'countries' : []
        };

    it("Should return HTML for the next country element in the sequence when passed no parameters", function () {
      var countryHTML,
          expectedHTML;

      expectedHTML = Mustache.render(countryHTMLTemplate, {
        'id' : otherCountriesMock.getFieldId(0),
        'name' : otherCountriesMock.getFieldName(0),
        'data-validation-name' : otherCountriesMock.getValidationName(0),
        'value' : '',
        'idx' : '0',
        'countryFieldName' : otherCountriesMock.getCountryFieldName
      });
      countryHTML = GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML.call(otherCountriesMock);
      expect(countryHTML).toEqual(expectedHTML);
    });

    it("Should return HTML for a country element with a set index when passed the options for that index", function () {
      var countryHTML,
          expectedHTML;

      expectedHTML = Mustache.render(countryHTMLTemplate, {
        'id' : otherCountriesMock.getFieldId(4),
        'name' : otherCountriesMock.getFieldName(4),
        'data-validation-name' : otherCountriesMock.getValidationName(4),
        'value' : 'Germany',
        'idx' : '4',
        'countryFieldName' : otherCountriesMock.getCountryFieldName,
        'remove-link' : otherCountriesMock.getRemoveLink(4)
      });
      countryHTML = GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML.call(otherCountriesMock, {
        'idx' : 4,
        'value' : 'Germany',
        'include-remove-link' : true
      });
      expect(countryHTML).toEqual(expectedHTML);
    });

    it("Should include a 'remove' link in the returned HTML if this is set in the options", function () {
      var countryHTML,
          expectedHTML;

      expectedHTML = Mustache.render(countryHTMLTemplate, {
        'id' : otherCountriesMock.getFieldId(1),
        'name' : otherCountriesMock.getFieldName(1),
        'data-validation-name' : otherCountriesMock.getValidationName(1),
        'value' : 'Germany',
        'idx' : '1',
        'countryFieldName' : otherCountriesMock.getCountryFieldName,
        'remove-link' : otherCountriesMock.getRemoveLink(1)
      });
      countryHTML = GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML.call(otherCountriesMock, {
        'idx' : 1,
        'value' : 'Germany',
        'include-remove-link' : true
      });
      expect(countryHTML).toEqual(expectedHTML);
    });
  });

  describe("AddCountryElements method", function () {
    var otherCountryFieldsMock = {
      'makeCountryHTML' : GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML,
      'getFieldId' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId,
      'getFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName,
      'getValidationName' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName,
      'getCountryFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldName,
      'getCountryFieldRemoveText' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldRemoveText,
      'getRemoveLink' : GOVUK.registerToVote.OtherCountryFields.prototype.getRemoveLink,
      'templates' : GOVUK.registerToVote.OtherCountryFields.prototype.templates,
      'getDummyFields' : GOVUK.registerToVote.OtherCountryFields.prototype.getDummyFields
    }

    beforeEach(function () {
      otherCountryFieldsMock.$addAnotherLink = $('<a href="" class="duplicate-control" data-validation-name="otherCountries">Add another country</a>');
      otherCountryFieldsMock.$container = $('<div id="add-countries" />');

      otherCountryFieldsMock.$container.append(otherCountryFieldsMock.$addAnotherLink);
      $(document.body).append(otherCountryFieldsMock.$container);
    });

    afterEach(function () {
      otherCountryFieldsMock.$addAnotherLink.remove();
      otherCountryFieldsMock.$container.remove();
    });

    it("Should add country elements to match the entries in the countries array", function () {
      otherCountryFieldsMock.countries = ['Belgium', 'Germany', 'Sweden'];

      GOVUK.registerToVote.OtherCountryFields.prototype.addCountryElements.call(otherCountryFieldsMock);
      expect($('.added-country input.country-autocomplete').eq(0).val()).toEqual('Belgium');
      expect($('.added-country input.country-autocomplete').eq(1).val()).toEqual('Germany');
      expect($('.added-country input.country-autocomplete').eq(2).val()).toEqual('Sweden');
    });

    it("Should remove the 'Add another country' link if the new country is the third", function () {
      otherCountryFieldsMock.countries = ['Belgium', 'Germany', 'Sweden'];

      GOVUK.registerToVote.OtherCountryFields.prototype.addCountryElements.call(otherCountryFieldsMock);
      expect(otherCountryFieldsMock.$container.find('.duplicate-control').length).toEqual(0);
    });
  });

  describe("GetDummyFields method", function () {
    var otherCountryFieldsMock;

    beforeEach(function () {
      otherCountryFieldsMock = {
        'countries' : [],
        'templates' : GOVUK.registerToVote.OtherCountryFields.prototype.templates,
        'getFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName
      };
    });

    it("Should add a dummy fields until the total of fields is 3", function () {
      var dummyFields; 

      otherCountryFieldsMock.countries = ['Belgium'];
      dummyFields = GOVUK.registerToVote.OtherCountryFields.prototype.getDummyFields.call(otherCountryFieldsMock);
      expect($(dummyFields).length).toEqual(2);
    });

    it("Should add dummy fields with the correct names", function () {
      var dummyFields; 

      otherCountryFieldsMock.countries = ['Belgium'];
      dummyFields = GOVUK.registerToVote.OtherCountryFields.prototype.getDummyFields.call(otherCountryFieldsMock);
      expect($(dummyFields).eq(0).attr('name')).toEqual(otherCountryFieldsMock.getFieldName(1));
    });
  });

  describe("UpdateValidation method", function () {
    var $container,
        countries,
        validationObjectMock;

    beforeEach(function () {
      var idx,
          opts;

      countries = ['Belgium', 'Germany', 'Poland'];
      validationObjectMock = { children : [] };
      $container = $('<div id="add-countries" data-validation-name="otherCountries" />');
      for (idx = 0; idx < 3; idx++) {
        opts = {
          'idx' : idx,
          'value' : countries[idx],
        };
        if (idx > 0) { opts['include-remove-link'] = true; }
        $container.append(GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML(opts));
        validationObjectMock.children.push(GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName(idx));
      }
      $(document.body).append($container);
    });

    afterEach(function () {
      $container.remove();
    });

    it("Should update the validation objects for all country elements", function () {
      var evt = {},
          $target = $container;

      $addedCountries = $container.find('.added-country');
      spyOn(GOVUK.registerToVote.validation.fields, 'getNames').and.callFake(function () {
        return [validationObjectMock];
      });
      spyOn(GOVUK.registerToVote.validation.fields, 'remove');
      spyOn(GOVUK.registerToVote.validation.fields, 'add');

      GOVUK.registerToVote.OtherCountryFields.prototype.updateValidation(evt, $target);
      expect(GOVUK.registerToVote.validation.fields.getNames).toHaveBeenCalledWith(['otherCountries']);
      expect(GOVUK.registerToVote.validation.fields.remove.calls.argsFor(0)[0]).toEqual(validationObjectMock.children[0]);
      expect(GOVUK.registerToVote.validation.fields.remove.calls.argsFor(1)[0]).toEqual(validationObjectMock.children[1]);
      expect(GOVUK.registerToVote.validation.fields.remove.calls.argsFor(2)[0]).toEqual(validationObjectMock.children[2]);
      expect(GOVUK.registerToVote.validation.fields.add.calls.argsFor(0)[0][0]).toEqual($addedCountries.eq(0).find('input.country-autocomplete')[0]);
      expect(GOVUK.registerToVote.validation.fields.add.calls.argsFor(1)[0][0]).toEqual($addedCountries.eq(1).find('input.country-autocomplete')[0]);
      expect(GOVUK.registerToVote.validation.fields.add.calls.argsFor(2)[0][0]).toEqual($addedCountries.eq(2).find('input.country-autocomplete')[0]);
    });
  });

  describe("AddCountry method", function () {
    var otherCountryFieldsMock = {
          'makeCountryHTML' : GOVUK.registerToVote.OtherCountryFields.prototype.makeCountryHTML,
          'getFieldId' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldId,
          'getFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getFieldName,
          'getValidationName' : GOVUK.registerToVote.OtherCountryFields.prototype.getValidationName,
          'addCountryElements'  : GOVUK.registerToVote.OtherCountryFields.prototype.addCountryElements,
          'updateCountryValues' : GOVUK.registerToVote.OtherCountryFields.prototype.updateCountryValues,
          'getCountryFieldName' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldName,
          'getCountryFieldRemoveText' : GOVUK.registerToVote.OtherCountryFields.prototype.getCountryFieldRemoveText,
          'getRemoveLink' : GOVUK.registerToVote.OtherCountryFields.prototype.getRemoveLink,
          'templates' : GOVUK.registerToVote.OtherCountryFields.prototype.templates,
          'getDummyFields' : GOVUK.registerToVote.OtherCountryFields.prototype.getDummyFields
        };

    beforeEach(function () {
      otherCountryFieldsMock.$addAnotherLink = $('<a href="" class="duplicate-control">Add another country</a>');
      otherCountryFieldsMock.$container = $('<div id="add-countries" />')
        .append(otherCountryFieldsMock.$addAnotherLink);
      $(document.body).append(otherCountryFieldsMock.$container);
    });

    afterEach(function () {
      otherCountryFieldsMock.$addAnotherLink.remove();
      otherCountryFieldsMock.$container.remove();
    });

    it("Should add a country element to the container element", function () {
      otherCountryFieldsMock.countries = [];
      spyOn(otherCountryFieldsMock, 'updateCountryValues');
      spyOn(otherCountryFieldsMock, 'addCountryElements').and.callThrough();

      expect($('.added-country').length).toEqual(0);
      expect(otherCountryFieldsMock.countries.length).toEqual(0);
      GOVUK.registerToVote.OtherCountryFields.prototype.addCountry.call(otherCountryFieldsMock);
      expect($('.added-country').length).toEqual(1);
      expect(otherCountryFieldsMock.countries.length).toEqual(1);
    });

    it("Should add a 'remove' link to the first country element if there are more than 1", function () {
      var $firstCountryElement;

      otherCountryFieldsMock.countries = ['Belgium'];
      $(otherCountryFieldsMock.makeCountryHTML()).insertBefore(otherCountryFieldsMock.$addAnotherLink);
      otherCountryFieldsMock.$container.find('.remove-field').eq(0).remove();
      $firstCountryElement = otherCountryFieldsMock.$container.find('.added-country');

      expect($firstCountryElement.length).toEqual(1);
      expect($firstCountryElement.find('.remove-field').length).toEqual(0);
      GOVUK.registerToVote.OtherCountryFields.prototype.addCountry.call(otherCountryFieldsMock);
      expect(otherCountryFieldsMock.$container.find('.remove-field').length).toEqual(2)
    });

    it("Should call the 'contentUpdate' event when a country is added", function () {
      var eventCalled = false;

      otherCountryFieldsMock.countries = [];
      spyOn(otherCountryFieldsMock, 'updateCountryValues');
      spyOn(otherCountryFieldsMock, 'addCountryElements').and.callThrough();
      spyOn($.fn, "trigger").and.callFake(function (e, eData) {
        if ((this[0] === document) && (eData.context[0] === otherCountryFieldsMock.$container[0])) {
          eventCalled = true;
        }
      });

      GOVUK.registerToVote.OtherCountryFields.prototype.addCountry.call(otherCountryFieldsMock);
      expect(eventCalled).toBe(true);
    });
  });

  describe("RemoveCountry method", function () {
    var otherCountryFieldsMock = $.extend({}, GOVUK.registerToVote.OtherCountryFields.prototype),
        $removeLink;

    beforeEach(function () {
      otherCountryFieldsMock.countries = [];
      otherCountryFieldsMock.$addAnotherLink = $('<a class="duplicate-control" href="#">Add another country</a>');
      otherCountryFieldsMock.$container = $('<div id="add-countries" />');
    });

    afterEach(function () {
      otherCountryFieldsMock.$container.remove();
    });

    it("Should remove a country element when passed its 'Remove' link as the parameter", function () {
      var countryData1 = {
            'idx' : '0',
            'value' : 'Belgium',
            'include-remove-link' : true
          },
          countryData2 = {
            'idx' : '0',
            'value' : 'Germany',
            'include-remove-link' : true
          },
          $countryElement1 = $(otherCountryFieldsMock.makeCountryHTML(countryData1));
          $countryElement2 = $(otherCountryFieldsMock.makeCountryHTML(countryData2));

      $removeLink = $countryElement2.find('.remove-field');
      otherCountryFieldsMock.countries = ['Belgium', 'Germany'];
      otherCountryFieldsMock.$container.append($countryElement1);
      otherCountryFieldsMock.$container.append($countryElement2);
      $(document.body).append(otherCountryFieldsMock.$container);

      expect($('.added-country').length).toEqual(2);
      GOVUK.registerToVote.OtherCountryFields.prototype.removeCountry.call(otherCountryFieldsMock, $removeLink);
      expect($('.added-country').length).toEqual(1);
    }); 

    it("Should create a country element without a 'remove' link when it is the only one left", function () {
      var countryData1 = {
            'idx' : '0',
            'value' : 'Belgium',
            'include-remove-link' : true
          },
          countryData2 = {
            'idx' : '0',
            'value' : 'Germany',
            'include-remove-link' : true
          },
          $countryElement1 = $(otherCountryFieldsMock.makeCountryHTML(countryData1));
          $countryElement2 = $(otherCountryFieldsMock.makeCountryHTML(countryData2));

      $removeLink = $countryElement2.find('.remove-field');
      otherCountryFieldsMock.countries = ['Belgium', 'Germany'];
      otherCountryFieldsMock.$container.append($countryElement1);
      otherCountryFieldsMock.$container.append($countryElement2);
      $(document.body).append(otherCountryFieldsMock.$container);
      
      expect($('.added-country').eq(0).find('.remove-field').length).toEqual(1);
      GOVUK.registerToVote.OtherCountryFields.prototype.removeCountry.call(otherCountryFieldsMock, $removeLink);
      expect($('.added-country').eq(0).find('.remove-field').length).toEqual(0);
    });

    it("Should leave the set in the correct order when removing a country element from the middle", function () {
      var countryNames = ['Belgium', 'France', 'Germany', 'Sweden'],
          countryData,
          countryHTML,
          existingCountryValues,
          idx;
      
      for (idx = 0; idx < 4; idx++) {
        countryData = {
          'idx' : idx,
          'value' : countryNames[idx]
        };
        if (idx > 0) { countryData['include-remove-link'] = true; }
        countryHTML = $(otherCountryFieldsMock.makeCountryHTML(countryData));
        otherCountryFieldsMock.$container.append(countryHTML);
      }
      $removeLink = otherCountryFieldsMock.$container.find('.remove-field').eq(1);
      $(document.body).append(otherCountryFieldsMock.$container);

      expect($('.added-country').length).toEqual(4);
      existingCountryValues = $.map($('.added-country input.country-autocomplete'), function (elm, idx) {
        return $(elm).val();
      });
      expect(existingCountryValues[2]).toEqual('Germany');
      GOVUK.registerToVote.OtherCountryFields.prototype.removeCountry.call(otherCountryFieldsMock, $removeLink);
      expect($('.added-country').length).toEqual(3);
      existingCountryValues = $.map($('.added-country input.country-autocomplete'), function (elm, idx) {
        return $(elm).val();
      });
      expect(existingCountryValues).toEqual(['Belgium', 'France', 'Sweden']);
    });

    it("Should call the 'contentRemoval' event when a country element is removed", function () {
      var countryData1 = {
            'idx' : 0,
            'value' : 'Belgium'
          },
          countryData2 = {
            'idx' : 1,
            'value' : 'Germany',
            'include-remove-link' : true
          },
          $countryElement1 = $(otherCountryFieldsMock.makeCountryHTML(countryData1)),
          $countryElement2 = $(otherCountryFieldsMock.makeCountryHTML(countryData2)),
          eventCalled = false;

      $removeLink = $countryElement2.find('.remove-field');
      otherCountryFieldsMock.countries = ['Belgium', 'Germany'];
      otherCountryFieldsMock.$container.append($countryElement1);
      otherCountryFieldsMock.$container.append($countryElement2);
      $(document.body).append(otherCountryFieldsMock.$container);
      spyOn($.fn, "trigger").and.callFake(function (e, eData) {
        if ((this[0] === document) && (eData.context[0] === otherCountryFieldsMock.$container[0])) {
          eventCalled = true;
        }
      });

      GOVUK.registerToVote.OtherCountryFields.prototype.removeCountry.call(otherCountryFieldsMock, $removeLink);
      expect(eventCalled).toBe(true);
    });
  });
});

describe("MarkSelected", function () {
  describe("Creating an instance", function () {
    var $radioLabel,
        $checkboxLabel;

    beforeEach(function () {
      $radioLabel = $(
        "<label for='field_1'>" +
          "<input type='radio' id='field_1' name='field_1' />" +
        "</label>"
      );
      $checkboxLabel = $(
        "<label for='field_1'>" +
          "<input type='checkbox' id='field_1' name='field_1' />" +
        "</label>"
      );
    });

    it("Should work with both radios and checkboxes", function () {
      var createInstance = function ($elm) {
            return new GOVUK.registerToVote.MarkSelected($elm);
          };

      expect(function () { createInstance($radioLabel) }).not.toThrow();
      expect(function () { createInstance($checkboxLabel) }).not.toThrow();
    });

    it("Should have the right interface", function () {
      var selectable;

      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(selectable.toggle).toBeDefined();
    });

    it("Should add a custom event to the document if selectable contains a radio", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(elementBoundTo).toBe(document);
      expect(eventBound).toBe("radio:" + $radioLabel.find("input").attr("name"));
    });

    it("The custom event should call the toggle method", function () {
      var inputName = $radioLabel.find("input").attr("name"),
          eventName = "radio:" + inputName,
          selectable,
          eventCallback;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          if ((evt === eventName) && (this[0] === document)) {
            eventCallback = callback;
          }
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      spyOn(selectable, "toggle");
      eventCallback(eventName, { "selectedRadio" : $radioLabel });
      expect(selectable.toggle).toHaveBeenCalled();
    });

    it("Should add a click event to the selectable if it contains a checkbox", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      expect(elementBoundTo).toBe($checkboxLabel.find('input')[0]);
      expect(eventBound).toBe("click");
    });

    it("Click event should call the toggle method", function () {
      var selectable,
          evtCallback,
          control = $checkboxLabel.find('input')[0];

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          if (evt === 'click' && this[0] === control) {
            evtCallback = callback;
          }
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      spyOn(selectable, "toggle");
      evtCallback();
      expect(selectable.toggle).toHaveBeenCalled();
    });

    it("Should add a class to the selectable if it is already chosen", function () {
      var selectedRadio;

      $radioLabel.find("input").attr("checked", true);
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect($radioLabel.hasClass("selected")).toBe(true);
    });
  });
});

describe("monitorRadios", function () {
  var $radioGroup;

  beforeEach(function () {
    $radioGroup = $(
      "<fieldset>" +
        "<input type='radio' id='field_1' name='field_1' />" +
        "<input type='radio' id='field_2' name='field_1' />" +
      "</fieldset>" 
    );
    GOVUK.registerToVote.monitorRadios.radioGroups = [];
  });

  it("Should set a change event on the fieldset of the radio sent in", function () {
    var radio = $radioGroup.find('#field_1')[0],
        evtCalledOn,
        evtSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        evtCalledOn = this[0];
        evtSet = evt;
        return this;
      }
    );
    GOVUK.registerToVote.monitorRadios(radio);
    expect($.fn.on).toHaveBeenCalled();
    expect(evtCalledOn).toBe($radioGroup[0]);
    expect(evtSet).toBe("click change");
  });

  it("Should trigger a custom event when an radio in the fieldset changes state", function () {
    var radio = $radioGroup.find("#field_1")[0],
        evtSet,
        callbackSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        callbackSet = callback;
      }
    );
    spyOn($.fn, "trigger").and.callFake(
      function (evt, evtData) {
        if (this[0] === document) {
          evtSet = evt;
          dataSent = evtData;
        }
      }
    );
    GOVUK.registerToVote.monitorRadios(radio);
    callbackSet.call($radioGroup[0], { "target" : radio });
    expect($.fn.trigger).toHaveBeenCalled();
    expect(evtSet).toBe("radio:" + radio.name);
    expect(dataSent).toEqual({
      "selectedControl" : radio,
      "fieldset" : $radioGroup[0]
    });
  });

  it("Should only bind one custom event for each group of radios", function () {
    var radio1 = $radioGroup.find('#field_1')[0],
        radio2 = $radioGroup.find('#field_2')[0],
        evtCalledOn,
        evtSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        evtCalledOn = this[0];
        evtSet = evt;
        return this;
      }
    );

    GOVUK.registerToVote.monitorRadios(radio1);
    GOVUK.registerToVote.monitorRadios(radio2);
    expect(GOVUK.registerToVote.monitorRadios.radioGroups.length).toEqual(1);
  });
});

describe("MarkSelected", function () {
  describe("Constructor", function () {
    var $radioLabel,
        $checkboxLabel;

    beforeEach(function () {
      $radioLabel = $(
        "<label for='field_1'>" +
          "<input type='radio' id='field_1' name='field_1' />" +
        "</label>"
      );
      $checkboxLabel = $(
        "<label for='field_1'>" +
          "<input type='checkbox' id='field_1' name='field_1' />" +
        "</label>"
      );
    });

    it("Should work with both radios and checkboxes", function () {
      var createInstance = function ($elm) {
            return new GOVUK.registerToVote.MarkSelected($elm);
          };

      expect(function () { createInstance($radioLabel) }).not.toThrow();
      expect(function () { createInstance($checkboxLabel) }).not.toThrow();
    });

    it("Should produce an instance with the correct interface", function () {
      var selectable;

      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(selectable.toggle).toBeDefined();
    });

    it("Should add a custom event to the document if selectable contains a radio", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(elementBoundTo).toBe(document);
      expect(eventBound).toBe("radio:" + $radioLabel.find("input").attr("name"));
    });

    it("The custom event should call the toggle method", function () {
      var inputName = $radioLabel.find("input").attr("name"),
          eventName = "radio:" + inputName,
          selectable,
          eventCallback;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          if ((evt === eventName) && (this[0] === document)) {
            eventCallback = callback;
          }
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      spyOn(selectable, "toggle");
      eventCallback(eventName, { "selectedControl" : $radioLabel });
      expect(selectable.toggle).toHaveBeenCalled();
    });

    it("Should add a click event to the selectable if it contains a checkbox", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      expect(elementBoundTo).toEqual($checkboxLabel.find('input')[0]);
      expect(eventBound).toBe("click");
    });

    it("Click event should call the toggle method", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      expect(elementBoundTo).toBe($checkboxLabel.find('input')[0]);
      expect(eventBound).toBe("click");
    });

    it("Should add a class to the selectable if it is already chosen", function () {
      var selectedControl;

      $radioLabel.find("input").attr("checked", true);
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect($radioLabel.hasClass("selected")).toBe(true);
    });
  });

  describe("toggle method", function () {
    var $radioFieldset,
        $checkboxFieldset,
        MarkSelectedMock,
        eventDataMock;

    beforeEach(function () {
      $radioFieldset = $(
        "<fieldset>" +
          "<label for='field_1'>" +
            "<input type='radio' id='field_1' name='field_1' />" +
          "</label>" +
          "<label for='field_2'>" +
            "<input type='radio' id='field_2' name='field_2' />" +
          "</label>" +
          "<label for='field_3'>" +
            "<input type='radio' id='field_3' name='field_3' />" +
          "</label>" +
        "</fieldset>"
      );
      $checkboxFieldset = $(
        "<fieldset>" +
          "<label for='field_1'>" +
            "<input type='checkbox' id='field_1' name='field_1' />" +
          "</label>" +
          "<label for='field_2'>" +
            "<input type='checkbox' id='field_2' name='field_2' />" +
          "</label>" +
        "</fieldset>"
      );
    });
    
    it("Should add the 'selected' class to a radio if called by a click which selects it", function () {
      var $control = $radioFieldset.find('#field_1'),
          $label = $control.parent('label');

      $control.attr('checked', true);
      MarkSelectedMock = {
        "$control" : $control,
        "$label" : $label
      };
      eventDataMock = {
        "selectedControl" : $control[0],
        "fieldset" : $radioFieldset
      };
      GOVUK.registerToVote.MarkSelected.prototype.toggle.call(MarkSelectedMock, eventDataMock);
      expect($label.hasClass("selected")).toBe(true);
    });

    it("Should add the 'selected' class to a checkbox if it sent in as checked", function () {
      var $control = $checkboxFieldset.find('#field_1'),
          $label = $control.parent('label');

      MarkSelectedMock = {
        "$control" : $control,
        "$label" : $label
      };
      eventDataMock = {
        "selectedControl" : $control[0]
      };
      $control.attr('checked', true);
      GOVUK.registerToVote.MarkSelected.prototype.toggle.call(MarkSelectedMock, eventDataMock);
      expect($label.hasClass('selected')).toEqual(true);
    });

    it("Should remove the 'selected' class from a checkbox if it sent in as not checked", function () {
      var $control = $checkboxFieldset.find('#field_1'),
          $label = $control.parent('label');

      MarkSelectedMock = {
        "$control" : $control,
        "$label" : $label
      };
      eventDataMock = {
        "selectedControl" : $control[0]
      };
      $control.attr('checked', false);
      GOVUK.registerToVote.MarkSelected.prototype.toggle.call(MarkSelectedMock, eventDataMock);
      expect($label.hasClass('selected')).toEqual(false);
    });
  });
});

describe("Autocomplete", function () {
  describe("Constructor", function () {
    var $input;

    beforeEach(function () {
      $input = $("<input type='text' id='field_1' name='field_1' />");
    });

    it("Should produce an instance with the correct interface", function () {
      var autocomplete;

      autocomplete = new GOVUK.registerToVote.Autocomplete($input);
      expect(autocomplete.getMenuId).toBeDefined();
      expect(autocomplete.compiledStatusText).toBeDefined();
      expect(autocomplete.compiledTemplate).toBeDefined();
      expect(autocomplete.updateStatus).toBeDefined();
      expect(autocomplete.events).toBeDefined();
      expect(autocomplete.getAutocompleteObj).toBeDefined();
    });

    it("Should initialize the textbox as a typeahead instance", function () {
      var autocomplete;

      spyOn($.fn, "typeahead");
      autocomplete = new GOVUK.registerToVote.Autocomplete($input);
      expect($.fn.typeahead).toHaveBeenCalled();
    });
  });

  describe("getMenuId method", function () {
    it("Should return the correct menu id", function () {
      var menuId = GOVUK.registerToVote.Autocomplete.prototype.getMenuId();

      expect(menuId).toEqual(GOVUK.registerToVote.Autocomplete.menuIdPrefix + '-1');
    });

    it("Should return a different menu id when called again", function () {
      var menuId = GOVUK.registerToVote.Autocomplete.prototype.getMenuId();

      expect(menuId).toEqual(GOVUK.registerToVote.Autocomplete.menuIdPrefix + '-2');
    });
  });

  describe("updateStatus method", function () {
    var AutocompleteMock,
        statusTextOneSuggestion = '1 result is available, use up and down arrow keys to navigate.',
        statusTextTwoSuggestions = '2 results are available, use up and down arrow keys to navigate.';

    beforeEach(function () {
      AutocompleteMock = {
        "$status" : $("<p>Default text</p>"),
        "compiledStatusText" : GOVUK.registerToVote.Autocomplete.prototype.compiledStatusText
      };
    });

    it("Should update to the correct status text for 1 result", function () {
      GOVUK.registerToVote.Autocomplete.prototype.updateStatus.call(AutocompleteMock, ['Angola']);
      expect(AutocompleteMock.$status.text()).toEqual(statusTextOneSuggestion);
    });

    it("Should update to the correct status text for 2 results", function () {
      GOVUK.registerToVote.Autocomplete.prototype.updateStatus.call(AutocompleteMock, ['Angola', 'Argentina']);
      expect(AutocompleteMock.$status.text()).toEqual(statusTextTwoSuggestions);
    });

    it("Should not update the status text for 0 results", function () {
      GOVUK.registerToVote.Autocomplete.prototype.updateStatus.call(AutocompleteMock, []);
      expect(AutocompleteMock.$status.text()).toEqual('Default text');
    });
  });

  describe("events property", function () {
    describe("onInitialized method", function () {
      var elementMock,
          AutocompleteMock;

      beforeEach(function () {
        elementMock = $(
          "<div>" +
            "<input type='text' value='France' />" +
            "<div class='tt-dropdown-menu'></div>" +
          "</div>"
        );
        AutocompleteMock = {
          "getAutocompleteObj" : function () {
            return {};
          },
          "$input" : elementMock.find('input'),
          "getMenuId" : function () {
            return "menu-id"
          }
        };
      });

      it("Should set up the status text correctly", function () {
        GOVUK.registerToVote.Autocomplete.prototype.events.onInitialized.call(AutocompleteMock, {
          'target' : AutocompleteMock.$input
        });
        expect(AutocompleteMock.$status).toBeDefined();
        expect(AutocompleteMock.$status.attr('role')).toEqual('status');
        expect(AutocompleteMock.$status.attr('aria-live')).toEqual('polite');
        expect(AutocompleteMock.$status.attr('class')).toEqual('typeahead-status visuallyhidden');
        expect(AutocompleteMock.$input.next()[0]).toEqual(AutocompleteMock.$status[0]);
      });

      it("Should add the correct ARIA attributes to the textbox", function () {
        GOVUK.registerToVote.Autocomplete.prototype.events.onInitialized.call(AutocompleteMock, {
          'target' : AutocompleteMock.$input
        });
        
        expect(AutocompleteMock.$input.attr('aria-autocomplete')).toEqual('list');
        expect(AutocompleteMock.$input.attr('aria-haspopup')).toEqual('menu-id');
      });

      it("Should set up the menu element", function () {
        GOVUK.registerToVote.Autocomplete.prototype.events.onInitialized.call(AutocompleteMock, {
          'target' : AutocompleteMock.$input
        });
        
        expect(AutocompleteMock.$menu).toBeDefined();
        expect(AutocompleteMock.$menu.attr('id')).toEqual('menu-id');
      });

      it("Should set a keydown event on the textbox", function () {
        var eventCalled = false;

        spyOn($.fn, "on").and.callFake(
          function (evt, callback) {
            if ((this[0] === AutocompleteMock.$input[0]) && (evt === 'keydown')) {
              eventCalled = true;
            }
          }
        );
        GOVUK.registerToVote.Autocomplete.prototype.events.onInitialized.call(AutocompleteMock, {
          'target' : AutocompleteMock.$input
        });
        expect(eventCalled).toBe(true);
      });
    });

    describe("onMoveTo method", function () {
      it("Should set the aria-activedescendant attribute to the suggestion the cursor is on", function () {
        var $input = $("<input type='text' value='France' />"),
            AutocompleteMock = {
              '$input' : $input
            },
            countryObj = {
              value: "Albania" 
            },
            evtObj = {};

        GOVUK.registerToVote.Autocomplete.prototype.events.onMoveTo.apply(AutocompleteMock, [evtObj, countryObj]);
        expect($input.attr('aria-activedescendant')).toEqual('Albania');
      });
    });

    describe("onEnter method", function () {
      it("Should hide the menu when enter is pressed", function () {
        var AutocompleteMock = {
              'menuIsShowing' : true,
              '$menu' : $('<div />')
            },
            evtObj = {};

        GOVUK.registerToVote.Autocomplete.prototype.events.onEnter.call(AutocompleteMock, evtObj);
        expect(AutocompleteMock.$menu.css('display')).toEqual('none');
      });
    });

    describe("onUpdate method", function () {
      it("Should update the status element", function () {
        var AutocompleteMock = {
              "updateStatus" : jasmine.createSpy("AutocompleteMock.updateStatus")
            };

        GOVUK.registerToVote.Autocomplete.prototype.events.onUpdate.call(AutocompleteMock);
        expect(AutocompleteMock.updateStatus).toHaveBeenCalled();
      });
    });
  });

  describe("getAutocompleteObj method", function () {
    it("Should call the existingObj method of GOVUK.registerToVote.autocompletes to get an Autocomplete instance", function () {
      var cachedAutocompletes = GOVUK.registerToVote.autocompletes,
          $input = {};

      GOVUK.registerToVote.autocompletes = {
        "existingObj" : jasmine.createSpy("GOVUK.registerToVote.autocompletes.existingObj")
      };
      GOVUK.registerToVote.Autocomplete.prototype.getAutocompleteObj.call(GOVUK.registerToVote.autocompletes, $input);
      expect(GOVUK.registerToVote.autocompletes.existingObj).toHaveBeenCalledWith($input);
      GOVUK.registerToVote.autocompletes = cachedAutocompletes;
    });
  });
});

describe("autocompletes", function () {
  var instanceMock,
      autoCompleteMock,
      $input;

  describe("existingId method", function () {
    it("Should get an id if an instance has been created for the textbox sent in", function () {
      var instanceId;

      autocompletesMock = {
        'cache' : { 'field_1' : {} }
      };
      $input = $("<input type='text' id='field_1' />");
      instanceId = GOVUK.registerToVote.autocompletes.existingId.call(autocompletesMock, $input);
      expect(instanceId).toEqual('field_1');
    });

    it("Should return false if no instance exists", function () {
      var instanceId;
      
      autocompletesMock = {
        'cache' : {}
      };
      $input = $("<input type='text' id='field_1' />");
      instanceId = GOVUK.registerToVote.autocompletes.existingId.call(autocompletesMock, $input);
      expect(instanceId).toEqual(false);
    });
  });

  describe("existingObj method", function () {
    it("Should get an instance if an one has been created for the textbox sent in", function () {
      var instance;

      instanceMock = {};
      autocompletesMock = {
        'cache' : { 'field_1' : instanceMock }
      };
      $input = $("<input type='text' id='field_1' />");
      instance = GOVUK.registerToVote.autocompletes.existingObj.call(autocompletesMock, $input);
      expect(instance).toBe(instanceMock);
    });
  });

  describe("createEvent method", function () {
    var createdEvent;

    beforeEach(function () {
      instanceMock = {
        'events' : {
          'onInitialized' : jasmine.createSpy('instanceMock.events.onInitialized'),
          'onMenuOpen' : jasmine.createSpy('instanceMock.events.onMenuOpen'),
          'onMenuClosed' : jasmine.createSpy('instanceMock.events.onMenuClosed'),
          'onMoveTo' : jasmine.createSpy('instanceMock.events.onMoveTo'),
          'onUpdate' : jasmine.createSpy('instanceMock.events.onUpdate'),
        }
      };
      spyOn(GOVUK.registerToVote, "Autocomplete").and.callFake(
        function () {
          return instanceMock;
        }
      );
      $input = $("<input type='text' id='field_1' />");
      GOVUK.registerToVote.autocompletes.add($input);
    });

    afterEach(function () {
      GOVUK.registerToVote.autocompletes.remove($input);
    });

    it("Can create an object with a trigger method that calls 'onInitialized' on the instance for the textbox you send it", function () {
      createdEvent = GOVUK.registerToVote.autocompletes.createEvent('initialized');
      createdEvent.trigger({ 'target' : $input[0] });
      expect(instanceMock.events.onInitialized).toHaveBeenCalled();
    });

    it("Can create an object with a trigger method that calls 'onMenuOpen' on the instance for the textbox you send it", function () {
      createdEvent = GOVUK.registerToVote.autocompletes.createEvent('opened');
      createdEvent.trigger({ 'target' : $input[0] });
      expect(instanceMock.events.onMenuOpen).toHaveBeenCalled();
    });

    it("Can create an object with a trigger method that calls 'onMenuClosed' on the instance for the textbox you send it", function () {
      createdEvent = GOVUK.registerToVote.autocompletes.createEvent('closed');
      createdEvent.trigger({ 'target' : $input[0] });
      expect(instanceMock.events.onMenuClosed).toHaveBeenCalled();
    });

    it("Can create an object with a trigger method calls 'onMoveTo' on the instance for the textbox you send it", function () {
      createdEvent = GOVUK.registerToVote.autocompletes.createEvent('movedto');
      createdEvent.trigger({ 'target' : $input[0] });
      expect(instanceMock.events.onMoveTo).toHaveBeenCalled();
    });

    it("Can create an object with a trigger method that calls 'onUpdate' on the instance for the textbox you send it", function () {
      createdEvent = GOVUK.registerToVote.autocompletes.createEvent('updated');
      createdEvent.trigger({ 'target' : $input[0] });
      expect(instanceMock.events.onUpdate).toHaveBeenCalled();
    });
  });

  describe("add method", function () {
    beforeEach(function () {
      $input = $("<input type='text' id='field_1' />");
    });

    it("Should do nothing for a textbox with an autocomplete", function () {
      var cache = {};

      autocompletesMock = {
        'cache' : cache,
        'existingId' : function () { return 'field_1'; }
      };
      GOVUK.registerToVote.autocompletes.add.call(autocompletesMock, $input);
      expect(autocompletesMock.cache).toBe(cache);
    });

    it("Should create an instance for a textbox without an autocomplete", function () {
      var cache = {};

      autocompletesMock = {
        'cache' : cache,
        'existingId' : function () { return false; }
      };
      GOVUK.registerToVote.autocompletes.add.call(autocompletesMock, $input);
      expect(autocompletesMock.cache.field_1).toBeDefined();
    });
  });

  describe("remove method", function () {
    it("Should remove the instance created for the sent textbox", function () {
      $input = $("<input type='text' id='field_1' />");
      autocompletesMock = {
        'cache' : { 'field_1' : {} },
        'existingId' : function () { return 'field_1'; }
      };
      spyOn($input, 'typeahead');
      GOVUK.registerToVote.autocompletes.remove.call(autocompletesMock, $input);
      expect(autocompletesMock.cache.field_1).not.toBeDefined();
      expect($input.typeahead).toHaveBeenCalledWith('destroy');
    });
  });
});

describe("PostcodeLookup", function () {
  var $container,
      $targetElement,
      $searchButton,
      $searchInput,
      resultsTemplate = 
        '<label for="address_postcode" class="hidden">' +
           'Postcode' +
        '</label>' +
        '<input type="hidden" id="input-address-postcode" name="address.postcode" value="{{postcode}}" class="text hidden">' +
        '<label for="address_uprn_select">{{selectLabel}}</label>' +
        '<div class="validation-wrapper">' +
          '<select id="address_uprn_select" name="address.uprn" class="lonely validate" ' +
          'data-validation-name="addressSelect" data-validation-type="field" data-validation-rules="nonEmpty"' +
          '>' +
          '<option value="">{{defaultOption}}</option>' +
          '{{#options}}' +
            '<option value="{{uprn}}">{{addressLine}}</option>' +
          '{{/options}}' +
          '</select>' +
        '</div>' +
        '<div class="optional-section" id="cant-find-address">' +
          '<h2>{{excuseToggle}}</h2>' +
          '<label for="address_manualAddress">{{excuseLabel}}</label>' +
          '<textarea name="address.manualAddress" id="address_manualAddress" class="small validate" maxlength=500  autocomplete="off" ' +
          'data-validation-name="addressExcuse" data-validation-type="field" data-validation-rules="nonEmpty"' +
          '></textarea>' +
        '</div>' +
        '<input type="hidden" id="possibleAddresses_postcode" name="possibleAddresses.postcode" value="{{postcode}}" />' +
        '<input type="hidden" id="possibleAddresses_jsonList" name="possibleAddresses.jsonList" value="{{resultsJSON}}" />' +
        '<button type="submit" id="continue" class="button next validation-submit" data-validation-sources="postcode address">Continue</button>',
      addressData = [ 
        {
          addressLine: "Pool House, Elgar Business Centre, Moseley Road, Hallow, Worcester, Worcestershire",
          uprn: "26742626",
          postcode: "WR2 6NJ",
          manualAddress: null
        },
        {
          addressLine: "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester, Worcestershire",
          uprn: "52489478",
          postcode: "WR2 6NJ",
          manualAddress: null
        }
      ],
      templateData = {
        'postcode' : 'WR2 6NJ',
        'selectLabel' : 'Select your address',
        'defaultOption' : '2 addresses found',
        'options' : addressData,
        'excuseToggle' : "I can't find my address in the list",
        'excuseLabel' : 'Enter your address',
        'resultsJSON' : '{"addresses":[]}'
      },
      defaultResultsHTML;

  beforeEach(function () {
    defaultResultsHTML = Mustache.render(resultsTemplate, templateData);
    $container = $(
      "<fieldset>" +
        "<input type='text' id='postcode_name' class='postcode' />" +
        "<button type='submit' id='find-address' data-validation-sources='postcode'>Find address</button>" +
      "</fieldset>"
    );
    $targetElement = $("<div id='found-addresses' />");
    $submitButton = $("<button id='continue'>Continue</button>");
    $searchInput = $container.find('#postcode_name');
    $searchButton = $container.find('#find-address');
    $(document.body).append($container);
    $(document.body).append($targetElement);
    $(document.body).append($submitButton);
  });

  afterEach(function () {
    $container.remove();
    $targetElement.remove();
    $submitButton.remove();
  });

  describe("Constructor", function () {
    it("Should produce an instance with the correct interface", function () {
      var postcodeLookup = new GOVUK.registerToVote.PostcodeLookup($searchButton, 'address');

      expect(postcodeLookup.bindEvents).toBeDefined();
      expect(postcodeLookup.onTimeout).toBeDefined();
      expect(postcodeLookup.onError).toBeDefined();
      expect(postcodeLookup.addLookup).toBeDefined();
      expect(postcodeLookup.getAddresses).toBeDefined();
    });

    it("Should add the correct ARIA attributes to the 'Find address' button & the element the results are added to", function () {
      var postcodeLookup = new GOVUK.registerToVote.PostcodeLookup($searchButton, 'address');

      expect($searchButton.attr('aria-controls')).toEqual('found-addresses');
      expect($targetElement.attr('aria-live')).toEqual('polite');
      expect($targetElement.attr('aria-busy')).toEqual('false');
      expect($targetElement.attr('role')).toEqual('region');
    });

    it("Should hide the page submit button if the lookup is in an optional section that is visible", function () {
      var postcodeLookup;

      $container.wrap("<div class='optional-section' />");
      postcodeLookup = new GOVUK.registerToVote.PostcodeLookup($searchButton, 'address');

      expect($submitButton.css('display')).toEqual('none');
    });

    it("Should call the bindEvents method", function () {
      var postcodeLookupMock = {
        'bindEvents' : jasmine.createSpy('postcodeLookupMock.bindEvents')
      };

      GOVUK.registerToVote.PostcodeLookup.apply(postcodeLookupMock, [$searchButton, 'address']);

      expect(postcodeLookupMock.bindEvents).toHaveBeenCalled();
    });

    it("Should mark the type of address (previous or not)", function () {
      var postcodeLookup;

      $("<label for='postcode_name'>previous address</label>").insertBefore($searchInput);
      postcodeLookup = new GOVUK.registerToVote.PostcodeLookup($searchButton, 'address');

      expect(postcodeLookup.addressIsPrevious).toEqual(true);
    });
  });

  describe("bindEvents method", function () {
    it("Should add a click event to the 'Find address' button", function () {
      var postcodeLookup,
          eventCalled,
          elementEventCalledOn;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventCalled = evt;
          elementEventCalledOn = this[0];
          return this;
        }
      );
      var postcodeLookup = new GOVUK.registerToVote.PostcodeLookup($searchButton, 'address');

      expect(eventCalled).toEqual('click');
      expect(elementEventCalledOn).toBe($searchButton[0]);
    });
  });

  describe("addLookup method", function () {
    var postcodeLookupMock,
        previousAddressTemplateData = {
          'postcode' : 'WR2 6NJ',
          'selectLabel' : 'Select your previous address',
          'defaultOption' : '2 addresses found',
          'options' : addressData,
          'excuseToggle' : "I can't find my previous address in the list",
          'excuseLabel' : 'Enter your previous address',
          'resultsJSON' : '{"addresses":[]}'
        },
        previousAddressResultsHTML = Mustache.render(resultsTemplate, previousAddressTemplateData),
        $result;

    beforeEach(function () {
      $targetElement = $("<div id='found-addresses' />");
      postcodeLookupMock = {
        'addressIsPrevious' : false,
        'fragment' : resultsTemplate,
        '$targetElement' : $targetElement,
        'hasAddress' : false
      };
      $(document.body).append($targetElement); 
    });

    afterEach(function () {
      $targetElement.remove();
    });

    it("Should add 'contains-addresses' to the div that the new HTML is added to", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLDefault = $(
        '<div>' +
          '<form action="' + window.location + '" method="POST">' +
            defaultResultsHTML +
          '</form>' +
        '</div>'
      );

      GOVUK.registerToVote.OptionalInformation = function () {};
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(postcodeLookupMock.$targetElement.hasClass('contains-addresses')).toEqual(true);
    });

    it("Should call GOVUK.registerToVote.OptionalInformation on any optional content in the new HTML", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLDefault = $(
        '<div>' +
          '<form action="' + window.location + '" method="POST">' +
            defaultResultsHTML +
          '</form>' +
        '</div>'
      );

      spyOn(GOVUK.registerToVote, "OptionalInformation");
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(GOVUK.registerToVote.OptionalInformation).toHaveBeenCalled();
    });

    it("Should set the 'hasAddresses' property to true", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLDefault = $(
        '<div>' +
          '<form action="' + window.location + '" method="POST">' +
            defaultResultsHTML +
          '</form>' +
        '</div>'
      );

      GOVUK.registerToVote.OptionalInformation = function () {};
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(postcodeLookupMock.hasAddresses).toEqual(true);
    });

    it("Should add the correct HTML to the targeted div if it's not in any optional sections", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLDefault = $(
        '<div>' +
          '<form action="' + window.location + '" method="POST">' +
            defaultResultsHTML +
          '</form>' +
        '</div>'
      );

      GOVUK.registerToVote.OptionalInformation = function () {};
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(postcodeLookupMock.$targetElement.html()).toEqual($addressHTMLDefault.html());
    });

    it("Should add the correct HTML to the targeted div in the only optional section on the page", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLDefault = $(
        '<div>' +
            defaultResultsHTML +
        '</div>'
      );

      $targetElement.wrap('<div class="optional-section-core-content" />');
      GOVUK.registerToVote.OptionalInformation = function () {};
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(postcodeLookupMock.$targetElement.html()).toEqual($addressHTMLDefault.html());
    });

    it("Should add the correct HTML to the targeted div if the 'addressIsPrevious' property is set", function () {
      var data = {
        "addresses" : addressData,
        "rawJSON" : '{"addresses":[]}' 
      },
      $addressHTMLPrevious = $(
        '<div>' +
          '<form action="' + window.location + '" method="POST">' +
            previousAddressResultsHTML +
          '</form>' +
        '</div>'
      );

      postcodeLookupMock.addressIsPrevious = true;
      GOVUK.registerToVote.OptionalInformation = function () {};
      GOVUK.registerToVote.PostcodeLookup.prototype.addLookup.apply(postcodeLookupMock, [data, 'WR2 6NJ']);

      expect(postcodeLookupMock.$targetElement.html()).toEqual($addressHTMLPrevious.html());
    });
  });

  describe("getAddresses method", function () {
    var cachedValidation = GOVUK.registerToVote.validation,
        cachedAjax = $.ajax,
        postcodeLookupMock,
        mockAjax;

    mockAjax = function (methodName, substitute) {
      var mock = {},
          callbacks = ['done', 'fail', 'always'],
          i, j;

      for (i = 0, j = callbacks.length; i < j; i++) {
        callback = callbacks[i]
        mock[callback] = (callback === methodName) ? substitute : function () { return this; };
      }
      return function () { return mock; };
    };

    beforeEach(function () {
      postcodeLookupMock = {
        "$searchInput" : $searchInput,
        "$searchButton" : $searchButton,
        "$targetElement" : $targetElement,
        "$waitMessage" : $('<p id="wait-for-request">Finding address</p>')
      };
    });

    afterEach(function () {
      $.ajax = cachedAjax;
      postcodeLookupMock.$waitMessage.remove();
      postcodeLookupMock.$targetElement.html();
    });
    
    it("Should do nothing if the postcode field is invalid", function () {
      GOVUK.registerToVote.validation.validate = jasmine.createSpy("GOVUK.registerToVote.validation.validate").and.callFake(
        function () {
          return false;
        }
      );
      spyOn($, "ajax");
      GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     

      expect(GOVUK.registerToVote.validation.validate).toHaveBeenCalled();
      expect($.ajax).not.toHaveBeenCalled();
    });

    it("Should add the progress indictator and set aria-busy to true if the field is valid", function () {
      var onDone;

      GOVUK.registerToVote.validation.validate = function () { return true; };
      $.ajax = mockAjax('done',
        function (callback) {
          onDone = callback;
          return this;
        }
      );
      GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     

      expect(document.getElementById('wait-for-request')).not.toEqual(null);
      expect($targetElement.attr('aria-busy')).toEqual('true');
    });

    it("Should use $.ajax to make a GET request to /address if the postcode field is valid", function () {
      var onDone;

      GOVUK.registerToVote.validation.validate = function () { return true; };
      postcodeLookupMock.$searchInput.val('WR2 6NJ');
      spyOn($, "ajax").and.callFake(
        mockAjax('done',
          function (callback) {
            onDone = callback;
            return this;
          }
        )
      );
      GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);

      expect($.ajax).toHaveBeenCalledWith({
        'url' : '/address/WR26NJ',
        'dataType' : 'json',
        'timeout' : 10000
      });
    });

    describe("done method set on $.ajax", function () {
      var addressData = { "addresses" : [
        {
          addressLine: "Pool House, Elgar Business Centre, Moseley Road, Hallow, Worcester, Worcestershire",
          uprn: "26742626",
          postcode: "WR2 6NJ",
          manualAddress: null
        }
      ]};

      it("Should mark the postcode as invalid if an empty results set is returned", function () {
        var onDone;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        spyOn(GOVUK.registerToVote.validation, "makeInvalid");
        $.ajax = mockAjax('done',
          function (callback) {
            onDone = callback;
            return this;
          }
        );
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onDone({ "addresses" : [] }, "", { "responseText" : "" });

        expect(GOVUK.registerToVote.validation.makeInvalid).toHaveBeenCalledWith(
          [{
            'name' : 'postcode',
            'rule' : 'postcode',
            '$source' : postcodeLookupMock.$searchInput
          }], postcodeLookupMock.$searchButton
        );
      });

      it("Should remove any existing HTML from the results div & the continue button if results are returned", function () {
        var onDone,
            cachedAdd = GOVUK.registerToVote.validation.fields.add;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        spyOn(GOVUK.registerToVote.validation, "makeInvalid");
        $.ajax = mockAjax('done',
          function (callback) {
            onDone = callback;
            return this;
          }
        );
        postcodeLookupMock.$targetElement.append("<p>Existing results</p>");
        postcodeLookupMock.addLookup = function () {};
        GOVUK.registerToVote.validation.fields.add = function () {};
        spyOn(GOVUK.registerToVote.validation.fields, "remove");
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onDone(addressData, "", { "responseText" : "" });

        expect(GOVUK.registerToVote.validation.fields.remove).toHaveBeenCalledWith('address');
        expect(GOVUK.registerToVote.validation.fields.remove).toHaveBeenCalledWith('addressSelect');
        expect(GOVUK.registerToVote.validation.fields.remove).toHaveBeenCalledWith('addressExcuse');
        expect(postcodeLookupMock.$targetElement.html()).toEqual('');
        expect(document.getElementById('continue')).toEqual(null);

        GOVUK.registerToVote.validation.fields.add = cachedAdd;
      });

      it("Should call the addLookup method with the response data to add the new HTML", function () {
        var onDone;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        postcodeLookupMock.$searchInput.val('WR2 6NJ');
        $.ajax = mockAjax('done',
          function (callback) {
            onDone = callback;
            return this;
          }
        );
        postcodeLookupMock.addLookup = jasmine.createSpy("postcodeLookupMock.addLookup");
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onDone(addressData, "", { "responseText" : "" });

        expect(postcodeLookupMock.addLookup).toHaveBeenCalledWith(addressData, "WR2 6NJ");
      });

      it("Should set validation on the new HTML", function () {
        var onDone;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        $.ajax = mockAjax('done',
          function (callback) {
            onDone = callback;
            return this;
          }
        );
        postcodeLookupMock.addLookup = function () {};
        GOVUK.registerToVote.validation.fields.add = jasmine.createSpy("GOVUK.registerToVote.validation.fields.add");
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onDone(addressData, "", { "responseText" : "" });

        expect(postcodeLookupMock.$targetElement.attr('data-validation-name')).toEqual('address');
        expect(postcodeLookupMock.$targetElement.attr('data-validation-type')).toEqual('fieldset');
        expect(postcodeLookupMock.$targetElement.attr('data-validation-rules')).toEqual('fieldOrExcuse');
        expect(postcodeLookupMock.$targetElement.attr('data-validation-children')).toEqual('addressSelect addressExcuse');
        expect(GOVUK.registerToVote.validation.fields.add).toHaveBeenCalledWith(postcodeLookupMock.$targetElement);
      });
    });

    describe("always method set on $.ajax", function () {
      it("Should remove the progress indictator and set aria-busy to false", function () {
        var onAlways;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        $.ajax = mockAjax('always',
          function (callback) {
            onAlways = callback;
            return this;
          }
        );
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onAlways();

        expect(document.getElementById('wait-for-request')).toEqual(null);
        expect(postcodeLookupMock.$targetElement.attr('aria-busy')).toEqual('false');
      });
    });

    describe("fail method set on $.ajax", function () {
      it("Should call onTimeout if the request fails by being longer than 10000ms", function () {
        var onFail;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        $.ajax = mockAjax('fail',
          function (callback) {
            onFail = callback;
            return this;
          }
        );
        postcodeLookupMock.onTimeout = jasmine.createSpy("postcodeLookupMock.onTimeout");
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onFail({}, 'timeout', '');

        expect(postcodeLookupMock.onTimeout).toHaveBeenCalled();
      });

      it("Should call onError if the request fails for a reason other than being too long", function () {
        var onFail;

        GOVUK.registerToVote.validation.validate = function () { return true; };
        $.ajax = mockAjax('fail',
          function (callback) {
            onFail = callback;
            return this;
          }
        );
        postcodeLookupMock.onError = jasmine.createSpy("postcodeLookupMock.onError");
        GOVUK.registerToVote.PostcodeLookup.prototype.getAddresses.call(postcodeLookupMock);     
        onFail({}, 'error', '');

        expect(postcodeLookupMock.onError).toHaveBeenCalled();
      });
    });
  });
});
