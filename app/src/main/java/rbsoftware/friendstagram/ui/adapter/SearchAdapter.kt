package rbsoftware.friendstagram.ui.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.ui.activity.UserProfileActivity

/**
 * Created by silve on 1/18/2018.
 */
class SearchAdapter(private val users: List<User>, private val context: Context) : RecyclerView.Adapter<SearchAdapter.ResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.username.text = user.username
        holder.profilePicture.setImageURI(user.profilePictureURL)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(UserProfileActivity.ARG_USERNAME, user.username)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = users.size

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name by lazy { itemView.findViewById(R.id.name) as TextView }
        val username by lazy { itemView.findViewById(R.id.username) as TextView }
        val profilePicture by lazy { itemView.findViewById(R.id.profile) as SimpleDraweeView }
    }
}