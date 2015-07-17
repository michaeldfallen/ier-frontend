describe("ToggleObj", function () {
  // ToggleObj doesn't have its own setup or bindEvent methods so stub them
  GOVUK.registerToVote.ToggleObj.prototype.setup = function () {};
  GOVUK.registerToVote.ToggleObj.prototype.bindEvents = function () {};

  describe("Constructor", function () {
    it("Should produce an instance with the correct interface", function () {
      var elm = document.createElement('div'),
          toggleObj = new GOVUK.registerToVote.ToggleObj(elm, 'optional-section');

      expect(toggleObj.setAccessibilityAPI).toBeDefined();
      expect(toggleObj.toggle).toBeDefined();
      expect(toggleObj.setInitialState).toBeDefined();
    });

    it("Should call the right methods when created", function () {
      var elm = document.createElement('div'),
          toggleObj;
      
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "setup").and.callFake(
        function () {
          return true;
        }
      );
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "bindEvents");

      toggleObj = new GOVUK.registerToVote.ToggleObj(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj.prototype.setup).toHaveBeenCalled();
      expect(GOVUK.registerToVote.ToggleObj.prototype.bindEvents).toHaveBeenCalled();
    });

    it("Should not call bindEvents if setup fails", function () {
      var elm = document.createElement('div'),
          toggleObj;
      
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "setup").and.callFake(
        function () {
          return false;
        }
      );
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "bindEvents");

      toggleObj = new GOVUK.registerToVote.ToggleObj(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj.prototype.bindEvents).not.toHaveBeenCalled();
    });
  });

  describe("setAccessibilityAPI method", function () {
    it("Should set the right ARIA attributes for hidden content", function () {
      var toggleMock = {
            "$content" : $("<div></div>"),
            "$toggle" : $("<a><span class='visuallyhidden'></span></a>"),
            "toggleActions" : { "hidden" : "Expand" }
          };

      GOVUK.registerToVote.ToggleObj.prototype.setAccessibilityAPI.call(toggleMock, 'hidden');
      expect(toggleMock.$content[0].getAttribute('aria-hidden')).toEqual('true');
      expect(toggleMock.$content[0].getAttribute('aria-expanded')).toEqual('false');
    });

    it("Should set the right ARIA attributes for shown content", function () {
      var toggleMock = {
            "$content" : $("<div></div>"),
            "$toggle" : $("<a><span class='visuallyhidden'></span></a>"),
            "toggleActions" : { "visible" : "Hide" }
          };

      GOVUK.registerToVote.ToggleObj.prototype.setAccessibilityAPI.call(toggleMock, 'visible');
      expect(toggleMock.$content[0].getAttribute('aria-hidden')).toEqual('false');
      expect(toggleMock.$content[0].getAttribute('aria-expanded')).toEqual('true');
    });
  });

  describe("toggle method", function () {
    var toggleMock = {
          "$content" : $("<div></div>"),
          "$toggle" : $("<a></a>"),
          "toggleClass" : "expanded-section-open"
        },
        cachedTrigger = $.fn.trigger;

    beforeEach(function () {
      toggleMock.setAccessibilityAPI = function () {};
      spyOn(toggleMock, "setAccessibilityAPI");
    });

    afterEach(function () {
      $.fn.trigger = cachedTrigger;
    });

    it("Should set right attributes for elements involved and call the right methods when hidden", function () {
      spyOn($.fn, "trigger");

      toggleMock.$content.css('display', 'none');
      GOVUK.registerToVote.ToggleObj.prototype.toggle.call(toggleMock);

      expect(toggleMock.$content.hasClass(toggleMock.toggleClass)).toBe(true);
      expect(toggleMock.$toggle.hasClass('toggle-open')).toBe(true);
      expect(toggleMock.setAccessibilityAPI).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalledWith('toggle.open', { '$toggle' : toggleMock.$toggle });
    });

    it("Should set right attributes for elements involved and call the right methods when shown", function () {
      spyOn($.fn, "trigger");

      toggleMock.$content.css('display', 'block');
      GOVUK.registerToVote.ToggleObj.prototype.toggle.call(toggleMock);

      expect(toggleMock.$content.hasClass(toggleMock.toggleClass)).toBe(false);
      expect(toggleMock.$toggle.hasClass('toggle-closed')).toBe(true);
      expect(toggleMock.setAccessibilityAPI).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalledWith('toggle.closed', { '$toggle' : toggleMock.$toggle });
    });
  });
  describe("setInitialState method", function () {
    it("Should set the elements involved to an open state if the toggleClass is set on content", function () {
      var toggleObjMockOpen = {
            "toggleClass" : "expanded-section-open",
            "$content" : $("<div class='expanded-section-open'></div>"),
            "$toggle" : $("<a class='toggle-closed'></a>"),
            "setAccessibilityAPI" : function () {}
          };

      spyOn(toggleObjMockOpen, "setAccessibilityAPI");

      GOVUK.registerToVote.ToggleObj.prototype.setInitialState.call(toggleObjMockOpen);
      expect(toggleObjMockOpen.$toggle.hasClass("toggle-open")).toBe(true);
      expect(toggleObjMockOpen.setAccessibilityAPI).toHaveBeenCalled();
    });

    it("Should do nothing if the toggleClass is not set on content", function () {
      var toggleObjMockClosed = {
            "toggleClass" : "expanded-section-open",
            "$content" : $("<div></div>"),
            "$toggle" : $("<a class='toggle-closed'></a>"),
            "setAccessibilityAPI" : function () {}
          };

      spyOn(toggleObjMockClosed, "setAccessibilityAPI");
      GOVUK.registerToVote.ToggleObj.prototype.setInitialState.call(toggleObjMockClosed);

      expect(toggleObjMockClosed.$toggle.hasClass("toggle-open")).toBe(false);
      expect(toggleObjMockClosed.setAccessibilityAPI).not.toHaveBeenCalled();
    });
  });
});

