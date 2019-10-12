const sid = "AC0aa50140845ca66cd5908798e790208f";
const token = "72f91a3f08970d1c2db25c1bb057740b";
const myNumFrom = "+15203406791";
const callBack = "http://f772636f.ngrok.io/twilioData";
const responseURL = "http://f772636f.ngrok.io/getTwiML";

const twilio = require('twilio')(sid,token);

twilio.calls.create({
    record: false,
    url:  responseURL,
    to: '+918097385231',
    statusCallback: callBack,
    statusCallbackMethod : "POST",
    from: myNumFrom
}).then(console.log).catch(console.log);