const  functions = require('firebase-functions');
const  admin = require('firebase-admin');

const buffer = 0;

let Users = {};

const addPing = function (userUid) {
    if(Users[userUid] !== null && Users[userUid] !== undefined){
        return;
    }

    admin.database().ref("users/"+userUid).once('value',(dataSnap)=>{
        const fcmToken = dataSnap.child("fcmToken").val();
        let timeout = dataSnap.child("timeout").val();
        if(timeout === undefined || timeout === null){
            timeout = 30000;
        }
        Users[userUid] = {
            'lastKnownAt': new Date().getTime(),
            'fcmToken':fcmToken,
            'details':dataSnap.val(),
            'timeout':timeout
        };
        console.log(Users[userUid]);
        ////TODO remove old values
        admin.database().ref("locations/"+userUid).remove();
        let userRef = admin.database().ref('locations/'+userUid);
        userRef.on('child_added',(data)=>{
            console.log(data.ref.parent.key);
            const uid = data.ref.parent.key;
            Users[uid].lastKnownAt = new Date().getTime();
            console.log("lastKnownUpdated for user "+uid);
        });
        console.log("starting timer");
        Users[userUid]["ref"]=userRef;
        Users[userUid]["timer"] = setInterval(()=> {
            checkTimer(userUid)
        },timeout);
    })
};

function checkTimer(user) {
    try {
        const lastDiff = new Date().getTime() - Users[user].lastKnownAt;
        let timeOut = Users[user].timeout;
        timeOut += buffer;
        console.log(lastDiff);
        console.log(timeOut);
        if (lastDiff > timeOut) {
            ////Todo raise alarm
            clearInterval(Users[user].timer);
            Users[user]["ref"].off('child_added');
            const fcmToken = Users[user]["fcmToken"];
            admin.messaging().sendToDevice(fcmToken, {
                notification: {
                    body: "We have raised SOS",
                    title: "Raised SOS"
                }
            }).then(console.log);
            Users[user] = null;
            console.log("raise alarm");
            return
        }
        console.log("alarm avoided");
    }catch (e) {
        
    }

}


function stopPingBack(user){
    if(Users[user] === null){
        return;
    }
    clearInterval(Users[user].timer);
    Users[user]["ref"].off('child_added');
    Users[user] = null;
    console.log("track discarded");
}

module.exports={
    'start':addPing,
    'stop':stopPingBack
};