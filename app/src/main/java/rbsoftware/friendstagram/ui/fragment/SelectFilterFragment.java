package rbsoftware.friendstagram.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import rbsoftware.friendstagram.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectFilterFragment extends Fragment {
    private static final String ARG_IMAGE_URI = "image_uri";

    private Uri imageURI;

    public SelectFilterFragment() {
        // Required empty public constructor
    }

    public static SelectFilterFragment newInstance(Uri imageURI) {
        SelectFilterFragment fragment = new SelectFilterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_URI, imageURI);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageURI = getArguments().getParcelable(ARG_IMAGE_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.image);
        image.setImageURI(imageURI);
    }
}
