const e = require('express');
const parser = require('body-parser');
const  admin = require('firebase-admin');
const secret = require('./secretKey');


try {
    admin.initializeApp({
        credential: admin.credential.cert(secret),
        databaseURL: "https://suraksha-9c58a.firebaseio.com"
    });
}catch (e) {

}



const expressApp = e();


expressApp.use(parser.json());
expressApp.use(parser.urlencoded({extended:true}));



expressApp.post("/smsMe",(req,res)=>{
    const uid = req.body.userUID;
    console.log("running sms me");
    console.log("running sms me for "+uid);
    admin.database().ref("locations/"+uid).limitToLast(1).once('value',(loc)=>{
        let url = "";
        console.log(loc.val())
        loc.forEach((d)=>{
            url = `https://www.google.com/maps/search/?api=1&query=${d.child("lat").val()},${d.child("lon").val()}`
        })
        admin.database().ref("users/"+uid).once('value',(dataSnap)=>{
            const userName  = dataSnap.child("name").val();
            dataSnap.child("emergencyNo").forEach((jugnu)=>{
                let msg = `Hey ${jugnu.key}\n${userName} is in danger\nTrack him/her at ${url}`;
                const msgTwil = require('./twiliosms');
                msgTwil.sendSms("+91"+jugnu.val(),msg);
                console.log(msg);
            })
            res.send({
                'status':'success',
                'msg':userName
            })
        });
    });
});

expressApp.use("/getTwiML",(req,res)=>{
    const twiML = require('twilio').twiml.VoiceResponse;
    const response = new twiML();
    /*const name = 'Tanvi';

    //response.say("Hey bro");
    //response.say("Hey bro");
    const url = "http://bioms-prosthetics.000webhostapp.com/Suraksha/1.mp3";
    //const fullurl = url + i.toString() + ".mp3"
    response.gather({
        input: 'speech',
        timeout: 2,
        hints: 'hi, hey, hello, bye',
        action: 'https://fandango-butterfly-3768.twil.io/speech_response1',
    }).play(url);
    
    /*response.gather({
        input: 'speech',
        timeout: 3,
        hints: 'bye',
        action: 'https://fandango-butterfly-3768.twil.io/speech_response',
      }).say('Hi');    */
      
    console.log(response.toString())
    res.send(response.toString());
    
});


expressApp.post("/helpNeeded",(req,res)=>{
    console.log(req.body);
    const lat = req.body.lat;
    const long = req.body.lon;
    const user = req.body.userUID;

    if(lat == undefined || long == undefined || user == undefined){
        res.send({
           'status':'error',
           'error':'invalid params passed'
        });
        return;
    }
    const fcmHelper = require('./fcmNotifier');
    fcmHelper.raiseHelpRequest(lat,long,user).then((receipt)=>{
        console.log(receipt);
       res.send({
           'status':'success',
           'receipt':receipt
       })
    }).catch((err)=>{
        console.log(err);
        res.send({
            'status':'error',
            'error':JSON.stringify(err)
        })
    });

});

expressApp.post("/endHelp",(req,res)=>{
    console.log("end pings");
    const user = req.body.userUID;
    if(user === undefined){
        res.send({
            'status':'error',
            'error':'invalid params passed'
        });
        return;
    }
    require('./raisePing').stop(user);
    res.send({
        'status':'success'
    })
});
expressApp.post("/callMe",(req,res)=>{
    require('./twilio');
    console.log("calling you");
    res.send({
        'status':'success'
    })
});

expressApp.post('/pingBackStart',(req,res)=>{
    const user = req.body.userUID;
    console.log("got ping request");
    if(user === undefined){
        res.send({
            'status':'error',
            'error':'invalid params passed'
        });
        return;
    }
    const pingHelper = require('./raisePing');
    pingHelper.start(user);
    res.send({
        "status":"success",
        "data":"started "+user
    });
});
const pingHelper = require('./raisePing');
// pingHelper.start("dummy");


expressApp.post("/twilioData",(req,res)=>{
    console.log(req.body);
    res.end()
});

expressApp.listen(1000,'0.0.0.0',()=>{
    console.log("listening on 5000");
});


module.exports = {
    'server':expressApp
};

