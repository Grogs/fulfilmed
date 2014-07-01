UK Cinema movie listings and ratings webservice
====

A RESTful webservice to wrap a few APIs and provide a nicer now-showing API for Cineworld & Odeon cinemas. Used by [gregd.me/cineworld](http://gregd.me/cineworld/) and an older Chrome extension: [Cineworld Ratings](https://chrome.google.com/webstore/detail/cineworld-ratings/aeihmofihjacjlkecnjpoicmaaandnnc) I wrote.


Getting Started
====
Configuration
----
The application requires API keys for; Rotten Tomatoes, Cineworld, and The Movie Database.
After obtaining the API keys, you'll need to modify src/main/resources/application.conf to include them.

Running locally
----
If you have gradle installed, type 'gradle run' in your terminal. If not, './gradlew run'.

Check it's working by browsing to the listings for [my local cinema](http://localhost:9001/api/cinema/66).

Making Changes
----
When call 'gradle run', 'JettyBootstrap' is executed - which is using the 'Config' object where all the main components of the service are setup.

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
