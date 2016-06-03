# FuseUniversal

Libraries used:
- AppCompat
- ButterKnife
- Retrofit 2 with Gson converter
- Otto
- Waiting Dots

Main behaviour:
- Keyboard Action Go launches the call to the server by using an IntentService.
- The service runs the request using retrofit and passes the result to the Event Bus (Otto)
- MainActivity is subscribed to the Event Bus.

Suggested test:
- Searching for "dev" is usually taking long. 
- This can be used to test the behaviour after rotation while a search is performed.

Additional notes:
- Having one only activity with such simple layout I decided no to include fragments.
- Instead of using color white on retry I have preferred to save the original Drawable and restore it
