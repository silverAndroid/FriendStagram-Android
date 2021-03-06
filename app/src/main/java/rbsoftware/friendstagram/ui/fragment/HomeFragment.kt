package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.temp.RandomString
import rbsoftware.friendstagram.ui.adapter.HomeAdapter

/**
 * Created by Rushil on 8/18/2017.
 */
class HomeFragment : Fragment() {
    private val images: Array<String> = arrayOf(
            "http://4.bp.blogspot.com/-F_6SfcFHKRE/UIjJKWfbt8I/AAAAAAAAA6w/AK5H_oGl9io/s1600/nature182.jpg",
            "http://rising.blackstar.com/wp-content/uploads/2012/08/95432c1c89bd11e1a9f71231382044a1_7-450x450.jpg",
            "https://s-media-cache-ak0.pinimg.com/originals/58/50/77/585077f705e1e1e6385940fee0e6a4d7.jpg",
            "http://blog.ink361.com/wp-content/uploads/2014/11/@hamiltonguevara-.jpg",
            "https://s-media-cache-ak0.pinimg.com/564x/94/a9/db/94a9db37f653d29075f14611c1bd7359.jpg",
            "http://blogcdn.befunky.com/wp-content/uploads/2015/11/Screen-shot-2015-11-09-at-12.55.33-PM.png",
            "http://imgs.abduzeedo.com/files/paul0v2/livefolk/livefolk-07.jpg",
            "http://hahaha.in/wp-content/uploads/2013/04/8e744a5aa38511e2b8e822000a1fbcc7_72.jpg",
            "http://s1.favim.com/610/150710/art-background-beautiful-dark-Favim.com-2933701.jpg",
            "http://66.media.tumblr.com/tumblr_mdd8uyvpgQ1reash6o2_1280.jpg",
            "http://stupiddope.com/wp-content/uploads/2014/07/cory-staudacher-instagram-03.jpg",
            "http://static.boredpanda.com/blog/wp-content/uploads/2015/09/nature-photography-men-of-instagram-16__605.jpg",
            "http://41.media.tumblr.com/eb2d4401b40f60d004c99d289a059590/tumblr_nqeudeGrEZ1u7vbhko2_1280.jpg",
            "http://s14.favim.com/610/160212/adventure-fun-girl-nature-Favim.com-3985099.jpg",
            "http://s8.favim.com/610/150421/alternative-art-background-beautiful-Favim.com-2669488.jpg",
            "http://s7.favim.com/610/151205/beach-boho-bright-instagram-Favim.com-3708977.jpg"
    )
    private val profilePictures = arrayOf(
            "https://rigorous-digital.co.uk/wp-content/uploads/2014/06/profile-round.png",
            "https://premium.wpmudev.org/forums/?bb_attachments=712464&bbat=47619&inline",
            "https://www.aptitudesoftware.com/wp-content/uploads/Round-profile-2.png",
            "http://i1028.photobucket.com/albums/y341/tepandoro/Profile%20Picture%20Round%202_zpskjsfozw5.png",
            "http://www.monkeypodmarketing.com/wp-content/uploads/2015/11/profile-round.png",
            "http://1.bp.blogspot.com/-Alsq9Bnv67k/VA4OnZk13CI/AAAAAAAADug/WaConURUg8M/s1600/Round%2BProfile%2BPic.png",
            "http://www.stephenhusted.com/wp-content/uploads/2014/12/round-profile-pic.png",
            "http://mattijsdevroedt.be/LDNiphoneography/wp-content/uploads/2013/09/round-profile.png",
            "http://xzhpx4b1m3p3qm9eq23m79ga.wpengine.netdna-cdn.com/wp-content/uploads/2014/06/Dr-Halland-Round-Profile-Pic.png",
            "http://www.outsourcecio.co.za/images/tim-profile-pic-round.png",
            "http://www.abbieterpening.com/wp-content/uploads/2013/10/profile-pic-round-200px.png",
            "http://www.ruchikabehal.com/wp-content/uploads/2013/12/Profile-round-shot.jpg",
            "https://media.creativemornings.com/uploads/user/avatar/167913/Adam_LR_Round_Profile_Pic.jpg",
            "http://s3-us-west-2.amazonaws.com/s.cdpn.io/6083/profile/profile-512_1.jpg",
            "http://ablissfulhaven.com/dev/wp-content/uploads/2015/06/round-chokolatta-profile-pic.png",
            "http://helpgrowchange.com/wp-content/uploads/2014/03/tb_profile_201303_round.png"
    )

    private val postGen = RandomString(250)
    private val usernameGen = RandomString(25)
    private val setToolbar: PublishSubject<Toolbar> = PublishSubject.create()
    private val posts: List<Post> = List(images.size, { i ->
        val post = Post(images[i], postGen.nextString())
        post.user = User(usernameGen.nextString(), profilePictures[i])
        post
    })

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = rv
        val progressBar = post_progress
        val adapter = HomeAdapter(posts)

        toolbar?.let { setToolbar.onNext(it) }
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }

    fun getToolbarManipulator() = setToolbar

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}