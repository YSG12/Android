//package com.stav.ideastreet.fragment;
//
//import android.content.Intent;
//import android.view.View;
//import android.widget.AdapterView;
//
//import com.stav.ideastreet.adapter.MAdapter;
//import com.stav.ideastreet.ui.CommentListActivity;
//
//
//public class FavFragment extends BaseContentFragment{
//
//    public static FavFragment newInstance(){
//        FavFragment fragment = new FavFragment();
//        return fragment;
//    }
//
//    @Override
//    public MAdapter getAdapter() {
//        // TODO Auto-generated method stub
//        return new MAdapter(getApplicationContext(), mListItems);
//    }
//
//    @Override
//    public void onListItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//        // TODO Auto-generated method stub
////		MyApplication.getInstance().setCurrentQiangYu(mListItems.get(position-1));
//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(), CommentListActivity.class);
//        intent.putExtra("data", mListItems.get(position-1));
//        startActivity(intent);
//    }
//}