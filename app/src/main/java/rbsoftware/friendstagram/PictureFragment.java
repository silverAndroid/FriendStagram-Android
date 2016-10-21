package rbsoftware.friendstagram;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rbsoftware.friendstagram.model.Post;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {

    private static Post post;

    public PictureFragment() {
        // Required empty public constructor
    }

    public static PictureFragment newInstance(Post post) {
        PictureFragment.post = post;
        return new PictureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        View pictureView = view.findViewById(R.id.picture);
        PictureViewHolder picture = new PictureViewHolder(pictureView);
        picture.init(post);
        return view;
    }

}
