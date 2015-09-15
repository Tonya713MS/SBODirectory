package com.sbodirectory.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbodirectory.R;
import com.sbodirectory.model.Company;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class CompanyListAdapter extends BaseAdapter{
    public static interface OnItemListener {
        public void onGetDirectionClicked(Company company);
        public void onWebsiteClicked(Company company);
    }

    private Context mContext;
    private List<Company> mCompanies;
    private OnItemListener mOnItemListener;

    public CompanyListAdapter(Context ctx, List<Company> items) {
        super();
        mContext = ctx;
        mCompanies = items;
    }

    public void setOnItemListener(OnItemListener l) {
        mOnItemListener = l;
    }

    @Override
    public int getCount() {
        return (mCompanies != null) ? mCompanies.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && mCompanies != null && position < getCount()) {
            return mCompanies.get(position);
        }
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.company_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        final Company company = mCompanies.get(position);
        holder.name.setText(company.getName());
        holder.address.setText(company.getAddress());
//        holder.addressExtra.setText(company.getAddressExtra());
        holder.phone.setText(company.getPhone());
        holder.website.setText(company.getWebsite());
        holder.category.setText(company.getCategory());
        holder.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemListener != null) {
                    mOnItemListener.onGetDirectionClicked(company);
                }
            }
        });
        if (!TextUtils.isEmpty(company.phone)) {
            holder.phone.setText(company.getPhone());
        } else {
            holder.phone.setText("Unknow website");
        }
        if (!TextUtils.isEmpty(company.website)) {
            holder.iconWebsite.setVisibility(View.VISIBLE);
            holder.website.setVisibility(View.VISIBLE);
        } else {
            holder.iconWebsite.setVisibility(View.GONE);
            holder.website.setVisibility(View.GONE);
        }
        holder.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemListener != null) {
                    mOnItemListener.onWebsiteClicked(company);
                }
            }
        });
        holder.thumbnail.setImageResource(R.drawable.thumb_default);
        if (!TextUtils.isEmpty(company.thumbnail)) {
            Picasso.with(mContext).load(company.thumbnail).into(holder.thumbnail);
        }

        return view;
    }

    private static class ViewHolder {
        public ImageView thumbnail;
        public TextView name, address, phone, website, category;
        public TextView btnDirection;
        public ImageView iconDirection;
        public ImageView iconWebsite, iconDot, iconPhone;
        public ViewHolder(View parent) {
            thumbnail = (ImageView)parent.findViewById(R.id.thumbnail);
            name = (TextView)parent.findViewById(R.id.name);
            address = (TextView)parent.findViewById(R.id.address);
            phone = (TextView)parent.findViewById(R.id.phone);
            website = (TextView)parent.findViewById(R.id.website);
            iconDirection = (ImageView)parent.findViewById(R.id.iconDirection);
            btnDirection = (TextView)parent.findViewById(R.id.btnDirection);
            iconWebsite = (ImageView)parent.findViewById(R.id.iconWebsite);
            iconDot = (ImageView)parent.findViewById(R.id.iconDot);
            iconPhone = (ImageView)parent.findViewById(R.id.iconPhone);
            category = (TextView) parent.findViewById(R.id.category);
            try {
                Typeface tf = Typeface.createFromAsset(parent.getContext().getAssets(), "MYRIADPRO-SEMIBOLD.OTF");
                name.setTypeface(tf,Typeface.BOLD);
            } catch (Exception e){}
        }
    }
}
