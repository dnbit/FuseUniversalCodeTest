package com.dnbitstudio.fuseuniversal;

import com.dnbitstudio.fuseuniversal.model.Company;
import com.dnbitstudio.fuseuniversal.otto.BusProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.tajchert.sample.DotsTextView;
import retrofit.Response;

public class MainActivity extends AppCompatActivity
{
    public static int BACKGROUND_COLOR = 0;
    public static int BACKGROUND_COLOR_DEFAULT = 0;
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String DOTS_PLAYING_KEY = "dots_playing";

    private Bus bus;

    // Drawable to store default state of mQueryValue
    Drawable mOriginalDrawable;
    @Bind(R.id.et_query_value)
    EditText mQueryValue;
    @Bind(R.id.dots)
    DotsTextView mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mOriginalDrawable = mQueryValue.getBackground();
        bus = BusProvider.getBus();

        mQueryValue.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                BACKGROUND_COLOR = BACKGROUND_COLOR_DEFAULT;
                // Setting the background to White is not visually nice
                // so I have preferred to revert to the background to its default instead
                if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                {
                    //noinspection deprecation
                    mQueryValue.setBackgroundDrawable(mOriginalDrawable);
                } else
                {
                    mQueryValue.setBackground(mOriginalDrawable);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        mQueryValue.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    performSearch(mQueryValue.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    // Using onPostCreate to ensure the color is set after the text
    // otherwise the addTextChangedListener would set it back again to default
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            BACKGROUND_COLOR = savedInstanceState.getInt(BACKGROUND_COLOR_KEY, BACKGROUND_COLOR_DEFAULT);
            if (BACKGROUND_COLOR != BACKGROUND_COLOR_DEFAULT)
            {
                mQueryValue.setBackgroundColor(BACKGROUND_COLOR);
            }

            boolean dotsPlaying = savedInstanceState.getBoolean(DOTS_PLAYING_KEY, false);
            if (dotsPlaying)
            {
                playDots();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(BACKGROUND_COLOR_KEY, BACKGROUND_COLOR);
        outState.putBoolean(DOTS_PLAYING_KEY, mDots.isPlaying());
    }

    public void performSearch(String queryValue)
    {
        if (queryValue.length() == 0)
        {
            Toast.makeText(this, R.string.please_type_name, Toast.LENGTH_SHORT).show();
            return;
        }

        String baseUrl = "https://" + queryValue.trim()
                + ".fusion-universal.com/api/v1/company.json";

        boolean isValid = Patterns.WEB_URL.matcher(baseUrl).matches();

        if (!isValid)
        {
            Toast.makeText(this, R.string.company_name_invalid,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        playDots();

        FetchDataService.startActionRequestData(getApplicationContext(), baseUrl);
    }

    @Subscribe
    public void parseReceivedData(Response<Company> response)
    {
        stopDots();
        // Note that, by definition, response cannot be null
        if (response.code() == 200)
        {
            // check if the value received is not null
            if (response.body() != null && response.body().getName() != null)
            {
                mQueryValue.setText(response.body().getName());
            } else
            {
                mQueryValue.setText(R.string.unknown_company);
            }

            // Move cursor to last position
            mQueryValue.setSelection(mQueryValue.getText().length());
            BACKGROUND_COLOR = Color.GREEN;

        } else
        {
            BACKGROUND_COLOR = Color.RED;
        }

        mQueryValue.setBackgroundColor(BACKGROUND_COLOR);
    }

    @Subscribe
    // Only using the company parameter for subscription purposes
    public void processFechtDataFailure(Company company)
    {
        stopDots();
        if (!Utils.isNetworkConnected(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), R.string.network_unavailable, Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void playDots()
    {
        mDots.setVisibility(View.VISIBLE);
        mDots.start();
    }

    public void stopDots()
    {
        mDots.setVisibility(View.INVISIBLE);
        mDots.stop();
    }
}