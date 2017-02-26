package rbsoftware.friendstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.User;

public class ProfileFragment extends Fragment implements ProfileAdapter.ImageClickListener {

    private static final String ARG_USERNAME = "username";
    private String[] images = {
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
    };

    private UpdateListener mListener;
    private ToolbarManipulator toolbarManipulator;
    private String username;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ToolbarManipulator manipulator, String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_TOOLBAR_MANIPULATOR, manipulator);
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            toolbarManipulator = (ToolbarManipulator) args.getSerializable(Constants.ARG_TOOLBAR_MANIPULATOR);
            username = args.getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(0);
        toolbarManipulator.setToolbar(toolbar);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        User user = new User(username, "http://tracara.com/wp-content/uploads/2016/04/aleksandar-radojicic-i-aja-e1461054273916.jpg?fa0c3d");
        user.setName("Aleksandar");
        user.setDescription("Time present & time past are both perhaps present in time future");
        final ProfileAdapter adapter = new ProfileAdapter(user, new ArrayList<String>(), this);

        SimpleDraweeView backDrop = (SimpleDraweeView) view.findViewById(R.id.backdrop);
        backDrop.setImageURI(Uri.parse("http://cdn.pcwallart.com/images/cool-backgrounds-hd-space-wallpaper-2.jpg"));

        SimpleDraweeView profilePicture = (SimpleDraweeView) view.findViewById(R.id.profile);
        profilePicture.setImageURI(Uri.parse(user.getProfilePictureURL()));

        rv.setAdapter(adapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        rv.setLayoutManager(layoutManager);

        update(25, 34, 10, user.getProfilePictureURL(), view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateListener) {
            mListener = (UpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void update(int posts, int followers, int following, String profilePictureURL, View mainView) {
        TextView numPosts = (TextView) mainView.findViewById(R.id.num_posts);
        TextView numFollowers = (TextView) mainView.findViewById(R.id.num_followers);
        TextView numFollowing = (TextView) mainView.findViewById(R.id.num_following);
        SimpleDraweeView profilePicture = (SimpleDraweeView) mainView.findViewById(R.id.profile);

        SpannableString postsString = new SpannableString(posts + "\nPOSTS");
        SpannableString followersString = new SpannableString(followers + "\nFOLLOWERS");
        SpannableString followingString = new SpannableString(following + "\nFOLLOWING");
        RelativeSizeSpan largerText = new RelativeSizeSpan(2f);
        int postsLength = Integer.toString(posts).length();
        int followersLength = Integer.toString(followers).length();
        int followingLength = Integer.toString(following).length();

        postsString.setSpan(largerText, 0, postsLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        followersString.setSpan(largerText, 0, followersLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        followingString.setSpan(largerText, 0, followingLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        numPosts.setText(postsString);
        numFollowers.setText(followersString);
        numFollowing.setText(followingString);
        profilePicture.setImageURI(Uri.parse(profilePictureURL));
    }

    @Override
    public void onImageClick(Post post) {
        mListener.onImageClick(post);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface UpdateListener {
        void onImageClick(Post post);
    }
}
