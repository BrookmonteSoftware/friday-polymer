/**
 * Display an alert in a JQuery dialog
 * @param message
 */
function showAlert(title, message)
{
    jQuery("#alertDiv")
    .dialog({
        modal: true,
        title: title,
        autoOpen: true,
        width: 'auto',
        resizable: false,
        buttons:
        {
            "Ok": function()
            {
                $(this).dialog("close");
            }
        },
        open: function()
        {
            document.getElementById("alertMessage").innerHTML = message;
        }
    });
    
    $("#alertDiv").parent().find(".ui-dialog-titlebar-close").blur();    
}
/**
 * getBaseLocation
 * 
 * Gets the base location of the web application.
 * 
 * @returns
 */
function getBaseLocation()
{
    var baseLocation = window.location.protocol;
    baseLocation += "//";
    baseLocation += window.location.host;

    baseLocation += window.location.pathname.substring(0, window.location.pathname.indexOf("/", 1));

    return baseLocation;
}

/**
 * sortBy
 * 
 * Sort an array of javascript object by any field
 * 
 * Use to sort arrays for JSON object by any field in the object.
 * 
 */
var sortBy = (function ()
{
  //cached privated objects
  var _toString = Object.prototype.toString,
      //the default parser function
      _parser = function (x) { return x; },
      //gets the item to be sorted
      _getItem = function (x) {
        return this.parser((_toString.call(x) === "[object Object]" && x[this.prop]) || x);
      };

  // Creates a method for sorting the Array
  // @array: the Array of elements
  // @o.prop: property name (if it is an Array of objects)
  // @o.desc: determines whether the sort is descending
  // @o.parser: function to parse the items to expected type
  return function (array, o) {
    if (!(array instanceof Array) || !array.length)
      return [];
    if (_toString.call(o) !== "[object Object]")
      o = {};
    if (typeof o.parser !== "function")
      o.parser = _parser;
    //if @o.desc is false: set 1, else -1
    o.desc = [1, -1][+!!o.desc];
    return array.sort(function (a, b) {
      a = _getItem.call(o, a);
      b = _getItem.call(o, b);
      return ((a > b) - (b > a)) * o.desc;
    });
  };

}());

function generateUUID()
{
    var d = new Date().getTime();
    
    if(window.performance && typeof window.performance.now === "function")
    {
        d += performance.now(); //use high-precision timer if available
    }
    
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
        function(c)
        {
          var r = (d + Math.random()*16)%16 | 0;
          d = Math.floor(d/16);
          return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
    
    return uuid;
}