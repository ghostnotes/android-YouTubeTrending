package co.ghostnotes.trending.main

internal sealed class GooglePlayServicesState {

    object CheckGooglePlayServicesAvailable: GooglePlayServicesState()

    object GooglePlayServicesAvailable: GooglePlayServicesState()

    object GooglePlayServicesUnavailable: GooglePlayServicesState()

}