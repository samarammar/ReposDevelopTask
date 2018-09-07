package ammar.samar.developtask.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import ammar.samar.developtask.Const;
import ammar.samar.developtask.R;
import ammar.samar.developtask.adapters.ReposAdapter;
import ammar.samar.developtask.interfaces.PaginationAdapterCallback;
import ammar.samar.developtask.interfaces.PaginationScrollListener;
import ammar.samar.developtask.models.ReposModel;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {


    private ReposAdapter reposAdapter;
    private FrameLayout frame;




    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = Const.PAGE_START;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getTotal();


        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();
//
    }

    private void showURLDialog(final String repoUrl, final String ownerUrl){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_url, null);




        builder.setPositiveButton("Owner", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                        .putExtra("URL", ownerUrl)
                );
            }
        });

        builder.setNegativeButton("repository ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                        .putExtra("URL",repoUrl)
                );
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void initViews(){
        frame=(FrameLayout)findViewById(R.id.frame);

        recyclerView = (RecyclerView) findViewById(R.id.rv_repos);
        recyclerView.setHasFixedSize(true);
        reposAdapter = new ReposAdapter(MainActivity.this, new ReposAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(ReposModel model) {

                showURLDialog(model.getRepoUrl(),model.getOwnerUrl());

            }
        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reposAdapter);
    }
    private void loadFirstPage()
    {
        Ion.with(MainActivity.this)
                .load("https://api.github.com/users/square/repos?page="+currentPage+"&per_page="+10)
//                .load("https://api.github.com/users/square/repos?&per_page=1000")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.i("total", String.valueOf(result.size()));
                        ReposModel repo=new ReposModel();
                        List<ReposModel> results=new ArrayList<ReposModel>();
                        results.clear();
                        Log.i("cur", String.valueOf(currentPage));
                        for (int i=0;i<result.size();i++){
                            JsonObject object =result.get(i).getAsJsonObject();
                            repo.setRepoName(object.get("name").getAsString());
                            repo.setFork(object.get("fork").getAsBoolean());
                            repo.setRepoDescription(object.get("description").getAsString());
                            repo.setRepoUrl(object.get("html_url").getAsString());
                            repo.setOwnerName(object.get("owner").getAsJsonObject().get("login").getAsString());
                            repo.setOwnerUrl(object.get("owner").getAsJsonObject().get("html_url").getAsString());
                            results.add(repo);
                        }
                        reposAdapter.addAll(results);
                        if (currentPage != TOTAL_PAGES) reposAdapter.addLoadingFooter();
                        else isLastPage = true;


                    }
                });
    }

    private void loadNextPage() {

            Ion.with(MainActivity.this)
                    .load("https://api.github.com/users/square/repos?page="+currentPage+"&per_page="+10)
//                .load("https://api.github.com/users/square/repos?&per_page=1000")
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            Log.i("total", String.valueOf(result.size()));

                            reposAdapter.removeLoadingFooter();
                            isLoading = false;
                            Log.i("cur1", String.valueOf(currentPage));
                            List<ReposModel> results=new ArrayList<ReposModel>();
                            ReposModel repo=new ReposModel();
                            for (int i=0;i<result.size();i++){
                                JsonObject object =result.get(i).getAsJsonObject();
                                repo.setRepoName(object.get("name").getAsString());
                                repo.setFork(object.get("fork").getAsBoolean());
                                repo.setRepoDescription(object.get("description").toString());
                                repo.setRepoUrl(object.get("html_url").getAsString());
                                repo.setOwnerName(object.get("owner").getAsJsonObject().get("login").getAsString());
                                repo.setOwnerUrl(object.get("owner").getAsJsonObject().get("html_url").getAsString());
                                results.add(repo);
                            }
                            reposAdapter.addAll(results);

                            if (currentPage != TOTAL_PAGES) reposAdapter.addLoadingFooter();
                            else isLastPage = true;


                        }
                    });
    }


    private void getTotal(){
        Ion.with(MainActivity.this)
//                .load("https://api.github.com/users/square/repos?page="+2+"&per_page="+10)
                .load("https://api.github.com/users/square/repos?&per_page=1000")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.i("total", String.valueOf(result.size()));
                        TOTAL_PAGES=result.size();

                    }
                });
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }
}
