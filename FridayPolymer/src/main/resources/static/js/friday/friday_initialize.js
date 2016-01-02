//var fridayGeoLocation = new FridayGeoLocation();
    
var initLocationIntervalTimer = null;
var intervalCounter = 0;
    
var trafficIncidents = new Array();
    


/**
 * Initialize the monthly calendar
 */
function initializeMonthlyCalendar()
{    
    var homeMonthlyCalendar = jQuery("#homeMonthlyCalendar");
    
    if (homeMonthlyCalendar != null)
    {
        $("#homeMonthlyCalendar").fullCalendar('destroy');
    }
    
    var monthCalendarCard = jQuery("#monthCalendarCard");
    var cardHeight = monthCalendarCard.height();
    
    jQuery('#homeMonthlyCalendar').fullCalendar(
    {
        height: cardHeight - 10,
        theme : false,
        header :
        {
            left : 'title',
            center : '',
            right : 'prevYear prev today next nextYear'
        },
        weekNumbers : true,
        weekMode : 'liquid',
        selectable : true,
        selectHelper : true,
        droppable: true,
        handleWindowResize: true,
        drop: function(date)
        {
            //alert("Dropped on " + date.format());
        },
        //eventReceive: function(event)
        //{
        //    alert(event.title);
        //},
        dayClick : function(date, allDay, jsEvent, view)
        {
            if (allDay)
            {
                jQuery('#homeDailyCalendar').fullCalendar("gotoDate", date);
            }
        },
        viewDisplay : function(view)
        {
            //window.setTimeout(syncDailyCalendar, 100);
        },
        select : function(startDate, endDate, allDay, jsEvent, view)
        {
            var startMom = moment(startDate);
            var endMom = moment(endDate).subtract(1, 'days');;
            
            if (!startMom.isSame(endMom))
            {                
                //alert(startMom.format() + "   " + endMom.format());
                showEventInputDialog(startMom, endMom, allDay);
            }
        },
        lazyFetching : false,
        eventRender : function(event, element, view)
        {
            element.attr("title", event.eventTitle);

        },
        eventAfterRender : function(event, element, view)
        {

        },
        eventClick : function(calEvent, jsEvent, view)
        {

        },
        eventSources : [
        {
          url : getBaseLocation() + '/holidays?maxTitleLength=100',
          type : 'GET',
          dataType : "json",
          error : function(jqXHR, textStatus, errorThrown)
          {
              alert('There was an error while fetching holidays. ' + errorThrown);
          }
        },
        {
          url : getBaseLocation() + '/events',
          type : 'GET',
          dataType : "json",
          headers:
          {
              "RequestDate": new Date().toString()
          },
          error : function(jqXHR, textStatus, errorThrown)
          {
              alert('There was an error while fetching reminders. ' + errorThrown);
          }
      } ]
    });
    
    
}

