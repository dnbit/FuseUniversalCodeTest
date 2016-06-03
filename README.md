# Fuse Universal Code Test

## Test description

1. Create a new public git repo on Github

2. Create new project using your preferred development platform (i.e iOS dev use XCode, Android use Android Studio):

  iOS:Create a XCode project with Cocoapods setup (https://cocoapods.org/)

  Android:Create new Android Studio project with Gradle setup

3. Add a textfield which is width of the device and set the keyboard type to Done or Go

4. On keyboard return make a network request to validate the company name using the following API:

  GET https://[COMPANY NAME].fusion-universal.com/api/v1/company.json

  Note: please replace [COMPANY NAME] with the text from the textfield

5. If the response is successful (status code 200) make the textfield green otherwise make it red

  Replace the textfield text with the response name Company name request: fusion

  Request example: https://fusion.fusion-universal.com/api/v1/company.json
    
  Response example:
    ```
    {"name": "Fuse","logo": "http://fusion.fusion-universal.com","custom_color":"#ea2184","password_changing":{"enabled":false,"secure_field": null}}
    ```
6. On textfield retry reset the textfield colour to white

7. Commit code to Github once complete

## Implementation details

### Libraries used:
- AppCompat
- ButterKnife
- Retrofit 2 with Gson converter
- Otto
- Waiting Dots

### Main behaviour:
- Keyboard Action Go launches the call to the server by using an IntentService.
- The service runs the request using retrofit and passes the result to the Event Bus (Otto)
- MainActivity is subscribed to the Event Bus.

### Suggested test:
- Searching for "dev" is usually taking long. 
- This can be used to test the behaviour after rotation while a search is performed.

### Additional notes:
- Having one only activity with such simple layout I decided no to include fragments.
- Instead of using color white on retry I have preferred to save the original Drawable and restore it
