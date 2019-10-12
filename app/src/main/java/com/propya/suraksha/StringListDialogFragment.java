package com.propya.suraksha;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.propya.suraksha.StringListDialogFragment.*;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     StringListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link Listener}.</p>
 */
public class StringListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARGS = "string_array";
    private Listener mListener;
    ArrayList<String> newArrayList;
    Context c;
    float steps,end = .5f;


    // TODO: Customize parameters
    public static StringListDialogFragment newInstance(String title,ArrayList<String> stringArrayList) {
        final StringListDialogFragment fragment = new StringListDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArrayList(ARGS,stringArrayList);
        args.putString("title",title);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_string_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        final TextView titleTextView = (TextView)view.findViewById(R.id.bottomTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle bundle = getArguments();
        newArrayList=bundle.getStringArrayList(ARGS);
        titleTextView.setText(bundle.getString("title"));
        recyclerView.setAdapter(new StringAdapter(getContext(),newArrayList));
    }

    public void attachListener(Listener listener){
        this.mListener = listener;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }



    public interface Listener {
        void onStringClicked(int position);
    }



    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        View alpha;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_string_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            alpha = itemView.findViewById(R.id.backGroundImage);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onStringClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });

        }

    }

    private class StringAdapter extends RecyclerView.Adapter<ViewHolder> {

//        private final int mItemCount;
        ArrayList<String> options;

        StringAdapter(Context c, ArrayList<String> options) {
            this.options=options;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            steps = ((1f-end)/newArrayList.size());
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(options.get(position));
            holder.alpha.setAlpha(1f-(steps*(newArrayList.size()-1-position)));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

    }

}
