package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.bus_traker_manager.R;

public class HomeFragment extends Fragment {
    private CardView busInfoCard;
    private TextView busInfoText;
    private Button scanQrButton;
    private ActivityResultLauncher<Intent> qrScanLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        busInfoCard = view.findViewById(R.id.busInfoCard);
        busInfoText = view.findViewById(R.id.busInfoText);
        scanQrButton = view.findViewById(R.id.buttonScanQr);
        busInfoCard.setVisibility(View.GONE);
        qrScanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    String busInfo = result.getData().getStringExtra("busInfo");
                    if (busInfo != null) {
                        busInfoCard.setVisibility(View.VISIBLE);
                        busInfoText.setText(busInfo);
                    }
                }
            }
        );
        scanQrButton.setOnClickListener(v -> startQrScan());
        return view;
    }

    private void startQrScan() {
        Intent intent = new Intent(getActivity(), QRCheckInActivity.class);
        qrScanLauncher.launch(intent);
    }
}
