package ru.sukhikh.appgetcoupon.Settings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import ru.sukhikh.appgetcoupon.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class TermsFragment extends Fragment {

    private View FragmentView;
    private String text;
    private String termsText;

    public TermsFragment(String text){
        this.text=text;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView = inflater.inflate(R.layout.terms_of_service, container, false);

        Toolbar toolbar = (Toolbar) FragmentView.findViewById(R.id.toolbar);
        toolbar.setTitle("Правовые документы");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        JsonTerms jsonTerms = (JsonTerms) new JsonTerms().execute(text);

        return FragmentView;
    }
    public class JsonTerms extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection connection = null;
            InputStream stream = null;

            try {
                URL fileURL = new URL(strings[0]);
                stream = fileURL.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line+"\n");
                }
                termsText = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return termsText;
        }
        @Override
        protected void onPostExecute(final String result) {
            TextView terms = FragmentView.findViewById(R.id.textTerms);
            terms.setText(termsText);
        }
    }
}
