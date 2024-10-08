﻿# Task_PlenigoAPI

Spring Shell and the Plenigo API

---
Create a tiny Spring Shell project with Gradle
https://spring.in/projects/spring-shell
https://gradle.org/

Familiarize yourself with the plenigo API

At https://api.plenigo-stage.com you get information how to authenticate against the plenigo API and at https://github.com/plenigo/plenigo-doc/blob/main/faqs/CURSORS.md you find all information you need to know about paging

## The Task
1. Get the first 300 orders
2. Get the email address and the creation date of the customer by loading the customer that belongs to the order
3. If there are invoices that are associated with the order also retrieve this information
4. Create a CSV file that has the order id (the order id can spread over multiple lines in case of multiple order items), order item position, title, price, tax rate, customer id, customer email of the invoice customer, customer creation date of the invoice customer, invoice id, invoice date
   
### API Key

The API key is provided by you via email.

### Additional Information

If this task takes too much time or energy, please let us know. If you have any questions feel free to ask.

---
9 August 2024

DONE:
- csv created with all the fields
- if order contains multiple positions, csv contains multiple rows for the same orderId
- API_KEY is of course hidden. To use your key, please create a file env.properties and put a variable in it named api.key=YOUR_API_KEY

TO DO:
- implement paging (csv shows up to 100 orders)
- fix price, sometimes it's not displayed

10 August 2024

DONE:
- implemented paging - method accepts any number, if greater than 100, it downloads 100 by 100 and the rest in the last call - no downloading in vain :)
- added logs for user, to be notified about the progress

I wish I had time to:
- handle all the corner cases, unchecked casts and assignments
- create files with custom names - for example add date, time
- check that all data is correctly parsed - price looks strange in excel, but I didn't have time to compare
- make the code prettier

But I am going on vacation, yay!! :)
