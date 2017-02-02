package rbsoftware.friendstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SharePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SharePostFragment extends Fragment {
    private static final String ARG_IMAGE_URI = "image_uri";

    private Uri mImageURI;
    private String caption;
    private EditText captionText;

    public SharePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageURI Parameter 1.
     * @return A new instance of fragment SharePostFragment.
     */
    public static SharePostFragment newInstance(Uri imageURI) {
        SharePostFragment fragment = new SharePostFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_URI, imageURI);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageURI = getArguments().getParcelable(ARG_IMAGE_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share_post, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.image);
        image.setImageURI(mImageURI);
        captionText = (EditText) view.findViewById(R.id.caption);
        captionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                caption = s.toString();
            }
        });
    }

    public String getCaption() {
        return caption;
    }

    public void showRequiredCaption() {
        captionText.setError(getString(R.string.error_field_required));
        captionText.requestFocus();
    }
}
