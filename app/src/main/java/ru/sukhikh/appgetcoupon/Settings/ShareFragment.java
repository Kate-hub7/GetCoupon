package ru.sukhikh.appgetcoupon.Settings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import ru.sukhikh.appgetcoupon.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ShareFragment extends Fragment {

    private View FragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentView = inflater.inflate(R.layout.email, container, false);

        final EditText textMessage = (EditText) FragmentView.findViewById(R.id.editTextMessage);
        textMessage.setHint("Отправьте нам ссылку на сайт, где вы нашли купон ");
        final Button buttonSend = (Button) FragmentView.findViewById(R.id.buttonSend);
        TextView textTitle = FragmentView.findViewById(R.id.textViewMessage);
        textTitle.setText("Поделитесь Вашим промокодом");

        Toolbar toolbar = (Toolbar) FragmentView.findViewById(R.id.toolbar);
        toolbar.setTitle("Поделиться");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String url = "http://closeyoureyes.jelastic.regruhosting.ru/add-promocode";
                String message = textMessage.getText().toString();
                if (message.length()==0){
                    return;
                }

                JsonPostPromocode w = (JsonPostPromocode) new JsonPostPromocode().execute(url, message);
                Toast.makeText(getActivity(), "Ваше предложение отправлено", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();

            }
        });

        textMessage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().length()!=0)
                    buttonSend.setTextColor(getResources().getColor(R.color.black));
                else
                    buttonSend.setTextColor(getResources().getColor(R.color.gray_btn_bg_color));
            }
        });

        return FragmentView;
    }

    public class JsonPostPromocode extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            HttpURLConnection connection = null;
            DataOutputStream bos = null;

            try {
                connection = (HttpURLConnection) new URL(strings[0]).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "GetCoupon Android 0.0");
                connection.setRequestProperty("Authorization", "Basic YWxpdmUtZ2V0Y291cG9uLXVzZXI6OW1sNzBEbzdqWHRtMDVIS0s=");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();

                JSONObject jo = new JSONObject();
                jo.put("promocode", strings[1]);

                bos = new DataOutputStream(connection.getOutputStream());
                String test = jo.toString();

                bos.write(test.getBytes(StandardCharsets.UTF_8));

                Log.d("", "server response: " + connection.getResponseCode());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(connection!=null)
                        connection.disconnect();
                    if(bos!=null)
                        bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
