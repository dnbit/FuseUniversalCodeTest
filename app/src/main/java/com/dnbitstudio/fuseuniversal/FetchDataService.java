package com.dnbitstudio.fuseuniversal;

import com.dnbitstudio.fuseuniversal.api.FuseAPI;
import com.dnbitstudio.fuseuniversal.model.Company;
import com.dnbitstudio.fuseuniversal.otto.BusProvider;
import com.squareup.otto.Bus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class FetchDataService extends IntentService
{
    private static final String ACTION_REQUEST_DATA =
            "com.dnbitstudio.fuseuniversal.action.REQUEST_DATA";

    private static final String EXTRA_BASE_URL = "com.dnbitstudio.fuseuniversal.extra.BASE_URL";

    private Bus bus;

    @Override
    public void onCreate()
    {
        super.onCreate();
        bus = BusProvider.getBus();
    }

    /**
     * Starts this service to perform action Request_Data with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRequestData(Context context, String baseUrl)
    {
        Intent intent = new Intent(context, FetchDataService.class);
        intent.setAction(ACTION_REQUEST_DATA);
        intent.putExtra(EXTRA_BASE_URL, baseUrl);
        context.startService(intent);
    }

    public FetchDataService()
    {
        super("FetchDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_REQUEST_DATA.equals(action))
            {
                final String baseUrl = intent.getStringExtra(EXTRA_BASE_URL);
                handleActionRequest_Data(baseUrl);
            }
        }
    }

    /**
     * Handle action Request_Data in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRequest_Data(String baseUrl)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FuseAPI fuseAPI = retrofit.create(FuseAPI.class);

        Call<Company> call = fuseAPI.getCompany();
        call.enqueue(new Callback<Company>()
        {
            @Override
            public void onResponse(Response<Company> response, Retrofit retrofit)
            {
                // Post the response to the bus
                bus.post(response);
            }

            @Override
            public void onFailure(Throwable t)
            {
                Company company = new Company();
                // Post a Company to the bus so the right method is called on the Activity
                bus.post(company);
            }
        });
    }
}