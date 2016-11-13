package rbsoftware.friendstagram;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rbsoftware.friendstagram.model.Post;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private static Post post;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(Post post) {
        PostFragment.post = post;
        return new PostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View pictureView = view.findViewById(R.id.picture);
        PostViewHolder picture = new PostViewHolder(pictureView);
        picture.init(post);
    }
}
