### Bandwidth Java Server Example for Mobile Voice Clients

The java-reference-app-voice is an example application to show how the Bandwidth Application Platform APIs can be used to build mobile voice applications. This example app is intended to work with the catapult-reference-app-voice-android and catapult-reference-app-voice-ios mobile applications to provide a working example of all the components synched together to provide SIP voice on your mobile device.

The java-reference-app voice is a spring based application that can be deployed as war in Tomcat or Jetty. These instructions document how to deploy the example on Heroku using jetty runner.

The java-reference-app-voice app exposes a simple REST interface to create an end user and return the credentials that allow the mobile device to connect to the App Platform registrar. A user is created as follows:
 
```
 POST /users
{
	"userName" : "steve", 
	"password" : "asdf1234876"
}
```


This returns the following:

```
HTTP/1.1 201 Created 
Location: /users/steve

{
	“userName”: “steve”,
	“phoneNumber”: “+13031234567”,
    “endpoint”: 
    {
    	"id": "re-4kafmcjrd3hkrjyjf7o7v2q",
    	"name": "steve",
    	"domainId": "rd-5vvpzrsuwhiynrgkfw35miy",
    	"enabled": true,
    	"applicationId": "a-xiaoeen22dr44gfy5bpqgpy",
    	"credentials": {
      		"realm": "myDomain.bwapp.bwsip.io",
      		"username": "steve"
    	},
    	"sipUri": "sip:steve@myDomain.bwapp.bwsip.io"
    }
}
```

The mobile app calls the server on start-up to create a user. It then uses the endpoint credentials to register with the App Platform SIP registrar.

The java-reference-app-voice app also processes calls. When creating the user, it associates the new App Platform phone number to an App Platform application, with the server url as the callbackUrl. This means all calls to that number are routed to the The java-reference-app-voice app.

When a call from the device comes in, the java-reference-app-voice app looks up the user associated with the SIP url to get the phoneNumber for that user. It then makes an outbound call to that number.

When a call is made to the phoneNumber, the java-reference-app-voice app looks up the sipUri for that phone and makes an outbound call to the device.

All this is enabled with the single REST API call to create a user.

### Prerequisites
To deploy this app on Heroku, you'll need the following:

1. An Application Platform Account. Sign up [here](https://catapult.inetwork.com/pages/signup.jsf) from one 
2. A Heroku account. Sign up [here](https://signup.heroku.com/www-header) for one 
3. The Heroku toolbelt installed. See [here](https://toolbelt.heroku.com/) for instructions on how to do this.
4. A working Java 1.7 development environment.
5. Maven 3.0 or greater

### Steps to deploy

1. Clone the repo
2. Build it
3. Create a new Heroku site
4. Edit the properties file with user-id, api-token and api-secret, heroku server
5. Push the app to the Heroku site
6. Test it

#### Clone the repo

From a command prompt run the following:

```
git clone https://github.com/bandwidthcom/java-reference-app-voice.git
cd java-reference-app-voice
```

#### Build it

From the command line run:

```
mvn clean install
```

#### Create a new Heroku app

From the command line run the following:

```
heroku create
```

#### Edit the properties file with user-id, api-token and api-secret, heroku-url

With your favorite text editor open the src/main/resources/catapult-app-example.properties file and replace the following (these value are available in the App Platform developer console from the Accounts tab):

```
sandbox.user.id=<your app platform user id>
sandbox.api.key=<your app platform api token>
sandbox.api.secret=<your app platform api secret>
sandbox.api.url=https://api.catapult.inetwork.com
sandbox.api.version=v1
sandbox.base.url=<your_herorku_server_url>
```

#### Push the app to the Heroku site

From the command line run the following:

```
git add .
git commit -m "updated config file"
git push heroku master
heroku open
```

#### Test it

Use the url from the heroku app to run the following curl command:

```
curl -H 'Content-Type: application/json' -d '{"userName" : "user1", "password" : "j7qIpoX48"}' -XPOST 'https://<heroku-site-url>/users'
```

Which returns:

```
{
    "userName":"user1",
    "endpoint":{"id":"re-ay7fbfphmovcypwwcqsnssy","name":"uep-oKz9hdQAnwZS","domainId":"rd-qflpsmz47yic54xqkmr36pq","enabled":true,"sipUri":"sip:uep-oKz9hdQAnwZS@ud-H6Z0z9y3VG53.bwapp.bwsip.io","credentials":{"realm":"ud-H6Z0z9y3VG53.bwapp.bwsip.io","username":"uep-oKz9hdQAnwZS"}},
    "phoneNumber":"+14692137316"
    
}
```



