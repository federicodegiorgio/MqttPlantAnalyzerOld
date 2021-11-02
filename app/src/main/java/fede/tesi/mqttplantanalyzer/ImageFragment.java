package fede.tesi.mqttplantanalyzer;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fede.tesi.mqttplantanalyzer.databinding.ImageFragmentBinding;

public class ImageFragment extends Fragment {
    private ImageFragmentBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private List<StorageReference> listImageRef=new ArrayList<>();
    ListView listView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ImageFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView=(ListView)view.findViewById(R.id.imagesList);
        final ImageArrayAdapter adapter = new ImageArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listImageRef);
        listView.setAdapter(adapter);
        storageRef = storage.getReference();
        //1634133409188.JPG
        StorageReference pathReference = storageRef.child("KSHwKdsgiNe5kgh1eKTXJZHGxxq1/246f28969298");
        pathReference.listAll()
            .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        // All the prefixes under listRef.
                        // You may call listAll() recursively on them.
                    }

                    for (StorageReference item : listResult.getItems()) {
                        listImageRef.add(item);
                    }
                    Collections.sort(listImageRef,Collections.reverseOrder());
                    listView.setAdapter(adapter);
                }
            });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                StorageReference value=adapter.getItem(position);
                Log.e("VALUE REF",value.getName());
                StorageReference pathObjReference = storageRef.child(value.getName());
                Intent i =new Intent(getActivity().getBaseContext(),ImageActivity.class);
                i.putExtra("imm","KSHwKdsgiNe5kgh1eKTXJZHGxxq1/246f28969298/"+value.getName());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getBaseContext().startActivity(i);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

