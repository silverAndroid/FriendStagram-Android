package rbsoftware.friendstagram.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Rushil on 8/18/2017.
 */
data class Post(@SerializedName("url") val imageURL: String, val description: String) : Parcelable {
    var id = 0
    @SerializedName("tags")
    var hashtags: List<String> = listOf()
    lateinit var user: User

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
        user = parcel.readParcelable(User::class.java.classLoader)
        id = parcel.readInt()
        parcel.readStringList(hashtags)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageURL)
        parcel.writeString(description)
        parcel.writeParcelable(user, flags)
        parcel.writeInt(id)
        parcel.writeStringList(hashtags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}