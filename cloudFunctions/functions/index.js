const functions = require('firebase-functions');
const admin = require('firebase-admin');
const server = require('./server');

admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.httpCall = functions.https.onRequest(server.server);
