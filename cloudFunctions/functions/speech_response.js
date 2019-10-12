export function handler(context, event, callback) {
    const twiml = require('twilio').twiml.VoiceResponse;
    const twiML = new twiml();
  
    const command = event.SpeechResult.toLowerCase();
    console.log(command);
    switch(command) {
      case 'cat':
        twiml.say('Fetching your cat fact.');
        //twiml.redirect('cat-facts');
        break;
      case 'number':
        twiml.say('Fetching your number fact.');
        //twiml.redirect('/number-facts');
        break;
      case 'chuck norris':
        twiml.say('Fetching your chuck norris fact.');
        //twiml.redirect('/chuck-facts');
        break;
      default:
        twiml.say(`Sorry but I do not recognize ${command} as a valid command. Try again.`);
        //twiml.redirect('/facts');
        break;
    } 
    callback(null, twiML);
  }