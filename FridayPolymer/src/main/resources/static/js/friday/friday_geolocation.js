
var googleApiKey = "AIzaSyAw9ZIE3VxDdlt0PjbkxE6zG5Al3RSvq1g";

var mapquestApiKey = "Fmjtd%7Cluub20a7lu%2Ca5%3Do5-9urg1z";

function FridayGeoLocation()
{
    // geoPlugin provides low accuracy location information
    // based on IP address (typically accurate to city, at best)
    this.geoPluginStatus = null;
    this.geoPluginIp = null;
    this.geoPluginCity = null;
    this.geoPluginRegion = null;
    this.geoPluginAreaCode = null;
    this.geoPluginDmaCode = null;
    this.geoPluginCountryCode = null;
    this.geoPluginCountryName = null;
    this.geoPluginContinentCode = null;
    this.geoPluginLatitude = null;
    this.geoPluginLongitude = null;
    this.geoPluginCurrencyCode = null;
    this.geoPluginCurrencySymbol = null;
    
    // geo provides (possibly) higher accuracy location information based
    // on cell-tower or GPS information (if available)
    // geoplugin and geo location information is combined
    // to provide the highest accuracy position available
    // at the current time on the current device
    this.geoLatitude = null;
    this.geoLongitude = null;
    this.geoAltitude = null;
    this.geoAccuracy = null;
    this.geoAltitudeAccuracy = null;
    this.geoHeading = null;
    this.geoSpeed = null;
    this.geoTimestamp = null;
    
    this.fridayGeoDataReady = false;
    
    this.fridayLatitude = null;
    this.fridayLongitude = null;
    this.fridayCity = null;
    this.fridayRegion = null;
    this.fridayAreaCode = null;
    this.fridayDmaCode = null;
    this.fridayCountryCode = null;
    this.fridayCountryName = null;
    this.fridayContinentCode = null;
    this.fridayCurrencyCode = null;
    this.fridayCurrencySymbol = null;
    this.fridayLocationAccuracy = null;      // low, medium, high
}

FridayGeoLocation.prototype.initializeGeopluginData = function()
{
//    this.geoPluginCity = geoplugin_city();
//    this.geoPluginRegion = geoplugin_region();
//    this.geoPluginAreaCode = geoplugin_areaCode(); 
//    this.geoPluginDmaCode = geoplugin_dmaCode(); 
//    this.geoPluginCountryCode = geoplugin_countryCode(); 
//    this.geoPluginCountryName = geoplugin_countryName(); 
//    this.geoPluginContinentCode = geoplugin_continentCode();
//    this.geoPluginLatitude = geoplugin_latitude(); 
//    this.geoPluginLongitude = geoplugin_longitude(); 
//    this.geoPluginCurrencyCode = geoplugin_currencyCode(); 
//    this.geoPluginCurrencySymbol = geoplugin_currencySymbol();

    this.geoPluginCity = null;
    this.geoPluginRegion = null;
    this.geoPluginAreaCode = null; 
    this.geoPluginDmaCode = null; 
    this.geoPluginCountryCode = null; 
    this.geoPluginCountryName = null; 
    this.geoPluginContinentCode = null;
    this.geoPluginLatitude = null; 
    this.geoPluginLongitude = null; 
    this.geoPluginCurrencyCode = null; 
    this.geoPluginCurrencySymbol = null;    
    
    this.fridayLocationAccuracy = "low";
};

FridayGeoLocation.prototype.initializeGeolocationData = function()
{
    if (navigator.geolocation)
    {
        var timeoutVal = 1000 * 1;     // 1 second time out
        navigator.geolocation.getCurrentPosition(
                function(position)
                {
                    this.geoLatitude = position.coords.latitude;                    
                    this.geoLongitude = position.coords.longitude;
                    this.geoAltitude = position.coords.altitude;
                    this.geoAccuracy = position.coords.accuracy;
                    this.geoAltitudeAccuracy = position.coords.altitudeAccuracy;
                    this.geoHeading = position.coords.heading;
                    this.geoSpeed = position.coords.speed;
                    this.geoTimestamp = position.timestamp;

                    if (this.geoAccuracy < 5)
                    {
                        this.fridayLocationAccuracy = "very-high";
                    }
                    else if (this.geoAccuracy < 10)
                    {
                        this.fridayLocationAccuracy = "high";
                    }
                    else if (this.geoAccuracy < 100)
                    {
                        this.fridayLocationAccuracy = "medium";
                    }
                    else
                    {
                        this.fridayLocationAccuracy = "low";
                    }
                    
                    this.fridayGeoDataReady = true;
                });
    }
    else
    {
        this.fridayGeoDataReady = true;
        //alert("Geolocation is not supported by this browser");
    }
};


FridayGeoLocation.prototype.getGeoLocationDataReady = function()
{
    return this.fridayGeoDataReady;
};

FridayGeoLocation.prototype.getLatitude = function()
{
    if (this.geoLatitude != null)
    {
        return this.geoLatitude;
    }
    else
    {
        return this.geoPluginLatitude;
    }
};

FridayGeoLocation.prototype.getLongitude = function()
{
    if (this.geoLongitude != null)
    {
        this.fridayLongitude = this.geoLongitude;;
        return this.geoLongitude;
    }
    else
    {
        this.fridayLongitude = this.geoPluginLongitude;
        return this.geoPluginLongitude;
    }
};
