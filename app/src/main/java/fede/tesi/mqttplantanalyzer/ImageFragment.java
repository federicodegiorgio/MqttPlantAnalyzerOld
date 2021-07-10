package fede.tesi.mqttplantanalyzer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.ByteArrayOutputStream;

import fede.tesi.mqttplantanalyzer.databinding.FragmentSecondBinding;
import fede.tesi.mqttplantanalyzer.databinding.ImageFragmentBinding;

public class ImageFragment extends Fragment {
    private ImageFragmentBinding binding;
    private DatabaseReference mDatabase;

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
        mDatabase = FirebaseDatabase.getInstance("https://mqttplantanalyzer-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("ex").setValue("Hello");
        DatabaseReference userRef = mDatabase.child("user");
        ImageView image =(ImageView)view.findViewById(R.id.imageCam);
        //encode image to base64 string
        byte[] imageAsBytes = Base64.decode("/9j//g==EgtRBFEEAgIFAwMDBAUFBgYFBQUFBgcJBwYGCAYFBQgKCAgJCQp2CgoGBwsLCwkLCQkKCQECAgICAgIEAgIECQYFBgkJCQl2CQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQl2CQkJCQkJCQkJCQkJCQn//g==IQ==AwQFBgcICQoLAQ==BgcICQoLEA==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ToXra40lVxnORjsKjwoP4nHHFNvW6FZoaXjClQx47hd28aa23aZMfw5zjn/PFK/LqGr2BmjVduMnPTH40wsi8Hl2HfihtXYut2NLKTyOpAwFpFPzgAHqRjv/jPOM8jPA/CkYqPu8nGc4pc3QVnuJuCkbuee4HNAaNlx2bD/9eqvpuOzWx//T/MM7gNwU888g0wqc84AB71KldEN2kuwxuCcj6DGKaWx+8GDjjFVdoNL+6MPORwuT1xTMZGR2KPrT5nsClYjOMgFRj8TTXGATj2zUxm+qH190Yck/d5N2zSMQMAKPQ8U03tYkjIYsNowPQikbK9AMHHbtRza2sO92JDSSVGT0bHB6UjYZuRz3OMc01JqWwk30G9FC8Dn+IVF2sNucDB6+gpqVktB8zFYh2LEe/TrSqHXhcc8EkH/PpSh2ya2C9lYQA4wByGpApKZwB8wOKSnIezP/1PzEO7AOz8B2CmMzYA==2TC7QyVyG6Dn2pjnMYwoPPIxVaX5gi9NRjPxkEcdBj126aXPpz16UkmtGNtrYazZyrL7Eg1GzHHI5+nShaXVxXl2AXDLymQRnp7U0MRn5RgYJOKVv5WCbI2kz1HOM9OlKXB2W/eRDA5wRQutxOTGO+CDtw==uZtNik9cRrg8dKYDuJ4GB1GKWytcvn6scuQPmUcYGaR2ZjghlA5x0ov1uDfY/9X8wiQT1bpj07U18HufvdcipUt2l1JvG1hjlS3G7Gc88Uwj5cn8qfMiebW6GYUdzx3A61F2v8wIG715qpS6IeijoMY=toNvXQZhR94tnIxxSfI=jvQVB+Yg44OB/n6VUZPqDabIsqcNk46cLR8rD5QT7Yp2Lpu6HrcaduA7dv8=pUYI+bJOPSjAA9zy2OlPm1NNLBhACuDweOgzTWCEEsV2s8HBOKUZXvqS7LU/////2XY=", Base64.DEFAULT);

        image.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

