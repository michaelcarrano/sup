package com.michaelcarrano.sup;

import com.parse.ParseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by michaelcarrano on 8/2/14.
 */
public class FriendListViewAdapter extends BaseAdapter {

    private static LayoutInflater mLayoutInflater;

    private List<ParseUser> mParseUsers;

    public FriendListViewAdapter(Context ctx, List<ParseUser> parseUsers) {
        mLayoutInflater = LayoutInflater.from(ctx);
        mParseUsers = parseUsers;
    }

    @Override
    public int getCount() {
        return mParseUsers != null ? mParseUsers.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mParseUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ParseUser friend = (ParseUser) getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_friend, parent, false);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.friend_username);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(friend.getUsername());

        return convertView;
    }

    public static class ViewHolder {

        public TextView username;
    }

}