function initializeDailyCalendar()
{
    var homeDailyCalendar = $("#homeDailyCalendar");
    
    if (homeDailyCalendar != null)
    {
        $("#homeDailyCalendar").fullCalendar('destroy');
    }

    var dayCalendarCard = jQuery("#dayCalendarCard");
    var cardHeight = dayCalendarCard.height();
    
    $('#homeDailyCalendar').fullCalendar(
    {
        height : cardHeight,
        theme : false,
        defaultView : "agendaDay",
        header :
        {
            left : 'title',
            center : '',
            right : 'prev today next'
        },

        selectable : true,
        selectHelper : true,
        unselectAuto : true,
        slotMinutes : 30,
        titleFormat :
        {
            month : 'MMMM yyyy', // September 2009
            week : "MMM d[ yyyy]{ '&#8212;'[ MMM] d yyyy}", // Sep 7 - 13 2009
            day : 'dddd MMMM DD, YYYY' // Tuesday, Sep 8, 2009
        },
        viewDisplay : function(view)
        {
            //window.setTimeout(syncMonthlyCalendar, 100);
        },
        select : function(startDate, endDate, allDay, jsEvent, view)
        {
            var startMom = moment(startDate);
            var endMom = moment(endDate);
            
            if (!startMom.isSame(endMom))
            {
                showEventInputDialog(startMom, endMom, allDay);
            }
            
        },
        eventRender : function(event, element, view)
        {
            element.css("color", "#FFFFFF");
            
            if (event.type == "HOLIDAY")
            {
                element.css("backgroundColor", "#FFCCCC");                
            }
            else if (event.type == "APPOINTMENT")
            {
                element.css("backgroundColor", "#E0F5FF");                
            }            
            else if (event.type == "PARTY")
            {
                element.css("backgroundColor", "#F0C2E0");                
            }
            else if (event.type == "CONCERT")
            {
                element.css("backgroundColor", "#D6FFAD");                
            }
            else if (event.type == "MOVIE")
            {
                element.css("backgroundColor", "#D6EBFF");                
            }
            else if (event.type == "PLAY")
            {
                element.css("backgroundColor", "#D6E0EB");                
            }
            else if (event.type == "MEETING")
            {
                element.css("backgroundColor", "#FF9980");
            }
            else if (event.type == "BIRTHDAY")
            {
                element.css("backgroundColor", "#FFF0B2");                
            }
            else if (event.type == "ANNIVERSARY")
            {
                element.css("backgroundColor", "#FFD6FF");                
            }
            else if (event.type == "TASK")
            {
                element.css("backgroundColor", "#CCE0FF");
            }
            else if (event.type == "REMINDER")
            {
                element.css("backgroundColor", "#F5D6CC");                
            }
            else if (event.type == "OTHER")
            {
                element.css("backgroundColor", " #E0F5FF");
            }
            
            element.css("font", "normal normal 12px Calibri, sans-serif");
            
            if (event.title == null)
            {
                event.title = "";
            }
            
            if (event.allDay)
            {
                if (event.location != null)
                {
                    element.html(event.title + "<br/>" + event.location);
                }
                else
                {
                    element.html(event.title);
                }
            }
            else
            {
                if (event.location != null)
                {
                    element.html(moment(event.start).format("h:mm a") + "-" + moment(event.end).format("h:mm a") + "<br/>" + event.title + "<br/>" + event.location);
                }
                else
                {
                    element.html(moment(event.start).format("h:mm a") + "-" + moment(event.end).format("h:mm a") + "<br/>" + event.title);
                }                
            }
        },
        eventSources : [
                        {
                            url : getBaseLocation() + '/holidays?maxTitleLength=100',
                            type : 'GET',
                            dataType : "json",
                            error : function(jqXHR, textStatus, errorThrown)
                            {
                                alert('There was an error while fetching daily calendar holidays. ' + errorThrown);
                            }
                        },
                        {                      
                            url : getBaseLocation() + '/events',
                            type : 'GET',
                            dataType : "json",
                            headers:
                            {
                                "RequestDate": new Date().toString()
                            },
                            error : function(jqXHR, textStatus, errorThrown)
                            {
                                alert('There was an error while fetching daily calendar events. ' + errorThrown);
                            }
                        } ]        
    });
    
    //$(".fc-agenda-axis").each(function(index)
    //{
    //    $(this).removeClass("ui-widget-header");
    //});

    //$(".fc-agenda-divider").removeClass("ui-widget-header");
    //$(".fc-agenda-gutter").removeClass("ui-widget-header");

    //$(".fc-agenda-divider-inner").each(function(index)
    //{
    //    $(this).removeClass("fc-agenda-divider-inner");
    //});

    //$("#homeDailyCalendar .fc-today").removeClass("ui-state-highlight");

}

function showEventInputDialog(startDate, endDate, allDay)
{
    document.querySelector("friday-event-dialog").showEventDialog(startDate, endDate, allDay);
}
