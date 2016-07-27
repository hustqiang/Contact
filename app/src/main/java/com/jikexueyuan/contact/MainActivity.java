package com.jikexueyuan.contact;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final ArrayList<String> names=new ArrayList<>();
    public final ArrayList<ArrayList<String>> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDataFromSystemContact();
        setDataToList();
        findViewById(R.id.btnAddContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInputDialog();
            }
        });
//        findViewById(R.id.showContact).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public void startInputDialog() {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.dialog));
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("添加联系人");
        inputDialog.setView(layout);

        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContentValues contentValues=new ContentValues();
                String userName=((EditText)layout.findViewById(R.id.editName)).getText().toString();
                Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
                long rawContactId = ContentUris.parseId(rawContactUri);

                contentValues.clear();
                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, userName);
                getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues);
                contentValues.clear();

                String phone= ((EditText)layout.findViewById(R.id.editPhone)).getText().toString();
                contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER,phone);
                getContentResolver().insert(ContactsContract.Data.CONTENT_URI,contentValues);



            }
        });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        inputDialog.show();

    }

    public void dialOrMessage(String number) {
        final String phoneNumber= "15927351171";
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialormessage, (ViewGroup) findViewById(R.id.dialormessage));
        final AlertDialog.Builder dialOrMessageDialog = new AlertDialog.Builder(this);
        dialOrMessageDialog.setTitle("请选择");
        dialOrMessageDialog.setView(layout);
        dialOrMessageDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialOrMessageDialog.show();
        TextView dial;
        TextView message;
        dial= (TextView) findViewById(R.id.dial);
        message= (TextView) findViewById(R.id.message);
        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:"+phoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:"+phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SEND, uri);
                intent.putExtra("sms_body", "//这里面就是要发送的内容");
                startActivity(intent);
            }
        });


    }

    public void setDataToList(){
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        // 创建一个ExpandableListAdapter对象
        ExpandableListAdapter adapter = new BaseExpandableListAdapter(){
                    // 获取指定组位置、指定子列表项处的子列表项数据
                    @Override
                    public Object getChild(int groupPosition,
                                           int childPosition)
                    {
                        return details.get(groupPosition).get(
                                childPosition);
                    }
                    @Override
                    public long getChildId(int groupPosition,
                                           int childPosition)
                    {
                        return childPosition;
                    }
                    @Override
                    public int getChildrenCount(int groupPosition)
                    {
                        return details.get(groupPosition).size();
                    }
                    private TextView getTextView()
                    {
                        AbsListView.LayoutParams lp = new AbsListView
                                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , 64);
                        TextView textView = new TextView(
                                MainActivity.this);
                        textView.setLayoutParams(lp);
                        textView.setGravity(Gravity.CENTER_VERTICAL
                                | Gravity.LEFT);
                        textView.setPadding(64, 0, 0, 0);
                        textView.setTextSize(20);



                        return textView;
                    }
                    // 该方法决定每个子选项的外观
                    @Override
                    public View getChildView(int groupPosition,
                                             int childPosition, boolean isLastChild,
                                             View convertView, ViewGroup parent)
                    {
                        TextView textView = getTextView();
                        textView.setText(getChild(groupPosition,
                                childPosition).toString());
                        textView.setTextSize(16);
                        textView.setPadding(96, 0, 0, 0);
                        return textView;
                    }
                    // 获取指定组位置处的组数据
                    @Override
                    public Object getGroup(int groupPosition)
                    {
                        return names.get(groupPosition);
                    }
                    @Override
                    public int getGroupCount()
                    {
                        return names.size();
                    }
                    @Override
                    public long getGroupId(int groupPosition)
                    {
                        return groupPosition;
                    }
                    // 该方法决定每个组选项的外观
                    @Override
                    public View getGroupView(int groupPosition,
                                             boolean isExpanded, View convertView,
                                             ViewGroup parent)
                    {
                        TextView textView = getTextView();
                        textView.setText(getGroup(groupPosition)
                                .toString());
                        return textView;
                    }
                    @Override
                    public boolean isChildSelectable(int groupPosition,
                                                     int childPosition)
                    {
                        return true;
                    }
                    @Override
                    public boolean hasStableIds()
                    {
                        return true;
                    }


                };

        Button btn=(Button)findViewById(R.id.btnAddContact);

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        btn.measure(w, h);
        int height =btn.getMeasuredHeight();

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, height);
        listView.setLayoutParams(lp);
        listView.setAdapter(adapter);

        for(int i = 0; i < adapter.getGroupCount(); i++){
            listView.expandGroup(i);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String phoneNumber = listView.getItemAtPosition(position).toString();
//                System.out.println("clicked");
//                System.out.println(phoneNumber);
                dialOrMessage(phoneNumber);
            }
        });
    }

    public void getDataFromSystemContact(){
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            while (cursor.moveToNext()) {
                String contact_ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                names.add(name);

                Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contact_ID, null, null);
                ArrayList<String> detail = new ArrayList<>();
                while (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    detail.add(phoneNumber);
                }
                phone.close();
                details.add(detail);
            }
            cursor.close();
        }
    }

}