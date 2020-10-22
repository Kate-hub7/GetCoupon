package ru.sukhikh.appgetcoupon.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import ru.sukhikh.appgetcoupon.Onboard.CustomIntro;

import ru.sukhikh.appgetcoupon.R;
import ru.sukhikh.appgetcoupon.Settings.FeedbackFragment;
import ru.sukhikh.appgetcoupon.Settings.ShareFragment;
import ru.sukhikh.appgetcoupon.Settings.TermsFragment;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ListFragment extends Fragment{

    private View FragmentView;
    private List<String> settings;

    public ListFragment(List<String> settings){
        this.settings=settings;
    }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView = inflater.inflate(R.layout.settings_activity, container, false);
        getFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment(settings)).commit();

        return FragmentView;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private List<String> settings;
        private static Boolean notifications;
        SettingsFragment(List<String> settings){
            this.settings=settings;
        }

        public static Boolean getPushState(){return notifications;}

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference myPref = (Preference) findPreference("service");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new TermsFragment(settings.get(2)));
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }
            });

            Preference messagePref = (Preference)findPreference("review");
            messagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new FeedbackFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }
            });

            Preference sharePref = (Preference)findPreference("share");
            sharePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new ShareFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }
            });

            Preference cooperationPref = (Preference)findPreference("cooperation");
            cooperationPref.setSummary(settings.get(1));
            cooperationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{settings.get(1)});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Выберите email клиент :"));
                    return true;
                }
            });

            Preference ratePref = (Preference)findPreference("rate");
            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.get(0)));
                    startActivity(browserIntent);
                    return true;
                }
            });

            Preference newPref = (Preference)findPreference("new");
            newPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), CustomIntro.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference cardPref = (Preference)findPreference("card");
            cardPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.get(3)));
                    startActivity(browserIntent);
                    return true;
                }
            });

            final SwitchPreferenceCompat pushPref = (SwitchPreferenceCompat)findPreference("push");
            if(notifications==null){
                if (!NotificationManagerCompat.from(getActivity()).areNotificationsEnabled())
                    pushPref.setChecked(false);
                else
                    pushPref.setChecked(true);
            }
            if(!NotificationManagerCompat.from(getActivity()).areNotificationsEnabled())
                pushPref.setChecked(false);
            notifications=pushPref.isChecked();
            pushPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    if(pushPref.isChecked()) {
                        if (!NotificationManagerCompat.from(getActivity()).areNotificationsEnabled()) {
                            new SweetAlertDialog(getContext())
                                    .setTitleText("Нет доступа к уведомлениям")
                                    .setContentText("Пожалуйста, включите уведомления для приложения GetCoupon в настройках")
                                    .show();
                            pushPref.setChecked(false);
                        }
                        else
                            notifications  = true;
                    }
                    else
                        notifications = false;
                    return true;
                }
            });
        }
    }
}