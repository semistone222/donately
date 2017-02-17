package com.semistone.donately.main;

import android.content.Context;
import android.support.annotation.IntDef;

import com.semistone.donately.R;

/**
 * Created by semistone on 2017-02-17.
 */

public class FragmentHelper {

    public static final String[] beneficiaries = {"Animal", "Refugee", "Environment", "Poverty"};

    private static final int ANIMAL = 0;
    private static final int REFUGEE = 1;
    private static final int ENVIRONMENT = 2;
    private static final int POVERTY = 3;

    @IntDef({ANIMAL, REFUGEE, ENVIRONMENT, POVERTY})
    public @interface BeneficiaryType {
    }

    public static Content getContent(Context context, @BeneficiaryType int type) {

        Content content = null;

        switch (type) {
            case POVERTY:
                content = getPoverty(context);
                break;
            case ANIMAL:
                content = getAnimal(context);
                break;
            case ENVIRONMENT:
                content = getEnvironment(context);
                break;
            case REFUGEE:
                content = getRefugee(context);
                break;
            default:
                break;
        }

        return content;
    }

    private static Content getPoverty(Context context) {
        Content content = new Content();
        content.setImageResourceID(R.drawable.poverty);
        content.setTitle(context.getString(R.string.fragment_poverty_title));
        content.setDescription(context.getString(R.string.fragment_poverty_description));
        content.setBeneficiary(context.getString(R.string.fragment_poverty_beneficiary));
        return content;
    }

    private static Content getRefugee(Context context) {
        Content content = new Content();
        content.setImageResourceID(R.drawable.refugee);
        content.setTitle(context.getString(R.string.fragment_refugee_title));
        content.setDescription(context.getString(R.string.fragment_refugee_description));
        content.setBeneficiary(context.getString(R.string.fragment_refugee_beneficiary));
        return content;
    }

    private static Content getAnimal(Context context) {
        Content content = new Content();
        content.setImageResourceID(R.drawable.animal);
        content.setTitle(context.getString(R.string.fragment_animal_title));
        content.setDescription(context.getString(R.string.fragment_animal_description));
        content.setBeneficiary(context.getString(R.string.fragment_animal_beneficiary));
        return content;
    }

    private static Content getEnvironment(Context context) {
        Content content = new Content();
        content.setImageResourceID(R.drawable.environment);
        content.setTitle(context.getString(R.string.fragment_environment_title));
        content.setDescription(context.getString(R.string.fragment_environment_description));
        content.setBeneficiary(context.getString(R.string.fragment_environment_beneficiary));
        return content;
    }

    public static class Content {
        int imageResourceID;
        String title;
        String description;
        String beneficiary;

        public int getImageResourceID() {
            return imageResourceID;
        }

        public void setImageResourceID(int imageResourceID) {
            this.imageResourceID = imageResourceID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getBeneficiary() {
            return beneficiary;
        }

        public void setBeneficiary(String beneficiary) {
            this.beneficiary = beneficiary;
        }
    }
}