describe("OptionalInformation", function () {
  describe("Constructor", function () {
    var elm;

    beforeEach(function () {
      elm = $('<div class="optional-section" data-toggle-text="Help" />"');
    });

    it("Should produce an instance with the correct interface", function () {
      var optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section');

      expect(optionalInformation.setup).toBeDefined();
      expect(optionalInformation.bindEvents).toBeDefined();
      expect(optionalInformation.setAccessibilityAPI).toBeDefined();
      expect(optionalInformation.toggle).toBeDefined();
      expect(optionalInformation.setInitialState).toBeDefined();
    });

    it("Should call the ToggleObj constructor with the same arguments sent to the OptionalInformation constructor", function () {
      var optionalInformation;

      spyOn(GOVUK.registerToVote, "ToggleObj");
      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj).toHaveBeenCalledWith(elm, 'optional-section');
    });

    it("Should not call bindEvents if the data-toggle-text attribute isn't set", function () {
      var optionalInformation;

      elm = $(
        '<div class="optional-section">' +
          '<h2>Help</h2>' +
          '<p>Some help content</p>' +
        '</div>'
      )[0];

      spyOn(GOVUK.registerToVote.OptionalInformation.prototype, "bindEvents");
      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section');
      expect(GOVUK.registerToVote.OptionalInformation.prototype.bindEvents).not.toHaveBeenCalled();
    });
  });

  describe("setup", function () {
    var elm,
        toggleLink = '<a href="#" class="toggle toggle-closed">Help</a>';

    beforeEach(function () {
      elm = $(
            "<div class='optional-section' data-toggle-text='Help'>" +
              "<h2>Help</h2>" +
              "</p>Some help content</p>" +
            "</div>"
            )[0];

      $(document.body).append(elm);
    });

    afterEach(function () {
      var $elm = $(elm);

      $elm.siblings("a.toggle").remove();
      $elm.remove();
    });

    it("Should return true if run on valid HTML", function () {
      var optionalInformationMock = {
            $content : $('.optional-section'),
            setAccessibilityAPI : function () {},
            setInitialState : function () {}
          },
          setupResult;

      setupResult = GOVUK.registerToVote.OptionalInformation.prototype.setup.call(optionalInformationMock);
      expect(setupResult).toEqual(true);
    });

    it("Should return false if run on invalid HTML", function () {
      var optionalInformationMock = {
            $content : $('.optional-section'),
            setAccessibilityAPI : function () {},
            setInitialState : function () {}
          },
          setupResult;

      optionalInformationMock.$content.removeAttr('data-toggle-text');
      setupResult = GOVUK.registerToVote.OptionalInformation.prototype.setup.call(optionalInformationMock);
      expect(setupResult).toEqual(false);
    });

    it("Should insert a toggle link before the optional information section", function () {
      var optionalInformation;

      expect($(elm).prev('a.toggle').length).toEqual(0);
      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($(elm).prev('a.toggle').length).toEqual(1);
    });

    it("Should insert a toggle link containing the text from it's data attribute", function () {
      var optionalInformation;

      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($(elm).prev('a.toggle').text()).toEqual($(elm).data('toggleText'));
    });

    it("Should visually hide the section's initial heading", function () {
      var optionalInformation;

      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($(elm).find('h2').hasClass('visuallyhidden')).toBe(true);
    });

    it("Should call the setInitialState method", function () {
      var optionalInformation;

      spyOn(GOVUK.registerToVote.OptionalInformation.prototype, "setInitialState");

      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect(GOVUK.registerToVote.OptionalInformation.prototype.setInitialState).toHaveBeenCalled();
    });
  });

  describe("bindEvents", function () {
    var elm;

    beforeEach(function () {
      elm = $(
            "<div class='optional-section' data-toggle-text='Help'>" +
              "<h2>Help</h2>" +
              "</p>Some help content</p>" +
            "</div>"
            )[0];
    });

    it("Should add a click event to the toggle link that calls the toggle method", function () {
      var optionalInformation,
          evtCallback;

      spyOn(GOVUK.registerToVote.OptionalInformation.prototype, "toggle");
      spyOn($.fn, "on").and.callFake(function (evt, callback) {
        evtCallback = callback;
      });

      optionalInformation = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($.fn.on).toHaveBeenCalled();
      evtCallback();
      expect(GOVUK.registerToVote.OptionalInformation.prototype.toggle).toHaveBeenCalled();
    });
  });
});
