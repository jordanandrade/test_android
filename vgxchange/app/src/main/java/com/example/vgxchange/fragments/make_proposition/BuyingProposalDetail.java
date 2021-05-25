package com.example.vgxchange.fragments.make_proposition;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.vgxchange.Constants;
import com.example.vgxchange.R;
import com.example.vgxchange.model.BuyingProp;
import com.example.vgxchange.model.ProductAnnounce;
import com.example.vgxchange.tool.DateTool;
import com.example.vgxchange.tool.PropositionState;
import com.example.vgxchange.tool.PropositionType;

import java.io.InputStream;
import java.net.URL;


public class BuyingProposalDetail extends Fragment {

    BuyingProp proposalToShow;
    ProductAnnounce productToShow;
    TextView name, description, price, announcerName, category, parutionDate,
            announcerMail, announcerPseudo, proposedPrice, propDateStart,
            propDateEnd, propFrom, propTo, propRentalLabel,
            propState;
    ImageView productImage;
    Button btnGoToRating;


    public BuyingProposalDetail() {
    }

    public static BuyingProposalDetail newInstance() {
        BuyingProposalDetail fragment = new BuyingProposalDetail();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buying_proposal_detail, container, false);
        if (getArguments() != null) {
            proposalToShow = (BuyingProp) getArguments().getSerializable(Constants.selectedBuyingProposalBundleKey);
            view = mapWithFields(view, proposalToShow);
            btnGoToRating = view.findViewById(R.id.btnGoToRating);


            if (proposalToShow.propositionState == PropositionState.WAITING.ordinal()) {
                btnGoToRating.setVisibility(View.GONE);

            } else if (proposalToShow.propositionState == PropositionState.ACCEPTED.ordinal()) {
                btnGoToRating.setText("Procéder au paiement");
                btnGoToRating.setOnClickListener(v -> {
                    Bundle selectedPropositionBundle = new Bundle();
                    selectedPropositionBundle.putSerializable(Constants.selectedBuyingProposalBundleKey, proposalToShow);
                    Navigation.findNavController(v).navigate(R.id.action_buyingProposalDetail_to_paymentFragment, selectedPropositionBundle);
                });

            } else if (proposalToShow.propositionState == PropositionState.DECLINED.ordinal()) {
                btnGoToRating.setVisibility(View.GONE);
            } else if (proposalToShow.propositionState == PropositionState.TERMINATED.ordinal()) {
                btnGoToRating.setText("Evaluer l'echange");
                btnGoToRating.setOnClickListener(v -> {
                    Bundle selectedPropositionBundle = new Bundle();
                    selectedPropositionBundle.putSerializable(Constants.selectedBuyingProposalBundleKey, proposalToShow);
                    Navigation.findNavController(v).navigate(R.id.action_buyingProposalDetail_to_ratingFragment, selectedPropositionBundle);
                });
            } else {
                btnGoToRating.setVisibility(View.GONE);
            }


        } else {
            Toast.makeText(getContext(), "Erreur de chargement des informations", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    public View mapWithFields(View v, BuyingProp buyingProp) {
        if (buyingProp != null) {
            name = v.findViewById(R.id.detailGameName);
            announcerName = v.findViewById(R.id.person_name);
            announcerMail = v.findViewById(R.id.person_email);
            announcerPseudo = v.findViewById(R.id.person_login);
            parutionDate = v.findViewById(R.id.detailParutionDate);
            price = v.findViewById(R.id.detailPrice);
            category = v.findViewById(R.id.detailProductCategory);
            proposedPrice = v.findViewById(R.id.propPrice);
            propFrom = v.findViewById(R.id.propFromDate);
            propTo = v.findViewById(R.id.propToDate);
            propDateStart = v.findViewById(R.id.propDateStart);
            propDateEnd = v.findViewById(R.id.propDateEnd);
            propDateEnd = v.findViewById(R.id.propDateEnd);
            propState = v.findViewById(R.id.propState);

            name.setText(buyingProp.productAnnounce.game.getName());
            announcerName.setText(buyingProp.productAnnounce.announcer.getName());
            announcerMail.setText(buyingProp.productAnnounce.announcer.getMail());
            announcerPseudo.setText(buyingProp.productAnnounce.announcer.getLogin());
            price.setText(Double.toString(buyingProp.productAnnounce.getPrice()));
            category.setText(buyingProp.productAnnounce.getGame().getCategory().getLabel());
            parutionDate.setText(DateTool.formatStringDateDDMMYY(buyingProp.productAnnounce.getParution()));
            proposedPrice.setText(Double.toString(buyingProp.getProposedAmount()));
            propState.setText(PropositionState.values()[proposalToShow.getPropositionState()].getDisplayValue());
            v = setProductImage(v, buyingProp.productAnnounce.getPhotoLink());



            try {
                propState.setBackgroundColor(Color.parseColor(PropositionState.values()[proposalToShow.propositionState].getColorString()));
            } catch (Exception e) {
                Log.d("PARSE COLOR ERROR", e.getMessage());
            }

            if (proposalToShow.propositionType == PropositionType.RENTAL.ordinal()) {
                propDateStart.setText(DateTool.formatStringDateDDMMYY(proposalToShow.getRentalStart()));
                propDateEnd.setText(DateTool.formatStringDateDDMMYY(proposalToShow.getRentalEnd()));
            } else {
                propFrom.setVisibility(View.GONE);
                propTo.setVisibility(View.GONE);
                propDateStart.setVisibility(View.GONE);
                propDateEnd.setVisibility(View.GONE);
            }

        }

        return v;
    }

    public View setProductImage(View v, String url) {
        productImage = v.findViewById(R.id.detailProductImage);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = (InputStream) new URL(url).getContent();
                    Drawable d = Drawable.createFromStream(is, url);
                    productImage.setImageDrawable(d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return v;
    }

}