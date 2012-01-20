/*==================================================
* Localization of labellers.js
*==================================================
*/
if (typeof Timeline.GregorianDateLabeller.monthNames == "undefined") Timeline.GregorianDateLabeller.monthNames = {};
Timeline.GregorianDateLabeller.monthNames["en"] = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];
if (typeof Timeline.GregorianDateLabeller.dayNames == "undefined") Timeline.GregorianDateLabeller.dayNames = {};
Timeline.GregorianDateLabeller.dayNames["en"] = [
    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
];
Timeline.GregorianDateLabeller.prototype.labelPrecise = function(date) {
    return SimileAjax.DateTime.removeTimeZoneOffset(
        date,
        this._timeZone //+ (new Date().getTimezoneOffset() / 60)
    ).toLocaleDateString();
};
