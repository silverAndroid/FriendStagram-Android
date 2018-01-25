package rbsoftware.friendstagram.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Rushil on 8/18/2017.
 */
data class User(val username: String, @SerializedName("profile_picture_url") val profilePictureURL: String?, val name: String = "", val description: String? = "") : Parcelable {
    var posts: MutableList<Post> = mutableListOf()
    @SerializedName("profile_background_url")
    var backgroundPictureURL: String? = null
    @SerializedName("following")
    var followingUserIDs: MutableList<User> = mutableListOf()
    @SerializedName("followers")
    var followerUserIDs: MutableList<User> = mutableListOf()
    var biography: String? = ""

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    ) {
        parcel.readTypedList(posts, Post.CREATOR)
        backgroundPictureURL = parcel.readString()
        parcel.readParcelableArray(followingUserIDs::class.java.classLoader)
        parcel.readParcelableArray(followerUserIDs::class.java.classLoader)
        biography = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(profilePictureURL)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeTypedList(posts)
        parcel.writeString(backgroundPictureURL)
        parcel.writeParcelableArray(followingUserIDs.toTypedArray(), 0)
        parcel.writeParcelableArray(followerUserIDs.toTypedArray(), 0)
        parcel.writeString(biography)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}