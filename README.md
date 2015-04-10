The java-reference-app-voice is an example application to show how the Bandwidth Application Platform APIs can be used to build mobile voice applications. This example app is intended to work with the catapult-reference-app-voice-android and catapult-reference-app-voice-ios mobile applications to provide a working example of all the components working together to provide voice on your mobile device.

The java-reference-app voice is a spring based application that can be deployed as war in Tomcat or Jetty. These instructions document how to deploy the example on Heroku using jetty runner.

The app exposes a simple REST interface to create an end user and return the credentials that allow the mobile device to connect to the App Platform registrar. A user is created as follows:
 
POST /users
{
	"userName" : "steve", 
	"password" : "asdf1234876"
}

This returns the following:

HTTP/1.1 201 Created 
Location: /v1/users/{user-id}/steve

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

The mobile app creates a user on start-up and then uses the endpoint credentials to register with the App Platform SIP registrar.

To deploy this app on Heroku, you'll need the following:

1. An Application Platform Account. Sign up here from one ()
2. A Heroku account. Sign up here for one ()
3. The Heroku toolbelt installed. See here for instructions on how to do this.
4. A working Java 1.7 development environment.
5. Maven 3.0 or greater

The steps to deploy this are:

1. Clone the repo
2. Edit the properties file with user-id, api-token and api-secret
3. Build it
4. Create a new Heroku site
5. Push the app to the Heroku site
6. Test it

Here are the details for these steps.

1. Clone the repo

From a command prompt run the following:

git clone https://github.com/bandwidthcom/java-reference-app-voice.git
cd java-reference-app-voice

2. Edit the properties file with user-id, api-token and api-secret

With your favorite text editor open the catapult-app-example.properties file and replace the following (these value are available in the App Platform developer console from the Accounts tab):

sandbox.user.id=<your app platform user id>
sandbox.api.key=<your app platform api key>
sandbox.api.secret=<your app platform api secret>
sandbox.api.url=https://api.catapult.inetwork.com
sandbox.api.version=v1


3. Build it

From the command line run:

mvn clean install


4. Create a new Heroku site
5. Push the app to the Heroku site
6. Test it

