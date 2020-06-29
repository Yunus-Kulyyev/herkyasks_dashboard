# HerkyAsks Dashboard

HerkyAsks Dashboard is an admin desktop software for the back-end of the Herky Asks Survey Project. It is written in Java and uses Firebase API 
to connect to the back-end server. Purpose of the software is to allow admins to seamlessly create, publish, edit, delete, and download surveys
and survey results.

## Breakdown

### User Side
1. Dashboard loads existing data from the back-end server and displays every active and inactive surveys as well as how many people participated
in each survey.
2. Each individual survey displayed on the dashboard has multiple available actions.
3. Admins can enable or disable each survey. Enabling the survey allows users of the Mobile App to take the survey and vice versa, disabling will
block users from taking the survey.
4. Admin can download survey results in excel format as well as download results for all of the surveys at the same time.
5. Deleting an existing survey from the database.
6. Creating a new Survey that is automatically uploaded to the database and avaialable to users immediately of needed.


## Technology

### Java
Java Programming Language by Oracle
### Firebase
Firebase Authorization and Database is integrated with Flutter, so this was a first choice to use for user registration and user 
credentials as well as storing user survey results
### JDatePicker
Third party Java library for prebuilt calendar and date manipulations (GNU license)

## Suggestions
For suggestions, you can head to "Issues" tab and open a new issue
## Authors

* **Yunus Kulyyev** - [Yunus](https://play.google.com/store/apps/dev?id=8637816620025557781&hl=en_US)
