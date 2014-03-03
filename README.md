Cineworld movie listings and ratings webservice
====
The Cineworld Unlimited card is pretty awesome, but I always find myself checking every films IMDb rating. Too manual; I want to see the ratings inline. So I created a Chrome extension: [Cineworld Ratings](https://chrome.google.com/webstore/detail/cineworld-ratings/aeihmofihjacjlkecnjpoicmaaandnnc). This is the RESTful backend for that extension.


Getting Started
====
Get an API key for Rotten Tomatoes and Cineworld. Modify application.conf to include your API keys.
If you have gradle installed, type 'gradle run' in your terminal. If not, './gradlew run'.

Check it's working by browsing to the listings for [my local cinema](http://localhost:9001/api/cinema/66).

License (GPLv3)
----
Copyright (c) Greg Dorrell

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
