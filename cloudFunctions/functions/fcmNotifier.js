const  functions = require('firebase-functions');
const  admin = require('firebase-admin');
const secret = require('./secretKey');


// try {
//     admin.initializeApp({
//         credential: admin.credential.cert(secret),
//         databaseURL: "https://suraksha-9c58a.firebaseio.com"
//     });
// }catch (e) {
//
// }


const askHelp = function (lat,lon,user) {

    return admin.messaging().sendToTopic('help', {
            data:{
                'type':'fcmLocationBroadcast',
                'lat':lat.toString(),
                'lon':lon.toString(),
                'radius':'500.0',
                'user':user
            }
        })

};


module.exports = {
    'raiseHelpRequest':askHelp,
};