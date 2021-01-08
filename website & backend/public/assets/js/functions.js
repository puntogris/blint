$(window).load(function() {

    $(".loader").delay(1000).fadeOut("slow");

}); 

$(function () {
    "use strict";
    var $window = $(window), /* window cash */
        $body = $("body"), /* body cash */
        $header = $('.large-header'); /* nav a cash */
    
    /* Loading Animations */
    $window.on("load", function () {
        $body.css({overflow: "visible"});
        $header.css({display: "block"});
    });
 
});

! function(t) {
    "use strict";

    t('#nav-icon2').click(function(){
        t(this).toggleClass('open');
    });

    t(".mobile_menu ul a").click(function() {
        t(".mobile_menu").fadeOut(600);
        t("#nav-icon2").toggleClass('open');
        t(".top_text").css("opacity", "1");
    }).append("<span>");

    t(".sandwich_menu").click(function() {
        if (t(".mobile_menu").is(":visible")) {
            t(".top_text").css("opacity", "1");
            t(".mobile_menu").fadeOut(600);
            t(".mobile_menu li a").removeClass("fadeInUp animated");
        } else {
            t(".top_text").css("opacity", ".1");
            t(".mobile_menu").fadeIn(600);
            t(".mobile_menu li a").addClass("fadeInUp animated");
        };
    });

    t(".sandwich_menu .nav").click(function() {
        t(".mobile_menu").is(":hide");
    });

    t(".boxs-height").mCustomScrollbar({
        theme:"3d-dark"
    });

/*------------------ Popup Window Effect------------------*/

    t(".popup").magnificPopup({type:"image"});

/*------------------ Masonry grid------------------*/

//Masonry parameters
    var tgrid = t('.grid');
tgrid.masonry({
  itemSelector: '.grid-item',
  percentPosition: true,
  singleMode: true,
  isResizable: true,
  isAnimated: true
});

t.fn.masonryImagesReveal = function( titems ) {
  var msnry = this.data('masonry');
  var itemSelector = msnry.options.itemSelector;
  // hide by default
  titems.hide();
  // append to container
  this.append( titems );
  titems.imagesLoaded().progress( function( imgLoad, image ) {
    // get item
    // image is imagesLoaded class, not <img>, <img> is image.img
    var titem = t( image.img ).parents( itemSelector );
    // un-hide item
    titem.show();
    // masonry does its thing
    msnry.appended( titem );
  });
  
  return this;
};

/*------------------ Masonry Filter ------------------*/

t(".filters .btn").click(function(e) {
  e.preventDefault();
  var filter = t(this).attr("data-filter");
  tgrid.masonryFilter({
   filter: function () {
    if (!filter) return true;
    return t(this).attr("data-filter") == filter;
  }
});
});

t.fn.masonryFilter = function (options) {
        //reload masonry
        var reload = function (tcontainer) {
            setTimeout(function () {
                tcontainer.masonry("layout");
            }, 100);
        };

        var process = function (tcontainer) {
            var items = tcontainer.masonry("getAllItems"),
            revealItems = [],
            hideItems = [];

            t.each(items, function(i) {
                var item = items[i];
                var elm = t(item.element),
                shouldShow = options.filter && options.filter.call(elm);

                if (shouldShow) {
                    if (item.isHidden) {
                        // -- Have to set this property so masonry does
                        //    not include hidden items when calling "layout"
                        item.isIgnored = false;
                        revealItems.push(item);
                      }
                    } else {
                        if (!item.isHidden) {                        
                        // -- Easier to set this property directy rather than
                        //    using the "ignore" method, as it takes in a DOM
                        //    element rather than the masonry item object.
                        item.isIgnored = true;
                        hideItems.push(item);
                      }
                    }
                  });

            tcontainer.masonry('hide', hideItems);
            tcontainer.masonry('reveal', revealItems);

            reload(tcontainer);
        };

        return this.each(function () {
            var self = t(this);
            process(self);
        });
      };

    var e = "",
        a = "",
        s = "",
        o = "",
        i = "#fafafa",
        n = "#fafafa";
    if (t(window).on("load", function() {
            t("body").addClass("loaded"), (new WOW).init()
        }), t("a.nav").length > 0 && t("a.nav").each(function() {
            e = t(this).attr("href"), "#home" != e && t(e).hide()
        }), t("a.nav").length > 0 && t("a.nav").on("click", function(e) {
            e.preventDefault(), a = t(this).attr("class"), s = t(this).attr("href"), t(this).hasClass("active") || (t(".overlay-bg").removeAttr("style"), t(this).addClass("active"), t("a.nav").each(function() {
                o = t(this).attr("href"), t(o).hide(), t(this).attr("href") != s && t(this).hasClass("active") && t(this).removeClass("active")
            }), a.indexOf("left") > 0 && setTimeout(function() {
               setTimeout(function() {
                    t(s).fadeIn(1e3)
                }, 200)
            }, 100), a.indexOf("right") > 0 && setTimeout(function() {
                setTimeout(function() {
                    t(s).fadeIn(1e3)
                }, 200)
            }, 100), a.indexOf("left_m") > 0 && setTimeout(function() {
                setTimeout(function() {
                    t(s).fadeIn(1e3)
                }, 200)
            }, 100), a.indexOf("right_m") > 0 && setTimeout(function() {
                setTimeout(function() {
                    t(s).fadeIn(1e3)
                }, 200)
            }, 100), a.indexOf("home") > 0 && setTimeout(function() {
                setTimeout(function() {
                    t(s).fadeIn(1e3)
                }, 200)
            }, 100))
        }), t("#countdown").length > 0 && t("#countdown").countdown(t("#countdown").attr("data-time"), function(e) {
            t(this).html(e.strftime("<div>%D<span>dias</span></div> <div>%H<span>horas</span></div> <div>%M<span>minutos</span></div> <div>%S<span>segundos</span></div>"))
        })) t("#mc-form").length > 0 && t("#mc-form").ajaxChimp({
        url: "http://nashdom.us15.list-manage.com/subscribe/post?u=8422f5254a6b55ed8e81a49ef&amp;id=57d75b6bb6"
    })

}(jQuery);
