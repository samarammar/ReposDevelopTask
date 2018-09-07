package ammar.samar.developtask.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ammar.samar.developtask.R;
import ammar.samar.developtask.interfaces.PaginationAdapterCallback;
import ammar.samar.developtask.models.ReposModel;

public class ReposAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int ITEM = 0; //shop
    private static final int LOADING = 1; //loading


    private List<ReposModel> reposModelList;
    private List<ReposModel> reposModelListFilterd;
    private Context context;
    //    private LayoutInflater inflater;
    // SearchModel searchModel;
    private OnItemLongClickListener listener;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public ReposAdapter(Context context, OnItemLongClickListener listener) {
        reposModelList=new ArrayList<>();
        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mCallback = (PaginationAdapterCallback) context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View itemView = inflater.inflate(R.layout.item_repo, parent, false);
                viewHolder = new ShopViewHolder(itemView);
                break;

            case LOADING:
                View loadingView = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ShopLoadingViewHolder(loadingView);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ReposModel reposModel = reposModelList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final ShopViewHolder shopViewHolder = (ShopViewHolder) holder;

                shopViewHolder.bind(reposModelList.get(position), listener);
                shopViewHolder.tvrepoName.setText(reposModel.getRepoName());
                shopViewHolder.tvrepoDesc.setText(reposModel.getRepoDescription().replaceAll("^\"|\"$",""));
                shopViewHolder.tvrepoOwner.setText(reposModel.getOwnerName());
                if(!reposModel.isFork()){
                    shopViewHolder.lay_card.setBackgroundColor(ContextCompat.getColor(context,R.color.light_green));
                }else if (reposModel.isFork()){

                    shopViewHolder.lay_card.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                }


                break;

            case LOADING:
                ShopLoadingViewHolder loadingViewHolder = (ShopLoadingViewHolder) holder;
                if (retryPageLoad) {
                    loadingViewHolder.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingViewHolder.mProgressBar.setVisibility(View.GONE);

                    loadingViewHolder.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingViewHolder.mErrorLayout.setVisibility(View.GONE);
                    loadingViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                }

                break;
//


        }


    }

    @Override
    public int getItemCount() {
        return reposModelList == null ? 0 : reposModelList.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return ITEM;
        } else {
            return (position == reposModelList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder {

        private TextView tvrepoName,tvrepoDesc,tvrepoOwner;
        private LinearLayout lay_card;


        public ShopViewHolder(View itemView) {
            super(itemView);
            tvrepoName = (TextView) itemView.findViewById(R.id.tv_repoName);
            tvrepoDesc = (TextView) itemView.findViewById(R.id.tv_repoDesc);
            tvrepoOwner = (TextView) itemView.findViewById(R.id.tv_repoOwnerName);
            lay_card = (LinearLayout) itemView.findViewById(R.id.lay_card);


        }

        public void bind(final ReposModel model, final OnItemLongClickListener listener) {

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(model);
                    return true;
                }



            });
        }
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(ReposModel model);
    }

    //loading shop item view holder

    protected class ShopLoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public ShopLoadingViewHolder(View itemView) {
            super(itemView);


            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }

    //Helpers


    public void add(ReposModel shop) {
        reposModelList.add(shop);
        notifyItemInserted(reposModelList.size() - 1);
    }

    public void addAll(List<ReposModel> reposList) {
        for (ReposModel repo : reposList) {
            add(repo);
        }
    }

    public void remove(ReposModel shop) {
        int position = reposModelList.indexOf(shop);
        if (position > -1) {
            reposModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ReposModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = reposModelList.size() - 1;
        ReposModel shop = getItem(position);

        if (shop != null) {
            reposModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ReposModel getItem(int position) {
        return reposModelList.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     */


    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(reposModelList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    reposModelListFilterd = reposModelList;
                } else {
                    List<ReposModel> filteredList = new ArrayList<>();
                    for (ReposModel repo : reposModelList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (repo.getRepoName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(repo);
                        }
                    }

                    reposModelListFilterd = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reposModelListFilterd;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                reposModelListFilterd = (ArrayList<ReposModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }



}
