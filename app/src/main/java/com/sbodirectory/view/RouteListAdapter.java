package com.sbodirectory.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbodirectory.R;
import com.sbodirectory.model.Company;
import com.sbodirectory.model.maps.GLeg;
import com.sbodirectory.model.maps.GRoute;
import com.sbodirectory.model.maps.GStep;
import com.sbodirectory.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class RouteListAdapter extends BaseAdapter{
    private GRoute mRoute;
    private GLeg mLeg;
    private Context mContext;
    public RouteListAdapter(Context context, GRoute route) {
        super();
        this.mContext = context;
        this.mRoute = route;
        if (route != null && route.legs != null && route.legs.size() > 0) {
            mLeg = route.legs.get(0);
        }
    }

    @Override
    public int getCount() {
        return (mLeg != null && mLeg.steps != null) ? mLeg.steps.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && mLeg.steps != null && position < getCount()) {
            return mLeg.steps.get(position);
        }
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.route_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        final GStep item = (GStep)getItem(position);
        if (item != null) {
            holder.instruction.setText(Html.fromHtml(Utils.getText(item.htmlInstructions)));
            if (item.distance != null) {
                holder.distance.setText(Utils.getText(item.distance.text));
            }
        }
        return view;
    }

    private static class ViewHolder {
        public TextView instruction, distance;

        public ViewHolder(View parent) {
            instruction = (TextView)parent.findViewById(R.id.instruction);
            distance = (TextView)parent.findViewById(R.id.distance);
        }
    }
}
