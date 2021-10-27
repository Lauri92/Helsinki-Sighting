# Helsinki Sighting

Helsinki Sighting is a simple application using MVVM design pattern to help users discover
activities, events and places around Helsinki. It's suitable for use for those who live in
Helsinki (or around), and for those who are staying in the city or nearby areas for a shorter amount
of time.

## Getting started

The application will work just by downloading or cloning the repository, there are no APIs used
which would require any API key setup.

## Features

* #### Discover Activities, Places and Events
    * Activities include items such as: Nature reserve walks, E-Bike tours and Helicopter flights
      and much more..
    * Places include for example: Cafeterias, Hotels, Restaurants...
    * Events contain information about concerts, exhibitions, operas and other events that are
      happening in the city.

* #### Information about the items (if available)
    * Name
    * Short description
    * Images
    * Address
    * Website
    * Opening hours, event dates, where and when activities start and end..
* #### Navigation
    * The application also comes with a map which displays the location of the selected item
    * In addition to this, a simple navigation tool is provided
        * supports travel by foot, bike or car
* #### Multiple ways to find items
    * Application shows items near the user
        * Possibility to adjust the radius from which items are displayed
    * In addition to showing nearby items, users can also search by a tag name to find items from
      each category that interest them without range restrictions
        * Some predefined tags are given for quick search
    * Possibility to add favorites

## Data persistence

The application has simple Room database setup for the purpose of storing favorite id's and a few
other pieces of information, for the purpose of using that data in order to fetch favorited item
from API.

## Web services

The application uses an API called [ *MyHelsinki Open API*](https://open-api.myhelsinki.fi/doc#/),
the API doesn't require any API key for usage. All of the data displayed in the application comes
from this source.

Requested data is fetched by using [Retrofit](https://square.github.io/retrofit/) by using several
different queries and handled in the client by several different datamodels.

## Map and Location

A library called [Osmdroid](https://github.com/osmdroid/osmdroid) is used for creating the map,
markers and navigation in the app's mapview.

User's current location is received
from [fused location provider](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient.html)
, which is provided by the Google Location Services API, part of Google Play Services, which
provides a more powerful, high-level framework that automates tasks such as location provider choice
and power management.

## Highlights of implementation

Overall, development of this application provided an opportunity of strengthening some core basics
about Android development in an environment which is a little bit more challenging. Areas improved
include for example: management of overall project structure, usage of Coroutines with the Helsinki
API and Room database mixed, Recyclerviews and updating data in adapters, using Retrofit with more
complicated datamodels and how to apply scope functions into code more often.

Along the way several new things were learned too, such as how Bottomsheets work and how
Livetemplates can make life much easier, for example when implementing an Adapter for a
Recyclerview.