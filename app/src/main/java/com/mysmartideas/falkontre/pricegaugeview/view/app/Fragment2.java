package com.mysmartideas.falkontre.pricegaugeview.view.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mysmartideas.falkontre.pricegaugeview.R;
import com.mysmartideas.falkontre.pricegaugeview.view.PriceGaugeView;


public class Fragment2 extends Fragment {

	private PriceGaugeView priceGaugeView;
	private ImageView mButton;

	public static Fragment2 newInstance() {
		return new Fragment2();
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_2, null);
		priceGaugeView = (PriceGaugeView) view.findViewById(R.id.sesame_view);
		priceGaugeView.setPrices(2.37f, 4.89f);
		mButton = (ImageView) view.findViewById(R.id.btn);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				priceGaugeView.setSesameValues(3.50f);
			}
		});

		return view;
	}
}
