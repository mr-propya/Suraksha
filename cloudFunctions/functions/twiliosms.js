const sid = "AC0aa50140845ca66cd5908798e790208f";
const token = "72f91a3f08970d1c2db25c1bb057740b";
const myNumFrom = "+15203406791";
const callBack = "http://3bb9b696.ngrok.io/twilioData";
const responseURL = "http://3bb9b696.ngrok.io/getTwiML";

const twilio = require('twilio')(sid,token);
/*
twilio.calls.create({
    record: false,
    url:  responseURL,
    to: '+918169023113',
    statusCallback: callBack,
    statusCallbackMethod : "POST",
    from: myNumFrom
}).then(console.log).catch(console.log);
*/

function sendSms(toMsg,msg){
    twilio.messages.create({
        body: msg, 
        from: '+15203406791', 
        to: toMsg
    }).then(message => console.log(message.sid));
}

module.exports = {
    'sendSms':sendSms
}