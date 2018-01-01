package co.ghostnotes.trending.main

internal sealed class PermissionState {

    object AskPermission: PermissionState()

    data class PermissionGranted(val accountName: String): PermissionState()

    object PermissionDenied: PermissionState()


}