package rbsoftware.friendstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.User;

public class ProfileFragment extends Fragment implements ProfileAdapter.ImageClickListener {

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        User user = new User("vemea", "http://tracara.com/wp-content/uploads/2016/04/aleksandar-radojicic-i-aja-e1461054273916.jpg?fa0c3d");
        user.setName("Aleksandar");
        user.setDescription("Time present & time past are both perhaps present in time future");
        final ProfileAdapter adapter = new ProfileAdapter(user, new ArrayList<>(Arrays.asList(images)), this);

        rv.setAdapter(adapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        rv.setLayoutManager(layoutManager);
        return view;
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
        void update(int posts, int followers, int following);
        void onImageClick(Post post);
    }
}
